/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server;

import shiip.serialization.*;
import shiip.transmission.MessageSender;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import static shiip.serialization.Headers.NAME_PATH;
import static shiip.serialization.Headers.STATUS;
import static shiip.serialization.Message.*;
import static shiip.serialization.Message.WINDOW_UPDATE_TYPE;
import static shiip.util.ServerStrings.*;
import static shiip.util.ServerStrings.ILLEGAL_STREAM_ID;

/**
 * handles Server Messages
 * @author Ian Laird
 * @version 2.0
 */
public class ServerMessageHandler {

    /**
     * handles a message but calling the correct subroutine
     * @param logger the logger to log to
     * @param m the message to handle
     * @param streams the map from integer to server stream
     * @param directory_base the base directory
     * @param isThreaded true means threaded
     * @param messageSender the mesage sender
     * @param lastEncounteredStreamId the last encountered stream id
     * @throws IOException if network error occurs
     */
    public static void handleMessage(boolean isThreaded, Logger logger, Message m, Map<Integer, ServerStream> streams, File directory_base, MessageSender messageSender, Integer lastEncounteredStreamId ) throws IOException{
        switch(m.getCode()){
            case DATA_TYPE:
                handleDataFrame(logger, (Data)m);
                break;
            case SETTINGS_TYPE:
                handleSettingsFrame(logger, (Settings)m);
                break;
            case HEADER_TYPE:
                handleHeadersFrame(isThreaded, logger, (Headers)m, streams, directory_base, messageSender, lastEncounteredStreamId);
                break;
            case WINDOW_UPDATE_TYPE:
                handleWindowUpdateFrame(logger, (Window_Update)m);
                break;
        }
    }

    /**
     * handles a received {@link Data} frame
     * @param logger the logger
     * @param d the data frame
     */
    private static void handleDataFrame(Logger logger, Data d){
        logger.info(UNEXPECTED_MESSAGE + d.toString());
    }

    /**
     * handles a received {@link Settings} frame
     * @param logger the logger
     * @param s the settings frame
     */
    private static void handleSettingsFrame(Logger logger, Settings s){
        logger.info(RECEIVED_MESSAGE + s.toString());
    }

    /**
     * handles a received {@link Window_Update} frame
     * @param logger the logger
     * @param w the window update frame
     */
    private static void handleWindowUpdateFrame(Logger logger, Window_Update w){
        logger.info(RECEIVED_MESSAGE + w.toString());
    }

    /**
     * handles a received {@link Headers} frame
     * @param logger the logger to log to
     * @param h the headers to handle
     * @param streams the map from integer to server stream
     * @param directory_base the base directory
     * @param messageSender the mesage sender
     * @param lastEncounteredStreamId the last encountered stream id
     * @throws IOException if network error occurs
     */
    private static void handleHeadersFrame(boolean isThreaded, Logger logger, Headers h, Map<Integer, ServerStream> streams, File directory_base, MessageSender messageSender, Integer lastEncounteredStreamId ) throws IOException {
        String path = h.getValue(Headers.NAME_PATH);

        // see if the stream id has already been encountered
        if(streams.containsKey(h.getStreamID())){
            logger.info(DUPLICATE_STREAM_ID + h.toString());
            return;
        }

        // see if there is a path specified
        if(Objects.isNull(path) || path.isEmpty()){
            logger.severe(NO_PATH_SPECIFIED);
            send404File(logger, h.getStreamID(), ERROR_404_NO_PATH, messageSender);
            return;
        }

        // see if the file exists and has correct permissions
        String fileName = h.getValue(NAME_PATH);
        String slashPrepender = fileName.startsWith("/") ? "" : "/";
        String filePath = directory_base.getCanonicalPath() + slashPrepender + fileName;
        File file = new File(filePath);

        // see if a directory
        if(file.exists() && file.isDirectory()){
            logger.severe(CANNOT_REQUEST_DIRECTORY);
            send404File(logger, h.getStreamID(), ERROR_404_DIRECTORY, messageSender);
            return;
        }

        // see if exists and has permissions
        if(!file.exists() || (file.isFile() && !file.canRead())){
            logger.severe(UNABLE_TO_OPEN_FILE + fileName);
            send404File(logger, h.getStreamID(), ERROR_404_FILE, messageSender);
            return;
        }

        // now make sure that the stream id is valid
        if(!testValidStreamId(lastEncounteredStreamId, h.getStreamID())){
            logger.info(ILLEGAL_STREAM_ID + h.toString());
            return;
        }

        // send a 200 status message
        send404File(logger, h.getStreamID(), "200 file found", messageSender);

        lastEncounteredStreamId = h.getStreamID();

        // send file
        if(isThreaded) {
            ThreadedServerStream threadedServerStream =
                    new ThreadedServerStream(lastEncounteredStreamId, new FileInputStream(file),
                            messageSender, (int) file.length());
            streams.put(h.getStreamID(), threadedServerStream);
            // spin off a new thread
            new Thread(threadedServerStream).start();
        }
        else{
            AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.READ);
            //TODO fix this
            UnthreadedServerStream unthreadedServerStream = new UnthreadedServerStream(asynchronousFileChannel, lastEncounteredStreamId, messageSender, (int) file.length());
            streams.put(h.getStreamID(), unthreadedServerStream);
            unthreadedServerStream.run();
        }

    }

    /**
     * sends a 404 message to the client
     * @param logger the logger to use
     * @param streamId the is of the stream that has 404
     * @param message404 the specific message to send
     * @param messageSender the message sender
     */
    private static void send404File(Logger logger, int streamId, String message404, MessageSender messageSender){
        try {
            Headers toSend = new Headers(streamId, true);
            toSend.addValue(STATUS, message404);
            messageSender.sendFrame(toSend);
        }catch(BadAttributeException | IOException e){
            logger.severe("Unable to send 404 message");
        }
    }

    /**
     * sees if a stream id of a headers received from a client is valid
     * @param lastEncounteredStreamId the last stream id encountered
     * @param streamId the stream id to test
     * @return TRUE means that it is a valid stream id (positive and odd and bigger than the last one
     */
    private static boolean testValidStreamId(Integer lastEncounteredStreamId, int streamId){
        return streamId >= lastEncounteredStreamId && ((streamId % 2) == 1);
    }
}
