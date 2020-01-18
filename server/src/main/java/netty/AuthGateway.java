package netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import messages.AuthMessage;
import control.CloudStorageServer;
import utils.CommandMessage;
import utils.Commands;
import jdbc.UsersAuthController;

import java.nio.file.Path;

/**
 * This server's class responds for client authentication.
 * It discards not Auth commandMessages from not authorized clients.
 */
public class AuthGateway extends ChannelInboundHandlerAdapter {
    //принимаем объект контроллера сетевого хранилища
    private final CloudStorageServer storageServer;
    //принимаем объект контроллера авторизации клиента
    private UsersAuthController usersAuthController;
    //объявляем переменную типа команды
    private int command;

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
        //если соединение установлено, отправляем клиенту сообщение
        ctx.writeAndFlush(new CommandMessage(Commands.SERVER_NOTIFICATION_CLIENT_CONNECTED));

        //TODO temporarily
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
        printMsg("[server]AuthGateway.channelInactive() - removed client(login): " +
                storageServer.getAuthorizedUsers().remove(ctx));

        //TODO temporarily
        printMsg("[server]UsersAuthController.authorizeUser - authorizedUsers: " +
                storageServer.getAuthorizedUsers().toString());
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
            //если это команда на регистрацию нового пользователя в сетевом хранилище
            if(commandMessage.getCommand() == Commands.REQUEST_SERVER_REGISTRATION){
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

    private void onRegistrationUserClientRequest(ChannelHandlerContext ctx, CommandMessage commandMessage) {
        //вынимаем объект авторизационного сообщения из объекта сообщения(команды)
        AuthMessage authMessage = (AuthMessage) commandMessage.getMessageObject();

        //TODO temporarily
        printMsg("[server]AuthGateway.onRegistrationUserClientRequest() - " +
                "login: " + authMessage.getLogin() + ", password: " + authMessage.getPassword());

        //если регистрация клиента в облачном хранилище прошла удачно
        if(usersAuthController.registerUser(ctx, authMessage)){
            //меняем команду на успешную
            command = Commands.SERVER_RESPONSE_REGISTRATION_OK;
        //если регистрация клиента в облачном хранилище не прошла
        } else {
            //инициируем переменную типа команды - ответ об ошибке
            //в этом случае, в объекте сообщения(команды) вернем принятый от клиента объект авторизационного сообщения
            command = Commands.SERVER_RESPONSE_REGISTRATION_ERROR;
        }

        //TODO temporarily
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

        //TODO temporarily
        printMsg("[server]AuthGateway.onAuthClientRequest() - " +
                "login: " + authMessage.getLogin() + ", password: " + authMessage.getPassword());

        //если авторизации клиента в облачном хранилище прошла удачно
        if(usersAuthController.authorizeUser(ctx, authMessage)){
            //меняем команду на успешную
            command = Commands.SERVER_RESPONSE_AUTH_OK;

            //TODO проверить как будут влиять несколько клиентов друг на друга
            //добавляем логин пользователя(имя его папки в сетевом хранилище)
            // к корневой директории клиента по умолчанию
            Path userStorageRoot = storageServer.getSTORAGE_ROOT_PATH();
            userStorageRoot = userStorageRoot.resolve(authMessage.getLogin());
            //пробрасываем дальше объект сообщения об успешной авторизации клиента
            ctx.fireChannelRead(new CommandMessage(command, userStorageRoot.toString()));
        //если авторизации клиента в облачном хранилище не прошла
        } else {
            //инициируем переменную типа команды - ответ об ошибке
            //в этом случае, в объекте сообщения(команды) вернем принятый от клиента объект авторизационного сообщения
            command = Commands.SERVER_RESPONSE_AUTH_ERROR;
            //инициируем новый объект сообщения(команды)
            commandMessage = new CommandMessage(command, authMessage);
            //отправляем объект сообщения(команды) клиенту
            ctx.writeAndFlush(commandMessage);

            //TODO temporarily
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