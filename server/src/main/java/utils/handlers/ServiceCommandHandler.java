package utils.handlers;

import jdbc.UsersDB;
import messages.AuthMessage;
import tcp.TCPConnection;
import tcp.TCPServer;

/**
 * The server class for operating with service command messages.
 */
public class ServiceCommandHandler extends AbstractCommandHandler {

    /**
     * Метод обработки авторизации клиента в сетевом хранилище
     * @param server - объект сервера
     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param authMessage - объект авторизационного сообщения
     * @return true, если авторизация прошла успешно
     */
    public boolean authorizeUser(TCPServer server, TCPConnection tcpConnection, AuthMessage authMessage){

        //FIXME fill me!
        server.printMsg("(Server)ServiceCommandHandler.authorizeUser - authMessage.getLogin(): " +
                authMessage.getLogin() + ". authMessage.getPassword(): " + authMessage.getPassword());

        //проверяем релевантность пары логина и пароля
        if(checkLoginAndPassword(server, authMessage.getLogin(), authMessage.getPassword())){
            //записываем логин пользователя как идентификатор клиента
            tcpConnection.setClientID(authMessage.getLogin());
            return true;
        }
        return false;
    }

    /** //FIXME
     * Заготовка метода для проверки релевантности пары логина и пароля
     * @param server - объект сервера
     * @param login - полученный логин пользователя
     * @param password - полученный пароль пользователя
     * @return true, если проверка пары прошла успешно
     */
    private boolean checkLoginAndPassword(TCPServer server, String login, String password) {
        //FIXME запросить jdbс проверить логин и пароль
        //если порт соединения совпадает с портом полученного объекта авторизационного запроса
        //листаем массив пар логинов и паролей
        for (int i = 0; i < UsersDB.users.length; i++) {
            //если нашли соответствующую пару логина и пароля
            if(login.equals(UsersDB.users[i][0]) && password.equals(UsersDB.users[i][1])){
                return true;
            }
        }
        return false;
    }

}
