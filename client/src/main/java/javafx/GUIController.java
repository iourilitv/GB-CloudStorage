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

//    //объявляем объекты коллекций файловых объектов
//    @FXML
//    ListView<File> clientItemListView, storageItemListView;
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
    private String clientCurrentDirPathname;

    //получаем текущую папку списка файловых объектов в серверной части GUI
    private String currentStorageDir;

    private Item clientDefaultDirItem;

    //получаем объект текущей папки списка файловых объектов в клиентской части GUI
    private Item clientCurrentDirItem;
//    //получаем текущую папку списка файловых объектов в серверной части GUI
//    private String currentStorageDir;

    //объявляем переменную введенного нового имени
    private String newName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //инициируем объект клиента облачного хранилища
        storageClient = new CloudStorageClient(GUIController.this);

//        //получаем текущую папку списка файловых объектов в клиентской части GUI
//        currentClientDir = storageClient.getClientDefaultDirectory();//""
//        //получаем текущую папку списка файловых объектов в серверной части GUI
//        currentStorageDir = storageClient.getStorageDefaultDirectory();//""
        //получаем текущую папку списка файловых объектов в клиентской части GUI
        clientCurrentDirPathname = CLIENT_DEFAULT_DIR;

        clientDefaultDirItem = new Item(CLIENT_DEFAULT_DIR);

        //получаем текущую папку списка файловых объектов в серверной части GUI
        currentStorageDir = STORAGE_DEFAULT_DIR;

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
//        updateClientItemListInGUI(clientDefaultDirectory());
//        updateClientItemListInGUI(CLIENT_DEFAULT_DIR);

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

//    /**
//     * Метод обновляет список элементов списка в заданной директории клиентской части
//     * @param directoryPathname - заданная директория относительно корневой
//     */
//    public void updateClientItemListInGUI(String directoryPathname) {
//        //обновляем текущую директорию
//        clientCurrentDirPathname = directoryPathname;
//        //в отдельном потоке запускаем обновление интерфейса
//        Platform.runLater(() -> {
//            //записываем в метку текущую директорию
//            clientDirLabel.setText("../" + clientCurrentDirPathname);
//            //обновляем заданный список файловых объектов
////            updateListView(clientItemListView, clientFilesList(currentClientDir));
//            updateListView(clientItemListView, clientItemsList(clientCurrentDirPathname));
//
//        });
//    }
    public void updateClientItemListInGUI(Item directoryItem) {
        //обновляем текущую директорию
        clientCurrentDirItem = directoryItem;
        //в отдельном потоке запускаем обновление интерфейса
        Platform.runLater(() -> {
            //записываем в метку текущую директорию
            clientDirLabel.setText("../" + clientCurrentDirItem.getItemPathname());
            //обновляем заданный список файловых объектов
//            updateListView(clientItemListView, clientFilesList(currentClientDir));
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
    /**
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

//    /**
//     * Метод обновляет заданный список файловых объектов.
//     * @param listView - коллекция файловых объектов
//     * @param fileObjs - массив файловых объектов
//     */
//    private void updateListView(ListView<File> listView, File[] fileObjs) {
//        //очищаем список элементов
//        listView.getItems().clear();
//        //обновляем список элементов списка
//        listView.getItems().addAll(fileObjs);
//        //инициируем объект кастомизированного элемента списка
//        listView.setCellFactory(itemListView -> new FileListCell());
//        //инициируем контекстное меню
//        setContextMenu(listView);
//    }
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
                //обновляем список элементов списка клиентской части
//                updateClientItemListInGUI(item.getItemPathname());
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

                //обновляем список файловых объектов в текущей директории
//                updateClientItemListInGUI(clientCurrentDirPathname);
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
//                updateClientItemListInGUI(clientCurrentDirPathname);
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
//        updateClientItemListInGUI(CLIENT_DEFAULT_DIR);
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
     * Выводит список файловых объектов  в родительской директории в клиентской части
     * @param mouseEvent - любой клик мышкой
     */
//    @FXML
//    public void onClientGoUpBtnClicked(MouseEvent mouseEvent) {
//        //если мы уже находимся в корневой директории клиента
//        if(clientCurrentDirPathname.equals(CLIENT_DEFAULT_DIR)){
//            //обновляем список в корневой директории
//            updateClientItemListInGUI(CLIENT_DEFAULT_DIR);
//            //если нет(ниже)
//        } else{
//            //выводим список в родительской директории
//            updateClientItemListInGUI(getDirParentPathname(clientCurrentDirPathname));
//        }
//    }
//    @FXML
//    public void onClientGoUpBtnClicked(MouseEvent mouseEvent) {
//        //если мы уже находимся в корневой директории клиента
//        if(clientCurrentDirPathname.equals(CLIENT_DEFAULT_DIR)){
//            //обновляем список в корневой директории
//            updateClientItemListInGUI(CLIENT_DEFAULT_DIR);
//            //если нет(ниже)
//        } else{
//            //выводим список в родительской директории
//            updateClientItemListInGUI(getDirParentPathname(clientCurrentDirPathname));
//        }
//    }
    @FXML
    public void onClientGoUpBtnClicked(MouseEvent mouseEvent) {
        //выводим список в родительской директории
//        updateClientItemListInGUI(getDirParentPathname(clientCurrentDirItem));
        updateClientItemListInGUI(getDirParentItem(clientCurrentDirItem, clientDefaultDirItem,
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

//    private String clientDefaultDirectory(){
//        return storageClient.getClientDefaultDirectory();
//    }

//    private File[] clientFilesList(String currentDirectory) {
//        return new File(realClientDirectory(currentDirectory)).listFiles();
//    }

//    private Item[] clientItemsList(String currentDirPathname) {
//
//        File dirFileObject = new File(realClientDirectory(currentDirPathname));
//        String parentName, parentPathname;
//        if(currentDirPathname.equals(CLIENT_DEFAULT_DIR)){
//            parentName = CLIENT_DEFAULT_DIR;
//            parentPathname = CLIENT_DEFAULT_DIR;
//
//        } else{
//
//            parentName = dirFileObject.getName();
////            parentPathname = getPathname(dirFileObject.getName(),
////                    getParent(currentDirPathname),
////                    CloudStorageClient.CLIENT_ROOT);
//            parentPathname = relativeDirPathname(dirFileObject.getPath(), CloudStorageClient.CLIENT_ROOT);
//        }
//
//
//        System.out.println("GUIController.clientItemsList() - " +
//                "dirFileObject.getName(): " + dirFileObject.getName() +
//                ", dirFileObject.getPath(): " + dirFileObject.getPath());
//
//        File[] files = dirFileObject.listFiles();
//        assert files != null;
//        Item[] items = new Item[files.length];
//        for (int i = 0; i < files.length; i++) {
//            String itemName = files[i].getName();
//
//            String itemPathname = getPathname(itemName, currentDirPathname,
//                    CloudStorageClient.CLIENT_ROOT);
//
//            items[i] = new Item(itemName, parentName, itemPathname, parentPathname, files[i].isDirectory());
//
//            System.out.println("GUIController.clientItemsList() - " +
//                    "items[" + i + "]: " + items[i].toString());
//
//        }
//        return items;
//    }
    private Item[] clientItemsList(Item directoryItem) {

        File dirFileObject = new File(realClientDirectory(directoryItem.getItemPathname()));
//        String parentName, parentPathname;
//        if(currentDirPathname.equals(CLIENT_DEFAULT_DIR)){
//            parentName = CLIENT_DEFAULT_DIR;
//            parentPathname = CLIENT_DEFAULT_DIR;
//
//        } else{
//
//            parentName = dirFileObject.getName();
////            parentPathname = getPathname(dirFileObject.getName(),
////                    getParent(currentDirPathname),
////                    CloudStorageClient.CLIENT_ROOT);
//            parentPathname = relativeDirPathname(dirFileObject.getPath(), CloudStorageClient.CLIENT_ROOT);
//        }

//        System.out.println("GUIController.clientItemsList() - " +
//                "dirFileObject.getName(): " + dirFileObject.getName() +
//                ", dirFileObject.getPath(): " + dirFileObject.getPath());

        File[] files = dirFileObject.listFiles();
        assert files != null;
        Item[] items = new Item[files.length];
        for (int i = 0; i < files.length; i++) {
            String itemName = files[i].getName();

            String itemPathname = getPathname(itemName, directoryItem.getItemPathname(),
                    CloudStorageClient.CLIENT_ROOT);

            items[i] = new Item(itemName, directoryItem.getItemName(), itemPathname,
                    directoryItem.getItemPathname(), files[i].isDirectory());

            System.out.println("GUIController.clientItemsList() - " +
                    "items[" + i + "]: " + items[i].toString());

        }
        return items;
    }

//    private String getPathname(String itemName, String currentDirPathname,
//                                    String rootPathname, String defaultDirPathname) {
////        return Paths.get(realClientDirectory(currentDirPathname), itemName)
////                .relativize(Paths.get(CloudStorageClient.CLIENT_ROOT)).toString();
//        if(currentDirPathname.equals(defaultDirPathname)){
//            return Paths.get(defaultDirPathname, itemName).toString();
//        } else {
//            return Paths.get(realClientDirectory(currentDirPathname), itemName)
//                    .relativize(Paths.get(rootPathname)).toString();
//        }
//    }
    private String getPathname(String itemName, String currentDirPathname,
                               String rootPathname) {
        Path rootPath = Paths.get(rootPathname);

        Path relativePath = rootPath.relativize(Paths.get(realClientDirectory(currentDirPathname), itemName));

        System.out.println("GUIController.getPathname() - " +
                "relativePath: " + relativePath);

        return relativePath.toString();
    }

    private String relativeDirPathname(String realDirPathname, String rootPathname) {
        Path rootPath = Paths.get(rootPathname);

        Path relativePath = rootPath.relativize(Paths.get(realDirPathname));

        System.out.println("GUIController.getPathname() - " +
                "path: " + relativePath);

        return relativePath.toString();
    }

//    private String getParent(String currentClientDir) {
//        //собираем путь до родительской папки
//        String parent = Paths.get(realClientDirectory(currentClientDir))
//                .relativize(Paths.get(CloudStorageClient.CLIENT_ROOT)).toString();
//        //из-за особенности метода relativize, где ".." - это корневая директория
//        if(parent.equals("..")){
//            parent = STORAGE_DEFAULT_DIR;
//        }
//
//        System.out.println("GUIController.onClientGoUpBtnClicked() - " +
//                "parent: " + parent);
//
//        return parent;
//    }

    private Item getDirParentItem(Item childrenDirItem, Item defaultDirItem, Path rootPath) {
        if(childrenDirItem.isDefaultDirectory() ||
                childrenDirItem.getParentName().equals(defaultDirItem.getItemName())){
            return defaultDirItem;
        } else {

            Path parentPath = getParentPathname(childrenDirItem.getParentPathname(),
                    rootPath);//FIXME
            String parentName = parentPath.getFileName().toString();//FIXME

            System.out.println("GUIController.getDirParentItem() - " +
                    "parentName: " + parentName +
                    ", parentPath.toString(): " + parentPath.toString());

            return new Item(childrenDirItem.getParentName(), parentName,
                    childrenDirItem.getParentPathname(), parentPath.toString(), true);

        }

    }

//    private String getParentName(Path relativePath) {
////        Path rootPath = Paths.get(rootPathname);
////        Path parentPath = Paths.get(realClientDirectory(pathname)).getParent();
//
////        Path relativePath = rootPath.relativize(parentPath);
//        String parentName = relativePath.getFileName().toString();
//
//        System.out.println("GUIController.getParentName() - " +
//                "relativePath: " + relativePath +
//                ", parentName: " + parentName);
//
//        return parentName;
//    }

    private Path getParentPathname(String pathname, Path rootPath) {
//        Path rootPath = Paths.get(rootPathname);
        Path parentPath = Paths.get(realClientDirectory(pathname)).getParent();

        System.out.println("GUIController.getParentPathname() - " +
                "parentPath.toString(): " + parentPath.toString());
        //GUIController.getParentPathname() - parentPath.toString(): storage\client_storage

        Path relativePath = rootPath.relativize(parentPath);

        System.out.println("GUIController.getParentPathname() - " +
                "relativePath.toString(): " + relativePath.toString());
        //GUIController.getParentPathname() - relativePath.toString(): ..\storage\client_storage

//        Path relativePath = rootPath.relativize(Paths.get(realClientDirectory(pathname), itemName));

        return relativePath;
    }

    //    public String realClientDirectory(String currentDirectory){
//        //собираем путь к текущей папке(к директории по умолчанию) для получения списка объектов
//        return Paths.get(CloudStorageClient.CLIENT_ROOT, currentDirectory).toString();
//    }
    public String realClientDirectory(String currentDirPathname){
        //собираем путь к текущей папке(к директории по умолчанию) для получения списка объектов
        return Paths.get(CloudStorageClient.CLIENT_ROOT, currentDirPathname).toString();
    }

    private String realClientItemPathname(String itemPathname) {
        return Paths.get(CloudStorageClient.CLIENT_ROOT, itemPathname).toString();
    }

    public String getNewName() {
        return newName;
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
