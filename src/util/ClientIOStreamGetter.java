/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 3
 * Class: Data Comm
 *******************************************************/

package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static util.ErrorCodes.ERROR_SOCKET_GET_IO;

/**
 * catches exception encountered when getting io streams
 * @author Ian Laird
 */
public final class ClientIOStreamGetter {

    /**
     * gete the {@link InputStream} for a {@link Socket} and
     * does exception handling
     * @param s the socket
     * @return the input stream of the Socket
     */
    public static InputStream getSocketInputStream(Socket s){
        try {
            return s.getInputStream();
        }catch(IOException e){
            System.err.println("Unable to get input stream for socket");
            System.exit(ERROR_SOCKET_GET_IO);
        }

        //unreachable statement
        return null;
    }

    /**
     * gets the {@link OutputStream} for a {@link Socket} and
     * does exception handling
     * @param s the socket
     * @return the output stream
     */
    public static OutputStream getSocketOutputStream(Socket s){
        try {
            return s.getOutputStream();
        }catch(IOException e){
            System.err.println("Unable to get input stream for socket");
            System.exit(ERROR_SOCKET_GET_IO);
        }

        //unreachable statement
        return null;
    }
}
