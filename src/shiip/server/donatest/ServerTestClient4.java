package shiip.server.donatest;

import java.io.IOException;

import shiip.serialization.BadAttributeException;
import shiip.serialization.Data;

public class ServerTestClient4 extends ServerTestClient {
    public ServerTestClient4(String[] args) throws Exception {
        super(args);
    }

    @Override
    protected void process(String[] args) throws IOException, BadAttributeException {
        framer.putFrame(new Data(1, false, new byte[] {1}).encode(encoder));
        super.process(args);
    }

    public static void main(String[] args) {
        try {
            new ServerTestClient4(args).go(args);
        } catch (Exception e) {
            System.out.println("Socket closed: " + e.getMessage());
        }
    }
}

