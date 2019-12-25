package utils;

import control.StorageTest;
import messages.DirectoryMessage;
import messages.FileFragmentMessage;
import messages.FileMessage;
import tcp.TCPConnection;
import utils.handlers.DirectoryCommandHandler;

import java.nio.file.Paths;

/**
 * The client class for recognizing command messages and control command handlers.
 */
public class CommandMessageManager {
    //принимаем объект тестера
    private StorageTest tester;
    //объявляем объект файлового обработчика
    private FileUtils fileUtils;

    //объявляем объект хендлера для операций с директориями
    private DirectoryCommandHandler directoryCommandHandler;

    //объявляем переменную типа команды
    private int command;

    public CommandMessageManager(StorageTest tester) {
        this.tester = tester;
        //инициируем объект хендлера для операций с директориями
        directoryCommandHandler = new DirectoryCommandHandler();
        //инициируем объект файлового обработчика
        fileUtils = new FileUtils();
    }

    /**
     * Метот распознает тип команды и обрабатывает ее.
     * @param commandMessage - объект сообщения(команды)
     */
    public void recognizeAndArrangeMessageObject(CommandMessage commandMessage) {
        //выполняем операции в зависимости от типа полученного сообщения(команды)
        switch (commandMessage.getCommand()) {
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
            //обрабатываем полученный от сервера ответ на запрос на скачивание с фрагментом файла из облачного хранилища
            case Commands.SERVER_RESPONSE_FILE_FRAGS_DOWNLOAD_OK:
                //вызываем метод обработки ответа от сервера с файлом-фрагментом
                //в директорию клиента
                onDownloadFileFragOkServerResponse(commandMessage);
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
        //если сохранение прошло удачно
        if(fileUtils.saveFile(toDir, fileMessage)){//FIXME см.выше
            //отправляем сообщение на сервер: подтверждение, что все прошло успешно
            command = Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_OK;
        //если что-то пошло не так
        } else {
            //выводим сообщение
            tester.printMsg("(Client)" + fileUtils.getMsg());
            //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
            command = Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_ERROR;
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

    /**
     * Метод обработки полученного от сервера ответа на запрос на скачивание с фрагментом файла из облачного хранилища
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadFileFragOkServerResponse(CommandMessage commandMessage) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileFragmentMessage fileFragmentMessage = (FileFragmentMessage) commandMessage.getMessageObject();

        //собираем целевую директорию на клиенте
        //сбрасываем до корневой папки
        String toTempDir = tester.getClientDefaultRoot();
        // добавляем временную директорию клиента из объекта сообщения(команды)
        toTempDir = toTempDir.concat("/").concat(fileFragmentMessage.getToTempDir());
        //создаем объект пути к папке с загруженным файлом
        String toDir = Paths.get(toTempDir).getParent().toString();//FIXME переделать на Path?

        //если сохранение полученного фрагмента файла во временную папку сетевого хранилища прошло удачно
        if(fileUtils.saveFileFragment(toTempDir, fileFragmentMessage)){

            //FIXME продумать, что отравлять серверу в ответ на присланный фрагмент и как это обрабатывать на сервере
//            //отправляем сообщение на сервер: подтверждение, что все прошло успешно
//            command = Commands.CLIENT_RESPONSE_FILE_FRAG_DOWNLOAD_OK;
            //если что-то пошло не так
        } else {
            //выводим сообщение
            tester.printMsg("(Client)" + fileUtils.getMsg());

            //FIXME продумать, что отравлять серверу в ответ на присланный фрагмент и как это обрабатывать на сервере
//            //инициируем переменную типа команды - ответ об ошибке
//            command = Commands.CLIENT_RESPONSE_FILE_FRAG_DOWNLOAD_ERROR;
        }
        //если это последний фрагмент
        if(fileFragmentMessage.isFinalFileFragment()){
            //если корректно собран файл из фрагментов сохраненных во временную папку
            if(fileUtils.compileFileFragments(toTempDir, toDir, fileFragmentMessage)){

                //FIXME продумать, что отравлять серверу в ответ на присланный фрагмент и как это обрабатывать на сервере
//                //ответ сервера, что сборка файла из загруженных фрагментов прошла успешно
//                command = Commands.CLIENT_RESPONSE_FILE_FRAGS_DOWNLOAD_OK;

                //если что-то пошло не так
            } else {
                //выводим сообщение
                tester.printMsg("(Client)" + fileUtils.getMsg());

                //FIXME продумать, что отравлять серверу в ответ на присланный фрагмент и как это обрабатывать на сервере
//                //инициируем переменную типа команды - ответ об ошибке
//                command = Commands.CLIENT_RESPONSE_FILE_FRAGS_DOWNLOAD_ERROR;
            }
        }
        //FIXME продумать, что отравлять серверу в ответ на присланный фрагмент и как это обрабатывать на сервере
//        //создаем объект файлового сообщения
//        fileFragmentMessage = new FileFragmentMessage(storageDir, clientDir, fileMessage.getFilename());
//        //отправляем объект сообщения(команды) на сервер
//        tester.getConnection().sendMessageObject(new CommandMessage(command, fileFragmentMessage));
    }
}