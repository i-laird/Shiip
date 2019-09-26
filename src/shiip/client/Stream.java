package shiip.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a Stream to a Shiip Server
 *
 * @author Laird
 */
public class Stream {
    private int streamId = 0;
    List<byte []> contents = new LinkedList<>();
    boolean isComplete = false;
    private String path;

    /**
     * adds the Data payload to this stream
     * @param b
     */
    public void addBytes(byte [] b){
        this.contents.add(b);
    }

    /**
     * writes all contents of the Stream to the file
     * The filename is determined by the path that was sent to the server.
     * @throws IOException if unable to write to the file
     */
    public void writeToFile() throws IOException {

        //replace all '/' with a '-'
        String modifiedPath = path.replace("/", "-");
        Path p = Paths.get(modifiedPath);
        Files.write(p, getContents());
    }

    /**
     * constructor for a Stream
     * @param streamId the id of the stream
     * @param path the path that the data is fetched from in the Server
     */
    public Stream(int streamId, String path) {
        this.streamId = streamId;
        this.path = path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    private byte [] getContents(){
        //merge all of the little arrays into one big one
        int size = contents.stream().mapToInt(x -> x.length).sum();
        byte [] toReturn = new byte [size];
        int loc = 0;
        for(byte [] b : contents){
            for(int i = 0; i < b.length; i++){
                toReturn[loc] = b[i];
                loc += 1;
            }
        }
        return toReturn;
    }

    public int getStreamId() {
        return streamId;
    }
}
