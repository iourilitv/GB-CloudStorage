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

//        System.out.println("UsersDB.addUserIntoMap() - " +
////                "registeredUsers.put(login, password): " + registeredUsers.put(login, password) +
//                ", registeredUsers.get(login): " + registeredUsers.get(login) +
////                ", registeredUsers.get(login).equals(password): " + registeredUsers.get(login).equals(password) +
//                ", registeredUsers.toString(): " + registeredUsers.toString());

        //возвращаем результат проверки действительно ли пользователь правильно добавлен в множество
        return registeredUsers.get(login) != null && registeredUsers.get(login).equals(password);
    }

    /**
     * Метод проверяет не зарегистрирован ли уже кто-то с таким логином.
     * @param login - проверяемый логин пользователя
     * @return - результат проверки
     */
    public boolean isUserExistInMap(String login) {
        System.out.println("UsersDB.isUserExistInMap() - " +
                "registeredUsers.containsKey(login): " + registeredUsers.containsKey(login) +
                ", registeredUsers.toString(): " + registeredUsers.toString());
        return registeredUsers.containsKey(login);
    }
}
