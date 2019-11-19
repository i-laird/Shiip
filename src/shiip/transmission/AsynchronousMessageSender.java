/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.transmission;

import com.twitter.hpack.Encoder;
import shiip.serialization.Framer;
import shiip.serialization.Message;
import shiip.server.attachment.WriteAttachment;
import shiip.server.completionHandlers.WriteHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

/**
 * async message sender
 * @author Ian Laird
 * @version 1.0
 */
public class AsynchronousMessageSender extends MessageSender {

    // the socket channel
    private AsynchronousSocketChannel asynchronousSocketChannel;

    // the encoder to use
    private Encoder encoder;

    // the logger to use
    private Logger logger;

    private final Queue<ByteBuffer> outputBuffer;

    /**
     * constructor
     * @param asynchronousSocketChannel the socket channel to use
     * @param encoder the encoder to use
     * @param logger the logger to use
     */
    public AsynchronousMessageSender(AsynchronousSocketChannel asynchronousSocketChannel, Encoder encoder, Logger logger) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
        this.encoder = encoder;
        this.logger = logger;
        this.outputBuffer = new LinkedList<>();
    }

    /**
     * Encodes, frames, and then sends a message over the saved output stream
     * @param m message to send
     * @throws IOException if unable to send the message
     */
    public void sendFrame(Message m) throws IOException{
        byte[] toSend = null;

        // to avoid race condition on the encoder
        synchronized (this) {
            toSend = Framer.getFramed(m.encode(this.encoder));
        }

        ByteBuffer bytes = ByteBuffer.wrap(toSend);

        WriteAttachment writeAttachment = new WriteAttachment();
        writeAttachment.setAsynchronousSocketChannel(this.asynchronousSocketChannel);
        writeAttachment.setByteBuffer(bytes);
        writeAttachment.setLogger(logger);
        writeAttachment.setOutputBuffer(this.outputBuffer);

        // this synchronization block is to protect the output buffer from race condition
        synchronized (this.outputBuffer){

            outputBuffer.add(bytes);

            // if nothing is queued up send it immediately
            if(this.outputBuffer.size() == 1){
                asynchronousSocketChannel.write(bytes, writeAttachment, new WriteHandler());
            }

        }
    }

    /**
     * get the async socket channel
     * @return the socket channel
     */
    public AsynchronousSocketChannel getAsynchronousSocketChannel() {
        return asynchronousSocketChannel;
    }
}
