package utils.handlers;

import jdbc.UsersDB;
import messages.AuthMessage;
import tcp.NettyServer;
import tcp.TCPConnection1;
import utils.CloudStorageServer;

import java.nio.file.Path;

/**
 * This server's class for operating with client registration and authentication.
 */
public class UsersAuthController {

    public enum Enum {
        //инициируем константу неавторизованного подключившегося пользователя
        UNKNOWN_USER;
    }

    //принимаем объект сервера
    private CloudStorageServer storageServer;

//    //инициируем переменную идентификатора подсоединенного клиента
//    private String clientID = Enum.UNKNOWN_USER.name();
//    //объявляем переменную для корневой директории пользователя в сетевом хранилище
//    private Path userStorageRoot;

    public UsersAuthController(CloudStorageServer storageServer) {
        this.storageServer = storageServer;

//        //инициируем переменную для корневой директории пользователя в сетевом хранилище
//        userStorageRoot = server.getStorageRoot();
    }

    /**
     * Метод обработки авторизации клиента в сетевом хранилище
     * @param authMessage - объект авторизационного сообщения
     * @return true, если авторизация прошла успешно
     */
    public boolean authorizeUser(AuthMessage authMessage){

        //FIXME fill me!
//        server.printMsg("[Server]ClientController.authorizeUser - authMessage.getLogin(): " +
//                authMessage.getLogin() + ". authMessage.getPassword(): " + authMessage.getPassword());

//        //проверяем релевантность пары логина и пароля
//        if(checkLoginAndPassword(authMessage.getLogin(), authMessage.getPassword())){
//            //если такой логин еще не подключен
//            if(clientID.equals(Enum.UNKNOWN_USER.name())){
//                //записываем логин пользователя как идентификатор клиента
//                setClientID(authMessage.getLogin());
//
//                //TODO temporarily
//                printMsg("[Server]ClientController.authorizeUser - clientID: " + clientID);
//
//                //устанавливаем корневую директорию пользователя в сетевом хранилище
//                //добавляем логин пользователя к корневой директории сетевого хранилища
//                userStorageRoot = userStorageRoot.resolve(clientID);
//
//                //TODO temporarily
//                printMsg("[Server]ClientController.authorizeUser - new userStorageRoot: " + userStorageRoot);
//
//                return true;
//            }
//        }
//        return false;
        //возвращаем результат проверки релевантности пары логина и пароля
        return checkLoginAndPassword(authMessage.getLogin(), authMessage.getPassword());
    }

    /** //FIXME
     * Заготовка метода для проверки релевантности пары логина и пароля
     * @param login - полученный логин пользователя
     * @param password - полученный пароль пользователя
     * @return true, если проверка пары прошла успешно
     */
    private boolean checkLoginAndPassword(String login, String password) {
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

    public void printMsg(String msg){
        storageServer.printMsg(msg);
    }

//    public String getClientID() {
//        return clientID;
//    }

//    public Path getUserStorageRoot() {
//        return userStorageRoot;
//    }

//    public void setClientID(String clientID) {
//        this.clientID = clientID;
//    }
}
