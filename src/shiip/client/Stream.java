package shiip.client;

import java.util.LinkedList;
import java.util.List;

public class Stream {
    private int streamId;
    List<byte []> contents = new LinkedList<>();
    boolean isComplete = false;
    private String path;

    public void addBytes(byte [] b){

    }

    public byte [] getBytes(){

    }

    public Stream(int streamId, String path) {
        this.streamId = streamId;
        this.path = path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
