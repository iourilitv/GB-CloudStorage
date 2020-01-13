package netty;

import control.CloudStorageClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import javafx.GUIController;
import messages.DirectoryMessage;
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

    //объявляем переменную типа команды
    private int command;//TODO сделать локальными?

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
            //обрабатываем полученный ответ сервера с массивом файловых объектов в заданной
            // директории пользователя в сетевом хранилище, если нет ошибок
            case Commands.SERVER_RESPONSE_ITEMS_LIST_OK:
            //обрабатываем полученное от сервера подтверждение успешной загрузке(сохранении)
            // файла в облачное хранилище
            case Commands.SERVER_RESPONSE_UPLOAD_ITEM_OK:
            //обрабатываем полученное от сервера подтверждение успешной загрузке(сохранении)
            // всего большого файла(по фрагментно) в облачное хранилище
            case Commands.SERVER_RESPONSE_FILE_FRAGS_UPLOAD_OK:
            //обрабатываем полученный от сервера ответ об успешном переименовании файла или папки в облачном хранилище
            case Commands.SERVER_RESPONSE_RENAME_ITEM_OK:
            //обрабатываем полученный от сервера ответ об успешном удалении файла или папки в облачном хранилище
            case Commands.SERVER_RESPONSE_DELETE_ITEM_OK:
                //выводим в GUI список файлов и папок в корневой пользовательской директории в сетевом хранилище
                updateStorageItemListInGUI(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке авторизации в облачное хранилище
            case Commands.SERVER_RESPONSE_AUTH_ERROR:
                //вызываем метод обработки ответа сервера
                onAuthErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера сообщение об ошибке загрузки(сохранения)
            // файла в облачное хранилище
            case Commands.SERVER_RESPONSE_UPLOAD_ITEM_ERROR:
                //вызываем метод обработки ответа сервера
                onUploadFileErrorServerResponse(commandMessage);
                break;
            //обрабатываем полученное от сервера подтверждение успешного скачивания файла из облачного хранилища
            case Commands.SERVER_RESPONSE_FILE_DOWNLOAD_OK:
                //вызываем метод обработки ответа сервера со скачанным целым файлом внутри
//                onDownloadFileOkServerResponse(commandMessage);//FIXME
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
//                onDownloadFileFragOkServerResponse(commandMessage);//FIXME
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
     * Метод обрабатывает полученное от сервера сообщение об ошибке загрузки(сохранения)
     * файла в облачное хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadFileErrorServerResponse(CommandMessage commandMessage) {
        //FIXME fill me!
        printMsg("[client]CommandMessageManager.onUploadFileErrorServerResponse() command: " + commandMessage.getCommand());
    }

//    /**
//     * Метод обработки ответа сервера со скачанным целым файлом внутри
//     * @param commandMessage - объект сообщения(команды)
//     */
//    private void onDownloadFileOkServerResponse(CommandMessage commandMessage) {
//        //вынимаем объект файлового сообщения из объекта сообщения(команды)
//        FileMessage fileMessage = (FileMessage) commandMessage.getMessageObject();
//        //вынимаем директорию заданную относительно userStorageRoot в сетевом хранилище из объекта сообщения(команды)
//        String storageDir = fileMessage.getFromDir();
//        //вынимаем заданную клиентскую директорию заданную относительно CLIENT_ROOT из объекта сообщения(команды)
//        String clientDir = fileMessage.getToDir();
//        //собираем реальную текущую директорию на клиенте
//        String realToDir = guiController.realClientDirectory(clientDir);
//        //если сохранение прошло удачно
//        if(fileUtils.saveFile(realToDir, fileMessage)){
//            //обновляем список файловых объектов на клиенте
//            guiController.updateClientItemListInGUI(clientDir);
//            //отправляем сообщение на сервер: подтверждение, что все прошло успешно
//            command = Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_OK;
//        //если что-то пошло не так
//        } else {
//            //выводим сообщение
//            printMsg("[client]" + fileUtils.getMsg());
//            //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
//            command = Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_ERROR;
//        }
//        //создаем объект файлового сообщения
//        fileMessage = new FileMessage(storageDir, clientDir, fileMessage.getFilename());
//        //отправляем объект сообщения(команды) на сервер
//        ctx.writeAndFlush(new CommandMessage(command, fileMessage));
//    }

    /**
     * Метод обрабатывает полученное от сервера сообщение об ошибке
     * скачивания файла из облачного хранилища
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadFileErrorServerResponse(CommandMessage commandMessage) {
        //FIXME fill me!
        printMsg("[client]CommandMessageManager.onDownloadFileErrorServerResponse() command: " + commandMessage.getCommand());
    }

//    /**
//     * Метод обработки полученного от сервера ответа на запрос на скачивание с фрагментом файла из облачного хранилища
//     * @param commandMessage - объект сообщения(команды)
//     */
//    private void onDownloadFileFragOkServerResponse(CommandMessage commandMessage) {
//        //вынимаем объект файлового сообщения из объекта сообщения(команды)
//        FileFragmentMessage fileFragmentMessage = (FileFragmentMessage) commandMessage.getMessageObject();
//
//        //собираем целевую директорию на клиенте
//        //сбрасываем до корневой папки
//        String toTempDir = storageClient.getClientDefaultDirectory();
//        // добавляем временную директорию клиента из объекта сообщения(команды)
//        toTempDir = toTempDir.concat("/").concat(fileFragmentMessage.getToTempDir());
//        //создаем объект пути к папке с загруженным файлом
//        String toDir = Paths.get(toTempDir).getParent().toString();//FIXME переделать на Path?
//
//        //если сохранение полученного фрагмента файла во временную папку сетевого хранилища прошло удачно
//        if(fileUtils.saveFileFragment(toTempDir, fileFragmentMessage)){
//
//            //FIXME продумать, что отравлять серверу в ответ на присланный фрагмент и как это обрабатывать на сервере
//            System.out.println();
////            //отправляем сообщение на сервер: подтверждение, что все прошло успешно
////            command = Commands.CLIENT_RESPONSE_FILE_FRAG_DOWNLOAD_OK;
//            //если что-то пошло не так
//        } else {
//            //выводим сообщение
//            printMsg("[client]" + fileUtils.getMsg());
//
//            //FIXME продумать, что отравлять серверу в ответ на присланный фрагмент и как это обрабатывать на сервере
////            //инициируем переменную типа команды - ответ об ошибке
////            command = Commands.CLIENT_RESPONSE_FILE_FRAG_DOWNLOAD_ERROR;
//        }
//        //если это последний фрагмент
//        if(fileFragmentMessage.isFinalFileFragment()){
//            //если корректно собран файл из фрагментов сохраненных во временную папку
//            if(fileUtils.compileFileFragments(toTempDir, toDir, fileFragmentMessage)){
//
//                //FIXME продумать, что отравлять серверу в ответ на присланный фрагмент и как это обрабатывать на сервере
//                System.out.println();
////                //ответ сервера, что сборка файла из загруженных фрагментов прошла успешно
////                command = Commands.CLIENT_RESPONSE_FILE_FRAGS_DOWNLOAD_OK;
//
//                //если что-то пошло не так
//            } else {
//                //выводим сообщение
//                printMsg("[client]" + fileUtils.getMsg());
//
//                //FIXME продумать, что отравлять серверу в ответ на присланный фрагмент и как это обрабатывать на сервере
////                //инициируем переменную типа команды - ответ об ошибке
////                command = Commands.CLIENT_RESPONSE_FILE_FRAGS_DOWNLOAD_ERROR;
//            }
//        }
//        //FIXME продумать, что отравлять серверу в ответ на присланный фрагмент и как это обрабатывать на сервере
////        //создаем объект файлового сообщения
////        fileFragmentMessage = new FileFragmentMessage(storageDir, clientDir, fileMessage.getFilename());
////        //отправляем объект сообщения(команды) на сервер
////        tester.getConnection().sendMessageObject(new CommandMessage(command, fileFragmentMessage));
//    }

    /**
     * Метод выводит в GUI список объектов(файлов и папок) в корневой пользовательской директории
     * в сетевом хранилище
     * @param commandMessage - объект сообщения(команды)
     */
    private void updateStorageItemListInGUI(CommandMessage commandMessage) {
        //вынимаем объект сообщения о директории из объекта сообщения(команды)
        DirectoryMessage directoryMessage = (DirectoryMessage) commandMessage.getMessageObject();
        //выводим в GUI список файлов и папок в корневой пользовательской директории в сетевом хранилище
        guiController.updateStorageItemListInGUI(directoryMessage.getDirectoryItem(),
                directoryMessage.getItemsList());
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