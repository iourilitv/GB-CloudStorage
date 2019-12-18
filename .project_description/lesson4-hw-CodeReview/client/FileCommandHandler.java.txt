package utils.handlers;

import control.StorageTest;
import messages.FileMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * The client class for operating with fileMessages and fileFragmentMessages.
 */
public class FileCommandHandler extends AbstractCommandHandler {

    /**
     * Метод сохраняет скачанный из сетевого хранилища(полученный от сервера) целый файл
     * в заданную директорию клиента
     * @param tester - объект тестера
     * @param toDir - заданная директория(папка) клиента
     * @param fileMessage - объект файлового сообщения с данными файла
     * @return true, если файл сохранен без ошибок
     */
    public boolean saveDownloadedFile(StorageTest tester, String toDir, FileMessage fileMessage) {
        try {
            //инициируем объект пути к файлу
            Path path = Paths.get(toDir, fileMessage.getFilename());
            //создаем новый файл и записываем в него данные из объекта файлового сообщения
            Files.write(path, fileMessage.getData(), StandardOpenOption.CREATE);
            //если длина сохраненного файла отличается от длины принятого файла
            if(Files.size(path) != fileMessage.getFileSize()){
                System.out.println("(Client)FileCommandHandler.saveUploadedFile() - Wrong the saved file size!");
                return false;
            }
        } catch (IOException e) {
            tester.printMsg("(Client)FileCommandHandler.saveUploadedFile() - " +
                    "Something wrong with the directory or the file!");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

//        System.out.println("(Client)FileCommandHandler.saveDownloadedFile - fileMessage.getFilename(): " +
//                fileMessage.getFilename() +
//                ". Arrays.toString(fileMessage.getData()): " +
//                Arrays.toString(fileMessage.getData()));


////TODO temporarily
//            System.out.println("(Client)FileCommandHandler.saveDownloadedFile() - savedFileSize: " + Files.size(path) +
//                    ", fileMessage.getFileSize(): " + fileMessage.getFileSize());

