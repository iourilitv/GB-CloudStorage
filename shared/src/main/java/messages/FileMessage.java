package messages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A Class of message objects for a files of a size less than
 * CONST_FRAG_SIZE in the FileFragmentMessage class.
 */
public class FileMessage extends AbstractMessage {
    //объявляем переменную директории источника
    private String fromDir;
    //объявляем переменную директории назначения
    private String toDir;
    //объявляем переменную имени файла
    private String filename;
    //объявляем переменную размера файла(в байтах)
    private long fileSize;
    //объявляем байтовый массив с данными из файла
    private byte[] data;

    public FileMessage(String fromDir, String toDir, String filename) {
        this.fromDir = fromDir;
        this.toDir = toDir;
        this.filename = filename;
    }

    public FileMessage(String fromDir, String toDir, String filename, long fileSize) {
        this.fromDir = fromDir;
        this.toDir = toDir;
        this.filename = filename;
        this.fileSize = fileSize;
    }

    /**
     * Метод читает все данные из файла в байтовый массив
     * @throws IOException - исключение ввода вывода
     */
    public void readFileData() throws IOException {
        //читаем все данные из файла побайтно в байтовый массив
        this.data = Files.readAllBytes(Paths.get(fromDir, filename));
    }

    //TODO
    public void readFileData(String fromDir) throws IOException {
        //читаем все данные из файла побайтно в байтовый массив
        this.data = Files.readAllBytes(Paths.get(fromDir, filename));
    }

    public String getFromDir() {
        return fromDir;
    }

    public String getToDir() {
        return toDir;
    }

    public String getFilename() {
        return filename;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getData() {
        return data;
    }
}
