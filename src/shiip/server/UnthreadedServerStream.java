package shiip.server;

import shiip.transmission.MessageSender;

import java.io.InputStream;

public class UnthreadedServerStream extends ServerStream {

    /**
     * constructor
     * @param streamId the id of the stream
     * @param ms sends messages to output stream
     * @param bytesToRead the number of bytes to be read from the input stream
     */
    public UnthreadedServerStream(int streamId, MessageSender ms, int bytesToRead){
        this.messageSender = ms;
        this.bytesProcessed = 0;
        this.bytesToRead = bytesToRead;
        this.isDone = false;
        this.streamId = streamId;
        this.nextAllowedSendTime = System.currentTimeMillis();
    }
    @Override
    public void run() {

    }
}
