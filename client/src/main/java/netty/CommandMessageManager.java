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
            //обрабатываем полученное от сервера подтверждение успешной регистрации
            // нового пользователя в облачное хранилище
            case Commands.SERVER_RESPONSE_REGISTRATION_OK:
                //вызываем метод обработки ответа сервера
                onRegistrationOKServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера подтверждение успешной авторизации в облачное хранилище
            case Commands.SERVER_RESPONSE_AUTH_OK:
                //вызываем метод обработки ответа сервера
                onAuthOKServerResponse(commandMessage);
                break;
            //обрабатываем полученный ответ сервера с массивом файловых объектов в заданной
            // директории пользователя в сетевом хранилище, если нет ошибок
            case Commands.SERVER_RESPONSE_ITEMS_LIST_OK:
            //обрабатываем полученное от сервера подтверждение успешного создания новой папки
            //с массивом файловых объектов в текущей директории пользователя в облачном хранилище
            case Commands.SERVER_RESPONSE_CREATE_NEW_FOLDER_OK:
            //обрабатываем полученное от сервера подтверждение успешной загрузке(сохранении)
            // файла в облачное хранилище
            case Commands.SERVER_RESPONSE_UPLOAD_ITEM_OK:
            //обрабатываем полученное от сервера подтверждение успешной загрузке(сохранении)
            // всего большого файла(по фрагментно) в облачное хранилище
            case Commands.SERVER_RESPONSE_UPLOAD_FILE_FRAGS_OK:
            //обрабатываем полученный от сервера ответ об успешном переименовании файла или папки в облачном хранилище
            case Commands.SERVER_RESPONSE_RENAME_ITEM_OK:
            //обрабатываем полученный от сервера ответ об успешном удалении файла или папки в облачном хранилище
            case Commands.SERVER_RESPONSE_DELETE_ITEM_OK:
                //выводим в GUI список файлов и папок в корневой пользовательской директории в сетевом хранилище
                updateStorageItemListInGUI(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке регистрации в облачное хранилище
            case Commands.SERVER_RESPONSE_REGISTRATION_ERROR:
                //вызываем метод обработки ответа сервера
                onRegistrationErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке авторизации в облачное хранилище
            case Commands.SERVER_RESPONSE_AUTH_ERROR:
                //вызываем метод обработки ответа сервера
                onAuthErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке при создании новой папки
            //в текущей директории пользователя в облачном хранилище
            case Commands.SERVER_RESPONSE_CREATE_NEW_FOLDER_ERROR:
                //вызываем метод обработки ответа сервера
                onCreateNewFolderErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке загрузки(сохранения)
            // файла в облачное хранилище
            case Commands.SERVER_RESPONSE_UPLOAD_ITEM_ERROR:
                //вызываем метод обработки ответа сервера
                onUploadItemErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера подтверждение успешного скачивания файла из облачного хранилища
            case Commands.SERVER_RESPONSE_DOWNLOAD_ITEM_OK:
                //вызываем метод обработки ответа сервера со скачанным целым файлом внутри
                onDownloadItemOkServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке скачивания файла из облачного хранилища
            case Commands.SERVER_RESPONSE_DOWNLOAD_ITEM_ERROR:
                //вызываем метод обработки ответа сервера
                onDownloadFileErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученный от сервера ответ на запрос на скачивание с фрагментом файла из облачного хранилища
            case Commands.SERVER_RESPONSE_DOWNLOAD_FILE_FRAG_OK:
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
    }

    /**
     * Метод обрабатывает полученное от сервера подтверждение успешной регистрации
     * нового пользователя в облачное хранилище.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onRegistrationOKServerResponse(CommandMessage commandMessage) {
        //устанавливаем режим отображения "Авторизован"
        guiController.setAuthMode(true);
        //выводим сообщение в метку уведомлений в GUI
        showTextInGUI("You have registered in the Cloud Storage. Press \"Authorization\" button.");
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке регистрации
     * нового пользователя в облачное хранилище.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onRegistrationErrorServerResponse(CommandMessage commandMessage) {
        //выводим сообщение в нижнюю метку GUI
        showTextInGUI("Something wrong with your registration data! Insert them again.");
        //открываем окно авторизации
        guiController.openAuthWindowInGUI();

        //FIXME включить режим регистрационной формы
    }

    /**
     * Метод обрабатывает полученное от сервера подтверждение успешной авторизации в облачное хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onAuthOKServerResponse(CommandMessage commandMessage) {
        //устанавливаем режим отображения "Авторизован"
        guiController.setAuthMode(true);
        //выводим в GUI список файлов и папок в корневой пользовательской директории в сетевом хранилище
        updateStorageItemListInGUI(commandMessage);
    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке авторизации в облачное хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onAuthErrorServerResponse(CommandMessage commandMessage) {
        //выводим сообщение в нижнюю метку GUI
        showTextInGUI("Something wrong with your login or password! Insert them again.");
        //открываем окно авторизации
        guiController.openAuthWindowInGUI();
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
     * Метод обработки ответа сервера со скачанным целым объектом элемента(файлом) внутри
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadItemOkServerResponse(CommandMessage commandMessage) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileMessage fileMessage = (FileMessage) commandMessage.getMessageObject();
        //если сохранение прошло удачно
        if(storageClient.downloadItem(fileMessage.getClientDirectoryItem(), fileMessage.getItem(),
                fileMessage.getData(), fileMessage.getFileSize())){
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
     * Метод обработки ответа от сервера на загрузку файла-фрагмента
     * в директорию в клиенте.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadFileFragOkServerResponse(CommandMessage commandMessage) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileFragmentMessage fileFragMsg = (FileFragmentMessage) commandMessage.getMessageObject();
        //если сохранение полученного фрагмента файла во временную папку клиента прошло удачно
        //объявляем переменную типа команды
        int command;
        if(storageClient.downloadItemFragment(fileFragMsg)){
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
        //если это последний фрагмент
        if(fileFragMsg.isFinalFileFragment()){
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
        storageClient.printMsg(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}