/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.completionHandlers;

import shiip.server.attachment.WriteAttachment;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.util.logging.Level;

/**
 * @author Ian Laird
 */
public class WriteHandler implements CompletionHandler<Integer, WriteAttachment> {

    /**
     * runs for the completion of a write
     * @param numRead the number written
     * @param writeAttachment the attachment
     */
    @Override
    public void completed(Integer numRead, WriteAttachment writeAttachment) {

        if(writeAttachment.getByteBuffer().hasRemaining()){
            writeAttachment.getAsynchronousSocketChannel()
                    .write(writeAttachment.getByteBuffer(), writeAttachment, new WriteHandler());
        }

    }

    /**
     * if the write fails
     * @param throwable the cause of the failure
     * @param writeAttachment the attachment
     */
    @Override
    public void failed(Throwable throwable, WriteAttachment writeAttachment) {
        try {
            writeAttachment.getAsynchronousSocketChannel().close();
        }catch (IOException e){
            writeAttachment.getLogger().log(Level.WARNING, "Close Failed", e);
        }
    }
}