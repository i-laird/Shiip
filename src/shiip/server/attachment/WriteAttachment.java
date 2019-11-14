/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.attachment;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

/**
 * @author Ian laird
 * @version 1.0
 */
public class WriteAttachment {

    // the socket channel to write to
    private AsynchronousSocketChannel asynchronousSocketChannel;

    // to logger to log to
    private Logger logger;

    // the byte buffer that is to be written
    private ByteBuffer byteBuffer;

    // the semaphore for writing
    private Semaphore sem;

    /**
     * get the async socket channel
     * @return the socket channel
     */
    public AsynchronousSocketChannel getAsynchronousSocketChannel() {
        return asynchronousSocketChannel;
    }

    /**
     * sets the aync socket channel
     * @param asynchronousSocketChannel the socket channel
     */
    public void setAsynchronousSocketChannel(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    /**
     * gets the logger
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * sets the logger
     * @param logger the logger
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * gets the byte buffer
     * @return the byte buffer
     */
    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    /**
     * sets the byte buffer
     * @param byteBuffer the byte buffer
     */
    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public Semaphore getSem() {
        return sem;
    }

    public void setSem(Semaphore sem) {
        this.sem = sem;
    }
}
