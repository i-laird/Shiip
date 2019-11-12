/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.attachment;

import java.io.File;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.logging.Logger;

/**
 * @author Ian laird
 */
public class ConnectionAttachment {

    // the socket channel
    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    // the logger
    private Logger logger;

    // the file base
    private File fileBase;

    /**
     * get the socket channel
     * @return socket channel
     */
    public AsynchronousServerSocketChannel getAsynchronousServerSocketChannel() {
        return asynchronousServerSocketChannel;
    }

    /**
     * set the socket channel
     * @param asynchronousServerSocketChannel the socket channel
     */
    public void setAsynchronousServerSocketChannel(AsynchronousServerSocketChannel asynchronousServerSocketChannel) {
        this.asynchronousServerSocketChannel = asynchronousServerSocketChannel;
    }

    /**
     * get the logger
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * set the logger
     * @param logger the logger
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * custom constructor
     * @param asynchronousServerSocketChannel the socket channel
     * @param logger the logger to use
     * @param FileBase the file base
     */
    public ConnectionAttachment(AsynchronousServerSocketChannel asynchronousServerSocketChannel, Logger logger, File FileBase) {
        this.asynchronousServerSocketChannel = asynchronousServerSocketChannel;
        this.logger = logger;
        this.fileBase = FileBase;
    }

    public File getFileBase() {
        return fileBase;
    }

    public void setFileBase(File fileBase) {
        this.fileBase = fileBase;
    }
}
