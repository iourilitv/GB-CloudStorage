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
    private FileUtils fileUtils;
    //принимаем объект обработчика операций с объектами элементов списков в GUI
    private final ItemUtils itemUtils = ItemUtils.getOwnObject();

    //FIXME temporarily - будет получать из GUI
    //инициируем константы логина и пароля пользователя
    private final String login = "login1";
    private final String password = "pass1";

    public CloudStorageClient(GUIController guiController) {
        //принимаем объект контроллера GUI
        this.guiController = guiController;
        //инициируем объект файлового обработчика
        fileUtils = new FileUtils();
    }

    /**
     * Метод начала работы клиента сетевого зранилища.
     * @throws Exception - исключение
     */
    public void run() throws Exception {
        //инициируем объект соединения
        new NettyClient(this, IP_ADDR, PORT).run();//TODO
    }

    /**
     * Публичный метод отправляет на сервер запрос на авторизацию в облачное хранилище
     * @param ctx - объект сетевого соединения
     */
    public void startAuthorization(ChannelHandlerContext ctx) {
        //принимаем объект сетевого соединения
        this.ctx = ctx;

        //TODO temporarily
        printMsg("***CloudStorageClient.requestAuthorization() - has started***");

        //отправляем на сервер запрос на авторизацию в облачное хранилище
        requestAuthorization(login, password);

        //TODO temporarily
        printMsg("***CloudStorageClient.requestAuthorization() - has finished***");
    }

    /**
     * Приватный метод отправляет на сервер запрос на авторизацию в облачное хранилище
     * @param login - логин пользователя
     * @param password - пароль пользователя
     */
    private void requestAuthorization(String login, String password) {
        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_AUTH,
                new AuthMessage(login, password)));
    }

//    /**
//     * Метод отправляет на сервер запрос на получение списка элементов заданной директории
//     * пользователя в сетевом хранилище
//     * @param directoryPathname - строка заданной относительной директории пользователя
//     * в сетевом хранилище
//     */
//    public void demandDirectoryItemList(String directoryPathname) {
//        //отправляем на сервер объект сообщения(команды)
//        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_FILE_OBJECTS_LIST,
//                new DirectoryMessage(directoryPathname)));
//    }
    public void demandDirectoryItemList(String directoryPathname) {
        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_ITEMS_LIST,
                new DirectoryMessage(directoryPathname)));
    }

//    /**
//     * Метод отправляет на сервер запрос на загрузку файла в облачное хранилище
//     * @param fromDir - директория источник на клиенте
//     * @param toDir - директория назначения в облачном хранилище
//     * @param filename - имя файла
//     * @throws IOException - исключение
//     */
//    public void demandUploadFile(String fromDir, String toDir, String filename) throws IOException {
//        //TODO temporarily
//        printMsg("***CloudStorageClient.uploadFile() - has started***");
//
//        //TODO temporarily
//        System.out.println("CloudStorageClient.uploadFile - fromDir: " + fromDir +
//                ", toDir: " + toDir + ", filename: " + filename);
//
//        //вычисляем размер файла
//        long fileSize = Files.size(Paths.get(fromDir, filename));
//        //если размер файла больше константы размера фрагмента
//        if(fileSize > FileFragmentMessage.CONST_FRAG_SIZE){
//            //запускаем метод отправки файла по частям
//            uploadFileByFrags(fromDir, toDir, filename, fileSize);
//        //если файл меньше
//        } else {
//            //запускаем метод отправки целого файла
//            uploadEntireFile(fromDir, toDir, filename, fileSize);
//        }
//
//        //TODO temporarily
//        printMsg("***CloudStorageClient.uploadFile() - has finished***");
//    }
    public void demandUploadFile(String clientFromDir, String storageToDir, Item clientItem) throws IOException {
        //TODO temporarily
        printMsg("***CloudStorageClient.uploadFile() - has started***");

        //TODO temporarily
//        System.out.println("CloudStorageClient.uploadFile - fromDir: " + fromDir +
//                ", toDir: " + toDir + ", filename: " + filename);

        Path realClientItemPath = itemUtils.getRealPath(clientItem.getItemPathname(), CLIENT_ROOT_PATH);

        //вычисляем размер файла
//        long fileSize = Files.size(Paths.get(fromDir, filename));
        long fileSize = Files.size(realClientItemPath);

        //если размер файла больше константы размера фрагмента
        if(fileSize > FileFragmentMessage.CONST_FRAG_SIZE){
            //запускаем метод отправки файла по частям
//            uploadFileByFrags(fromDir, toDir, filename, fileSize);//FIXME

            //если файл меньше
        } else {
            //запускаем метод отправки целого файла
            uploadEntireFile(clientFromDir, storageToDir, clientItem, fileSize);
        }

        //TODO temporarily
        printMsg("***CloudStorageClient.uploadFile() - has finished***");
    }

    /**
     * Метод отправки по частям большого файла размером более константы максмального размера фрагмента файла
     * @param fromDir - директория(относительно корня) клиента где хранится файл источник
     * @param toDir - директория(относительно корня) в сетевом хранилище
     * @param filename - строковое имя файла
     * @param fullFileSize - размер целого файла в байтах
     * @throws IOException - исключение
     */
    private void uploadFileByFrags(String fromDir, String toDir,
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
        System.out.println("CloudStorageClient.uploadFileByFrags() - fullFileSize: " + fullFileSize);
        System.out.println("CloudStorageClient.uploadFileByFrags() - totalFragsNumber: " + totalFragsNumber);
        System.out.println("CloudStorageClient.uploadFileByFrags() - totalEntireFragsNumber: " + totalEntireFragsNumber);

        //устанавливаем началные значения номера текущего фрагмента и стартового байта
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

            //отправляем на сервер объект сообщения(команды)
            ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_FILE_FRAG_UPLOAD,
                    fileFragmentMessage));

        }

        //TODO temporarily
        System.out.println("CloudStorageClient.uploadFileByFrags() - currentFragNumber: " + totalFragsNumber);
        System.out.println("CloudStorageClient.uploadFileByFrags() - finalFileFragmentSize: " + finalFileFragmentSize);

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

            //отправляем на сервер объект сообщения(команды)
            ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_FILE_FRAG_UPLOAD,
                    fileFragmentMessage));
        }

        //TODO temporarily
        long finish = System.currentTimeMillis() - start;
        System.out.println("CloudStorageClient.uploadFileByFrags() - duration(mc): " + finish);
    }

//    /**
//     * Метод отправки целого файла размером менее константы максмального размера фрагмента файла
//     * @param fromDir - директория(относительно корня) клиента, где хранится файл источник
//     * @param toDir - директория(относительно корня) в сетевом хранилище
//     * @param filename - строковое имя файла
//     * @param fileSize - размер файла в байтах
//     */
//    private void uploadEntireFile(String fromDir, String toDir, String filename, long fileSize) {
//        //инициируем объект файлового сообщения
//        FileMessage fileMessage = new FileMessage(fromDir, toDir, filename, fileSize);
//
//        //TODO temporarily
//        System.out.println("CloudStorageClient.uploadEntireFile() - fileUtils: " + fileUtils +
//                ", fromDir: " + fromDir +
//                ", toDir: " + toDir +
//                ", fileMessage: " + fileMessage);
//
//        //читаем файл и записываем данные в байтовый массив объекта файлового сообщения
//        //FIXME Разобраться с абсолютными папкими клиента
//        //если скачивание прошло удачно
//        if(fileUtils.readFile(fromDir, fileMessage)){
//            //отправляем на сервер объект сообщения(команды)
//            ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_FILE_UPLOAD,
//                    fileMessage));
//        //если что-то пошло не так
//        } else {
//            //выводим сообщение
//            printMsg("[client]" + fileUtils.getMsg());
//        }
//    }
    private void uploadEntireFile(String clientFromDir, String storageToDir,
                                  Item clientItem, long fileSize) {
        //инициируем объект файлового сообщения
        FileMessage fileMessage = new FileMessage(clientFromDir, storageToDir,
                clientItem.getItemName(), fileSize);

        //TODO temporarily
        System.out.println("CloudStorageClient.uploadEntireFile() - fileUtils: " + fileUtils +
                ", fromDir: " + clientFromDir +
                ", toDir: " + storageToDir +
                ", fileMessage: " + fileMessage);

        //читаем файл и записываем данные в байтовый массив объекта файлового сообщения
        //FIXME Разобраться с абсолютными папкими клиента
        //если скачивание прошло удачно
        if(fileUtils.readFile(clientItem.getItemPathname(), fileMessage)){
            //отправляем на сервер объект сообщения(команды)
            ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_FILE_UPLOAD,
                    fileMessage));
            //если что-то пошло не так
        } else {
            //выводим сообщение
            printMsg("[client]" + fileUtils.getMsg());
        }
    }

    /**
     * Метод отправляет на сервер запрос на скачивание файла из облачного хранилища
     * @param fromDir - директория(относительно корня) в сетевом хранилище, где хранится файл источник
     * @param toDir - директория(относительно корня) клиента
     * @param filename - строковое имя файла
     */
    public void demandDownloadFile(String fromDir, String toDir, String filename){
        //TODO temporarily
        printMsg("***CloudStorageClient.downloadFile() - has started***");

        //инициируем объект файлового сообщения
        FileMessage fileMessage = new FileMessage(fromDir, toDir, filename);

        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_FILE_DOWNLOAD,
                fileMessage));

        //TODO temporarily
        printMsg("***CloudStorageClient.downloadFile() - has finished***");
    }

    /**
     * Метод переименовывает объект элемента списка.
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
     * Метод отправляет на сервер запрос на переименовании файла или папки в облачном хранилище
     * @param directory - заданная директория в облачном хранилище
     * @param itemName - объект элемента списка
     */
    public void demandRenameItem(String directory, String itemName, String newName) {
        //инициируем объект файлового сообщения
        FileMessage fileMessage = new FileMessage(directory, itemName);
        //записываем в сообщение новое имя(вынужденно, т.к. такой конструктор уже занят)
        fileMessage.setNewName(newName);
        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_RENAME_FILE_OBJECT,
                fileMessage));
    }

    /**
     * Метод удаляет файл или папку в текущей директории на клиенте
     * @param item - объект списка в клиенте
     * @return - результат переименования
     */
    public boolean deleteClientItem(Item item) {
        //инициируем файловый объект для объекта списка в клиенте
        File fileObject = new File(itemUtils.getRealPath(item.getItemPathname(), CLIENT_ROOT_PATH).toString());
        //вызываем метод удаления папки или файла
        return fileUtils.deleteFileObject(fileObject);
    }

    /**
     * Метод отправляет на сервер запрос на удаление файла или папки в облачном хранилище
     * @param directory - заданная директория в облачном хранилище
     * @param itemName - объект элемента списка
     */
    public void demandDeleteItem(String directory, String itemName) {
        //инициируем объект файлового сообщения
        FileMessage fileMessage = new FileMessage(directory, itemName);
        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_DELETE_FILE_OBJECT,
                fileMessage));
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

    public void printMsg(String msg){
        log.append(msg).append("\n");
    }

}
