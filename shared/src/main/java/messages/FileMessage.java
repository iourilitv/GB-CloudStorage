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
    //объявляем переменную контрольной суммы целого файла
    private String fileChecksum;

    //this constructor is for uploadEntireFile operation
    public FileMessage(Item storageDirectoryItem, Item item, long fileSize) {
        this.storageDirectoryItem = storageDirectoryItem;
        this.item = item;
        this.fileSize = fileSize;
    }

    //this constructor is for downloadEntireFile demanding operation
    public FileMessage(Item storageDirectoryItem, Item clientDirectoryItem, Item item) {
        this.storageDirectoryItem = storageDirectoryItem;
        this.clientDirectoryItem = clientDirectoryItem;
        this.item = item;
    }

    //this constructor is for downloadEntireFile receiving operation
    public FileMessage(Item storageDirectoryItem, Item clientDirectoryItem, Item item, long fileSize) {
        this.storageDirectoryItem = storageDirectoryItem;
        this.clientDirectoryItem = clientDirectoryItem;
        this.item = item;
        this.fileSize = fileSize;
    }

    //для операции переименования
    public FileMessage(Item storageDirectoryItem, Item item, String newName) {
        this.storageDirectoryItem = storageDirectoryItem;
        this.item = item;
        this.newName = newName;
    }

    //для операции удаления
    public FileMessage(Item storageDirectoryItem, Item item) {
        this.storageDirectoryItem = storageDirectoryItem;
        this.item = item;
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

    public String getFileChecksum() {
        return fileChecksum;
    }

    public void setFileChecksum(String fileChecksum) {
        this.fileChecksum = fileChecksum;
    }
}
