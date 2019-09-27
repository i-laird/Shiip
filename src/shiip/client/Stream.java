/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 2
 * Class: Data Comm
 *******************************************************/

package shiip.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Stream to a Shiip Server
 *
 * @author Laird
 */
public class Stream {

    // the stream id of this stream
    private int streamId = 0;

    // all of the data payloads associates with this stream
    List<byte []> contents = new LinkedList<>();

    // if a data frame with end of stream has been received
    boolean isComplete = false;

    // the path that this was retrieved from
    private String path;

    /**
     * adds the Data payload to this stream
     * @param b the bytes to add to the stream
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

    /**
     * Sets the path of this stream
     * @param path the path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Sets that this stream is done
     * @param complete true for if complete
     */
    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    /**
     * Gets all of the contents of this stream
     * @return contents
     */
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

    /**
     * returns the stream id
     * @return stream id
     */
    public int getStreamId() {
        return streamId;
    }

    /**
     * sees if two Streams are equal
     * @param o the other stream
     * @return true means they are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stream stream = (Stream) o;
        return streamId == stream.streamId &&
                isComplete == stream.isComplete &&
                Objects.equals(contents, stream.contents) &&
                Objects.equals(path, stream.path);
    }

    /**
     * hashes a stream
     * @return the hashcode for a stream
     */
    @Override
    public int hashCode() {

        return Objects.hash(streamId, contents, isComplete, path);
    }
}
