package utils.handlers;

import messages.AuthMessage;
import messages.Commands;
import messages.FileMessage;
import tcp.TCPServer;
import utils.CommandMessage;

import java.io.IOException;

/**
 * The server class for recognizing command messages and control command handlers.
 */
public class ObjectHandler {
    //принимаем объект сервера
    TCPServer server;
    //объявляем объект файлового сообщения для полного файла
    FileMessage fileMessage;
    //объявляем объект файлового хендлера
    FileCommandHandler fileCommandHandler;
    //объявляем пременную для клиентской директории
    String clientDir;
    //объявляем пременную для директории в сетевом хранилище
    String storageDir;

    public ObjectHandler(TCPServer server) {
        this.server = server;
    }

    /**
     * Метот распознает тип команды и обрабатывает ее.
     * @param messageObject - объект сообщения(команды)
     */
    public void recognizeAndArrangeMessageObject(CommandMessage messageObject) {
        //выполняем операции в зависимости от типа полученного сообщения(команды)
        switch (messageObject.getCommand()) {
            //обрабатываем полученный от клиента запрос на загрузку(сохранение) файла в облачное хранилище
            case Commands.REQUEST_SERVER_FILE_UPLOAD:
                //вызываем метод обработки запроса от клиента на загрузку целого файла клиента
                // в директорию в сетевом хранилище.
                uploadFile(messageObject);
                break;
            //обрабатываем полученный от клиента запрос на скачивание файла из облачного хранилища
            case Commands.REQUEST_SERVER_FILE_DOWNLOAD:
                //вызываем метод обработки запроса от клиента на скачивание целого файла клиента
                // из директории в сетевом хранилище
                downloadFile(messageObject);
                break;
            //обрабатываем полученный от клиента запрос на авторизацию в облачное хранилище
            case Commands.REQUEST_SERVER_AUTH:
//                    ServiceCommandHandler serviceCommandHandler = (ServiceCommandHandler) messageObject.getCommandHandler();
                AuthMessage authMessage = (AuthMessage) messageObject.getMessageObject();
                ServiceCommandHandler serviceCommandHandler = new ServiceCommandHandler(authMessage);
                serviceCommandHandler.authorizeUser();
                break;
        }

    }

    /**
     * Метод обработки запроса от клиента на загрузку целого файла клиента в директорию в
     * сетевом хранилище.
     * @param messageObject - объект сообщения(команды)
     */
    private void uploadFile(CommandMessage messageObject) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        fileMessage = (FileMessage) messageObject.getMessageObject();
        //инициируем объект файлового хендлера
        fileCommandHandler = new FileCommandHandler(fileMessage);
        //вынимаем заданную клиентскую директорию из объекта сообщения(команды)
        clientDir = fileMessage.getFromDir();
        //вынимаем заданную директорию сетевого хранилища из объекта сообщения(команды)
        storageDir = fileMessage.getToDir();
        //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
        int command = Commands.SERVER_RESPONSE_FILE_UPLOAD_ERROR;
        //если сохранение прошло удачно
        if(fileCommandHandler.saveUploadedFile(server, storageDir, fileMessage)){
            //проверяем сохраненный файл по контрольной сумме//FIXME
            if(true){
                //отправляем сообщение на сервер: подтверждение, что все прошло успешно
                command = Commands.SERVER_RESPONSE_FILE_UPLOAD_OK;
            }
        }
        //создаем объект файлового сообщения
        fileMessage = new FileMessage(storageDir, clientDir, fileMessage.getFilename());
        //отправляем объект сообщения(команды) клиенту
        server.sendToClient("login1", new CommandMessage(command, fileMessage));
    }

    /**
     * Метод обработки запроса от клиента на скачивание целого файла клиента из директории в
     * сетевом хранилище.
     * @param messageObject - объект сообщения(команды)
     */
    private void downloadFile(CommandMessage messageObject) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        fileMessage = (FileMessage) messageObject.getMessageObject();
        //инициируем объект файлового хендлера
        fileCommandHandler = new FileCommandHandler(fileMessage);
        //вынимаем заданную директорию сетевого хранилища из объекта сообщения(команды)
        storageDir = fileMessage.getFromDir();
        //вынимаем заданную клиентскую директорию из объекта сообщения(команды)
        clientDir = fileMessage.getToDir();
        //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
        int command = Commands.SERVER_RESPONSE_FILE_DOWNLOAD_ERROR;
        //создаем объект файлового сообщения
        fileMessage = new FileMessage(storageDir, clientDir, fileMessage.getFilename());
        //если скачивание прошло удачно
        if(fileCommandHandler.downloadFile(server, fileMessage)){
            //проверяем сохраненный файл по контрольной сумме//FIXME
            if(true){
                //отправляем сообщение на сервер: подтверждение, что все прошло успешно
                command = Commands.SERVER_RESPONSE_FILE_DOWNLOAD_OK;
            }
        }
        //отправляем объект сообщения(команды) клиенту
        server.sendToClient("login1", new CommandMessage(command, fileMessage));
    }
}