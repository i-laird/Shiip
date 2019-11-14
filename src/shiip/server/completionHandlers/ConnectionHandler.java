/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.completionHandlers;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;
import shiip.serialization.NIODeframer;
import shiip.server.ServerStream;
import shiip.server.attachment.ConnectionPrefaceAttachment;
import shiip.server.attachment.ReadAttachment;
import shiip.server.ServerAIO;
import shiip.server.attachment.ConnectionAttachment;
import shiip.server.exception.ConnectionPrefaceException;
import shiip.transmission.AsynchronousMessageSender;
import shiip.util.EncoderDecoderWrapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static shiip.util.ServerStrings.CONNECTION_PREFACE_ERROR;

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
        clientChan.read(connPreface, connectionPrefaceAttachment, new ConnectionPrefaceHandler());

    }

    /**
     * failure of the connection
     * @param e the exception
     * @param attachment the attachment for the connection
     */
    @Override
    public void failed(Throwable e, ConnectionAttachment attachment) {
        if(e instanceof ConnectionPrefaceException){
            attachment.getLogger().severe(CONNECTION_PREFACE_ERROR + ((ConnectionPrefaceException)e).getReceivedString());
        }else {
            attachment.getLogger().log(Level.WARNING, "Connection failed", e);
        }
        try {
            attachment.getAsynchronousSocketChannel().close();
        }catch (IOException e2){}
    }
}
