package jdbc;

import java.util.HashMap;
import java.util.Map;

/**
 * The class is a temporarily storage for users data. //FIXME move it all to DB
 */
public class UsersDB {
    private static final UsersDB ownInstance = new UsersDB();

    public static UsersDB getOwnInstance() {
        registeredUsers.put("login1", "pass1");
        registeredUsers.put("login2", "pass2");
        return ownInstance;
    }

    //объявляем множество зарегистрированных клиентов <логин, пароль>
    private static final Map<String, String> registeredUsers = new HashMap<>();

    /**
     * Метод добавляет нового пользователя в множество зарегистрированных в облачном хранилище.
     * @param login - логин нового пользователя
     * @param password - пароль нового пользователя
     * @return - результат добавления и проверки нового пользователя в множество
     */
    public boolean addUserIntoMap(String login, String password){
        //добавляем пользователя в множество
        registeredUsers.put(login, password);
        //возвращаем результат проверки действительно ли пользователь правильно добавлен в множество
        return checkLoginAndPassword(login, password);
    }

    /**
     * Метод проверяет не зарегистрирован ли уже кто-то с таким логином.
     * @param login - проверяемый логин пользователя
     * @return - результат проверки
     */
    public boolean isUserExistInMap(String login) {
        return registeredUsers.containsKey(login);
    }

    /**
     * Метод проверяет релевантность пары логина и пароля
     * @param login - полученный логин пользователя
     * @param password - полученный пароль пользователя
     * @return результат проверки
     */
    public boolean checkLoginAndPassword(String login, String password) {
        return registeredUsers.get(login) != null && registeredUsers.get(login).equals(password);
    }
}
