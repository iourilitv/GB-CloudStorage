package utils;

/**
 * The class is a public storage of command types.
 */
public class Commands {
    //***инициируем константы команд:***
    //запрос на сервер на авторизацию пользователя с таким логином и паролем
    public static final int REQUEST_SERVER_AUTH = 911101;
    //ответ сервера, что авторизация прошла успешно
    public static final int SERVER_RESPONSE_AUTH_OK = 911202;
    //ответ сервера, что авторизация прошла успешно
    public static final int SERVER_RESPONSE_AUTH_ERROR = 911209;

    //запрос на сервер загрузить(сохранить) файл
    public static final int REQUEST_SERVER_FILE_UPLOAD = 101101;
    //ответ клиента - подтверждение успешного получения обновленного списка, файлов в облачном хранилище
    public static final int CLIENT_RESPONSE_FILE_UPLOAD_OK = 101102;
    //ответ клиента - сообщение об ошибке получения обновленного списка, файлов в облачном хранилище
    public static final int CLIENT_RESPONSE_FILE_UPLOAD_ERROR = 101109;
    //ответ сервера, что файл успешно загружен(сохранен)
    public static final int SERVER_RESPONSE_FILE_UPLOAD_OK = 101202;
    //ответ сервера, что при загрузке(сохранении) файла произошла ошибка
    public static final int SERVER_RESPONSE_FILE_UPLOAD_ERROR = 101209;

    //запрос на сервер скачать файл
    public static final int REQUEST_SERVER_FILE_DOWNLOAD = 202101;
    //ответ сервера с файлом, если нет ошибок
    public static final int CLIENT_RESPONSE_FILE_DOWNLOAD_OK = 202102;
    //ответ сервера, что при скачивании файла произошла ошибка
    public static final int CLIENT_RESPONSE_FILE_DOWNLOAD_ERROR = 202109;
    //ответ сервера с файлом, если нет ошибок
    public static final int SERVER_RESPONSE_FILE_DOWNLOAD_OK = 202202;
    //ответ сервера, что при скачивании файла произошла ошибка
    public static final int SERVER_RESPONSE_FILE_DOWNLOAD_ERROR = 202209;

    //запрос на сервер предоставить список файлов в папке
    public static final int REQUEST_SERVER_FILES_LIST = 303101;
    //переименовать файл на сервере
    public static final int REQUEST_SERVER_RENAME_FILE = 404101;
    //удалить файл на сервере
    public static final int REQUEST_SERVER_DELETE_FILE = 505101;
    //переместить файл в другую папку
    public static final int REQUEST_SERVER_MOVE_FILE = 606101;

    //запрос на сервер загрузить(сохранить) фрагмент файла
    public static final int REQUEST_SERVER_FILE_FRAG_UPLOAD = 111101;

    //TODO Надо? это драфт
//    //ответ клиента - подтверждение успешного получения обновленного списка, файлов в облачном хранилище
//    public static final int CLIENT_RESPONSE_FILE_UPLOAD_OK = 101102;
//    //ответ клиента - сообщение об ошибке получения обновленного списка, файлов в облачном хранилище
//    public static final int CLIENT_RESPONSE_FILE_UPLOAD_ERROR = 101109;

    //ответ сервера, что фрагмент файла успешно загружен(сохранен)
    public static final int SERVER_RESPONSE_FILE_FRAG_UPLOAD_OK = 111202;
    //ответ сервера, что при загрузке(сохранении) фрагмент файла произошла ошибка
    public static final int SERVER_RESPONSE_FILE_FRAG_UPLOAD_ERROR = 111209;
    //ответ сервера, что сборка файла из загруженных фрагментов прошла успешно
    public static final int SERVER_RESPONSE_FILE_FRAGS_UPLOAD_OK = 111222;
    //ответ сервера, что при сборке файла из загруженных фрагментов произошла ошибка
    public static final int SERVER_RESPONSE_FILE_FRAGS_UPLOAD_ERROR = 111299;
}
