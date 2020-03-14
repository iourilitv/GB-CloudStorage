package utils;

import messages.AuthMessage;
import messages.DirectoryMessage;
import messages.FileFragmentMessage;
import messages.FileMessage;
import tcp.TCPConnection;
import tcp.TCPServer;
import utils.handlers.FileCommandHandler;
import utils.handlers.ServiceCommandHandler;

import java.nio.file.Paths;

/**
 * The server class for recognizing command messages and control command handlers.
 */
public class CommandMessageManager {
    //принимаем объект сервера
    private TCPServer server;
    //объявляем объект сервисного хендлера
    private ServiceCommandHandler serviceCommandHandler;
    //объявляем объект файлового хендлера
    private FileCommandHandler fileCommandHandler;
    //объявляем переменную для корневой директории пользователя в сетевом хранилище
    private String userStorageRoot;

    public CommandMessageManager(TCPServer server) {
        this.server = server;
        //инициируем объект сервисного хендлера
        serviceCommandHandler = new ServiceCommandHandler();
        //инициируем объект файлового хендлера
        fileCommandHandler = new FileCommandHandler();
        //инициируем переменную для корневой директории пользователя в сетевом хранилище
        userStorageRoot = server.getStorageRoot();//FIXME не будет ли юзер видеть все хранилище?
    }

    /**
     * Метод распознает тип команды и обрабатывает ее.
     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    public void recognizeAndArrangeMessageObject(TCPConnection tcpConnection, CommandMessage commandMessage) {
        //***блок обработки объектов сервисных сообщений(команд), полученных от клиента***
        //если подсоединившийся клиент еще не распознан
        if(tcpConnection.getClientID().equals("unknownID")){
            //если полученное от клиента сообщение это запрос на авторизацию в облачное хранилище
            if(commandMessage.getCommand() == Commands.REQUEST_SERVER_AUTH){
                //вызываем метод обработки запроса от клиента
                onAuthClientRequest(tcpConnection, commandMessage);
                return;
            }
        }

        //если сюда прошли, значит клиент авторизован
        //***блок обработки объектов НЕсервисных сообщений(команд), полученных от клиента***
        //выполняем операции в зависимости от типа полученного не сервисного сообщения(команды)
        switch (commandMessage.getCommand()) {
            //обрабатываем полученный от клиента запрос на загрузку(сохранение) файла в облачное хранилище
            case Commands.REQUEST_SERVER_FILE_UPLOAD:
                //вызываем метод обработки запроса от клиента на загрузку целого файла клиента
                // в директорию в сетевом хранилище.
                onUploadFileClientRequest(tcpConnection, commandMessage);
                break;
            //обрабатываем полученное от клиента подтверждение успешного получения обновленного
            // списка файлов клиента в облачном хранилище
            case Commands.CLIENT_RESPONSE_FILE_UPLOAD_OK:
                //вызываем метод обработки ответа клиента
                onUploadFileOkClientResponse(tcpConnection, commandMessage);
                break;
            //обрабатываем полученное от клиента сообщение об ошибке получения обновленного
            // списка файлов клиента в облачном хранилище
            case Commands.CLIENT_RESPONSE_FILE_UPLOAD_ERROR:
                //вызываем метод обработки ответа клиента
                onUploadFileErrorClientResponse(tcpConnection, commandMessage);
                break;
            //обрабатываем полученный от клиента запрос на скачивание целого файла из облачного хранилища
            case Commands.REQUEST_SERVER_FILE_DOWNLOAD:
                //вызываем метод обработки запроса от клиента на скачивание целого файла клиента
                // из директории в сетевом хранилище
                onDownloadFileClientRequest(tcpConnection, commandMessage);
                break;
            //обрабатываем полученное от клиента подтверждение успешного сохранения целого файла,
            // скачанного из облачного хранилища
            case Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_OK:
                //вызываем метод обработки ответа клиента
                onDownloadFileOkClientResponse(tcpConnection, commandMessage);
                break;
            //обрабатываем полученное от клиента сообщение об ошибке сохранения целого файла,
            // скачанного из облачного хранилища
            case Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_ERROR:
                //вызываем метод обработки ответа клиента
                onDownloadFileErrorClientResponse(tcpConnection, commandMessage);
                break;
            //обрабатываем полученный от клиента запрос на загрузку(сохранение) фрагмента файла в облачное хранилище
            case Commands.REQUEST_SERVER_FILE_FRAG_UPLOAD:
                //вызываем метод обработки запроса от клиента на загрузку файла-фрагмента
                //в директорию в сетевом хранилище.
                onUploadFileFragClientRequest(tcpConnection, commandMessage);
                break;
        }
    }

    /**
     * Метод обрабатывает полученный от клиента запрос на авторизацию в облачное хранилище
     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onAuthClientRequest(TCPConnection tcpConnection, CommandMessage commandMessage) {
        //вынимаем объект авторизационного сообщения из объекта сообщения(команды)
        AuthMessage authMessage = (AuthMessage) commandMessage.getMessageObject();
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
        //инициируем объект сообщения о директории
        DirectoryMessage directoryMessage = new DirectoryMessage();
        //формируем список файлов и папок в корневой директории клиента по умолчанию
        directoryMessage.composeFilesAndFoldersNamesList(userStorageRoot);
        //отправляем объект сообщения(команды) клиенту
        server.sendToClient(tcpConnection, new CommandMessage(command, directoryMessage));
    }

    /**
     * Метод обработки запроса от клиента на загрузку целого файла клиента в директорию в
     * сетевом хранилище.
     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadFileClientRequest(TCPConnection tcpConnection, CommandMessage commandMessage) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileMessage fileMessage = (FileMessage) commandMessage.getMessageObject();
        //вынимаем заданную директорию сетевого хранилища из объекта сообщения(команды)
        String storageDir = fileMessage.getToDir();
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
        //инициируем объект сообщения о директории
        DirectoryMessage directoryMessage = new DirectoryMessage();
        //формируем список файлов и папок в корневой директории клиента по умолчанию
        directoryMessage.composeFilesAndFoldersNamesList(userStorageRoot);
        //отправляем объект сообщения(команды) клиенту
        server.sendToClient(tcpConnection, new CommandMessage(command, directoryMessage));
    }

    /**
     * Метод обрабатывает полученное от клиента подтверждение успешного получения
     * обновленного списка файлов клиента в облачном хранилище
     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadFileOkClientResponse(TCPConnection tcpConnection, CommandMessage commandMessage) {
        //FIXME fill me!
        server.printMsg("(Server)ObjectHandler.onUploadFileOkClientResponse() command: " + commandMessage.getCommand());
    }

    /**
     * Метод обрабатывает полученное от клиента сообщение об ошибке получения обновленного
     * списка файлов клиента в облачном хранилище
     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadFileErrorClientResponse(TCPConnection tcpConnection, CommandMessage commandMessage) {
        //FIXME fill me!
        server.printMsg("(Server)ObjectHandler.onUploadFileErrorClientResponse() command: " + commandMessage.getCommand());
    }

    /**
     * Метод обработки запроса от клиента на скачивание целого файла клиента из директории в
     * сетевом хранилище.
     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadFileClientRequest(TCPConnection tcpConnection, CommandMessage commandMessage) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileMessage fileMessage = (FileMessage) commandMessage.getMessageObject();
        //вынимаем заданную директорию сетевого хранилища из объекта сообщения(команды)
        String storageDir = fileMessage.getFromDir();
        //вынимаем заданную клиентскую директорию из объекта сообщения(команды)
        String clientDir = fileMessage.getToDir();
        //собираем целевую директорию пользователя в сетевом хранилище
        String fromDir = userStorageRoot;//сбрасываем до корневой папки пользователя в сетевом хранилище
        fromDir = fromDir.concat("/").concat(storageDir);//добавляем значение подпапки
        //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
        int command = Commands.SERVER_RESPONSE_FILE_DOWNLOAD_ERROR;
        //создаем объект файлового сообщения
        fileMessage = new FileMessage(fromDir, clientDir, fileMessage.getFilename());
        //если скачивание прошло удачно
        if(fileCommandHandler.downloadFile(server, fromDir, fileMessage)){
            //проверяем сохраненный файл по контрольной сумме//FIXME
            if(true){
                //отправляем сообщение на сервер: подтверждение, что все прошло успешно
                command = Commands.SERVER_RESPONSE_FILE_DOWNLOAD_OK;
            }
        }
        //отправляем объект сообщения(команды) клиенту
        server.sendToClient(tcpConnection, new CommandMessage(command, fileMessage));
    }

    /**
     * Метод обрабатывает полученное от клиента подтверждение успешного сохранения целого файла,
     * скачанного из облачного хранилища
     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadFileOkClientResponse(TCPConnection tcpConnection, CommandMessage commandMessage) {
        //FIXME fill me!
        server.printMsg("(Server)ObjectHandler.onDownloadFileOkClientResponse() command: " + commandMessage.getCommand());
    }

    /**
     * Метод обрабатывает полученное от клиента сообщение об ошибке сохранения целого файла,
     * скачанного из облачного хранилища
     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadFileErrorClientResponse(TCPConnection tcpConnection, CommandMessage commandMessage) {
        //FIXME fill me!
        server.printMsg("(Server)ObjectHandler.onDownloadFileErrorClientResponse() command: " + commandMessage.getCommand());
    }

    /**
     * Метод обработки запроса от клиента на загрузку файла-фрагмента
     * в директорию в сетевом хранилище.
     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadFileFragClientRequest(TCPConnection tcpConnection, CommandMessage commandMessage) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileFragmentMessage fileFragmentMessage = (FileFragmentMessage) commandMessage.getMessageObject();
        //собираем целевую директорию пользователя в сетевом хранилище
        //сбрасываем до корневой папки пользователя в сетевом хранилище
        String toTempDir = userStorageRoot;
        // добавляем временную директорию сетевого хранилища из объекта сообщения(команды)
        toTempDir = toTempDir.concat("/").concat(fileFragmentMessage.getToTempDir());
        //создаем объект пути к папке с загруженным файлом
        String toDir = Paths.get(toTempDir).getParent().toString();//FIXME переделать на Path?
        //инициируем директорию для показа списка загруженных фрагментов или файла
        String directory = toTempDir;
        //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
        int command = Commands.SERVER_RESPONSE_FILE_FRAG_UPLOAD_ERROR;
        //если сохранение полученного фрагмента файла во временную папку сетевого хранилища прошло удачно
        if(fileCommandHandler.saveUploadedFileFragment(server, toTempDir, fileFragmentMessage)){
            //проверяем сохраненный файл по контрольной сумме//FIXME
            if(true){
                //отправляем сообщение на сервер: подтверждение, что все прошло успешно
                command = Commands.SERVER_RESPONSE_FILE_FRAG_UPLOAD_OK;
            }
        }
        //если это последний фрагмент
        if(fileFragmentMessage.isFinalFileFragment()){
            //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
            command = Commands.SERVER_RESPONSE_FILE_FRAGS_UPLOAD_ERROR;
            //если корректно собран файл из фрагментов сохраненных во временную папку
            if(fileCommandHandler.compileUploadedFileFragments(server, toTempDir, toDir, fileFragmentMessage)){
                //ответ сервера, что сборка файла из загруженных фрагментов прошла успешно
                command = Commands.SERVER_RESPONSE_FILE_FRAGS_UPLOAD_OK;
                //устанавливаем финальное значение папки для показа загруженного файла
                directory = toDir;
            }
        }

        //инициируем объект сообщения о директории
        DirectoryMessage directoryMessage = new DirectoryMessage();
        //формируем список файлов и папок в корневой директории клиента по умолчанию
        directoryMessage.composeFilesAndFoldersNamesList(directory);
        //отправляем объект сообщения(команды) клиенту
        server.sendToClient(tcpConnection, new CommandMessage(command, directoryMessage));
    }
}