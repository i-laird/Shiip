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

    // Maximum size of the payload
    public static final int MAXIMUM_PAYLOAD_SIZE = 16384;

    // Size of the length field
    public static final int PREFIX_SIZE = 3;

    // Size of the headers of the payload
    public static final int HEADER_SIZE = 6;

    // Data identifier type
    static final byte DATA_TYPE = (byte)0x0;

    // Settings identifier type
    static final byte SETTINGS_TYPE = (byte)0x4;

    // Window_Update identifier type
    static final byte WINDOW_UPDATE_TYPE = (byte)0x8;

    // Unset flags field
    static final byte NO_FLAGS = 0x0;

    // Mask for single most significant byte
    static final byte HIGHEST_BIT_IN_BYTE = (byte)0X128;

    // Full mask for byte
    public static final byte MAX_BYTE = (byte)0xFF;

    // Position of the flag field in the header
    static final int FLAG_POS_IN_HEADER = 1;

    // Required Settings flag field when serializing
    static final byte REQUIRED_SETTINGS_FLAGS_SERIALIZATION = 0x1;

    // Position of the R bit in the header
    static final int BYTE_CONTAINING_R_BIT_LOCATION = 3;

    // Position of the second R bit in the header of a Window_Update
    static final int BYTE_CONTAINING_SECOND_R_BIT_WINDOW_UPDATE = 7;

     public static final int LARGEST_INT = 2147483647;

}
