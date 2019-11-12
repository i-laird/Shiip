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

    // the array of byte buffers
    private ByteBuffer [] bb;

    // the async socket channel
    private AsynchronousSocketChannel asynchronousSocketChannel;

    // used to block until the connection preface has been read in
    private Semaphore sem;

    /**
     * constructor
     * @param bb the byte buffer array
     * @param asynchronousSocketChannel the async socket channel
     * @param sem the semaphore
     */
    public ConnectionPrefaceAttachment(ByteBuffer[] bb, AsynchronousSocketChannel asynchronousSocketChannel, Semaphore sem) {
        this.bb = bb;
        this.asynchronousSocketChannel = asynchronousSocketChannel;
        this.sem = sem;
    }

    /**
     * gets the bb array
     * @return getter
     */
    public ByteBuffer[] getBb() {
        return bb;
    }

    /**
     * sets the bb array
     * @param bb the bb array to use
     */
    public void setBb(ByteBuffer[] bb) {
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

    public Semaphore getSem() {
        return sem;
    }

    public void setSem(Semaphore sem) {
        this.sem = sem;
    }
}
