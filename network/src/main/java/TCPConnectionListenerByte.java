import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface TCPConnectionListenerByte {
    //возможные события
    void onConnectionReady(TCPConnectionByte tcpConnectionByte);//соединение установлено
    void onDisconnect(TCPConnectionByte tcpConnectionByte);//соединение разорвано
    void onException(TCPConnectionByte tcpConnectionByte, Exception e);//что-то пошло не так и появилось исключение

    /**
     * Метод обработки события получения клиентом набора байт от сервера и наоборот
     * @param tcpConnectionByte - объект соединения
     * @param bytes - последовательность байт
     */
    void onReceiveBytes(TCPConnectionByte tcpConnectionByte, byte... bytes);

    void onReceiveByte(TCPConnectionByte tcpConnectionByte, byte b);

    void onReceiveObject(TCPConnectionByte tcpConnectionByte, ObjectInputStream ois);

}
