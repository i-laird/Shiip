/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 3
 * Class: Data Comm
 *******************************************************/

package shiip.transmission;

import com.twitter.hpack.Decoder;
import shiip.serialization.BadAttributeException;
import shiip.serialization.Deframer;
import shiip.serialization.Message;

import java.io.IOException;
import java.io.InputStream;

/**
 * Used to receive {@link Message}
 * @author Ian laird
 */
public class MessageReceiver {
    private Deframer deframer = null;
    private Decoder decoder = null;

    /**
     * constructor
     * @param in the input stream to read the messages from
     * @param decoder the decoder to use
     */
    public MessageReceiver(InputStream in, Decoder decoder) {
        this.deframer = new Deframer(in);
        this.decoder = decoder;
    }

    /**
     * decodes, and then deframes a message from the saved input stream
     * @return the retrieved Message
     * @throws IOException if unable to read the message
     * @throws BadAttributeException if the message has bad attributes
     */
    public Message receiveMessage() throws IOException, BadAttributeException{
        synchronized (this) {
            return Message.decode(deframer.getFrame(), decoder);
        }
    }
}
