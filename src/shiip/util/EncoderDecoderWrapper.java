/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 3
 * Class: Data Comm
 *******************************************************/

package shiip.util;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;

/**
 * holds the global encoder and decoder
 */
public class EncoderDecoderWrapper {

    // max header size for encoder and decoder
    private static final int MAX_HEADER_SIZE = 1024;

    // max header table size
    private static final int MAX_HEADER_TABLE_SIZE = 1024;

    /**
     * gets the global encoder
     * @return the global encoder
     */
    public static Encoder getEncoder(){
        return new Encoder(MAX_HEADER_TABLE_SIZE);
    }

    /**
     * gets the global decoder
     * @return the global decoder
     */
    public static Decoder getDecoder(){
        return new Decoder(MAX_HEADER_SIZE, MAX_HEADER_TABLE_SIZE);
    }

    /**
     * cannot create instance variables of this class
     */
    private EncoderDecoderWrapper(){}
}
