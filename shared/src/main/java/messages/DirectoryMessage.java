package messages;

import utils.Item;

/**
 * A Class for a directories objects.
 */
public class DirectoryMessage extends AbstractMessage {
//    //объявляем переменную директории
//    private String directory;
//    //объявляем массив со списком объектов файлов и папок в директории
//    private File[] fileObjectsList;

    //объявляем строку имени пути до директории
    private String directoryPathname;
    //принимаем объект заданной директории
    private Item directoryItem;
    //объявляем массив со списком объектов в директории
    private Item[] itemsList;

    public DirectoryMessage(String directoryPathname) {
        this.directoryPathname = directoryPathname;
    }

    public DirectoryMessage(Item directoryItem, Item[] itemsList) {
        this.directoryItem = directoryItem;
        this.itemsList = itemsList;
    }

    public String getDirectoryPathname() {
        return directoryPathname;
    }

    public Item getDirectoryItem() {
        return directoryItem;
    }

    public Item[] getItemsList() {
        return itemsList;
    }

    //    public DirectoryMessage(String directory) {
//        this.directory = directory;
//    }

//    /**
//     * Метод формирует массив объектов файлов и папок в директории
//     * @param directory - заданная директория
//     */
//    public void takeFileObjectsList(String directory){
//        //собираем массив объектов файлов и папок в заданной директории
//        fileObjectsList = new File(directory).listFiles();
//    }

//    public String getDirectory() {
//        return directory;
//    }

//    public File[] getFileObjectsList() {
//        return fileObjectsList;
//    }
}
