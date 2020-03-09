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

/**
 * The client's class for recognizing command messages and control command handlers.
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
        //передаем объект соединения в объект клиента сетевого хранилища
        storageClient.setCtx(ctx);
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

            //TODO Upd 21. Добавить в лог.
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
     * Метод распознает тип команды и обрабатывает ее.
     * @param commandMessage - объект сообщения(команды)
     */
    public void recognizeAndArrangeMessageObject(CommandMessage commandMessage) {
        //выполняем операции в зависимости от типа полученного сообщения(команды)
        switch (commandMessage.getCommand()) {
            //обрабатываем полученное от сервера подтверждение успешного подключения клиента
            case SERVER_NOTIFICATION_CLIENT_CONNECTED:
                //вызываем метод обработки ответа сервера
                onServerConnectedResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера подтверждение готовности отключения клиента
            case SERVER_RESPONSE_DISCONNECT_OK:
                //вызываем метод обработки ответа сервера
                onServerDisconnectOKServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке при попытке отключения клиента
            case SERVER_RESPONSE_DISCONNECT_ERROR:
                //вызываем метод обработки ответа сервера
                onServerDisconnectErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера подтверждение успешной регистрации
            // нового пользователя в облачное хранилище
            case SERVER_RESPONSE_REGISTRATION_OK:
                //вызываем метод обработки ответа сервера
                onRegistrationOKServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера подтверждение успешной авторизации в облачное хранилище
            case SERVER_RESPONSE_AUTH_OK:
                //вызываем метод обработки ответа сервера
                onAuthOKServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера подтверждение успешного
            // изменения пароля пользователя в облачное хранилище
            case SERVER_RESPONSE_CHANGE_PASSWORD_OK:
                //вызываем метод обработки ответа сервера
                onChangePasswordServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке изменения пароля пользователя в облачное хранилище
            case SERVER_RESPONSE_CHANGE_PASSWORD_ERROR:
                //вызываем метод обработки ответа сервера
                onChangePasswordErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученный ответ сервера с массивом файловых объектов в заданной
            // директории пользователя в сетевом хранилище, если нет ошибок
            case SERVER_RESPONSE_ITEMS_LIST_OK:
            //обрабатываем полученное от сервера подтверждение успешного создания новой папки
            //с массивом файловых объектов в текущей директории пользователя в облачном хранилище
            case SERVER_RESPONSE_CREATE_NEW_FOLDER_OK:
            //обрабатываем полученное от сервера подтверждение успешной загрузки(сохранении)
            // файла в облачное хранилище
            case SERVER_RESPONSE_UPLOAD_ITEM_OK:
            //обрабатываем полученное от сервера подтверждение успешной загрузки(сохранении)
            // всего большого файла(по фрагментно) в облачное хранилище
            case SERVER_RESPONSE_UPLOAD_FILE_FRAGS_OK:
            //обрабатываем полученный от сервера ответ об успешном переименовании файла или папки в облачном хранилище
            case SERVER_RESPONSE_RENAME_ITEM_OK:
            //обрабатываем полученный от сервера ответ об успешном удалении файла или папки в облачном хранилище
            case SERVER_RESPONSE_DELETE_ITEM_OK:
                //выводим в GUI список файлов и папок в корневой пользовательской директории в сетевом хранилище
                updateStorageItemListInGUI(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке регистрации в облачное хранилище
            case SERVER_RESPONSE_REGISTRATION_ERROR:
                //вызываем метод обработки ответа сервера
                onRegistrationErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке авторизации в облачное хранилище
            case SERVER_RESPONSE_AUTH_ERROR:
                //вызываем метод обработки ответа сервера
                onAuthErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке при создании новой папки
            //в текущей директории пользователя в облачном хранилище
            case SERVER_RESPONSE_CREATE_NEW_FOLDER_ERROR:
                //вызываем метод обработки ответа сервера
                onCreateNewFolderErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке загрузки(сохранения)
            // файла в облачное хранилище
            case SERVER_RESPONSE_UPLOAD_ITEM_ERROR:
                //вызываем метод обработки ответа сервера
                onUploadItemErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера подтверждение успешной загрузки(сохранении)
            // фрагмента файла в облачное хранилище
            case SERVER_RESPONSE_UPLOAD_FILE_FRAG_OK:
                //вызываем метод обработки ответа сервера
                onUploadFileFragOkServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке загрузки(сохранения)
            // фрагмента файла в облачное хранилище
            case SERVER_RESPONSE_UPLOAD_FILE_FRAG_ERROR:
                //вызываем метод обработки ответа сервера
                onUploadFileFragErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера подтверждение успешного скачивания файла из облачного хранилища
            case SERVER_RESPONSE_DOWNLOAD_ITEM_OK:
                //вызываем метод обработки ответа сервера со скачанным целым файлом внутри
                onDownloadItemOkServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке скачивания файла из облачного хранилища
            case SERVER_RESPONSE_DOWNLOAD_ITEM_ERROR:
                //вызываем метод обработки ответа сервера
                onDownloadFileErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученный от сервера ответ на запрос на скачивание с фрагментом файла из облачного хранилища
            case SERVER_RESPONSE_DOWNLOAD_FILE_FRAG_OK:
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
        //открываем окно авторизации
        guiController.openAuthWindowInGUI();
        //устанавливаем режим отображения GUI "Подключен"
        guiController.setDisconnectedMode(false);
    }

    /**
     * Метод обрабатывает полученное от сервера подтверждение готовности отключения клиента.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onServerDisconnectOKServerResponse(CommandMessage commandMessage) {
        //запускаем процесс отключения от сервера и переход в автономный режим или закрытия приложения
        showTextInGUI("The disconnecting from server is allowed!");
        //запускаем процесс отключения от сервера
        storageClient.disconnect();
        //выводим текст в метку
        showTextInGUI("Server disconnected. Press \"Connect to the Cloud Storage\" button.");

    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке при попытке отключения клиента.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onServerDisconnectErrorServerResponse(CommandMessage commandMessage) {
        showTextInGUI("The disconnecting from server is not allowed!");
    }

    /**
     * Метод обрабатывает полученное от сервера подтверждение успешной регистрации
     * нового пользователя в облачное хранилище.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onRegistrationOKServerResponse(CommandMessage commandMessage) {
        //выводим сообщение в метку уведомлений в GUI
        showTextInGUI("You have registered in the Cloud Storage. Press \"Authorization\" button.");
        //закрываем регистрационное окно и открываем авторизационное окно
        guiController.setRegisteredAndUnauthorisedMode();
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке регистрации
     * нового пользователя в облачное хранилище.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onRegistrationErrorServerResponse(CommandMessage commandMessage) {
        //выводим сообщение в нижнюю метку GUI
        showTextInGUI("Probably this login has been registered before! Try again.");
    }

    /**
     * Метод обрабатывает полученное от сервера подтверждение успешной авторизации в облачное хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onAuthOKServerResponse(CommandMessage commandMessage) {
        //устанавливаем режим отображения "Авторизован"
        guiController.setAuthorizedMode(true);
        //выводим в GUI список файлов и папок в корневой пользовательской директории в сетевом хранилище
        updateStorageItemListInGUI(commandMessage);
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке авторизации в облачное хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onAuthErrorServerResponse(CommandMessage commandMessage) {
        //выводим сообщение в нижнюю метку GUI
        showTextInGUI("Something wrong with your login or password! Are you registered?");
    }

    /**
     * Метод обрабатывает полученное от сервера подтверждение успешного
     * изменения пароля пользователя в облачное хранилище.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onChangePasswordServerResponse(CommandMessage commandMessage) {
        //выводим сообщение в GUI
        showTextInGUI("Your password has been changed successfully!");
        //возвращаем режим отображения GUI в режим "авторизован"
        guiController.setAuthorizedMode(true);
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке изменения пароля пользователя в облачное хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onChangePasswordErrorServerResponse(CommandMessage commandMessage) {
        //выводим сообщение в GUI
        showTextInGUI("Your new data has not been accepted. Try again!");
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке при создании новой папки
     * в текущей директории пользователя в облачном хранилище.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onCreateNewFolderErrorServerResponse(CommandMessage commandMessage) {
        //выводим сообщение в нижнюю метку GUI
        showTextInGUI("Something wrong with a new folder creating! Try it again.");
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке загрузки(сохранения)
     * объекта элемента(файла) в облачное хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadItemErrorServerResponse(CommandMessage commandMessage) {
        //FIXME fill me!
        printMsg("[client]CommandMessageManager.onUploadFileErrorServerResponse() command: " + commandMessage.getCommand());
    }

    /**
     * Метод обрабатывает полученное от сервера подтверждение
     * успешной загрузки(сохранении) фрагмента файла в облачное хранилище.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadFileFragOkServerResponse(CommandMessage commandMessage) {
        //вынимаем объект сообщения фрагмента файла из объекта сообщения(команды)
        FileFragmentMessage fileFragMsg = (FileFragmentMessage) commandMessage.getMessageObject();
        //выводим сообщение в лог
        printMsg("[client]CommandMessageManager.onUploadFileFragOkServerResponse() - " +
                "uploaded fragments: " + fileFragMsg.getCurrentFragNumber() +
                "/" + fileFragMsg.getTotalFragsNumber());
        //выводим в GUI информацию с номером загруженного фрагмента файла
        storageClient.showTextInGUI("File uploading. Completed fragment: " +
                fileFragMsg.getCurrentFragNumber() +
                "/" + fileFragMsg.getTotalFragsNumber());
        //сбрасываем защелку в цикле отправки фрагментов
        fileUtils.getCountDownLatch().countDown();
        //если это финальный фрагмент
        if(fileFragMsg.getCurrentFragNumber() == fileFragMsg.getTotalFragsNumber()){
            //выводим в GUI информацию о компиляции итогового файла из фрагментов в сетевом хранилише
            storageClient.showTextInGUI("File uploading. Final compiling entire file...");
        }
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение
     * об ошибке загрузки(сохранения) фрагмента файла в облачное хранилище.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadFileFragErrorServerResponse(CommandMessage commandMessage) {
        //вынимаем объект сообщения фрагмента файла из объекта сообщения(команды)
        FileFragmentMessage fileFragMsg = (FileFragmentMessage) commandMessage.getMessageObject();
        //выводим сообщение в лог
        printMsg("[client]CommandMessageManager.onUploadFileFragErrorServerResponse() - " +
                "Error of downloading the fragment: " + fileFragMsg.getCurrentFragNumber() +
                "/" + fileFragMsg.getTotalFragsNumber());
        //повторяем отправку на загрузку этого фрагмента заново
        storageClient.sendFileFragment(fileFragMsg, Commands.REQUEST_SERVER_UPLOAD_FILE_FRAG);
    }

    /**
     * Метод обработки ответа сервера со скачанным целым объектом элемента(файлом) внутри
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadItemOkServerResponse(CommandMessage commandMessage) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileMessage fileMessage = (FileMessage) commandMessage.getMessageObject();
        //если сохранение прошло удачно
        if(storageClient.downloadItem(fileMessage)){
            //очищаем метку уведомлений
            showTextInGUI("");
            //обновляем список файловых объектов на клиенте
            guiController.updateClientItemListInGUI(fileMessage.getClientDirectoryItem());
        //если что-то пошло не так
        } else {
            //печатаем сообщение в консоль
            printMsg("[client]" + fileUtils.getMsg());
            //выводим сообщение в GUI
            showTextInGUI(fileUtils.getMsg());
        }
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке
     * скачивания объекта элемента(файла) из облачного хранилища
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadFileErrorServerResponse(CommandMessage commandMessage) {
        //FIXME fill me!
        printMsg("[client]CommandMessageManager.onDownloadFileErrorServerResponse() command: " + commandMessage.getCommand());
    }

    /**
     * Метод обработки ответа от сервера на скачивание файла-фрагмента
     * в директорию в клиенте.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadFileFragOkServerResponse(CommandMessage commandMessage) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileFragmentMessage fileFragMsg = (FileFragmentMessage) commandMessage.getMessageObject();
        //объявляем переменную типа команды
        Commands command;
        //если сохранение полученного фрагмента файла во временную папку клиента прошло удачно
        if(storageClient.downloadItemFragment(fileFragMsg)){
            //выводим в GUI информацию с номером загруженного фрагмента файла
            storageClient.showTextInGUI("File downloading. Completed fragment: " +
                    fileFragMsg.getCurrentFragNumber() +
                    "/" + fileFragMsg.getTotalFragsNumber());
            //отправляем сообщение на сервер: подтверждение, что все прошло успешно
            command = Commands.CLIENT_RESPONSE_DOWNLOAD_FILE_FRAG_OK;
            //если что-то пошло не так
        } else {
            //печатаем сообщение в консоль
            printMsg("[client]" + fileUtils.getMsg());
            //выводим сообщение в GUI
            showTextInGUI(fileUtils.getMsg());
            //инициируем переменную типа команды - ответ об ошибке
            command = Commands.CLIENT_RESPONSE_DOWNLOAD_FILE_FRAG_ERROR;
        }
        //обнуляем байтовый массив в объект сообщения фрагмента файла
        fileFragMsg.setData(null);
        //отправляем объект сообщения(команды) серверу
        ctx.writeAndFlush(new CommandMessage(command, fileFragMsg));
        //если это последний фрагмент
        if(fileFragMsg.isFinalFileFragment()){
            //выводим в GUI информацию о компиляции итогового файла из фрагментов в сетевом хранилише
            storageClient.showTextInGUI("File downloading. Final compiling entire file...");
            //если корректно собран файл из фрагментов сохраненных во временную папку
            if(storageClient.compileItemFragments(fileFragMsg)){
                //очищаем метку уведомлений
                showTextInGUI("");
                //обновляем список файловых объектов на клиенте
                guiController.updateClientItemListInGUI(
                        fileFragMsg.getToDirectoryItem());
                //если что-то пошло не так
            } else {
                //печатаем сообщение в консоль
                printMsg("[client]" + fileUtils.getMsg());
                //выводим сообщение в GUI
                showTextInGUI(fileUtils.getMsg());
            }
        }
    }

    /**
     * Метод выводит в GUI список объектов(файлов и папок) в корневой пользовательской директории
     * в сетевом хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void updateStorageItemListInGUI(CommandMessage commandMessage) {
        //вынимаем объект сообщения о директории из объекта сообщения(команды)
        DirectoryMessage directoryMessage = (DirectoryMessage) commandMessage.getMessageObject();
        //очищаем метку уведомлений
        showTextInGUI("");
        //выводим в GUI список файлов и папок в корневой пользовательской директории в сетевом хранилище
        guiController.updateStorageItemListInGUI(directoryMessage.getDirectoryItem(),
                directoryMessage.getItemsList());
    }

    /**
     * Метод выводит сообщение в нижнюю метку GUI
     * @param text - сообщение
     */
    public void showTextInGUI(String text){
        //выводим сообщение в нижнюю метку GUI
        guiController.showTextInGUI(text);
    }

    public void printMsg(String msg){
        storageClient.writeToLog(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}