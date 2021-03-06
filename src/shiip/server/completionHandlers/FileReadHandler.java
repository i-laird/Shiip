/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.completionHandlers;

import shiip.serialization.BadAttributeException;
import shiip.serialization.Data;
import shiip.serialization.Message;
import shiip.server.attachment.FileReadAttachment;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.util.Arrays;

import static shiip.server.completionHandlers.ConnectionPrefaceHandler.READ_FAILED;
/**
 * @author Ian laird
 * @version 1.0
 * Handles a file read
 */
public class FileReadHandler implements CompletionHandler<Integer, FileReadAttachment> {

    /**
     * runs for the completion of a read
     * @param numRead the number of bytes read
     * @param readAttachment the attachment (includes the byte buffer that has the bytes)
     */
    @Override
    public void completed(Integer numRead, FileReadAttachment readAttachment) {

        boolean isEnd = false;
        byte [] readBytes = null;

        // if no bytes were read
        if(numRead == READ_FAILED){
            readBytes = new byte[0];
            isEnd = true;
        }

        // if some bytes were read
        else{
            readBytes = Arrays.copyOf(
                    readAttachment.getReadAttachment().getByteBuffer().array(),
                    numRead);
            readAttachment.getReadAttachment().getByteBuffer().clear();
        }

        //create data packet from whatever was read and send it
        try {
            Message m = new Data(readAttachment.getStreamId(), isEnd, readBytes);
            readAttachment.getReadAttachment().getAsynchronousMessageSender().sendFrame(m);
        }catch( BadAttributeException | IOException e){
            failed(e, readAttachment);
            return; //terminate
        }

        // increment the number read from the file channel
        readAttachment.setNumRead(readAttachment.getNumRead() + numRead);

        // if it is not the end read more from the file
        if(!isEnd){
            readAttachment.getFileChannel()
                    .read(readAttachment.getReadAttachment()
                                    .getByteBuffer(),

                            // says the position in the byte buffer to write to
                            // (0 because byte buffer has been cleared out)
                            readAttachment.getNumRead(),
                            readAttachment, this); //TODO will 'this' work
        }

    }

    /**
     * for failure of a read
     * @param throwable the exception cause of it
     * @param readAttachment the attachment
     */
    @Override
    public void failed(Throwable throwable, FileReadAttachment readAttachment) {

        //nothing needs to be done
    }
}
