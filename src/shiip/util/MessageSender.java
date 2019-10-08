/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 3
 * Class: Data Comm
 *******************************************************/

package shiip.util;

import com.twitter.hpack.Encoder;
import shiip.serialization.Framer;
import shiip.serialization.Message;

import java.io.IOException;
import java.io.OutputStream;

/**
 * used to send messages to the given output stream
 * @author Ian Laird
 */
public class MessageSender {

    // the framer that is to be used in this connection
    private Framer framer = null;

    //the encoder for this connection
    private Encoder encoder = null;

    /**
     * constructor
     * @param out the output stream to send messages to
     * @param encoder the encoder to use
     */
    public MessageSender(OutputStream out, Encoder encoder) {
        this.framer = new Framer(out);
        this.encoder = encoder;
    }

    /**
     * Encodes, frames, and then sends a message over the saved output stream
     * @param m message to send
     * @throws IOException if unable to send the message
     */
    public void sendFrame(Message m) throws IOException{
        framer.putFrame(m.encode(this.encoder));
    }
}
