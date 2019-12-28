package utils;

import netty.NettyServer;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    //объявляем объект контроллера авторизации клиента
    private UsersAuthController usersAuthController;

    public void run() throws Exception {
        //инициируем объект контроллера авторизации пользователей
        usersAuthController = new UsersAuthController(this);
        //инициируем объект сетевого подключения
        new NettyServer(this, PORT).run();
    }

    public Path getStorageRoot() {
        return storageRoot;
    }

    public UsersAuthController getUsersAuthController() {
        return usersAuthController;
    }

    public void printMsg(String msg){
        log.append(msg).append("\n");
    }
}