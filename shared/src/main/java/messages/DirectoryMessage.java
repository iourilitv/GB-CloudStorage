package messages;

import java.io.File;
import java.util.Arrays;

/**
 * A Class for a directories objects.
 */
public class DirectoryMessage extends AbstractMessage {
    //объявляем переменную директории
    private String directory;
    //объявляем строчный массив со списком файлов в директории
    private String[] fileNamesList;

    public DirectoryMessage() {
        this.directory = "";
    }

    public DirectoryMessage(String directory) {
        this.directory = directory;
    }

    /**
     * Метод формирует строчный массив со списком файлов в директории
     * @param directory - заданная директория
     */
    public void composeFileNamesList(String directory){
        //собираем в строковый массив названия файлов в заданной директории
        fileNamesList = new File(directory).list();
    }

    public String getDirectory() {
        return directory;
    }

    public String[] getFileNamesList() {
        return fileNamesList;
    }
}
