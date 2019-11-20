/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.completionHandlers;

import shiip.server.attachment.ConnectionPrefaceAttachment;
import shiip.server.ServerAIO;
import shiip.server.attachment.ConnectionAttachment;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import java.util.concurrent.TimeUnit;

import static shiip.server.Server.CLIENT_INACTIVE_TIMEOUT;

/**
 * @author Ian laird
 */
public class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, ConnectionAttachment> {

    // the starting value of the connection preface mutex
    private static final int CONNECTION_PREFACE_MUTEX = 0;

    /**
     * runs for completion of a connection
     * @param clientChan the connection channel
     * @param attachment the attachment of the connection
     */
    @Override
    public void completed(AsynchronousSocketChannel clientChan, ConnectionAttachment attachment) {

        // make it so that another connection can be accepted
        attachment.getAsynchronousServerSocketChannel().accept(attachment, new ConnectionHandler());

        attachment.setAsynchronousSocketChannel(clientChan);

        int bytesToRead = ServerAIO.CLIENT_CONNECTION_PREFACE.length;

        ByteBuffer connPreface = ByteBuffer.allocate(bytesToRead);

        ConnectionPrefaceAttachment connectionPrefaceAttachment =
                new ConnectionPrefaceAttachment(connPreface, clientChan, attachment);

        // read in the connection preface
        clientChan.read(connPreface, CLIENT_INACTIVE_TIMEOUT, TimeUnit.MILLISECONDS, connectionPrefaceAttachment, new ConnectionPrefaceHandler());

    }

    /**
     * failure of the connection
     * @param e the exception
     * @param attachment the attachment for the connection
     */
    @Override
    public void failed(Throwable e, ConnectionAttachment attachment) {
        try {
            attachment.getAsynchronousSocketChannel().close();
        }catch (IOException e2){}
    }
}
