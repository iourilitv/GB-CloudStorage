package jdbc;

import io.netty.channel.ChannelHandlerContext;
import messages.AuthMessage;
import control.CloudStorageServer;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для организации сервиса авторизации и связи с БД
 * Связь БД и приложения осуществляется через посредника, JDBC драйвер(библиотека).
 */
public class UsersAuthController {
    //инициируем объект класса
    private static UsersAuthController ounInstance = new UsersAuthController();

    public static UsersAuthController getOunInstance(CloudStorageServer storageServer) {
        ounInstance.storageServer = storageServer;
        //инициируем множество авторизованных клиентов
        ounInstance.authorizedUsers = new HashMap<>();
        return ounInstance;
    }

    //принимаем объект сервера
    private CloudStorageServer storageServer;
    //объявляем множество авторизованных клиентов <логин, соединение>
    private Map<String, ChannelHandlerContext> authorizedUsers;
    //инициируем объект имитации соединения с БД
    UsersDB usersDB = UsersDB.getOwnInstance();

    /**
     * Метод-прокладка запускает процесс регистрации нового пользователя в БД
     * @param authMessage - объект авторизационного сообщения
     * @return - результат операции регистрации в БД
     */
    public boolean registerUser(AuthMessage authMessage) {
        //если директория с таким логином уже есть в сетевом хранилище
        if(isUserRootDirExist(authMessage.getLogin())){
            //выводим сообщение в консоль
            printMsg("[server]UsersAuthController.registerUser() - " +
                    "A user's root directory for this login exists!");
            //и выходим с false
            return false;
        }

        //если пользователь с таким логином уже зарегистрирован в БД
        if(isUserRegistered(authMessage.getLogin())){
            //выводим сообщение в консоль
            printMsg("[server]UsersAuthController.registerUser() - " +
                    "A user with this login has been registered already!");
            //и выходим с false
            return false;
        }
        //если регистрация нового пользователя в БД прошла не удачно
        if(!addUserIntoDB(authMessage.getLogin(), authMessage.getPassword())){
            //выводим сообщение в консоль
            printMsg("[server]UsersAuthController.authorizeUser() - " +
                    "This user has not been registered yet!");
            //и выходим с false
            return false;
        }
        //если создание конрневой директории для нового пользователяпрошла не удачно
        if(!storageServer.createNewUserRootFolder(authMessage.getLogin())){
            //выводим сообщение в консоль
            printMsg("[server]UsersAuthController.authorizeUser() - " +
                    "This user's root directory exists already!");
            //и выходим с false
            return false;
        }
        return true;
    }

    /**
     * Метод обработки авторизации клиента в сетевом хранилище.
     * @param ctx - сетевое соединение
     * @param authMessage - объект авторизационного сообщения
     * @return true, если авторизация прошла успешно
     */
    public boolean authorizeUser(AuthMessage authMessage, ChannelHandlerContext ctx){
        //если пользователь еще не зарегистрирован в БД
        if(!isUserRegistered(authMessage.getLogin())){
            //выводим сообщение в консоль
            printMsg("[server]UsersAuthController.authorizeUser() - " +
                    "This user has not been registered yet!");
            //и выходим с false
            return false;
        }
        //если пользователь с таким логином уже авторизован
        if(isUserAuthorized(authMessage.getLogin(), ctx)){
            //выводим сообщение в консоль
            printMsg("[server]UsersAuthController.authorizeUser - " +
                    "This user has been authorised already!");
            //и выходим с false
            return false;
        }
        //если пара логина и пароля релевантна
        if(checkLoginAndPassword(authMessage.getLogin(), authMessage.getPassword())){
            //добавляем пользователя в список авторизованных
            authorizedUsers.put(authMessage.getLogin(), ctx);
            //возвращаем true, чтобы завершить процесс регистрации пользователя
            return true;
        }
        return false;
    }

    /**
     * Перегруженный метод удаляет клиента из списка авторизованных(по ключу), если оно было авторизовано.
     * @param login - -ключ - логин пользователя
     */
    public void deAuthorizeUser(String login) {
        //и удаляем пользователя из списка
        authorizedUsers.remove(login);
    }

    /**
     * Перегруженный метод удаляет клиента из списка авторизованных(по значению), если оно было авторизовано.
     * @param ctx - значение - сетевое соединение клиента
     */
    public void deAuthorizeUser(ChannelHandlerContext ctx) {
        //в цикле ищем ключ со значение заданого логина
        for (Map.Entry<String, ChannelHandlerContext> keys: authorizedUsers.entrySet()) {
            if(keys.getValue().equals(ctx)){
                //и удаляем его из списка
                authorizedUsers.remove(keys.getKey());
            }
        }
    }

    //Метод добавления данных пользователя в БД
    public boolean addUserIntoDB(String login, String password){
        return usersDB.addUserIntoMap(login, password);
    }

    /**
     * Метод-прокладка запускает проверку есть ли уже корневая директория для заданного логина.
     * @param login - логин нового пользователя
     * @return - результат проверки есть ли уже корневая директория для заданного логина
     */
    public boolean isUserRootDirExist(String login) {
        return storageServer.isUserRootDirExist(login);
    }

    /**
     * Метод-прокладка запускает процесс проверки введенного логина в БД на уникальность
     * (зарегистрирован уже такой логин?).
     * @param login - проверяемый логин
     * @return - результат проверки
     */
    public boolean isUserRegistered(String login) {
        return usersDB.isUserExistInMap(login);
    }

    /**
     * Метод проверяет не авторизован ли уже пользователь с таким логином.
     * @param login - логин пользователя
     * @param ctx - сетевое соединение
     * @return - результат проверки
     */
    private boolean isUserAuthorized(String login, ChannelHandlerContext ctx) {
        //возвращаем результат проверки есть ли уже элемент в списке авторизованных с такими
        // объектом соединения или логином
        return authorizedUsers.containsKey(login) || authorizedUsers.containsValue(ctx);
    }

    /**
     * Метод-прокладка для проверки релевантности пары логина и пароля
     * @param login - полученный логин пользователя
     * @param password - полученный пароль пользователя
     * @return true, если проверка пары прошла успешно
     */
    private boolean checkLoginAndPassword(String login, String password) {
        return usersDB.checkLoginAndPassword(login, password);
    }

    public void printMsg(String msg){
        storageServer.printMsg(msg);
    }

    public Map<String, ChannelHandlerContext> getAuthorizedUsers() {
        return authorizedUsers;
    }

}

