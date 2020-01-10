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
    private final Path storageRoot = Paths.get("storage","server_storage");
    //инициируем константу строки названия корневой директории для списка в серверной части GUI
    private final String storageDefaultDirectory = "";
    //объявляем множество авторизованных клиентов <соединение, логин>
    private Map<ChannelHandlerContext, String> authorizedUsers;
    //объявляем объект контроллера авторизации клиента
    private UsersAuthController usersAuthController;
    //объявляем объект файлового обработчика
    private FileUtils fileUtils;

    public void run() throws Exception {
        //инициируем множество авторизованных клиентов
        authorizedUsers = new HashMap<>();
        //инициируем объект контроллера авторизации пользователей
        usersAuthController = new UsersAuthController(this);
        //инициируем объект файлового обработчика
        fileUtils = new FileUtils();
        //инициируем объект сетевого подключения
        new NettyServer(this, PORT).run();
    }

    public Path getStorageRoot() {
        return storageRoot;
    }

    public String getStorageDefaultDirectory() {
        return storageDefaultDirectory;
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