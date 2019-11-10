package shiip.server;

import shiip.transmission.MessageSender;

import java.io.InputStream;
import java.nio.channels.AsynchronousFileChannel;

public class UnthreadedServerStream extends ServerStream {

    private AsynchronousFileChannel asynchronousFileChannel = null;

    /**
     * constructor
     * @param streamId the id of the stream
     * @param ms sends messages to output stream
     * @param bytesToRead the number of bytes to be read from the input stream
     */
    public UnthreadedServerStream(AsynchronousFileChannel asynchronousFileChannel, int streamId, MessageSender ms, int bytesToRead){
        this.asynchronousFileChannel = asynchronousFileChannel;
        this.messageSender = ms;
        this.bytesProcessed = 0;
        this.bytesToRead = bytesToRead;
        this.isDone = false;
        this.streamId = streamId;
        this.nextAllowedSendTime = System.currentTimeMillis();
    }

    /**
     * runs the unthreaded server stream
     */
    @Override
    public void run() {

    }
}
