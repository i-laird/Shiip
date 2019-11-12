package shiip.server.donatest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;

import shiip.serialization.BadAttributeException;
import shiip.serialization.Data;
import shiip.serialization.Deframer;
import shiip.serialization.Framer;
import shiip.serialization.Headers;
import shiip.serialization.Message;
import shiip.serialization.Settings;
import tls.TLSFactory;

public class ServerTestClient {
    protected static final Charset ENC = StandardCharsets.US_ASCII;
    protected static final String PREFACE = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n";

    protected String server;
    protected Socket s;
    protected OutputStream out;
    protected Deframer deframer;
    protected Framer framer;
    protected Decoder decoder;
    protected Encoder encoder;
    protected Map<Integer, OutputStream> files = new HashMap<>();

    public static void main(String[] args) throws Exception {
        new ServerTestClient(args).go(args);
    }
    
    public void go(String[] args) throws IOException, BadAttributeException {
        preface();
        postPreface();
        process(args);
        done();
    }
    
    public ServerTestClient(String[] args) throws Exception {
        // Get parameter(s)
        if (args.length < 2) {
            throw new IllegalArgumentException("Parameter(s): <server> <port> <path>...");
        }
        server = args[0];
        int port = Integer.parseInt(args[1]);

        // Set up connection
        s = TLSFactory.getClientSocket(server, port);
        out = s.getOutputStream();
    }

    protected void preface() throws IOException {
        // Send connection preface
        out.write(PREFACE.getBytes(ENC));
    }

    protected void postPreface() throws IOException, BadAttributeException {
        // Set up deframer
        deframer = new Deframer(s.getInputStream());
        framer = new Framer(out);

        // Set up De/Encoder
        decoder = new Decoder(4096, 4096);
        encoder = new Encoder(4096);

        // Send Settings frame
        framer.putFrame(new Settings().encode(null));
    }

    protected void request(String path, int streamID) throws BadAttributeException, IOException {
        // Create request header for default page
        Headers header = new Headers(streamID, false);
        header.addValue(":method", "GET");
        header.addValue(":authority", server);
        header.addValue(":scheme", "https");
        header.addValue(":path", path);
        header.addValue("user-agent", "Mozilla/5.0");

        // Send request
        framer.putFrame(header.encode(encoder));
        // Record streamID but no file output stream
        files.put(streamID, null);
        new File(fileName(streamID)).delete();
    }

    protected void process(String[] args) throws IOException, BadAttributeException {
        // Request files
        int streamID = 1;
        for (int i = 2; i < args.length; i++) {
            request(args[i], streamID);
            streamID += 2;
        }
        // Process messages until all files processed
        while (files.size() > 0) {
            try {
                // Get and print next frame + message
                Message m = Message.decode(deframer.getFrame(), decoder);
                // Handle unknown stream ID
                if (!files.containsKey(m.getStreamID())) {
                    System.err.println("Unexpected stream ID: " + m.getStreamID());
                    // Handle Data
                } else if (m instanceof Data) {
                    handle((Data) m);
                } else if (m instanceof Headers) {
                    handle((Headers) m);
                } else {
                    System.err.println("Received unexpected: " + m);
                }
            } catch (BadAttributeException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    protected void handle(Data d) throws IOException {
        @SuppressWarnings("resource")
        OutputStream out = files.get(d.getStreamID());
        if (out == null) {
            System.err.println("Data without header: " + d.getStreamID());
        } else {
            out.write(d.getData());
            // If Data message has end flag, then we are done
            if (d.isEnd()) {
                files.get(d.getStreamID()).close();
                files.remove(d.getStreamID());
            }
        }
    }

    @SuppressWarnings("resource")
    protected void handle(Headers h) throws IOException {
        if (files.get(h.getStreamID()) != null) {
            System.err.println("Repeated header: " + h.getStreamID());
        } else {
            if (h.getValue(":status").startsWith("200")) {
                files.put(h.getStreamID(), new FileOutputStream(fileName(h.getStreamID())));
            } else {
                System.err.println("Problem with " + h.getStreamID() + ": " + h.getValue(":status"));
                files.remove(h.getStreamID());
            }
        }
    }
    
    protected String fileName(int id) {
        return "file" + id;
    }
    
    protected void done() {
        try {
            s.close();
        } catch (IOException e) {
        }
    }
}
