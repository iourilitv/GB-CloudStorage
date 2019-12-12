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
    //принимаем объект файлового сообщения для полного файла
    private FileMessage fileMessage;
    //принимаем объект файлового сообщения для фрагмента файла
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

//    /**
//     * Метод сохраняет полученный от клиента целый файл
//     * в заданную директорию сетевого хранилища(сервера)
//     * @param fromDir - заданная директория(папка) сервера
//     * @param toDir - заданная директория(папка) клиента
//     * @param fileMessage - объект файла, полученного от сервера
//     * @throws IOException - исключение ввода-вывода
//     */
//    public boolean saveUploadedFile(String fromDir, String toDir, FileMessage fileMessage) throws IOException {
//        System.out.println("(Server)FileCommandHandler.saveDownloadedFile - fileMessage.getFilename(): " +
//                fileMessage.getFilename() +
//                ". Arrays.toString(fileMessage.getData()): " +
//                Arrays.toString(fileMessage.getData()));
//
//        Files.write(Paths.get(toDir, fileMessage.getFilename()),
//                fileMessage.getData(), StandardOpenOption.CREATE);
//
//        return true;//FIXME
//    }
    public boolean saveUploadedFile(TCPServer server, String toDir, FileMessage fileMessage) {
        System.out.println("(Server)FileCommandHandler.saveDownloadedFile - fileMessage.getFilename(): " +
                fileMessage.getFilename() +
                ". Arrays.toString(fileMessage.getData()): " +
                Arrays.toString(fileMessage.getData()));
        try {
            //создаем новый файл и записываем в него данные из объекта файлового сообщения
            Files.write(Paths.get(toDir, fileMessage.getFilename()),
                    fileMessage.getData(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            server.printMsg("FileCommandHandler.saveUploadedFile() - Something wrong with the directory or the file!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

//    /**
//     * Метод читает данные из целого файла в заданной директорию сетевого хранилища и
//     * отпраляет клиенту объект сообщения с данными файла.
//     * @param server - объект сервера
//     * @param fromDir - заданная директория(папка) сервера
//     * @param toDir - заданная директория(папка) клиента
//     * @param filename - имя файла
//     * @throws IOException - исключение ввода-вывода
//     */
//    public void downloadAndSendFile(TCPServer server, String fromDir, String toDir, String filename) throws IOException {
//        //FIXME добавить проверку на наличие файла в директории?
//
//        System.out.println("(Server)FileCommandHandler.downloadFile - fileMessage.getFilename(): " +
//                fileMessage.getFilename() +
//                ". Arrays.toString(fileMessage.getData()): " +
//                Arrays.toString(fileMessage.getData()));
//
//        FileMessage fileMessage = new FileMessage(fromDir, toDir, filename);
//        fileMessage.readFileData();//TODO
//
//        server.sendToClient("login1", new CommandMessage(Commands.SERVER_RESPONSE_FILE_DOWNLOAD_OK,
//                fileMessage));
//    }
    public boolean downloadFile(TCPServer server, FileMessage fileMessage) {
        //FIXME добавить проверку на наличие файла в директории?

        System.out.println("(Server)FileCommandHandler.downloadFile - fileMessage.getFilename(): " +
                fileMessage.getFilename() +
                ". Arrays.toString(fileMessage.getData()): " +
                Arrays.toString(fileMessage.getData()));

        try {
            //считываем данные из файла и записываем их в объект файлового сообщения
            fileMessage.readFileData();
        } catch (IOException e) {
            server.printMsg("FileCommandHandler.downloadFile() - Something wrong with the directory or the file!");
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
