package tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import messages.AuthMessage;
import messages.DirectoryMessage;
import utils.CloudStorageServer;
import utils.CommandMessage;
import utils.Commands;
import utils.handlers.UsersAuthController;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This server's class responds for client authentication.
 * It discards not Auth commandMessages from not authorized clients.
 */
public class AuthGateway extends ChannelInboundHandlerAdapter {
    //принимаем объект контроллера сетевого хранилища
    private final CloudStorageServer storageServer;
    //принимаем объект сервера
    private NettyServer server;//TODO не нужен?
    //принимаем объект контроллера авторизации клиента
    private UsersAuthController usersAuthController;

//    //объявляем объект обработчика сообщений(команд)
//    private CommandMessageManager1 commandMessageManager;

//    //объявляем объект сервисного хендлера
//    private UsersAuthController usersAuthController;

    //объявляем переменную типа команды
    private int command;

    public AuthGateway(CloudStorageServer storageServer, NettyServer server) {
        this.storageServer = storageServer;
        this.server = server;
        //принимаем объект контроллера авторизации пользователей
        usersAuthController = storageServer.getUsersAuthController();

//        //инициируем объект контроллера клиента
//        usersAuthController = new UsersAuthController(server);

//        //инициируем объект обработчика сообщений(команд)
//        commandMessageManager = new CommandMessageManager1(server);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);

//        //если соединение установлено, то добавляем его в список
//        server.getConnections().add(new TCPConnection1(ctx));

//        //TODO temporarily
//        server.printMsg("Client has connected. \nServerInboundHandler.channelActive() - ctx: " + ctx +
//                ", connections.size(): " + server.getConnections().size()
//                + ", connections.toString(): " + server.getConnections().toString());

        //если соединение установлено, отправляем клиенту сообщение
        ctx.writeAndFlush(new CommandMessage(Commands.SERVER_NOTIFICATION_CLIENT_CONNECTED));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        super.channelInactive(ctx);

        //FIXME разобрать
//        //если соединение отвалилось, ищем в коллекции его объект по соединению
//        //удаляем объект соединения из коллекции
//        server.getConnections().removeIf(c -> c.getCtx().equals(ctx));
//
//        //TODO temporarily
//        server.printMsg("Client has disconnected. \nServerInboundHandler.channelInactive() - ctx: " + ctx +
//                ", connections.size(): " + server.getConnections().size()
//                + ", connections.toString(): " + server.getConnections().toString());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msgObject) {
        //десериализуем объект сообщения(команды)
        try {
            //инициируем из объекта сообщения объект команды
            CommandMessage commandMessage = (CommandMessage) msgObject;
            //если это не команда на авторизацию
            if(commandMessage.getCommand() != Commands.REQUEST_SERVER_AUTH){
                return;
            }
            //вызываем метод обработки запроса от клиента
            onAuthClientRequest(ctx, commandMessage);
        } finally {
            ReferenceCountUtil.release(msgObject);
        }
    }

    /**
     * Метод обрабатывает полученный от клиента запрос на авторизацию в облачное хранилище
     * @param ctx - объект соединения netty, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onAuthClientRequest(ChannelHandlerContext ctx, CommandMessage commandMessage) {
        //вынимаем объект авторизационного сообщения из объекта сообщения(команды)
        AuthMessage authMessage = (AuthMessage) commandMessage.getMessageObject();

        //TODO temporarily
        printMsg("[server]AuthGateway.onAuthClientRequest() - " +
                "login: " + authMessage.getLogin() + ", password: " + authMessage.getPassword());

        //если авторизации клиента в облачном хранилище прошла удачно
        if(usersAuthController.authorizeUser(authMessage)){
            //меняем команду на успешную
            command = Commands.SERVER_RESPONSE_AUTH_OK;

            //TODO проверить как будут влиять несколько клиентов друг на друга
            //добавляем логин пользователя(имя его папки в сетевом хранилище)
            // к корневой директории клиента по умолчанию
            Path userStorageRoot = storageServer.getStorageRoot();
            userStorageRoot = userStorageRoot.resolve(authMessage.getLogin());

//            //инициируем объект сообщения о директории
//            DirectoryMessage directoryMessage = new DirectoryMessage();
//
//            //формируем список файлов и папок в корневой директории клиента по умолчанию//TODO turn String into Path
//            directoryMessage.composeFilesAndFoldersNamesList(usersAuthController.getUserStorageRoot().toString());
//            //инициируем новый объект сообщения(команды)
//            commandMessage = new CommandMessage(command, directoryMessage);
//
//            //удаляем этот хэндлер из конвеера//TODO check!
//            ctx.channel().pipeline().remove(this);
            ctx.fireChannelRead(new CommandMessage(command, userStorageRoot));

        //если авторизации клиента в облачном хранилище не прошла
        } else {
            //инициируем переменную типа команды - ответ об ошибке
            //в этом случае, в объекте сообщения(команды) вернем принятый от клиента объект авторизационного сообщения
            command = Commands.SERVER_RESPONSE_AUTH_ERROR;
            //инициируем новый объект сообщения(команды)
            commandMessage = new CommandMessage(command, authMessage);
        }

        //отправляем объект сообщения(команды) клиенту
//        server.sendToClient(tcpConnection, new CommandMessage(command, directoryMessage));//TODO
        ctx.writeAndFlush(commandMessage);

        //TODO temporarily
        printMsg("[server]AuthGateway.onAuthClientRequest() - ctx: " + ctx +
                ", command: " + commandMessage.getCommand());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) /*throws Exception*/ {
        cause.printStackTrace();
        ctx.close();
    }

    public void printMsg(String msg){
        storageServer.printMsg(msg);
    }
}

//TODO DELETE!
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

//            if(clientID.equals(NettyServer.Enum.UNKNOWN_USER.name())){
//                //если полученное от клиента сообщение это запрос на авторизацию в облачное хранилище
//                if(commandMessage.getCommand() == Commands.REQUEST_SERVER_AUTH){
//                    //вызываем метод обработки запроса от клиента
//                    onAuthClientRequest(ctx, tcpConnection, commandMessage);
//                }
//            }

//            //распознаем и обрабатываем полученный объект сообщения(команды)
//            commandMessageManager.recognizeAndArrangeMessageObject(ctx, commandMessage);
