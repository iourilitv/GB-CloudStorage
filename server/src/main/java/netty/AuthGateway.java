package netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import messages.AuthMessage;
import control.CloudStorageServer;
import utils.CommandMessage;
import utils.Commands;
import jdbc.UsersAuthController;

/**
 * This server's class responds for client authentication.
 * It discards not Auth commandMessages from not authorized clients.
 */
public class AuthGateway extends ChannelInboundHandlerAdapter {
    //принимаем объект соединения
    private ChannelHandlerContext ctx;
    //принимаем объект контроллера сетевого хранилища
    private final CloudStorageServer storageServer;
    //принимаем объект контроллера авторизации клиента
    private UsersAuthController usersAuthController;
    //объявляем переменную типа команды
    private Commands command;

    public AuthGateway(CloudStorageServer storageServer) {
        this.storageServer = storageServer;
        //принимаем объект контроллера авторизации пользователей
        usersAuthController = storageServer.getUsersAuthController();
    }

     /**
     * Метот обрабатывает событие - установление соединения с клиентом.
     * По событию отправляет сообщение-уведомление клиенту.
     * @param ctx - объект соединения netty, установленного с клиентом
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //принимаем объект соединения
        this.ctx = ctx;
        //если соединение установлено, отправляем клиенту сообщение
        ctx.writeAndFlush(new CommandMessage(Commands.SERVER_NOTIFICATION_CLIENT_CONNECTED));

        //TODO Upd 21. Добавить в лог.
        printMsg("[server]AuthGateway.channelActive() - ctx: " + ctx +
                ", command: " + Commands.SERVER_NOTIFICATION_CLIENT_CONNECTED);
    }

    /**
     * Метот обрабатывает событие - разрыв соединения с клиентом
     * @param ctx - объект соединения netty, установленного с клиентом
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //если соединение отвалилось, удаляем объект соединения из коллекции
        usersAuthController.deAuthorizeUser(ctx);

        //TODO Upd 21. Добавить в лог.
        printMsg("[server]UsersAuthController.authorizeUser - authorizedUsers: " +
                usersAuthController.getAuthorizedUsers().toString());
    }

    /**
     * Метод обрабатывает событие - получение десериализованного объекта.
     * Инициирует объект команды из объекта сообщения и обрабатывает только
     * запрос от клиента на авторизацию.
     * @param ctx - объект соединения netty, установленного с клиентом
     * @param msgObject - десериализованный объект сообщения
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msgObject) {
        try {
            //инициируем объект команды из объекта сообщения
            CommandMessage commandMessage = (CommandMessage) msgObject;
            //если это команда на отсоединение клиента от сервера
            //в авторизованном режиме
            if(commandMessage.getCommand() == Commands.REQUEST_SERVER_DISCONNECT){
                //вызываем метод обработки запроса от клиента
                onDisconnectClientRequest(commandMessage);
                //если это команда на регистрацию нового пользователя в сетевом хранилище
            } else if(commandMessage.getCommand() == Commands.REQUEST_SERVER_REGISTRATION){
                //вызываем метод обработки запроса от клиента
                onRegistrationUserClientRequest(ctx, commandMessage);
                //если это команда на авторизацию пользователя в сетевом хранилище
            } else if(commandMessage.getCommand() == Commands.REQUEST_SERVER_AUTH){
                //вызываем метод обработки запроса от клиента
                onAuthClientRequest(ctx, commandMessage);
            }
        } finally {
            ReferenceCountUtil.release(msgObject);
        }
    }

    /**
     * Метод обрабатывает полученный от клиента запрос на отсоединение пользователя
     * от сервера в НЕ авторизованном режиме.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDisconnectClientRequest(CommandMessage commandMessage) {
        //отправляем объект сообщения(команды) клиенту
        ctx.writeAndFlush(new CommandMessage(Commands.SERVER_RESPONSE_DISCONNECT_OK));

        //TODO Upd 21. Добавить в лог.
        printMsg("[server]AuthGateway.onDisconnectClientRequest() - " +
                "Unauthorized client has been disconnected! ctx : " + ctx);

        //закрываем соединение с клиентом(вроде на ctx.close(); не отключал соединение?)
        ctx.channel().close();
    }

    /**
     * Метод обрабатывает полученный от клиента запрос на регистрацию
     * нового пользователя в облачное хранилище.
     * @param ctx - объект соединения netty, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onRegistrationUserClientRequest(ChannelHandlerContext ctx,
                                                 CommandMessage commandMessage) {
        //вынимаем объект авторизационного сообщения из объекта сообщения(команды)
        AuthMessage authMessage = (AuthMessage) commandMessage.getMessageObject();
        //если регистрация клиента в облачном хранилище прошла удачно
        if(usersAuthController.registerUser(authMessage)){
            //меняем команду на успешную
            command = Commands.SERVER_RESPONSE_REGISTRATION_OK;
        //если регистрация клиента в облачном хранилище не прошла
        } else {
            //инициируем переменную типа команды - ответ об ошибке
            //в этом случае, в объекте сообщения(команды) вернем принятый от клиента объект авторизационного сообщения
            command = Commands.SERVER_RESPONSE_REGISTRATION_ERROR;
        }

        //TODO Upd 21. Добавить в лог.
        printMsg("[server]AuthGateway.onRegistrationUserClientRequest() - ctx: " + ctx +
                ", command: " + commandMessage.getCommand());

        //инициируем новый объект сообщения(команды)
        commandMessage = new CommandMessage(command, authMessage);
        //отправляем объект сообщения(команды) клиенту
        ctx.writeAndFlush(commandMessage);
    }

    /**
     * Метод обрабатывает полученный от клиента запрос на авторизацию в облачное хранилище
     * @param ctx - объект соединения netty, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onAuthClientRequest(ChannelHandlerContext ctx, CommandMessage commandMessage) {
        //вынимаем объект авторизационного сообщения из объекта сообщения(команды)
        AuthMessage authMessage = (AuthMessage) commandMessage.getMessageObject();
        //если есть директория с именем логина пользователя и
        // авторизации клиента в облачном хранилище прошла удачно
        if(usersAuthController.isUserRootDirExist(authMessage.getLogin()) &&
                usersAuthController.authorizeUser(authMessage, ctx)){
            //меняем команду на успешную
            command = Commands.SERVER_RESPONSE_AUTH_OK;
            //пробрасываем дальше объект сообщения об успешной авторизации клиента
            ctx.fireChannelRead(new CommandMessage(command, authMessage.getLogin()));
        //если авторизации клиента в облачном хранилище не прошла или
        // директория с именем логина отсутствует
        } else {
            //инициируем переменную типа команды - ответ об ошибке
            //в этом случае, в объекте сообщения(команды) вернем принятый от клиента объект авторизационного сообщения
            command = Commands.SERVER_RESPONSE_AUTH_ERROR;
            //инициируем новый объект сообщения(команды)
            commandMessage = new CommandMessage(command, authMessage);
            //отправляем объект сообщения(команды) клиенту
            ctx.writeAndFlush(commandMessage);

            //TODO Upd 21. Добавить в лог.
            printMsg("[server]AuthGateway.onAuthClientRequest() - ctx: " + ctx +
                    ", command: " + commandMessage.getCommand());
        }
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