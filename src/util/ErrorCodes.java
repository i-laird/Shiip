/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 2
 * Class: Data Comm
 *******************************************************/

package util;

/**
 * Error Codes for Client and Server
 * @author Laird
 */
public final class ErrorCodes {

    // ERROR CODES ***********************************************

    // invalid number of params error
    public static final int INVALID_PARAM_NUMBER_ERROR = 1;

    // invalid url error
    public static final int BAD_URL_ERROR = 2;

    //invalid port error
    public static final int BAD_PORT_ERROR = 3;

    //unable to create socket error
    public static final int SOCKET_CREATION_ERROR = 4;

    // for when there is an error writing to a file
    public static final int ERROR_WRITING_TO_FILE = 5;

    // for when there is an error in receiving the connection preface
    public static final int ERROR_SENDING_REQUEST_TO_SERVER = 6;

    // for when there is an error sending the connection preface
    public static final int ERROR_SENDING_CONNECTION_PREFACE = 7;

    // for when there is a communication error with the network
    public static final int NETWORK_ERROR = 8;

    // error getting socket io streams
    public static final int ERROR_SOCKET_GET_IO = 9;

    //error closing socket
    public static final int ERROR_CLOSING_SOCKET = 10;

    // bad port number
    public static final int BAD_PORT_NUM_ERROR = 11;

    // unable to create the logger
    public static final int LOGGER_PROBLEM = 12;

    // problem with doc root
    public static final int ERROR_DOC_ROOT = 13;

    // bad public value
    public static final int BAD_PUBLIC_VALUE = 14;

    // error message received
    public static final int ERROR_MESSAGE_RECEIVED = 15;

    // for a bad op
    public static final int ERROR_OP_SPECIFIED = 16;
}
