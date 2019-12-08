import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;

public class ByteServer implements TCPConnectionListenerByte {//создаем слушателя прямо в этом классе

    public static void main(String[] args) throws IOException {
        new ByteServer();
    }

    //создадим экземпляр ссылочного массива(список) установленных соединенией
    private final ArrayList<TCPConnectionByte> connections = new ArrayList<>();
    //инициируем объект директории для хранения файлов клиента
    private final File storageDir = new File("storage/server_storage");
    //объявляем объект файла
    private File file;
    //инициируем объект имени файла
    private String fileName = "acmp_ru.png";
    //объявляем объект графического файла
    private File fileG;
    //объявляем объект потока записи байтов в файл
    FileOutputStream fos;
    //объявляем объект буферезированного потока записи байтов в файл
    BufferedOutputStream bos;

    private ByteServer() throws IOException {
//        //инициируем объект файла
//        file = new File("D:\\GeekBrains\\20191130_GB-Разработка_сетевого_хранилища_на_Java\\cloudstorage\\server\\src\\main\\resources\\files\\fileS1.txt");
//        //создаем новый файл, если его нет и очищаем, если есть
//        file.createNewFile();
//        //удаляем файл по закрытию приложения
//        file.deleteOnExit();//TODO не работает, если останавливать в IDEA!
//        //инициируем объект потока записи байтов в файл
////        fos = new FileOutputStream(file, true);//2-nd param = true - append to the file, instead of renew the file
//        fos = new FileOutputStream(file);
//        //инициируем объект буферезированного потока записи байтов в файл
////        bos = new BufferedOutputStream(fos, 2);//2-nd param = 2 bytes - a size of buffer instead of 8192 in default
//        bos = new BufferedOutputStream(fos);

        //инициируем объект графического файла
        fileG = new File(storageDir + "/" + fileName);
        fileG.createNewFile();
        fos = new FileOutputStream(fileG);
        bos = new BufferedOutputStream(fos);

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
        } finally {
            //закрываем потоки по закрытию
            fos.close();
            bos.close();
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
//        System.out.println("Server input bytes array: " + Arrays.toString(bytes));
//
//        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes))) {
//            Byte[] array;
//            ArrayList<Byte> ab = new ArrayList<>();
//            while ((dis.read()) != - 1 ) {
//                ab.add(dis.readByte());
//                System.out.println("Server ab.toString(): " + ab.toString());
//            }
//            array = (Byte[]) ab.toArray();
//
//            System.out.println("Server saved bytes array: " + Arrays.toString(array));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Метод обработки события получения сервером одного байта от клиента
     * @param tcpConnectionByte - объект соединения
     * @param b - байт
     */
    @Override
    public void onReceiveByte(TCPConnectionByte tcpConnectionByte, byte b) {
        System.out.println("Server input byte: " + b);
        try {
            bos.write(b);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    public void onReceiveByte(TCPConnectionByte tcpConnectionByte, byte b) {
//        System.out.println("Server input byte: " + b);
//
//        byte [] byteObj = null ;
//        try (ByteArrayOutputStream barrOut = new ByteArrayOutputStream()) {
//            barrOut.write(b);
//            byteObj = barrOut.toByteArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("ByteServer.onReceiveByte byteObj: " + Arrays.toString(byteObj));
//
////        try (BufferedInputStream barrIn = new BufferedInputStream();
////             ObjectInputStream objIn = new ObjectInputStream(barrIn)){
////            CommandMessage commandMessage = (CommandMessage) objIn.readObject();
////            bos.write(b);
////            bos.flush();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//    }

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

