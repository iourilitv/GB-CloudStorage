package jdbc;

import io.netty.channel.ChannelHandlerContext;
import messages.AuthMessage;
import control.CloudStorageServer;
import java.sql.*;
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
    //объявляем множество авторизованных клиентов <соединение, логин>
    private Map<ChannelHandlerContext, String> authorizedUsers;
//    //объявляем объект соединения с БД
//    private Connection connection;
    // !_ note _! this is just init
    // it will not create a connection
    MySQLConnect mysqlConnect = new MySQLConnect();

    //объект для отправки запросов в JDBC драйвер(библиотека) с помощью метода connect(),
    // который переправляет его в БД.
    // И получает результат (объект класса ResultSet) с помощью executeQuery(sql)
    private Statement stmt;

//    /**
//     * Метод подключения к БД.
//     * //@throws SQLException
//     */
//    public void connect() throws SQLException {
//        try {
//            // обращение к драйверу. просто инициализирует класс, с которым потом будем работать
//            Class.forName("org.sqlite.JDBC");
//            // установка подключения
//            connection = DriverManager.getConnection("jdbc:sqlite:cloudStorageDB.db");
//            // создание Statement для возможности отправки запросов
//            stmt = connection.createStatement();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Метод-прокладка запускает процесс регистрации нового пользователя в БД
     * @param ctx - сетевое соединение
     * @param authMessage - объект авторизационного сообщения
     * @return - результат операции регистрации в БД
     */
    public boolean registerUser(ChannelHandlerContext ctx, AuthMessage authMessage) {
        //если пользователь с таким логином уже зарегистрирован в БД
//        if(isUserRegistered(authMessage.getLogin())){//TODO временно деактивировано
//            //выводим сообщение в консоль
//            printMsg("[server]UsersAuthController.registerUser() - A user with this login has been registered already!");
//            //и выходим с false
//            return false;
//        }
        //если регистрация нового пользователя в БД прошла не удачно//TODO временно деактивировано
//        if(!addUserIntoDB(authMessage.getLogin(), authMessage.getPassword())){
//            //выводим сообщение в консоль
//            printMsg("[server]UsersAuthController.authorizeUser() - This user has not been registered yet!");
//            //и выходим с false
//            return false;
//        }
        //если создание конрневой директории для нового пользователяпрошла не удачно
        if(!storageServer.createNewUserRootFolder(authMessage.getLogin())){
            //выводим сообщение в консоль
            printMsg("[server]UsersAuthController.authorizeUser() - This user's root directory exists already!");
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
    public boolean authorizeUser(ChannelHandlerContext ctx, AuthMessage authMessage){

        printMsg("UsersAuthController.getUsersListFromDB() return: " + getUsersListFromDB());

//        createNewTable("users");

        //если пользователь еще не зарегистрирован в БД
        if(!isUserRegistered(authMessage.getLogin())){//TODO временно деактивировано
            //выводим сообщение в консоль
            printMsg("[server]UsersAuthController.authorizeUser() - This user has not been registered yet!");
            //и выходим с false
            return false;
        }

        //если пользователь уже авторизован
        if(isUserAuthorized(ctx, authMessage.getLogin())){
            //выводим сообщение в консоль
            printMsg("[server]UsersAuthController.authorizeUser - This user has been authorised already!");
            //и выходим с false
            return false;
        }
        //авторизуем пользователя, если он еще не авторизован
        authorizedUsers.put(ctx, authMessage.getLogin());
        return true;
    }

    private boolean createNewTable(String users) {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS warehouses (\n"
                + "    id integer PRIMARY KEY,\n"
                + "    name text NOT NULL,\n"
                + "    capacity real\n"
                + ");";

        try {
            // оправка запроса и получение ответа из БД
            ResultSet rs = stmt.executeQuery(sql);

            // если есть строка возвращаем результат, если нет, то вернеться null
            if(!rs.next()) {
                //таких логина или ник в БД нет
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Метод добавления данных пользователя в БД
    public boolean addUserIntoDB(String login, String password){
        // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
        //записываем данные нового юзера в БД
        String sql = String.format("INSERT INTO main (login, password) VALUES ('%s', '%s')", login, password);

        try {
            // оправка запроса и получение ответа из БД
            int rs = stmt.executeUpdate(sql);

            // если строка добавлена, то возвращается 1, если нет, то вернеться 0?
            if(rs != 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Метод проверки введенного логина в БД на уникальность(зарегистрирован уже такой логин?)
    public boolean isUserRegistered(String login) {
        // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
        String sql = String.format("SELECT id FROM main where login = '%s'", login);
        try {
            // оправка запроса и получение ответа из БД
            ResultSet rs = stmt.executeQuery(sql);

            // если есть строка возвращаем результат, если нет, то вернеться null
            if(!rs.next()) {
                //таких логина или ник в БД нет
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Метод проверяет не авторизован ли уже пользовательс таким логином.
     * @param ctx - сетевое соединение
     * @param login - логин пользователя
     * @return - результат проверки
     */
    private boolean isUserAuthorized(ChannelHandlerContext ctx, String login) {
        //возвращаем результат проверки есть ли уже элемент в списке авторизованных с такими
        // объектом соединения или логином
        return authorizedUsers.containsKey(ctx) || authorizedUsers.containsValue(login);
    }

    public boolean getUsersListFromDB() {
        String sql = "SELECT * FROM `users`";

        try {
            stmt = mysqlConnect.connect().prepareStatement(sql);
            // оправка запроса и получение ответа из БД
            ResultSet rs = stmt.executeQuery(sql);

            // если есть строка возвращаем результат, если нет, то вернеться null
            if(!rs.next()) {
                //таких логина или ник в БД нет
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            mysqlConnect.disconnect();
        }


        // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
//        String sql = "SELECT * FROM main";
//        String sql = String.format("SELECT * FROM '%s'", "main");

//        try {
//            // оправка запроса и получение ответа из БД
//            ResultSet rs = stmt.executeQuery(sql);
//
//            // если есть строка возвращаем результат, если нет, то вернеться null
//            if(!rs.next()) {
//                //таких логина или ник в БД нет
//                return true;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return false;
    }

//    /**
//     * Метод отключения от БД
//     */
//    public void disconnect() {
//        try {
//            // закрываем соединение
//            connection.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    public void printMsg(String msg){
        storageServer.printMsg(msg);
    }

    public Map<ChannelHandlerContext, String> getAuthorizedUsers() {
        return authorizedUsers;
    }

}

