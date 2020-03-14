package utils.handlers;

import java.util.Arrays;

/**
 * The client class for operating with directoryMessages.
 */
public class DirectoryCommandHandler extends AbstractCommandHandler {

    /**
     * Метод выводит в GUI список файлов и папок в корневой пользовательской директории
     * в сетевом хранилище.
     * @param directory - заданная пользовательская директория в сетевом хранилище
     * @param fileNamesList - список названий файлов и папок в заданной директории
     */
    public void updateStorageFilesAndFoldersListInGUI(String directory, String[] fileNamesList){
        //FIXME передать в GUI

        //TODO temporarily
        System.out.println("(Client)DirectoryCommandHandler.updateStorageFilesListInGUI directory: " +
                directory +
                ". filesList: " + Arrays.toString(fileNamesList));
    }

}
