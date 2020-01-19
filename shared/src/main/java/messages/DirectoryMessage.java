package messages;

import utils.Item;

/**
 * A Class for a directories objects.
 */
public class DirectoryMessage extends AbstractMessage {
    //объявляем строку имени пути до директории
    private String directoryPathname;
    //объявляем строку нового имени элемента
    private String newDirName;
    //принимаем объект заданной директории
    private Item directoryItem;
    //объявляем массив со списком объектов в директории
    private Item[] itemsList;

    public DirectoryMessage(String directoryPathname) {
        this.directoryPathname = directoryPathname;
    }

    public DirectoryMessage(String directoryPathname, String newDirName) {
        this.directoryPathname = directoryPathname;
        this.newDirName = newDirName;
    }

    public DirectoryMessage(Item directoryItem, Item[] itemsList) {
        this.directoryItem = directoryItem;
        this.itemsList = itemsList;
    }

    public String getDirectoryPathname() {
        return directoryPathname;
    }

    public String getNewDirName() {
        return newDirName;
    }

    public Item getDirectoryItem() {
        return directoryItem;
    }

    public Item[] getItemsList() {
        return itemsList;
    }

}
