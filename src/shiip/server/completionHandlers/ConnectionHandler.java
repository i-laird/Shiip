/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.completionHandlers;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;
import shiip.serialization.NIODeframer;
import shiip.server.attachment.ReadAttachment;
import shiip.server.ServerAIO;
import shiip.server.attachment.ConnectionAttachment;
import shiip.transmission.AsynchronousMessageSender;
import shiip.util.EncoderDecoderWrapper;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Level;

/**
 * @author Ian laird
 */
public class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, ConnectionAttachment> {

    /**
     * runs for completion of a connection
     * @param clientChan the connection channel
     * @param attachment the attachment of the connection
     */
    @Override
    public void completed(AsynchronousSocketChannel clientChan, ConnectionAttachment attachment) {

        // make it so that another connection can be accepted
        attachment.getAsynchronousServerSocketChannel().accept(attachment, new ConnectionHandler());

        Encoder encoder = EncoderDecoderWrapper.getEncoder();
        Decoder decoder = EncoderDecoderWrapper.getDecoder();

        // each connection gets its own non blocking deframer
        NIODeframer deframer = new NIODeframer();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(ServerAIO.BUFFER_SIZE);

        // create the read attachment
        ReadAttachment readAttachment = new ReadAttachment();
        readAttachment.setAsynchronousSocketChannel(clientChan);
        readAttachment.setDecoder(decoder);
        readAttachment.setDeframer(deframer);
        readAttachment.setByteBuffer(byteBuffer);
        readAttachment.setAsynchronousMessageSender(new AsynchronousMessageSender(clientChan, encoder, attachment.getLogger()));

        // now handle a read
        clientChan.read(byteBuffer, readAttachment, new ReadHandler());

    }

    /**
     * failure of the connection
     * @param e the exception
     * @param attachment the attachment for the connection
     */
    @Override
    public void failed(Throwable e, ConnectionAttachment attachment) {
        attachment.getLogger().log(Level.WARNING, "Connection failed", e);
    }
}
