package utils;

import io.netty.channel.ChannelHandlerContext;
import jdbc.UsersDB;
import messages.AuthMessage;

/**
 * This server's class for operating with client registration and authentication.
 */
public class UsersAuthController {
    //принимаем объект сервера
    private CloudStorageServer storageServer;

    public UsersAuthController(CloudStorageServer storageServer) {
        this.storageServer = storageServer;
    }

    /**
     * Метод обработки авторизации клиента в сетевом хранилище
     * @param authMessage - объект авторизационного сообщения
     * @return true, если авторизация прошла успешно
     */
    public boolean authorizeUser(ChannelHandlerContext ctx, AuthMessage authMessage){
        //если пользователь уже авторизован
        if(isUserAuthorized(ctx, authMessage.getLogin())){
            //выводим сообщение в консоль
            printMsg("[server]UsersAuthController.authorizeUser - This user has been authorised already!");
            //и выходим с false
            return false;
        }

        //если пара логина и пароля релевантна
        if(checkLoginAndPassword(authMessage.getLogin(), authMessage.getPassword())){
            //регистрируем пользователя, если он еще не зарегистрирован
            storageServer.getAuthorizedUsers().put(ctx, authMessage.getLogin());

            //TODO temporarily
            printMsg("[server]UsersAuthController.authorizeUser - authorizedUsers: " +
                    storageServer.getAuthorizedUsers().toString());

            //возвращаем true, чтобы завершить процесс регистрации пользователя
            return true;
        }
        return false;
    }

    private boolean isUserAuthorized(ChannelHandlerContext ctx, String login) {
        //возвращаем результат проверки есть ли уже элемент в списке авторизованных с такими
        // объектом соединения или логином
        return storageServer.getAuthorizedUsers().containsKey(ctx) ||
                storageServer.getAuthorizedUsers().containsValue(login);
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

}