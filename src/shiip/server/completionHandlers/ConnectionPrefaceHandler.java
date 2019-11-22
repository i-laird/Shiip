/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.completionHandlers;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;
import shiip.serialization.NIODeframer;
import shiip.server.ServerAIO;
import shiip.server.ServerStream;
import shiip.server.attachment.ConnectionPrefaceAttachment;
import shiip.server.attachment.ReadAttachment;
import shiip.server.exception.ConnectionPrefaceException;
import shiip.transmission.AsynchronousMessageSender;
import shiip.util.EncoderDecoderWrapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.concurrent.TimeUnit;

import static shiip.server.Server.CLIENT_INACTIVE_TIMEOUT;
import static shiip.util.ServerStrings.CONNECTION_PREFACE_ERROR;
import static shiip.client.Client.CLIENT_CONNECTION_PREFACE;


/**
 * @author Ian Laird
 * @version 1.0
 * Handles a Connection Preface read
 */
public class ConnectionPrefaceHandler implements CompletionHandler<Integer, ConnectionPrefaceAttachment> {

    // bytes failed to be read
    public static final int READ_FAILED = -1;

    /**
     * completed
     * @param result the number of bytes read
     * @param attachment the attachment
     */
    @Override
    public void completed(Integer result, ConnectionPrefaceAttachment attachment) {
        ByteBuffer bb = attachment.getBb();

        // if unable to read in the whole conenction preface terminate the connection
        if(result == READ_FAILED){
            // TODO check this logic
            byte [] readBytes = Arrays.copyOfRange(bb.array(), 0, bb.position());
            failed(new ConnectionPrefaceException(CONNECTION_PREFACE_ERROR,
                    new String(readBytes, StandardCharsets.US_ASCII)), attachment);
        }

        int numLeft = CLIENT_CONNECTION_PREFACE.length - bb.position();

        // if the whole preface has not been read in yet go again
        if(numLeft > 0){
            attachment.getAsynchronousSocketChannel().read(
                    bb, CLIENT_INACTIVE_TIMEOUT, TimeUnit.MILLISECONDS, attachment, this);
        }else {

            // connection preface has been successfully read in so now begin reading

            // ensure that the preface read is valid
            byte[] readBytes = bb.array();
            if (!Arrays.equals(CLIENT_CONNECTION_PREFACE, readBytes)) {
                failed(new ConnectionPrefaceException(CONNECTION_PREFACE_ERROR,
                        new String(readBytes, StandardCharsets.US_ASCII)), attachment);
            }

            // get the encoder and decoder for the connection
            Encoder encoder = EncoderDecoderWrapper.getEncoder();
            Decoder decoder = EncoderDecoderWrapper.getDecoder();

            // each connection gets its own non blocking deframer
            NIODeframer deframer = new NIODeframer();

            // create the byte buffer for this connection
            ByteBuffer byteBuffer = ByteBuffer.allocate(ServerAIO.BUFFER_SIZE);

            // create the streams for the connection
            Map<Integer, ServerStream> streams = new HashMap<>();

            // create the read attachment
            ReadAttachment readAttachment = new ReadAttachment();
            readAttachment.setAsynchronousSocketChannel(attachment.getAsynchronousSocketChannel());
            readAttachment.setDecoder(decoder);
            readAttachment.setDeframer(deframer);
            readAttachment.setByteBuffer(byteBuffer);
            readAttachment.setLogger(attachment.getConnectionAttachment().getLogger());
            readAttachment.setDirectoryBase(attachment.getConnectionAttachment().getFileBase());
            readAttachment.setAsynchronousMessageSender(
                    new AsynchronousMessageSender(attachment.getAsynchronousSocketChannel(),
                            encoder, attachment.getConnectionAttachment().getLogger()));
            readAttachment.setLastEncounteredStreamId(0);
            readAttachment.setStreams(streams);

            // now handle a read
            attachment.getAsynchronousSocketChannel().read(
                    byteBuffer, CLIENT_INACTIVE_TIMEOUT, TimeUnit.MILLISECONDS, readAttachment, new ReadHandler());
        }
    }

    /**
     * failed
     * @param exc the cause
     * @param attachment the attachment
     */
    @Override
    public void failed(Throwable exc, ConnectionPrefaceAttachment attachment) {

        // if the error is a bad connection preface then terminate
        if(exc instanceof ConnectionPrefaceException){
            attachment.getConnectionAttachment().getLogger().severe(
                    CONNECTION_PREFACE_ERROR + ((ConnectionPrefaceException)exc).getReceivedString());
        }else if(exc instanceof InterruptedByTimeoutException){
            attachment.getConnectionAttachment().getLogger().log(Level.WARNING, "Timeout occurred", exc);
        }

        // close the socket
        try {
            attachment.getAsynchronousSocketChannel().close();
        }catch(IOException e){}


        // TODO why is this here
        // zero out the byte buffer
        attachment.getBb().clear();
        attachment.getBb().put(new byte [CLIENT_CONNECTION_PREFACE.length]);
        attachment.getBb().clear();

    }

}
