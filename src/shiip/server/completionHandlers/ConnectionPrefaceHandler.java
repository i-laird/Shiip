/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.completionHandlers;

import shiip.server.ServerAIO;
import shiip.server.attachment.ConnectionPrefaceAttachment;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;


/**
 * @author Ian Laird
 * @version 1.0
 * Handles a Connection Preface read
 */
public class ConnectionPrefaceHandler implements CompletionHandler<Integer, ConnectionPrefaceAttachment> {

    /**
     * completed
     * @param result the number of bytes read
     * @param attachment the attachment
     */
    @Override
    public void completed(Integer result, ConnectionPrefaceAttachment attachment) {
        if(result == -1){
            //TODO what to do?
        }
        ByteBuffer bb = attachment.getBb();

        int numLeft = ServerAIO.CLIENT_CONNECTION_PREFACE.length - bb.position();

        // if the whole preface has not been read in yet go again
        if(numLeft > 0){
            attachment.getAsynchronousSocketChannel().read(bb, attachment, this);
        }else {

            //done
            attachment.getSem().release();
        }
    }

    /**
     * failed
     * @param exc the cause
     * @param attachment the attachment
     */
    @Override
    public void failed(Throwable exc, ConnectionPrefaceAttachment attachment) {

        // zero out the byte buffer
        attachment.getBb().clear();
        attachment.getBb().put(new byte [ServerAIO.CLIENT_CONNECTION_PREFACE.length]);
        attachment.getBb().clear();

        // release the mutex
        attachment.getSem().release();
    }

}
