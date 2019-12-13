package utils.handlers;

import messages.AuthMessage;
import messages.Commands;
import messages.FileMessage;
import tcp.TCPConnection;
import tcp.TCPServer;
import utils.CommandMessage;

/**
 * The server class for recognizing command messages and control command handlers.
 */
public class ObjectHandler {
    //принимаем объект сервера
    private TCPServer server;
    //объявляем объект файлового сообщения для полного файла
    private FileMessage fileMessage;
    //объявляем объект файлового хендлера
    private FileCommandHandler fileCommandHandler;
    //объявляем переменную для клиентской директории
    private String clientDir;
    //объявляем переменную для корневой директории пользователя в сетевом хранилище
    private String userStorageRoot;
    //объявляем переменную для директории, заданной относительно userStorageRoot в сетевом хранилище
    private String storageDir;
    //объявляем объект авторизационного сообщения
    private AuthMessage authMessage;
    //объявляем объект сервисного хендлера
    private ServiceCommandHandler serviceCommandHandler;

    public ObjectHandler(TCPServer server) {
        this.server = server;
        //инициируем переменную для корневой директории пользователя в сетевом хранилище
        userStorageRoot = server.getStorageRoot();//FIXME не будет ли юзер видеть все хранилище?
    }

//    /**
//     * Метот распознает тип команды и обрабатывает ее.
//     * @param messageObject - объект сообщения(команды)
//     */
//    public void recognizeAndArrangeMessageObject(CommandMessage messageObject) {
//        //выполняем операции в зависимости от типа полученного сообщения(команды)
//        switch (messageObject.getCommand()) {
//            //обрабатываем полученный от клиента запрос на авторизацию в облачное хранилище
//            case Commands.REQUEST_SERVER_AUTH:
//                //вызываем метод обработки запроса от клиента
//                onAuthClientRequest(messageObject);
//                break;
//            //обрабатываем полученный от клиента запрос на загрузку(сохранение) файла в облачное хранилище
//            case Commands.REQUEST_SERVER_FILE_UPLOAD:
//                //вызываем метод обработки запроса от клиента на загрузку целого файла клиента
//                // в директорию в сетевом хранилище.
//                onUploadFileClientRequest(messageObject);
//                break;
//            //обрабатываем полученное от клиента подтверждение успешного получения обновленного
//            // списка файлов клиента в облачном хранилище
//            case Commands.CLIENT_RESPONSE_FILE_UPLOAD_OK:
//                //вызываем метод обработки ответа клиента
//                onUploadFileOkClientResponse(messageObject);
//                break;
//            //обрабатываем полученное от клиента сообщение об ошибке получения обновленного
//            // списка файлов клиента в облачном хранилище
//            case Commands.CLIENT_RESPONSE_FILE_UPLOAD_ERROR:
//                //вызываем метод обработки ответа клиента
//                onUploadFileErrorClientResponse(messageObject);
//                break;
//            //обрабатываем полученный от клиента запрос на скачивание целого файла из облачного хранилища
//            case Commands.REQUEST_SERVER_FILE_DOWNLOAD:
//                //вызываем метод обработки запроса от клиента на скачивание целого файла клиента
//                // из директории в сетевом хранилище
//                onDownloadFileClientRequest(messageObject);
//                break;
//            //обрабатываем полученное от клиента подтверждение успешного сохранения целого файла,
//            // скачанного из облачного хранилища
//            case Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_OK:
//                //вызываем метод обработки ответа клиента
//                onDownloadFileOkClientResponse(messageObject);
//                break;
//            //обрабатываем полученное от клиента сообщение об ошибке сохранения целого файла,
//            // скачанного из облачного хранилища
//            case Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_ERROR:
//                //вызываем метод обработки ответа клиента
//                onDownloadFileErrorClientResponse(messageObject);
//                break;
//        }
//    }
    public void recognizeAndArrangeMessageObject(TCPConnection tcpConnection, CommandMessage messageObject) {
        //***блок обработки объектов сервисных сообщений(команд), полученных от клиента***
        //если подсоединившийся клиент еще не распознан
        if(tcpConnection.getClientID().equals("unknownID")){
            //если полученное от клиента сообщение это запрос на авторизацию в облачное хранилище
            if(messageObject.getCommand() == Commands.REQUEST_SERVER_AUTH){
                //вызываем метод обработки запроса от клиента
                onAuthClientRequest(tcpConnection, messageObject);
                return;
            }
        }

        //если сюда прошли, значит клиент авторизован
        //***блок обработки объектов НЕсервисных сообщений(команд), полученных от клиента***
        //выполняем операции в зависимости от типа полученного не сервисного сообщения(команды)
        switch (messageObject.getCommand()) {
            //обрабатываем полученный от клиента запрос на загрузку(сохранение) файла в облачное хранилище
            case Commands.REQUEST_SERVER_FILE_UPLOAD:
                //вызываем метод обработки запроса от клиента на загрузку целого файла клиента
                // в директорию в сетевом хранилище.
                onUploadFileClientRequest(tcpConnection, messageObject);
                break;
            //обрабатываем полученное от клиента подтверждение успешного получения обновленного
            // списка файлов клиента в облачном хранилище
            case Commands.CLIENT_RESPONSE_FILE_UPLOAD_OK:
                //вызываем метод обработки ответа клиента
                onUploadFileOkClientResponse(tcpConnection, messageObject);
                break;
            //обрабатываем полученное от клиента сообщение об ошибке получения обновленного
            // списка файлов клиента в облачном хранилище
            case Commands.CLIENT_RESPONSE_FILE_UPLOAD_ERROR:
                //вызываем метод обработки ответа клиента
                onUploadFileErrorClientResponse(tcpConnection, messageObject);
                break;
            //обрабатываем полученный от клиента запрос на скачивание целого файла из облачного хранилища
            case Commands.REQUEST_SERVER_FILE_DOWNLOAD:
                //вызываем метод обработки запроса от клиента на скачивание целого файла клиента
                // из директории в сетевом хранилище
                onDownloadFileClientRequest(tcpConnection, messageObject);
                break;
            //обрабатываем полученное от клиента подтверждение успешного сохранения целого файла,
            // скачанного из облачного хранилища
            case Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_OK:
                //вызываем метод обработки ответа клиента
                onDownloadFileOkClientResponse(tcpConnection, messageObject);
                break;
            //обрабатываем полученное от клиента сообщение об ошибке сохранения целого файла,
            // скачанного из облачного хранилища
            case Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_ERROR:
                //вызываем метод обработки ответа клиента
                onDownloadFileErrorClientResponse(tcpConnection, messageObject);
                break;
        }
    }

//    /**
//     * Метод обрабатывает полученный от клиента запрос на авторизацию в облачное хранилище
//     * @param messageObject - объект сообщения(команды)
//     */
//    private void onAuthClientRequest(TCPConnection tcpConnection, CommandMessage messageObject) {
//        //вынимаем объект авторизационного сообщения из объекта сообщения(команды)
//        authMessage = (AuthMessage) messageObject.getMessageObject();
//        //инициируем объект сервисного хендлера
//        serviceCommandHandler = new ServiceCommandHandler(authMessage);
//        //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
//        int command = Commands.SERVER_RESPONSE_AUTH_ERROR;
//        //если авторизации клиента в облачном хранилище прошла удачно
//        if(serviceCommandHandler.authorizeUser(server, authMessage)){
//            //меняем команду на успешную
//            command = Commands.SERVER_RESPONSE_AUTH_OK;
//            //добавляем логин пользователя(имя его папки в сетевом хранилище)
//            // к корневой директории клиента по умолчанию
//            userStorageRoot = userStorageRoot.concat("/").concat(authMessage.getLogin());
//        }
////        //создаем объект авторизационного сообщения
////        authMessage = new AuthMessage();//TODO надо?
//
//        //отправляем объект сообщения(команды) клиенту//FIXME где брать логин?
//        server.sendToClient("login1", new CommandMessage(command, authMessage));
//    }
    private void onAuthClientRequest(TCPConnection tcpConnection, CommandMessage messageObject) {
        //вынимаем объект авторизационного сообщения из объекта сообщения(команды)
        authMessage = (AuthMessage) messageObject.getMessageObject();
        //инициируем объект сервисного хендлера
        serviceCommandHandler = new ServiceCommandHandler(authMessage);
        //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
        int command = Commands.SERVER_RESPONSE_AUTH_ERROR;
        //если авторизации клиента в облачном хранилище прошла удачно
        if(serviceCommandHandler.authorizeUser(server, tcpConnection, authMessage)){
            //меняем команду на успешную
            command = Commands.SERVER_RESPONSE_AUTH_OK;
            //добавляем логин пользователя(имя его папки в сетевом хранилище)
            // к корневой директории клиента по умолчанию
            userStorageRoot = userStorageRoot.concat("/").concat(authMessage.getLogin());
        }
//        //создаем объект авторизационного сообщения
//        authMessage = new AuthMessage();//TODO надо? Можно отправить список фалов в директории

        //отправляем объект сообщения(команды) клиенту
        server.sendToClient(tcpConnection, new CommandMessage(command, authMessage));
    }

//    /**
//     * Метод обработки запроса от клиента на загрузку целого файла клиента в директорию в
//     * сетевом хранилище.
//     * @param messageObject - объект сообщения(команды)
//     */
//    private void onUploadFileClientRequest(CommandMessage messageObject) {
//        //вынимаем объект файлового сообщения из объекта сообщения(команды)
//        fileMessage = (FileMessage) messageObject.getMessageObject();
//        //инициируем объект файлового хендлера
//        fileCommandHandler = new FileCommandHandler(fileMessage);
//        //вынимаем заданную клиентскую директорию из объекта сообщения(команды)
//        clientDir = fileMessage.getFromDir();
//        //вынимаем заданную директорию сетевого хранилища из объекта сообщения(команды)
//        storageDir = fileMessage.getToDir();
//        //собираем целевую директорию пользователя в сетевом хранилище
//        String toDir = userStorageRoot;//сбрасываем до корневой папки пользователя в сетевом хранилище
//        toDir = toDir.concat("/").concat(storageDir);//добавляем значение подпапки
//        //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
//        int command = Commands.SERVER_RESPONSE_FILE_UPLOAD_ERROR;
//        //если сохранение прошло удачно
//        if(fileCommandHandler.saveUploadedFile(server, toDir, fileMessage)){
//            //проверяем сохраненный файл по контрольной сумме//FIXME
//            if(true){
//                //отправляем сообщение на сервер: подтверждение, что все прошло успешно
//                command = Commands.SERVER_RESPONSE_FILE_UPLOAD_OK;
//            }
//        }
//        //создаем объект файлового сообщения
//        fileMessage = new FileMessage(storageDir, clientDir, fileMessage.getFilename());
//        //отправляем объект сообщения(команды) клиенту//FIXME где брать логин?
//        server.sendToClient("login1", new CommandMessage(command, fileMessage));
//    }
    private void onUploadFileClientRequest(TCPConnection tcpConnection, CommandMessage messageObject) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        fileMessage = (FileMessage) messageObject.getMessageObject();
        //инициируем объект файлового хендлера
        fileCommandHandler = new FileCommandHandler(fileMessage);
        //вынимаем заданную клиентскую директорию из объекта сообщения(команды)
        clientDir = fileMessage.getFromDir();
        //вынимаем заданную директорию сетевого хранилища из объекта сообщения(команды)
        storageDir = fileMessage.getToDir();
        //собираем целевую директорию пользователя в сетевом хранилище
        String toDir = userStorageRoot;//сбрасываем до корневой папки пользователя в сетевом хранилище
        toDir = toDir.concat("/").concat(storageDir);//добавляем значение подпапки
        //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
        int command = Commands.SERVER_RESPONSE_FILE_UPLOAD_ERROR;
        //если сохранение прошло удачно
        if(fileCommandHandler.saveUploadedFile(server, toDir, fileMessage)){
            //проверяем сохраненный файл по контрольной сумме//FIXME
            if(true){
                //отправляем сообщение на сервер: подтверждение, что все прошло успешно
                command = Commands.SERVER_RESPONSE_FILE_UPLOAD_OK;
            }
        }
        //создаем объект файлового сообщения
        fileMessage = new FileMessage(storageDir, clientDir, fileMessage.getFilename());
        //отправляем объект сообщения(команды) клиенту//FIXME где брать логин?
        server.sendToClient(tcpConnection, new CommandMessage(command, fileMessage));
    }

//    /**
//     * Метод обрабатывает полученное от клиента подтверждение успешного получения
//     * обновленного списка файлов клиента в облачном хранилище
//     * @param messageObject - объект сообщения(команды)
//     */
//    private void onUploadFileOkClientResponse(CommandMessage messageObject) {
//        //FIXME fill me!
//        server.printMsg("Server.respondOnUploadFileOK command: " + messageObject.getCommand());
//    }
    private void onUploadFileOkClientResponse(TCPConnection tcpConnection, CommandMessage messageObject) {
        //FIXME fill me!
        server.printMsg("Server.respondOnUploadFileOK command: " + messageObject.getCommand());
    }

//    /**
//     * Метод обрабатывает полученное от клиента сообщение об ошибке получения обновленного
//     * списка файлов клиента в облачном хранилище
//     * @param messageObject - объект сообщения(команды)
//     */
//    private void onUploadFileErrorClientResponse(CommandMessage messageObject) {
//        //FIXME fill me!
//        server.printMsg("Server.respondOnUploadFileError command: " + messageObject.getCommand());
//    }
    private void onUploadFileErrorClientResponse(TCPConnection tcpConnection, CommandMessage messageObject) {
        //FIXME fill me!
        server.printMsg("Server.respondOnUploadFileError command: " + messageObject.getCommand());
    }

//    /**
//     * Метод обработки запроса от клиента на скачивание целого файла клиента из директории в
//     * сетевом хранилище.
//     * @param messageObject - объект сообщения(команды)
//     */
//    private void onDownloadFileClientRequest(CommandMessage messageObject) {
//        //вынимаем объект файлового сообщения из объекта сообщения(команды)
//        fileMessage = (FileMessage) messageObject.getMessageObject();
//        //инициируем объект файлового хендлера
//        fileCommandHandler = new FileCommandHandler(fileMessage);
//        //вынимаем заданную директорию сетевого хранилища из объекта сообщения(команды)
//        storageDir = fileMessage.getFromDir();
//        //вынимаем заданную клиентскую директорию из объекта сообщения(команды)
//        clientDir = fileMessage.getToDir();
//        //собираем целевую директорию пользователя в сетевом хранилище
//        String fromDir = userStorageRoot;//сбрасываем до корневой папки пользователя в сетевом хранилище
//        fromDir = fromDir.concat("/").concat(storageDir);//добавляем значение подпапки
//        //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
//        int command = Commands.SERVER_RESPONSE_FILE_DOWNLOAD_ERROR;
//        //создаем объект файлового сообщения
//        fileMessage = new FileMessage(storageDir, clientDir, fileMessage.getFilename());
//        //если скачивание прошло удачно
//        if(fileCommandHandler.downloadFile(server, fileMessage, fromDir)){
//            //проверяем сохраненный файл по контрольной сумме//FIXME
//            if(true){
//                //отправляем сообщение на сервер: подтверждение, что все прошло успешно
//                command = Commands.SERVER_RESPONSE_FILE_DOWNLOAD_OK;
//            }
//        }
//        //отправляем объект сообщения(команды) клиенту//FIXME где брать логин?
//        server.sendToClient("login1", new CommandMessage(command, fileMessage));
//    }
    private void onDownloadFileClientRequest(TCPConnection tcpConnection, CommandMessage messageObject) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        fileMessage = (FileMessage) messageObject.getMessageObject();
        //инициируем объект файлового хендлера
        fileCommandHandler = new FileCommandHandler(fileMessage);
        //вынимаем заданную директорию сетевого хранилища из объекта сообщения(команды)
        storageDir = fileMessage.getFromDir();
        //вынимаем заданную клиентскую директорию из объекта сообщения(команды)
        clientDir = fileMessage.getToDir();
        //собираем целевую директорию пользователя в сетевом хранилище
        String fromDir = userStorageRoot;//сбрасываем до корневой папки пользователя в сетевом хранилище
        fromDir = fromDir.concat("/").concat(storageDir);//добавляем значение подпапки
        //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
        int command = Commands.SERVER_RESPONSE_FILE_DOWNLOAD_ERROR;
        //создаем объект файлового сообщения
        fileMessage = new FileMessage(storageDir, clientDir, fileMessage.getFilename());
        //если скачивание прошло удачно
        if(fileCommandHandler.downloadFile(server, fileMessage, fromDir)){
            //проверяем сохраненный файл по контрольной сумме//FIXME
            if(true){
                //отправляем сообщение на сервер: подтверждение, что все прошло успешно
                command = Commands.SERVER_RESPONSE_FILE_DOWNLOAD_OK;
            }
        }
        //отправляем объект сообщения(команды) клиенту//FIXME где брать логин?
        server.sendToClient(tcpConnection, new CommandMessage(command, fileMessage));
    }

//    /**
//     * Метод обрабатывает полученное от клиента подтверждение успешного сохранения целого файла,
//     * скачанного из облачного хранилища
//     * @param messageObject - объект сообщения(команды)
//     */
//    private void onDownloadFileOkClientResponse(CommandMessage messageObject) {
//        //FIXME fill me!
//        server.printMsg("Server.respondOnDownloadFileOK command: " + messageObject.getCommand());
//    }
    private void onDownloadFileOkClientResponse(TCPConnection tcpConnection, CommandMessage messageObject) {
        //FIXME fill me!
        server.printMsg("Server.respondOnDownloadFileOK command: " + messageObject.getCommand());
    }

//    /**
//     * Метод обрабатывает полученное от клиента сообщение об ошибке сохранения целого файла,
//     * скачанного из облачного хранилища
//     * @param messageObject - объект сообщения(команды)
//     */
//    private void onDownloadFileErrorClientResponse(CommandMessage messageObject) {
//        //FIXME fill me!
//        server.printMsg("Server.respondOnDownloadFileError command: " + messageObject.getCommand());
//    }
    private void onDownloadFileErrorClientResponse(TCPConnection tcpConnection, CommandMessage messageObject) {
        //FIXME fill me!
        server.printMsg("Server.respondOnDownloadFileError command: " + messageObject.getCommand());
    }
}