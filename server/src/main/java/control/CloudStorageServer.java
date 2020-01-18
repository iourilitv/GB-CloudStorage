package control;

import io.netty.channel.ChannelHandlerContext;
import jdbc.UsersAuthController;
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
    private static final Path STORAGE_ROOT_PATH = Paths.get("storage","server_storage");
    //инициируем константу строки названия корневой директории для списка в серверной части GUI
    private final String STORAGE_DEFAULT_DIR = "";
    //объявляем объекты директории пользователя по умолчанию в серверной части GUI
    private Item storageDefaultDirItem;
    //объявляем множество авторизованных клиентов <соединение, логин>
    //TODO перенести их storageServer в UsersAuthController
    private Map<ChannelHandlerContext, String> authorizedUsers;

    //объявляем объект контроллера авторизации клиента
    private UsersAuthController usersAuthController;
    //объявляем объект файлового обработчика
    private FileUtils fileUtils = FileUtils.getOwnObject();
    //принимаем объект обработчика операций с объектами элементов списков в GUI
    private final ItemUtils itemUtils = ItemUtils.getOwnObject();

    public void run() throws Exception {
//        //инициируем множество авторизованных клиентов
//        //TODO перенести их storageServer в UsersAuthController
//        authorizedUsers = new HashMap<>();

        //инициируем объект контроллера авторизации пользователей
        usersAuthController = UsersAuthController.getOunInstance(this);
        //устанавливаем связь с БД в момент запуска сервера
        usersAuthController.connect();
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

    /**
     * Метод запускает процесс сохранения полученного от клиента объекта(файла)
     * в заданную директорию в сетевом хранилище.
     * @param storageToDirItem - объект заданной директории в сетевом хранилище
     * @param item - объект элемента от клиента
     * @param data - массив байт из файла
     * @param fileSize - размер файла
     * @param userStorageRoot - объект пути к корневой директории пользователя в сетевом хранилище
     * @return - результат сохранения объекта
     */
    public boolean uploadItem(Item storageToDirItem, Item item, byte[] data, long fileSize, Path userStorageRoot){
        //инициируем новый объект пути к объекту
        Path realNewToItemPath = Paths.get(
                itemUtils.getRealPath(storageToDirItem.getItemPathname(), userStorageRoot).toString(),
                item.getItemName());
        return fileUtils.saveFile(realNewToItemPath, data, fileSize);
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
     * @throws IOException - исключение ввода-вывода
     */
    public void downloadItem(FileMessage fileMessage, Path userStorageRoot,
                             ChannelHandlerContext ctx) throws IOException {
        //если объект элемента - это директория
        if(fileMessage.getItem().isDirectory()){
            //FIXME что-то делаем, а пока выходим
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
        int command;
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

//    public Map<ChannelHandlerContext, String> getAuthorizedUsers() {
//        return authorizedUsers;
//    }
//    public Map<ChannelHandlerContext, String> getAuthorizedUsers() {
//        return usersAuthController.getAuthorizedUsers();
//    }

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