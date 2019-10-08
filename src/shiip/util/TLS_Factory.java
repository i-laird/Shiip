/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 3
 * Class: Data Comm
 *******************************************************/

package shiip.util;

import javax.net.ssl.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.cert.X509Certificate;

/**
 * for using TLS
 * @author Ian Laird
 */
public final class TLS_Factory {

    /**
     * code provided by Dr. Donahoo
     * @param server the server
     * @param portNum the port number
     * @return a socket with TLS
     * @throws Exception if unable to create the Socket
     */
    public static Socket create_TLS_Client(String server, int portNum)
            throws Exception {

        // the following method was provided by Dr. Donahoo
        final SSLContext ctx = SSLContext.getInstance("TLSv1.3");
        ctx.init(null, new TrustManager[] { new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        } }, null);
        final SSLSocketFactory ssf = ctx.getSocketFactory();
        final SSLSocket s = (SSLSocket) ssf.createSocket(server, portNum);
        s.setEnabledCipherSuites(new String[]
                { "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256" });
        final SSLParameters p = s.getSSLParameters();
        p.setApplicationProtocols(new String[] { "h2" });
        s.setSSLParameters(p);
        s.startHandshake();

        return s;
    }

    /**
     * creates server socket with tls
     * @param port the port number
     * @return the server socket with tls
     */
    public static ServerSocket create_TLS_Server(int port){
        return null;
    }
}
