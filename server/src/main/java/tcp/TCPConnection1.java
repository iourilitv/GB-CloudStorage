package tcp;

import io.netty.channel.ChannelHandlerContext;

public class TCPConnection1 {
    //принимаем объект соединения с сервером
    private ChannelHandlerContext ctx;
    //инициируем переменную идентификатора подсоединенного клиента
    private String clientID = "unknownID";

    public TCPConnection1(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }
}