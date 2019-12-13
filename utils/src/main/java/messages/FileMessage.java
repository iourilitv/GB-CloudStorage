package messages;

import java.io.File;
import java.io.IOException;

/**
 * A Class for file objects.
 */
public class FileMessage extends AbstractMessage {
    //объявляем переменную файла
    File file;
    //принимаем переменную имени файла
    String fileName;

    public FileMessage(String root, String filename) throws IOException {//TODO
        super(root, filename);
    }

//    public FileMessage(String fileName) {
//        this.fileName = fileName;
//        this.file = new File(fileName);
//    }
}
