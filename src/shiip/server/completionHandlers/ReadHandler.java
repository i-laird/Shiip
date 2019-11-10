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
import java.util.Objects;

/**
 * @author Ian Laird
 */
public class ReadHandler implements CompletionHandler<Integer, ReadAttachment> {

    /**
     * runs for the completion of a read
     * @param numRead the number of bytes read
     * @param readAttachment the attachment (includes the byte buffer that has the bytes)
     */
    @Override
    public void completed(Integer numRead, ReadAttachment readAttachment) {

        if(numRead == -1){
            //TODO
        }

        // get all of the bytes that were read into the byte buffer
        byte [] readBytes = new byte[numRead];
        readAttachment.getByteBuffer().get(readBytes, 0, numRead);
        readAttachment.getByteBuffer().clear();

        // get deframed bytes
        byte [] deframedBytes = readAttachment.getDeframer().getFrame(readBytes);

        //if a message does exist handle it
        if(Objects.nonNull(deframedBytes)) {
            Message m = null;
            try {
                m = Message.decode(deframedBytes, readAttachment.getDecoder());
                //TODO the streams
                ServerMessageHandler.handleMessage(false, readAttachment.getLogger(), m, null,
                        readAttachment.getDirectoryBase(), readAttachment.getAsynchronousMessageSender(), readAttachment.getLastEncounteredStreamId());
            } catch (BadAttributeException | IOException e) {
                failed(e, readAttachment);
            }
        }

        // get ready to read more
        readAttachment.getAsynchronousSocketChannel().read(readAttachment.getByteBuffer(), readAttachment, new ReadHandler());

    }

    /**
     * for failure of a read
     * @param throwable the exception cause of it
     * @param readAttachment the attachment
     */
    @Override
    public void failed(Throwable throwable, ReadAttachment readAttachment) {

    }
}
