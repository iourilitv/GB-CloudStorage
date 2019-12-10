package messages;

import java.io.Serializable;

/**
 * Abstract class for any types of messages such as files, commands etc.
 */
public class AbstractMessage implements Serializable {

}

//TODO Delete ta the finish

//    private String filename;
//    private byte[] data;

//    public String getFilename() {
//        return filename;
//    }

//    public byte[] getData() {
//        return data;
//    }

//    public AbstractMessage(String root, String filename) throws IOException {
//        this.filename = filename;
//        this.data = Files.readAllBytes(Paths.get(root, filename));
//    }