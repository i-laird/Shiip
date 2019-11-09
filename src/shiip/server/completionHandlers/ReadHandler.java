package shiip.server.completionHandlers;

import shiip.serialization.BadAttributeException;
import shiip.serialization.Message;
import shiip.server.ServerMessageHandler;
import shiip.server.attachment.ReadAttachment;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.util.Objects;

public class ReadHandler implements CompletionHandler<Integer, ReadAttachment> {

    @Override
    public void completed(Integer numRead, ReadAttachment readAttachment) {

        if(numRead == -1){
            //TODO
        }

        // get all of the bytes that were read into the byte buffer
        byte [] readBytes = new byte[numRead];
        readAttachment.getByteBuffer().get(readBytes, 0, numRead);
        readAttachment.getByteBuffer().clear();

        // get deframed bytes
        byte [] deframedBytes = readAttachment.getDeframer().getFrame(readBytes);

        // a frame does not exist yet
        if(Objects.isNull(deframedBytes)){
            readAttachment.getAsynchronousSocketChannel().read(readAttachment.getByteBuffer(), readAttachment, new ReadHandler());
            return;
        }

        Message m = null;
        try {
            m = Message.decode(deframedBytes, readAttachment.getDecoder());
            //TODO the streams
            ServerMessageHandler.handleMessage(false, readAttachment.getLogger(), m, null,
                    readAttachment.getDirectoryBase(), readAttachment.getAsynchronousMessageSender(), readAttachment.getLastEncounteredStreamId());
        }catch(BadAttributeException | IOException e){
            failed(e, readAttachment);
        }

    }

    @Override
    public void failed(Throwable throwable, ReadAttachment readAttachment) {

    }
}
