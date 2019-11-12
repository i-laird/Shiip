/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server;

import shiip.server.attachment.FileReadAttachment;
import shiip.server.attachment.ReadAttachment;
import shiip.server.completionHandlers.FileReadHandler;
import shiip.transmission.AsynchronousMessageSender;
import shiip.transmission.MessageSender;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.logging.Logger;

/**
 * an async server stream
 * @author Ian Laird
 * @version 1.0
 */
public class UnthreadedServerStream extends ServerStream {

    // the async file channel
    private AsynchronousFileChannel asynchronousFileChannel = null;

    // the logger
    private Logger logger;

    // the socket channel
    private AsynchronousSocketChannel socketChannel = null;

    /**
     * @param streamId the id of the stream
     * @param ms sends messages to output stream
     * @param bytesToRead the number of bytes to be read from the input stream
     * @param logger the logger
     * @param socketChannel the socket channel
     * @param asynchronousFileChannel the async file channel
     */
    public UnthreadedServerStream(Logger logger, AsynchronousSocketChannel socketChannel, AsynchronousFileChannel asynchronousFileChannel, int streamId, MessageSender ms, int bytesToRead){
        this.asynchronousFileChannel = asynchronousFileChannel;
        this.messageSender = ms;
        this.bytesProcessed = 0;
        this.bytesToRead = bytesToRead;
        this.isDone = false;
        this.streamId = streamId;
        this.nextAllowedSendTime = System.currentTimeMillis();
        this.logger = logger;
        this.socketChannel = socketChannel;
    }

    /**
     * runs the unthreaded server stream
     */
    @Override
    public void run() {

        // create the byte buffer of maximum size allowed
        ByteBuffer bb = ByteBuffer.allocateDirect(ServerAIO.MAXDATASIZE);

        //create the read attachment
        ReadAttachment ra = new ReadAttachment();
        ra.setLogger(logger);
        ra.setAsynchronousSocketChannel(socketChannel);
        ra.setByteBuffer(bb);
        ra.setAsynchronousMessageSender((AsynchronousMessageSender) this.messageSender);

        // create the file read attachment
        FileReadAttachment fra = new FileReadAttachment();
        fra.setReadAttachment(ra);
        fra.setFileChannel(asynchronousFileChannel);
        fra.setNumRead(this.bytesProcessed);

        // now actually perform the read
        this.asynchronousFileChannel.read(bb,
                        this.bytesProcessed,
                        fra, new FileReadHandler());
    }
}
