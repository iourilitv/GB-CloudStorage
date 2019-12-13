package messages;

/**
 * A Class for authorization commands from client to server.
 */
public class AuthMessage extends AbstractMessage {
    //принимаем переменную логина пользователя
    private String login;
    //принимаем переменную пароля пользователя
    private String password;
    //принимаем переменную порта соединений клиента пользователя
    private int port;

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

//    public AuthMessage(String login, String password) {
//        this.login = login;
//        this.password = password;
//    }

    public AuthMessage(int port, String login, String password) {
        this.port = port;
        this.login = login;
        this.password = password;
    }


}

