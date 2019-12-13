import java.io.*;
import java.net.Socket;

public class TCPConnectionByte {
    private final Socket socket;
    private final Thread rxThread;
    private final TCPConnectionListenerByte eventListenerByte;

    //TODO
    //объявляем переменные для буферезированных входного и выходного потоков
//    private final BufferedInputStream inCom;
    private final BufferedOutputStream outCom;

    //конструктор для создания соединения
    public TCPConnectionByte(TCPConnectionListenerByte eventListenerByte, String ipAddr, int port) throws IOException {
        //создаем его на базе другого конструктора
        this(eventListenerByte, new Socket(ipAddr, port));
    }

    //конструктор для создания соединения снаружи
    public TCPConnectionByte(TCPConnectionListenerByte eventListenerByte, Socket socket) throws IOException {
        this.eventListenerByte = eventListenerByte;
        this.socket = socket;

        //TODO
//        inCom = new BufferedInputStream(new DataInputStream(socket.getInputStream()));
        //
//        DataInputStream dis = new DataInputStream(socket.getInputStream());//TODO

        //синтаксис требует использовать только финализированную переменную, но просто переменную нельзя менять
        //поэтому используется схема с элементом финализированного массива с только одним элементом
//        final Byte[] inComByte = new Byte[1];
//        outCom = new BufferedOutputStream(new FileOutputStream(""));
        outCom = new BufferedOutputStream(new DataOutputStream(socket.getOutputStream()));

        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListenerByte.onConnectionReady(TCPConnectionByte.this);
                    while(!rxThread.isInterrupted()){

                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());//TODO
                        //слушаем вход соединения и читаем поступающие байты(пока они есть)
//                        eventListenerByte.onReceiveByte(TCPConnectionByte.this, dis.readByte());//TODO
//                        eventListenerByte.onReceiveBytes(TCPConnectionByte.this, dis.readByte());
                        eventListenerByte.onReceiveObject(TCPConnectionByte.this, ois);//TODO
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

    //TODO
    public synchronized void sendMessageObject(byte [] arr/*messages.AbstractMessage message*/){
        try {
            outCom.write(arr);
            outCom.flush();//принудительно передаем в сеть строку их буфера
        } catch (IOException e) {
            eventListenerByte.onException(TCPConnectionByte.this, e);
            disconnect();
        }
    }

    public synchronized void sendByte(byte b){
        try {
            outCom.write(b);
            outCom.flush();//принудительно передаем в сеть байт из буфера
        } catch (IOException e) {
            eventListenerByte.onException(TCPConnectionByte.this, e);
            disconnect();
        }
    }

//    public synchronized void sendByte(BufferedInputStream bis){
//        try {
//            int x;
//            while((x = bis.read()) != -1){
//                System.out.println("TCPConn.. x: " + x);
//                outCom.write(x);
//                outCom.flush();//принудительно передаем в сеть байты из буфера
//            }
//        } catch (IOException e) {
//            eventListenerByte.onException(TCPConnectionByte.this, e);
//            disconnect();
//        }
//    }

    public synchronized void disconnect(){
        rxThread.interrupt();
        try {
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
