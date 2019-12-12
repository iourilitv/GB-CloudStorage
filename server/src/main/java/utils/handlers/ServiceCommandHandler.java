package utils.handlers;

import messages.AuthMessage;
import tcp.TCPServer;

/**
 * The server class for operating with service command messages.
 */
public class ServiceCommandHandler extends CommandHandler{
    //принимаем объект авторизационного сообщения
    private AuthMessage authMessage;

    public ServiceCommandHandler(AuthMessage authMessage) {
        this.authMessage = authMessage;
    }

    public AuthMessage getAuthMessage() {
        return authMessage;
    }

    /**
     * Метод обработки авторизации клиента в сетевом хранилище
     * @param server - объект сервера
     * @param authMessage - объект авторизационного сообщения
     * @return true, если авторизация прошла успешно
     */
    public boolean authorizeUser(TCPServer server, AuthMessage authMessage){

        //FIXME fill me!
        server.printMsg("(Server)ServiceCommandHandler.authorizeUser - authMessage.getLogin(): " +
                authMessage.getLogin() + ". authMessage.getPassword(): " + authMessage.getPassword());

        //проверяем релевантность пары логина и пароля
        if(server.checkLoginAndPassword(authMessage.getLogin(), authMessage.getPassword())){
            //устанавливаем соответствие текущего соединения клиента с логином пользователя
            if(server.determineClient(authMessage.getPort(), authMessage.getLogin())){
                return true;
            };
        }
        return false;
    }
}
