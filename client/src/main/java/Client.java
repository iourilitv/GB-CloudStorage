import java.io.*;

public class Client {

    public static void main(String[] args) throws IOException {
//        new Client().send();
        new TCPClient().send();
    }

//    //инициируем константу IP адреса сервера(здесь - адрес моего ноута в домашней локальной сети)
//    private static final String IP_ADDR = "192.168.1.103";//89.222.249.131(внешний белый адрес)
//    //инициируем константу порта соединения
//    private static final int PORT = 8189;
//    //инициируем переменную для печати сообщений в консоль
//    private final PrintStream log = System.out;
//    //объявляем переменную сетевого соединения
//    private TCPConnection connection;
//    //инициируем строку названия директории для хранения файлов клиента
//    private final String storageDir = "storage/client_storage";
//    //объявляем объект сообщения(команды)
//    private CommandMessage messageObject;
//    //объявляем объект обработчика сообщений(команд)
//    private ObjectHandler objectHandler;
//
//    public Client() {
//        //инициируем объект обработчика сообщений(команд)
//        objectHandler = new ObjectHandler();
//        try {
//            //инициируем переменную сетевого соединения
//            //устанавливаем соединение при открытии окна
//            connection = new TCPConnection(this, IP_ADDR, PORT);
//        } catch (IOException e) {
//            printMsg("Connection exception: " + e);
//        }
//    }
//
//    public void send () throws IOException {
//        connection.sendMessageObject(new CommandMessage(CommandMessage.CMD_MSG_REQUEST_FILE_UPLOAD,
//                new UploadCommandHandler(new FileMessage(storageDir, "file1.txt"))));
//
//        connection.sendMessageObject(new CommandMessage(CommandMessage.CMD_MSG_REQUEST_AUTH,
//                new ServiceCommandHandler(new AuthMessage("login1", "pass1"))));
//    }
//
//    @Override
//    public void onConnectionReady(TCPConnection tcpConnection) {
//        printMsg("Connection ready...");
//    }
//
//    @Override
//    public void onDisconnect(TCPConnection tcpConnection) {
//        printMsg("Connection close");
//    }
//
//    @Override
//    public void onException(TCPConnection tcpConnection, Exception e) {
//        printMsg("Connection exception: " + e);
//    }
//
//    @Override
//    public void onReceiveObject(TCPConnection tcpConnection, ObjectInputStream ois) {
//        //десериализуем объект сообщения(команды)
//        try {
//            messageObject = (CommandMessage) ois.readObject();
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        //распознаем и обрабатываем полученный объект сообщения(команды)
//        objectHandler.recognizeAndArrangeMessageObject(messageObject, storageDir);
//    }
//
//    public String getStorageDir() {
//        return storageDir;
//    }
//
//    private synchronized void printMsg(String msg){
//        log.append(msg).append("\n");
//    }
}

//TODO Delete at the finish

//        //инициируем объект файла
//        file = new File("D:\\GeekBrains\\20191130_GB-Разработка_сетевого_хранилища_на_Java\\cloudstorage\\client\\src\\main\\resources\\files\\file1.txt");
//        //инициируем объект потока чтения байтов из файла
//        fis = new FileInputStream(file);
//        //инициируем объект буферезированного потока чтения байтов из файла
//        bis = new BufferedInputStream(fis);

//    //объявляем объект файла
//    private File file;
//    //инициируем объект имени файла объекта команды(сообщения)
//    private String messageFileName = "messageFile.bin";
//    //инициируем объект имени файла
//    private String fileName = "acmp_ru.png";
//    //объявляем объект графического файла
//    private File fileG;
//    //объявляем объект файла объекта команды(сообщения)
//    private File messageFile;
//    //объявляем объект потока чтения байтов из файла
//    FileInputStream fis;
//    //объявляем объект буферезированного потока чтения байтов из файла
//    BufferedInputStream bis;
//объявляем объект команды(сообщения)
//    AbstractMessage message;
//    FileFragment message;

//        //инициируем объект графического файла
//        fileG = new File(storageDir + "/" + fileName);
//        //инициируем объект файла объекта команды(сообщения)
//        messageFile = new File(storageDir + "/" + messageFileName);

//    private void writeDownMessageObjectToFile(AbstractMessage message){
//        file = new File(storageDir + "/" + messageFileName);
//        //в блоке трай с ресурсом(ами) записываем объект команды(сообщения) во временный файл
//        try(FileOutputStream fos = new FileOutputStream(file);
//            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
//            //создаем файл, если его нет, или очищаем, если есть файл
//            file.createNewFile();
//            //записываем объект в файл с помощью потока
//            oos.writeObject(message);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//        message = new CommandMessage(storageDir, "file1.txt");
//        message = new FileFragment(storageDir, messageFileName);
//        message = new FileFragment(storageDir, "file1.txt");
//        out.writeObject(message);

//    /**
//     * Метод читает файл и отправляет его на сервер побайтно
//     * @param file - объект файла
//     */
//    private void readAndSendFile(File file) {
//        try (InputStream in = new BufferedInputStream(new
//                FileInputStream(file))) {
//            int x;
//            while ((x = in.read()) != - 1 ) {
//                connection.sendByte((byte)x);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    private void readAndSendMessage(AbstractMessage message) {
//        byte [] byteObj = null ;
//        try (ByteArrayOutputStream barrOut = new ByteArrayOutputStream();
//                ObjectOutputStream objOut = new ObjectOutputStream(barrOut)
//        ) {
//            objOut.writeObject(message);
//            byteObj = barrOut.toByteArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("ClientByte.readAndSendMessage byteObj: " + Arrays.toString(byteObj));
//
//        try (ByteArrayInputStream barrIn = new ByteArrayInputStream(byteObj)) {
//
//            int x;
//            while ((x = barrIn.read()) != - 1 ) {
//                connection.sendByte((byte)x);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


//    /**
//     * Метод обработки события получения клиентом набора байт от сервера
//     * @param tcpConnectionByte - объект соединения
//     * @param bytes - последовательность байт
//     */
//    @Override
//    public void onReceiveBytes(TCPConnectionByte tcpConnectionByte, byte... bytes) {
//        System.out.println("Client input bytes array: " + Arrays.toString(bytes));
//    }

//    @Override
//    public void onReceiveByte(TCPConnectionByte tcpConnectionByte, byte b) {
////        System.out.println("Client input byte: " + b);
//    }