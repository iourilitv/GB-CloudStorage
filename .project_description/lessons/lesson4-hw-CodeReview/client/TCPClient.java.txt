package tcp;

import control.StorageTest;
import utils.CommandMessage;
import utils.CommandMessageManager;

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
    //принимаем объект тестера
    private StorageTest storageTest;
    //объявляем объект обработчика сообщений(команд)
    private CommandMessageManager commandMessageManager;

    public TCPClient(StorageTest storageTest) {
        this.storageTest = storageTest;
        //инициируем объект обработчика сообщений(команд)
        commandMessageManager = new CommandMessageManager(storageTest);
        try {
            //инициируем переменную сетевого соединения
            //устанавливаем соединение при открытии окна
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
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

    /** //FIXME возможно придется перенести в отдельный класс
     * Метод десериализует объект сообщения(команды)
     * @param tcpConnection - объект сетевого соединения с сервером
     * @param ois - входящий поток данных объекта
     */
    @Override
    public void onReceiveObject(TCPConnection tcpConnection, ObjectInputStream ois) {
        try {
            //инициируем объект сообщения(команды)
            CommandMessage commandMessage = (CommandMessage) ois.readObject();
            //распознаем и обрабатываем полученный объект сообщения(команды)
            commandMessageManager.recognizeAndArrangeMessageObject(commandMessage);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public TCPConnection getConnection() {
        return connection;
    }

    public synchronized void printMsg(String msg){
        log.append(msg).append("\n");
    }

}
