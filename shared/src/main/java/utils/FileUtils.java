package utils;

import io.netty.channel.ChannelHandlerContext;
import messages.FileFragmentMessage;
import messages.FileMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * The common class is for operations with fileObjects.
 */
public class FileUtils {
    //инициируем синглтон(объект класса)
    private static FileUtils ownInstance = new FileUtils();

    public static FileUtils getInstance() {
        return ownInstance;
    }

    //принимаем объект обработчика операций с объектами элементов списков в GUI
    private final ItemUtils itemUtils = ItemUtils.getInstance();
    //принимаем объект обработчика операций хэширования
    private final HashUtils hashUtils = HashUtils.getInstance();
    //инициируем строковую переменную для вывода сообщений
    private String msg;
    //объявляем объект защелки
    private CountDownLatch countDownLatch;

    /**
     * Метод читает данные из целого файла в заданной директории в объект файлового сообщения.
     * @param realItemPath - объект реального пути к объекту элемента
     * @param fileMessage - объект файлового сообщения
     * @return - результат чтения данных из файла
     */
    public boolean readFile(Path realItemPath, FileMessage fileMessage) {
        try {
            //инициируем локальную переменную контрольной суммы целого файла
            String fileChecksum = hashUtils.hashFile(realItemPath.toFile());
            //сохраняем в объект сообщения контрольной суммы целого файла
            fileMessage.setFileChecksum(fileChecksum);
            //считываем данные из файла и записываем их в объект файлового сообщения
            fileMessage.readFileData(realItemPath.toString());
            //записываем размер файла для скачивания
            fileMessage.setFileSize(Files.size(realItemPath));
        } catch (IOException | NoSuchAlgorithmException e) {
            msg = "FileUtils.readFile() - Something wrong with the directory or the file!";
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Метод отправки по частям большого файла размером более константы максмального размера фрагмента файла.
     * @param toDirItem - объект директории назначения
     * @param item - объект элемента(исходный файл)
     * @param fullFileSize - размер целого файла в байтах
     * @param rootPath - объект пути к корневой папке
     * @param ctx - сетевое соединение
     * @param command - конастанта типа команды
     */
    public void cutAndSendFileByFrags(Item toDirItem, Item item,
                                      long fullFileSize, Path rootPath,
                                      ChannelHandlerContext ctx, Commands command) {
        //TODO Upd 21. Добавить в лог.
        long start = System.currentTimeMillis();
        //запускаем в отдельном процессе, чтобы не тормозить основные процессы(подвисает GUI)
        new Thread(() -> {
            try {
                //***разбиваем файл на фрагменты***
                //рассчитываем количество полных фрагментов файла
                //ATTENTION! Приводимое выражение должно быть в скобках!
                // Иначе сначала fullFileSize(long) приводится к int, что приводит к ошибке при больших чем int значениях.
                int totalEntireFragsNumber = (int) (fullFileSize / FileFragmentMessage.CONST_FRAG_SIZE);
                //рассчитываем размер последнего фрагмента файла
                //ATTENTION! Странно, но здесь (выражение без скобок) ошибки нет, как чуть выше.
                //на всякий случай поставил скобки.
                int finalFileFragmentSize = (int) (fullFileSize - FileFragmentMessage.CONST_FRAG_SIZE * totalEntireFragsNumber);
                //рассчитываем общее количество фрагментов файла
                //если есть последний фрагмент, добавляем 1 к количеству полных фрагментов файла
                int totalFragsNumber = (finalFileFragmentSize == 0) ?
                        totalEntireFragsNumber : totalEntireFragsNumber + 1;

                //TODO Upd 21. Добавить в лог.
                System.out.println("FileUtils.cutAndSendFileByFrags() - fullFileSize: " + fullFileSize);
                System.out.println("FileUtils.cutAndSendFileByFrags() - totalFragsNumber: " + totalFragsNumber);
                System.out.println("FileUtils.cutAndSendFileByFrags() - totalEntireFragsNumber: " + totalEntireFragsNumber);

                //устанавливаем начальные значения номера текущего фрагмента и стартового байта
                long startByte = 0;
                //инициируем байтовый массив для чтения данных для полных фрагментов
                byte[] data = new byte[FileFragmentMessage.CONST_FRAG_SIZE];
                //***в цикле создаем целые фрагменты, читаем в них данные и отправляем***
                for (int i = 1; i <= totalEntireFragsNumber; i++) {
                    //вызываем метод отправки сообщения
                    sendFileFragment(toDirItem, item, fullFileSize,
                            i, totalFragsNumber, FileFragmentMessage.CONST_FRAG_SIZE,
                            data, startByte, rootPath, ctx, command);
                    //инициируем защелку и ждем получения подтверждения получателя
                    countDownLatch = new CountDownLatch(1);
                    countDownLatch.await();
                    //увеличиваем указатель стартового байта на размер фрагмента
                    startByte += FileFragmentMessage.CONST_FRAG_SIZE;
                }

                //TODO Upd 21. Добавить в лог.
                System.out.println("FileUtils.cutAndSendFileByFrags() - currentFragNumber: " + totalFragsNumber);
                System.out.println("FileUtils.cutAndSendFileByFrags() - finalFileFragmentSize: " + finalFileFragmentSize);

                //***отправляем последний фрагмент, если он есть***
                if(totalFragsNumber > totalEntireFragsNumber){
                    //инициируем байтовый массив для чтения данных для последнего фрагмента
                    byte[] dataFinal = new byte[finalFileFragmentSize];
                    //вызываем метод отправки сообщения
                    sendFileFragment(toDirItem, item, fullFileSize,
                            totalFragsNumber, totalFragsNumber, finalFileFragmentSize,
                            dataFinal, startByte, rootPath, ctx, command);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        //TODO Upd 21. Добавить в лог.
        long finish = System.currentTimeMillis() - start;
        System.out.println("FileUtils.cutAndSendFileByFrags() - duration(mc): " + finish);
    }

    /**
     * Метод отправки объекта сообщения с объектом фрагментом файла.
     * @param toDirItem - объект директории назначения
     * @param item - объект элемента(исходный файл)
     * @param fullFileSize - размер целого файла в байтах
     * @param fragNumber - номер фрагмента
     * @param totalFragsNumber - общее количество фрагментов
     * @param fileFragSize - размер фрагмента в байтах
     * @param data - байтовый массив с данными фрагмента файла
     * @param startByte - индекс начального байта фрагмента в целом файле
     * @param rootPath - объект пути к корневой папке
     * @param ctx - сетевое соединение
     * @param command - конастанта типа команды
     */
    public void sendFileFragment(Item toDirItem, Item item, long fullFileSize,
                                 int fragNumber, int totalFragsNumber, int fileFragSize,
                                 byte[] data, long startByte, Path rootPath,
                                 ChannelHandlerContext ctx, Commands command){
        try {
            //инициируем объект фрагмента файлового сообщения
            FileFragmentMessage fileFragmentMessage = new FileFragmentMessage(
                    toDirItem, item, fullFileSize, fragNumber,
                    totalFragsNumber, fileFragSize, data);
            //читаем данные во фрагмент с определенного места файла
            fileFragmentMessage.readFileDataToFragment(
                    itemUtils.getRealPath(item.getItemPathname(), rootPath).toString(),
                    startByte);
            //вычисляем и сохраняем в объект сообщения контрольную сумму
            // байтового массива фрагмента файла
            fileFragmentMessage.setFragChecksum(
                    hashUtils.hashBytes(fileFragmentMessage.getData()));
            //отправляем на сервер объект сообщения(команды)
            ctx.writeAndFlush(new CommandMessage(command, fileFragmentMessage));
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод сохраняет данные из полученного байтового массива в целый файл.
     * @param fileMessage - объект файлового сообщения
     * @param realItemPath - объект реального пути к объекту элемента
     * @return - результат сохранения данных из байтового массива в файл
     */
    public boolean saveFile(FileMessage fileMessage, Path realItemPath) {
        try {
            //создаем новый файл и записываем в него данные из объекта файлового сообщения
            Files.write(realItemPath, fileMessage.getData(), StandardOpenOption.CREATE);
            //если длина сохраненного файла отличается от длины принятого файла
            if(Files.size(realItemPath) != fileMessage.getFileSize()){
                msg = "FileUtils.saveFile() - Wrong the saved file size!";
                return false;
            //если контрольная сумма сохраненного файла отличается от исходной контрольной суммы
            } else if(!fileMessage.getFileChecksum().
                    equals(hashUtils.hashFile(realItemPath.toFile()))){
                msg = "FileUtils.saveFile() - " +
                        "Wrong checksum of the saved file!";
                return false;
            }
        } catch (IOException | NoSuchAlgorithmException e) {
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
            //если текущий фрагмент первый
            if(fileFragMsg.getCurrentFragNumber() == 1){
                //инициируем объект временной директории
                File dir = new File(realToTempDirPath.toString());
                //если временная директория уже существует(возможно не пустая)
                if(dir.exists()){
                    //то предварительно удаляем
                    deleteFolder(dir);
                }
                //и создаем новую временную директорию
                msg = "FileUtils.saveFileFragment() - " +
                        "dir." + dir.getPath() +
                        ", dir.mkdir(): " + dir.mkdir();
            }
            //создаем новый файл-фрагмент и записываем в него данные из объекта файлового сообщения
            Files.write(realToFragPath, fileFragMsg.getData(), StandardOpenOption.CREATE);
            //если длина сохраненного файла-фрагмента отличается от длины принятого фрагмента файла
            if(Files.size(realToFragPath) != fileFragMsg.getFileFragmentSize()){
                msg = "FileUtils.saveFileFragment() - " +
                        "Wrong the saved file fragment size!";
                return false;
            //если контрольная сумма сохраненного файла-фрагмента отличается от исходной контрольной суммы
            } else if(!fileFragMsg.getFragChecksum().
                    equals(hashUtils.hashFile(realToFragPath.toFile()))){
                msg = "FileUtils.saveFileFragment() - " +
                        "Wrong checksum of the saved file fragment #" +
                        fileFragMsg.getCurrentFragNumber() + "!";
                return false;
            }
        } catch (IOException | NoSuchAlgorithmException e) {
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
        //TODO Upd 21. Добавить в лог.
        long start = System.currentTimeMillis();

        try {
            //инициируем файловый объект для временной папки
            File tempDirFileObject = new File(realToTempDirPath.toString());
            //инициируем массив файлов-фрагментов во временной папке
            File[] fragFiles = tempDirFileObject.listFiles();
            //если количество файлов-фрагментов не совпадает с требуемым
            if(fragFiles == null ||
                    fragFiles.length != fileFragMsg.getTotalFragsNumber()){
                msg = ("FileUtils.compileFileFragments() - " +
                        "Wrong the saved file fragments count!");
                return false;
            }
            //переписываем данные из канала-источника в канал-назначения данные
            // из файлов-фрагментов в итоговый файл
            transferDataFromFragsToFinalFile(realToFilePath, fragFiles);
            //если длина сохраненного файла отличается от длины полного исходного файла
            if(Files.size(realToFilePath) != fileFragMsg.getFullFileSize()){
                msg = "FileUtils.compileFileFragments() - " +
                        "Wrong size of the saved entire file!";
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

        //TODO Upd 21. Добавить в лог.
        long finish = System.currentTimeMillis() - start;
        System.out.println("FileUtils.compileUploadedFileFragments() - duration(mc): " + finish);

        return true;
    }

    /**
     * Метод переписывает данные из канала-источника в канал-назначения данные
     * из файлов-фрагментов в итоговый файл.
     * @param realToFilePath - объект пеального пути к итоговому файлу
     * @param fragFiles - массив файлов-фрагментов
     * @throws IOException - исключение
     */
    private void transferDataFromFragsToFinalFile(Path realToFilePath,
                                                  File[] fragFiles) throws IOException {
        //удаляем файл, если уже существует
        Files.deleteIfExists(realToFilePath);
        //создаем новый файл для сборки загруженных фрагментов файла
        File finalFile = new File(realToFilePath.toString());
        //инициируем выходной поток и канал для записи данных в итоговый файл
        RandomAccessFile toFileRAF = new RandomAccessFile(finalFile, "rw");
        FileChannel toChannel = toFileRAF.getChannel();
        //в цикле листаем временную папку и добавляем в файл данные из файлов-фрагментов
        for (File fragFile : fragFiles) {
            //инициируем входной поток и канал для чтения данных из файла-фрагмента
            FileInputStream fromFileInStream = new FileInputStream(fragFile);
            FileChannel fromChannel = fromFileInStream.getChannel();
            //переписываем данные через каналы
            fromChannel.transferTo(0, fragFile.length(), toChannel);
            //закрываем входные потоки и каналы
            fromFileInStream.close();
            fromChannel.close();
        }
        //закрываем выходные потоки и каналы
        toFileRAF.close();
        toChannel.close();
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
        //если папка недоступна, выходим с false
        if(folder.listFiles() == null) {
            System.out.println("FileUtils.deleteFolder() - " +
                    "This folder is system or not accessible!");
            return false;
        }
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

    /**
     * Метод создает файловый объект новой папки.
     * @param realDirPathname - строка пути к новой папке
     * @return - результат создания файлового объекта новой папки
     */
    public boolean createNewFolder(String realDirPathname) {
        //инициируем новый файловый объект
        File dir = new File(realDirPathname);
        //если такая папке уже существует
        if(dir.exists()){
            //выходим с false
            System.out.println("CloudStorageServer.createNewFolder() - A folder with this name exists.");
            return false;
        }
        //возвращаем результат создания новой папки
        return dir.mkdir();
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public String getMsg() {
        return msg;
    }

}
