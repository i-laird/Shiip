/*******************************************************
 * Author: Ian Laird, Andrew walker
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/

package shiip.serialization.test;

/**
 * Contains testing constants
 *
 * @version 1.0
 * @author Ian Laird, Andrew Walker
 */
class TestingConstants {
    public static final int MAXIMUM_PAYLOAD_SIZE = 16384;
    public static final int PREFIX_SIZE = 3;
    public static final int HEADER_SIZE = 6;
    public static final int MAXIMUM_PAYLOAD_AND_HEADER_SIZE =
            MAXIMUM_PAYLOAD_SIZE + HEADER_SIZE;
     static final byte DATA_TYPE = (byte)0x0;
     static final byte SETTINGS_TYPE = (byte)0x4;
     static final byte WINDOW_UPDATE_TYPE = (byte)0x8;
     static final byte NO_FLAGS = 0x0;
     static final byte HIGHEST_BIT_IN_BYTE = (byte)0X128;
    public static final byte MAX_BYTE = (byte)0xFF;


    static final int FLAG_POS_IN_HEADER = 1;
     static final byte REQUIRED_SETTINGS_FLAGS_SERIALIZATION = 0x1;
     static final int BYTE_CONTAINING_R_BIT_LOCATION = 3;
     static final int BYTE_CONTAINING_SECOND_R_BIT_WINDOW_UPDATE = 7;
}
