package shiip.util;

/**
 * @author Laird
 */
public class ServerStrings {

    // for a parsing error that is encountered
    public static final String UNABLE_TO_PARSE = "Unable to parse: ";

    // unexpected message
    public static final String UNEXPECTED_MESSAGE = "Unexpected message: ";

    // for an invalid message
    public static final String INVALID_MESSAGE = "Invalid message: ";

    // received message
    public static final String RECEIVED_MESSAGE = "Received message: ";

    // cannot open the requested file
    public static final String UNABLE_TO_OPEN_FILE = "File not found";

    // cannot access the requested directory
    public static final String CANNOT_REQUEST_DIRECTORY = "Cannot request directory: ";

    // no path in the Headers frame
    public static final String NO_PATH_SPECIFIED = "No or bad path";

    // duplicate ClientStream id
    public static final String DUPLICATE_STREAM_ID = "Duplicate request: ";

    // illegal stream id
    public static final String ILLEGAL_STREAM_ID = "Illegal stream ID: ";

    // 404 for no path specified
    public static final String ERROR_404_NO_PATH = "404 No or bad path";

    // 404 error for file not found
    public static final String ERROR_404_FILE = "404 File not found";

    // 404 error for directory not found
    public static final String ERROR_404_DIRECTORY = "404 Cannot request directory";

    // if settings frame not sent during the startup
    public static final String ERROR_NO_CONNECTION_SETTINGS_FRAME = "Error Did not receive Settings Frame";

    // for a bad connection preface
    public static final String CONNECTION_PREFACE_ERROR = "Bad Preface: ";
}
