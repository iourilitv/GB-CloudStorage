import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

public class ClientByte implements TCPConnectionListenerByte {
    //инициируем константу IP адреса сервера(здесь - адрес моего ноута в домашней локальной сети)
    private static final String IP_ADDR = "192.168.1.103";//89.222.249.131(внешний белый адрес)
    //инициируем константу порта соединения
    private static final int PORT = 8189;
    //инициируем переменную для печати сообщений в консоль
    private final PrintStream log = System.out;
    //объявляем переменную сетевого соединения
    private TCPConnectionByte connection;

    public ClientByte(){
        try {
            //инициируем переменную сетевого соединения
            connection = new TCPConnectionByte(this, IP_ADDR, PORT);//устанавливаем соединение при открытии окна
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
    }

    public void send () {
        byte [] arr = { 65 , 66 , 67 };
        connection.sendMessageObject(arr);
        printMsg("client sending bytes");
    }

    //метод для работы с исключением. Т.к. он будет работать из разных потоков и окна, и соединения,
    //то его синхронизируем и применяем структуру для многопоточности
    private synchronized void printMsg(String msg){
        log.append(msg).append("\n");
    }

    @Override
    public void onConnectionReady(TCPConnectionByte tcpConnectionByte) {
        printMsg("Connection ready...");
    }

    @Override
    public void onDisconnect(TCPConnectionByte tcpConnectionByte) {
        printMsg("Connection close");
    }

    @Override
    public void onException(TCPConnectionByte tcpConnectionByte, Exception e) {
        printMsg("Connection exception: " + e);
    }

    /**
     * Метод обработки события получения клиентом набора байт от сервера
     * @param tcpConnectionByte - объект соединения
     * @param bytes - последовательность байт
     */
    @Override
    public void onReceiveBytes(TCPConnectionByte tcpConnectionByte, byte... bytes) {
        System.out.println("Client input bytes array: " + Arrays.toString(bytes));
    }
}
