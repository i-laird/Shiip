/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server;

import shiip.transmission.MessageSender;

/**
 * @author Ian Laird
 * @version 1.0
 * An abstraction of a server stream
 */
public abstract class ServerStream {

    // sends messages to an output stream
    protected MessageSender messageSender;

    // the number of bytes that have been sent to the output stream
    protected int bytesProcessed;

    // the number of bytes that are to be read from the input stream
    protected int bytesToRead;

    // indicates if the stream is done
    protected boolean isDone;

    // the stream id of this stream
    protected int streamId = 0;

    // the next system time that the stream can send a message
    protected long nextAllowedSendTime;

    /**
     * the main method of a ServerStream
     */
    public abstract void run();

    /**
     * indicates if all bytes have been read from the input stream
     * @return TRUE means done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * the next allowed send time
     * @return next allowed send time
     */
    public long getNextAllowedSendTime() {
        return nextAllowedSendTime;
    }
}
