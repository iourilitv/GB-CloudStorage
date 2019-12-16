package utils.handlers;

import messages.FileMessage;
import tcp.TCPServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * The server class for operating with fileMessages and fileFragmentMessages.
 */
public class FileCommandHandler extends AbstractCommandHandler {

    /**
     * Метод сохраняет полученный от клиента целый файл
     * в заданную директорию сетевого хранилища(сервера)
     * @param server - объект сервера
     * @param toDir - заданная директория(папка) клиента в сетевом хранилище
     * @param fileMessage - объект файлового сообщения с данными файла
     * @return true, если файл сохранен без ошибок
     */
    public boolean saveUploadedFile(TCPServer server, String toDir, FileMessage fileMessage) {
        try {
            //инициируем объект пути к файлу
            Path path = Paths.get(toDir, fileMessage.getFilename());
            //создаем новый файл и записываем в него данные из объекта файлового сообщения
            Files.write(path, fileMessage.getData(), StandardOpenOption.CREATE);
            //если длина сохраненного файла отличается от длины принятого файла
            if(Files.size(path) != fileMessage.getFileSize()){
                server.printMsg("(Server)FileCommandHandler.saveUploadedFile() - Wrong the saved file size!");
                return false;
            }
        } catch (IOException e) {
            server.printMsg("(Server)FileCommandHandler.saveUploadedFile() - Something wrong with the directory or the file!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Метод читает данные из целого файла в заданной директорию сетевого хранилища и
     * отправляет клиенту объект сообщения с данными файла.
     * @param server - объект сервера
     * @param fileMessage - объект файлового сообщения с данными файла
     * @return true, если файл скачан без ошибок
     */
    public boolean downloadFile(TCPServer server, String fromDir, FileMessage fileMessage) {
        try {
            //считываем данные из файла и записываем их в объект файлового сообщения
            fileMessage.readFileData(fromDir);

            //инициируем объект пути к файлу
            Path path = Paths.get(fromDir, fileMessage.getFilename());
            //записываем размер файла для скачивания
            fileMessage.setFileSize(Files.size(path));
            //если длина скачанного файла отличается от длины исходного файла в хранилище
            if(fileMessage.getFileSize() != fileMessage.getData().length){
                server.printMsg("(Server)FileCommandHandler.downloadFile() - Wrong the read file size!");
                return false;
            }
        } catch (IOException e) {
            server.printMsg("(Server)FileCommandHandler.downloadFile() - Something wrong with the directory or the file!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

}

//        System.out.println("(Server)FileCommandHandler.saveDownloadedFile - fileMessage.getFilename(): " +
//                fileMessage.getFilename() +
//                ". Arrays.toString(fileMessage.getData()): " +
//                Arrays.toString(fileMessage.getData()));


//        System.out.println("(Server)FileCommandHandler.downloadFile - fileMessage.getFilename(): " +
//                fileMessage.getFilename() +
//                ". Arrays.toString(fileMessage.getData()): " +
//                Arrays.toString(fileMessage.getData()));


//            //TODO temporarily
//            System.out.println("savedFileSize: " + Files.size(path) +
//                    ", fileMessage.getFileSize(): " + fileMessage.getFileSize());