/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.completionHandlers;

import shiip.server.attachment.WriteAttachment;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

/**
 * @author Ian Laird
 * @version 1.0
 * handles a write
 */
public class WriteHandler implements CompletionHandler<Integer, WriteAttachment> {

    /**
     * runs for the completion of a write
     * @param numWritten the number written
     * @param writeAttachment the attachment
     */
    @Override
    public void completed(Integer numWritten, WriteAttachment writeAttachment) {

        if(writeAttachment.getByteBuffer().hasRemaining()){
            writeAttachment.getAsynchronousSocketChannel()
                    .write(writeAttachment.getByteBuffer(), writeAttachment, new WriteHandler());
        }
        else{

            // done with the write so do the next
            synchronized (writeAttachment.getOutputBuffer()){

                // there will be at least the current item here
                writeAttachment.getOutputBuffer().remove();

                // now get the current item to work on if present
                if(writeAttachment.getOutputBuffer().size() > 0){
                    ByteBuffer toSend = writeAttachment.getOutputBuffer().peek();
                    writeAttachment.setByteBuffer(toSend);
                    writeAttachment.getAsynchronousSocketChannel().write(toSend, writeAttachment, this);
                }
            }
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
        }finally {
            synchronized (writeAttachment.getOutputBuffer()) {
                writeAttachment.getOutputBuffer().poll();
            }
        }
    }
}