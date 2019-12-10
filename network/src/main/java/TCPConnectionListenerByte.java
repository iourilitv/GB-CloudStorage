import java.io.ObjectInputStream;

public interface TCPConnectionListenerByte {
    //возможные события
    void onConnectionReady(TCPConnectionByte tcpConnectionByte);//соединение установлено
    void onDisconnect(TCPConnectionByte tcpConnectionByte);//соединение разорвано
    void onException(TCPConnectionByte tcpConnectionByte, Exception e);//что-то пошло не так и появилось исключение
    //получен сериализованный объект сообщения(объект команды)
    void onReceiveObject(TCPConnectionByte tcpConnectionByte, ObjectInputStream ois);

}
