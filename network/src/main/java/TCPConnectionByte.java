import java.io.*;
import java.net.Socket;

public class TCPConnectionByte {
    private final Socket socket;
    private final Thread rxThread;
    private final TCPConnectionListenerByte eventListenerByte;

    //TODO
    //объявляем переменные для буферезированных входного и выходного потоков
    private final BufferedOutputStream outCom;
    //объявляем объект входящего потока для получения сериализованного объекта сообщения(команды)
    ObjectInputStream objectInputStream;

    //конструктор для создания соединения
    public TCPConnectionByte(TCPConnectionListenerByte eventListenerByte, String ipAddr, int port) throws IOException {
        //создаем его на базе другого конструктора
        this(eventListenerByte, new Socket(ipAddr, port));
    }

    //конструктор для создания соединения снаружи
    public TCPConnectionByte(TCPConnectionListenerByte eventListenerByte, Socket socket) throws IOException {
        this.eventListenerByte = eventListenerByte;
        this.socket = socket;

        outCom = new BufferedOutputStream(new DataOutputStream(socket.getOutputStream()));

        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListenerByte.onConnectionReady(TCPConnectionByte.this);
                    while(!rxThread.isInterrupted()){
                        //инициируем объект входящего потока для получения сериализованного объекта сообщения(команды)
                        objectInputStream = new ObjectInputStream(socket.getInputStream());
                        //слушаем вход соединения и читаем поступающие байты(пока они есть)
                        eventListenerByte.onReceiveObject(TCPConnectionByte.this, objectInputStream);//TODO
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    eventListenerByte.onDisconnect(TCPConnectionByte.this);
                }
            }
        });
        rxThread.start();
    }

    //FIXME
    public synchronized void sendMessageObject(byte [] arr/*messages.AbstractMessage message*/){
        try {
            outCom.write(arr);
            outCom.flush();//принудительно передаем в сеть строку их буфера
        } catch (IOException e) {
            eventListenerByte.onException(TCPConnectionByte.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect(){
        rxThread.interrupt();
        try {

            objectInputStream.close();//TODO

            socket.close();
        } catch (IOException e) {
            eventListenerByte.onException(TCPConnectionByte.this, e);
        }
    }

    //переопределяем метод toString, чтобы перед сообщением выводить от кого оно пришло
    @Override
    public String toString(){
        return "TCPConnectionByte: " + socket.getInetAddress() + ": " + socket.getPort();
    }

    public Socket getSocket() {
        return socket;
    }
}


//TODO Delete at the finish

//    public synchronized void sendByte(byte b){
//        try {
//            outCom.write(b);
//            outCom.flush();//принудительно передаем в сеть байт из буфера
//        } catch (IOException e) {
//            eventListenerByte.onException(TCPConnectionByte.this, e);
//            disconnect();
//        }
//    }
