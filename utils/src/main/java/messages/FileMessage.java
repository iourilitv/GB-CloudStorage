package messages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A Class of message objects for a files of a size less than
 * CONST_FRAG_SIZE in the FileFragmentMessage class.
 */
public class FileMessage extends AbstractMessage {
//    private String root;
    private String fromDir;//TODO
    private String toDir;//TODO
    private String filename;
    private byte[] data;

//    public FileMessage(String root, String filename) throws IOException {
//        this.root = root;
//        this.filename = filename;
//        //читаем все данные из файла побайтно в байтовый массив
//        this.data = Files.readAllBytes(Paths.get(root, filename));
//    }
    public FileMessage(String fromDir, String toDir, String filename) throws IOException {
        this.fromDir = fromDir;
        this.toDir = toDir;
        this.filename = filename;

    }

    public void readFileData() throws IOException {
        //читаем все данные из файла побайтно в байтовый массив
        this.data = Files.readAllBytes(Paths.get(fromDir, filename));
    }

    public String getFromDir() {
        return fromDir;
    }

    public String getToDir() {
        return toDir;
    }

    //    public String getRoot() {
//        return root;
//    }

    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }
}
