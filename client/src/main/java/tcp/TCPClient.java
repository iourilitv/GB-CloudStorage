package tcp;

import messages.AuthMessage;
import messages.Commands;
import messages.FileMessage;
import utils.CommandMessage;
import utils.handlers.ObjectHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;

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
    //инициируем строку названия директории облачного хранилища(сервера) для хранения файлов клиента
    private final String storageDir = "storage/server_storage";
    //инициируем строку названия директории клиента для хранения файлов
    private final String clientDir = "storage/client_storage";
    //объявляем объект сообщения(команды)
    private CommandMessage messageObject;
    //объявляем объект обработчика сообщений(команд)
    private ObjectHandler objectHandler;

    public TCPClient() {
        //инициируем объект обработчика сообщений(команд)
        objectHandler = new ObjectHandler();
        try {
            //инициируем переменную сетевого соединения
            //устанавливаем соединение при открытии окна
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
    }

    //FIXME удалить, когда будет реализован интерфейс
    public void send () throws IOException {
        //отправляем на сервер запрос на загрузку файла в облачное хранилище
//        FileMessage fileMessage = new FileMessage(clientDir, storageDir, "file1.txt"));
//        fileMessage.readFileData();
//        connection.sendMessageObject(new CommandMessage(Commands.REQUEST_SERVER_FILE_UPLOAD,
//                fileMessage));
        uploadFile(clientDir, storageDir, "file1.txt");

//        //отправляем на сервер запрос на авторизацию в облачное хранилище
//        connection.sendMessageObject(new CommandMessage(Commands.REQUEST_SERVER_AUTH,
//                new AuthMessage("login1", "pass1")));
        //отправляем на сервер запрос на скачивание файла из облачного хранилища
//        connection.sendMessageObject(new CommandMessage(Commands.REQUEST_SERVER_FILE_DOWNLOAD,
//                new FileMessage(storageDir, clientDir, "acmp_ru.png")));
        downloadFile(storageDir, clientDir, "acmp_ru.png");
    }

    //отправляем на сервер запрос на загрузку файла в облачное хранилище
    //FIXME перенести в контроллер интерфейса
    public void uploadFile(String fromDir, String toDir, String filename){
        //инициируем объект файлового сообщения
        FileMessage fileMessage = new FileMessage(fromDir, toDir, filename);
        try {
            //читаем файл и записываем данные в байтовый массив объекта файлового сообщения
            fileMessage.readFileData();
        } catch (IOException e) {
            //печатаем в консоль сообщение об ошибке считывания файла
            printMsg("There is no file in the directory!");
            e.printStackTrace();//TODO
        }
        //отправляем на сервер объект сообщения(команды)
        connection.sendMessageObject(new CommandMessage(Commands.REQUEST_SERVER_FILE_UPLOAD,
                fileMessage));
    }

    //отправляем на сервер запрос на скачивание файла из облачного хранилища
    //FIXME перенести в контроллер интерфейса
    public void downloadFile(String fromDir, String toDir, String filename){
        //инициируем объект файлового сообщения
        FileMessage fileMessage = new FileMessage(fromDir, toDir, filename);
        //отправляем на сервер объект сообщения(команды)
        connection.sendMessageObject(new CommandMessage(Commands.REQUEST_SERVER_FILE_DOWNLOAD,
                fileMessage));
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

    private synchronized void printMsg(String msg){
        log.append(msg).append("\n");
    }
}
