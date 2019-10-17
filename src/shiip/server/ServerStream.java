/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 3
 * Class: Data Comm
 *******************************************************/

package shiip.server;

import shiip.serialization.BadAttributeException;
import shiip.serialization.Data;
import shiip.transmission.MessageSender;

import java.io.IOException;
import java.io.InputStream;

import static shiip.server.Server.MAXDATASIZE;

/**
 * a stream within the server side of a Shiip connection
 * @author ian laird
 */
public class ServerStream extends Thread{

    // the input stream associated with this stream
    private InputStream in;

    // sends messages to an output stream
    private MessageSender messageSender;

    // the number of bytes that have been sent to the output stream
    private int bytesProcessed;

    // the number of bytes that are to be read from the input stream
    private int bytesToRead;

    // indicates if the stream is done
    private boolean isDone;

    // the stream id of this stream
    private int streamId = 0;

    // the next system time that the stream can send a message
    private long nextAllowedSendTime;

    /**
     * constructor
     * @param fin the input stream that bytes will be read from
     * @param ms sends messages to output stream
     * @param bytesToRead the number of bytes to be read from the input stream
     */
    public ServerStream(int streamId, InputStream fin, MessageSender ms, int bytesToRead){
        this.in = fin;
        this.messageSender = ms;
        this.bytesProcessed = 0;
        this.bytesToRead = bytesToRead;
        this.isDone = false;
        this.streamId = streamId;
        this.nextAllowedSendTime = System.currentTimeMillis();
    }

    /**
     * the main method of a ServerStream
     */
    public void run(){
        try {

            long currTime = System.currentTimeMillis();

            //wait until the next allowed time
            if(currTime < this.nextAllowedSendTime) {
                this.wait(this.nextAllowedSendTime - currTime);
            }

            while (!this.isDone && !this.isInterrupted()) {
                this.writeFrameToOutputStream();
            }
        }catch(IOException | InterruptedException e){

            // nothing needs to be done because the thread will terminate
        }
    }

    /**
     * writes a Data Frame to the Output Stream
     * @throws IOException if problem with reading or writing
     */
    public void writeFrameToOutputStream() throws IOException {

        if(this.isDone){
            return;
        }

        if(System.currentTimeMillis() < this.nextAllowedSendTime){
            return;
        }

        int numToWrite;

        if(bytesProcessed + MAXDATASIZE < bytesToRead){
            numToWrite = MAXDATASIZE;
        }else{
            numToWrite = bytesToRead - bytesProcessed;
            this.isDone = true;
        }

        // read these bytes from the file
        byte [] toWrite = new byte[numToWrite];
        in.readNBytes(toWrite, 0, numToWrite);

        try {
            Data data = new Data(streamId, isDone, toWrite);

            // now write to the output stream
            messageSender.sendFrame(data);
        }catch (BadAttributeException e){

            // unreachable
        }
        // increment the processed count
        bytesProcessed += numToWrite;
        this.nextAllowedSendTime = System.currentTimeMillis() + Server.MINDATAINTERVAL;
    }

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
