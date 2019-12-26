package tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import utils.CommandMessage;
import utils.CommandMessageManager1;

import java.io.IOException;

public class ServerInboundHandler extends ChannelInboundHandlerAdapter {
    //принимаем объект сервера
    private NettyServer server;
    //объявляем объект сообщения(команды)
    private CommandMessage commandMessage;
    //объявляем объект обработчика сообщений(команд)
    private CommandMessageManager1 commandMessageManager;

    public ServerInboundHandler(NettyServer server) {
        this.server = server;
        //инициируем объект обработчика сообщений(команд)
        commandMessageManager = new CommandMessageManager1(server);
    }

//    @Override
//    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//        super.channelRegistered(ctx);
//        //TODO temporarily
//        server.printMsg("ServerInboundHandler.channelRegistered() - ctx: " + ctx);
//    }
//
//    @Override
//    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//        super.channelUnregistered(ctx);
//        //TODO temporarily
//        server.printMsg("ServerInboundHandler.channelUnregistered() - ctx: " + ctx);
//    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        //если соединение установлено, то добавляем его в список
        server.getConnections().add(new TCPConnection1(ctx));

        //TODO temporarily
        server.printMsg("Client has connected. \nServerInboundHandler.channelActive() - ctx: " + ctx +
                ", connections.size(): " + server.getConnections().size()
                + ", connections.toString(): " + server.getConnections().toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        //если соединение отвалилось, ищем в коллекции его объект по соединению
        //удаляем объект соединения из коллекции
        server.getConnections().removeIf(c -> c.getCtx().equals(ctx));

        //TODO temporarily
        server.printMsg("Client has disconnected. \nServerInboundHandler.channelInactive() - ctx: " + ctx +
                ", connections.size(): " + server.getConnections().size()
                + ", connections.toString(): " + server.getConnections().toString());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msgObject) /*throws Exception*/ {
//        try {
//            if (msg instanceof FileRequest) {
//                FileRequest fr = (FileRequest) msg;
//                if (Files.exists(Paths.get("server_storage/" + fr.getFilename()))) {
//                    FileMessage fm = new FileMessage(Paths.get("server_storage/" + fr.getFilename()));
//                    ctx.writeAndFlush(fm);
//                }
//            }
//        } finally {
//            ReferenceCountUtil.release(msg);
//        }
        //десериализуем объект сообщения(команды)
        try {
            commandMessage = (CommandMessage) msgObject;
            //распознаем и обрабатываем полученный объект сообщения(команды)
            commandMessageManager.recognizeAndArrangeMessageObject(ctx, commandMessage);
        } catch (IOException/* | ClassNotFoundException*/ e) {
            e.printStackTrace();
        }
        finally {
            ReferenceCountUtil.release(msgObject);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) /*throws Exception*/ {
        cause.printStackTrace();
        ctx.close();
    }
}
