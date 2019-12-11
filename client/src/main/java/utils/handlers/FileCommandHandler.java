package utils.handlers;

import messages.FileFragmentMessage;
import messages.FileMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * The client class for operating with the command message "upload() a file".
 */
public class FileCommandHandler extends CommandHandler{
    private FileMessage fileMessage;
    private FileFragmentMessage fileFragmentMessage;

    public FileCommandHandler(FileMessage fileMessage) {
        this.fileMessage = fileMessage;
    }

    public FileCommandHandler(FileFragmentMessage fileFragmentMessage) {
        this.fileFragmentMessage = fileFragmentMessage;
    }

    public FileMessage getFileMessage() {
        return fileMessage;
    }

    public FileFragmentMessage getFileFragmentMessage() {
        return fileFragmentMessage;
    }

    //FIXME
//    public void uploadFile(String storageDir) throws IOException {
//        System.out.println("Server.onReceiveObject - fileMessage.getFilename(): " +
//                fileMessage.getFilename() +
//                ". Arrays.toString(fileMessage.getData()): " +
//                Arrays.toString(fileMessage.getData()));
//
//        Files.write(Paths.get(storageDir, fileMessage.getFilename()),
//                fileMessage.getData(), StandardOpenOption.CREATE);
//    }

    /**
     * Метод сохраняет скачанный(полученный) целый файл в заданную директорию клиента
     * @param clientDir - заданная директория(папка) клиента
     * @throws IOException - исключение ввода-вывода
     */
    public void saveDownloadedFile(FileMessage fileMessage, String clientDir) throws IOException {
        System.out.println("(Client)FileCommandHandler.saveDownloadedFile - fileMessage.getFilename(): " +
                fileMessage.getFilename() +
                ". Arrays.toString(fileMessage.getData()): " +
                Arrays.toString(fileMessage.getData()));

        Files.write(Paths.get(clientDir, fileMessage.getFilename()),
                fileMessage.getData(), StandardOpenOption.CREATE);
    }
}