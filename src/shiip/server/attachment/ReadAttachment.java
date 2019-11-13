/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.attachment;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;
import shiip.serialization.NIODeframer;
import shiip.server.ServerStream;
import shiip.transmission.AsynchronousMessageSender;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Ian laird
 */
public class ReadAttachment {

    // the socket channel
    private AsynchronousSocketChannel asynchronousSocketChannel;

    // the decoder to use
    private Decoder decoder;

    // the deframer to use
    private NIODeframer deframer;

    // the byte buffer to use
    private ByteBuffer byteBuffer;

    // the logger to use
    private Logger logger;

    // the directory base
    private File directoryBase;

    // the message sender to use
    private AsynchronousMessageSender asynchronousMessageSender;

    // the last encountered stream id
    private Integer lastEncounteredStreamId;

    // the streams for the connection
    private Map<Integer, ServerStream> streams;

    // the current stream id
    private int currStreamId;

    /**
     * gets the async socket channel
     * @return the socket channel
     */
    public AsynchronousSocketChannel getAsynchronousSocketChannel() {
        return asynchronousSocketChannel;
    }


    /**
     * sets the socket channel
     * @param asynchronousSocketChannel the socket channel
     */
    public void setAsynchronousSocketChannel(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    /**
     * gets the decoder
     * @return the decoder
     */
    public Decoder getDecoder() {
        return decoder;
    }

    /**
     * set decoder
     * @param decoder decoder
     */
    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    /**
     * deframer
     * @return deframer
     */
    public NIODeframer getDeframer() {
        return deframer;
    }

    /**
     * deframer
     * @param deframer deframer
     */
    public void setDeframer(NIODeframer deframer) {
        this.deframer = deframer;
    }


    /**
     * bb
     * @return bb
     */
    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    /**
     * bb
     * @param byteBuffer  bb
     */
    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    /**
     * logger
     * @return logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * logger
     * @param logger logger
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * directory base
     * @return the directory base
     */
    public File getDirectoryBase() {
        return directoryBase;
    }

    /**
     * directory base
     * @param directoryBase directory base
     */
    public void setDirectoryBase(File directoryBase) {
        this.directoryBase = directoryBase;
    }

    /**
     * message sender
     * @return the message sender
     */
    public AsynchronousMessageSender getAsynchronousMessageSender() {
        return asynchronousMessageSender;
    }

    /**
     * message sender
     * @param asynchronousMessageSender the message sender
     */
    public void setAsynchronousMessageSender(AsynchronousMessageSender asynchronousMessageSender) {
        this.asynchronousMessageSender = asynchronousMessageSender;
    }

    /**
     * the last encountered stream id
     * @return last encountered stream id
     */
    public Integer getLastEncounteredStreamId() {
        return lastEncounteredStreamId;
    }

    /**
     * stream id
     * @param lastEncounteredStreamId id
     */
    public void setLastEncounteredStreamId(Integer lastEncounteredStreamId) {
        this.lastEncounteredStreamId = lastEncounteredStreamId;
    }

    /**
     * gets the current stream id
     * @return the current stream id
     */
    public int getCurrStreamId() {
        return currStreamId;
    }

    /**
     * sets the current stream id
     * @param currStreamId the current stream id
     */
    public void setCurrStreamId(int currStreamId) {
        this.currStreamId = currStreamId;
    }

    /**
     * gets the streams
     * @return the streams
     */
    public Map<Integer, ServerStream> getStreams() {
        return streams;
    }

    /**
     * sets the streams
     * @param streams the streams
     */
    public void setStreams(Map<Integer, ServerStream> streams) {
        this.streams = streams;
    }
}
