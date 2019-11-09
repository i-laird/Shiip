package shiip.transmission;

import com.twitter.hpack.Encoder;
import shiip.serialization.Framer;
import shiip.serialization.Message;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

public class AsynchronousMessageSender extends MessageSender {

    private AsynchronousSocketChannel asynchronousSocketChannel;
    private Encoder encoder;

    public AsynchronousMessageSender(AsynchronousSocketChannel asynchronousSocketChannel, Encoder encoder) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
        this.encoder = encoder;
    }

    /**
     * Encodes, frames, and then sends a message over the saved output stream
     * @param m message to send
     * @throws IOException if unable to send the message
     */
    public void sendFrame(Message m) throws IOException{
        byte [] toSend = Framer.getFramed(m.encode(this.encoder));

        //TODO actually do the write
    }

}
