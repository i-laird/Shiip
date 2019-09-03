/* Framer.java 1.0 8/31/2019
 *
 * Copyright 2019 Ian Laird
 */


package shiip.serialization;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Frames a message so that it can be sent in the shiip protocol.
 *
 * @version 1.0
 * @author Ian Laird
 */
public class Framer {
    public static final int PREFIX_SIZE = 3;
    public static final int MAXIMUM_PAYLOAD_SIZE = 2048;
    public static final int HEADER_SIZE = 6;
    public static final int MAXIMUM_PAYLOAD_AND_HEADER_SIZE = MAXIMUM_PAYLOAD_SIZE + HEADER_SIZE;

    private OutputStream out = null;

    /**
     * Custom Constructor
     *
     * @param out where shiip protocol messages will be sent to
     */
    public Framer(OutputStream out){
        this.out = Objects.requireNonNull(out, "Output stream cannot be null");
    }

    /**
     * Frames a message and then sends it in the associated output stream.
     *
     * @param message the message that is to be framed
     * @throws IOException if the frame payload is longer than 2048 bytes
     * @throws IOException if the message is not at least 6 bytes long
     * @throws NullPointerException if the message is null
     */
    public void putFrame(byte [] message) throws IOException, NullPointerException{
        message = Objects.requireNonNull(message, "The message cannot be null");

        //see if the message is too long (the six bytes of header are not included)
        if(message.length > MAXIMUM_PAYLOAD_AND_HEADER_SIZE){
            throw new IOException("The frame payload is too long");
        }

        //see if the message is too short
        if(message.length < HEADER_SIZE){
            throw new IOException("The 24 bit header must be included");
        }


        byte [] lengthAsBytes = ByteBuffer.allocate(4).putInt(message.length - HEADER_SIZE).array();

        //only use the last three bytes
        byte [] framedMessage = new byte [message.length + PREFIX_SIZE ];

        //first put in the prefix bytes
        for (int i =0; i < PREFIX_SIZE; i++){
            framedMessage[i] = lengthAsBytes[i + 1];
        }

        for(int i = 0; i < message.length; i++){
            framedMessage[i + PREFIX_SIZE] = message[i];
        }

        //write the framed message to the output stream
        out.write(framedMessage);
    }
}
