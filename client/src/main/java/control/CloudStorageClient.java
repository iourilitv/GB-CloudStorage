package control;

import io.netty.channel.ChannelHandlerContext;
import messages.AuthMessage;
import messages.FileFragmentMessage;
import messages.FileMessage;
import netty.NettyClient;
import utils.CommandMessage;
import utils.Commands;
import utils.FileUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

/**
 * This client's class is responded for operation with storage by communication with command handlers.
 */
public class CloudStorageClient {
    //принимаем объект соединения
    ChannelHandlerContext ctx;
    //инициируем константу IP адреса сервера(здесь - адрес моего ноута в домашней локальной сети)
    private static final String IP_ADDR = "192.168.1.102";//89.222.249.131(внешний белый адрес)
    //инициируем константу порта соединения
    private static final int PORT = 8189;
    //инициируем переменную для печати сообщений в консоль
    private final PrintStream log = System.out;

    //инициируем переменную для директории, заданной относительно userStorageRoot в сетевом хранилище
    private String storageDir = "";
    //инициируем строку названия директории облачного хранилища(сервера) для хранения файлов клиента
    private final String clientDefaultRoot = "storage/client_storage";
    //инициируем переменную для директории, заданной относительно clientRoot
    private String clientDir = "";
    //объявляем переменную для текущей директории клиента
    private String currentClientDir;

    //объявляем объект файлового обработчика
    private FileUtils fileUtils;

    //FIXME temporarily - будет получать из GUI
    //инициируем константы логина и пароля пользователя
    private final String login = "login1";
    private final String password = "pass1";

    public CloudStorageClient() {

    }

    public void run() throws Exception {
        //инициируем объект файлового обработчика
        fileUtils = new FileUtils();//TODO здесь ли инициализировать или в CommandMessageManager
        //инициируем объект соединения
        new NettyClient(this, IP_ADDR, PORT).run();
    }

    //FIXME удалить, когда будет реализован интерфейс
    public void startTest(ChannelHandlerContext ctx) throws IOException {
        //принимаем переменную четевого подключения
        this.ctx = ctx;
        //инициируем переменную для текущей директории клиента
        currentClientDir = clientDefaultRoot;
        //отправляем на сервер запрос на авторизацию в облачное хранилище
        requestAuthorization(ctx, login, password);
        //добавляем к корневой директории пользователя в сетевом хранилище
        // имя подпапки назначения
        storageDir = storageDir.concat("folderToUploadFile");
        //инициируем переменную для текущей директории клиента
        currentClientDir = clientDefaultRoot;
        //отправляем на сервер запрос на загрузку маленького файла в облачное хранилище
        uploadFile(currentClientDir, storageDir, "toUpload.txt");//TODO for test
        //отправляем на сервер запрос на загрузку большого файла в облачное хранилище
        uploadFile(currentClientDir, storageDir, "toUploadBIG.mp4");//TODO for test
//        uploadFile(currentClientDir, storageDir, "toUploadMedium.png");//TODO for test

        //восстанавливаем начальное значение директории в сетевом хранилище//TODO temporarily
        storageDir = "";
        //добавляем к корневой директории клиента имя подпапки назначения на клиенте
        clientDir = clientDir.concat("folderToDownloadFile");
        //отправляем на сервер запрос на скачивание маленького файла из облачного хранилища
        downloadFile(storageDir, clientDir, "toDownload.png");//TODO for test
        //отправляем на сервер запрос на скачивание большого файла из облачного хранилища
        downloadFile(storageDir, clientDir, "toDownloadBIG.mp4");//TODO for test
    }

    //отправляем на сервер запрос на авторизацию в облачное хранилище
    private void requestAuthorization(ChannelHandlerContext ctx, String login, String password) {
        //TODO temporarily
        printMsg("***CloudStorageClient.requestAuthorization() - has started***");

        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_AUTH,
                new AuthMessage(login, password)));

        //TODO temporarily
        printMsg("***CloudStorageClient.requestAuthorization() - has finished***");
    }

    //отправляем на сервер запрос на загрузку файла в облачное хранилище
    //FIXME перенести в контроллер интерфейса
    public void uploadFile(String fromDir, String toDir, String filename) throws IOException {
        //TODO temporarily
        printMsg("***CloudStorageClient.uploadFile() - has started***");

        //вычисляем размер файла
        long fileSize = Files.size(Paths.get(fromDir, filename));
        //если размер файла больше константы размера фрагмента
        if(fileSize > FileFragmentMessage.CONST_FRAG_SIZE){
            //запускаем метод отправки файла по частям
            uploadFileByFrags(fromDir, toDir, filename, fileSize);
        //если файл меньше
        } else {
            //запускаем метод отправки целого файла
            uploadEntireFile(fromDir, toDir, filename, fileSize);
        }

        //TODO temporarily
        printMsg("***CloudStorageClient.uploadFile() - has finished***");
    }

    /**
     * Метод отправки по частям большого файла размером более константы максмального размера фрагмента файла
     * @param fromDir - директория(относительно корня) клиента где хранится файл источник
     * @param toDir - директория(относительно корня) в сетевом хранилище
     * @param filename - строковое имя файла
     * @param fullFileSize - размер целого файла в байтах
     * @throws IOException - исключение
     */
    private void uploadFileByFrags(String fromDir, String toDir, String filename, long fullFileSize) throws IOException {
        //TODO temporarily
        long start = System.currentTimeMillis();

        //***разбиваем файл на фрагменты***
        //рассчитываем количество полных фрагментов файла
        int totalEntireFragsNumber = (int) fullFileSize / FileFragmentMessage.CONST_FRAG_SIZE;
        //рассчитываем размер последнего фрагмента файла
        int finalFileFragmentSize = (int) fullFileSize - FileFragmentMessage.CONST_FRAG_SIZE * totalEntireFragsNumber;
        //рассчитываем общее количество фрагментов файла
        //если есть последний фрагмент, добавляем 1 к количеству полных фрагментов файла
        int totalFragsNumber = (finalFileFragmentSize == 0) ?
                totalEntireFragsNumber : totalEntireFragsNumber + 1;

        //TODO temporarily
        System.out.println("CloudStorageClient.uploadFileByFrags() - fullFileSize: " + fullFileSize);
        System.out.println("CloudStorageClient.uploadFileByFrags() - totalFragsNumber: " + totalFragsNumber);
        System.out.println("CloudStorageClient.uploadFileByFrags() - totalEntireFragsNumber: " + totalEntireFragsNumber);

        //устанавливаем началные значения номера текущего фрагмента и стартового байта
        long startByte = 0;
        //инициируем байтовый массив для чтения данных для полных фрагментов
        byte[] data = new byte[FileFragmentMessage.CONST_FRAG_SIZE];
        //инициируем массив имен фрагментов файла
        String[] fragsNames = new String[totalFragsNumber];
        //***в цикле создаем целые фрагменты, читаем в них данные и отправляем***
        for (int i = 1; i <= totalEntireFragsNumber; i++) {
            //инициируем объект фрагмента файлового сообщения
            FileFragmentMessage fileFragmentMessage =
                    new FileFragmentMessage(fromDir, toDir, filename, fullFileSize,
                            i, totalFragsNumber, FileFragmentMessage.CONST_FRAG_SIZE, fragsNames, data);
            //читаем данные во фрагмент с определенного места файла
            fileFragmentMessage.readFileDataToFragment(fromDir, filename, startByte);
            //увеличиваем указатель стартового байта на размер фрагмента
            startByte += FileFragmentMessage.CONST_FRAG_SIZE;

            //отправляем на сервер объект сообщения(команды)
            ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_FILE_FRAG_UPLOAD,
                    fileFragmentMessage));

        }

        //TODO temporarily
        System.out.println("CloudStorageClient.uploadFileByFrags() - currentFragNumber: " + totalFragsNumber);
        System.out.println("CloudStorageClient.uploadFileByFrags() - finalFileFragmentSize: " + finalFileFragmentSize);

        //***отправляем последний фрагмент, если он есть***
        if(totalFragsNumber > totalEntireFragsNumber){
            //инициируем байтовый массив для чтения данных для последнего фрагмента
            byte[] dataFinal = new byte[finalFileFragmentSize];
            //инициируем объект фрагмента файлового сообщения
            FileFragmentMessage fileFragmentMessage =
                    new FileFragmentMessage(fromDir, toDir, filename, fullFileSize,
                            totalFragsNumber, totalFragsNumber, finalFileFragmentSize, fragsNames, dataFinal);
            //читаем данные во фрагмент с определенного места файла
            fileFragmentMessage.readFileDataToFragment(fromDir, filename, startByte);

            //отправляем на сервер объект сообщения(команды)
            ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_FILE_FRAG_UPLOAD,
                    fileFragmentMessage));
        }

        //TODO temporarily
        long finish = System.currentTimeMillis() - start;
        System.out.println("CloudStorageClient.uploadFileByFrags() - duration(mc): " + finish);
    }

    /**
     * Метод отправки целого файла размером менее константы максмального размера фрагмента файла
     * @param fromDir - директория(относительно корня) клиента где хранится файл источник
     * @param toDir - директория(относительно корня) в сетевом хранилище
     * @param filename - строковое имя файла
     * @param fileSize - размер файла в байтах
     */
    private void uploadEntireFile(String fromDir, String toDir, String filename, long fileSize) {
        //инициируем объект файлового сообщения
        FileMessage fileMessage = new FileMessage(fromDir, toDir, filename, fileSize);

        //TODO temporarily
        System.out.println("CloudStorageClient.uploadEntireFile() - fileUtils: " + fileUtils +
                ", currentClientDir: " + currentClientDir +
                ", fileMessage: " + fileMessage);

        //читаем файл и записываем данные в байтовый массив объекта файлового сообщения
        //FIXME Разобраться с абсолютными папкими клиента
        //если скачивание прошло удачно
        if(fileUtils.readFile(currentClientDir, fileMessage)){

            //отправляем на сервер объект сообщения(команды)
            ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_FILE_UPLOAD,
                    fileMessage));

            //если что-то пошло не так
        } else {
            //выводим сообщение
            printMsg("[client]" + fileUtils.getMsg());
        }
    }

    //отправляем на сервер запрос на скачивание файла из облачного хранилища
    //FIXME перенести в контроллер интерфейса
    public void downloadFile(String fromDir, String toDir, String filename){
        //TODO temporarily
        printMsg("***CloudStorageClient.downloadFile() - has started***");

        //инициируем объект файлового сообщения
        FileMessage fileMessage = new FileMessage(fromDir, toDir, filename);

        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_FILE_DOWNLOAD,
                fileMessage));

        //TODO temporarily
        printMsg("***CloudStorageClient.downloadFile() - has finished***");
    }

    public String getClientDefaultRoot() {
        return clientDefaultRoot;
    }

    public FileUtils getFileUtils() {
        return fileUtils;
    }

    public void printMsg(String msg){
        log.append(msg).append("\n");
    }

}
