import java.io.*;
import java.util.ArrayList;
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
    //объявляем объект файла
    private File file;

    public ClientByte(){
        //инициируем объект файла
        file = new File("D:\\GeekBrains\\20191130_GB-Разработка_сетевого_хранилища_на_Java\\cloudstorage\\client\\src\\main\\resources\\files\\file1.txt");
        try {
            //инициируем переменную сетевого соединения
            connection = new TCPConnectionByte(this, IP_ADDR, PORT);//устанавливаем соединение при открытии окна
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
    }

    public void send () throws IOException {
        byte [] arr = { 65 , 66 , 67 , -127};
        //собираем байтовый массив из файла
//        byte [] arr = readFile(file);

        connection.sendMessageObject(arr);
        printMsg("client sending bytes");
    }

    private byte[] readFile(File file) {
        //TODO Длина файла в long!
        byte[] array =  new byte[(int) file.length()];

        try (InputStream in = new BufferedInputStream( new
                FileInputStream( file ))) {
            int x;
            while ((x = in.read()) != - 1 ) {
//                x;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array;
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

    @Override
    public void onReceiveByte(TCPConnectionByte tcpConnectionByte, byte b) {
        System.out.println("Client input byte: " + b);
    }
}
