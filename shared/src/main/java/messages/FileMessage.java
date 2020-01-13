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
    //принимаем объект родительской директории элемента в клиенте
    private Item clientDirectoryItem;
    //принимаем объект родительской директории элемента в сетевом хранилище
    private Item storageDirectoryItem;
    //принимаем объект элемента
    private Item item;
    //принимаем переменную нового имени файла
    private String newName;

    //для операции удаления
    public FileMessage(Item storageDirectoryItem, Item item) {
        this.storageDirectoryItem = storageDirectoryItem;
        this.item = item;
    }

    //для операции переименования
    public FileMessage(Item storageDirectoryItem, Item item, String newName) {
        this.storageDirectoryItem = storageDirectoryItem;
        this.item = item;
        this.newName = newName;
    }

    //downloadEntireFile
    public FileMessage(String fromDir, String toDir, String filename) {
        this.fromDir = fromDir;
        this.toDir = toDir;
        this.filename = filename;
    }

//    public FileMessage(String fromDir, String toDir, String filename, long fileSize) {
//        this.fromDir = fromDir;
//        this.toDir = toDir;
//        this.filename = filename;
//        this.fileSize = fileSize;
//    }
    //uploadEntireFile
//    public FileMessage(Item clientDirectoryItem, Item storageDirectoryItem, Item item, long fileSize) {
//        this.clientDirectoryItem = clientDirectoryItem;
//        this.storageDirectoryItem = storageDirectoryItem;
//        this.item = item;
//        this.fileSize = fileSize;
//    }
    public FileMessage(Item storageDirectoryItem, Item item, long fileSize) {
        this.storageDirectoryItem = storageDirectoryItem;
        this.item = item;
        this.fileSize = fileSize;
    }

    /**
     * Метод заполняет массив байтами, считанными из файла
     * @param itemPathname - строка имени пути к объекту
     * @throws IOException - исключение ввода вывода
     */
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

    public String getFilename() {
        return filename;
    }

    public Item getClientDirectoryItem() {
        return clientDirectoryItem;
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
