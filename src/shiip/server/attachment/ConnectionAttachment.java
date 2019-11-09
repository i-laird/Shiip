/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.attachment;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.logging.Logger;

public class ConnectionAttachment {
    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    private Logger logger;

    public AsynchronousServerSocketChannel getAsynchronousServerSocketChannel() {
        return asynchronousServerSocketChannel;
    }

    public void setAsynchronousServerSocketChannel(AsynchronousServerSocketChannel asynchronousServerSocketChannel) {
        this.asynchronousServerSocketChannel = asynchronousServerSocketChannel;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public ConnectionAttachment(AsynchronousServerSocketChannel asynchronousServerSocketChannel, Logger logger) {
        this.asynchronousServerSocketChannel = asynchronousServerSocketChannel;
        this.logger = logger;
    }
}
