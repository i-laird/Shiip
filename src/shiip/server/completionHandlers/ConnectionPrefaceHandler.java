/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.completionHandlers;

import shiip.server.ServerAIO;
import shiip.server.attachment.ConnectionPrefaceAttachment;
import shiip.server.exception.ConnectionPrefaceException;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static shiip.util.ServerStrings.CONNECTION_PREFACE_ERROR;

/**
 * @author Ian Laird
 * @version 1.0
 * Handles a Connection Preface read
 */
public class ConnectionPrefaceHandler implements CompletionHandler<Long, ConnectionPrefaceAttachment> {

    /**
     * completed
     * @param result the number of bytes read
     * @param attachment the attachment
     */
    @Override
    public void completed(Long result, ConnectionPrefaceAttachment attachment) {
        if(result == -1){
            //TODO this is an error
        }
        ByteBuffer bb = attachment.getBb()[0];

        int numLeft = ServerAIO.CLIENT_CONNECTION_PREFACE.length - bb.position();
        // if the whole preface has not been read in yet go again
        if(numLeft > 0){
            attachment.getAsynchronousSocketChannel().read(attachment.getBb(), 0, numLeft, (long)3, TimeUnit.SECONDS, attachment, this);
        }

        // now ensure that the preface read is valid
        byte [] readBytes = bb.array();
        if(!Arrays.equals(ServerAIO.CLIENT_CONNECTION_PREFACE, readBytes)){
            failed(new ConnectionPrefaceException(CONNECTION_PREFACE_ERROR, new String(readBytes, StandardCharsets.US_ASCII)), attachment);
        }
    }

    /**
     * failed
     * @param exc the cause
     * @param attachment the attachment
     */
    @Override
    public void failed(Throwable exc, ConnectionPrefaceAttachment attachment) {

    }

}
