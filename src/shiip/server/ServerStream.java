/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 3
 * Class: Data Comm
 *******************************************************/

package shiip.server;

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

    // the output stream associated with this stream
    private OutputStream out;

    // the number of bytes that have been sent to the output stream
    private int bytesProcessed;

    // the number of bytes that are to be read from the input stream
    private int bytesToRead;

    // indicates if the stream is done
    private boolean isDone;

    /**
     * constructor
     * @param fin the input stream that bytes will be read from
     * @param out the output stream that bytes will be written to
     * @param bytesToRead the number of bytes to be read from the input stream
     */
    public ServerStream(InputStream fin, OutputStream out, int bytesToRead){
        this.in = fin;
        this.out = out;
        this.bytesProcessed = 0;
        this.bytesToRead = bytesToRead;
        this.isDone = false;
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
        byte [] toWrite = in.readNBytes(numToWrite);

        // now write these to the output stream
        out.write(toWrite);

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
