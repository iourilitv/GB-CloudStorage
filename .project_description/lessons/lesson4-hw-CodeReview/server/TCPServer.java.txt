package tcp;

import utils.CommandMessage;
import utils.CommandMessageManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * The class is responded for connecting with the clients apps.
 */
public class TCPServer implements TCPConnectionListener {//создаем слушателя прямо в этом классе

    //создадим экземпляр ссылочного массива(список) установленных соединенией
    private final ArrayList<TCPConnection> connections = new ArrayList<>();
    //инициируем переменную для печати сообщений в консоль
    private final PrintStream log = System.out;
    //инициируем строку названия директории облачного хранилища(сервера) для хранения файлов клиента
    private final String storageRoot = "storage/server_storage";

    //объявляем объект сообщения(команды)
    private CommandMessage messageObject;
    //объявляем объект обработчика сообщений(команд)
    private CommandMessageManager commandMessageManager;

    public TCPServer() {
        printMsg("Server running...");
        //инициируем объект обработчика сообщений(команд)
        commandMessageManager = new CommandMessageManager(this);
        //создаем серверсокет, который слушает порт TCP:8189
        try(ServerSocket serverSocket = new ServerSocket(8189)){//это "try с ресурсом"
            //сервер слушает входящие соединения
            //на каждое новое соединение сервер создает tcp.TCPConnection
            while(true){
                try{
                    //сначала настроить dependencies с network в настройках модуля
                    //передаем себя как слушателя и объект сокета (его возвращает accept() при входящем соединении)
                    //в бесконечном цикле висим в методе accept(), который ждет новое внешнее соединение
                    //и как только соединение установилось он возвращает готовый объект сокета, который связан
                    //с этим соединением. Мы тут же передаем этот сокет в конструктор tcp.TCPConnection, включая и себя,
                    // как слушателя и создаем его экземпляр
                    new TCPConnection(this, serverSocket.accept());
                } catch(IOException e){
                    System.out.println("tcp.TCPConnection: " + e);
                }
            }
        } catch (IOException e){
            throw new RuntimeException(e);//закрываем сокет, если что-то пошло не так
        }
    }

    //синхронизируем методы, чтобы нельзя было в них попасть одновременно из разных потоков
    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        //если соединение установлено, то добавляем его в список
        connections.add(tcpConnection);

        //TODO temporarily
        printMsg("TCPServer.onConnectionReady() " + connections.toString());

    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        //если соединение отвалилось, то удаляем его из списка
        connections.remove(tcpConnection);
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnectionByte exception: " + e);
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
        commandMessageManager.recognizeAndArrangeMessageObject(tcpConnection, messageObject);
    }

    /**
     * Метод отправки объекта сообщения подключенному клиенту
     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param messageObject - объект сообщения(команды)
     */
    public void sendToClient(TCPConnection tcpConnection, CommandMessage messageObject){
        //если соединение не прервано
        if(tcpConnection != null && tcpConnection.getSocket().isConnected()){
            //отравляем клиенту объект сообщения(команды)
            tcpConnection.sendMessageObject(messageObject);
        }
    }

    public String getStorageRoot() {
        return storageRoot;
    }

    public ArrayList<TCPConnection> getConnections() {
        return connections;
    }

    public synchronized void printMsg(String msg){
        log.append(msg).append("\n");
    }
}