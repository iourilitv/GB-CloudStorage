package messages;

import java.io.IOException;

/**
 * A Class for authorization commands from client to server.
 */
public class AuthMessage extends AbstractMessage {
    private String login;
    private String password;

    public AuthMessage(String root, String filename) throws IOException {//TODO
        super(root, filename);
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

//    public AuthMessage(String login, String password) {
//        this.login = login;
//        this.password = password;
//    }
}

