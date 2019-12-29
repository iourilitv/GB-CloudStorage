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

    //принимаем объект пути к заданной директории
    private String directory;

    public CommandMessage(int command) {
        this.command = command;
    }

    public CommandMessage(int command, String directory) {
        this.command = command;
        this.directory = directory;
    }

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

    public String getDirectory() {
        return directory;
    }
}