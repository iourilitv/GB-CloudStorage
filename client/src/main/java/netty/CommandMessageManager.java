package netty;

import control.CloudStorageClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import javafx.GUIController;
import messages.DirectoryMessage;
import messages.FileFragmentMessage;
import messages.FileMessage;
import utils.CommandMessage;
import utils.Commands;
import utils.FileUtils;

import java.nio.file.Paths;
import java.util.Arrays;

/**
 * The client class for recognizing command messages and control command handlers.
 */
public class CommandMessageManager extends ChannelInboundHandlerAdapter {
    //принимаем объект исходящего хэндлера
    private CloudStorageClient storageClient;
    //принимаем объект файлового обработчика
    private FileUtils fileUtils;
    //принимаем объект соединения
    private ChannelHandlerContext ctx;
    //принимаем объект контроллера GUI
    private GUIController guiController;

    //объявляем переменную типа команды
    private int command;

    public CommandMessageManager(CloudStorageClient storageClient) {
        this.storageClient = storageClient;
        //принимаем объект файлового обработчика
        fileUtils = storageClient.getFileUtils();
        //принимаем объект контроллера GUI
        guiController = storageClient.getGuiController();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        //принимаем объект соединения
        this.ctx = ctx;
    }

    /**
     * Метод отрабатываем событие получение объекта сообщения.
     * Преобразует объект сообщения в объект соманды и запускает его обработку.
     * @param ctx - объект сетевого соединения
     * @param msgObject - объект сообщения
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msgObject) {
        try {
            //десериализуем объект сообщения(команды)
            CommandMessage commandMessage = (CommandMessage) msgObject;

            //TODO temporarily
            printMsg("[client]CommandMessageManager.channelRead() - command: "
                    + commandMessage.getCommand());

            //распознаем и обрабатываем полученный объект сообщения(команды)
            recognizeAndArrangeMessageObject(commandMessage);
        }
        finally {
            ReferenceCountUtil.release(msgObject);
        }
    }

    /**
     * Метот распознает тип команды и обрабатывает ее.
     * @param commandMessage - объект сообщения(команды)
     */
    public void recognizeAndArrangeMessageObject(CommandMessage commandMessage) {
        //выполняем операции в зависимости от типа полученного сообщения(команды)
        switch (commandMessage.getCommand()) {
            //обрабатываем полученное от сервера подтверждение успешного подключения клиента
            case Commands.SERVER_NOTIFICATION_CLIENT_CONNECTED:
                //вызываем метод обработки ответа сервера
                onServerConnectedResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера подтверждение успешной авторизации в облачное хранилище
            case Commands.SERVER_RESPONSE_AUTH_OK:
            //обрабатываем полученный от сервера ответ об успешном переименовании файла или папки в облачном хранилище
            case Commands.SERVER_RESPONSE_RENAME_FILE_OBJECT_OK:
            //обрабатываем полученный от сервера ответ об успешном удалении файла или папки в облачном хранилище
            case Commands.SERVER_RESPONSE_DELETE_FILE_OBJECT_OK:
                //выводим в GUI список файлов и папок в корневой пользовательской директории в сетевом хранилище
                updateStorageItemListInGUI(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке авторизации в облачное хранилище
            case Commands.SERVER_RESPONSE_AUTH_ERROR:
                //вызываем метод обработки ответа сервера
                onAuthErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученный ответ сервера с массивом файловых объектов в заданной
            // директории пользователя в сетевом хранилище, если нет ошибок
            case Commands.SERVER_RESPONSE_FILE_OBJECTS_LIST_OK:
                //вызываем метод обработки ответа сервера
                onFileObjectsListOkServerResponse(commandMessage);
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
     * Метод обрабатывает полученное от сервера подтверждение успешного подключения клиента
     * @param commandMessage - объект сообщения(команды)
     */
    private void onServerConnectedResponse(CommandMessage commandMessage) {
        //отправляем запрос на авторизацию в обланое хранилище
        storageClient.startAuthorization(ctx);
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке авторизации в облачное хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onAuthErrorServerResponse(CommandMessage commandMessage) {
        //FIXME
        // вывести в GUI сообщение об ошибке
        // повторить запрос на авторизацию с новыми данными логина и пароля
        printMsg("[client]CommandMessageManager.onAuthErrorServerResponse() - Something wrong with your login and password!");
    }

    /**
     * Метод обрабатывает полученный ответ сервера с массивом файловых объектов в заданной
     * директории пользователя в сетевом хранилище, если нет ошибок
     * @param commandMessage - объект сообщения(команды)
     */
    private void onFileObjectsListOkServerResponse(CommandMessage commandMessage) {
        //вынимаем объект сообщения о директории из объекта сообщения(команды)
        DirectoryMessage directoryMessage = (DirectoryMessage) commandMessage.getMessageObject();
        //обновляем список элементов списка серверной части
        guiController.updateStorageItemListInGUI(directoryMessage.getDirectory(),
                directoryMessage.getFileObjectsList());
    }

    /**
     * Метод обрабатывает полученное от сервера подтверждение успешной загрузки(сохранения)
     * файла в облачное хранилище.
     * Выводит в GUI полученный от сервера список файлов и папок в корневой пользовательской директории в сетевом хранилище.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadFileOkServerResponse(CommandMessage commandMessage) {
        //вынимаем объект сообщения о директории из объекта сообщения(команды)
        DirectoryMessage directoryMessage = (DirectoryMessage) commandMessage.getMessageObject();
        //выводим в GUI список файлов и папок в корневой пользовательской директории в сетевом хранилище
        guiController.updateStorageItemListInGUI(directoryMessage.getDirectory(),
                directoryMessage.getFileObjectsList());

        //TODO temporarily
        printMsg("[client]CommandMessageManager.onUploadFileOkServerResponse() - " +
                "command: " + commandMessage.getCommand() +
                ". directoryMessage.getDirectory(): " + directoryMessage.getDirectory() +
                ". directoryMessage.getFileObjectsList(): " +
                Arrays.toString(directoryMessage.getFileObjectsList()));

    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке загрузки(сохранения)
     * файла в облачное хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadFileErrorServerResponse(CommandMessage commandMessage) {
        //FIXME fill me!
        printMsg("[client]CommandMessageManager.onUploadFileErrorServerResponse() command: " + commandMessage.getCommand());
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
        //вынимаем заданную клиентскую директорию заданную относительно CLIENT_ROOT из объекта сообщения(команды)
        String clientDir = fileMessage.getToDir();
        //собираем реальную текущую директорию на клиенте
        String realToDir = guiController.realClientDirectory(clientDir);
        //если сохранение прошло удачно
        if(fileUtils.saveFile(realToDir, fileMessage)){
            //обновляем список файловых объектов на клиенте
            guiController.updateClientItemListInGUI(clientDir);
            //отправляем сообщение на сервер: подтверждение, что все прошло успешно
            command = Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_OK;
        //если что-то пошло не так
        } else {
            //выводим сообщение
            printMsg("[client]" + fileUtils.getMsg());
            //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
            command = Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_ERROR;
        }
        //создаем объект файлового сообщения
        fileMessage = new FileMessage(storageDir, clientDir, fileMessage.getFilename());
        //отправляем объект сообщения(команды) на сервер
        ctx.writeAndFlush(new CommandMessage(command, fileMessage));
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке
     * скачивания файла из облачного хранилища
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadFileErrorServerResponse(CommandMessage commandMessage) {
        //FIXME fill me!
        printMsg("[client]CommandMessageManager.onDownloadFileErrorServerResponse() command: " + commandMessage.getCommand());
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
        String toTempDir = storageClient.getClientDefaultDirectory();
        // добавляем временную директорию клиента из объекта сообщения(команды)
        toTempDir = toTempDir.concat("/").concat(fileFragmentMessage.getToTempDir());
        //создаем объект пути к папке с загруженным файлом
        String toDir = Paths.get(toTempDir).getParent().toString();//FIXME переделать на Path?

        //если сохранение полученного фрагмента файла во временную папку сетевого хранилища прошло удачно
        if(fileUtils.saveFileFragment(toTempDir, fileFragmentMessage)){

            //FIXME продумать, что отравлять серверу в ответ на присланный фрагмент и как это обрабатывать на сервере
            System.out.println();
//            //отправляем сообщение на сервер: подтверждение, что все прошло успешно
//            command = Commands.CLIENT_RESPONSE_FILE_FRAG_DOWNLOAD_OK;
            //если что-то пошло не так
        } else {
            //выводим сообщение
            printMsg("[client]" + fileUtils.getMsg());

            //FIXME продумать, что отравлять серверу в ответ на присланный фрагмент и как это обрабатывать на сервере
//            //инициируем переменную типа команды - ответ об ошибке
//            command = Commands.CLIENT_RESPONSE_FILE_FRAG_DOWNLOAD_ERROR;
        }
        //если это последний фрагмент
        if(fileFragmentMessage.isFinalFileFragment()){
            //если корректно собран файл из фрагментов сохраненных во временную папку
            if(fileUtils.compileFileFragments(toTempDir, toDir, fileFragmentMessage)){

                //FIXME продумать, что отравлять серверу в ответ на присланный фрагмент и как это обрабатывать на сервере
                System.out.println();
//                //ответ сервера, что сборка файла из загруженных фрагментов прошла успешно
//                command = Commands.CLIENT_RESPONSE_FILE_FRAGS_DOWNLOAD_OK;

                //если что-то пошло не так
            } else {
                //выводим сообщение
                printMsg("[client]" + fileUtils.getMsg());

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

    /**
     * Метод выводим в GUI список файлов и папок в корневой пользовательской директории в сетевом хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void updateStorageItemListInGUI(CommandMessage commandMessage) {
        //вынимаем объект сообщения о директории из объекта сообщения(команды)
        DirectoryMessage directoryMessage = (DirectoryMessage) commandMessage.getMessageObject();
        //выводим в GUI список файлов и папок в корневой пользовательской директории в сетевом хранилище
        guiController.updateStorageItemListInGUI(directoryMessage.getDirectory(),
                directoryMessage.getFileObjectsList());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void printMsg(String msg){
        storageClient.printMsg(msg);
    }
}