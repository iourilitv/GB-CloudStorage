import java.io.ObjectInputStream;

public interface TCPConnectionListener {
    //возможные события
    void onConnectionReady(TCPConnection tcpConnection);//соединение установлено
    void onDisconnect(TCPConnection tcpConnection);//соединение разорвано
    void onException(TCPConnection tcpConnection, Exception e);//что-то пошло не так и появилось исключение
    //получен сериализованный объект сообщения(объект команды)
    void onReceiveObject(TCPConnection tcpConnection, ObjectInputStream ois);

}
