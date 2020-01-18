package jdbc;

import io.netty.channel.ChannelHandlerContext;
import messages.AuthMessage;
import control.CloudStorageServer;

import java.sql.*;

/**
 * Класс для организации сервиса авторизации и связи с БД
 * Связь БД и приложения осуществляется через посредника, JDBC драйвер(библиотека).
 */
public class UsersAuthController {
    //инициируем объект класса
    private static UsersAuthController ounInstance = new UsersAuthController();

    //принимаем объект сервера
    private static CloudStorageServer storageServer;

//    public UsersAuthController(CloudStorageServer storageServer) {
//        this.storageServer = storageServer;
//    }

    public static UsersAuthController getOunInstance(CloudStorageServer storageServer) {
        UsersAuthController.storageServer = storageServer;
        return ounInstance;
    }

    //объект для установления связи
    private static Connection connection;
    //объект для отправки запросов в JDBC драйвер(библиотека) с помощью метода connect(),
    // который переправляет его в БД.
    // И получает результат (объект класса ResultSet) с помощью executeQuery(sql)
    private static Statement stmt;

    /**
     * Метод подключения к БД.
     * //@throws SQLException
     */
    public void connect() throws SQLException {
        try {
            // обращение к драйверу. просто инициализирует класс, с которым потом будем работать
            Class.forName("org.sqlite.JDBC");
            // установка подключения
            connection = DriverManager.getConnection("jdbc:sqlite:cloudStorageDB.db");
            // создание Statement для возможности отправки запросов
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public boolean registerUser(ChannelHandlerContext ctx, AuthMessage authMessage) {
        return true;
    }

    /**
     * Метод обработки авторизации клиента в сетевом хранилище.
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
            //TODO перенести их storageServer в UsersAuthController
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

    //Метод проверки введенных логина в БД на уникальность
    public boolean checkLoginInDB(String login) {
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
     * Метод отключения от БД
     */
    public void disconnect() {
        try {
            // закрываем соединение
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printMsg(String msg){
        storageServer.printMsg(msg);
    }


//    //Метод проверки есть ли уже таблица черного списка у пользователя в БД
//    //если есть строка возвращаем результат, если нет, то вернеться null
//    public static String getUserBlacklistNameByNicknameInDB(String nickOfOwner) {
//        // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
//        String sql = String.format("SELECT name_blacklists FROM main where nickname = '%s'", nickOfOwner);
//        try {
//            // оправка запроса и получение ответа из БД
//            ResultSet rs = stmt.executeQuery(sql);
//            // если есть строка возвращаем результат, если нет, то вернеться null
//            if(rs.next()) {
//                return rs.getString(1);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    //Метод создания в БД таблицы с черным списком пользователя
//    //если таблица создана успешно, возвращает true
//    public static boolean createUserBlacklistInDB(String nickOfOwner){
//        //формируем имя таблицы с черным списком пользователя
//        String nameOfBlacklist = nickOfOwner + "blacklist";
//        //создаем новую таблицу для черного списка пользователя
//        // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
//        String sql1 = String.format("CREATE TABLE %s ( nickname REFERENCES main (nickname) );", nameOfBlacklist);
//        //добавляем имя таблицы черного списка в строку пользователя
//        String sql2 = String.format("UPDATE main SET name_blacklists = '%s' WHERE nickname = '%s'", nameOfBlacklist, nickOfOwner);
//
//        try {
//            // оправка запроса и получение ответа из БД
//            int rs = stmt.executeUpdate(sql1 + sql2);
//
//            // если таблица создана, то возвращается 1, если нет, то вернеться 0?
//            if(rs != 0) {
//                return true;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

//    //Метод добавления имени в таблицу черного списка пользователя в БД
//    //если строка добавлена, то возвращается true
//    public static boolean addNicknameIntoBlacklistInDB(String nickOfOwner, String nickname){
//        //находим имя таблицы черного списка по имени его владельца
//        String nameOfBlacklistTable = getUserBlacklistNameByNicknameInDB(nickOfOwner);
//        //добавляем в таблицу черного списка имя пользователя
//        // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
//        String sql = String.format("INSERT INTO %s (nickname) VALUES ('%s')", nameOfBlacklistTable, nickname);
//
//        try {
//            // оправка запроса и получение ответа из БД
//            int rs = stmt.executeUpdate(sql);
//
//            // если строка добавлена, то возвращается 1, если нет, то вернеться 0?
//            if(rs != 0) {
//                return true;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

//    //Метод проверки имени в таблице черного списка пользователя в БД
//    //если такое имя есть, то возвращается true
//    public static boolean checkUserInBlacklistDB(String nickOfOwner, String nickname){
//        //находим имя таблицы черного списка по имени его владельца
//        String nameOfBlacklistTable = getUserBlacklistNameByNicknameInDB(nickOfOwner);
//        //проверяем есть вообще черный список
//        if(nameOfBlacklistTable == null){
//            //возвращаем false, если нет таблицы
//            return false;
//        }
//        //ищем имя пользователя в таблице черного списка
//        // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
//        String sql = String.format("SELECT nickname FROM %s WHERE nickname = '%s'", nameOfBlacklistTable, nickname);
//
//        try {
//            // оправка запроса и получение ответа из БД
//            ResultSet rs = stmt.executeQuery(sql);
//
//            // если есть строка возвращаем результат, если нет, то вернеться null
//            if(rs.next()) {
//                return (rs.getString(1)).equals(nickname);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

//    //Метод удаления имени из таблицы черного списка пользователя в БД
//    //если строка удалена, то возвращается true
//    public static boolean deleteUserFromBlacklistDB(String nickOfOwner, String nickname){
//        //находим имя таблицы черного списка по имени его владельца
//        String nameOfBlacklistTable = getUserBlacklistNameByNicknameInDB(nickOfOwner);
//
//        // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
//        //удаляем имя пользователя из таблицы черного списка
//        String sql = String.format("DELETE FROM %s WHERE nickname = '%s'", nameOfBlacklistTable, nickname);
//        try {
//            // оправка запроса и получение ответа из БД
//            int rs = stmt.executeUpdate(sql);
//
//            // если строка удалена, то возвращается 1, если нет, то вернеться 0?
//            if(rs != 0) {
//                return true;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }




//    /**
//     * Метод запрашивающий в БД nickname по совпадению логина и пароля.
//     * @param login - логин
//     * @param pass - пароль
//     * @return значение колонки nickname, если сопадение
//     */
//    public static String getNickByLoginAndPass(String login, String pass) {
//        // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
//        String sql = String.format("SELECT nickname FROM main where login = '%s' and password = '%s'", login, pass);
//
//        try {
//            // оправка запроса и получение ответа из БД
//            ResultSet rs = stmt.executeQuery(sql);
//
//            // если есть строка возвращаем результат, если нет, то вернеться null
//            if(rs.next()) {
//                //индекс колонки в запросе (здесь 1 - это nickname). Но индексация в БД начинается с 1
//                //можно также вызвать и по columnLabel (здесь было бы "nickname"), но по индексу быстрее
//                return rs.getString(1);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


}

