package utils.handlers;

import messages.AuthMessage;
import messages.Commands;
import messages.DirectoryMessage;
import messages.FileMessage;
import tcp.TCPClient;
import utils.CommandMessage;

/**
 * The client class for recognizing command messages and control command handlers.
 */
public class ObjectHandler {
    //принимаем объект клиента
    private TCPClient client;
    //объявляем объект авторизационного сообщения
    private AuthMessage authMessage;
    //объявляем объект сервисного хендлера
    private ServiceCommandHandler serviceCommandHandler;
    //объявляем объект файлового сообщения для полного файла
    private FileMessage fileMessage;
    //объявляем объект файлового хендлера
    private FileCommandHandler fileCommandHandler;
    //объявляем переменную для клиентской директории
    private String clientDir;
    //объявляем переменную для заданной относительно userStorageRoot директории в сетевом хранилище
    private String storageDir;
    //объявляем объект сообщения о директории
    DirectoryMessage directoryMessage;
    //объявляем объект хендлера для операций с директориями
    DirectoryCommandHandler directoryCommandHandler;

    public ObjectHandler(TCPClient client) {
        this.client = client;
    }

    /**
     * Метот распознает тип команды и обрабатывает ее.
     * @param messageObject - объект сообщения(команды)
     */
    public void recognizeAndArrangeMessageObject(CommandMessage messageObject) {
        //выполняем операции в зависимости от типа полученного сообщения(команды)
        switch (messageObject.getCommand()) {
            //обрабатываем полученное от сервера подтверждение успешной загрузки(сохранения)
            // файла в облачное хранилище
            case Commands.SERVER_RESPONSE_FILE_UPLOAD_OK:
                //вызываем метод обработки ответа сервера
                onUploadFileOkServerResponse(messageObject);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке загрузки(сохранения)
            // файла в облачное хранилище
            case Commands.SERVER_RESPONSE_FILE_UPLOAD_ERROR:
                //вызываем метод обработки ответа сервера
                onUploadFileErrorServerResponse(messageObject);
                break;
            //обрабатываем полученное от сервера подтверждение успешного скачивания файла из облачного хранилища
            case Commands.SERVER_RESPONSE_FILE_DOWNLOAD_OK:
                //вызываем метод обработки ответа сервера со скачанным целым файлом внутри
                onDownloadFileOkServerResponse(messageObject);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке скачивания файла из облачного хранилища
            case Commands.SERVER_RESPONSE_FILE_DOWNLOAD_ERROR:
                //вызываем метод обработки ответа сервера
                onDownloadFileErrorServerResponse(messageObject);
                break;
            //обрабатываем полученное от сервера подтверждение успешной авторизации в облачное хранилище
            case Commands.SERVER_RESPONSE_AUTH_OK:
                //вызываем метод обработки ответа сервера
                onAuthOkServerResponse(messageObject);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке авторизации в облачное хранилище
            case Commands.SERVER_RESPONSE_AUTH_ERROR:
                //вызываем метод обработки ответа сервера
                onAuthErrorServerResponse(messageObject);
                break;
        }
    }

    /**
     * Метод обрабатывает полученное от сервера подтверждение успешной авторизации в облачное хранилище
     * @param messageObject - объект сообщения(команды)
     */
    private void onAuthOkServerResponse(CommandMessage messageObject) {
        //вынимаем объект сообщения о директории из объекта сообщения(команды)
        directoryMessage = (DirectoryMessage) messageObject.getMessageObject();
        //инициируем объект хендлера для операций с директориями
        directoryCommandHandler = new DirectoryCommandHandler(directoryMessage);
        //выводим в GUI список файлов и папок в корневой пользовательской директории в сетевом хранилище
        directoryCommandHandler.updateStorageFilesAndFoldersListInGUI(directoryMessage.getDirectory(),
                directoryMessage.getNamesList());

        //TODO temporarily
        //сбрасываем защелку
        client.getCountDownLatch().countDown();
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке авторизации в облачное хранилище
     * @param messageObject - объект сообщения(команды)
     */
    private void onAuthErrorServerResponse(CommandMessage messageObject) {
        //FIXME
        // вывести в GUI сообщение об ошибке
        // повторить запрос на авторизацию с новыми данными логина и пароля

        client.printMsg("(Client)ObjectHandler.onAuthErrorServerResponse() - Something wrong with your login and password!");
    }

    /**
     * Метод обрабатывает полученное от сервера подтверждение успешной загрузки(сохранения)
     * файла в облачное хранилище
     * @param messageObject - объект сообщения(команды)
     */
    private void onUploadFileOkServerResponse(CommandMessage messageObject) {
        //вынимаем объект сообщения о директории из объекта сообщения(команды)
        directoryMessage = (DirectoryMessage) messageObject.getMessageObject();
        //инициируем объект хендлера для операций с директориями
        directoryCommandHandler = new DirectoryCommandHandler(directoryMessage);
        //выводим в GUI список файлов и папок в корневой пользовательской директории в сетевом хранилище
        directoryCommandHandler.updateStorageFilesAndFoldersListInGUI(directoryMessage.getDirectory(),
                directoryMessage.getNamesList());

        //TODO temporarily
        //сбрасываем защелку
        client.getCountDownLatch().countDown();
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке загрузки(сохранения)
     * файла в облачное хранилище
     * @param messageObject - объект сообщения(команды)
     */
    private void onUploadFileErrorServerResponse(CommandMessage messageObject) {
        //FIXME fill me!
        client.printMsg("(Client)ObjectHandler.onUploadFileErrorServerResponse() command: " + messageObject.getCommand());
    }

    /**
     * Метод обработки ответа сервера со скачанным целым файлом внутри
     * @param messageObject - объект сообщения(команды)
     */
    private void onDownloadFileOkServerResponse(CommandMessage messageObject) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        fileMessage = (FileMessage) messageObject.getMessageObject();
        //инициируем объект файлового хендлера
        fileCommandHandler = new FileCommandHandler(fileMessage);
        //вынимаем заданную директорию сетевого хранилища из объекта сообщения(команды)
        storageDir = fileMessage.getFromDir();
        //вынимаем заданную клиентскую директорию из объекта сообщения(команды)
        clientDir = fileMessage.getToDir();

        //FIXME придется указывать абсолютный путь, если будет выбор папки клиента
        //собираем текущую директорию на клиенте
        String toDir = client.getClientDefaultRoot();
        toDir = toDir.concat("/").concat(storageDir);
        //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
        int command = Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_ERROR;
        //если сохранение прошло удачно
        if(fileCommandHandler.saveDownloadedFile(client, toDir, fileMessage)){//FIXME см.выше
            //проверяем сохраненный файл по контрольной сумме//FIXME
            if(true){
                //отправляем сообщение на сервер: подтверждение, что все прошло успешно
                command = Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_OK;
            }
        }
        //создаем объект файлового сообщения
        fileMessage = new FileMessage(storageDir, clientDir, fileMessage.getFilename());
        //отправляем объект сообщения(команды) на сервер
        client.getConnection().sendMessageObject(new CommandMessage(command, fileMessage));
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке
     * скачивания файла из облачного хранилища
     * @param messageObject - объект сообщения(команды)
     */
    private void onDownloadFileErrorServerResponse(CommandMessage messageObject) {
        //FIXME fill me!
        client.printMsg("Client.onDownloadFileErrorServerResponse() command: " + messageObject.getCommand());
    }
}