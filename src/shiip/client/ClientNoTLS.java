/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.client;

import static shiip.client.Client.TLS_NOT_ENABLED;
import static shiip.client.Client.calledByMain;

/**
 * Creates a Client Without TLS
 * @author Ian Laird
 */
public class ClientNoTLS {

    /**
     * main method
     * @param args the command line params of the client without tls enabled
     */
    public static void main(String[] args) {
        calledByMain(args, TLS_NOT_ENABLED);
    }
}
