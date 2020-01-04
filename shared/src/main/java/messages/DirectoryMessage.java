package messages;

import java.io.File;

/**
 * A Class for a directories objects.
 */
public class DirectoryMessage extends AbstractMessage {
    //объявляем переменную директории
    private String directory;
    //объявляем массив со списком объектов файлов и папок в директории
    private File[] fileObjectsList;

//    public DirectoryMessage() {
//        this.directory = "";
//    }

    public DirectoryMessage(String directory) {
        this.directory = directory;
    }

    /**
     * Метод формирует массив объектов файлов и папок в директории
     * @param directory - заданная директория
     */
    public void takeFileObjectsList(String directory){
        //собираем массив объектов файлов и папок в заданной директории
        fileObjectsList = new File(directory).listFiles();
    }

    public String getDirectory() {
        return directory;
    }

    public File[] getFileObjectsList() {
        return fileObjectsList;
    }
}
