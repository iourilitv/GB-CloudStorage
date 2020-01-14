package utils;

import messages.FileFragmentMessage;
import messages.FileMessage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * The common class for operating with fileMessages and fileFragmentMessages.
 */
public class FileUtils {
    //инициируем строковую переменную для вывода сообщений
    private String msg;

    /**
     * Метод читает данные из целого файла в заданной директорию.
     * @param realItemPath - объект реального пути к объекту элемента
     * @param fileMessage - объект файлового сообщения
     * @return - результат чтения данных из файла
     */
    public boolean readFile(Path realItemPath, FileMessage fileMessage) {
        try {
            //считываем данные из файла и записываем их в объект файлового сообщения
            fileMessage.readFileData(realItemPath.toString());
            //записываем размер файла для скачивания
            fileMessage.setFileSize(Files.size(realItemPath));
            //если длина скачанного файла отличается от длины исходного файла в хранилище
            if(fileMessage.getFileSize() != fileMessage.getData().length){
                msg = "FileUtils.downloadFile() - Wrong the read file size!";
                return false;
            }
        } catch (IOException e) {
            msg = "FileUtils.downloadFile() - Something wrong with the directory or the file!";
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Метод сохраняет данные из байтового массива в целый файл.
     * @param realItemPath - объект реального пути к объекту элемента
     * @param data - байтовый массив из источника
     * @param fileSize - размер источника
     * @return - результат сохранения данных из байтового массива в целый файл
     */
    public boolean saveFile(Path realItemPath, byte[] data, long fileSize) {
        try {
            //создаем новый файл и записываем в него данные из объекта файлового сообщения
            Files.write(realItemPath, data, StandardOpenOption.CREATE);
            //если длина сохраненного файла отличается от длины принятого файла
            //проверяем сохраненный файл по контрольной сумме//FIXME
            if(Files.size(realItemPath) != fileSize){
                msg = "FileUtils.saveFile() - Wrong the saved file size!";
                return false;
            }
        } catch (IOException e) {
            msg = "FileUtils.saveFile() - Something wrong with the directory or the file!";
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Метод создает временную директорию, если нет, создает в ней временные файлы-фрагменты,
     * куда сохраняет данные из сообщения фрагмента файла.
     * @param realToTempDirPath - объект пути к временной папке для файлов-фрагментов
     * @param realToFragPath - объект пути к файлу-фрагменту
     * @param fileFragMsg - объект сообщения фрагмента файла
     * @return результат сохранения файла-фрагмента
     */
    public boolean saveFileFragment(Path realToTempDirPath, Path realToFragPath,
                                    FileFragmentMessage fileFragMsg) {
        try {
            //инициируем объект временной директории
            File dir = new File(realToTempDirPath.toString());
            //если временной директории нет
            if(!dir.exists()){
                //создаем временную директорию
                System.out.println("FileUtils.saveFileFragment() - " +
                        "dir." + dir.getPath() +
                        ", dir.mkdir(): " + dir.mkdir());
            }
            //создаем новый файл-фрагмент и записываем в него данные из объекта файлового сообщения
            Files.write(realToFragPath, fileFragMsg.getData(), StandardOpenOption.CREATE);
            //если длина сохраненного файла-фрагмента отличается от длины принятого фрагмента файла
            //проверяем сохраненный файл по контрольной сумме//FIXME добавить
            if(Files.size(realToFragPath) != fileFragMsg.getFileFragmentSize()){
                msg = "FileUtils.saveFileFragment() - " +
                        "Wrong the saved file fragment size!";
                return false;
            }
        } catch (IOException e) {
            msg = "FileUtils.saveFileFragment() - " +
                    "Something wrong with the directory or the file!";
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Метод собирает целый файл из файлов-фрагментов, сохраненных во временной папке,
     * сохраняет его в директорию назначения и удаляет временную папку с файлами-фрагментами
     * @param realToTempDirPath - объект реального пути к временной папка для файлов-фрагментов
     * @param realToFilePath - объект реального пути к итоговому файлу
     * @param fileFragMsg - объект файлового сообщения
     * @return результат процесса сборки целого файла из файлов-фрагментов
     */
    public boolean compileFileFragments(Path realToTempDirPath, Path realToFilePath,
                                        FileFragmentMessage fileFragMsg) {
        //TODO temporarily
        long start = System.currentTimeMillis();

        try {
            //инициируем файловый объект для временной папки
            File tempDirFileObject = new File(realToTempDirPath.toString());
            //инициируем массив файлов-фрагментов во временной папке
            File[] fragFiles = tempDirFileObject.listFiles();
            //если количество файлов-фрагментов не совпадает с требуемым
            assert fragFiles != null;
            if(fragFiles.length != fileFragMsg.getTotalFragsNumber()){
                msg = ("FileUtils.compileFileFragments() - " +
                        "Wrong the saved file fragments count!");
                return false;
            }
            //удаляем файл, если уже существует
            Files.deleteIfExists(realToFilePath);
            //создаем новый файл для сборки загруженных фрагментов файла
            Files.createFile(realToFilePath);
            //в цикле листаем временную папку и добавляем в файл данные из файлов-фрагментов
            for (File fragFile : fragFiles) {
                //ищем требуемый фрагмент во временной папке и инициируем канал для чтения из него
                ReadableByteChannel source = Channels.newChannel(
                        Files.newInputStream(Paths.get(fragFile.getPath())));
                //инициируем канал для записи в файл назначения
                WritableByteChannel destination = Channels.newChannel(
                        Files.newOutputStream(realToFilePath, StandardOpenOption.APPEND));
                //переписываем данные из файла фрагмента в файл-назначения через канал
                copyData(source, destination);

                //TODO temporarily
                System.out.println("FileUtils.compileFileFragments() - " +
                        "fragFiles[i].getName(): " + fragFile.getName() +
                        "FileFragSize: " + Files.size(Paths.get(fragFile.getPath())) +
                        ". Files.size(realToFilePath): " + Files.size(realToFilePath));

                //закрываем потоки и каналы
                source.close();
                destination.close();
            }

            //если длина сохраненного файла-фрагмента отличается от длины принятого фрагмента файла
            if(Files.size(realToFilePath) != fileFragMsg.getFullFileSize()){
                msg = "FileUtils.compileFileFragments() - " +
                        "Wrong a size of the saved entire file!";
                return false;
                //если файл собран без ошибок
            } else {
                //***удаляем временную папку***
                if(!deleteFolder(tempDirFileObject)){
                    msg = "FileUtils.compileFileFragments() - " +
                            "Something wrong with the temp folder deleting!!";
                    return false;
                }
            }
        } catch (IOException e) {
            msg = "FileUtils.compileFileFragments() - " +
                    "Something wrong with the directory or the file!";
            e.printStackTrace();
            return false;
        }

        //TODO temporarily
        long finish = System.currentTimeMillis() - start;
        System.out.println("FileUtils.compileUploadedFileFragments() - duration(mc): " + finish);

        return true;
    }

    /**
     * Метод переписывает данные из канала-источника в канал-назначения с применением буфера
     * @param source - канал чтения данных из источника
     * @param destination - канал записи данных
     * @throws IOException - исключение
     */
    private void copyData(ReadableByteChannel source, WritableByteChannel destination) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        while (source.read(buffer) != -1) {
            // The Buffer Is Used To Be Drained
            buffer.flip();
            // Make Sure That The Buffer Was Fully Drained
            while (buffer.hasRemaining()) {
                destination.write(buffer);
            }
            // Now The Buffer Is Empty!
            buffer.clear();
        }
    }

    /**
     * Метод удаляет файловый объект.
     * @param fileObject - файловый объект
     * @return true - удаление прошло успешно
     */
    public boolean deleteFileObject(File fileObject) {
        boolean result;
        //если это директория
        if(fileObject.isDirectory()){
            //очищаем и удаляем папку
            result = deleteFolder(fileObject);
        } else{
            //удаляем файл
            result = fileObject.delete();
        }
        return result;
    }

    /**
     * Метод удаляет заданную папку и все объекты в ней.
     * @param folder - файловый объект заданной папки
     * @return true - удалена папка и все объекты в ней
     */
    private boolean deleteFolder(File folder) {
        //в цикле листаем временную папку и удаляем все файлы-фрагменты
        for (File f : Objects.requireNonNull(folder.listFiles())) {
            //если это директория
            if(f.isDirectory()){
                //очищаем и удаляем папку
                deleteFolder(f);
            } else{
                //удаляем файл
                System.out.println("FileUtils.deleteFolder() - f.delete(): " + f.delete());
            }
        }
        //теперь можем удалить пустую папку
        return Objects.requireNonNull(folder.listFiles()).length == 0 && folder.delete();
    }

    public String getMsg() {
        return msg;
    }
}
