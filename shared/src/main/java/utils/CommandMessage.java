package utils;

import messages.AbstractMessage;

import java.io.Serializable;

/**
 * The class for operating with commands for communication between the server and clients.
 */
public class CommandMessage implements Serializable {
    //принимаем переменную типа операции(команды) на выполнение
    private int command;
    //принимаем объект сообщения(команды) операции
    private AbstractMessage messageObject;

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