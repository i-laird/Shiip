/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 3
 * Class: Data Comm
 *******************************************************/

package shiip.server;

import shiip.serialization.BadAttributeException;
import shiip.serialization.Data;
import shiip.util.MessageSender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static shiip.server.Server.MAXDATASIZE;

/**
 * a stream within the server side of a Shiip connection
 * @author ian laird
 */
public class ServerStream {

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
    }

    /**
     * writes a Data Frame to the Output Stream
     * @throws IOException if problem with reading or writing
     */
    public void writeFrameToOutputStream() throws IOException {

        if(this.isDone){
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
    }

    /**
     * indicates if all bytes have been read from the input stream
     * @return TRUE means done
     */
    public boolean isDone() {
        return isDone;
    }
}
