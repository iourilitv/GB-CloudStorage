package messages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A Class of message objects for a files of a size less than
 * CONST_FRAG_SIZE in the FileFragmentMessage class.
 */
public class FileMessage extends AbstractMessage {
    private String filename;
    private byte[] data;

    public FileMessage(String root, String filename) throws IOException {
        this.filename = filename;
        //читаем все данные из файла побайтно в байтовый массив
        this.data = Files.readAllBytes(Paths.get(root, filename));
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }
}
