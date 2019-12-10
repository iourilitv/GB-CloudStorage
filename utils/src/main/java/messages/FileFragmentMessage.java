package messages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileFragmentMessage extends AbstractMessage {
    //инициируем константу размера фрагментов файла в байтах
    private static final int CONST_FRAG_SIZE = 1024 * 1024;
    private String filename;
    private byte[] data;
    //объявляем переменную текущего фрагмента файла
    private int currentFragNumber;
    //объявляем переменную общего количества фрагментов файла
    private int totalFragsNumber;

    public FileFragmentMessage(String root, String filename) throws IOException {
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

    @Override
    public String toString() {
        return "FileFragment{" +
                "filename='" + filename + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
