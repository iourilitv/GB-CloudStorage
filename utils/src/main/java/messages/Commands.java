package messages;

public class Commands {
//    private static final Commands ourInstance = new Commands();
//
//    public static Commands getInstance () {
//        return ourInstance;
//    }

    //***инициируем константы команд:***
    //запрос на сервер на авторизацию пользователя с таким логином и паролем
    public static final int REQUEST_SERVER_AUTH = 911101;
    //ответ сервера, что авторизация прошла успешно
    public static final int SERVER_RESPONSE_AUTH_OK = 911202;
    //запрос на сервер загрузить(сохранить) файл
    public static final int REQUEST_SERVER_FILE_UPLOAD = 101101;
    //ответ сервера, что файл успешно загружен(сохранен)
    public static final int SERVER_RESPONSE_FILE_UPLOADED = 101202;
    //запрос на сервер скачать файл
    public static final int REQUEST_SERVER_FILE_DOWNLOAD = 202101;
    //ответ сервера с присланным файлом или с пустым приложением, если файла нет//TODO
    public static final int SERVER_RESPONSE_FILE_DOWNLOAD = 202202;
    //запрос на сервер предоставить список файлов в папке
    public static final int REQUEST_SERVER_FILES_LIST = 303101;
    //переименовать файл на сервере
    public static final int REQUEST_SERVER_RENAME_FILE = 404101;
    //удалить файл на сервере
    public static final int REQUEST_SERVER_DELETE_FILE = 505101;
    //переместить файл в другую папку
    public static final int REQUEST_SERVER_MOVE_FILE = 606101;
}
