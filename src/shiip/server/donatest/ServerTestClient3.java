package shiip.server.donatest;

import java.io.IOException;

public class ServerTestClient3 extends ServerTestClient {
    public ServerTestClient3(String[] args) throws Exception {
        super(args);
    }

    @Override
    protected void preface() throws IOException {
        // Send bad connection preface
        out.write("PRE * HTTP/2.0\r\n\r\nSM\r\n\r\n".getBytes(ENC));
    }

    public static void main(String[] args) {
        try {
            new ServerTestClient3(args).go(args);
        } catch (Exception e) {
            System.out.println("Socket closed: " + e.getMessage());
        }
    }
}
