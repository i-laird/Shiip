package shiip.server.attachment;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class ConnectionPrefaceAttachment {
    private ByteBuffer [] bb;
    private AsynchronousSocketChannel asynchronousSocketChannel;

    public ConnectionPrefaceAttachment(ByteBuffer[] bb, AsynchronousSocketChannel asynchronousSocketChannel) {
        this.bb = bb;
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    public ByteBuffer[] getBb() {
        return bb;
    }

    public void setBb(ByteBuffer[] bb) {
        this.bb = bb;
    }

    public AsynchronousSocketChannel getAsynchronousSocketChannel() {
        return asynchronousSocketChannel;
    }

    public void setAsynchronousSocketChannel(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }
}
