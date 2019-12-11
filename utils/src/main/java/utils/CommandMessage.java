package utils;

import messages.AbstractMessage;

import java.io.Serializable;

/**
 * The class for operating with commands for communication between the server and clients.
 */
public class CommandMessage implements Serializable {

    private int command;//тип операции на выполнение
    private AbstractMessage messageObject;//объект сообщения(команды) операции

    public CommandMessage(int command, AbstractMessage messageObject) {
        this.command = command;
        this.messageObject = messageObject;
    }

    public int getCommand() {
        return command;
    }

    public AbstractMessage getMessageObject() {
        return messageObject;
    }
}