package shiip.server;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;
import shiip.serialization.*;
import shiip.tls.TLSFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SimpleServer {
    private static final Charset ENC = StandardCharsets.US_ASCII;

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Parameter(s): <port>");
        }
        int port = Integer.parseInt(args[0]);
        ServerSocket ss = TLSFactory.getServerListeningSocket(port, "mykeystore", "secret");
        Socket s = TLSFactory.getServerConnectedSocket(ss);

        InputStream in = s.getInputStream();
        byte [] preface = new byte[24];
       in.readNBytes(preface, 0, 24);
        System.out.println("Recvd: " + new String(preface, ENC));

        OutputStream out = s.getOutputStream();
        Deframer deframer = new Deframer(in);
        Framer framer = new Framer(out);

        // Set up De/Encoder
        Decoder decoder = new Decoder(4096, 4096);
        Encoder encoder = new Encoder(4096);

        // Receive and print messages until get Headers
        Message msg;
        do {
            msg = Message.decode(deframer.getFrame(), decoder);
            System.out.println("Recvd: " + msg);
        } while (!(msg instanceof Headers));

        // Send fake Data
        Headers h = new Headers(msg.getStreamID(), false);
        h.addValue(":status", "200");
        framer.putFrame(h.encode(encoder));
        framer.putFrame(new Data(msg.getStreamID(), true, "This is the text".getBytes(ENC)).encode(encoder));

        in.close();
        out.close();
        s.close();
        ss.close();
    }
}
