package control;

import io.netty.channel.ChannelHandlerContext;
import javafx.GUIController;
import messages.AuthMessage;
import messages.DirectoryMessage;
import messages.FileFragmentMessage;
import messages.FileMessage;
import netty.NettyClient;
import utils.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This client's class is responded for operation with storage by communication with command handlers.
 */
public class CloudStorageClient {
    //принимаем объект хендлера для операций с директориями
    private GUIController guiController;
    //принимаем объект соединения
    ChannelHandlerContext ctx;
    //инициируем константу IP адреса сервера(здесь - адрес моего ноута в домашней локальной сети)
    private static final String IP_ADDR = "localhost";//"192.168.1.102";//89.222.249.131(внешний белый адрес)
    //инициируем константу порта соединения
    private static final int PORT = 8189;
    //инициируем переменную для печати сообщений в консоль
    private final PrintStream log = System.out;
    //инициируем константу объект пути к корневой директории для списка в клиентской части GUI
    public static final Path CLIENT_ROOT_PATH = Paths.get("storage","client_storage");
    //объявляем объект файлового обработчика
    private FileUtils fileUtils = FileUtils.getOwnObject();
    //принимаем объект обработчика операций с объектами элементов списков в GUI
    private final ItemUtils itemUtils = ItemUtils.getOwnObject();

    public CloudStorageClient(GUIController guiController) {
        //принимаем объект контроллера GUI
        this.guiController = guiController;
    }

    /**
     * Метод начала работы клиента сетевого зранилища.
     * @throws Exception - исключение
     */
    public void run() throws Exception {
        //инициируем объект соединения
        new NettyClient(this, IP_ADDR, PORT).run();
    }


    public void demandRegistration(String login, String password) {
        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_REGISTRATION,
                new AuthMessage(login, password)));
    }

//    /**
//     * Публичный метод отправляет на сервер запрос на авторизацию в облачное хранилище
//     */
//    public void startAuthorization() {
//        //отправляем на сервер запрос на авторизацию в облачное хранилище
//        requestAuthorization(guiController.getLogin(), guiController.getPassword());
//    }
//    /**
//     * Приватный метод отправляет на сервер запрос на авторизацию в облачное хранилище
//     * @param login - логин пользователя
//     * @param password - пароль пользователя
//     */
//    private void requestAuthorization(String login, String password) {
//        //отправляем на сервер объект сообщения(команды)
//        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_AUTH,
//                new AuthMessage(login, password)));
//    }
    public void demandAuthorization(String login, String password) {
        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_AUTH,
                new AuthMessage(login, password)));
    }

    /**
     * Метод отправляет на сервер запрос на получение списка элементов заданной директории
     * пользователя в сетевом хранилище
     * @param directoryPathname - строка заданной относительной директории пользователя
     * в сетевом хранилище
     */
    public void demandDirectoryItemList(String directoryPathname) {
        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_ITEMS_LIST,
                new DirectoryMessage(directoryPathname)));
    }

    /**
     * Метод отправляет на сервер запрос на загрузку объекта элемента(пока только файла)
     * из клиента в облачное хранилище.
     * @param storageToDirItem - объект директории назначения в сетевом хранилище
     * @param clientItem - объект элемента списка(файла) на клиенте
     * @throws IOException - исключение ввода-вывода
     */
    public void demandUploadItem(Item storageToDirItem, Item clientItem) throws IOException {
        //если объект элемента - это директория
        if(clientItem.isDirectory()){
            //выводим сообщение в нижнюю метку GUI
            showTextInGUI("It is not allowed to upload a directory!");
            return;
        }
        //инициируем объект реального пути к объекту элемента в клиенте
        Path realClientItemPath = itemUtils.getRealPath(clientItem.getItemPathname(), CLIENT_ROOT_PATH);
        //вычисляем размер файла
        long fileSize = Files.size(realClientItemPath);
        //если размер файла больше константы размера фрагмента
        if(fileSize > FileFragmentMessage.CONST_FRAG_SIZE){
            //запускаем метод отправки файла по частям
            uploadFileByFrags(storageToDirItem, clientItem, fileSize);
            //если файл меньше
        } else {
            //запускаем метод отправки целого файла
            uploadEntireFile(storageToDirItem, clientItem, fileSize);
        }
    }

    /**
     * Метод-прокладка запускает процесс нарезки и отправки клиенту по частям большого файла
     * размером более константы максмального размера фрагмента файла
     * @param storageToDirItem - объект директори назначения в сетевом хранилище
     * @param clientItem - объект элемента в клиенте
     * @param fullFileSize - размер целого файла в байтах
     */
    private void uploadFileByFrags(Item storageToDirItem, Item clientItem, long fullFileSize) {
        fileUtils.cutAndSendFileByFrags(storageToDirItem, clientItem, fullFileSize,
                CLIENT_ROOT_PATH, ctx, Commands.REQUEST_SERVER_UPLOAD_FILE_FRAG);
    }

    /**
     * Метод отправки целого файла размером менее константы максмального размера фрагмента файла.
     * @param storageToDirItem - объект директории назначения в сетевом хранилище
     * @param clientItem - объект элемента списка(файла) на клиенте
     * @param fileSize - размер файла в байтах
     */
    private void uploadEntireFile(Item storageToDirItem, Item clientItem, long fileSize) {
        //инициируем объект файлового сообщения
        FileMessage fileMessage = new FileMessage(storageToDirItem,
                clientItem, fileSize);
        //читаем файл и записываем данные в байтовый массив объекта файлового сообщения
        //если скачивание прошло удачно
        if(fileUtils.readFile(itemUtils.getRealPath(clientItem.getItemPathname(), CLIENT_ROOT_PATH),
                fileMessage)){
            //отправляем на сервер объект сообщения(команды)
            ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_UPLOAD_ITEM,
                    fileMessage));
            //если что-то пошло не так
        } else {
            //выводим сообщение в консоль
            printMsg("[client]" + fileUtils.getMsg());
            //выводим сообщение в нижнюю метку GUI
            showTextInGUI(fileUtils.getMsg());
        }
    }

    /**
     * Метод отправляет на сервер запрос на скачивание объекта элемента из облачного хранилища.
     * @param storageFromDirItem - объект директории источника в сетевом хранилище
     * @param clientToDirItem - объект директории назначения в клиенте
     * @param storageItem - объект объекта элемента(источника) в сетевом хранилище
     */
    public void demandDownloadItem(Item storageFromDirItem, Item clientToDirItem, Item storageItem){
        //инициируем объект файлового сообщения
        FileMessage fileMessage = new FileMessage(storageFromDirItem, clientToDirItem, storageItem);
        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_DOWNLOAD_ITEM,
                fileMessage));
    }

    /**
     * Метод запускает процесс сохранения полученного от сервера объекта(файла)
     * в заданную директорию в сетевом хранилище.
     * @param clientToDirItem - объект заданной директории в клиенте
     * @param item - объект элемента о сервера
     * @param data - массив байт из файла
     * @param fileSize - размер файла
     * @return - результат сохранения объекта
     */
    public boolean downloadItem(Item clientToDirItem, Item item, byte[] data, long fileSize){
        //инициируем строку имени реального пути к папке с объектом элемента
        String realDirPathname = itemUtils.getRealPath(clientToDirItem.getItemPathname(), CLIENT_ROOT_PATH).toString();
        //инициируем новый объект пути к объекту
        Path realNewToItemPath = Paths.get(realDirPathname, item.getItemName());
        return fileUtils.saveFile(realNewToItemPath, data, fileSize);
    }

    /**
     * Метод запускает процесс сохранения файла-фрагмента из полученного байтового массива
     * во временной директории в клиенте.
     * @param fileFragMsg - объект файлового сообщения
     * @return результат процесс сохранения файла-фрагмента из полученного байтового массива
     */
    public boolean downloadItemFragment(FileFragmentMessage fileFragMsg) {
        //инициируем реальный путь к временной папке для файлов-фрагментов
        Path realToTempDirPath = itemUtils.getRealPath(
                Paths.get(
                        fileFragMsg.getToDirectoryItem().getItemPathname(),
                        fileFragMsg.getToTempDirName()).toString(),
                CLIENT_ROOT_PATH);
        //инициируем реальный путь к файлу-фрагменту
        Path realToFragPath = Paths.get(
                realToTempDirPath.toString(), fileFragMsg.getFragName());
        //если сохранение полученного фрагмента файла во временную папку сетевого хранилища прошло удачно
        return fileUtils.saveFileFragment(realToTempDirPath, realToFragPath, fileFragMsg);
    }

    /**
     * Метод запускает процесс сборки целого файла из файлов-фрагментов.
     * @param fileFragMsg - объект файлового сообщения
     * @return результат процесса сборки целого файла из файлов-фрагментов
     */
    public boolean compileItemFragments(FileFragmentMessage fileFragMsg) {
        //инициируем реальный путь к временной папке для файлов-фрагментов
        Path realToTempDirPath = itemUtils.getRealPath(
                Paths.get(
                        fileFragMsg.getToDirectoryItem().getItemPathname(),
                        fileFragMsg.getToTempDirName()).toString(),
                CLIENT_ROOT_PATH);
        //инициируем реальный путь к файлу-фрагменту
        Path realToFilePath = itemUtils.getRealPath(
                Paths.get(
                        fileFragMsg.getToDirectoryItem().getItemPathname(),
                        fileFragMsg.getItem().getItemName()).toString(),
                CLIENT_ROOT_PATH);
        //возвращаем результат процесса сборки целого объекта(файла) из файлов-фрагментов
        return fileUtils.compileFileFragments(realToTempDirPath, realToFilePath, fileFragMsg);
    }

    /**
     * Метод переименовывает объект элемента списка на клиенте.
     * @param origin - текущий объект элемента списка
     * @param newName - новое имя элемента
     * @return - результат переименования
     */
    public boolean renameClientItem(Item origin, String newName) {
        //инициируем объект пути к исходному файловому объекту
        Path originPath = itemUtils.getRealPath(origin.getItemPathname(), CLIENT_ROOT_PATH);
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
     * Метод отправляет на сервер запрос на переименовании объекта(файла или папки) в облачном хранилище.
     * @param storageDirectoryItem - объект заданной директории в облачном хранилище
     * @param storageOriginItem - объект элемента списка
     * @param newName - строка нового имени элемента списка
     */
    public void demandRenameItem(Item storageDirectoryItem, Item storageOriginItem, String newName) {
        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_RENAME_ITEM,
                new FileMessage(storageDirectoryItem, storageOriginItem, newName)));
    }

    /**
     * Метод удаляет файл или папку в текущей директории на клиенте
     * @param item - объект списка в клиенте
     * @return - результат удаления
     */
    public boolean deleteClientItem(Item item) {
        //инициируем файловый объект для объекта списка в клиенте
        File fileObject = new File(itemUtils.getRealPath(item.getItemPathname(), CLIENT_ROOT_PATH).toString());
        //вызываем метод удаления папки или файла
        return fileUtils.deleteFileObject(fileObject);
    }

    /**
     * Метод отправляет на сервер запрос на удаление объекта(файла или папки) в облачном хранилище.
     * @param storageDirectoryItem - объект заданной директории в облачном хранилище
     * @param item - объект элемента списка
     */
    public void demandDeleteItem(Item storageDirectoryItem, Item item) {
        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_DELETE_ITEM,
                new FileMessage(storageDirectoryItem, item)));
    }

    /**
     * Метод-прокладка возвращает объект элемента родительской директории объекта элемента текущей директории.
     * @param directoryItem - объект элемента текущей директории
     * @param defaultDirItem - объект элемента директории по умолчанию(начальной)
     * @param rootPath - объект пути к реальной корневой директории
     * @return - объект элемента родительской директории объекта элемента текущей директории
     */
    public Item getParentDirItem(Item directoryItem, Item defaultDirItem, Path rootPath) {
        return itemUtils.getParentDirItem(directoryItem, defaultDirItem,
                rootPath);
    }

    /**
     * Метод-прокладка возвращает массив объектов элементов в заданной директории в клиенте.
     * @param clientCurrentDirItem - объект заданной директории в клиенте
     * @return - массив объектов элементов в заданной директории в клиенте
     */
    public Item[] clientItemsList(Item clientCurrentDirItem) {
        return itemUtils.getItemsList(clientCurrentDirItem, CLIENT_ROOT_PATH);
    }

    public FileUtils getFileUtils() {
        return fileUtils;
    }

    public GUIController getGuiController() {
        return guiController;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void printMsg(String msg){
        log.append(msg).append("\n");
    }

    /**
     * Метод выводит сообщение в нижнюю метку GUI
     * @param text - сообщение
     */
    public void showTextInGUI(String text){
        //выводим сообщение в нижнюю метку GUI
        guiController.showTextInGUI(text);
    }

    public void demandDisconnecting() {

        System.out.println("CloudStorageClient.demandDisconnecting() - Отправляем серверу запрос о разрыве соединения");
        //отправляем на сервер объект сообщения(команды)
        //FIXME
//        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_DELETE_ITEM,
//                new FileMessage(storageDirectoryItem, item)));
    }

}
