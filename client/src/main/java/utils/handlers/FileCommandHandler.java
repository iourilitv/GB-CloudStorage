package utils.handlers;

import control.StorageTest;
import messages.FileFragmentMessage;
import messages.FileMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * The client class for operating with fileMessages and fileFragmentMessages.
 */
public class FileCommandHandler extends CommandHandler{

    /**
     * Метод сохраняет скачанный из сетевого хранилища(полученный от сервера) целый файл
     * в заданную директорию клиента
     * @param tester - объект тестера
     * @param toDir - заданная директория(папка) клиента
     * @param fileMessage - объект файлового сообщения с данными файла
     * @return true, если файл сохранен без ошибок
     */
    public boolean saveDownloadedFile(StorageTest tester, String toDir, FileMessage fileMessage) {

        //FIXME добавить проверку директории и наличия файла с таким названием
        try {
            //создаем новый файл и записываем в него данные из объекта файлового сообщения
            Files.write(Paths.get(toDir, fileMessage.getFilename()),
                    fileMessage.getData(), StandardOpenOption.CREATE);
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
