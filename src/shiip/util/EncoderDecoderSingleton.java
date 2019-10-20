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
public class EncoderDecoderSingleton {

    // max header size for encoder and decoder
    private static final int MAX_HEADER_SIZE = 1024;

    // max header table size
    private static final int MAX_HEADER_TABLE_SIZE = 1024;

    // the global encoder
    private static Encoder encoder = null;

    // the global decoder
    private static Decoder decoder = null;

    /**
     * gets the global encoder
     * @return the global encoder
     */
    public static Encoder getEncoder(){
        if(encoder == null){
            encoder = new Encoder(MAX_HEADER_TABLE_SIZE);
        }
        return encoder;
    }

    /**
     * gets the global decoder
     * @return the global decoder
     */
    public static Decoder getDecoder(){
        if(decoder == null){
            decoder = new Decoder(MAX_HEADER_SIZE, MAX_HEADER_TABLE_SIZE);
        }
        return decoder;
    }

    /**
     * cannot create instance variables of this class
     */
    private EncoderDecoderSingleton(){}
}
