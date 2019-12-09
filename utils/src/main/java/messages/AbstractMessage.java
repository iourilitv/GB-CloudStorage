package messages;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Abstract class for any types of messages such as files, commands etc.
 */
public class AbstractMessage implements Serializable {
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
//        this.data = Files.readAllBytes(Paths.get(root, filename));//TODO
//    }
}
