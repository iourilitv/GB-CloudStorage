package utils;

import messages.AbstractMessage;

import java.io.Serializable;
import java.nio.file.Path;

/**
 * The class for operating with commands for communication between the server and clients.
 */
public class CommandMessage implements Serializable {
    //принимаем переменную типа операции(команды) на выполнение
    private int command;
    //принимаем объект сообщения(команды) операции
    private AbstractMessage messageObject;
    //принимаем объект пути к заданной директории
    private Path path;

    public CommandMessage(int command) {
        this.command = command;
    }

    public CommandMessage(int command, Path path) {
        this.command = command;
        this.path = path;
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

    public Path getPath() {
        return path;
    }
}