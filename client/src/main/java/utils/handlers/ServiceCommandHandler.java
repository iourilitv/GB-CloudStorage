package utils.handlers;

import messages.AuthMessage;
import tcp.TCPClient;

/**
 * The client class for operating with service command messages.
 */
public class ServiceCommandHandler extends CommandHandler{
    private AuthMessage authMessage;

    public ServiceCommandHandler(AuthMessage authMessage) {
        this.authMessage = authMessage;
    }

    public AuthMessage getAuthMessage() {
        return authMessage;
    }

    /**
     * Метод обработки события успешной авторизации в облачном хранилище
     * @param client - объект клиента
     */
    public void isAuthorized(TCPClient client, AuthMessage authMessage){
        //FIXME что здесь делать?//чтото изменить в GUI?
        // Вывести список файлов в директории в сетевом хранилище, например
        client.printMsg("(Client)ServiceCommandHandler.isAuthorized: true! - authMessage.getLogin(): " +
                authMessage.getLogin() + ". authMessage.getPassword(): " + authMessage.getPassword());

        //TODO temporarily
        //сбрасываем защелку
        client.getCountDownLatch().countDown();

    }
}
