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

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static shiip.util.ServerStrings.CONNECTION_PREFACE_ERROR;


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

            // connection preface has been successfully read in so now begin reading

            // ensure that the preface read is valid
            byte[] readBytes = bb.array();
            if (!Arrays.equals(ServerAIO.CLIENT_CONNECTION_PREFACE, readBytes)) {
                failed(new ConnectionPrefaceException(CONNECTION_PREFACE_ERROR, new String(readBytes, StandardCharsets.US_ASCII)), attachment);
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
            readAttachment.setAsynchronousMessageSender(new AsynchronousMessageSender(attachment.getAsynchronousSocketChannel(), encoder, attachment.getConnectionAttachment().getLogger()));
            readAttachment.setLastEncounteredStreamId(0);
            readAttachment.setStreams(streams);

            // now handle a read
            attachment.getAsynchronousSocketChannel().read(byteBuffer, readAttachment, new ReadHandler());
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

    }

}
