package utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import messages.AuthMessage;
import messages.DirectoryMessage;
import messages.FileFragmentMessage;
import messages.FileMessage;
import tcp.NettyServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * The server class for recognizing command messages and control command handlers.
 */
public class CommandMessageManager1 extends ChannelInboundHandlerAdapter {
    //принимаем объект контроллера сетевого хранилища
    private final CloudStorageServer storageServer;
    //принимаем объект сервера
    private NettyServer server;//TODO не нужен?

//    //принимаем переменную для корневой директории пользователя в сетевом хранилище
//    private String userStorageRoot;//TODO turn String into Path
    //принимаем переменную для корневой директории пользователя в сетевом хранилище
    private Path userStorageRoot;//TODO turn String into Path

    //объявляем объект файлового обработчика
    private FileUtils fileUtils;
    //объявляем переменную типа команды
    private int command;

//    //принимаем объект контроллера авторизации клиента
//    private ClientController clientController;
//    //принимаем объект клиентского соединения(хэндлера соединения с клиентом)
//    private TCPConnection1 tcpConnection;
//    //объявляем объект сервисного хендлера
//    private ServiceCommandHandler1 serviceCommandHandler;
//    //объявляем переменную для корневой директории пользователя в сетевом хранилище
//    private String userStorageRoot;
//    //принимаем идентификатор пользователя
//    private String clientID;

    public CommandMessageManager1(CloudStorageServer storageServer, NettyServer server) {
        this.storageServer = storageServer;
        this.server = server;

//        //принимаем переменную для корневой директории пользователя в сетевом хранилище
//        userStorageRoot = server.getUserController().getUserStorageRoot().toString();

        //инициируем объект файлового обработчика
        fileUtils = new FileUtils();

//        //принимаем объект контроллера пользователя
//        clientController = server.getClientController();
//        //инициируем переменную для корневой директории пользователя в сетевом хранилище
//        userStorageRoot = server.getStorageRoot();//FIXME не будет ли юзер видеть все хранилище?

    }

//    /**
//     * Метод распознает тип команды и обрабатывает ее.
//     * @param ctx - объект соединения netty, установленного с клиентом
//     * @param commandMessage - объект сообщения(команды)
//     */
//    public void recognizeAndArrangeMessageObject(ChannelHandlerContext ctx, CommandMessage commandMessage) throws IOException {
//        //принимаем объект клиентского соединения(хэндлера соединения с клиентом)
//        tcpConnection = server.findTCPConnection(ctx);
//        //инициируем идентификатор пользователя
//        clientID = tcpConnection.getClientID();
//
//        //TODO temporarily
//        System.out.println("[server]CommandMessageManager1.recognizeAndArrangeMessageObject() - " +
//                "clientID: " + clientID +
//                ", NettyServer.Enum.UNKNOWN_USER.name(): " + NettyServer.Enum.UNKNOWN_USER.name());
//
//        //***блок обработки объектов сервисных сообщений(команд), полученных от клиента***
//        //если подсоединившийся клиент еще не распознан
//        //FIXME где идентифицировать клиента?
//        if(clientID.equals(NettyServer.Enum.UNKNOWN_USER.name())){
//            //если полученное от клиента сообщение это запрос на авторизацию в облачное хранилище
//            if(commandMessage.getCommand() == Commands.REQUEST_SERVER_AUTH){
//                //вызываем метод обработки запроса от клиента
//                onAuthClientRequest(ctx, tcpConnection, commandMessage);
//            }
//        } else {
//            //если сюда прошли, значит клиент авторизован
//            //***блок обработки объектов НЕсервисных сообщений(команд), полученных от клиента***
//            //выполняем операции в зависимости от типа полученного не сервисного сообщения(команды)
//            switch (commandMessage.getCommand()) {
//                //обрабатываем полученный от клиента запрос на загрузку(сохранение) файла в облачное хранилище
//                case Commands.REQUEST_SERVER_FILE_UPLOAD:
//                    //вызываем метод обработки запроса от клиента на загрузку целого файла клиента
//                    // в директорию в сетевом хранилище.
//                    onUploadFileClientRequest(ctx, commandMessage);
//                    break;
//                //обрабатываем полученное от клиента подтверждение успешного получения обновленного
//                // списка файлов клиента в облачном хранилище
//                case Commands.CLIENT_RESPONSE_FILE_UPLOAD_OK:
//                    //вызываем метод обработки ответа клиента
//                    onUploadFileOkClientResponse(ctx, commandMessage);
//                    break;
//                //обрабатываем полученное от клиента сообщение об ошибке получения обновленного
//                // списка файлов клиента в облачном хранилище
//                case Commands.CLIENT_RESPONSE_FILE_UPLOAD_ERROR:
//                    //вызываем метод обработки ответа клиента
//                    onUploadFileErrorClientResponse(ctx, commandMessage);
//                    break;
//                //обрабатываем полученный от клиента запрос на скачивание целого файла из облачного хранилища
//                case Commands.REQUEST_SERVER_FILE_DOWNLOAD:
//                    //вызываем метод обработки запроса от клиента на скачивание целого файла клиента
//                    // из директории в сетевом хранилище
//                    onDownloadFileClientRequest(ctx, commandMessage);
//                    break;
//                //обрабатываем полученное от клиента подтверждение успешного сохранения целого файла,
//                // скачанного из облачного хранилища
//                case Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_OK:
//                    //вызываем метод обработки ответа клиента
//                    onDownloadFileOkClientResponse(ctx, commandMessage);
//                    break;
//                //обрабатываем полученное от клиента сообщение об ошибке сохранения целого файла,
//                // скачанного из облачного хранилища
//                case Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_ERROR:
//                    //вызываем метод обработки ответа клиента
//                    onDownloadFileErrorClientResponse(ctx, commandMessage);
//                    break;
//                //обрабатываем полученный от клиента запрос на загрузку(сохранение) фрагмента файла в облачное хранилище
//                case Commands.REQUEST_SERVER_FILE_FRAG_UPLOAD:
//                    //вызываем метод обработки запроса от клиента на загрузку файла-фрагмента
//                    //в директорию в сетевом хранилище.
//                    onUploadFileFragClientRequest(ctx, commandMessage);
//                    break;
//            }
//        }
//
//    }

    /**
     * Метод в полученном объекте сообщения распознает тип команды и обрабатывает ее.
     * @param ctx - объект соединения netty, установленного с клиентом
     * @param msg - входящий объект сообщения
     * @throws Exception - исключение
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);

        //инициируем из объекта сообщения объект команды
        CommandMessage commandMessage = (CommandMessage) msg;

//        //инициируем идентификатор пользователя
//        clientID = clientController.getClientID();

//        //TODO temporarily
//        System.out.println("[server]CommandMessageManager1.recognizeAndArrangeMessageObject() - " +
//                "clientID: " + clientID +
//                ", NettyServer.Enum.UNKNOWN_USER.name(): " + NettyServer.Enum.UNKNOWN_USER.name());

        //***блок обработки объектов сервисных сообщений(команд), полученных от клиента***
//        //если подсоединившийся клиент еще не распознан
//        //FIXME где идентифицировать клиента?
//        if(clientID.equals(NettyServer.Enum.UNKNOWN_USER.name())){
//            //если полученное от клиента сообщение это запрос на авторизацию в облачное хранилище
//            if(commandMessage.getCommand() == Commands.REQUEST_SERVER_AUTH){
//                //вызываем метод обработки запроса от клиента
//                onAuthClientRequest(ctx, tcpConnection, commandMessage);
//            }
//        } else {
        //если сюда прошли, значит клиент авторизован
        //***блок обработки объектов НЕсервисных сообщений(команд), полученных от клиента***
        //выполняем операции в зависимости от типа полученного не сервисного сообщения(команды)
        switch (commandMessage.getCommand()) {
            //обрабатываем полученный от клиента запрос на загрузку(сохранение) файла в облачное хранилище
            case Commands.REQUEST_SERVER_FILE_UPLOAD:
                //вызываем метод обработки запроса от клиента на загрузку целого файла клиента
                // в директорию в сетевом хранилище.
                onUploadFileClientRequest(ctx, commandMessage);
                break;
            //обрабатываем полученное от клиента подтверждение успешного получения обновленного
            // списка файлов клиента в облачном хранилище
            case Commands.CLIENT_RESPONSE_FILE_UPLOAD_OK:
                //вызываем метод обработки ответа клиента
                onUploadFileOkClientResponse(ctx, commandMessage);
                break;
            //обрабатываем полученное от клиента сообщение об ошибке получения обновленного
            // списка файлов клиента в облачном хранилище
            case Commands.CLIENT_RESPONSE_FILE_UPLOAD_ERROR:
                //вызываем метод обработки ответа клиента
                onUploadFileErrorClientResponse(ctx, commandMessage);
                break;
            //обрабатываем полученный от клиента запрос на скачивание целого файла из облачного хранилища
            case Commands.REQUEST_SERVER_FILE_DOWNLOAD:
                //вызываем метод обработки запроса от клиента на скачивание целого файла клиента
                // из директории в сетевом хранилище
                onDownloadFileClientRequest(ctx, commandMessage);
                break;
            //обрабатываем полученное от клиента подтверждение успешного сохранения целого файла,
            // скачанного из облачного хранилища
            case Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_OK:
                //вызываем метод обработки ответа клиента
                onDownloadFileOkClientResponse(ctx, commandMessage);
                break;
            //обрабатываем полученное от клиента сообщение об ошибке сохранения целого файла,
            // скачанного из облачного хранилища
            case Commands.CLIENT_RESPONSE_FILE_DOWNLOAD_ERROR:
                //вызываем метод обработки ответа клиента
                onDownloadFileErrorClientResponse(ctx, commandMessage);
                break;
            //обрабатываем полученный от клиента запрос на загрузку(сохранение) фрагмента файла в облачное хранилище
            case Commands.REQUEST_SERVER_FILE_FRAG_UPLOAD:
                //вызываем метод обработки запроса от клиента на загрузку файла-фрагмента
                //в директорию в сетевом хранилище.
                onUploadFileFragClientRequest(ctx, commandMessage);
                break;
            //обрабатываем полученный от AuthGateway проброшенный запрос на авторизацию клиента в облачное хранилище
            //возвращаем список объектов в корневой директорию пользователя в сетевом хранилище.
            case Commands.SERVER_RESPONSE_AUTH_OK:
                //вызываем метод обработки запроса от AuthGateway
                onAuthClientRequest(ctx, commandMessage);
                break;
        }
    }
    //    /**
//     * Метод обрабатывает полученный от клиента запрос на авторизацию в облачное хранилище
//     * @param ctx - //TODO
//     * @param tcpConnection - объект соединения, установленного с клиентом
//     * @param commandMessage - объект сообщения(команды)
//     */
//    private void onAuthClientRequest(ChannelHandlerContext ctx, TCPConnection1 tcpConnection, CommandMessage commandMessage) {
//        //вынимаем объект авторизационного сообщения из объекта сообщения(команды)
//        AuthMessage authMessage = (AuthMessage) commandMessage.getMessageObject();
//
//        //TODO temporarily
//        System.out.println("[server]CommandMessageManager1.onAuthClientRequest() - " +
//                "login: " + authMessage.getLogin() + ", password: " + authMessage.getPassword());
//
//        //если авторизации клиента в облачном хранилище прошла удачно
//        if(serviceCommandHandler.authorizeUser(server, tcpConnection, authMessage)){
//            //меняем команду на успешную
//            command = Commands.SERVER_RESPONSE_AUTH_OK;
//
//            //TODO проверить как будут влиять несколько клиентов друг на друга
//            //добавляем логин пользователя(имя его папки в сетевом хранилище)
//            // к корневой директории клиента по умолчанию
//            userStorageRoot = userStorageRoot.concat("/").concat(authMessage.getLogin());
//            //инициируем объект сообщения о директории
//            DirectoryMessage directoryMessage = new DirectoryMessage();
//            //формируем список файлов и папок в корневой директории клиента по умолчанию
//            directoryMessage.composeFilesAndFoldersNamesList(userStorageRoot);
//            //инициируем новый объект сообщения(команды)
//            commandMessage = new CommandMessage(command, directoryMessage);
//        //если авторизации клиента в облачном хранилище не прошла
//        } else {
//            //инициируем переменную типа команды - ответ об ошибке
//            //в этом случае, в объекте сообщения(команды) вернем принятый от клиента объект авторизационного сообщения
//            command = Commands.SERVER_RESPONSE_AUTH_ERROR;
//        }
//
//        //отправляем объект сообщения(команды) клиенту
////        server.sendToClient(tcpConnection, new CommandMessage(command, directoryMessage));//TODO
//        ctx.writeAndFlush(commandMessage);
//
//        //TODO temporarily
//        server.printMsg("CommandMessageManager1.onAuthClientRequest() - ctx: " + ctx +
//                ", command: " + commandMessage.getCommand());
//    }

    /**
     * Метод обработки запроса от клиента на загрузку целого файла клиента в директорию в
     * сетевом хранилище.
//     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadFileClientRequest(ChannelHandlerContext ctx, CommandMessage commandMessage) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileMessage fileMessage = (FileMessage) commandMessage.getMessageObject();

        //вынимаем заданную директорию сетевого хранилища из объекта сообщения(команды)
        String storageDir = fileMessage.getToDir();
        //собираем целевую директорию пользователя в сетевом хранилище//TODO turn String into Path
        String toDir = userStorageRoot.toString();//сбрасываем до корневой папки пользователя в сетевом хранилище
        toDir = toDir.concat("/").concat(storageDir);//добавляем значение подпапки

        //если сохранение прошло удачно
        if(fileUtils.saveFile(toDir, fileMessage)){
            //отправляем сообщение на сервер: подтверждение, что все прошло успешно
            command = Commands.SERVER_RESPONSE_FILE_UPLOAD_OK;
        //если что-то пошло не так
        } else {
            //выводим сообщение
            server.printMsg("(Server)" + fileUtils.getMsg());
            //инициируем переменную типа команды(по умолчанию - ответ об ошибке)
            command = Commands.SERVER_RESPONSE_FILE_UPLOAD_ERROR;
        }
        //инициируем объект сообщения о директории
        DirectoryMessage directoryMessage = new DirectoryMessage();
        //формируем список файлов и папок в заданной директории клиента в сетевом хранилище
        directoryMessage.composeFilesAndFoldersNamesList(toDir);

        //отправляем объект сообщения(команды) клиенту, используя буферизированную запись в канал
//        server.sendToClient(ctx, new CommandMessage(command, directoryMessage));//TODO
        ctx.writeAndFlush(new CommandMessage(command, directoryMessage));
    }

    /**
     * Метод обрабатывает полученное от клиента подтверждение успешного получения
     * обновленного списка файлов клиента в облачном хранилище
//     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadFileOkClientResponse(ChannelHandlerContext ctx, CommandMessage commandMessage) {
        //FIXME fill me!
        printMsg("[server]CommandMessageManager1.onUploadFileOkClientResponse() command: " + commandMessage.getCommand());
    }

    /**
     * Метод обрабатывает полученное от клиента сообщение об ошибке получения обновленного
     * списка файлов клиента в облачном хранилище
//     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadFileErrorClientResponse(ChannelHandlerContext ctx, CommandMessage commandMessage) {
        //FIXME fill me!
        printMsg("[server]CommandMessageManager1.onUploadFileErrorClientResponse() command: " + commandMessage.getCommand());
    }

    /**
     * Метод обработки запроса от клиента на скачивание целого файла клиента из директории в
     * сетевом хранилище.
//     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadFileClientRequest(ChannelHandlerContext ctx, CommandMessage commandMessage) throws IOException {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileMessage fileMessage = (FileMessage) commandMessage.getMessageObject();
        //вынимаем заданную директорию сетевого хранилища из объекта сообщения(команды)
        String storageDir = fileMessage.getFromDir();
        //вынимаем заданную клиентскую директорию из объекта сообщения(команды)
        String clientDir = fileMessage.getToDir();

        //FIXME переделать после ответа препадователя
        //собираем целевую директорию пользователя в сетевом хранилище
//        String fromDir = userStorageRoot;//сбрасываем до корневой папки пользователя в сетевом хранилище
//        fromDir = fromDir.concat("/").concat(storageDir);//добавляем значение подпапки
//        String fromDir = userStorageRoot + "/" + storageDir;//TODO проба
        Path fromDirPath = userStorageRoot;
        fromDirPath = fromDirPath.resolve(storageDir);
        String fromDir = fromDirPath.toString();

        //вычисляем размер файла
        long fileSize = Files.size(Paths.get(fromDir, fileMessage.getFilename()));
//        long fileSize = Files.size(toFilePath);

        //если размер запрашиваемого файла больше константы размера фрагмента
        if(fileSize > FileFragmentMessage.CONST_FRAG_SIZE){
            //запускаем метод отправки файла по частям
            downloadFileByFrags(ctx, fromDir, clientDir,
                    fileMessage.getFilename(), fileSize);
            //если файл меньше
        } else {
            //запускаем метод отправки целого файла
            downloadEntireFile(ctx, fromDir, clientDir, fileMessage.getFilename());
        }
    }

    /**
     * Метод обрабатывает полученное от клиента подтверждение успешного сохранения целого файла,
     * скачанного из облачного хранилища
//     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadFileOkClientResponse(ChannelHandlerContext ctx, CommandMessage commandMessage) {
        //FIXME fill me!
        printMsg("[server]CommandMessageManager1.onDownloadFileOkClientResponse() command: " + commandMessage.getCommand());
    }

    /**
     * Метод обрабатывает полученное от клиента сообщение об ошибке сохранения целого файла,
     * скачанного из облачного хранилища
//     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onDownloadFileErrorClientResponse(ChannelHandlerContext ctx, CommandMessage commandMessage) {
        //FIXME fill me!
        printMsg("[server]CommandMessageManager1.onDownloadFileErrorClientResponse() command: " + commandMessage.getCommand());
    }

    /**
     * Метод обработки запроса от клиента на загрузку файла-фрагмента
     * в директорию в сетевом хранилище.
//     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onUploadFileFragClientRequest(ChannelHandlerContext ctx, CommandMessage commandMessage) {
        //вынимаем объект файлового сообщения из объекта сообщения(команды)
        FileFragmentMessage fileFragmentMessage = (FileFragmentMessage) commandMessage.getMessageObject();
        //собираем целевую директорию пользователя в сетевом хранилище
        //сбрасываем до корневой папки пользователя в сетевом хранилище
        String toTempDir = userStorageRoot.toString();//TODO turn String into Path
        // добавляем временную директорию сетевого хранилища из объекта сообщения(команды)
        toTempDir = toTempDir.concat("/").concat(fileFragmentMessage.getToTempDir());
        //создаем объект пути к папке с загруженным файлом
        String toDir = Paths.get(toTempDir).getParent().toString();//FIXME переделать на Path?
        //инициируем директорию для показа списка загруженных фрагментов или файла
        String directory = toTempDir;
        //если сохранение полученного фрагмента файла во временную папку сетевого хранилища прошло удачно
        if(fileUtils.saveFileFragment(toTempDir, fileFragmentMessage)){
            //отправляем сообщение на сервер: подтверждение, что все прошло успешно
            command = Commands.SERVER_RESPONSE_FILE_FRAG_UPLOAD_OK;
        //если что-то пошло не так
        } else {
            //выводим сообщение
            printMsg("[Server]" + fileUtils.getMsg());
            //инициируем переменную типа команды - ответ об ошибке
            command = Commands.SERVER_RESPONSE_FILE_FRAG_UPLOAD_ERROR;
        }
        //если это последний фрагмент
        if(fileFragmentMessage.isFinalFileFragment()){
            //если корректно собран файл из фрагментов сохраненных во временную папку
            if(fileUtils.compileFileFragments(toTempDir, toDir, fileFragmentMessage)){
                //ответ сервера, что сборка файла из загруженных фрагментов прошла успешно
                command = Commands.SERVER_RESPONSE_FILE_FRAGS_UPLOAD_OK;
                //устанавливаем финальное значение папки для показа загруженного файла
                directory = toDir;
            //если что-то пошло не так
            } else {
                //выводим сообщение
                printMsg("[Server]" + fileUtils.getMsg());
                //инициируем переменную типа команды - ответ об ошибке
                command = Commands.SERVER_RESPONSE_FILE_FRAGS_UPLOAD_ERROR;
            }
        }
        //инициируем объект сообщения о директории
        DirectoryMessage directoryMessage = new DirectoryMessage();
        //формируем список файлов и папок в корневой директории клиента по умолчанию
        directoryMessage.composeFilesAndFoldersNamesList(directory);

        //отправляем объект сообщения(команды) клиенту
//        server.sendToClient(ctx, new CommandMessage(command, directoryMessage));//TODO
        ctx.writeAndFlush(new CommandMessage(command, directoryMessage));
    }

    /**
     * Метод скачивания и отправки по частям большого файла размером более
     * константы максмального размера фрагмента файла
//     * @param tcpConnection- объект соединения, установленного с клиентом
     * @param fromDir - директория(относительно корня) клиента где хранится файл источник
     * @param toDir - директория(относительно корня) в сетевом хранилище
     * @param filename - строковое имя файла
     * @param fullFileSize - размер целого файла в байтах
     * @throws IOException - исключение
     */
    private void downloadFileByFrags(ChannelHandlerContext ctx, String fromDir, String toDir,
                                     String filename, long fullFileSize) throws IOException {
        //TODO temporarily
        long start = System.currentTimeMillis();

        //***разбиваем файл на фрагменты***
        //рассчитываем количество полных фрагментов файла
        int totalEntireFragsNumber = (int) fullFileSize / FileFragmentMessage.CONST_FRAG_SIZE;
        //рассчитываем размер последнего фрагмента файла
        int finalFileFragmentSize = (int) fullFileSize - FileFragmentMessage.CONST_FRAG_SIZE * totalEntireFragsNumber;
        //рассчитываем общее количество фрагментов файла
        //если есть последний фрагмент, добавляем 1 к количеству полных фрагментов файла
        int totalFragsNumber = (finalFileFragmentSize == 0) ?
                totalEntireFragsNumber : totalEntireFragsNumber + 1;

        //TODO temporarily
        printMsg("[server]CommandMessageManager1.downloadFileByFrags() - fullFileSize: " + fullFileSize);
        printMsg("[server]CommandMessageManager1.downloadFileByFrags() - totalFragsNumber: " + totalFragsNumber);
        printMsg("[server]CommandMessageManager1.downloadFileByFrags() - totalEntireFragsNumber: " + totalEntireFragsNumber);

        //устанавливаем начальные значения номера текущего фрагмента и стартового байта
        long startByte = 0;
        //инициируем байтовый массив для чтения данных для полных фрагментов
        byte[] data = new byte[FileFragmentMessage.CONST_FRAG_SIZE];
        //инициируем массив имен фрагментов файла
        String[] fragsNames = new String[totalFragsNumber];
        //***в цикле создаем целые фрагменты, читаем в них данные и отправляем***
        for (int i = 1; i <= totalEntireFragsNumber; i++) {
            //инициируем объект фрагмента файлового сообщения
            FileFragmentMessage fileFragmentMessage =
                    new FileFragmentMessage(fromDir, toDir, filename, fullFileSize,
                            i, totalFragsNumber, FileFragmentMessage.CONST_FRAG_SIZE, fragsNames, data);
            //читаем данные во фрагмент с определенного места файла
            fileFragmentMessage.readFileDataToFragment(fromDir, filename, startByte);
            //увеличиваем указатель стартового байта на размер фрагмента
            startByte += FileFragmentMessage.CONST_FRAG_SIZE;

            //отправляем объект сообщения(команды) клиенту
//            server.sendToClient(ctx, new CommandMessage(Commands.SERVER_RESPONSE_FILE_FRAGS_DOWNLOAD_OK,
//                    fileFragmentMessage));//TODO
            ctx.writeAndFlush(new CommandMessage(Commands.SERVER_RESPONSE_FILE_FRAGS_DOWNLOAD_OK,
                    fileFragmentMessage));
        }

        //TODO temporarily
        printMsg("[server]CommandMessageManager1.downloadFileByFrags() - currentFragNumber: " + totalFragsNumber);
        printMsg("[server]CommandMessageManager1.downloadFileByFrags() - finalFileFragmentSize: " + finalFileFragmentSize);

        //***отправляем последний фрагмент, если он есть***
        if(totalFragsNumber > totalEntireFragsNumber){
            //инициируем байтовый массив для чтения данных для последнего фрагмента
            byte[] dataFinal = new byte[finalFileFragmentSize];
            //инициируем объект фрагмента файлового сообщения
            FileFragmentMessage fileFragmentMessage =
                    new FileFragmentMessage(fromDir, toDir, filename, fullFileSize,
                            totalFragsNumber, totalFragsNumber, finalFileFragmentSize, fragsNames, dataFinal);
            //читаем данные во фрагмент с определенного места файла
            fileFragmentMessage.readFileDataToFragment(fromDir, filename, startByte);

            //отправляем объект сообщения(команды) клиенту
//            server.sendToClient(ctx, new CommandMessage(Commands.SERVER_RESPONSE_FILE_FRAGS_DOWNLOAD_OK,
//                    fileFragmentMessage));//TODO
            ctx.writeAndFlush(new CommandMessage(Commands.SERVER_RESPONSE_FILE_FRAGS_DOWNLOAD_OK,
                    fileFragmentMessage));
        }

        //TODO temporarily
        long finish = System.currentTimeMillis() - start;
        printMsg("[server]CommandMessageManager1.downloadFileByFrags() - duration(mc): " + finish);
    }

    /**
     * Метод скачивания и отправки целого небольшого файла размером менее
     * константы максмального размера фрагмента файла
//     * @param tcpConnection - объект соединения, установленного с клиентом
     * @param fromDir - директория(относительно корня) клиента где хранится файл источник
     * @param clientDir - директория(относительно корня) в сетевом хранилище
     * @param filename - строковое имя файла
     */
    private void downloadEntireFile(ChannelHandlerContext ctx, String fromDir, String clientDir, String filename){
        //создаем объект файлового сообщения
        FileMessage fileMessage = new FileMessage(fromDir, clientDir, filename);
        //если скачивание прошло удачно
        if(fileUtils.readFile(fromDir, fileMessage)){
            //инициируем переменную типа команды - ответ cо скачанным файлом
            command = Commands.SERVER_RESPONSE_FILE_DOWNLOAD_OK;
        //если что-то пошло не так
        } else {
            //выводим сообщение
            printMsg("[Server]" + fileUtils.getMsg());
            //инициируем переменную типа команды - ответ об ошибке скачивания
            command = Commands.SERVER_RESPONSE_FILE_DOWNLOAD_ERROR;
        }

        //отправляем объект сообщения(команды) клиенту
//        server.sendToClient(ctx, new CommandMessage(command, fileMessage));//TODO
        ctx.writeAndFlush(new CommandMessage(command, fileMessage));
    }

    /**
     * Метод обрабатывает полученный от AuthGateway проброшенный запрос на авторизацию клиента в облачное хранилище
     * Возвращает список объектов в корневой директорию пользователя в сетевом хранилище.
     * @param ctx - объект соединения netty, установленного с клиентом
     * @param commandMessage - объект сообщения(команды)
     */
    private void onAuthClientRequest(ChannelHandlerContext ctx, CommandMessage commandMessage) {
        //вынимаем тип команды из объекта сообщения(команды)
        command = commandMessage.getCommand();
        //вынимаем объет пути к его корневой директории клиента в сетевом хранилище
        userStorageRoot = commandMessage.getPath();
        //инициируем объект сообщения о директории
        DirectoryMessage directoryMessage = new DirectoryMessage();
        //формируем список файлов и папок в корневой директории клиента по умолчанию//TODO turn String into Path
        directoryMessage.composeFilesAndFoldersNamesList(userStorageRoot.toString());
        //инициируем новый объект сообщения(команды)
        commandMessage = new CommandMessage(command, directoryMessage);

        //TODO temporarily
        DirectoryMessage directoryMessage1 = (DirectoryMessage)commandMessage.getMessageObject();
        printMsg("[server]CommandMessageManager1.onAuthClientRequest() - " +
                "command: " + commandMessage.getCommand() +
                ", password: " + Arrays.toString(directoryMessage1.getNamesList()));

        //отправляем объект сообщения(команды) клиенту
        ctx.writeAndFlush(commandMessage);
    }

    public void printMsg(String msg){
        storageServer.printMsg(msg);
    }
}