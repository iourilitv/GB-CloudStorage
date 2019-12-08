import java.io.*;
import java.util.Arrays;

public class ClientByte implements TCPConnectionListenerByte {

    public static void main(String[] args) throws IOException {
        new ClientByte().send();
    }

    //инициируем константу IP адреса сервера(здесь - адрес моего ноута в домашней локальной сети)
    private static final String IP_ADDR = "192.168.1.103";//89.222.249.131(внешний белый адрес)
    //инициируем константу порта соединения
    private static final int PORT = 8189;
    //инициируем переменную для печати сообщений в консоль
    private final PrintStream log = System.out;
    //объявляем переменную сетевого соединения
    private TCPConnectionByte connection;
    //инициируем объект директории для хранения файлов клиента
    private final File storageDir = new File("storage/client_storage");
    //объявляем объект файла
    private File file;
    //инициируем объект имени файла
    private String fileName = "acmp_ru.png";
    //объявляем объект графического файла
    private File fileG;
    //объявляем объект потока чтения байтов из файла
    FileInputStream fis;
    //объявляем объект буферезированного потока чтения байтов из файла
    BufferedInputStream bis;

    public ClientByte() throws FileNotFoundException {
//        //инициируем объект файла
//        file = new File("D:\\GeekBrains\\20191130_GB-Разработка_сетевого_хранилища_на_Java\\cloudstorage\\client\\src\\main\\resources\\files\\file1.txt");
//        //инициируем объект потока чтения байтов из файла
//        fis = new FileInputStream(file);
//        //инициируем объект буферезированного потока чтения байтов из файла
//        bis = new BufferedInputStream(fis);

        //TODO
        //инициируем объект графического файла
        fileG = new File(storageDir + "/" + fileName);

        try {
            //инициируем переменную сетевого соединения
            connection = new TCPConnectionByte(this, IP_ADDR, PORT);//устанавливаем соединение при открытии окна
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
    }

    public void send () throws IOException {
    //читаем и отправляем побайтно содержимое файла не зависимо от его типа
    readAndSendFile(fileG);
//    readAndSendMessage(new CommandMessage(CommandMessage.CMD_MSG__REQUEST_FILES_LIST, fileG));
    printMsg("client sending bytes");
}

    /**
     * Метод читает файл и отправляет его на сервер побайтно
     * @param file - объект файла
     */
    private void readAndSendFile(File file) {
        try (InputStream in = new BufferedInputStream(new
                FileInputStream(file))) {
            int x;
            while ((x = in.read()) != - 1 ) {
                connection.sendByte((byte)x);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readAndSendMessage(AbstractMessage message) {
        byte [] byteObj = null ;
        try (ByteArrayOutputStream barrOut = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(barrOut)
        ) {
            objOut.writeObject(message);
            byteObj = barrOut.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("ClientByte.readAndSendMessage byteObj: " + Arrays.toString(byteObj));

        try (ByteArrayInputStream barrIn = new ByteArrayInputStream(byteObj)) {

            int x;
            while ((x = barrIn.read()) != - 1 ) {
                connection.sendByte((byte)x);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
//        System.out.println("Client input byte: " + b);
    }
}
