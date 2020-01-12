package javafx;

import control.CloudStorageClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * The client class for operating with directoryMessages.
 */
public class GUIController implements Initializable {
    //объявляем объекты кнопок для коллекции файловых объектов клиента и сервера
    @FXML
    Button  clientHomeButton, storageHomeButton,//"В корневую директорию"
            clientGoUpButton, storageGoUpButton,//"Подняться на папку выше"
            clientNewFolderButton, storageNewFolderButton;//"Создать новую папку"
    //объявляем объекты меток для коллекций файловых объектов
    @FXML
    Label clientDirLabel, storageDirLabel;

    //объявляем объекты коллекций файловых объектов
    @FXML
    ListView<Item> clientItemListView, storageItemListView;

    @FXML
    Label label;//TODO Зачем?
    //объявляем объект контроллера клиента облачного хранилища
    private CloudStorageClient storageClient;
    //инициируем константу строки названия директории по умолчанию относительно корневой директории
    // для списка в клиентской части GUI
    private final String CLIENT_DEFAULT_DIR = "";
    //инициируем константу строки названия корневой директории для списка в серверной части GUI
    private final String STORAGE_DEFAULT_DIR = "";
    //получаем текущую папку списка файловых объектов в клиентской части GUI
    private String clientCurrentDirPathname;//TODO удалить?

    //получаем текущую папку списка файловых объектов в серверной части GUI
    private String currentStorageDir;//TODO удалить?
    //объявляем объект директории по умолчанию в клиенте
    private Item clientDefaultDirItem;

    //объявляем объект текущей папки списка файловых объектов в клиентской части GUI
    private Item clientCurrentDirItem;
    //объявляем переменную введенного нового имени
    private String newName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //инициируем объект клиента облачного хранилища
        storageClient = new CloudStorageClient(GUIController.this);
        //получаем текущую папку списка файловых объектов в клиентской части GUI
        clientCurrentDirPathname = CLIENT_DEFAULT_DIR;//TODO удалить?
        //инициируем объект директории по умолчанию в клиенте
        clientDefaultDirItem = new Item(CLIENT_DEFAULT_DIR);

        //получаем текущую папку списка файловых объектов в серверной части GUI
        currentStorageDir = STORAGE_DEFAULT_DIR;//TODO удалить?

        //инициируем в клиентской части интерфейса список объектов в директории по умолчанию
        initializeClientItemListView();
        //инициируем в серверной части интерфейса список объектов в директории по умолчанию
        initializeStorageItemListView();
    }

    /**
     * Метод инициирует в клиентской части интерфейса список объектов в директории по умолчанию
     */
    public void initializeClientItemListView() {
        //выводим в клиентской части интерфейса список объектов в директории по умолчанию
        updateClientItemListInGUI(clientDefaultDirItem);
    }

    /**
     * Метод инициирует в серверной части интерфейса список объектов в директории по умолчанию
     */
//    public void initializeStorageItemListView() {
//        //выводим в клиентской части интерфейса список объектов в директории по умолчанию
//        updateStorageItemListInGUI("../",
//                new File[]{new File("waiting for an item list from the server...")});
//        //в отдельном потоке
//        new Thread(() -> {
//            try {
//                //запускаем логику клиента облачного хранилища
//                storageClient.run();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
    public void initializeStorageItemListView() {
        //выводим в клиентской части интерфейса список объектов в директории по умолчанию
//        updateStorageItemListInGUI("../",
//                new Item[]{new Item("waiting for an item list from the server...",
//                        STORAGE_DEFAULT_DIR, "waiting for an item list from the server...",
//                        STORAGE_DEFAULT_DIR, false)});//FIXME
        //в отдельном потоке
        new Thread(() -> {
            try {
                //запускаем логику клиента облачного хранилища
                storageClient.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Метод обновляет список элементов списка в заданной директории клиентской части
     * @param directoryItem - объект заданной директории
     */
    public void updateClientItemListInGUI(Item directoryItem) {
        //обновляем объект текущей директории
        clientCurrentDirItem = directoryItem;
        //в отдельном потоке запускаем обновление интерфейса
        Platform.runLater(() -> {
            //записываем в метку относительный строковый путь текущей директории
            clientDirLabel.setText("../" + clientCurrentDirItem.getItemPathname());
            //обновляем заданный список объектов элемента
            updateListView(clientItemListView, clientItemsList(clientCurrentDirItem));
        });
    }

//    /**
//     * Метод выводит в GUI список файлов и папок в корневой пользовательской директории
//     * в сетевом хранилище.
//     * @param directory - заданная пользовательская директория в сетевом хранилище
//     * @param fileObjs - массив объектов класса File(файлы и директории)
//     */
//    public void updateStorageItemListInGUI(String directory, File[] fileObjs){
//        //обновляем текущую директорию
//        currentStorageDir = directory;
//        //в отдельном потоке запускаем обновление интерфейса
//        Platform.runLater(() -> {
//            //выводим текущую директорию в метку серверной части
//            storageDirLabel.setText(currentStorageDir);
//            //обновляем заданный список файловых объектов
//            updateListView(storageItemListView, fileObjs);
//        });
//    }
    /** //FIXME directory переделать на Item
     * Метод выводит в GUI список файлов и папок в корневой пользовательской директории
     * в сетевом хранилище.
     * @param directory - заданная пользовательская директория в сетевом хранилище
     * @param items - массив объектов класса File(файлы и директории)
     */
    public void updateStorageItemListInGUI(String directory, Item[] items){
        //обновляем текущую директорию
        currentStorageDir = directory;
        //в отдельном потоке запускаем обновление интерфейса
        Platform.runLater(() -> {
            //выводим текущую директорию в метку серверной части
            storageDirLabel.setText(currentStorageDir);
            //обновляем заданный список файловых объектов
            updateListView(storageItemListView, items);
        });
    }

    /**
     * Метод обновляет заданный список файловых объектов.
     * @param listView - коллекция файловых объектов
     * @param items - массив файловых объектов
     */
    private void updateListView(ListView<Item> listView, Item[] items) {
        //очищаем список элементов
        listView.getItems().clear();
        //обновляем список элементов списка
        listView.getItems().addAll(items);
        //инициируем объект кастомизированного элемента списка
        listView.setCellFactory(itemListView -> new FileListCell());
        //инициируем контекстное меню
        setContextMenu(listView);
    }

    /**
     * Метод инициирует контекстное меню для переданной в параметре коллекции файловых объектов.
     * @param listView - коллекция файловых объектов
     */
    private void setContextMenu(ListView<Item> listView){
        //инициируем объект контестного меню
        ContextMenu contextMenu = new ContextMenu();
        //если текущий список клиентский
        if(listView.equals(clientItemListView)){
            // добавляем скопом элементы в контестное меню
            contextMenu.getItems().add(menuItemUpload(listView));
            //если текущий список облачного хранилища
        } else if(listView.equals(storageItemListView)){
            // добавляем скопом элементы в контестное меню
            contextMenu.getItems().add(menuItemDownload(listView));
        }
        // добавляем скопом оставщиеся элементы в контестное меню
        contextMenu.getItems().addAll(menuItemRename(listView), menuItemDelete(listView));
        //создаем временный элемент контекстного меню
        MenuItem menuItem = menuItemGetInto(listView);
        //устаналиваем событие на клик правой кнопки мыши по элементу списка
        listView.setOnContextMenuRequested(event -> {
            //если контекстное меню уже показывается или снова кликнуть на пустой элемент списка
            if(contextMenu.isShowing() ||
                    listView.getSelectionModel().getSelectedItems().isEmpty()){
                //скрываем контекстное меню
                contextMenu.hide();
                //очищаем выделение
                listView.getSelectionModel().clearSelection();
                return;
            }
            // и если выбранный элемент это директория
            if(listView.getSelectionModel().getSelectedItem().isDirectory()){
                //если контекстное меню не показывается
                if(!contextMenu.getItems().contains(menuItem)){
                    // добавляем элемент в контестное меню
                    contextMenu.getItems().add(0, menuItem);
                }
            //если не директория
            } else {
                // удаляем элемент из контестного меню
                contextMenu.getItems().remove(menuItem);
            }
            //показываем контекстное меню в точке клика(позиция левого-верхнего угла контекстного меню)
            contextMenu.show(listView, event.getScreenX(), event.getScreenY());
        });
    }

    /**
     * Метод инициирует элемент контекстного меню "Получить список файловых объектов",
     * только для выбранной директории.
     * @param listView - текущий список файловых объектов
     * @return - объект элемента контекстного меню "Get into"
     */
    private MenuItem menuItemGetInto(ListView<Item> listView) {
        //инициируем пункт контекстного меню "Получить список файловых объектов"
        MenuItem menuItemGetInto = new MenuItem("Get into");
        //устанавливаем обработчика нажатия на этот пункт контекстного меню
        menuItemGetInto.setOnAction(event -> {
            //запоминаем кликнутый элемент списка
            Item item = listView.getSelectionModel().getSelectedItem();
            //если текущий список клиентский
            if(listView.equals(clientItemListView)){
                //обновляем список объектов элемента клиентской части
                updateClientItemListInGUI(item);
            //если текущий список облачного хранилища
            } else if(listView.equals(storageItemListView)){
                //отправляем на сервер запрос на получение списка элементов заданной директории
                //пользователя в сетевом хранилище
                storageClient.demandDirectoryItemList(item.getItemName());//FIXME
            }
            //сбрасываем выделение после действия
            listView.getSelectionModel().clearSelection();
        });
        return menuItemGetInto;
    }

    /**
     * Метод инициирует элемент контекстного меню "Загрузить в облачное хранилище"
     * @param listView - текущий список файловых объектов
     * @return - объект элемента контекстного меню "Upload"
     */
    private MenuItem menuItemUpload(ListView<Item> listView) {
        //инициируем пункт контекстного меню "Загрузить в облачное хранилище"
        MenuItem menuItemUpload = new MenuItem("Upload");
        //устанавливаем обработчика нажатия на этот пункт контекстного меню
        menuItemUpload.setOnAction(event -> {
            //запоминаем кликнутый элемент списка
            Item item = listView.getSelectionModel().getSelectedItem();

//            System.out.println("GUIController.callContextMenu().menuItemUpload().setOnAction() - " +
//                    "\nitem: " + item +
//                    ", item.getParent(): " + item.getParent());

            try {
                //отправляем на сервер запрос на загрузку файла в облачное хранилище
                storageClient.demandUploadFile(item.getParentName(), currentStorageDir, item.getItemName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //сбрасываем выделение после действия
            listView.getSelectionModel().clearSelection();
        });
        return menuItemUpload;
    }

    /**
     * Метод инициирует элемент контекстного меню "Скачать из облачного хранилища"
     * @param listView - текущий список файловых объектов
     * @return - объект элемента контекстного меню "Download"
     */
    private MenuItem menuItemDownload(ListView<Item> listView) {
        //инициируем пункт контекстного меню "Скачать из облачного хранилища"
        MenuItem menuItemDownload = new MenuItem("Download");
        //устанавливаем обработчика нажатия на этот пункт контекстного меню
        menuItemDownload.setOnAction(event -> {
            //запоминаем кликнутый элемент списка
            Item item = listView.getSelectionModel().getSelectedItem();
            //отправляем на сервер запрос на скачивание файла из облачного хранилища
            storageClient.demandDownloadFile(currentStorageDir,
                    clientCurrentDirPathname, item.getItemName());
            //сбрасываем выделение после действия
            listView.getSelectionModel().clearSelection();
        });
        return menuItemDownload;
    }

    /**
     * Метод инициирует элемент контекстного меню "Переименовать"
     * @param listView - текущий список файловых объектов
     * @return - объект элемента контекстного меню "Rename"
     */
    private MenuItem menuItemRename(ListView<Item> listView) {
        //инициируем пункт контекстного меню "Переименовать"
        MenuItem menuItemRename = new MenuItem("Rename");
        //устанавливаем обработчика нажатия на этот пункт контекстного меню
        menuItemRename.setOnAction(event -> {
            //запоминаем выбранный элемент списка
            Item origin = listView.getSelectionModel().getSelectedItem();
            //открываем диалоговое окно переименования файлового объекта
            String newName = takeNewNameWindow(origin);
            //если текущий список клиентский
            if(listView.equals(clientItemListView)){
                //переименовываем файловый объект
//                origin.renameTo(new File(Paths.get(origin.getParentPathname(),
//                        newName).toString()));
                new File(origin.getItemPathname()).renameTo(new File(Paths.get(origin.getParentPathname(),
                        newName).toString()));

                //обновляем список объектов элемента в текущей директории
                updateClientItemListInGUI(clientCurrentDirItem);
                //если текущий список облачного хранилища
            } else if(listView.equals(storageItemListView)){
                //отправляем на сервер запрос на переименования файлового объекта в заданной директории
                //пользователя в сетевом хранилище
                storageClient.demandRenameItem(currentStorageDir, origin.getItemName(), newName);
            }
            //сбрасываем выделение после действия
            listView.getSelectionModel().clearSelection();
        });
        return menuItemRename;
    }

    /**
     * Метод открывает модальное окно для ввода нового имени элемента списка.
     * @param origin - файловый объект - оригинал
     * @return - текстовую строку с новым имем
     */
    private String takeNewNameWindow(Item origin) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rename.fxml"));
            Parent root = loader.load();
            RenameController renameController = loader.getController();

            //FIXME добавить проверку релевантности имени - не должно быть пустой, пробела, и т.п.
            //записываем текущее имя в текстовое поле
            renameController.newName.setText(origin.getItemName());
            renameController.backController = this;

            stage.setTitle("insert a new name");
            stage.setScene(new Scene(root, 200, 50));
            stage.isAlwaysOnTop();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newName;
    }

    /**
     * Метод инициирует элемент контекстного меню "Удалить"
     * @param listView - текущий список файловых объектов
     * @return - объект элемента контекстного меню "Delete"
     */
    private MenuItem menuItemDelete(ListView<Item> listView) {
        //инициируем пункт контекстного меню "Удалить"
        MenuItem menuItemDelete = new MenuItem("Delete");
        //устанавливаем обработчика нажатия на этот пункт контекстного меню
        menuItemDelete.setOnAction(event -> {

            System.out.println("GUIController.callContextMenu().menuItemDelete.setOnAction() - " +
                    "\nlistView.getSelectionModel().getSelectedItem(): " +
                    listView.getSelectionModel().getSelectedItem());

            //TODO добавить диалоговое окно - предупреждение-подтверждение

            //запоминаем выбранный элемент списка
            Item item = listView.getSelectionModel().getSelectedItem();
            //если текущий список клиентский
            if(listView.equals(clientItemListView)){
                //удаляем файл или папку в текущей директории на клиенте
//                storageClient.deleteItem(item);
                storageClient.deleteItem(new File(realClientItemPathname(item.getItemPathname())));

                //обновляем список элементов списка клиентской части
                updateClientItemListInGUI(clientCurrentDirItem);
            //если текущий список облачного хранилища
            } else if(listView.equals(storageItemListView)){
                //отправляем на сервер запрос на получение списка элементов заданной директории
                //пользователя в сетевом хранилище
                storageClient.demandDeleteItem(currentStorageDir, item.getItemName());
            }
            //сбрасываем выделение после действия
            listView.getSelectionModel().clearSelection();
        });
        return menuItemDelete;
    }

    /**
     * Метод отрабатывает нажатие на кнопку Home в клиентской части GUI.
     * Выводит список файловых объектов в корневой директории в клиентской части
     * @param mouseEvent - любой клик мышкой
     */
    @FXML
    public void onClientHomeBtnClicked(MouseEvent mouseEvent) {
        updateClientItemListInGUI(clientDefaultDirItem);

    }

    /**
     * Метод отрабатывает нажатие на кнопку Home в серверной части GUI.
     * Выводит список файловых объектов в корневой директории в серверной части
     * @param mouseEvent - любой клик мышкой
     */
    @FXML
    public void onStorageHomeBtnClicked(MouseEvent mouseEvent) {
        storageClient.demandDirectoryItemList(STORAGE_DEFAULT_DIR);
    }

    /**
     * Метод отрабатывает нажатие на кнопку GoUp в клиентской части GUI.
     * Выводит список объектов элемента в родительской директории в клиентской части
     * @param mouseEvent - любой клик мышкой
     */
    @FXML
    public void onClientGoUpBtnClicked(MouseEvent mouseEvent) {
        //выводим список в родительской директории
        updateClientItemListInGUI(getParentDirItem(clientCurrentDirItem, clientDefaultDirItem,
                CloudStorageClient.CLIENT_ROOT_PATH));
    }

    /**
     * Метод отрабатывает нажатие на кнопку GoUp в серверной части GUI.
     * Выводит список файловых объектов  в родительской директории в клиентской части
     * @param mouseEvent - любой клик мышкой
     */
    @FXML
    public void onStorageGoUpBtnClicked(MouseEvent mouseEvent) {
        //FIXME заменить FILE на Item
        storageClient.demandDirectoryItemList(new File(currentStorageDir).getParent());
    }

    /** //FIXME переделать на универсальный root
     * Метод возвращает массив объектов элементов в заданной директории.
     * @param directoryItem - объект элемента заданной директории
     * @return - массив объектов элементов в заданной директории
     */
    private Item[] clientItemsList(Item directoryItem) {
        //инициируем временный файловый объект заданной директории
        File dirFileObject = new File(realClientDirectory(directoryItem.getItemPathname()));
        //инициируем и получаем массив файловых объектов заданной директории
        File[] files = dirFileObject.listFiles();
        assert files != null;
        //инициируем массив объектов элементов в заданной директории
        Item[] items = new Item[files.length];
        for (int i = 0; i < files.length; i++) {
            //инициируем переменную имени элемента
            String itemName = files[i].getName();
            //инициируем строковыю переменную пути к элементу относительно директории по умолчанию
            String itemPathname = getItemPathname(itemName, directoryItem.getItemPathname(),
                    CloudStorageClient.CLIENT_ROOT);//FIXME переделать на универсальный
            //инициируем объект элемента в заданной директории
            items[i] = new Item(itemName, directoryItem.getItemName(), itemPathname,
                    directoryItem.getItemPathname(), files[i].isDirectory());
        }
        return items;
    }

    ////FIXME переделать на универсальный
    private String getItemPathname(String itemName, String currentDirPathname,
                                   String rootPathname) {
        Path rootPath = Paths.get(rootPathname);

        Path relativePath = rootPath.relativize(Paths.get(realClientDirectory(currentDirPathname), itemName));


        return relativePath.toString();
    }

    /**
     * Метод возвращает объект элемента родительской директории объекта элемента текущей директории.
     * @param directoryItem - объект элемента текущей директории
     * @param defaultDirItem - объект элемента директории по умолчанию(начальной)
     * @param rootPath - объект пути к реальной корневой директории
     * @return - объект элемента родительской директории объекта элемента текущей директории
     */
    private Item getParentDirItem(Item directoryItem, Item defaultDirItem, Path rootPath) {
        //если текущая и родительская директория являются директориями по умолчанию
        if(directoryItem.isDefaultDirectory() ||
                directoryItem.getParentName().equals(defaultDirItem.getItemName())){
            //возвращаем объект элемента директории по умолчанию(начальной)
            return defaultDirItem;
        } else {
            //инициируем объект пути к родительской директории
            Path parentPath = getParentPath(directoryItem.getParentPathname(),
                    rootPath);
            //получаем имя родительской директории
            String parentName = parentPath.getFileName().toString();
            return new Item(directoryItem.getParentName(), parentName,
                    directoryItem.getParentPathname(), parentPath.toString(), true);
        }
    }

    /**
     * Метод возвращает объект пути к родителю элемента списка.
     * @param itemPathname - строка пути к элементу
     * @param rootPath - объект пути к реальной корневой директории
     * @return - объект пути к родителю элемента списка
     */
    private Path getParentPath(String itemPathname, Path rootPath) {
        //инициируем объект реального пути к родительской директории
        Path parentPath = Paths.get(realClientDirectory(itemPathname)).getParent();
        //возвращаем путь к родительской папке относительно директории по умолчанию
        return rootPath.relativize(parentPath);
    }

    /** //FIXME убрать дублирование с realClientItemPathname и может заменить на Path?
     * Метод возвращает строку реального пути к
     * @param currentDirPathname -
     * @return -
     */
    public String realClientDirectory(String currentDirPathname){
        //собираем путь к текущей папке(к директории по умолчанию) для получения списка объектов
        return Paths.get(CloudStorageClient.CLIENT_ROOT, currentDirPathname).toString();
    }

    //FIXME убрать дублирование с realClientItemPathname и может заменить на Path?
    private String realClientItemPathname(String itemPathname) {
        return Paths.get(CloudStorageClient.CLIENT_ROOT, itemPathname).toString();
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    //Метод отправки запроса об отключении на сервер
    public void dispose() {
        System.out.println("Отправляем сообщение о закрытии");
//        try {
//            //проверяем подключен ли клиент
//            if (out != null && !socket.isClosed()) {
//                out.writeUTF("/end");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
