import messages.AbstractMessage;
import messages.AuthMessage;
import messages.CommandMessage;

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
//    private final File storageDir = new File("storage/client_storage");//TODO
    private final String storageDir = "storage/client_storage";
    //объявляем объект файла
    private File file;
    //инициируем объект имени файла
    private String fileName = "acmp_ru.png";
    //объявляем объект графического файла
    private File fileG;
    //инициируем объект имени файла объекта команды(сообщения)
    private String messageFileName = "messageFile.bin";
    //объявляем объект файла объекта команды(сообщения)
    private File messageFile;
    //объявляем объект потока чтения байтов из файла
    FileInputStream fis;
    //объявляем объект буферезированного потока чтения байтов из файла
    BufferedInputStream bis;
    //объявляем объект команды(сообщения)
//    AbstractMessage message;//TODO
//    FileFragment message;//TODO
    MessageObject messageObject;//TODO

    public ClientByte() {
//        //инициируем объект графического файла
//        fileG = new File(storageDir + "/" + fileName);//TODO
        //инициируем объект файла объекта команды(сообщения)
//        messageFile = new File(storageDir + "/" + messageFileName);

        try {
            //инициируем переменную сетевого соединения
            connection = new TCPConnectionByte(this, IP_ADDR, PORT);//устанавливаем соединение при открытии окна
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
    }

//    public void send () {
////        //читаем и отправляем побайтно содержимое файла не зависимо от его типа
////      file = fileG;//TODO
//        //инициируем объект команды(сообщения)
////      message = new CommandMessage(CommandMessage.CMD_MSG__REQUEST_SERVER_DELETE_FILE, fileG);
//        try {
//            message = new CommandMessage(storageDir, messageFileName);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //записываем объект команды во временный файл
//        writeDownMessageObjectToFile(message);
//        //отправляем на сервер побайтно содержимое файла
//        readAndSendFile(file);//TODO
//
////      readAndSendMessage(new messages.CommandMessage(messages.CommandMessage.CMD_MSG__REQUEST_FILES_LIST, fileG));
//        printMsg("Client has sent the bytes.");
//    }
public void send () {
    try {
        ObjectOutputStream out = new ObjectOutputStream(connection.getSocket().getOutputStream());
//        message = new CommandMessage(storageDir, "file1.txt");//TODO
//        message = new FileFragment(storageDir, messageFileName);
//        message = new FileFragment(storageDir, "file1.txt");//TODO
//        out.writeObject(message);//TODO

        //TODO
        //инициируем объект объекта сообщения, в котором завернуто сообщение типа "команда"
        messageObject = new MessageObject("Command",
                new CommandMessage(storageDir, "file1.txt"));
        out.writeObject(messageObject);

//        out.close();//TODO
        out = new ObjectOutputStream(connection.getSocket().getOutputStream());

        messageObject = new MessageObject("Auth",
                new AuthMessage("login1", "pass1"));
        out.writeObject(messageObject);

    } catch (IOException e) {
        e.printStackTrace();
    }

    printMsg("Client has sent the bytes.");
}

    private void writeDownMessageObjectToFile(AbstractMessage message){
        file = new File(storageDir + "/" + messageFileName);
        //в блоке трай с ресурсом(ами) записываем объект команды(сообщения) во временный файл
        try(FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            //создаем файл, если его нет, или очищаем, если есть файл
            file.createNewFile();
            //записываем объект в файл с помощью потока
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onReceiveObject(TCPConnectionByte tcpConnectionByte, ObjectInputStream ois) {
        System.out.println("Client received the object: ");
    }
}

//        //инициируем объект файла
//        file = new File("D:\\GeekBrains\\20191130_GB-Разработка_сетевого_хранилища_на_Java\\cloudstorage\\client\\src\\main\\resources\\files\\file1.txt");
//        //инициируем объект потока чтения байтов из файла
//        fis = new FileInputStream(file);
//        //инициируем объект буферезированного потока чтения байтов из файла
//        bis = new BufferedInputStream(fis);
