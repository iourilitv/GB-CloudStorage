package utils.handlers;

import messages.DirectoryMessage;

import java.io.File;
import java.util.Arrays;

/**
 * The client class for operating with directoryMessages.
 */
public class DirectoryCommandHandler extends CommandHandler{
    //принимаем объект сообщения о директории
    private DirectoryMessage directoryMessage;

    public DirectoryCommandHandler(DirectoryMessage directoryMessage) {
        this.directoryMessage = directoryMessage;
    }

    /**
     * Метод выводит в GUI список файлов и папок в корневой пользовательской директории
     * в сетевом хранилище.
     * @param directory - заданная пользовательская директория в сетевом хранилище
     * @param filesList - список файлов и папок в заданной директории
     */
    public void updateStorageFilesListInGUI(String directory, File[] filesList){
        //FIXME передать в GUI

        //TODO temporarily
        System.out.println("(Client)DirectoryCommandHandler.updateStorageFilesListInGUI directory: " +
                directory +
                ". filesList: " + Arrays.toString(filesList));
    }

}
