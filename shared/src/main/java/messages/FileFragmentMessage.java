package messages;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * A Class of message objects for a files of a size more than
 * CONST_FRAG_SIZE in the FileFragmentMessage class.
 */
public class FileFragmentMessage extends AbstractMessage {
    //инициируем константу размера фрагментов файла в байтах
    public static final int CONST_FRAG_SIZE = 1024 * 1024;
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
    private Byte[] dataObj;

    //объявляем переменную текущего фрагмента файла
    private int currentFragNumber;
    //объявляем переменную общего количества фрагментов файла
    private int totalFragsNumber;
    //объявляем переменную имени временной папки для хранения фрагментов в директории назначения
    private String toTempDir;

    //конструктор для фрагментов(полных) с первого по предпоследнего включительно
    public FileFragmentMessage(
            String fromDir, String toDir, String filename, long fullFileSize,
                               int currentFragNumber, int totalFragsNumber) {
        //инициируем переменные объекта
        init(fromDir, toDir, filename, fullFileSize,
        currentFragNumber, totalFragsNumber, -1);
    }

    //конструктор для последнего(не полного) фрагмента
//    public FileFragmentMessage(String filename, int fileFragmentSize, int currentFragNumber, int totalFragsNumber) {
//        this.filename = filename;
//        this.fileFragmentSize = fileFragmentSize;
//        this.currentFragNumber = currentFragNumber;
//        this.totalFragsNumber = totalFragsNumber;
//        //составляем имя фрагмента файла(для сохранения в директории)
//        fileFragmentName = filename;
//        fileFragmentName = fileFragmentName.concat(".frg")
//                .concat(String.valueOf(currentFragNumber))
//                .concat("-").concat(String.valueOf(totalFragsNumber));
//        //Пример: toUpload.txt.frg1024-1024
//    }
    public FileFragmentMessage(
            String fromDir, String toDir, String filename, long fullFileSize,
            int currentFragNumber, int totalFragsNumber, int fileFragmentSize) {
        //инициируем переменные объекта
        init(fromDir, toDir, filename, fullFileSize,
                currentFragNumber, totalFragsNumber, fileFragmentSize);
    }

    private void init(String fromDir, String toDir, String filename, long fullFileSize,
                      int currentFragNumber, int totalFragsNumber, int fileFragmentSize){
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

    //читаем данные файла из определенного места
//    public void readFileDataToFragmentData(String fromDir, String filename, long startByte) throws IOException {
//        //читаем все данные из файла побайтно в байтовый массив
//        this.data = Files.readAllBytes(Paths.get(fromDir, filename));
//        //инициируем объект
//        RandomAccessFile raf = new RandomAccessFile(filename, "r");
//    }
    public void readFileDataToFragmentData(String fromDir, String filename, long startByte) throws IOException {
        //собираем полное название файла(с директорией)
        String path = fromDir;
        path = path.concat("/").concat(filename);
        // открываем файл для чтения
        RandomAccessFile raf = new RandomAccessFile(path, "r");
        // ставим указатель на нужный вам символ
        raf.seek(startByte);
        byte b;
        int count = 0;
        ArrayList<Byte> arrayList = new ArrayList<>(CONST_FRAG_SIZE);
        // ***побитово читаем и добавляем символы в строку***
        //пока есть что читать и количество считанного не превысило константу размера фрагментов файла в байтах
        while(count++ < CONST_FRAG_SIZE && (int) (b = (byte) raf.read()) != -1){
            //добавляем считанный байт в коллекцию
            arrayList.add(b);
        }
        //переписываем данные из коллекции в байтовый массив
        dataObj = (Byte[])arrayList.toArray();

        //TODO temporarily
        System.out.println("FileFragmentMessage.readFileDataToFragmentData() - data.length: " + data.length +
                ", Arrays.toString(data): " + Arrays.toString(data));

        raf.close();
    }

    //читаем данные из сохраненного фрагмента во временной папке
    public void readFileFragmentData(String fromTempDir, String fileFragmentName) throws IOException {
        //читаем все данные из файла побайтно в байтовый массив
        this.data = Files.readAllBytes(Paths.get(fromTempDir, fileFragmentName));
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }

}
