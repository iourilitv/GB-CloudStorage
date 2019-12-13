import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileFragment implements Serializable {
    private String filename;
    private byte[] data;

    public FileFragment(String root, String filename) throws IOException {
        this.filename = filename;
//        //создаем новый файл, если его нет и очищаем, если есть
//        new File(root + "/" + filename).createNewFile();//FIXME Не нужно?
        //читаем все данные из файла побайтно в байтовый массив
        this.data = Files.readAllBytes(Paths.get(root, filename));//TODO
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
