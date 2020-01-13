package utils;

/**
 * The class is a public storage of command types.
 */
public class Commands {
    //***инициируем константы команд:***
    //оповещение от сервера, что клиент подключился
    public static final int SERVER_NOTIFICATION_CLIENT_CONNECTED = 911000;

    //запрос на сервер на авторизацию пользователя с таким логином и паролем
    public static final int REQUEST_SERVER_AUTH = 911101;
    //ответ сервера, что авторизация прошла успешно
    public static final int SERVER_RESPONSE_AUTH_OK = 911202;
    //ответ сервера, что авторизация прошла успешно
    public static final int SERVER_RESPONSE_AUTH_ERROR = 911209;

    //запрос на сервер загрузить(сохранить) объект элемента списка
    public static final int REQUEST_SERVER_UPLOAD_ITEM = 101101;
    //ответ сервера, что объект успешно загружен(сохранен)
    public static final int SERVER_RESPONSE_UPLOAD_ITEM_OK = 101202;
    //ответ сервера, что при загрузке(сохранении) объекта произошла ошибка
    public static final int SERVER_RESPONSE_UPLOAD_ITEM_ERROR = 101209;

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

    //запрос на сервер предоставить список объектов файлов и папок в заданной директории
    public static final int REQUEST_SERVER_ITEMS_LIST = 303101;
    //ответ сервера с массивом объектов в заданной директории пользователя
    // в сетевом хранилище, если нет ошибок
    public static final int SERVER_RESPONSE_ITEMS_LIST_OK = 303102;

    //переименовать объект на сервере
    public static final int REQUEST_SERVER_RENAME_ITEM = 404101;
    //ответ сервера с массивом объектов в заданной директории пользователя
    // в сетевом хранилище, если нет ошибок
    public static final int SERVER_RESPONSE_RENAME_ITEM_OK = 404102;
    //ответ сервера, что при переименовании объекта в облачном хранилище произошла ошибка
    public static final int SERVER_RESPONSE_RENAME_ITEM_ERROR = 404109;

    //запрос на сервер удалить объект элемента списка на сервере
    public static final int REQUEST_SERVER_DELETE_ITEM = 505101;
    //ответ сервера - подтверждение успешного удаления объекта элемента списка в облачном хранилище
    public static final int SERVER_RESPONSE_DELETE_ITEM_OK = 505102;
    //ответ сервера, что при удаления объекта элемента списка в облачном хранилище произошла ошибка
    public static final int SERVER_RESPONSE_DELETE_ITEM_ERROR = 505109;

    //переместить файл в другую папку
    public static final int REQUEST_SERVER_MOVE_FILE = 606101;

    //запрос на сервер загрузить(сохранить) фрагмент файла
    public static final int REQUEST_SERVER_FILE_FRAG_UPLOAD = 111101;

    //ответ сервера, что фрагмент файла успешно загружен(сохранен)
    public static final int SERVER_RESPONSE_FILE_FRAG_UPLOAD_OK = 111202;
    //ответ сервера, что при загрузке(сохранении) фрагмент файла произошла ошибка
    public static final int SERVER_RESPONSE_FILE_FRAG_UPLOAD_ERROR = 111209;
    //ответ сервера, что сборка файла из загруженных фрагментов прошла успешно
    public static final int SERVER_RESPONSE_FILE_FRAGS_UPLOAD_OK = 111222;
    //ответ сервера, что при сборке файла из загруженных фрагментов произошла ошибка
    public static final int SERVER_RESPONSE_FILE_FRAGS_UPLOAD_ERROR = 111299;

    //ответ сервера с файлом-фрагментом, если нет ошибок
    public static final int SERVER_RESPONSE_FILE_FRAGS_DOWNLOAD_OK = 222202;
    //ответ сервера, что при скачивании файла-фрагмента произошла ошибка
    public static final int SERVER_RESPONSE_FILE_FRAGS_DOWNLOAD_ERROR = 222209;//TODO надо ли?
}
