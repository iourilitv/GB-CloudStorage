package utils.handlers;

import messages.DirectoryMessage;

/**
 * The server class for operating with directoryMessages.
 */
public class DirectoryCommandHandler extends CommandHandler{
    //принимаем объект сообщения о директории
    private DirectoryMessage directoryMessage;

    public DirectoryCommandHandler(DirectoryMessage directoryMessage) {
        this.directoryMessage = directoryMessage;
    }



}
