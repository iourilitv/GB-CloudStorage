public interface TCPConnectionListener {
    //возможные события
    void onConnectionReady(TCPConnection tcpConnection);//соединение установлено
    void onReceiveString(TCPConnection tcpConnection, String value);//принята строка сообщения
    void onDisconnect(TCPConnection tcpConnection);//соединение разорвано
    void onException(TCPConnection tcpConnection, Exception e);//что-то пошло не так и появилось исключение

}
