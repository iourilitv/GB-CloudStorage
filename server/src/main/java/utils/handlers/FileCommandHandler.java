package utils.handlers;

import messages.FileFragmentMessage;
import messages.FileMessage;
import tcp.TCPServer;

import java.io.File;
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

    /**
     * Метод создает временную директорию, если нет, создает в ней временные файлы-фрагменты,
     * куда сохраняет данные из сообщения фрагмента файла.
     * @param server - объект сервера
     * @param toTempDir - временная папка для файлов-фрагментов
     * @param fileFragmentMessage - объект сообщения фрагмента файла
     * @return true, если файл-фрагмент сохранен без ошибок
     */
    public boolean saveUploadedFileFragment(TCPServer server, String toTempDir, FileFragmentMessage fileFragmentMessage) {
        try {
            //инициируем объект пути к фрагменту файла
            Path path = Paths.get(toTempDir, fileFragmentMessage.getFileFragmentName());
            //инициируем объект временной директории
            File dir = new File(toTempDir);//TODO возможно можно упростить?
            //если временной директории нет
            if(!dir.exists()){
                //создаем временную директорию
                dir.mkdir();
            }
            //создаем новый файл-фрагмент и записываем в него данные из объекта файлового сообщения
            Files.write(path, fileFragmentMessage.getData(), StandardOpenOption.CREATE);

            System.out.println("(Server)FileCommandHandler.saveUploadedFile() - " +
                    "Files.size(path): " + Files.size(path) +
                    ". fileFragmentMessage.getFileFragmentSize(): " +
                    fileFragmentMessage.getFileFragmentSize());

            //если длина сохраненного файла-фрагмента отличается от длины принятого фрагмента файла
            if(Files.size(path) != fileFragmentMessage.getFileFragmentSize()){
                server.printMsg("(Server)FileCommandHandler.saveUploadedFileFragment() - Wrong the saved file fragment size!");
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
     * Метод собирает целый файл из файлов-фрагментов, сохраненных во временной папке,
     * сохраняет его в директорию назначения и удаляет временную папку с файлами-фрагментами
     * @param server - объект сервера
     * @param toTempDir - временная папка для файлов-фрагментов
     * @param toDir - заданная директория для загрузки целого файла
     * @param fileFragmentMessage - объект сообщения фрагмента файла
     * @return true, если целый файл собран и сохранен без ошибок
     */
    public boolean compileUploadedFileFragments(
            TCPServer server, String toTempDir, String toDir,
            FileFragmentMessage fileFragmentMessage
    ) {
        try {
            //инициируем объект пути к временной папке с фрагментами файла
            Path pathToFile = Paths.get(toDir, fileFragmentMessage.getFilename());
            //создаем новый файл для сборки загруженных фрагментов файла
            Files.createFile(pathToFile);
            //инициируем массив названий файлов-фрагментов(сами уже отсортированны во временной папке)
            String[] fragsNames = new File(toTempDir).list();
            //добавлено по требованию IDEA
            assert fragsNames != null;
            //если количество файлов-фрагментов не совпадает с требуемым
            if(fragsNames.length != fileFragmentMessage.getTotalFragsNumber()){
                server.printMsg("(Server)FileCommandHandler.compileUploadedFileFragments() - " +
                        "Wrong the saved file fragments count!");
                return false;
            }
            //читаем последовательно список фрагментов и записываем данные из них в файл
            for (String fragName : fragsNames) {
                //записываем в файл данные из фрагмента
                Files.write(pathToFile, Files.readAllBytes(Paths.get(toTempDir, fragName)),
                        StandardOpenOption.APPEND);
            }
            //если длина сохраненного файла-фрагмента отличается от длины принятого фрагмента файла
            if(Files.size(pathToFile) != fileFragmentMessage.getFullFileSize()){
                server.printMsg("(Server)FileCommandHandler.compileUploadedFileFragments() - " +
                        "Wrong the saved entire file size!");
                return false;
            //если файл собран без ошибок
            } else {
                //***удаляем временную папку***
                //в цикле листаем впеменную папку и удаляем все файлы-фрагменты
                for (String fragName : fragsNames) {
                    //удаляем файл-фрагмент
                    Files.delete(Paths.get(toTempDir, fragName));
                }
                //теперь можем удалить пустую папку
                Files.delete(Paths.get(toTempDir));
            }
        } catch (IOException e) {
            server.printMsg("(Server)FileCommandHandler.compileUploadedFileFragments() - " +
                    "Something wrong with the directory or the file!");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}