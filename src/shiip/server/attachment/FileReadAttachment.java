package shiip.server.attachment;

import java.nio.channels.AsynchronousFileChannel;

public class FileReadAttachment {

    private ReadAttachment readAttachment;
    private AsynchronousFileChannel fileChannel;
    int numRead = 0;

    public ReadAttachment getReadAttachment() {
        return readAttachment;
    }

    public void setReadAttachment(ReadAttachment readAttachment) {
        this.readAttachment = readAttachment;
    }

    public AsynchronousFileChannel getFileChannel() {
        return fileChannel;
    }

    public void setFileChannel(AsynchronousFileChannel fileChannel) {
        this.fileChannel = fileChannel;
    }

    public int getNumRead() {
        return numRead;
    }

    public void setNumRead(int numRead) {
        this.numRead = numRead;
    }
}
