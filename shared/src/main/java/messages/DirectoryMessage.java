package messages;

import java.io.File;

/**
 * A Class for a directories objects.
 */
public class DirectoryMessage extends AbstractMessage {
    //объявляем переменную директории
    private String directory;

    //объявляем строчный массив со списком файлов и папок в директории
    private String[] namesList;
    //объявляем массив со списком объектов файлов и папок в директории
    private File[] fileObjectsList;

    public DirectoryMessage() {
        this.directory = "";
    }

    public DirectoryMessage(String directory) {
        this.directory = directory;
    }

//    /**
//     * Метод формирует строчный массив со списком файлов и папок в директории
//     * @param directory - заданная директория
//     */
//    public void composeFilesAndFoldersNamesList(String directory){
//        //собираем в строковый массив названия файлов и папок в заданной директории
//        namesList = new File(directory).list();
//    }

    public void takeFileObjectsList(String directory){
        //собираем массив объектов файлов и папок в заданной директории
        fileObjectsList = new File(directory).listFiles();
    }

    public String getDirectory() {
        return directory;
    }

//    public String[] getNamesList() {
//        return namesList;
//    }

    public File[] getFileObjectsList() {
        return fileObjectsList;
    }
}
