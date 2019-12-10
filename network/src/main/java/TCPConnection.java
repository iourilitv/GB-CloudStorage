import java.io.*;
import java.net.Socket;

public class TCPConnection {
    private final Socket socket;
    private final Thread rxThread;
    private final TCPConnectionListener eventListener;

    //TODO
    //объявляем переменные для буферезированных входного и выходного потоков
    private final BufferedOutputStream outCom;
    //объявляем объект входящего потока для получения сериализованного объекта сообщения(команды)
    ObjectInputStream objectInputStream;

    //конструктор для создания соединения
    public TCPConnection(TCPConnectionListener eventListener, String ipAddr, int port) throws IOException {
        //создаем его на базе другого конструктора
        this(eventListener, new Socket(ipAddr, port));
    }

    //конструктор для создания соединения снаружи
    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;

        outCom = new BufferedOutputStream(new DataOutputStream(socket.getOutputStream()));

        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while(!rxThread.isInterrupted()){
                        //инициируем объект входящего потока для получения сериализованного объекта сообщения(команды)
                        objectInputStream = new ObjectInputStream(socket.getInputStream());
                        //слушаем вход соединения и читаем поступающие байты(пока они есть)
                        eventListener.onReceiveObject(TCPConnection.this, objectInputStream);//TODO
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
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
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect(){
        rxThread.interrupt();
        try {

            objectInputStream.close();//TODO

            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
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
