/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/


package shiip.server.attachment;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Semaphore;

/**
 * @author Ian Laird
 * Attachment for Connection Preface
 */
public class ConnectionPrefaceAttachment {

    // the byte buffer
    private ByteBuffer  bb;

    // the async socket channel
    private AsynchronousSocketChannel asynchronousSocketChannel;

    private ConnectionAttachment connectionAttachment;

    /**
     * constructor
     * @param bb the byte buffer array
     * @param asynchronousSocketChannel the async socket channel
     * @param sem the semaphore
     */
    public ConnectionPrefaceAttachment(ByteBuffer bb, AsynchronousSocketChannel asynchronousSocketChannel, ConnectionAttachment connectionAttachment) {
        this.bb = bb;
        this.asynchronousSocketChannel = asynchronousSocketChannel;
        this.connectionAttachment = connectionAttachment;
    }

    /**
     * gets the bb array
     * @return getter
     */
    public ByteBuffer getBb() {
        return bb;
    }

    /**
     * sets the bb array
     * @param bb the bb array to use
     */
    public void setBb(ByteBuffer bb) {
        this.bb = bb;
    }

    /**
     * gets the async socket channel
     * @return the async socket channel
     */
    public AsynchronousSocketChannel getAsynchronousSocketChannel() {
        return asynchronousSocketChannel;
    }

    /**
     * sets the async socket channel
     * @param asynchronousSocketChannel setter
     */
    public void setAsynchronousSocketChannel(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    public ConnectionAttachment getConnectionAttachment() {
        return connectionAttachment;
    }

    public void setConnectionAttachment(ConnectionAttachment connectionAttachment) {
        this.connectionAttachment = connectionAttachment;
    }
}
