package utils;

import utils.handlers.CommandHandler;

import java.io.Serializable;

/**
 * A Class for communication commands between client and server.
 */
public class CommandMessage implements Serializable {
    //***инициируем константы команд:***
    //запрос на авторизацию пользователя с таким логином и паролем
    public static final int CMD_MSG_REQUEST_AUTH = 23792836;
    //авторизация прошла успешно
    public static final int CMD_MSG_AUTH_OK = 23792837;
    //загрузить(сохранить) файл
    public static final int CMD_MSG_REQUEST_FILE_UPLOAD = 398472947;
    //скачать файл
    public static final int CMD_MSG_REQUEST_FILE_DOWNLOAD = 398472948;
    //предоставить список файлов в папке
    public static final int CMD_MSG__REQUEST_FILES_LIST = 398472949;//340274982;
    //переименовать файл на сервере
    public static final int CMD_MSG__REQUEST_SERVER_RENAME_FILE = 239622745;
    //удалить файл на сервере
    public static final int CMD_MSG__REQUEST_SERVER_DELETE_FILE = 239622746;
    //переместить файл в другую папку
    public static final int CMD_MSG__REQUEST_SERVER_MOVE_FILE = 239622747;

    private int command;//тип операции на выполнение
    private CommandHandler commandHandler;//хендлер операции

    public CommandMessage(int command, CommandHandler commandHandler) {
        this.command = command;
        this.commandHandler = commandHandler;
    }

    public int getCommand() {
        return command;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

}


//TODO Deleted at the finish

//    private AbstractMessage[] attachment;
//    public AbstractMessage[] getAttachment() {
//        return attachment;
//    }

//    public CommandMessage(int command, AbstractMessage... attachment) {
//        this.command = command;
//        this.attachment = attachment;
//    }

//    @Override
//    public String toString() {
//        return "messages.CommandMessage{" +
//                "command=" + command +
//                ", attachment=" + Arrays.toString(attachment) +
//                '}';
//    }


