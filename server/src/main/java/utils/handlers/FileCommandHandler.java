package utils.handlers;

import messages.Commands;
import messages.FileFragmentMessage;
import messages.FileMessage;
import tcp.TCPServer;
import utils.CommandMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * The server class for operating with the command message "upload() a file".
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

//    public void uploadFile(FileMessage fileMessage, String storageDir) throws IOException {
//        System.out.println("Server.onReceiveObject - fileMessage.getFilename(): " +
//                fileMessage.getFilename() +
//                ". Arrays.toString(fileMessage.getData()): " +
//                Arrays.toString(fileMessage.getData()));
//
//        Files.write(Paths.get(storageDir, fileMessage.getFilename()),
//                fileMessage.getData(), StandardOpenOption.CREATE);
//    }

    /**
     * Метод сохраняет полученный целый файл в заданную директорию
     * @param storageDir - заданная директория(папка)
     * @throws IOException - исключение ввода-вывода
     */
    public void saveUploadedFile(FileMessage fileMessage, String storageDir) throws IOException {
        System.out.println("(Server)FileCommandHandler.saveDownloadedFile - fileMessage.getFilename(): " +
                fileMessage.getFilename() +
                ". Arrays.toString(fileMessage.getData()): " +
                Arrays.toString(fileMessage.getData()));

        Files.write(Paths.get(storageDir, fileMessage.getFilename()),
                fileMessage.getData(), StandardOpenOption.CREATE);
    }

    public void downloadFile(TCPServer tcpServer, FileMessage fileMessage, String storageDir) throws IOException {
        System.out.println("(Server)UploadCommandHandler.downloadFile - fileMessage.getFilename(): " +
                fileMessage.getFilename() +
                ". Arrays.toString(fileMessage.getData()): " +
                Arrays.toString(fileMessage.getData()));

        tcpServer.sendToClient("login", new CommandMessage(Commands.REQUEST_SERVER_FILE_UPLOAD,
                new FileCommandHandler(new FileMessage(storageDir, "file1.txt"))));
//        connection.sendMessageObject(new CommandMessage(Commands.REQUEST_SERVER_FILE_UPLOAD,
//                new FileCommandHandler(new FileMessage(storageDir, "file1.txt"))));
    }


}
