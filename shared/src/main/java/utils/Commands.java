package utils;

/**
 * The class is a public storage of command types.
 */
public enum Commands {
    SERVER_NOTIFICATION_CLIENT_CONNECTED,//оповещение от сервера, что клиент подключился
    REQUEST_SERVER_DISCONNECT,//запрос на сервер на отсоединение пользователя от сервера
    SERVER_RESPONSE_DISCONNECT_OK,//ответ сервера, что отсоединение прошло успешно
    SERVER_RESPONSE_DISCONNECT_ERROR,//ответ сервера, что при отсоединении произошла ошибка

    REQUEST_SERVER_REGISTRATION,//запрос на сервер на регистрация пользователя с таким логином и паролем
    SERVER_RESPONSE_REGISTRATION_OK,//ответ сервера, что регистрация прошла успешно
    SERVER_RESPONSE_REGISTRATION_ERROR,//ответ сервера, что при регистрации произошла ошибка

    REQUEST_SERVER_AUTH,//запрос на сервер на авторизацию пользователя с таким логином и паролем
    SERVER_RESPONSE_AUTH_OK,//ответ сервера, что авторизация прошла успешно
    SERVER_RESPONSE_AUTH_ERROR,//ответ сервера, что при авторизации произошла ошибка

    REQUEST_SERVER_CHANGE_PASSWORD,//запрос на сервер на изменение пароля пользователя
    SERVER_RESPONSE_CHANGE_PASSWORD_OK,//ответ сервера, что изменение пароля прошло успешно
    SERVER_RESPONSE_CHANGE_PASSWORD_ERROR,//ответ сервера, что при изменении пароля произошла ошибка

    REQUEST_SERVER_UPLOAD_ITEM,//запрос на сервер загрузить(сохранить) объект элемента списка
    SERVER_RESPONSE_UPLOAD_ITEM_OK,//ответ сервера, что объект успешно загружен(сохранен)
    SERVER_RESPONSE_UPLOAD_ITEM_ERROR,//ответ сервера, что при загрузке(сохранении) объекта произошла ошибка

    REQUEST_SERVER_UPLOAD_FILE_FRAG,//запрос на сервер загрузить(сохранить) фрагмент файла
    SERVER_RESPONSE_UPLOAD_FILE_FRAG_OK,//ответ сервера, что фрагмент файла успешно загружен(сохранен)
    SERVER_RESPONSE_UPLOAD_FILE_FRAG_ERROR,//ответ сервера, что при загрузке(сохранении) фрагмент файла произошла ошибка
    SERVER_RESPONSE_UPLOAD_FILE_FRAGS_OK,//ответ сервера, что сборка файла из загруженных фрагментов прошла успешно
    SERVER_RESPONSE_UPLOAD_FILE_FRAGS_ERROR,//ответ сервера, что при сборке файла из загруженных фрагментов произошла ошибка

    //запрос на сервер скачать объект элемента списка(пока только файл)
    REQUEST_SERVER_DOWNLOAD_ITEM,
    //ответ сервера с объектом элемента списка(пока только файл), если нет ошибок
    SERVER_RESPONSE_DOWNLOAD_ITEM_OK,
    //ответ сервера, что при скачивании объекта элемента списка(пока только файл) произошла ошибка
    SERVER_RESPONSE_DOWNLOAD_ITEM_ERROR,

    //ответ сервера с файлом-фрагментом, если нет ошибок
    SERVER_RESPONSE_DOWNLOAD_FILE_FRAG_OK,
    //ответ сервера, что при скачивании файла-фрагмента произошла ошибка
    SERVER_RESPONSE_DOWNLOAD_FILE_FRAG_ERROR,//TODO надо ли?
    //ответ клиента, что файл-фрагмент получен и сохранен успешно
    CLIENT_RESPONSE_DOWNLOAD_FILE_FRAG_OK,
    //ответ клиента, что при получени или сохранении файла-фрагмента произошла ошибка
    CLIENT_RESPONSE_DOWNLOAD_FILE_FRAG_ERROR,

    //запрос на сервер предоставить список объектов файлов и папок в заданной директории
    REQUEST_SERVER_ITEMS_LIST,
    //ответ сервера с массивом объектов в заданной директории пользователя
    // в сетевом хранилище, если нет ошибок
    SERVER_RESPONSE_ITEMS_LIST_OK,

    //переименовать объект на сервере
    REQUEST_SERVER_RENAME_ITEM,
    //ответ сервера с массивом объектов в заданной директории пользователя
    // в сетевом хранилище, если нет ошибок
    SERVER_RESPONSE_RENAME_ITEM_OK,
    //ответ сервера, что при переименовании объекта в облачном хранилище произошла ошибка
    SERVER_RESPONSE_RENAME_ITEM_ERROR,

    //запрос на сервер удалить объект элемента списка на сервере
    REQUEST_SERVER_DELETE_ITEM,
    //ответ сервера - подтверждение успешного удаления объекта элемента списка в облачном хранилище
    SERVER_RESPONSE_DELETE_ITEM_OK,
    //ответ сервера, что при удаления объекта элемента списка в облачном хранилище произошла ошибка
    SERVER_RESPONSE_DELETE_ITEM_ERROR,

    //запрос на сервер создать папку в сетевом хранилище
    REQUEST_SERVER_CREATE_NEW_FOLDER,
    //ответ сервера с массивом объектов в заданной директории пользователя
    // в сетевом хранилище, если нет ошибок
    SERVER_RESPONSE_CREATE_NEW_FOLDER_OK,
    //ответ сервера, что при удаления создании папки в облачном хранилище произошла ошибка
    SERVER_RESPONSE_CREATE_NEW_FOLDER_ERROR,

    //переместить файл в другую папку
    REQUEST_SERVER_MOVE_FILE

}
