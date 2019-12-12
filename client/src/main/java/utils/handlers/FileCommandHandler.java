package utils.handlers;

import messages.FileFragmentMessage;
import messages.FileMessage;
import tcp.TCPClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * The client class for operating with the command message "upload() a file".
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

    /**
     * Метод сохраняет скачанный из сетевого хранилища(полученный от сервера) целый файл
     * в заданную директорию клиента
     * @param client - объект клиента
     * @param toDir - заданная директория(папка) клиента
     * @param fileMessage - объект файлового сообщения с данными файла
     * @return true, если файл сохранен без ошибок
     */
    public boolean saveDownloadedFile(TCPClient client, String toDir, FileMessage fileMessage) {
        System.out.println("(Client)FileCommandHandler.saveDownloadedFile - fileMessage.getFilename(): " +
                fileMessage.getFilename() +
                ". Arrays.toString(fileMessage.getData()): " +
                Arrays.toString(fileMessage.getData()));

        //FIXME добавить проверку директории и наличия файла с таким названием
        try {
            //создаем новый файл и записываем в него данные из объекта файлового сообщения
            Files.write(Paths.get(toDir, fileMessage.getFilename()),
                    fileMessage.getData(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            client.printMsg("FileCommandHandler.saveUploadedFile() - Something wrong with the directory or the file!");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}