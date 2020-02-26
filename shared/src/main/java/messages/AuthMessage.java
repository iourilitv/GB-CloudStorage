package messages;

/**
 * A Class for authorization commands from client to server.
 */
public class AuthMessage extends AbstractMessage {
    //принимаем переменную логина пользователя
    private String login;
    //принимаем переменную имени пользователя
    private String first_name;
    //принимаем переменную фамилии пользователя
    private String last_name;
    //принимаем переменную email пользователя
    private String email;
    //принимаем переменную пароля пользователя
    private String password;
    //принимаем переменную нового пароля пользователя
    private String newPassword;

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public AuthMessage() {
    }

    //конструктор для авторизации пользователя
    public AuthMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }

    //конструктор для смены пароля пользователя
    public AuthMessage(String login, String password, String newPassword) {
        this.login = login;
        this.password = password;
        this.newPassword = newPassword;
    }

    //конструктор для регистрации нового пользователя
    public AuthMessage(String login, String first_name, String last_name,
                       String email, String password) {
        this.login = login;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getNewPassword() {
        return newPassword;
    }
}

