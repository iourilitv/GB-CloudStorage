package messages;

import utils.Item;

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
//    //объявляем переменную заданной директории
//    private String directory;
//    //объявляем переменную имени файлового объекта
//    private String fileObjectName;
    //принимаем объект родительской директории
    private Item storageDirectoryItem;
    //принимаем объект элемента
    private Item item;
    //принимаем переменную нового имени файла
    private String newName;

    //для операций переименования, удаления
//    public FileMessage(String directory, String fileObjectName) {
//        this.directory = directory;
//        this.fileObjectName = fileObjectName;
//    }

    //для операции удаления
//    public FileMessage(Item item) {
//        this.item = item;
//    }
    public FileMessage(Item storageDirectoryItem, Item item) {
        this.storageDirectoryItem = storageDirectoryItem;
        this.item = item;
    }

    //для операции переименования
//    public FileMessage(Item item, String newName) {
//        this.item = item;
//        this.newName = newName;
//    }
    public FileMessage(Item storageDirectoryItem, Item item, String newName) {
        this.storageDirectoryItem = storageDirectoryItem;
        this.item = item;
        this.newName = newName;
    }

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

//    /**
//     * Метод читает все данные из файла в байтовый массив
//     * @throws IOException - исключение ввода вывода
//     */
//    public void readFileData() throws IOException {
//        //читаем все данные из файла побайтно в байтовый массив
//        this.data = Files.readAllBytes(Paths.get(fromDir, filename));
//    }

    //TODO
//    public void readFileData(String fromDir) throws IOException {
//        //читаем все данные из файла побайтно в байтовый массив
//        this.data = Files.readAllBytes(Paths.get(fromDir, filename));
//    }
    public void readFileData(String itemPathname) throws IOException {
        //читаем все данные из файла побайтно в байтовый массив
        this.data = Files.readAllBytes(Paths.get(itemPathname));
    }

    public String getFromDir() {
        return fromDir;
    }

    public String getToDir() {
        return toDir;
    }

//    public String getDirectory() {
//        return directory;
//    }

//    public String getFileObjectName() {
//        return fileObjectName;
//    }

    public String getFilename() {
        return filename;
    }

    public Item getStorageDirectoryItem() {
        return storageDirectoryItem;
    }

    public Item getItem() {
        return item;
    }

    public String getNewName() {
        return newName;
    }

//    public void setNewName(String newName) {
//        this.newName = newName;
//    }

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
