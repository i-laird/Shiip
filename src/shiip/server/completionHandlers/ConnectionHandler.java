/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.completionHandlers;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;
import shiip.serialization.NIODeframer;
import shiip.server.attachment.ConnectionPrefaceAttachment;
import shiip.server.attachment.ReadAttachment;
import shiip.server.ServerAIO;
import shiip.server.attachment.ConnectionAttachment;
import shiip.server.exception.ConnectionPrefaceException;
import shiip.transmission.AsynchronousMessageSender;
import shiip.util.EncoderDecoderWrapper;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static shiip.util.ServerStrings.CONNECTION_PREFACE_ERROR;

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

        int bytesToRead = ServerAIO.CLIENT_CONNECTION_PREFACE.length;

        ByteBuffer [] connPreface = {ByteBuffer.allocate(bytesToRead)};

        ConnectionPrefaceAttachment connectionPrefaceAttachment = new ConnectionPrefaceAttachment(connPreface, clientChan);

        //TODO put a semaphore before and after this

        // read in the connection preface
        clientChan.read(connPreface, 0, bytesToRead, (long)3, TimeUnit.SECONDS, connectionPrefaceAttachment, new ConnectionPrefaceHandler());

        // get the encoder and decoder for the connection
        Encoder encoder = EncoderDecoderWrapper.getEncoder();
        Decoder decoder = EncoderDecoderWrapper.getDecoder();

        // each connection gets its own non blocking deframer
        NIODeframer deframer = new NIODeframer();

        // create the byte buffer for this connection
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(ServerAIO.BUFFER_SIZE);

        // create the read attachment
        ReadAttachment readAttachment = new ReadAttachment();
        readAttachment.setAsynchronousSocketChannel(clientChan);
        readAttachment.setDecoder(decoder);
        readAttachment.setDeframer(deframer);
        readAttachment.setByteBuffer(byteBuffer);
        readAttachment.setLogger(attachment.getLogger());
        readAttachment.setDirectoryBase(attachment.getFileBase());
        readAttachment.setAsynchronousMessageSender(new AsynchronousMessageSender(clientChan, encoder, attachment.getLogger()));
        readAttachment.setLastEncounteredStreamId(0);

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
