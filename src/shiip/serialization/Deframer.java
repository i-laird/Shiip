/* Deframer.java 1.0 8/31/2019
 *
 * Copyright 2019 Ian Laird
 */


package shiip.serialization;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

/* the size of a shiip message prefix */
import static shiip.serialization.Framer.HEADER_SIZE;
import static shiip.serialization.Framer.PREFIX_SIZE;

/**
 * Able to deframe a message sent using the shiip protocol.
 *
 * @version 1.0
 * @author Ian Laird
 */
public class Deframer {

    /* the input stream from which shiip messages will be read */
    private InputStream in = null;

    /**
     * Custom Constructor
     *
     * @param in where shiip protocol messages will be read from
     */
    public Deframer(InputStream in){
        this.in = Objects.requireNonNull(in, "Null is not allowed for the input stream");
    }

    /**
     * Reads a message from the input stream it was constructed with
     * and returns the deframed message.
     *
     * @return the message after it has been deframed
     * @throws IOException if the input stream has an i/o error
     * @throws EOFException if the prefix bytes cannot be properly read or
     *                      if an EOF is encountered prematurely
     */
    public byte [] getFrame() throws IOException{
        int prefixBytesRead = 0;

        //get the length of the frame from the first 3 bytes
        byte [] prefixBytes = new byte[4];
        prefixBytes[0] = (byte)0;
        prefixBytesRead = in.read(prefixBytes, 1, PREFIX_SIZE);
        if(prefixBytesRead < PREFIX_SIZE){
            throw new EOFException("Unable to read Prefix Bytes");
        }

        int length = ByteBuffer.wrap(prefixBytes).getInt();

        length += HEADER_SIZE;

        byte [] message = new byte [length];

        int numBytesRead = in.read(message, 0, length);

        if(numBytesRead < length){
            throw new EOFException("Premature EOF encountered");
        }

        return message;
    }
}
