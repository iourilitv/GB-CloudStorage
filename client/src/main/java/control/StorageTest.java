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
            //отправляем на сервер запрос на загрузку файла в облачное хранилище
            uploadFile(clientDir, storageDir, "toUpload.txt");

            //инициируем объект защелки на один сброс
            countDownLatch = new CountDownLatch(100000);//TODO
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

//    //отправляем на сервер запрос на загрузку файла в облачное хранилище
//    //FIXME перенести в контроллер интерфейса
//    public void uploadFile(String fromDir, String toDir, String filename){
//        //TODO temporarily
//        printMsg("***TCPClient.uploadFile() - has started***");
//
//        try {
//            //вычисляем размер файла
//            long fileSize = Files.size(Paths.get(currentClientDir, filename));
//
//            //инициируем объект файлового сообщения
//            FileMessage fileMessage = new FileMessage(fromDir, toDir, filename, fileSize);
//            //читаем файл и записываем данные в байтовый массив объекта файлового сообщения
//            fileMessage.readFileData(currentClientDir);//FIXME Разобраться с абсолютными папкими клиента
//
//            //если длина считанного файла отличается от длины исходного файла в хранилище
//            if(fileMessage.getFileSize() != fileMessage.getData().length){
//                printMsg("(Client)StorageTest.uploadFile() - Wrong the read file size!");
//                return;
//            }
//
//            //отправляем на сервер объект сообщения(команды)
//            connection.sendMessageObject(new CommandMessage(Commands.REQUEST_SERVER_FILE_UPLOAD,
//                    fileMessage));
//        } catch (IOException e) {
//            //печатаем в консоль сообщение об ошибке считывания файла
//            printMsg("TCPClient.uploadFile() - There is no file in the directory!");
//            e.printStackTrace();
//        }
//
//        //TODO temporarily
//        printMsg("***TCPClient.uploadFile() - has finished***");
//    }
    //отправляем на сервер запрос на загрузку файла в облачное хранилище
    //FIXME перенести в контроллер интерфейса
    public void uploadFile(String fromDir, String toDir, String filename) throws IOException {
        //TODO temporarily
        printMsg("***TCPClient.uploadFile() - has started***");

        //вычисляем размер файла
        long fileSize = Files.size(Paths.get(currentClientDir, filename));

        if(fileSize > FileFragmentMessage.CONST_FRAG_SIZE){
            //запускаем метод отправки файла по частям
//            uploadFileByFrags();
        } else {
            //запускаем метод отправки целого файла
            uploadEntireFile(fromDir, toDir, filename, fileSize);
        }


        //TODO temporarily
        printMsg("***TCPClient.uploadFile() - has finished***");
    }

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
