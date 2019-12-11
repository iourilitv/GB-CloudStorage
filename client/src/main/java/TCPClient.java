import messages.AuthMessage;
import messages.FileMessage;
import utils.CommandMessage;
import utils.handlers.ObjectHandler;
import utils.handlers.ServiceCommandHandler;
import utils.handlers.UploadCommandHandler;

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
    //инициируем строку названия директории для хранения файлов клиента
    private final String storageDir = "storage/client_storage";
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

    public void send () throws IOException {
        connection.sendMessageObject(new CommandMessage(CommandMessage.CMD_MSG_REQUEST_FILE_UPLOAD,
                new UploadCommandHandler(new FileMessage(storageDir, "file1.txt"))));

        connection.sendMessageObject(new CommandMessage(CommandMessage.CMD_MSG_REQUEST_AUTH,
                new ServiceCommandHandler(new AuthMessage("login1", "pass1"))));
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
        objectHandler.recognizeAndArrangeMessageObject(messageObject, storageDir);
    }

    public String getStorageDir() {
        return storageDir;
    }

    private synchronized void printMsg(String msg){
        log.append(msg).append("\n");
    }
}
