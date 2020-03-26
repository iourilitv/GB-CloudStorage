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
import java.nio.file.*;

/**
 * This client's class is responsible for operation with storage by communication with command handlers.
 */
public class CloudStorageClient {
    //инициируем переменную для печати сообщений в консоль
    private final PrintStream log = System.out;
    //принимаем объект хендлера для операций с директориями
    private GUIController guiController;
    //принимаем объект соединения
    private ChannelHandlerContext ctx;
    //объявляем переменную IP адреса сервера
    private static String IP_ADDR;
    //объявляем переменную порта соединения
    private static int PORT;
    //объявляем переменную объект пути к корневой директории для списка в клиентской части GUI
    public static Path CLIENT_ROOT_PATH;
    //объявляем объект файлового обработчика
    private FileUtils fileUtils = FileUtils.getInstance();
    //принимаем объект обработчика операций с объектами элементов списков в GUI
    private final ItemUtils itemUtils = ItemUtils.getInstance();
    //инициируем объект хендлера настроек приложения
    private final PropertiesHandler propertiesHandler = PropertiesHandler.getOwnObject();

    public CloudStorageClient(GUIController guiController) {
        //принимаем объект контроллера GUI
        this.guiController = guiController;
    }

    /**
     * Метод инициирует процесс настройки серверного приложения.
     */
    public void initConfiguration() {
        //запускаем процесс применения конфигурации приложения
        propertiesHandler.setConfiguration();
        //инициируем переменную IP адреса сервера
        String ip_addr = propertiesHandler.getProperty("IP_ADDR");
        //если пользователем задано другое значение IP адреса
        if(!ip_addr.isEmpty()){
            //применяем значение пользователя
            IP_ADDR = ip_addr;
        } else {
            //в противном случае применяем дефорлтное значение IP адреса
            IP_ADDR = propertiesHandler.getProperty("IP_ADDR_DEFAULT");
        }
        //выводим в лог значение ip-адреса сервера
        writeToLog("CloudStorageClient.initConfiguration() - IP_ADDR: " + IP_ADDR);

        //инициируем переменную порта соединения
        String port = propertiesHandler.getProperty("PORT");
        //если пользователем задано другое значение порта
        if(!port.isEmpty()){
            //применяем значение пользователя
            PORT = Integer.parseInt(port);
        } else {
            //в противном случае применяем дефорлтное значение порта
            PORT = Integer.parseInt(propertiesHandler.getProperty("PORT_DEFAULT"));
        }
        //выводим в лог значение порта сервера
        writeToLog("CloudStorageClient.initConfiguration() - PORT: " + PORT);

        //инициируем переменную объект пути к корневой директории для списка в клиентской части GUI
        String root_absolute = propertiesHandler.getProperty("Root_absolute");
        //если поле свойства не пустое и путь реально существует(например, usb-флешка вставлена)
        if(!root_absolute.isEmpty() && Files.exists(Paths.get(root_absolute))){
            //применяем значение пользователя
            CLIENT_ROOT_PATH = Paths.get(root_absolute);
        } else {
            //в противном случае применяем дефорлтное значение пути к корневой директории
            CLIENT_ROOT_PATH = Paths.get(propertiesHandler.getProperty("Root_default"));

            try {
                //создаем новую клиентскую директорию, если еще не создана
                Files.createDirectory(CLIENT_ROOT_PATH);
            } catch (IOException e) {
                writeToLog("[client]CloudStorageClient.initConfiguration() - " +
                        "Something wrong with new client root directory creating. Probably it does already exist!");
            }
        }
        //выводим в лог значение корневой директории клиента
        writeToLog("CloudStorageClient.initConfiguration() - CLIENT_ROOT_PATH: " + CLIENT_ROOT_PATH);
    }

    /**
     * Метод начала работы клиента сетевого зранилища.
     */
    public void run() throws Exception {
        //инициируем объект соединения
        new NettyClient(this, IP_ADDR, PORT).run();
    }

    /**
     * Метод отправляет на сервер запрос на регистрацию нового пользователя в облачное хранилище.
     * @param login - логин пользователя
     * @param first_name - имя пользователя
     * @param last_name - фамилия пользователя
     * @param email - email пользователя
     * @param password - пароль пользователя
     */
    public void demandRegistration(String login, String first_name, String last_name,
                                   String email, String password) {
        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_REGISTRATION,
                new AuthMessage(login, first_name, last_name, email, password)));
    }

    /**
     * Метод отправляет на сервер запрос на авторизацию пользователя в облачное хранилище.
     * @param login - логин пользователя
     * @param password - пароль пользователя
     */
    public void demandAuthorization(String login, String password) {
        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_AUTH,
                new AuthMessage(login, password)));
    }

    /**
     * Метод отправляет запрос на изменение пароля пользователя в сетевое хранилище.
     * @param login - логин пользователя
     * @param password - текущий пароль пользователя
     * @param newPassword - новый пароль пользователя
     */
    public void demandChangePassword(String login, String password, String newPassword) {
        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_CHANGE_PASSWORD,
                new AuthMessage(login, password, newPassword)));
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
     * размером более константы максимального размера фрагмента файла.
     * @param storageToDirItem - объект директори назначения в сетевом хранилище
     * @param clientItem - объект элемента в клиенте
     * @param fullFileSize - размер целого файла в байтах
     */
    private void uploadFileByFrags(Item storageToDirItem, Item clientItem, long fullFileSize) {
        //выводим сообщение в GUI
        showTextInGUI("File uploading. Cutting into fragments...");
        fileUtils.cutAndSendFileByFrags(storageToDirItem, clientItem, fullFileSize,
                CLIENT_ROOT_PATH, ctx, Commands.REQUEST_SERVER_UPLOAD_FILE_FRAG);
    }

    /**
     * Метод-прокладка запускает процесс отправки отдельного фрагмента файла в сетевое хранилище.
     * @param fileFragMsg - объект сообщения фрагмента файла из объекта сообщения(команды)
     * @param command - переменная типа команды
     */
    public void sendFileFragment(FileFragmentMessage fileFragMsg, Commands command) {
        //инициируем новый байтовый массив
        byte[] data = new byte[fileFragMsg.getFileFragmentSize()];
        //вычисляем индекс стартового байта фрагмента в целом файле
        long startByte = FileFragmentMessage.CONST_FRAG_SIZE * fileFragMsg.getCurrentFragNumber();
        //вызываем метод отправки объекта сообщения с новым байтовым массивом данных фрагмента
        fileUtils.sendFileFragment(fileFragMsg.getToDirectoryItem(), fileFragMsg.getItem(),
                fileFragMsg.getFullFileSize(), fileFragMsg.getCurrentFragNumber(),
                fileFragMsg.getTotalFragsNumber(), fileFragMsg.getFileFragmentSize(),
                data, startByte, CLIENT_ROOT_PATH, ctx, command);
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
            writeToLog("[client]" + fileUtils.getMsg());
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
     * в заданную директорию в клиенте.
     * @param fileMessage - объект фалового сообщения
     * @return - результат сохранения объекта
     */
    public boolean downloadItem(FileMessage fileMessage){
        //инициируем локальную переменную объекта директории назначения в клиенте
        Item clientToDirItem = fileMessage.getClientDirectoryItem();
        //инициируем строку имени реального пути к папке с объектом элемента
        String realDirPathname = itemUtils.getRealPath(clientToDirItem.getItemPathname(), CLIENT_ROOT_PATH).toString();
        //инициируем новый объект пути к объекту
        Path realNewToItemPath = Paths.get(realDirPathname, fileMessage.getItem().getItemName());
        return fileUtils.saveFile(fileMessage, realNewToItemPath);
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
     * Метод-прокладка запускаем процесс создания новой папки в текущей директории в клиенте.
     * @param clientCurrentDirPathname - строка рути к текущей директории в клиенте
     * @param newDirName - строка имени новой папки
     * @return - результат создания новой папки в текущей директории в клиенте
     */
    public boolean createNewFolder(String clientCurrentDirPathname, String newDirName) {
        //инициируем строку реального пути к текущей папке
        String realCurrentDirPathname = itemUtils.getRealPath(
                clientCurrentDirPathname, CLIENT_ROOT_PATH).toString();
        //инициируем строку реального пути к новой папке
        String realNewDirPathname = Paths.get(realCurrentDirPathname, newDirName).toString();
        //запускаем процесс создания новой папки в текущей директории в клиенте
        return fileUtils.createNewFolder(realNewDirPathname);
    }

    /**
     * Метод отправляет на сервер запрос на объекта новой папки в текущей директории в облачном хранилище.
     * @param storageCurrentDirPathname - строка рути к текущей директории в облачном хранилище
     * @param newDirName - строка имени новой папки
     */
    public void demandCreateNewDirectory(String storageCurrentDirPathname, String newDirName) {
        //отправляем на сервер объект сообщения(команды)
        ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_CREATE_NEW_FOLDER,
                new DirectoryMessage(storageCurrentDirPathname, newDirName)));
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

    /**
     * Метод-прокладка запускает процесс сохранения в конфигурационный файл
     * нового значения абсолютного пути к корневой директории клиента.
     */
    public void saveClientRootPathProperty(String propertyValue){
        propertiesHandler.savePropertyIntoConfigFile("Root_absolute", propertyValue);
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

    public void writeToLog(String msg){
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

    /**
     * Метод отправляет на сервер запрос об отключении.
     */
    public void demandDisconnect() {
        //если соединение установлено
        if(ctx != null && !ctx.isRemoved()){
            //выводим сообщение в метку уведомлений
            showTextInGUI("Disconnecting the Cloud Storage server...");
            //и в лог
            writeToLog("CloudStorageClient.demandDisconnecting() - Отправляем серверу запрос о разрыве соединения");
            //отправляем на сервер объект сообщения(команды)
            ctx.writeAndFlush(new CommandMessage(Commands.REQUEST_SERVER_DISCONNECT,
                    new AuthMessage()));
        }
    }

    /**
     * Метод закрывает соединение с сервером и устанавливает режим отображения GUI "Отсоединен".
     */
    public void disconnect() {
        //если соединение установлено
        if(ctx != null && !ctx.isRemoved()){
            //закрываем соединение
            ctx.close();
        }
        //устанавливаем режим отображения GUI "Отсоединен"
        guiController.setDisconnectedMode(true);
    }

}
