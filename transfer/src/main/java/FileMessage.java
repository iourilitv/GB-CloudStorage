import java.io.File;

/**
 * A Class for file objects.
 */
public class FileMessage extends AbstractMessage {
    //объявляем переменную файла
    File file;
    //принимаем переменную имени файла
    String fileName;

    public FileMessage(String fileName) {
        this.fileName = fileName;
        this.file = new File(fileName);
    }
}
