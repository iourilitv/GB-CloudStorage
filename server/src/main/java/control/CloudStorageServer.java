package control;

import io.netty.channel.ChannelHandlerContext;
import jdbc.UsersAuthController;
import messages.AuthMessage;
import messages.DirectoryMessage;
import messages.FileFragmentMessage;
import messages.FileMessage;
import netty.NettyServer;
import utils.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This server's class is for operations with a cloud storage.
 */
public class CloudStorageServer {
    //инициируем переменную для печати сообщений в консоль
    private final PrintStream log = System.out;
    //объявляем переменную порта сервера
    private int PORT;
    //объявляем объект пути к корневой директории облачного хранилища(сервера) для хранения файлов клиентов
    private static Path STORAGE_ROOT_PATH;
    //инициируем константу строки названия корневой директории для списка в серверной части GUI
    private final String STORAGE_DEFAULT_DIR = "";
    //объявляем объекты директории пользователя по умолчанию в серверной части GUI
    private Item storageDefaultDirItem;
    //принимаем объект контроллера авторизации пользователей
    private final UsersAuthController usersAuthController = UsersAuthController.getOwnInstance();
    //объявляем объект файлового обработчика
    private final FileUtils fileUtils = FileUtils.getInstance();
    //принимаем объект обработчика операций с объектами элементов списков в GUI
    private final ItemUtils itemUtils = ItemUtils.getInstance();
    //принимаем объект хендлера настроек приложения
    private final PropertiesHandler propertiesHandler = PropertiesHandler.getInstance();

    /**
     * Метод инициирует процесс настройки приложения.
     */
    public void initConfiguration() {
        //запускаем процесс применения конфигурации приложения
        propertiesHandler.setConfiguration();
        //инициируем переменную порта соединения
        int port = propertiesHandler.getCurrentProperties().getPort_custom();
        //если пользователем задано другое значение порта
        if(port > 0){
            //применяем значение пользователя
            PORT = port;
        } else {
            //в противном случае применяем дефорлтное значение порта
            PORT = propertiesHandler.getCurrentProperties().getPORT_DEFAULT();
        }
        //выводим в лог примененное значение порта
        printMsg("[server]CloudStorageServer.initConfiguration() - PORT: " + PORT);

        //инициируем строку установленного пользователем пути к корневой директории
        String root_absolute = propertiesHandler.getCurrentProperties().getRoot_absolute();
        //если поле свойства не пустое и путь реально существует(например, usb-флешка вставлена)
        if(!root_absolute.isEmpty() && Files.exists(Paths.get(root_absolute))){
            //применяем значение пользователя
            STORAGE_ROOT_PATH = Paths.get(root_absolute);
        } else {
            //в противном случае применяем дефорлтное значение пути к корню
            STORAGE_ROOT_PATH = Paths.get(propertiesHandler.getCurrentProperties().getROOT_DEFAULT());

            try {
                //создаем новую клиентскую директорию, если еще не создана
                Files.createDirectory(STORAGE_ROOT_PATH);
            } catch (IOException e) {
                printMsg("[server]CloudStorageServer.initConfiguration() - " +
                        "Something wrong with new storage root directory creating. Probably it does already exist!");
            }
        }
        //выводим в лог примененное значение пути к корню
        printMsg("[server]CloudStorageServer.initConfiguration() - STORAGE_ROOT_PATH: " + STORAGE_ROOT_PATH);
    }

    /**
     * Метод запускает приложение сервера.
     */
    public void run() throws Exception {
        //инициируем настройки контроллера авторизации пользователей
        usersAuthController.init(this);
        //инициируем объект директории по умолчанию в серверной части GUI
        storageDefaultDirItem = new Item(STORAGE_DEFAULT_DIR);
        //инициируем объект сетевого подключения
        new NettyServer(this, PORT).run();
    }

    /**
     * Метод-прокладка запускае процесс изменения пароля пользователя в БД.
     * @param authMessage - объект фвторизационного сообщения
     * @param ctx - объект соединения с клиентом
     * @return - результат изменения пароля пользователя в БД
     */
    public boolean changeUserPassword(AuthMessage authMessage, ChannelHandlerContext ctx) {
        //инициируем объект контрольного соединения(для заданного логина)
        ChannelHandlerContext checkedCtx = usersAuthController.getAuthorizedUsers()
                .get(authMessage.getLogin());
        //если в списке зарегистрированных пользователей есть такой логин и
        // если объект его соединения совпадает с соединением текущего пользователя
        if(checkedCtx != null &&
                checkedCtx.channel().equals(ctx.channel())){

            //выполняем и возвращаем результат изменения пароля пользователя в БД
            return usersAuthController.changeUserPassword(authMessage.getLogin(),
                    authMessage.getPassword(), authMessage.getNewPassword());
        //в противном случае
        } else {
            //выводим сообщение в лог и возвращаем false
            printMsg("CloudStorageServer.changeUserPassword() - Wrong login! " +
                    ". login: " + authMessage.getLogin() +
                    ". ctx.channel(): " + ctx.channel());
            return false;
        }
    }

    /**
     * Метод-прокладка возвращает массив объектов элементов в заданной директории в сетевом хранилище.
     * @param storageDirItem - объект заданной директории в сетевом хранилище
     * @param userStorageRoot - объект реального пути к корневой директории пользователя в сетевом хранилище
     * @return - массив объектов элементов в заданной директории в сетевом хранилище
     */
    public Item[] storageItemsList(Item storageDirItem, Path userStorageRoot) {
        return itemUtils.getItemsList(storageDirItem, userStorageRoot);
    }

    /**
     * Метод инициирует объект директории пользователя в сетевом хранилище.
     * @param storageDirPathname - строка имени пути к директории
     * @param userStorageRoot - объект пути к корневой директории пользователя в сетевом хранилище
     * @return - объект директории пользователя в сетевом хранилище
     */
    public Item createStorageDirectoryItem(String storageDirPathname, Path userStorageRoot) {
        return itemUtils.createDirectoryItem(storageDirPathname, storageDefaultDirItem, userStorageRoot);
    }

    /**
     * Метод запускает процесс сохранения полученного от клиента объекта(файла)
     * в заданную директорию в сетевом хранилище.
     * @param fileMessage - объект фалового сообщения
     * @param userStorageRoot - объект пути к корневой директории пользователя в сетевом хранилище
     * @return - результат сохранения объекта
     */
    public boolean uploadItem(FileMessage fileMessage, Path userStorageRoot){
        //инициируем локальную переменную объекта директории назначения в сетевом хранилище
        Item storageToDirItem = fileMessage.getStorageDirectoryItem();
        //инициируем новый объект пути к объекту
        Path realNewToItemPath = Paths.get(
                itemUtils.getRealPath(storageToDirItem.getItemPathname(), userStorageRoot).toString(),
                fileMessage.getItem().getItemName());
        return fileUtils.saveFile(fileMessage, realNewToItemPath);
    }

    /**
     * Метод запускает процесс сохранения файла-фрагмента из полученного байтового массива.
     * @param fileFragMsg - объект файлового сообщения
     * @param userStorageRoot - объект пути к корневой директории пользователя в сетевом хранилище
     * @return результат процесс сохранения файла-фрагмента из полученного байтового массива
     */
    public boolean uploadItemFragment(FileFragmentMessage fileFragMsg, Path userStorageRoot) {
        //инициируем реальный путь к временной папке для файлов-фрагментов
        Path realToTempDirPath = itemUtils.getRealPath(
                Paths.get(
                        fileFragMsg.getToDirectoryItem().getItemPathname(),
                        fileFragMsg.getToTempDirName()).toString(),
                userStorageRoot);
        //инициируем реальный путь к файлу-фрагменту
        Path realToFragPath = Paths.get(
                        realToTempDirPath.toString(), fileFragMsg.getFragName());
        //если сохранение полученного фрагмента файла во временную папку сетевого хранилища прошло удачно
        return fileUtils.saveFileFragment(realToTempDirPath, realToFragPath, fileFragMsg);
    }

    /**
     * Метод запускает процесс сборки целого файла из файлов-фрагментов.
     * @param fileFragMsg - объект файлового сообщения
     * @param userStorageRoot - объект пути к корневой директории пользователя в сетевом хранилище
     * @return результат процесса сборки целого файла из файлов-фрагментов
     */
    public boolean compileItemFragments(FileFragmentMessage fileFragMsg, Path userStorageRoot) {
        //инициируем реальный путь к временной папке для файлов-фрагментов
        Path realToTempDirPath = itemUtils.getRealPath(
                Paths.get(
                        fileFragMsg.getToDirectoryItem().getItemPathname(),
                        fileFragMsg.getToTempDirName()).toString(),
                userStorageRoot);
        //инициируем реальный путь к файлу-фрагменту
        Path realToFilePath = itemUtils.getRealPath(
                Paths.get(
                        fileFragMsg.getToDirectoryItem().getItemPathname(),
                        fileFragMsg.getItem().getItemName()).toString(),
                        userStorageRoot);
        //возвращаем результат процесса сборки целого объекта(файла) из файлов-фрагментов
        return fileUtils.compileFileFragments(realToTempDirPath, realToFilePath, fileFragMsg);
    }

    /**
     * Метод запускает процесс скачивания и отправки клиенту объекта элемента(пока только файла).
     * @param fileMessage - объект фалового сообщения
     * @param userStorageRoot - объект пути к корневой директории пользователя в сетевом хранилище
     * @param ctx - объект сетевого соединения
     */
    public void downloadItem(FileMessage fileMessage, Path userStorageRoot,
                             ChannelHandlerContext ctx) throws IOException {
        //если объект элемента - это директория
        if(fileMessage.getItem().isDirectory()){
            //FIXME Upd 26. что-то делаем, а пока выходим
            printMsg("[server]CloudStorageServer.downloadItem() - Directory downloading is not allowed!");
            return;
        }
        //инициируем объект реального пути к объекту элемента в сетевом хранилище
        Path realStorageItemPath = itemUtils.getRealPath(fileMessage.getItem().getItemPathname(), userStorageRoot);
        //вычисляем размер файла
        long fileSize = Files.size(realStorageItemPath);
        //если размер запрашиваемого файла больше константы размера фрагмента
        if(fileSize > FileFragmentMessage.CONST_FRAG_SIZE){
            //запускаем метод отправки файла по частям
            downloadFileByFrags(fileMessage.getClientDirectoryItem(),
                    fileMessage.getItem(), fileSize, userStorageRoot, ctx);
            //если файл меньше
        } else {
            //запускаем метод отправки целого файла
            downloadEntireFile(fileMessage.getClientDirectoryItem(), fileMessage.getItem(),
                    fileMessage.getItem(), fileSize, userStorageRoot, ctx);
        }
    }

    /**
     * Метод-прокладка запускает процесс нарезки и отправки на сервер по частям большого файла
     * размером более константы максмального размера фрагмента файла
     * @param clientToDirItem - объект директории назначения в клиенте
     * @param storageItem - объект элемента в сетевом хранилище
     * @param fullFileSize - размер целого файла в байтах
     * @param userStorageRoot - объект пути к корневой директории пользователя в сетевом хранилище
     * @param ctx - сетевое соединение
     */
    private void downloadFileByFrags(Item clientToDirItem, Item storageItem,
                                     long fullFileSize, Path userStorageRoot,
                                     ChannelHandlerContext ctx) {
        fileUtils.cutAndSendFileByFrags(clientToDirItem, storageItem, fullFileSize,
                userStorageRoot, ctx, Commands.SERVER_RESPONSE_DOWNLOAD_FILE_FRAG_OK);
    }

    /**
     * Метод-прокладка запускает процесс отправки клиенту отдельного фрагмента файла
     * из сетевого хранилища.
     * @param fileFragMsg - объект сообщения фрагмента файла из объекта сообщения(команды)
     * @param command - переменная типа команды
     * @param userStorageRoot - объект пути к корневой директории пользователя в сетевом хранилище
     * @param ctx - объект сетевого соединения
     */
    public void sendFileFragment(FileFragmentMessage fileFragMsg, Commands command,
                                 Path userStorageRoot, ChannelHandlerContext ctx) {
        //инициируем новый байтовый массив
        byte[] data = new byte[fileFragMsg.getFileFragmentSize()];
        //вычисляем индекс стартового байта фрагмента в целом файле
        long startByte = FileFragmentMessage.CONST_FRAG_SIZE * fileFragMsg.getCurrentFragNumber();
        //вызываем метод отправки объекта сообщения с новым байтовым массивом данных фрагмента
        fileUtils.sendFileFragment(fileFragMsg.getToDirectoryItem(), fileFragMsg.getItem(),
                fileFragMsg.getFullFileSize(), fileFragMsg.getCurrentFragNumber(),
                fileFragMsg.getTotalFragsNumber(), fileFragMsg.getFileFragmentSize(),
                data, startByte, userStorageRoot, ctx, command);
    }

    /**
     * Метод скачивания и отправки целого небольшого файла размером менее
     * @param clientToDirItem - объект директории назначения клиента
     * @param storageItem - объект директории источника в сетевом хранилище, где хранится файл источник
     * @param item - объект элемента в сетевом хранилище
     * @param fileSize - размер объекта(файла)
     * @param userStorageRoot - объект пути к корневой директории пользователя в сетевом хранилище
     * @param ctx - объект сетевого соединения
     */
    private void downloadEntireFile(Item clientToDirItem, Item storageItem, Item item,
                       long fileSize, Path userStorageRoot, ChannelHandlerContext ctx){
        //создаем объект файлового сообщения
        FileMessage fileMessage = new FileMessage(storageItem, clientToDirItem, item, fileSize);
        Commands command;
        //если скачивание прошло удачно
        if(fileUtils.readFile(itemUtils.getRealPath(storageItem.getItemPathname(), userStorageRoot),
                fileMessage)){
            //инициируем переменную типа команды - ответ cо скачанным файлом
            command = Commands.SERVER_RESPONSE_DOWNLOAD_ITEM_OK;
            //если что-то пошло не так
        } else {
            //выводим сообщение
            printMsg("[server]" + fileUtils.getMsg());
            //инициируем переменную типа команды - ответ об ошибке скачивания
            command = Commands.SERVER_RESPONSE_DOWNLOAD_ITEM_ERROR;
        }
        //отправляем объект сообщения(команды) клиенту
        ctx.writeAndFlush(new CommandMessage(command, fileMessage));
    }

    /**
     * Метод переименовывает объект элемента списка в серверном хранилище.
     * @param origin - текущий объект элемента списка в серверном хранилище
     * @param newName - новое имя элемента
     * @param userStorageRoot - объект пути к корневой директории пользователя в сетевом хранилище
     * @return - результат переименования
     */
    public boolean renameStorageItem(Item origin, String newName, Path userStorageRoot) {
        //инициируем объект пути к исходному файловому объекту
        Path originPath = itemUtils.getRealPath(origin.getItemPathname(), userStorageRoot);
        //инициируем файловый объект для объекта списка в клиенте
        File originFileObject = new File(originPath.toString());
        //инициируем объект пути к новому файловому объекту
        Path newPath = Paths.get(originFileObject.getParent(), newName);
        //инициируем файловый объект для нового файлового объекта
        File newFileObject = new File(newPath.toString());
        //возвращаем результат переименования файлового объекта
        return originFileObject.renameTo(newFileObject);
    }

    /**
     * Метод создает новую папку в текущей директории в сетевом хранилище.
     * @param directoryMessage - сообщение о директории
     * @param userStorageRoot - объект пути к корневой директории пользователя в сетевом хранилище
     * @return - результат создания новой папки в текущей директории в сетевом хранилище
     */
    public boolean createNewFolder(DirectoryMessage directoryMessage, Path userStorageRoot) {
        //инициируем объект пути к родительской директории
        Path realParentDirPath = itemUtils.getRealPath(
                directoryMessage.getDirectoryPathname(), userStorageRoot);
        //инициируем объект пути к новой папке
        String realNewDirPathname = Paths.get(
                realParentDirPath.toString(), directoryMessage.getNewDirName()).toString();
        //возвращаем результат создания новой папки
        return fileUtils.createNewFolder(realNewDirPathname);
    }

    /**
     * Метод создает новую корневую директорию для нового пользователя.
     * @param login - логин нового пользователя
     * @return - результат создания новой корневой директории для нового пользователя
     */
    public boolean createNewUserRootFolder(String login) {
        //инициируем объект пути к новой корневой директории нового пользователя
        String realDirPathname = Paths.get(STORAGE_ROOT_PATH.toString(), login).toString();
        //возвращаем результат создания новой папки
        return fileUtils.createNewFolder(realDirPathname);
    }

    /**
     * Метод проверяет есть ли уже корневая директория для заданного логина.
     * @param login - логин нового пользователя
     * @return - результат проверки есть ли уже корневая директория для заданного логина
     */
    public boolean isUserRootDirExist(String login) {
        return Files.exists(STORAGE_ROOT_PATH.resolve(login));
    }

    /**
     * Метод удаляет объект элемента списка(файл или папку) в текущей директории в серверном хранилище.
     * @param item - объект списка в серверном хранилище
     * @param userStorageRoot - объект пути к корневой директории пользователя в сетевом хранилище
     * @return - результат удаления
     */
    public boolean deleteClientItem(Item item, Path userStorageRoot) {
        //инициируем файловый объект для объекта списка в клиенте
        File fileObject = new File(itemUtils.getRealPath(item.getItemPathname(),
                userStorageRoot).toString());
        //вызываем метод удаления папки или файла
        return fileUtils.deleteFileObject(fileObject);
    }

    public Path getSTORAGE_ROOT_PATH() {
        return STORAGE_ROOT_PATH;
    }

    public String getSTORAGE_DEFAULT_DIR() {
        return STORAGE_DEFAULT_DIR;
    }

    public UsersAuthController getUsersAuthController() {
        return usersAuthController;
    }

    public FileUtils getFileUtils() {
        return fileUtils;
    }

    public void printMsg(String msg){
        log.append(msg).append("\n");
    }

}