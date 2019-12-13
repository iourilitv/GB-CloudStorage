import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {
    private static final String IP_ADDR = "192.168.1.103";//89.222.249.131
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        //TODO Important! Все GUI java работают с потоками ограниченно - только из главного потока,
        // т.е. в ними нельзя раборать из разных потоков. Кроме swing, с которым нельзя работать даже
        // из главного потока, а только из EDT. Чтобы запустить строку в EDT, нужна следующая конструкция:
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField("yuriy");
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;//переменная сетевого соединения

    private ClientWindow(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);//чтобы окно располагалось всегда по середине
        setAlwaysOnTop(true);//чтобы окно было всегда поверх других окон

        log.setEditable(false);
        log.setLineWrap(true);//автоматический перенос слов
        add(log, BorderLayout.CENTER);

        fieldInput.addActionListener(this);//чтобы перехватить слушателя (событие нажатие Enter),
        //без использования анонимного класса

        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickname, BorderLayout.NORTH);

        setVisible(true);
        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);//устанавливаем соединение при открытии окна
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if(msg.equals("")) return;//не отправляем пустую строку
        fieldInput.setText(null);//очищаем поле
        connection.sendString(fieldNickname.getText() + ": " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMsg("Connection exception: " + e);
    }

    //метод для работы с исключением. Т.к. он будет работать из разных потоков и окна, и соединения,
    //то его синхронизируем и применяем структуру для многопоточности
    private synchronized void printMsg(String msg){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                //гарантированно устанавливаем курсор в самый конец текста(вниз)
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
