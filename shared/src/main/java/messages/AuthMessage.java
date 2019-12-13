package messages;

/**
 * A Class for authorization commands from client to server.
 */
public class AuthMessage extends AbstractMessage {
    //принимаем переменную логина пользователя
    private String login;
    //принимаем переменную пароля пользователя
    private String password;

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public AuthMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }

}

