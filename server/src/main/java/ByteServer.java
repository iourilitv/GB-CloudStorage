import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;

public class ByteServer implements TCPConnectionListenerByte {//создаем слушателя прямо в этом классе

    public static void main(String[] args) {
        new ByteServer();
    }

    //создадим экземпляр ссылочного массива(список) установленных соединенией
    private final ArrayList<TCPConnectionByte> connections = new ArrayList<>();

    private ByteServer(){
        System.out.println("Server running...");
        //создаем серверсокет, который слушает порт TCP:8189
        try(ServerSocket serverSocket = new ServerSocket(8189)){//это "try с ресурсом"
            //сервер слушает входящие соединения
            //на каждое новое соединение сервер создает TCPConnection
            while(true){
                try{
                    //сначала настроить dependencies с network в настройках модуля
                    //передаем себя как слушателя и объект сокета (его возвращает accept() при входящем соединении)
                    //в бесконечном цикле висим в методе accept(), который ждет новое внешнее соединение
                    //и как только соединение установилось он возвращает готовый объект сокета, который связан
                    //с этим соединением. Мы тут же передаем этот сокет в конструктор TCPConnection, включая и себя,
                    // как слушателя и создаем его экземпляр
                    new TCPConnectionByte(this, serverSocket.accept());
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
    public void onConnectionReady(TCPConnectionByte tcpConnectionByte) {
        //если соединение установлено, то добавляем его в список
        connections.add(tcpConnectionByte);
        sendToAllConnections("ClientByte connected: " + tcpConnectionByte);
        //при этом неявно вызовется переопределенный метод toString в tcpConnection //"TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }

    @Override
    public void onDisconnect(TCPConnectionByte tcpConnectionByte) {
        //если соединение отвалилось, то удаляем его из списка
        connections.remove(tcpConnectionByte);
        sendToAllConnections("ClientByte disconnected: " + tcpConnectionByte);
    }

    @Override
    public void onException(TCPConnectionByte tcpConnectionByte, Exception e) {
        System.out.println("TCPConnectionByte exception: " + e);
    }

    /**
     * Метод обработки события получения сервером набора байт от клиента
     * @param tcpConnectionByte - объект соединения
     * @param bytes - последовательность байт
     */
    @Override
    public void onReceiveBytes(TCPConnectionByte tcpConnectionByte, byte... bytes) {
        System.out.println("Server input bytes array: " + Arrays.toString(bytes));
    }

    //метод рассылки всем подключившимся сообщения об подключении/отключении пользователя
    private void sendToAllConnections(String value){
        System.out.println(value);//для отладки выводим сообщение в консоль
        final int cnt = connections.size();
        for (int i = 0; i < cnt; i++) {
            //TODO Использовать только для рассылки сервисных сообщений всем пользователям
//            connections.get(i).sendMessageObject();
        }
    }
}

