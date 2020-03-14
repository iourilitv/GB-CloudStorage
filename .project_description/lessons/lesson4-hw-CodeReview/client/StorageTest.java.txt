package control;

import messages.AuthMessage;
import messages.FileFragmentMessage;
import messages.FileMessage;
import tcp.TCPClient;
import tcp.TCPConnection;
import utils.CommandMessage;
import utils.Commands;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

/**
 * The class is responded for operation with storage by communication with command handlers.
 */
public class StorageTest {

    public StorageTest() {
        //инициируем объект сетевого соединения с сервером
        TCPClient tcpClient = new TCPClient(this);
        //принимаем объект соединения
        connection = tcpClient.getConnection();
        //запускаем тестирование
        startTest(connection);
    }

    //TODO temporarily
    //объявляем объект защелки
    private CountDownLatch countDownLatch;

    //инициируем переменную для директории, заданной относительно userStorageRoot в сетевом хранилище
    private String storageDir = "";
    //инициируем строку названия директории облачного хранилища(сервера) для хранения файлов клиента
    private final String clientDefaultRoot = "storage/client_storage";
    //инициируем переменную для директории, заданной относительно clientRoot
    private String clientDir = "";
    //объявляем переменную для текущей директории клиента
    private String currentClientDir;
    //инициируем переменную для печати сообщений в консоль
    private final PrintStream log = System.out;
    //объявляем переменную сетевого соединения
    private TCPConnection connection;

    //FIXME удалить, когда будет реализован интерфейс
    public void startTest(TCPConnection connection) {
        //инициируем переменную для текущей директории клиента
        currentClientDir = clientDefaultRoot;
        //инициируем объект защелки на один сброс
        countDownLatch = new CountDownLatch(1);
        //отправляем на сервер запрос на авторизацию в облачное хранилище
        requestAuthorization("login1", "pass1");

        try {
            //ждем сброса защелки
            countDownLatch.await();
            //добавляем к корневой директории пользователя в сетевом хранилище
            // имя подпапки назначения
            storageDir = storageDir.concat("folderToUploadFile");
            //инициируем переменную для текущей директории клиента
            currentClientDir = clientDefaultRoot;
            //отправляем на сервер запрос на загрузку маленького файла в облачное хранилище
//            uploadFile(currentClientDir, storageDir, "toUpload.txt");//TODO for test
            //отправляем на сервер запрос на загрузку большого файла в облачное хранилище
            uploadFile(currentClientDir, storageDir, "toUploadBIG.mp4");//TODO for test
//            uploadFile(currentClientDir, storageDir, "toUploadMedium.png");//TODO for test

            //инициируем объект защелки на один сброс
            countDownLatch = new CountDownLatch(1);
            //ждем сброса защелки
            countDownLatch.await();
            //восстанавливаем начальное значение директории в сетевом хранилище//TODO temporarily
            storageDir = "";
            //добавляем к корневой директории клиента имя подпапки назначения на клиенте
            clientDir = clientDir.concat("folderToDownloadFile");
            //отправляем на сервер запрос на скачивание файла из облачного хранилища
            downloadFile(storageDir, clientDir, "toDownload.png");
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    //отправляем на сервер запрос на авторизацию в облачное хранилище
    private void requestAuthorization(String login, String password) {
        //TODO temporarily
        printMsg("***TCPClient.requestAuthorization() - has started***");

        //отправляем на сервер объект сообщения(команды)
        connection.sendMessageObject(new CommandMessage(Commands.REQUEST_SERVER_AUTH,
                new AuthMessage(login, password)));

        //TODO temporarily
        printMsg("***TCPClient.requestAuthorization() - has finished***");
    }

    //отправляем на сервер запрос на загрузку файла в облачное хранилище
    //FIXME перенести в контроллер интерфейса
    public void uploadFile(String fromDir, String toDir, String filename) throws IOException {
        //TODO temporarily
        printMsg("***TCPClient.uploadFile() - has started***");

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
        printMsg("***TCPClient.uploadFile() - has finished***");
    }

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
        System.out.println("StorageTest.uploadFileByFrags() - fullFileSize: " + fullFileSize);
        System.out.println("StorageTest.uploadFileByFrags() - totalFragsNumber: " + totalFragsNumber);
        System.out.println("StorageTest.uploadFileByFrags() - totalEntireFragsNumber: " + totalEntireFragsNumber);

        //устанавливаем началные значения номера текущего фрагмента и стартового байта
        int currentFragNumber = 1;
        long startByte = 0;
        //***в цикле создаем целые фрагменты, читаем в них данные и отправляем***
        while(currentFragNumber <= totalEntireFragsNumber){
            //инициируем объект фрагмента файлового сообщения
            FileFragmentMessage fileFragmentMessage =
                    new FileFragmentMessage(fromDir, toDir, filename, fullFileSize,
                            currentFragNumber++, totalFragsNumber, FileFragmentMessage.CONST_FRAG_SIZE);

            //читаем данные во фрагмент с определенного места файла
            fileFragmentMessage.readFileDataToFragment(fromDir, filename, startByte, FileFragmentMessage.CONST_FRAG_SIZE);
            //увеличиваем указатель стартового байта на размер фрагмента
            startByte += FileFragmentMessage.CONST_FRAG_SIZE;

            //отправляем на сервер объект сообщения(команды)
            connection.sendMessageObject(new CommandMessage(Commands.REQUEST_SERVER_FILE_FRAG_UPLOAD,
                    fileFragmentMessage));
        }

        //TODO temporarily
        System.out.println("StorageTest.uploadFileByFrags() - currentFragNumber: " + currentFragNumber);
        System.out.println("StorageTest.uploadFileByFrags() - finalFileFragmentSize: " + finalFileFragmentSize);

        //***отправляем последний фрагмент, если он есть***
        if(totalFragsNumber > totalEntireFragsNumber){
            //инициируем объект фрагмента файлового сообщения
            FileFragmentMessage fileFragmentMessage =
                    new FileFragmentMessage(fromDir, toDir, filename, fullFileSize,
                            currentFragNumber, totalFragsNumber, finalFileFragmentSize);
            //читаем данные во фрагмент с определенного места файла
            fileFragmentMessage.readFileDataToFragment(fromDir, filename, startByte, finalFileFragmentSize);
            //отправляем на сервер объект сообщения(команды)
            connection.sendMessageObject(new CommandMessage(Commands.REQUEST_SERVER_FILE_FRAG_UPLOAD,
                    fileFragmentMessage));
        }

        //TODO temporarily
        long finish = System.currentTimeMillis() - start;
        System.out.println("StorageTest.uploadFileByFrags() - duration(mc): " + finish);
    }

    /**
     * Метод отправки целого файла размером менее константы максмальго размера фрагмента файла
     * @param fromDir - директория(относительно корня) клиента где хранится файл источник
     * @param toDir - директория(относительно корня) в сетевом хранилище
     * @param filename - строковое имя файла
     * @param fileSize - размер файла в байтах
     */
    private void uploadEntireFile(String fromDir, String toDir, String filename, long fileSize) {
        try {
            //инициируем объект файлового сообщения
            FileMessage fileMessage = new FileMessage(fromDir, toDir, filename, fileSize);
            //читаем файл и записываем данные в байтовый массив объекта файлового сообщения
            fileMessage.readFileData(currentClientDir);//FIXME Разобраться с абсолютными папкими клиента

            //если длина считанного файла отличается от длины исходного файла в хранилище
            if(fileMessage.getFileSize() != fileMessage.getData().length){
                printMsg("(Client)StorageTest.uploadFile() - Wrong the read file size!");
                return;
            }

            //отправляем на сервер объект сообщения(команды)
            connection.sendMessageObject(new CommandMessage(Commands.REQUEST_SERVER_FILE_UPLOAD,
                    fileMessage));
        } catch (IOException e) {
            //печатаем в консоль сообщение об ошибке считывания файла
            printMsg("TCPClient.uploadFile() - There is no file in the directory!");
            e.printStackTrace();
        }
    }

    //отправляем на сервер запрос на скачивание файла из облачного хранилища
    //FIXME перенести в контроллер интерфейса
    public void downloadFile(String fromDir, String toDir, String filename){
        //TODO temporarily
        printMsg("***TCPClient.downloadFile() - has started***");

        //инициируем объект файлового сообщения
        FileMessage fileMessage = new FileMessage(fromDir, toDir, filename);
        //отправляем на сервер объект сообщения(команды)
        connection.sendMessageObject(new CommandMessage(Commands.REQUEST_SERVER_FILE_DOWNLOAD,
                fileMessage));

        //TODO temporarily
        printMsg("***TCPClient.downloadFile() - has finished***");
    }

    public TCPConnection getConnection() {
        return connection;
    }

    public String getClientDefaultRoot() {
        return clientDefaultRoot;
    }

    //TODO temporarily
    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public synchronized void printMsg(String msg){
        log.append(msg).append("\n");
    }

}
