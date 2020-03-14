package messages;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;

/**
 * A Class of message objects for a files of a size more than
 * CONST_FRAG_SIZE in the FileFragmentMessage class.
 */
public class FileFragmentMessage extends AbstractMessage {
    //инициируем константу размера фрагментов файла в байтах
    public static final int CONST_FRAG_SIZE = 1024 * 1024 * 10;
    //объявляем переменную директории источника
    private String fromDir;
    //объявляем переменную директории назначения
    private String toDir;
    //объявляем переменную имени файла(полного)
    private String filename;
    //объявляем переменную размера файла(в байтах)
    private long fullFileSize;
    //объявляем переменную имени фрагмента файла
    private String fileFragmentName;
    //объявляем переменную размера фрагмента файла(в байтах)
    private int fileFragmentSize;
    //объявляем байтовый массив с данными из файла
    private byte[] data;

    //объявляем переменную текущего фрагмента файла
    private int currentFragNumber;
    //объявляем переменную общего количества фрагментов файла
    private int totalFragsNumber;
    //объявляем переменную имени временной папки для хранения фрагментов в директории назначения
    private String toTempDir;

    public FileFragmentMessage(
            String fromDir, String toDir, String filename, long fullFileSize,
            int currentFragNumber, int totalFragsNumber, int fileFragmentSize) {
        this.fromDir = fromDir;
        this.toDir = toDir;
        this.filename = filename;
        this.fullFileSize = fullFileSize;
        this.currentFragNumber = currentFragNumber;
        this.totalFragsNumber = totalFragsNumber;
        if(fileFragmentSize == -1){
            //записываем значение размера фрагмента по константе
            this.fileFragmentSize = CONST_FRAG_SIZE;
        } else{
            this.fileFragmentSize = fileFragmentSize;
        }
        //составляем имя фрагмента файла(для сохранения в директории)
        fileFragmentName = filename;

        fileFragmentName = fileFragmentName.concat(".frg")
                .concat(String.valueOf(currentFragNumber))
                .concat("-").concat(String.valueOf(totalFragsNumber));
        //Пример: toUpload.txt.frg1-1024 ... toUpload.txt.frg1024-1024

        //составляем имя временной папки в директории назначения для хранения фрагментов файла
        toTempDir = toDir;
        toTempDir = toTempDir.concat("/").concat(filename)
                .concat("-temp-").concat(String.valueOf(fullFileSize));
        //Пример: .../toUpload.txt-temp-102425820//FIXME допускаются ли точки в имени папки?
    }

    /**
     * Метод чтения данных из определенного места файла в файтовый массив.
     * @param fromDir - директория файла источника
     * @param filename - имя файла источника
     * @param startByte - индекс байта начала считывания
     * @param fileFragmentSize - размер фрагмента файла
     * @throws IOException - исключение ввода-вывода
     */
    public void readFileDataToFragment(String fromDir, String filename, long startByte, int fileFragmentSize) throws IOException {
        //собираем полное название файла(с директорией)
        String path = fromDir;
        path = path.concat("/").concat(filename);
        // открываем файл для чтения
        RandomAccessFile raf = new RandomAccessFile(path, "r");
        //инициируем объект входного буферезированного потока с преобразованием raf в поток
        BufferedInputStream bis = new BufferedInputStream(Channels.newInputStream(raf.getChannel()));
        // ставим указатель на нужный вам символ
        raf.seek(startByte);
        //инициируем байтовый массив
        data = new byte[fileFragmentSize];
        //вычитываем данные из файла
        //считывает байты данных длиной до b.length из этого файла в массив байтов.
        bis.read(data);
        raf.close();
        bis.close();
    }

    public String getToDir() {
        return toDir;
    }

    public String getFilename() {
        return filename;
    }

    public long getFullFileSize() {
        return fullFileSize;
    }

    public byte[] getData() {
        return data;
    }

    public String getToTempDir() {
        return toTempDir;
    }

    public String getFileFragmentName() {
        return fileFragmentName;
    }

    public int getFileFragmentSize() {
        return fileFragmentSize;
    }

    public boolean isFinalFileFragment(){
        return currentFragNumber == totalFragsNumber;
    }

    public int getTotalFragsNumber() {
        return totalFragsNumber;
    }
}