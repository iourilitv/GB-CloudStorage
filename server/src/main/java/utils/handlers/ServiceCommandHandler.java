package utils.handlers;

import messages.AuthMessage;

/**
 * A class for operating with service command messages.
 */
public class ServiceCommandHandler extends CommandHandler{
    private AuthMessage authMessage;

    public ServiceCommandHandler(AuthMessage authMessage) {
        this.authMessage = authMessage;
    }

    public AuthMessage getAuthMessage() {
        return authMessage;
    }

    public void authorizeUser(){

        System.out.println("Server.onReceiveObject - authMessage.getLogin(): " +
                authMessage.getLogin() + ". authMessage.getPassword(): " + authMessage.getPassword());

    }
}
