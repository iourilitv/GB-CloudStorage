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
import utils.Item;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The client class for operating with directoryMessages.
 */
public class GUIController implements Initializable {
    @FXML
    Button AuthorisationButton;//TODO надо ли?

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
    Label label;

    //объявляем объект контроллера клиента облачного хранилища
    private CloudStorageClient storageClient;
    //объявляем переменные логина и пароля пользователя
    private String login;
    private String password;
    //инициируем константу строки названия директории по умолчанию относительно корневой директории
    // для списка в клиентской части GUI
    private final String CLIENT_DEFAULT_DIR = "";
    //инициируем константу строки названия корневой директории для списка в серверной части GUI
    private final String STORAGE_DEFAULT_DIR = "";
    //объявляем объекты директории по умолчанию в клиентской и серверной части GUI
    private Item clientDefaultDirItem, storageDefaultDirItem;
    //объявляем объекты текущей папки списка объектов элемента в клиентской и серверной части GUI
    private Item clientCurrentDirItem, storageCurrentDirItem;
    //объявляем переменную введенного нового имени объекта элемента
    private String newName = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //инициируем объект клиента облачного хранилища
        storageClient = new CloudStorageClient(GUIController.this);
        //инициируем объекты директории по умолчанию в клиентской и серверной части GUI
        clientDefaultDirItem = new Item(CLIENT_DEFAULT_DIR);
        storageDefaultDirItem = new Item(STORAGE_DEFAULT_DIR);

//        //открываем окно авторизации
//        openAuthWindow();

        //инициируем в клиентской части интерфейса список объектов в директории по умолчанию
        initializeClientItemListView();
        //инициируем в серверной части интерфейса список объектов в директории по умолчанию
        initializeStorageItemListView();
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
     * Метод открывает модальное окно для ввода логина и пароля пользователя.
     */
    public void openAuthWindow() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.backController = this;

            stage.setTitle("Authorisation to the Cloud Storage by LYS");
            stage.setScene(new Scene(root, 300, 200));
            stage.isAlwaysOnTop();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            //определяем действия по событию закрыть окно по крестику через лямбда
            //TODO не вызывается закрытии окна
            stage.setOnCloseRequest(event -> {
                System.out.println("stage.setOnCloseRequest...");
                loginController.dispose();
//                label.getScene().getWindow().hide();
            });
            //TODO не вызывается закрытии окна
            stage.setOnHidden(event -> {
                System.out.println("stage.setOnHidden...");
                loginController.dispose();
//                label.getScene().getWindow().hide();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод запускает процесс показа основного окна и процесс авторизации в сетевом хранилище.
     */
    public void startAuthorisation() {
//        //инициируем в клиентской части интерфейса список объектов в директории по умолчанию
//        initializeClientItemListView();
//        //инициируем в серверной части интерфейса список объектов в директории по умолчанию
//        initializeStorageItemListView();
//        //в отдельном потоке
//        new Thread(() -> {
//            try {
//                //запускаем логику клиента облачного хранилища
//                storageClient.run();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();

        storageClient.startAuthorization();
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
    public void initializeStorageItemListView() {
        //выводим в клиентской части интерфейса список объектов в директории по умолчанию
        updateStorageItemListInGUI(new Item(STORAGE_DEFAULT_DIR),
                new Item[]{new Item("waiting for an item list from the server...",
                        "", "waiting for an item list from the server...",
                        "", false)});
//        //в отдельном потоке
//        new Thread(() -> {
//            try {
//                //запускаем логику клиента облачного хранилища
//                storageClient.run();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
    }

    /**
     * Метод обновляет список элементов списка в заданной директории клиентской части
     * @param directoryItem - объект элемента заданной директории
     */
    public void updateClientItemListInGUI(Item directoryItem) {
        //обновляем объект текущей директории
        clientCurrentDirItem = directoryItem;
        //в отдельном потоке запускаем обновление интерфейса
        Platform.runLater(() -> {
            //очищаем метку уведомлений
            label.setText("");
            //записываем в метку относительный строковый путь текущей директории
            clientDirLabel.setText(">>" + clientCurrentDirItem.getItemPathname());
            //обновляем заданный список объектов элемента
            updateListView(clientItemListView, storageClient.clientItemsList(clientCurrentDirItem));
        });
    }

    /**
     * Метод выводит в GUI список объектов(файлов и папок)
     * в корневой пользовательской директории в сетевом хранилище.
     * @param directoryItem - объект полученной пользовательской директории в сетевом хранилище
     * @param items - массив объектов элементов в директории
     */
    public void updateStorageItemListInGUI(Item directoryItem, Item[] items){
        //обновляем объект текущей директории
        storageCurrentDirItem = directoryItem;
        //в отдельном потоке запускаем обновление интерфейса
        Platform.runLater(() -> {
            //очищаем метку уведомлений
            label.setText("");
            //выводим текущую директорию в метку серверной части
            storageDirLabel.setText(">>" + storageCurrentDirItem.getItemPathname());
            //обновляем заданный список файловых объектов
            updateListView(storageItemListView, items);
        });
    }

    /**
     * Метод обновляет заданный список объектов элемента.
     * @param listView - коллекция объектов элемента
     * @param items - массив объектов элемента
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
     * Метод инициирует контекстное меню для переданной в параметре коллекции объектов элемента.
     * @param listView - коллекция объектов элемента
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
     * Метод инициирует элемент контекстного меню "Get into".
     * Запрашивает список объектов для выбранной директории.
     * @param listView - текущий список объектов элемента
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
                storageClient.demandDirectoryItemList(item.getItemPathname());
            }
            //сбрасываем выделение после действия
            listView.getSelectionModel().clearSelection();
        });
        return menuItemGetInto;
    }

    /**
     * Метод инициирует элемент контекстного меню "Загрузить в облачное хранилище"
     * @param listView - текущий список объектов элемента
     * @return - объект элемента контекстного меню "Upload"
     */
    private MenuItem menuItemUpload(ListView<Item> listView) {
        //инициируем пункт контекстного меню "Загрузить в облачное хранилище"
        MenuItem menuItemUpload = new MenuItem("Upload");
        //устанавливаем обработчика нажатия на этот пункт контекстного меню
        menuItemUpload.setOnAction(event -> {
            //запоминаем кликнутый элемент списка
            Item item = listView.getSelectionModel().getSelectedItem();
            try {
                //отправляем на сервер запрос на загрузку файла в облачное хранилище
                storageClient.demandUploadItem(storageCurrentDirItem, item);
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
     * @param listView - текущий список объектов элемента
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
            storageClient.demandDownloadItem(storageCurrentDirItem, clientCurrentDirItem, item);
            //сбрасываем выделение после действия
            listView.getSelectionModel().clearSelection();
        });
        return menuItemDownload;
    }

    /**
     * Метод инициирует элемент контекстного меню "Переименовать"
     * @param listView - текущий список объектов элемента
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
            takeNewNameWindow(origin);
            //если имя пришло пустое(при закрытии окна), то выходим без действий
            if(newName.isEmpty()){
                System.out.println("GUIController.menuItemRename() - the newName var is empty!");
                return;
            }
            //если текущий список клиентский
            if(listView.equals(clientItemListView)){
                //переименовываем файловый объект
                //если произошла ошибка при переименовании
                if(!storageClient.renameClientItem(origin, newName)){
                    //TODO добавить диалоговое окно - предупреждение об ошибке
                    System.out.println("GUIController.menuItemRename() - Some thing wrong with item renaming!");
                }
                //обновляем список объектов элемента в текущей директории
                updateClientItemListInGUI(clientCurrentDirItem);
                //если текущий список облачного хранилища
            } else if(listView.equals(storageItemListView)){
                //отправляем на сервер запрос на переименования объекта в заданной директории
                //пользователя в сетевом хранилище
                storageClient.demandRenameItem(storageCurrentDirItem, origin, newName);
            }
            //сбрасываем выделение после действия
            listView.getSelectionModel().clearSelection();
            //очищаем переменную имени
            newName = "";
        });
        return menuItemRename;
    }

    /**
     * Метод открывает модальное окно для ввода нового имени элемента списка.
     * @param origin - объект элемента - оригинал
     */
    private void takeNewNameWindow(Item origin) {
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
    }

    /**
     * Метод инициирует элемент контекстного меню "Удалить"
     * @param listView - текущий список объектов элемента
     * @return - объект элемента контекстного меню "Delete"
     */
    private MenuItem menuItemDelete(ListView<Item> listView) {
        //инициируем пункт контекстного меню "Удалить"
        MenuItem menuItemDelete = new MenuItem("Delete");
        //устанавливаем обработчика нажатия на этот пункт контекстного меню
        menuItemDelete.setOnAction(event -> {
            //TODO добавить диалоговое окно - предупреждение-подтверждение

            //запоминаем выбранный элемент списка
            Item item = listView.getSelectionModel().getSelectedItem();
            //если текущий список клиентский
            if(listView.equals(clientItemListView)){
                //удаляем файл или папку в текущей директории на клиенте
                //если произошла ошибка при удалении
                if(!storageClient.deleteClientItem(item)){
                    //TODO добавить диалоговое окно - предупреждение об ошибке
                    System.out.println("GUIController.menuItemRename() - Some thing wrong with item deleting!");
                }
                //обновляем список элементов списка клиентской части
                updateClientItemListInGUI(clientCurrentDirItem);
            //если текущий список облачного хранилища
            } else if(listView.equals(storageItemListView)){
                //отправляем на сервер запрос на удаление объекта в заданной директории
                //пользователя в сетевом хранилище
                storageClient.demandDeleteItem(storageCurrentDirItem, item);
            }
            //сбрасываем выделение после действия
            listView.getSelectionModel().clearSelection();
        });
        return menuItemDelete;
    }

    /**
     * Метод отрабатывает нажатие на кнопку "Home" в клиентской части GUI.
     * Выводит список объектов элемента в корневой директории в клиентской части
     * @param mouseEvent - любой клик мышкой
     */
    @FXML
    public void onClientHomeBtnClicked(MouseEvent mouseEvent) {
        updateClientItemListInGUI(clientDefaultDirItem);
    }

    /**
     * Метод отрабатывает нажатие на кнопку "Home" в серверной части GUI.
     * Запрашивает у сервера список объектов элемента в корневой директории в серверной части
     * @param mouseEvent - любой клик мышкой
     */
    @FXML
    public void onStorageHomeBtnClicked(MouseEvent mouseEvent) {
        storageClient.demandDirectoryItemList(STORAGE_DEFAULT_DIR);
    }

    /**
     * Метод отрабатывает нажатие на кнопку "GoUp" в клиентской части GUI.
     * Выводит список объектов элемента в родительской директории в клиентской части
     * @param mouseEvent - любой клик мышкой
     */
    @FXML
    public void onClientGoUpBtnClicked(MouseEvent mouseEvent) {
        //выводим список в родительской директории
        updateClientItemListInGUI(storageClient.getParentDirItem(
                clientCurrentDirItem, clientDefaultDirItem,
                CloudStorageClient.CLIENT_ROOT_PATH));
    }

    /**
     * Метод отрабатывает нажатие на кнопку "GoUp" в серверной части GUI.
     * Запрашивает у сервера список объектов элемента в родительской директории в клиентской части
     * @param mouseEvent - любой клик мышкой
     */
    @FXML
    public void onStorageGoUpBtnClicked(MouseEvent mouseEvent) {
        storageClient.demandDirectoryItemList(storageCurrentDirItem.getParentPathname());
    }

    public String getLabelText() {
        return label.getText();
    }

    public void setLabelText(String text) {
        label.setText(text);
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setLogin(String text) {
        login = text;
    }

    public void setPassword(String text) {
        password = text;
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
