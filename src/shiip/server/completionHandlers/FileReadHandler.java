package shiip.server.completionHandlers;

import shiip.serialization.BadAttributeException;
import shiip.serialization.Data;
import shiip.serialization.Message;
import shiip.server.attachment.FileReadAttachment;

import java.io.IOException;
import java.nio.channels.CompletionHandler;

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
        if(numRead == -1){
            readBytes = new byte[0];
            isEnd = true;
        }
        else{
            readBytes = new byte[numRead];
            readAttachment.getReadAttachment().getByteBuffer().get(readBytes, 0, numRead);
            readAttachment.getReadAttachment().getByteBuffer().clear();
        }

        //create data packet and send it
        try {
            Message m = new Data(readAttachment.getReadAttachment().getCurrStreamId(), isEnd, readBytes);
            readAttachment.getReadAttachment().getAsynchronousMessageSender().sendFrame(m);
        }catch (BadAttributeException | IOException e){
            failed(e, readAttachment);
        }

        // increment the number read from the file channel
        readAttachment.setNumRead(readAttachment.getNumRead() + numRead);

        // if it is not the end read more from the file
        if(!isEnd){
            readAttachment.getFileChannel()
                    .read(readAttachment.getReadAttachment()
                                    .getByteBuffer(),
                            readAttachment.getNumRead(),
                            readAttachment, new FileReadHandler());
        }

    }

    /**
     * for failure of a read
     * @param throwable the exception cause of it
     * @param readAttachment the attachment
     */
    @Override
    public void failed(Throwable throwable, FileReadAttachment readAttachment) {

    }
}
