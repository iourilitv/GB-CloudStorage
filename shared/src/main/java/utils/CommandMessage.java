package utils;

import messages.AbstractMessage;

import java.io.Serializable;

/**
 * The class for operating with commands for communication between the server and clients.
 */
public class CommandMessage implements Serializable {
    //принимаем переменную типа операции(команды) на выполнение
    private Commands command;
    //принимаем объект сообщения(команды) операции
    private AbstractMessage messageObject;
    //принимаем объект пути к заданной директории
    private String message;

    public CommandMessage(Commands command) {
        this.command = command;
    }

    public CommandMessage(Commands command, String message) {
        this.command = command;
        this.message = message;
    }

    public CommandMessage(Commands command, AbstractMessage messageObject) {
        this.command = command;
        this.messageObject = messageObject;
    }

    public Commands getCommand() {
        return command;
    }

    public AbstractMessage getMessageObject() {
        return messageObject;
    }

    public String getMessage() {
        return message;
    }
}