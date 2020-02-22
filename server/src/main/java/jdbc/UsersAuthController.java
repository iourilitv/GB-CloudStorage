package jdbc;

//import com.mysql.cj.jdbc.Blob;
import io.netty.channel.ChannelHandlerContext;
import messages.AuthMessage;
import control.CloudStorageServer;
import security.SecureHasher;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для организации сервиса авторизации и связи с БД
 * Связь БД и приложения осуществляется через посредника, JDBC драйвер(библиотека).
 */
public class UsersAuthController {
    //инициируем объект класса
    private static UsersAuthController ownInstance = new UsersAuthController();

    public static UsersAuthController getOwnInstance() {
        return ownInstance;
    }

    //принимаем объект сервера
    private CloudStorageServer storageServer;
    //объявляем множество авторизованных клиентов <логин, соединение>
    private Map<String, ChannelHandlerContext> authorizedUsers;
    //объявляем объект соединения с БД
//    // !_ note _! this is just init, it will not create a connection
    MySQLConnect mysqlConnect = new MySQLConnect();//TODO удалить
    Connection connection = new MySQLConnect().connect();//TODO move to init()
    //объект для отправки запросов в JDBC драйвер(библиотека) с помощью метода connect(),
    // который переправляет его в БД.
    // И получает результат (объект класса ResultSet) с помощью executeQuery(sql)
    private Statement stmt;//TODO удалить
    private PreparedStatement preparedStatement;
    //
    private final SecureHasher secureHasher = SecureHasher.getOwnInstance();

    public void init(CloudStorageServer storageServer) throws SQLException {
        ownInstance.storageServer = storageServer;
        //инициируем множество авторизованных клиентов
        ownInstance.authorizedUsers = new HashMap<>();
        //инициируем объект запроса в БД
        stmt = mysqlConnect.connect().createStatement();//TODO удалить
//        connection = new MySQLConnect().connect();
    }

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
        // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
        //записываем данные нового юзера в БД
        String sql = String.format("INSERT INTO users (login, password) VALUES ('%s', '%s')", login, password);
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
        // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
        String sql = String.format("SELECT user_id FROM users WHERE login = '%s'", login);
        try {
            // оправка запроса и получение ответа из БД
            ResultSet rs = stmt.executeQuery(sql);
            // если есть строка, то rs.next() возвращает true, если нет - false
            if(rs.next()) {
                //такой логин есть в БД
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
        // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
        String sql = String.format("SELECT user_id FROM users WHERE login = '%s' AND password = '%s'", login, password);
        try {
            // оправка запроса и получение ответа из БД
            ResultSet rs = stmt.executeQuery(sql);
            // если есть строка, то rs.next() возвращает true, если нет - false
            if(rs.next()) {
                //такая пара логина и пароля есть в БД
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

//    public boolean insertUserIntoDBSecurely(String login, String password){
//        try {
//            //TODO temporarily
//            stmt = mysqlConnect.connect().createStatement();
//
//           //
//            byte[] secure_salt = secureHasher.generateSalt();
//            byte[] secure_hash = secureHasher.generateSecureHash(password, secure_salt);
//
//            Blob blob = mysqlConnect.connect().createBlob();
//            OutputStream outputStream = blob.setBinaryStream(1);
//            outputStream.write(secure_hash);
//
//            // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
//            //записываем данные нового юзера в БД
////            String sql = String.format("INSERT INTO users (login, secure_hash, secure_salt) " +
////                    "VALUES ('%s', '%s', '%s')", login,
////                    blob.setBytes(1, secure_hash),
////                    blob.setBytes(1, secure_salt));
//            String sql = String.format("INSERT INTO users (login, secure_hash, secure_salt) " +
//                            "VALUES ('%s', '%s', '%s')", login,
//                    blob.setBytes(1, secure_hash),
//                    blob.setBytes(1, secure_salt));
//
////            String sql = String.format("UPDATE users SET secure_hash = '%s', secure_salt = '%s' " +
////                            "WHERE login = '%s'",
////                    blob.setBytes(1, secure_hash),
////                    blob.setBytes(1, secure_salt), login);
//
//            // оправка запроса и получение ответа из БД
//            int rs = stmt.executeUpdate(sql);
//            // если строка добавлена, то возвращается 1, если нет, то вернеться 0?
//            if(rs != 0) {
//                return true;
//            }
//        } catch (SQLException | InvalidKeySpecException | IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
    //1WORKS!
//    //https://www.youtube.com/watch?v=zOXCou_6jfQ
//    //Урок Java 190: JDBC 8 : BLOB - Binary large objects
//    public boolean insertUserIntoDBSecurely(String login, String password){
//        try {
////            CallableStatement callableStatement = mysqlConnect.connect().prepareCall(
////                    "INSERT INTO users2 (login, secure_hash, secure_salt) VALUES (?, ?, ?)");
//
//           //
//            byte[] secure_salt = secureHasher.generateSalt();
//            byte[] secure_hash = secureHasher.generateSecureHash(password, secure_salt);
//
//            Blob blob = mysqlConnect.connect().createBlob();
//            try(OutputStream outputStream = blob.setBinaryStream(1)){
//                outputStream.write(secure_hash);
//            };
//
//            // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
//            //записываем данные нового юзера в БД
//            //TODO не обращать внимание!
//            // работает, но IDEA выдает ошибку -
//            // не видит таблицу "user2", хотя тут же создал новую как предложение исправить ошибку
//            String sql = "INSERT INTO users2 (login, secure_hash, secure_salt) VALUES (?, ?, ?)";
//
//            PreparedStatement preparedStatement = mysqlConnect.connect().prepareStatement(sql);
//            preparedStatement.setString(1, login);
//            preparedStatement.setBinaryStream(2, blob.getBinaryStream());
//            preparedStatement.setBinaryStream(3, blob.getBinaryStream());
//            // оправка запроса и получение ответа из БД
//            int rs = preparedStatement.executeUpdate();
//            // если строка добавлена, то возвращается 1, если нет, то вернеться 0?
//            if(rs != 0) {
//                return true;
//            }
//        } catch (SQLException | InvalidKeySpecException | IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
    //2WORKS
//    //https://www.youtube.com/watch?v=hqHyCZkon34
//    // Java JDBC Tutorial – Part 10: BLOB - Reading and Writing BLOB with MySQL
//    public boolean insertUserIntoDBSecurely(String login, String password){
//        try {
//           //
//            byte[] secure_salt = secureHasher.generateSalt();
//            byte[] secure_hash = secureHasher.generateSecureHash(password, secure_salt);
//
//            // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
//            //записываем данные нового юзера в БД
////            String sql = String.format("UPDATE users SET secure_hash = ?, secure_salt = ? " +
////                            "WHERE login = '%s'", login);
//            //java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near '?, secure_salt = ? WHERE login = 'login1'' at line 1
//
//            //TODO не обращать внимание!
//            // работает, но IDEA выдает ошибку -
//            // не видит таблицу "user2", хотя тут же создал новую как предложение исправить ошибку
////            String sql = "UPDATE users2 SET secure_hash=?, secure_salt = ? " +
////                    "WHERE login = '" + login + "'";
////            //java.sql.SQLException: No value specified for parameter 2
//            String sql = "UPDATE users2 SET secure_hash=? WHERE login = '" + login + "'";
//
//            PreparedStatement preparedStatement = mysqlConnect.connect().prepareStatement(sql);
//            preparedStatement.setBinaryStream(1, new ByteArrayInputStream(secure_hash));
//            // оправка запроса и получение ответа из БД
//            int rs = preparedStatement.executeUpdate();
//            // если строка добавлена, то возвращается 1, если нет, то вернеться 0?
//            if(rs != 0) {
//                return true;
//            }
//        } catch (SQLException | InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
    //3WORKS!
    //FINAL VERSION with PreparedStatement
    public boolean insertUserIntoDBSecurely(String login, String password){
        try {
           //
            byte[] secure_salt = secureHasher.generateSalt();
            byte[] secure_hash = secureHasher.generateSecureHash(password, secure_salt);

            //записываем данные нового юзера в БД
            // формирование строки запроса PreparedStatement.
            // ? - для последовательного подставления значений в соотвествующее место
            //TODO не обращать внимание!
            // работает, но IDEA выдает ошибку -
            // не видит таблицу "users", хотя тут же создал новую как предложение исправить ошибку
            String sql = "INSERT INTO users (login, secure_hash, secure_salt) VALUES (?, ?, ?)";

//            Connection connection = mysqlConnect.connect();
//            PreparedStatement preparedStatement = mysqlConnect.connect().prepareStatement(sql);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, login);
            preparedStatement.setBinaryStream(2, new ByteArrayInputStream(secure_hash));
            preparedStatement.setBinaryStream(3, new ByteArrayInputStream(secure_salt));
            // оправка запроса и получение ответа из БД
            int rs = preparedStatement.executeUpdate();
            // если строка добавлена, то возвращается 1, если нет, то вернеться 0?
            if(rs != 0) {
                return true;
            }
        } catch (SQLException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return false;
    }
    //4DOESN'T WORK!
//    //FINAL VERSION with Statement
//    public boolean insertUserIntoDBSecurely(String login, String password){
//        try {
//            stmt = mysqlConnect.connect().createStatement();
//           //
//            byte[] secure_salt = secureHasher.generateSalt();
//            byte[] secure_hash = secureHasher.generateSecureHash(password, secure_salt);
//
//            // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
//            //записываем данные нового юзера в БД
//            String sql = String.format("INSERT INTO users2 (login, secure_hash, secure_salt) " +
//                            "VALUES ('%s', '%s', '%s')", login,
//                    new ByteArrayInputStream(secure_hash).read(),
//                    new ByteArrayInputStream(secure_salt).read());
//            //без .read() записывается ссылка java.io.ByteArrayInputStream@3578436e
//            //c .read() - 130
//
//            // оправка запроса и получение ответа из БД
//            int rs = stmt.executeUpdate(sql);
//            // если строка добавлена, то возвращается 1, если нет, то вернеться 0?
//            if(rs != 0) {
//                return true;
//            }
//        } catch (SQLException | InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    public boolean updateUserPasswordInDBSecurely(String login, String password){
        try {
            //
            byte[] secure_salt = secureHasher.generateSalt();
            byte[] secure_hash = secureHasher.generateSecureHash(password, secure_salt);

            // формирование строки запроса PreparedStatement.
            // ? - для последовательного подставления значений в соотвествующее место
            //TODO не обращать внимание!
            // работает, но IDEA выдает ошибку -
            // не видит таблицу "users", хотя тут же создал новую как предложение исправить ошибку
            String sql = "UPDATE users SET secure_hash = ?, secure_salt = ? WHERE login = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBinaryStream(1, new ByteArrayInputStream(secure_hash));
            preparedStatement.setBinaryStream(2, new ByteArrayInputStream(secure_salt));
            preparedStatement.setString(3, login);
            // оправка запроса и получение ответа из БД
            int rs = preparedStatement.executeUpdate();
            // если строка добавлена, то возвращается 1, если нет, то вернеться 0?
            if(rs != 0) {
                return true;
            }
        } catch (SQLException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return false;
    }

    //1WORKS!
//    public boolean checkLoginAndPasswordSecurely(String login, String password) {
//        // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
//        String sql = String.format("SELECT * FROM users2 WHERE login = '%s'", login);
//        try {
//            //TODO temporarily
////            stmt = mysqlConnect.connect().createStatement();
//
//            // оправка запроса и получение ответа из БД
//            ResultSet rs = stmt.executeQuery(sql);
//            // если есть строка, то rs.next() возвращает true, если нет - false
//            if(rs.next()) {
//
//                byte[] secure_hash = rs.getBytes("secure_hash");
//                byte[] secure_salt = rs.getBytes("secure_salt");
//
//                System.out.println("UsersAuthController.checkLoginAndPasswordSecurely(). - " +
//                        "Arrays.toString(secure_hash): " + Arrays.toString(secure_hash) +
//                        "\n. Arrays.toString(secure_salt): " + Arrays.toString(secure_salt));
//
//                if (secureHasher.compareSecureHashes(password, secure_hash, secure_salt)) {
//                    //такая пара логина и пароля есть в БД
//                    return true;
//                }
//            }
//        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
    //2WORKS!
    public boolean checkLoginAndPasswordInDBSecurely(String login, String password) {
        try {
            // формирование строки запроса PreparedStatement.
            // ? - для последовательного подставления значений в соотвествующее место
            //TODO не обращать внимание!
            // работает, но IDEA выдает ошибку -
            // не видит таблицу "users", хотя тут же создал новую как предложение исправить ошибку
            String sql = "SELECT * FROM users WHERE login = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, login);
            // оправка запроса и получение ответа из БД
            ResultSet rs = preparedStatement.executeQuery();
            // если есть строка, то rs.next() возвращает true, если нет - false
            if(rs.next()) {

                byte[] secure_hash = rs.getBytes("secure_hash");
                byte[] secure_salt = rs.getBytes("secure_salt");

                System.out.println("UsersAuthController.checkLoginAndPasswordSecurely(). - " +
                        "Arrays.toString(secure_hash): " + Arrays.toString(secure_hash) +
                        "\n. Arrays.toString(secure_salt): " + Arrays.toString(secure_salt));

                if (secureHasher.compareSecureHashes(password, secure_hash, secure_salt)) {
                    //такая пара логина и пароля есть в БД
                    return true;
                }
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void printMsg(String msg){
        storageServer.printMsg(msg);
    }

    public Map<String, ChannelHandlerContext> getAuthorizedUsers() {
        return authorizedUsers;
    }

}

