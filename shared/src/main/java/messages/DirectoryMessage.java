package messages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * A Class for a directories objects.
 */
public class DirectoryMessage extends AbstractMessage {
    //объявляем переменную директории
    private String directory;
    //объявляем строчный массив со списком файлов в директории
//    private String[] filesList;
    //объявляем массив со списком объектов файлов в директории
    private File[] filesList;

    public DirectoryMessage() {
        this.directory = "";
    }

    public DirectoryMessage(String directory) {
        this.directory = directory;
    }

//    public void composeFilesList(String directory){
//        try {
//            //собираем в строковый массив названия файлов в заданной директории
////            filesList = (File[]) Files.list(Paths.get(directory)).toArray();
//
////            File directoryObject = new File(directory);
////            filesList = directoryObject.listFiles();
////
//            filesList = new File(directory).listFiles();
//
//            //TODO temporarily
//            System.out.println("DirectoryMessage.composeFilesList() - filesList: " +
//                    Arrays.toString(filesList));
//        } catch (IOException e) {
//
//            //FIXME что здесь?
//            System.out.println("DirectoryMessage.composeFilesList()" + e);
////            e.printStackTrace();
//        }
//    }
    public void composeFilesList(String directory){
        //собираем в строковый массив названия файлов в заданной директории
        filesList = new File(directory).listFiles();
        //TODO temporarily
        System.out.println("DirectoryMessage.composeFilesList() - filesList: " +
                Arrays.toString(filesList));

    }

    public String getDirectory() {
        return directory;
    }

    public File[] getFilesList() {
        return filesList;
    }
}
