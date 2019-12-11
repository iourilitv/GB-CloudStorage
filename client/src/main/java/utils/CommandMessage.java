package utils;

import utils.handlers.CommandHandler;

import java.io.Serializable;

/**
 * The client class for operating with commands for communication between the client and server.
 */
public class CommandMessage implements Serializable {
//    //***инициируем константы команд:***
//    //запрос на сервер на авторизацию пользователя с таким логином и паролем
//    public static final int CMD_MSG_REQUEST_SERVER_AUTH = 911101;
//    //ответ сервера, что авторизация прошла успешно
//    public static final int CMD_MSG_SERVER_RESPONSE_AUTH_OK = 911202;
//    //запрос на сервер загрузить(сохранить) файл
//    public static final int CMD_MSG_REQUEST_SERVER_FILE_UPLOAD = 101101;
//    //ответ сервера, что файл успешно загружен(сохранен)
//    public static final int CMD_MSG_SERVER_RESPONSE_FILE_UPLOADED = 101202;
//    //запрос на сервер скачать файл
//    public static final int CMD_MSG_REQUEST_SERVER_FILE_DOWNLOAD = 202101;
//    //запрос на сервер предоставить список файлов в папке
//    public static final int CMD_MSG_REQUEST_SERVER_FILES_LIST = 303101;
//    //переименовать файл на сервере
//    public static final int CMD_MSG_REQUEST_SERVER_RENAME_FILE = 239622745;
//    //удалить файл на сервере
//    public static final int CMD_MSG_REQUEST_SERVER_DELETE_FILE = 239622746;
//    //переместить файл в другую папку
//    public static final int CMD_MSG_REQUEST_SERVER_MOVE_FILE = 239622747;

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


