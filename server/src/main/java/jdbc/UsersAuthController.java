package jdbc;

import io.netty.channel.ChannelHandlerContext;
import messages.AuthMessage;
import control.CloudStorageServer;
import security.SecureHasher;

import java.io.ByteArrayInputStream;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
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
    //принимаем объект для операций шифрования
    private final SecureHasher secureHasher = SecureHasher.getOwnInstance();
    //объявляем множество авторизованных клиентов <логин, соединение>
    private Map<String, ChannelHandlerContext> authorizedUsers;
    //объявляем объект соединения с БД
    Connection connection;
    //объявляем объект подготовленного запрос в БД
    private PreparedStatement preparedStatement;

    /**
     * Метод инициирует необходимые объекты и переменные
     * @param storageServer - объект сервера
     */
    public void init(CloudStorageServer storageServer) {
        ownInstance.storageServer = storageServer;
        //инициируем множество авторизованных клиентов
        ownInstance.authorizedUsers = new HashMap<>();
        //инициируем объект соединения с БД
        connection = new MySQLConnect().connect();
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
        if(!insertUserIntoDBSecurely(authMessage.getLogin(), authMessage.getFirst_name(),
                authMessage.getLast_name(), authMessage.getEmail(), authMessage.getPassword())){
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
        if(checkLoginAndPasswordInDBSecurely(authMessage.getLogin(), authMessage.getPassword())){
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

    /**
     * Метод заменяет данные безопасного хэша и "соли" пользователя в БД.
     * @param login - заданный логин пользователя
     * @param oldPassword - заданный текущий пароль пользователя
     * @param newPassword - заданный новый пароль пользователя
     * @return - результат изменения пароля в БД
     */
    public boolean changeUserPassword(String login, String oldPassword, String newPassword){
        //если старый пароль пользователя не подходит
        if(!checkLoginAndPasswordInDBSecurely(login, oldPassword)){
            //выводим сообщение в консоль
            printMsg("[server]UsersAuthController.changeUserPassword() - " +
                    "This user's old password is not acceptable!");
            //и выходим с false
            return false;
        }
        //если пароль пользователя не был заменен на новый
        if(!updateUserPasswordInDBSecurely(login, newPassword)){
            //выводим сообщение в консоль
            printMsg("[server]UsersAuthController.changeUserPassword() - " +
                    "The user's password hasn't been changed!");
            //и выходим с false
            return false;
        }
        return true;
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
     * Метод проверяет не авторизован ли уже пользователь с таким логином и объектом соединения.
     * @param login - логин пользователя
     * @param ctx - сетевое соединение
     * @return - результат проверки
     */
    private boolean isUserAuthorized(String login, ChannelHandlerContext ctx) {
        //если есть элемент с таким логином в списке авторизованных пользователей
        if(authorizedUsers.containsKey(login)){
            return true;
        }
        //проверяем все элементы списка по значениям(на всякий случай)
        for (Map.Entry<String, ChannelHandlerContext> user: authorizedUsers.entrySet()) {
            //если есть элемент в списке авторизованных с такими объектом соединения
            if(user.getValue().channel().equals(ctx.channel())){
                return true;
            }
        }
        //возвращаем результат проверки  или логином
        return false;
    }

    /**
     * Метод проверяет введенный логин в БД на уникальность(не зарегистрирован ли уже такой логин?).
     * @param login - проверяемый логин
     * @return - результат проверки
     */
    private boolean isUserRegistered(String login) {
        try {
            //инициируем объект запроса в БД
            Statement statement = connection.createStatement();
            // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
            String sql = String.format("SELECT login FROM users WHERE login = '%s'", login);
            // оправка запроса и получение ответа из БД
            ResultSet rs = statement.executeQuery(sql);
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
     * Метод безопасно верифицирует заданные логин и пароль с данными пользователя в БД.
     * @param login - заданный логин пользователя
     * @param password - заданный пароль пользователя
     * @return - результат проверки данных в БД
     */
    private boolean checkLoginAndPasswordInDBSecurely(String login, String password) {
        try {
            //формируем строку для запроса PreparedStatement
            // ? - для последовательного подставления значений в соотвествующее место
            String sql = "SELECT * FROM users WHERE login = ?";
            //инициируем объект подготовленнного запроса
            preparedStatement = connection.prepareStatement(sql);
            //добавляем в запрос параметр 1 - строку логина
            preparedStatement.setString(1, login);
            //оправляем запрос и получяем ответ из БД
            ResultSet rs = preparedStatement.executeQuery();
            // если есть строка, то rs.next() возвращает true, если нет - false
            if(rs.next()) {
                //выделяем из результата запроса данные безопасного пароля пользователя из БД
                // инициируем временные байтовые массивы для безопасного хэша и "соли"
                byte[] secure_hash = rs.getBytes("secure_hash");
                byte[] secure_salt = rs.getBytes("secure_salt");
                //если заданный пароль совпадает с безопасным паролем в БД
                if (secureHasher.compareSecureHashes(password, secure_hash, secure_salt)) {
                    //пара логина и пароля релевантна
                    return true;
                }
            }
        } catch (SQLException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Метод безопасно добавляет нового пользователя в БД.
     * @param login - логин пользователя
     * @param first_name - имя пользователя
     * @param last_name - фамилия пользователя
     * @param email - email пользователя
     * @param password - пароль пользователя
     * @return - результат добавляения новой строки в БД.
     */
    private boolean insertUserIntoDBSecurely(String login, String first_name, String last_name,
                                            String email, String password){
        try {
            //генерирует "соль" - случайный байтовый массив
            byte[] secure_salt = secureHasher.generateSalt();
            //генерируем байтовый массив - безопасный хэш с "солью" для заданного пароля и "соли"
            byte[] secure_hash = secureHasher.generateSecureHash(password, secure_salt);
            // формируем строку для запроса PreparedStatement
            // ? - для последовательного подставления значений в соотвествующее место
            String sql = "INSERT INTO users (login, first_name, last_name, email, secure_hash, secure_salt) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            //инициируем объект подготовленнного запроса
            preparedStatement = connection.prepareStatement(sql);
            //добавляем в запрос параметр 1 - строку логина
            preparedStatement.setString(1, login);
            //добавляем в запрос параметр 2 - строку имени пользователя
            preparedStatement.setString(2, first_name);
            //добавляем в запрос параметр 3 - строку фамилии пользователя
            preparedStatement.setString(3, last_name);
            //добавляем в запрос параметр 4 - строку email пользователя
            preparedStatement.setString(4, email);
            //добавляем в запрос параметр 5 - байтовый массив безопасного хэша
            preparedStatement.setBinaryStream(5, new ByteArrayInputStream(secure_hash));
            //добавляем в запрос параметр 6 - байтовый массив "соли"
            preparedStatement.setBinaryStream(6, new ByteArrayInputStream(secure_salt));
            //оправляем запрос и получяем ответ из БД
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

    /**
     * Метод заменяет данные безопасного хэша и "соли" пользователя в БД.
     * @param login - заданный логин пользователя
     * @param password - заданный пароль пользователя
     * @return - результат изменения данных в БД
     */
    private boolean updateUserPasswordInDBSecurely(String login, String password){
        try {
            //генерирует "соль" - случайный байтовый массив
            byte[] secure_salt = secureHasher.generateSalt();
            //генерируем байтовый массив - безопасный хэш с "солью" для заданного пароля и "соли"
            byte[] secure_hash = secureHasher.generateSecureHash(password, secure_salt);
            //формируем строку для запроса PreparedStatement
            // ? - для последовательного подставления значений в соотвествующее место
            String sql = "UPDATE users SET secure_hash = ?, secure_salt = ? WHERE login = ?";
            //инициируем объект подготовленнного запроса
            preparedStatement = connection.prepareStatement(sql);
            //добавляем в запрос параметр 1 - байтовый массив безопасного хэша
            preparedStatement.setBinaryStream(1, new ByteArrayInputStream(secure_hash));
            //добавляем в запрос параметр 2 - байтовый массив "соли"
            preparedStatement.setBinaryStream(2, new ByteArrayInputStream(secure_salt));
            //добавляем в запрос параметр 3 - строку логина
            preparedStatement.setString(3, login);
            //оправляем запрос и получяем ответ из БД
            int rs = preparedStatement.executeUpdate();
            // если данные заменены, то возвращается 1, если нет, то вернеться 0?
            if(rs != 0) {
                return true;
            }
        } catch (SQLException | InvalidKeySpecException e) {
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

