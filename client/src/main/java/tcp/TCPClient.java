package tcp;

import messages.AuthMessage;
import utils.Commands;
import messages.FileMessage;
import utils.CommandMessage;
import utils.handlers.ObjectHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;

/**
 * The class is responded for connecting with the server app.
 */
public class TCPClient implements TCPConnectionListener {
    //инициируем константу IP адреса сервера(здесь - адрес моего ноута в домашней локальной сети)
    private static final String IP_ADDR = "192.168.1.103";//89.222.249.131(внешний белый адрес)
    //инициируем константу порта соединения
    private static final int PORT = 8189;
    //инициируем переменную для печати сообщений в консоль
    private final PrintStream log = System.out;
    //объявляем переменную сетевого соединения
    private TCPConnection connection;

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

    //объявляем объект сообщения(команды)
    private CommandMessage messageObject;
    //объявляем объект обработчика сообщений(команд)
    private ObjectHandler objectHandler;

    public TCPClient() {
        //инициируем объект обработчика сообщений(команд)
        objectHandler = new ObjectHandler(this);
        //инициируем переменную для текущей директории клиента
        currentClientDir = clientDefaultRoot;
        try {
            //инициируем переменную сетевого соединения
            //устанавливаем соединение при открытии окна
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
    }

    //FIXME удалить, когда будет реализован интерфейс
    public void send() {
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
            countDownLatch = new CountDownLatch(1);
            //ждем сброса защелки
            countDownLatch.await();
            //восстанавливаем начальное значение директории в сетевом хранилище//TODO temporarily
            storageDir = "";
            //добавляем к корневой директории клиента имя подпапки назначения на клиенте
            clientDir = clientDir.concat("folderToDownloadFile");

            System.out.println("clientDir: " + clientDir);

            //отправляем на сервер запрос на скачивание файла из облачного хранилища
            downloadFile(storageDir, clientDir, "toDownload.png");
        } catch (InterruptedException e) {
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
    public void uploadFile(String fromDir, String toDir, String filename){
        //TODO temporarily
        printMsg("***TCPClient.uploadFile() - has started***");

        //инициируем объект файлового сообщения
        FileMessage fileMessage = new FileMessage(fromDir, toDir, filename);
        try {
            //читаем файл и записываем данные в байтовый массив объекта файлового сообщения
            fileMessage.readFileData(currentClientDir);//FIXME Разобраться с абсолютными папкими клиента

        } catch (IOException e) {
            //печатаем в консоль сообщение об ошибке считывания файла
            printMsg("TCPClient.uploadFile() - There is no file in the directory!");
            e.printStackTrace();
        }
        //отправляем на сервер объект сообщения(команды)
        connection.sendMessageObject(new CommandMessage(Commands.REQUEST_SERVER_FILE_UPLOAD,
                fileMessage));

        //TODO temporarily
        printMsg("***TCPClient.uploadFile() - has finished***");
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

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connection ready...");
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMsg("Connection exception: " + e);
    }

    @Override
    public void onReceiveObject(TCPConnection tcpConnection, ObjectInputStream ois) {
        //десериализуем объект сообщения(команды)
        try {
            messageObject = (CommandMessage) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        //распознаем и обрабатываем полученный объект сообщения(команды)
        objectHandler.recognizeAndArrangeMessageObject(messageObject);
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
