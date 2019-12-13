package utils.handlers;

import messages.AuthMessage;
import tcp.TCPConnection;
import tcp.TCPServer;

import java.util.ArrayList;

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
        if(checkLoginAndPassword(server, authMessage.getLogin(), authMessage.getPassword())){
            //устанавливаем соответствие текущего соединения клиента с логином пользователя
            if(determineClient(server, authMessage.getPort(), authMessage.getLogin())){
                return true;
            }
        }
        return false;
    }

    //FIXME
    //Заготовка метода для проверки релевантности пары логина и пароля
    private boolean checkLoginAndPassword(TCPServer server, String login, String password) {
        //FIXME запросить jdbс проверить логин и пароль
        //если порт соединения совпадает с портом полученного объекта авторизационного запроса
        if(login.equals("login1") && password.equals("pass1")){//FIXME
            return true;
        }
        return false;
    }

    //FIXME Заготовка метода
    //устанавливаем соответствие текущего соединения клиента с логином пользователя
    private boolean determineClient(TCPServer server, int port, String login) {
        ArrayList<TCPConnection> connections = server.getConnections();

        //листаем список активных соединений
        for (int i = 0; i < connections.size(); i++) {

            server.printMsg("TCPServer.determineClient() - connections.get(i).getSocket().getLocalPort(): " +
                    connections.get(i).getSocket().getPort());

            //если порт соединения совпадает с портом полученного объекта авторизационного запроса
            if(connections.get(i).getSocket().getPort() == port){//FIXME
                //записываем логин клиента как идентификатор клиента
                connections.get(i).setClientID(login);
                return true;
            }
        }
        return false;
    }
}
