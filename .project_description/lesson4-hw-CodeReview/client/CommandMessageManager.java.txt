package utils;

import control.StorageTest;
import messages.DirectoryMessage;
import messages.FileMessage;
import utils.handlers.DirectoryCommandHandler;
import utils.handlers.FileCommandHandler;

/**
 * The client class for recognizing command messages and control command handlers.
 */
public class CommandMessageManager {
    //принимаем объект тестера
    private StorageTest tester;
    //объявляем объект файлового хендлера
    private FileCommandHandler fileCommandHandler;
    //объявляем объект хендлера для операций с директориями
    private DirectoryCommandHandler directoryCommandHandler;

    public CommandMessageManager(StorageTest tester) {
        this.tester = tester;
        //инициируем объект хендлера для операций с директориями
        directoryCommandHandler = new DirectoryCommandHandler();
        //инициируем объект файлового хендлера
        fileCommandHandler = new FileCommandHandler();
    }

    /**
     * Метот распознает тип команды и обрабатывает ее.
     * @param commandMessage - объект сообщения(команды)
     */
    public void recognizeAndArrangeMessageObject(CommandMessage commandMessage) {
        //выполняем операции в зависимости от типа полученного сообщения(команды)
        switch (commandMessage.getCommand()) {
            //обрабатываем полученное от сервера подтверждение успешной загрузки(сохранения)
            // файла в облачное хранилище
            case Commands.SERVER_RESPONSE_FILE_UPLOAD_OK:
                //вызываем метод обработки ответа сервера
                onUploadFileOkServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке загрузки(сохранения)
            // файла в облачное хранилище
            case Commands.SERVER_RESPONSE_FILE_UPLOAD_ERROR:
                //вызываем метод обработки ответа сервера
                onUploadFileErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера подтверждение успешного скачивания файла из облачного хранилища
            case Commands.SERVER_RESPONSE_FILE_DOWNLOAD_OK:
                //вызываем метод обработки ответа сервера со скачанным целым файлом внутри
                onDownloadFileOkServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке скачивания файла из облачного хранилища
            case Commands.SERVER_RESPONSE_FILE_DOWNLOAD_ERROR:
                //вызываем метод обработки ответа сервера
                onDownloadFileErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера подтверждение успешной авторизации в облачное хранилище
            case Commands.SERVER_RESPONSE_AUTH_OK:
                //вызываем метод обработки ответа сервера
                onAuthOkServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке авторизации в облачное хранилище
            case Commands.SERVER_RESPONSE_AUTH_ERROR:
                //вызываем метод обработки ответа сервера
                onAuthErrorServerResponse(commandMessage);
                break;
        }
    }

    /**
     * Метод обрабатывает полученное от сервера подтверждение успешной авторизации в облачное хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onAuthOkServerResponse(CommandMessage commandMessage) {
        //вынимаем объект сообщения о директории из объекта сообщения(команды)
        DirectoryMessage directoryMessage = (DirectoryMessage) commandMessage.getMessageObject();
        //выводим в GUI список файлов и папок в корневой пользовательской директории в сетевом хранилище
        directoryCommandHandler.updateStorageFilesAndFoldersListInGUI(directoryMessage.getDirectory(),
                directoryMessage.getNamesList());

        //TODO temporarily
        //сбрасываем защелку
        tester.getCountDownLatch().countDown();
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке авторизации в облачное хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onAuthErrorServerResponse(CommandMessage commandMessage) {
        //FIXME
        // вывести в GUI сообщение об ошибке
        // повторить запрос на авторизацию с новыми данными логина и пароля

        tester.printMsg("(Client)ObjectHandler.onAuthErrorServerResponse() - Something wrong with your login and password!");
    }

    /**
     * Метод обрабатывает полученное от сервера подтверждение успешной загрузки(сохранения)
     * файла в облачное хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadFileOkServerResponse(CommandMessage commandMessage) {
        //вынимаем объект сообщения о директории из объекта сообщения(команды)
        DirectoryMessage directoryMessage = (DirectoryMessage) commandMessage.getMessageObject();
        //выводим в GUI список файлов и папок в корневой пользовательской директории в сетевом хранилище
        directoryCommandHandler.updateStorageFilesAndFoldersListInGUI(directoryMessage.getDirectory(),
                directoryMessage.getNamesList());

        //TODO temporarily
        //сбрасываем защелку
        tester.getCountDownLatch().countDown();
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке загрузки(сохранения)
     * файла в облачное хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadFileErrorServerResponse(CommandMessage commandMessage) {
        //FIXME fill me!
        tester.printMsg("(Client)ObjectHandler.onUploadFileErrorServerResponse() command: " + commandMessage.getCommand());
    }

    /**
     * Метод обработки ответа сервера со скачанным целым файлом внутри
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadFileOkServerResponse(CommandMessage commandMessage) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileMessage fileMessage = (FileMessage) commandMessage.getMessageObject();
        //вынимаем директорию заданную относительно userStorageRoot в сетевом хранилище из объекта сообщения(команды)
        String storageDir = fileMessage.getFromDir();
        //вынимаем заданную клиентскую директорию из объекта сообщения(команды)
        String clientDir = fileMessage.getToDir();

        //FIXME придется указывать абсолютный путь, если будет выбор папки клиента
        //собираем текущую директорию на клиенте
        String toDir = tester.getClientDefaultRoot();
        toDir = toDir.concat("/").concat(clientDir);

        //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
        int command = Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_ERROR;
        //если сохранение прошло удачно
        if(fileCommandHandler.saveDownloadedFile(tester, toDir, fileMessage)){//FIXME см.выше
            //проверяем сохраненный файл по контрольной сумме//FIXME
            if(true){
                //отправляем сообщение на сервер: подтверждение, что все прошло успешно
                command = Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_OK;
            }
        }
        //создаем объект файлового сообщения
        fileMessage = new FileMessage(storageDir, clientDir, fileMessage.getFilename());
        //отправляем объект сообщения(команды) на сервер
        tester.getConnection().sendMessageObject(new CommandMessage(command, fileMessage));
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке
     * скачивания файла из облачного хранилища
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadFileErrorServerResponse(CommandMessage commandMessage) {
        //FIXME fill me!
        tester.printMsg("Client.onDownloadFileErrorServerResponse() command: " + commandMessage.getCommand());
    }
}