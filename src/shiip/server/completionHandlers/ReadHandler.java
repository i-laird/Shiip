/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.completionHandlers;

import shiip.serialization.BadAttributeException;
import shiip.serialization.Message;
import shiip.server.ServerMessageHandler;
import shiip.server.attachment.ReadAttachment;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static shiip.server.Server.CLIENT_INACTIVE_TIMEOUT;
import static shiip.server.completionHandlers.ConnectionPrefaceHandler.READ_FAILED;
import static shiip.util.ServerStrings.INVALID_MESSAGE;


/**
 * @author Ian Laird
 * @version 1.0
 * handles a read
 */
public class ReadHandler implements CompletionHandler<Integer, ReadAttachment> {

    /**
     * runs for the completion of a read
     * @param numRead the number of bytes read
     * @param readAttachment the attachment (includes the byte buffer that has the bytes)
     */
    @Override
    public void completed(Integer numRead, ReadAttachment readAttachment) {

        // TODO what if only input closed
        // if none were read it must mean that the socket closed
        if(numRead == READ_FAILED){
            try {
                readAttachment.getAsynchronousSocketChannel().close();
            }catch (IOException e){}
            return;
        }

        // get all of the bytes that were read into the byte buffer
        byte [] readBytes =
                Arrays.copyOf(readAttachment.getByteBuffer().array(), numRead);
        readAttachment.getByteBuffer().clear();

        byte[] deframedBytes = null;
        boolean flag = false;

        // keep going until no message can be handles
        // will go at least once guaranteed
        while(true) {
            try {
                byte[] bytesToUse = null;
                if (!flag) {
                    bytesToUse = readBytes;
                } else {
                    bytesToUse = new byte[0];
                }
                flag = true;
                deframedBytes = readAttachment.getDeframer().getFrame(bytesToUse);

                // if there is no message present break out
                if(Objects.isNull(deframedBytes)){
                    break;
                }
                Message m = null;
                try {
                    m = Message.decode(deframedBytes, readAttachment.getDecoder());
                    ServerMessageHandler.handleMessage(
                            false,
                            readAttachment.getLogger(),
                            m,
                            readAttachment.getStreams(),
                            readAttachment.getDirectoryBase(),
                            readAttachment.getAsynchronousMessageSender(),
                            readAttachment.getLastEncounteredStreamId());
                } catch (BadAttributeException | IOException e) {
                    failed(e, readAttachment);
                    return;
                }
            }catch(IllegalArgumentException e2){
                readAttachment.getLogger().info(INVALID_MESSAGE + e2.getMessage());
                deframedBytes = null;
            }
        }

        // get ready to read more
        readAttachment.getAsynchronousSocketChannel().read(
                readAttachment.getByteBuffer(), CLIENT_INACTIVE_TIMEOUT,
                TimeUnit.MILLISECONDS, readAttachment, this);

    }

    /**
     * for failure of a read
     * @param throwable the exception cause of it
     * @param readAttachment the attachment
     */
    @Override
    public void failed(Throwable throwable, ReadAttachment readAttachment) {
        if(throwable instanceof InterruptedByTimeoutException) {
            try {
                readAttachment.getAsynchronousSocketChannel().close();
            } catch (IOException e) {
            }
        }
    }
}
