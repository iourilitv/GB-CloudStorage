package netty;

import control.CloudStorageServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import messages.DirectoryMessage;
import messages.FileFragmentMessage;
import messages.FileMessage;
import utils.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The server's class for recognizing command messages and control command handlers.
 */
public class CommandMessageManager extends ChannelInboundHandlerAdapter {
    //принимаем объект соединения
    private ChannelHandlerContext ctx;
    //принимаем объект контроллера сетевого хранилища
    private final CloudStorageServer storageServer;

    //принимаем объект реального пути к корневой директории пользователя в сетевом хранилище
    private Path userStorageRoot;
    //принимаем логин пользователя
    private String login;
    //объявляем объект файлового обработчика
    private FileUtils fileUtils;
    //объявляем переменную типа команды
    private Commands command;

    public CommandMessageManager(CloudStorageServer storageServer) {
        this.storageServer = storageServer;
        //принимаем объект файлового обработчика
        fileUtils = storageServer.getFileUtils();
    }

    /**
     * Метод в полученном объекте сообщения распознает тип команды и обрабатывает ее.
     * @param ctx - объект соединения netty, установленного с клиентом
     * @param msg - входящий объект сообщения
     * @throws Exception - исключение
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //принимаем объект соединения
        this.ctx = ctx;
        //инициируем из объекта сообщения объект команды
        CommandMessage commandMessage = (CommandMessage) msg;
        //если сюда прошли, значит клиент авторизован
        //***блок обработки объектов сообщений(команд), полученных от клиента***
        //выполняем операции в зависимости от типа полученного не сервисного сообщения(команды)
        switch (commandMessage.getCommand()) {
            //обрабатываем полученный от AuthGateway проброшенный запрос на авторизацию клиента в облачное хранилище
            //возвращаем список объектов в корневой директорию пользователя в сетевом хранилище.
            case SERVER_RESPONSE_AUTH_OK:
                //вызываем метод обработки запроса от AuthGateway
                onAuthClientRequest(commandMessage);
                break;
            //обрабатываем полученный от клиента запрос на отсоединение пользователя от сервера
            case REQUEST_SERVER_DISCONNECT:
                //вызываем метод обработки запроса от AuthGateway
                onDisconnectClientRequest(commandMessage);
                break;
            //обрабатываем полученный от клиента запрос на список объектов файлов и папок
            // в заданной директории в облачном хранилище
            case REQUEST_SERVER_ITEMS_LIST:
                //вызываем метод обработки запроса от клиента
                onDirectoryItemsListClientRequest(commandMessage);
                break;
            //обрабатываем полученный от клиента запрос на создание новой директории в облачном хранилище
            case REQUEST_SERVER_CREATE_NEW_FOLDER:
                //вызываем метод обработки запроса от клиента
                onCreateNewFolderClientRequest(commandMessage);
                break;
            //обрабатываем полученный от клиента запрос на загрузку(сохранение) файла в облачное хранилище
            case REQUEST_SERVER_UPLOAD_ITEM:
                //вызываем метод обработки запроса от клиента на загрузку целого файла клиента
                // в директорию в сетевом хранилище.
                onUploadItemClientRequest(commandMessage);
                break;
            //обрабатываем полученный от клиента запрос на скачивание целого файла из облачного хранилища
            case REQUEST_SERVER_DOWNLOAD_ITEM:
                //вызываем метод обработки запроса от клиента на скачивание целого файла клиента
                // из директории в сетевом хранилище
                onDownloadItemClientRequest(commandMessage);
                break;
            //обрабатываем полученный от клиента запрос на загрузку(сохранение) фрагмента файла в облачное хранилище
            case REQUEST_SERVER_UPLOAD_FILE_FRAG:
                //вызываем метод обработки запроса от клиента на загрузку файла-фрагмента
                //в директорию в сетевом хранилище.
                onUploadFileFragClientRequest(commandMessage);
                break;
            //обрабатываем полученный от клиента запрос на переименование файла или папки
            // в заданной директории в сетевом хранилище
            case REQUEST_SERVER_RENAME_ITEM:
                //вызываем метод обработки запроса от клиента
                onRenameItemClientRequest(commandMessage);
                break;
            //обрабатываем полученный от клиента запрос на удаление файла или папки
            // в заданной директории в сетевом хранилище
            case REQUEST_SERVER_DELETE_ITEM:
                //вызываем метод обработки запроса от клиента
                onDeleteItemClientRequest(commandMessage);
                break;
        }
    }

    /**
     * Метод обрабатывает полученный от AuthGateway проброшенный запрос на авторизацию клиента в облачное хранилище
     * Возвращает список объектов в корневой директорию пользователя в сетевом хранилище.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onAuthClientRequest(CommandMessage commandMessage) {
        //принимаем логин пользователя
        login = commandMessage.getMessage();
        //формируем объект реального пути к его корневой директории клиента в сетевом хранилище
        userStorageRoot = Paths.get(storageServer.getSTORAGE_ROOT_PATH().toString(), login);
        //инициируем объект для принятой директории сетевого хранилища
        Item storageDirItem = new Item(storageServer.getSTORAGE_DEFAULT_DIR());
        //отправляем объект сообщения(команды) клиенту со списком файлов и папок в
        // заданной директории клиента в сетевом хранилище
        sendItemsList(storageDirItem, commandMessage.getCommand());
        //удаляем входящий хэндлер AuthGateway, т.к. после авторизации он больше не нужен
        printMsg("[server]CommandMessageManager.onAuthClientRequest() - " +
                "removed pipeline: " + ctx.channel().pipeline().remove(AuthGateway.class));
    }

    /**
     * Метод обрабатывает полученный от клиента запрос на отсоединение пользователя от сервера.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDisconnectClientRequest(CommandMessage commandMessage) {
        //отправляем объект сообщения(команды) клиенту
        ctx.writeAndFlush(new CommandMessage(Commands.SERVER_RESPONSE_DISCONNECT_OK));
        //удаляем пользователя из списка авторизованных, если он был авторизован
        storageServer.getUsersAuthController().deAuthorizeUser(login);
        //закрываем соединение с клиентом(вроде на ctx.close(); не отключал соединение?)
        ctx.channel().close();
    }

    /**
     * Метод обрабатывает полученный от клиента запрос на список объектов(файлов и папок)
     * в заданной директории в облачном хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDirectoryItemsListClientRequest(CommandMessage commandMessage) {
        //вынимаем объект сообщения о директории из объекта сообщения(команды)
        DirectoryMessage directoryMessage = (DirectoryMessage) commandMessage.getMessageObject();
        //инициируем объект для принятой директории сетевого хранилища
        Item storageDirItem = storageServer.createStorageDirectoryItem(
                directoryMessage.getDirectoryPathname(), userStorageRoot);
        //отправляем объект сообщения(команды) клиенту со списком объектов(файлов и папок) в
        // заданной директории клиента в сетевом хранилище
        sendItemsList(storageDirItem, Commands.SERVER_RESPONSE_ITEMS_LIST_OK);
    }

    /**
     * обрабатываем полученный от клиента запрос на создание новой директории в облачном хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onCreateNewFolderClientRequest(CommandMessage commandMessage) {
        //вынимаем объект сообщения о директории из объекта сообщения(команды)
        DirectoryMessage directoryMessage = (DirectoryMessage) commandMessage.getMessageObject();
        //если новая папка создана удачно
        if(storageServer.createNewFolder(directoryMessage, userStorageRoot)){
            //отправляем сообщение на сервер: подтверждение, что все прошло успешно
            command = Commands.SERVER_RESPONSE_CREATE_NEW_FOLDER_OK;
            //если что-то пошло не так
        } else {
            //выводим сообщение
            printMsg("[server]" + fileUtils.getMsg());
            //инициируем переменную типа команды
            command = Commands.SERVER_RESPONSE_CREATE_NEW_FOLDER_ERROR;
        }
        //инициируем объект для принятой директории сетевого хранилища
        Item storageDirItem = storageServer.createStorageDirectoryItem(
                directoryMessage.getDirectoryPathname(), userStorageRoot);
        //отправляем объект сообщения(команды) клиенту со списком объектов(файлов и папок) в
        // заданной директории клиента в сетевом хранилище
        sendItemsList(storageDirItem, command);
    }

    /**
     * Метод обработки запроса от клиента на загрузку целого объекта(файла) клиента
     * в заданную директорию в сетевом хранилище.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadItemClientRequest(CommandMessage commandMessage) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileMessage fileMessage = (FileMessage) commandMessage.getMessageObject();
        //если сохранение прошло удачно
//        if(storageServer.uploadItem(fileMessage.getStorageDirectoryItem(), fileMessage.getItem(),
//                fileMessage.getData(), fileMessage.getFileSize(), userStorageRoot)){
        if(storageServer.uploadItem(fileMessage, userStorageRoot)){

            //отправляем сообщение на сервер: подтверждение, что все прошло успешно
            command = Commands.SERVER_RESPONSE_UPLOAD_ITEM_OK;
            //если что-то пошло не так
        } else {
            //выводим сообщение
            printMsg("[server]" + fileUtils.getMsg());
            //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
            command = Commands.SERVER_RESPONSE_UPLOAD_ITEM_ERROR;
        }
        //отправляем объект сообщения(команды) клиенту со списком объектов(файлов и папок) в
        // заданной директории клиента в сетевом хранилище
        sendItemsList(fileMessage.getStorageDirectoryItem(), command);
    }

    /**
     * Метод обработки запроса от клиента на скачивание целого объекта элемента(файла)
     * из директории в сетевом хранилище в клиента.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadItemClientRequest(CommandMessage commandMessage) throws IOException {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileMessage fileMessage = (FileMessage) commandMessage.getMessageObject();
        //запускаем процесс скачивания и отправки объекта элемента
        storageServer.downloadItem(fileMessage, userStorageRoot, ctx);
    }

    /**
     * Метод обрабатывает полученное от клиента подтверждение успешного сохранения целого файла,
     * скачанного из облачного хранилища
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadFileOkClientResponse(CommandMessage commandMessage) {
        //FIXME fill me!
        printMsg("[server]CommandMessageManager.onDownloadFileOkClientResponse() command: " + commandMessage.getCommand());
    }

    /**
     * Метод обрабатывает полученное от клиента сообщение об ошибке сохранения целого файла,
     * скачанного из облачного хранилища
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadFileErrorClientResponse(CommandMessage commandMessage) {
        //FIXME fill me!
        printMsg("[server]CommandMessageManager.onDownloadFileErrorClientResponse() command: " + commandMessage.getCommand());
    }

    /**
     * Метод обработки запроса от клиента на загрузку файла-фрагмента
     * в директорию в сетевом хранилище.
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadFileFragClientRequest(CommandMessage commandMessage) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileFragmentMessage fileFragMsg = (FileFragmentMessage) commandMessage.getMessageObject();
        //если сохранение полученного фрагмента файла во временную папку сетевого хранилища прошло удачно
        if(storageServer.uploadItemFragment(fileFragMsg, userStorageRoot)){
            //отправляем сообщение на сервер: подтверждение, что все прошло успешно
            command = Commands.SERVER_RESPONSE_UPLOAD_FILE_FRAG_OK;
            //если что-то пошло не так
        } else {
            //выводим сообщение
            printMsg("[server]" + fileUtils.getMsg());
            //инициируем переменную типа команды - ответ об ошибке
            command = Commands.SERVER_RESPONSE_UPLOAD_FILE_FRAG_ERROR;
        }
        //если это последний фрагмент
        if(fileFragMsg.isFinalFileFragment()){
            //если корректно собран файл из фрагментов сохраненных во временную папку
            if(storageServer.compileItemFragments(fileFragMsg, userStorageRoot)){
                //ответ сервера, что сборка файла из загруженных фрагментов прошла успешно
                command = Commands.SERVER_RESPONSE_UPLOAD_FILE_FRAGS_OK;
            //если что-то пошло не так
            } else {
                //выводим сообщение
                printMsg("[server]" + fileUtils.getMsg());
                //инициируем переменную типа команды - ответ об ошибке
                command = Commands.SERVER_RESPONSE_UPLOAD_FILE_FRAGS_ERROR;
            }
            //отправляем объект сообщения(команды) клиенту со списком объектов(файлов и папок) в
            // заданной директории клиента в сетевом хранилище
            sendItemsList(fileFragMsg.getToDirectoryItem(), command);
        }
    }

    /**
     * Метод обрабатываем полученный от клиента запрос на переименование объекта элемента
     * списка(файла или папки) в заданной директории в сетевом хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onRenameItemClientRequest(CommandMessage commandMessage) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileMessage fileMessage = (FileMessage) commandMessage.getMessageObject();
        //если сохранение прошло удачно
        if(storageServer.renameStorageItem(fileMessage.getItem(), fileMessage.getNewName(), userStorageRoot)){
            //отправляем сообщение на сервер: подтверждение, что все прошло успешно
            command = Commands.SERVER_RESPONSE_RENAME_ITEM_OK;
            //если что-то пошло не так
        } else {
            //выводим сообщение
            printMsg("[server]" + fileUtils.getMsg());
            //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
            command = Commands.SERVER_RESPONSE_RENAME_ITEM_ERROR;
        }
        //отправляем объект сообщения(команды) клиенту со списком объектов(файлов и папок) в
        // заданной директории клиента в сетевом хранилище
        sendItemsList(fileMessage.getStorageDirectoryItem(), command);
    }

    /**
     * Метод обрабатываем полученный от клиента запрос на удаление объекта элемента
     * списка(файла или папки) в заданной директории в сетевом хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDeleteItemClientRequest(CommandMessage commandMessage) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileMessage fileMessage = (FileMessage) commandMessage.getMessageObject();
        //если удаление прошло удачно
        if(storageServer.deleteClientItem(fileMessage.getItem(), userStorageRoot)){
            //отправляем сообщение на сервер: подтверждение, что все прошло успешно
            command = Commands.SERVER_RESPONSE_DELETE_ITEM_OK;
            //если что-то пошло не так
        } else {
            //выводим сообщение
            printMsg("[server]" + fileUtils.getMsg());
            //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
            command = Commands.SERVER_RESPONSE_DELETE_ITEM_ERROR;
        }
        //отправляем объект сообщения(команды) клиенту со списком объектов(файлов и папок) в
        // заданной директории клиента в сетевом хранилище
        sendItemsList(fileMessage.getStorageDirectoryItem(), command);
    }

    /**
     * Метод формирует и отправляет клиенту сообщение(команду) с массивом объектов
     * в заданной директории пользователя в сетевом хранилище.
     * @param storageDirItem - объект заданной директории пользователя в сетевом хранилище
     * @param command - комманда об успешном или не успешном формировании списка
     */
    private void sendItemsList(Item storageDirItem, Commands command) {
        //инициируем объект сообщения о директории с массивом объектов
        // в заданной директории клиента в сетевом хранилище
        DirectoryMessage directoryMessage = new DirectoryMessage(storageDirItem,
                storageServer.storageItemsList(storageDirItem, userStorageRoot));
        //отправляем объект сообщения(команды) клиенту
        ctx.writeAndFlush(new CommandMessage(command, directoryMessage));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void printMsg(String msg){
        storageServer.printMsg(msg);
    }
}