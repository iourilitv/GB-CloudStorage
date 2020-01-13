package utils;

import io.netty.channel.ChannelHandlerContext;
import netty.NettyServer;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * This server's class for operating with a cloud storage.
 */
public class CloudStorageServer {
    //инициируем константу порта сервера
    private final int PORT = 8189;
    //инициируем переменную для печати сообщений в консоль
    private final PrintStream log = System.out;
    //инициируем объект пути к корневой директории облачного хранилища(сервера) для хранения файлов клиентов
    private final Path STORAGE_ROOT_PATH = Paths.get("storage","server_storage");//TODO static
    //инициируем константу строки названия корневой директории для списка в серверной части GUI
    private final String STORAGE_DEFAULT_DIR = "";
    //объявляем объекты директории пользователя по умолчанию в серверной части GUI
    private Item storageDefaultDirItem;
    //объявляем множество авторизованных клиентов <соединение, логин>
    private Map<ChannelHandlerContext, String> authorizedUsers;
    //объявляем объект контроллера авторизации клиента
    private UsersAuthController usersAuthController;
    //объявляем объект файлового обработчика
    private FileUtils fileUtils;//TODO переделать на синглтон
    //принимаем объект обработчика операций с объектами элементов списков в GUI
    private final ItemUtils itemUtils = ItemUtils.getOwnObject();

    public void run() throws Exception {
        //инициируем множество авторизованных клиентов
        authorizedUsers = new HashMap<>();
        //инициируем объект контроллера авторизации пользователей
        usersAuthController = new UsersAuthController(this);
        //инициируем объект файлового обработчика
        fileUtils = new FileUtils();
        //инициируем объект директории по умолчанию в серверной части GUI
        storageDefaultDirItem = new Item(STORAGE_DEFAULT_DIR);
        //инициируем объект сетевого подключения
        new NettyServer(this, PORT).run();
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

    public Path getSTORAGE_ROOT_PATH() {
        return STORAGE_ROOT_PATH;
    }

    public String getSTORAGE_DEFAULT_DIR() {
        return STORAGE_DEFAULT_DIR;
    }

    public Map<ChannelHandlerContext, String> getAuthorizedUsers() {
        return authorizedUsers;
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