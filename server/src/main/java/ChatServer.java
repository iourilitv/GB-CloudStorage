import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

//пауза на 1:04:35
//Нужно установить программу PuTTY - это простой клиент(типа telnet) для проверки серверной части
//Load
// Hostname <ip address даже внешний> port 8189
//Connection type Raw (сырые)
//нажать Open
public class ChatServer implements TCPConnectionListener {//создаем слушателя прямо в этом классе

    public static void main(String[] args) {
        new ChatServer();
    }

    //создадим экземпляр ссылочного массива(список) установленных соединенией
    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer(){
        System.out.println("Server running...");
        //создаем серверсокет, который слушает порт TCP:8189
        try(ServerSocket serverSocket = new ServerSocket(8189)){//это "try с ресурсом"
            //сервер слушает входящие соединения
            //на каждое новое соединение сервер создает TCPConnection
            while(true){
                try{
                    //сначала настроить dependecies с network в настройках модуля
                    //передаем себя как слушателя и объект сокета (его возвращает accept() при входящем соединении)
                    //в бесконечном цикле висим в методе accept(), который ждет новое внешнее соединение
                    //и как только соединение установилось он возвращает готовый объект сокета, который связан
                    //с этим соединением. Мы тут же передаем этот сокет в конструктор TCPConnection, включая и себя,
                    // как слушателя и создаем его экземпляр
                    new TCPConnection(this, serverSocket.accept());
                } catch(IOException e){
                    System.out.println("TCPConnection: " + e);
                }
            }

        } catch (IOException e){
            throw new RuntimeException(e);//закрываем сокет, если что-то пошло не так
        }
    }

    //синхронизируем методы, чтобы нельзя было в них попасть одновременно из разных потоков
    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        //если соединение установлено, то добавляем его в список
        connections.add(tcpConnection);
        sendToAllConnections("Client connected: " + tcpConnection);
        //при этом неявно вызовется переопределенный метод toString в tcpConnection //"TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);//отправим всем приятую строку сообщения
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        //если соединение отвалилось, то удаляем его из списка
        connections.remove(tcpConnection);
        sendToAllConnections("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    //метод рассылки всем подключившимся сообщения об подключении/отключении пользователя
    private void sendToAllConnections(String value){
        System.out.println(value);//для отладки выводим сообщение в консоль
        final int cnt = connections.size();
        for (int i = 0; i < cnt; i++) {
            connections.get(i).sendString(value);
        }
    }
}

