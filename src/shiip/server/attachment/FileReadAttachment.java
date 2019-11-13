/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.attachment;

import java.nio.channels.AsynchronousFileChannel;

/**
 * @author Ian Laird
 * @version 1.0
 * File Read Attachment
 */
public class FileReadAttachment {

    // the associated read attachment
    private ReadAttachment readAttachment;

    // the async file channel
    private AsynchronousFileChannel fileChannel;

    // the number of bytes read TODO check this
    int numRead = 0;

    int streamId;

    /**
     * gets the read attachement
     * @return the read attachment
     */
    public ReadAttachment getReadAttachment() {
        return readAttachment;
    }

    /**
     * sets the read attachment
     * @param readAttachment the read attachment
     */
    public void setReadAttachment(ReadAttachment readAttachment) {
        this.readAttachment = readAttachment;
    }

    /**
     * gets the file channel
     * @return the file channel
     */
    public AsynchronousFileChannel getFileChannel() {
        return fileChannel;
    }

    /**
     * sets the file channel
     * @param fileChannel the file channel
     */
    public void setFileChannel(AsynchronousFileChannel fileChannel) {
        this.fileChannel = fileChannel;
    }

    /**
     * gets the number read
     * @return the number read
     */
    public int getNumRead() {
        return numRead;
    }

    /**
     * sets the number read
     * @param numRead the number read
     */
    public void setNumRead(int numRead) {
        this.numRead = numRead;
    }

    public int getStreamId() {
        return streamId;
    }

    public void setStreamId(int streamId) {
        this.streamId = streamId;
    }
}
