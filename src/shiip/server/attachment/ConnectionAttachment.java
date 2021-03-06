/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.attachment;

import java.io.File;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
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

    // the async socket channel
    private AsynchronousSocketChannel asynchronousSocketChannel;

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

    /**
     * gets the file base
     * @return the file base
     */
    public File getFileBase() {
        return fileBase;
    }

    /**
     * sets the file base
     * @param fileBase file base
     */
    public void setFileBase(File fileBase) {
        this.fileBase = fileBase;
    }

    /**
     * gets the async socket channel
     * @return get
     */
    public AsynchronousSocketChannel getAsynchronousSocketChannel() {
        return asynchronousSocketChannel;
    }

    /**
     * sets the async socket channel
     * @param asynchronousSocketChannel the async socket channel
     */
    public void setAsynchronousSocketChannel(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }
}
