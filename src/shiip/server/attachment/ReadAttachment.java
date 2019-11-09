/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.attachment;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;
import shiip.serialization.NIODeframer;
import shiip.transmission.AsynchronousMessageSender;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.logging.Logger;

public class ReadAttachment {

    private AsynchronousSocketChannel asynchronousSocketChannel;
    private Decoder decoder;
    private NIODeframer deframer;
    private ByteBuffer byteBuffer;
    private Logger logger;
    private File directoryBase;
    private AsynchronousMessageSender asynchronousMessageSender;
    private Integer lastEncounteredStreamId;

    public AsynchronousSocketChannel getAsynchronousSocketChannel() {
        return asynchronousSocketChannel;
    }

    public void setAsynchronousSocketChannel(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    public Decoder getDecoder() {
        return decoder;
    }

    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    public NIODeframer getDeframer() {
        return deframer;
    }

    public void setDeframer(NIODeframer deframer) {
        this.deframer = deframer;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public File getDirectoryBase() {
        return directoryBase;
    }

    public void setDirectoryBase(File directoryBase) {
        this.directoryBase = directoryBase;
    }

    public AsynchronousMessageSender getAsynchronousMessageSender() {
        return asynchronousMessageSender;
    }

    public void setAsynchronousMessageSender(AsynchronousMessageSender asynchronousMessageSender) {
        this.asynchronousMessageSender = asynchronousMessageSender;
    }

    public Integer getLastEncounteredStreamId() {
        return lastEncounteredStreamId;
    }

    public void setLastEncounteredStreamId(Integer lastEncounteredStreamId) {
        this.lastEncounteredStreamId = lastEncounteredStreamId;
    }
}
