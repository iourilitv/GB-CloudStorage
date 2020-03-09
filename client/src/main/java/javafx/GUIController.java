package javafx;

import control.CloudStorageClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import utils.Item;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The client class is for operations with GUI.
 */
public class GUIController implements Initializable {
    //объявляем объекты пунктов верхнего меню
    @FXML
    private MenuItem disconnectMenuItem, changePasswordMenuItem;

    @FXML
    private StackPane connectToCloudStorageStackPane;

    @FXML
    private Button connectToCloudStorageButton;

    //объявляем объекты кнопок для коллекции файловых объектов клиента и сервера
    @FXML
    private Button clientHomeButton, storageHomeButton,//"В корневую директорию"
            clientGoUpButton, storageGoUpButton,//"Подняться на папку выше"
            clientRefreshButton, storageRefreshButton,//обновить папку
            clientNewFolderButton, storageNewFolderButton;//"Создать новую папку"

    //объявляем объекты меток для коллекций файловых объектов
    @FXML
    private Label clientDirLabel, storageDirLabel;

    //объявляем объекты коллекций объектов элементов
    @FXML
    private ListView<Item> clientItemListView, storageItemListView;

    //объявляем объект метки уведомлений
    @FXML
    private Label noticeLabel;

    //объявляем объект менеджера окон
    private WindowsManager windowsManager;
    //объявляем объект контроллера клиента облачного хранилища
    private CloudStorageClient storageClient;
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
        //инициируем объект менеджера окон
        windowsManager = WindowsManager.getInstance();
        //передаем ему настройки
        windowsManager.init(GUIController.this);
        //инициируем объект клиента облачного хранилища
        storageClient = new CloudStorageClient(GUIController.this);
        //устанавливаем настройки приложения
        storageClient.initConfiguration();
        //инициируем объекты директории по умолчанию в клиентской и серверной части GUI
        clientDefaultDirItem = new Item(CLIENT_DEFAULT_DIR);
        storageDefaultDirItem = new Item(STORAGE_DEFAULT_DIR);
        //выводим текст в метку
        noticeLabel.setText("Server disconnected. Press \"Connect to the Cloud Storage\" button.");
        //инициируем в клиентской части интерфейса список объектов в директории по умолчанию
        initializeClientItemListView();
        //инициируем в серверной части интерфейса список объектов в директории по умолчанию
        initializeStorageItemListView();
    }

    /**
     * Метод обрабатывает нажатие connectToCloudStorageButton и запускает процесс
     * подключения к серверу облачного хранилища.
     */
    @FXML
    private void onConnectToCloudStorageButtonClick() {
        //выводим текст в метку
        noticeLabel.setText("Connecting to the Cloud Storage server, please wait..");
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
     * Метод-прокладка запускает процессы: показа окна авторизации в режиме авторизации
     * и процесс регистрации пользователя в сетевом хранилище.
     * @param login - логин пользователя
     * @param first_name - имя пользователя
     * @param last_name - фамилия пользователя
     * @param email - email пользователя
     * @param password - пароль пользователя
     */
    public void demandRegistration(String login, String first_name, String last_name,
                                   String email, String password) {
        storageClient.demandRegistration(login, first_name, last_name, email, password);
    }

    /**
     * Метод-прокладка запускает процесс показа основного окна и процесс
     * авторизации пользователя в сетевом хранилище.
     * @param login - логин пользователя
     * @param password - пароль пользователя
     */
    public void demandAuthorisation(String login, String password) {
        storageClient.demandAuthorization(login, password);
    }

    /**
     * Метод-прокладка запускает процесс отправки запроса на изменение пароля пользователя
     * в сетевое хранилище.
     * @param login - логин пользователя
     * @param password - текущий пароль пользователя
     * @param newPassword - новый пароль пользователя
     */
    public void demandChangePassword(String login, String password, String newPassword) {
        storageClient.demandChangePassword(login, password, newPassword);
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
                //выводим сообщение в нижнюю метку
                noticeLabel.setText("Uploading a file...");
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
            //выводим сообщение в нижнюю метку
            noticeLabel.setText("File downloading. Waiting for a cloud server response...");
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
            //если окно было просто закрыто по крестику, то выходим без действий
            if(!windowsManager.openNewNameWindow(origin)){
                return;
            }

            //если текущий список клиентский
            if(listView.equals(clientItemListView)){
                //переименовываем файловый объект
                //если произошла ошибка при переименовании
                if(!storageClient.renameClientItem(origin, newName)){
                    //выводим сообщение в метку оповещений
                    noticeLabel.setText("Something wrong with item renaming!");
                    //печатаем в лог сообщение об ошибке
                    writeToLog("GUIController.menuItemRename() - Something wrong with item renaming!");
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
     * Метод инициирует элемент контекстного меню "Удалить"
     * @param listView - текущий список объектов элемента
     * @return - объект элемента контекстного меню "Delete"
     */
    private MenuItem menuItemDelete(ListView<Item> listView) {
        //инициируем пункт контекстного меню "Удалить"
        MenuItem menuItemDelete = new MenuItem("Delete");
        //устанавливаем обработчика нажатия на этот пункт контекстного меню
        menuItemDelete.setOnAction(event -> {
            //TODO Upd 33. добавить диалоговое окно - предупреждение-подтверждение на удаление

            //запоминаем выбранный элемент списка
            Item item = listView.getSelectionModel().getSelectedItem();
            //если текущий список клиентский
            if(listView.equals(clientItemListView)){
                //удаляем файл или папку в текущей директории на клиенте
                //если произошла ошибка при удалении
                if(!storageClient.deleteClientItem(item)){
                    //выводим сообщение в метку оповещений
                    noticeLabel.setText("Something wrong with item deleting!");
                    //печатаем в лог сообщение об ошибке
                    writeToLog("GUIController.menuItemRename() - Something wrong with item deleting!");
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
     * Метод отрабатывает нажатие на пунтк меню "About".
     * @param actionEvent - событие(здесь клик мыши)
     */
    @FXML
    public void onAboutMenuItemClick(ActionEvent actionEvent) {
        //открываем сцену с информацией о программе
        windowsManager.openAboutScene();
    }

    /**
     * Метод отрабатывает нажатие на пунтк меню "Change Client Root".
     * @param actionEvent - событие(здесь клик мыши)
     */
    @FXML
    public void onChangeClientRootMenuItemClick(ActionEvent actionEvent) {
        //открываем окно для ввода строки нового абсолютного пути к корневой директории клиента
        windowsManager.openChangingClientRootWindow();
    }

    /**
     * Метод отрабатывает нажатие на пункт меню "ChangePassword".
     * @param actionEvent - событие(здесь клик мыши)
     */
    @FXML
    public void onChangePasswordMenuItemClick(ActionEvent actionEvent) {
        //запускаем процесс отправки запроса на изменение пароля пользователя
        windowsManager.openChangingPasswordWindow();
    }

    /**
     * Метод отрабатывает нажатие на пункт меню "Disconnect".
     * @param actionEvent - событие(здесь клик мыши)
     */
    @FXML
    public void onDisconnectMenuItemClick(ActionEvent actionEvent) {
        writeToLog("GUIController.onDisconnectLinkClick()");
        //запускаем процесс отправки запроса на отключение
        storageClient.demandDisconnect();
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
     * Запрашивает у сервера список объектов элемента в родительской директории в серверной части
     * @param mouseEvent - любой клик мышкой
     */
    @FXML
    public void onStorageGoUpBtnClicked(MouseEvent mouseEvent) {
        storageClient.demandDirectoryItemList(storageCurrentDirItem.getParentPathname());
    }

    /**
     * Метод отрабатывает нажатие на кнопку "Refresh" в клиентской части GUI.
     * Запрашивает у сервера список объектов элемента в текущей директории в клиентской части
     * @param mouseEvent - любой клик мышкой
     */
    @FXML
    public void onClientRefreshBtnClicked(MouseEvent mouseEvent) {
        //обновляем список объектов элемента клиентской части
        updateClientItemListInGUI(clientCurrentDirItem);
    }

    /**
     * Метод отрабатывает нажатие на кнопку "Refresh" в серверной части GUI.
     * Запрашивает у сервера список объектов элемента в текущей директории в серверной части
     * @param mouseEvent - любой клик мышкой
     */
    @FXML
    public void onStorageRefreshBtnClicked(MouseEvent mouseEvent) {
        //отправляем на сервер запрос на получение списка элементов заданной директории
        //пользователя в сетевом хранилище
        storageClient.demandDirectoryItemList(storageCurrentDirItem.getItemPathname());
    }

    /**
     * Метод отрабатывает нажатие на кнопку "NewFolder" в клиентской части GUI.
     * Запрашивает у сервера создать новую папку в текущей директории в клиентской части
     * @param mouseEvent - любой клик мышкой
     */
    @FXML
    public void onClientNewFolderBtnClicked(MouseEvent mouseEvent) {
        //открываем модальное окно для ввода нового имени
        windowsManager.openNewNameWindow();
        //если окно было закрыто штатно, а не по крестику выхода
        if(newName.isEmpty()){
            return;
        }
        //если новая папка создана удачно
        if(!storageClient.createNewFolder(storageCurrentDirItem.getItemPathname(), newName)){
            noticeLabel.setText("A folder has not created!");
            return;
        }
        //обновляем список объектов в текущей клиентской директории
        updateClientItemListInGUI(clientCurrentDirItem);
    }

    /**
     * Метод отрабатывает нажатие на кнопку "NewFolder" в серверной части GUI.
     * Запрашивает у сервера создать новую папку в текущей директории в серверной части
     * @param mouseEvent - любой клик мышкой
     */
    @FXML
    public void onStorageNewFolderBtnClicked(MouseEvent mouseEvent) {
        //открываем модальное окно для ввода нового имени
        windowsManager.openNewNameWindow();
        //если окно было закрыто штатно, а не по крестику выхода
        if(newName.isEmpty()){
            return;
        }
        //отправляем на сервер запрос на получение списка элементов заданной директории
        //пользователя в сетевом хранилище
        storageClient.demandCreateNewDirectory(storageCurrentDirItem.getItemPathname(), newName);
    }

    /**
     * Метод-прокладка, чтобы открывать окно GUI в других потоках
     */
    public void openAuthWindowInGUI() {
        //в отдельном потоке запускаем обновление интерфейса
        //открываем окно авторизации
        Platform.runLater(() -> windowsManager.openAuthorisationWindow());
    }

    /**
     * Метод устанавливает режим отображения GUI "Отсоединен" или "Подсоединен".
     * @param isDisconnectedMode - если true - "Отсоединен"
     */
    public void setDisconnectedMode(boolean isDisconnectedMode) {
        //показываем и активируем(если isDisconnectedMode = true)
        // панель с кнопкой подключения к серверу
        connectToCloudStorageStackPane.setManaged(isDisconnectedMode);
        connectToCloudStorageStackPane.setVisible(isDisconnectedMode);
        //активируем кнопку connectToCloudStorageButton
        connectToCloudStorageButton.setDisable(!isDisconnectedMode);

        //скрываем и деактивируем (если isDisconnectedMode = true)
        //деактивируем кнопки сетевого хранилища
        storageHomeButton.setDisable(isDisconnectedMode);
        storageGoUpButton.setDisable(isDisconnectedMode);
        storageRefreshButton.setDisable(isDisconnectedMode);
        storageNewFolderButton.setDisable(isDisconnectedMode);
        // список объектов в сетевом хранилище
        storageItemListView.setManaged(!isDisconnectedMode);
        storageItemListView.setVisible(!isDisconnectedMode);

        //деактивируем пункт меню Disconnect
        disconnectMenuItem.setDisable(isDisconnectedMode);
        //деактивируем пункт меню ChangePassword
        changePasswordMenuItem.setDisable(isDisconnectedMode);
    }

    /**
     * Метод устанавливает GUI в режим авторизован или нет, в зависимости от параметра
     * @param isAuthMode - true - сервер авторизовал пользователя
     */
    public void setAuthorizedMode(boolean isAuthMode) {
        //активируем/деактивируем кнопки сетевого хранилища
        storageHomeButton.setDisable(!isAuthMode);
        storageGoUpButton.setDisable(!isAuthMode);
        storageRefreshButton.setDisable(!isAuthMode);
        storageNewFolderButton.setDisable(!isAuthMode);
        //скрываем и деактивируем(если isAuthMode = true) кнопку подключения к серверу
        connectToCloudStorageStackPane.setManaged(!isAuthMode);
        connectToCloudStorageStackPane.setVisible(!isAuthMode);
        //показываем и активируем(если isAuthMode = true) список объектов в сетевом хранилище
        storageItemListView.setManaged(isAuthMode);
        storageItemListView.setVisible(isAuthMode);
        //если авторизация получена
        if(isAuthMode){
            //если объект контроллера регистрации не нулевой
            if(windowsManager.getRegistrationController() != null){
                //закрываем окно формы в потоке JavaFX
                Platform.runLater(() -> windowsManager.getRegistrationController().hideWindow());
            }
            //если объект контроллера авторизации не нулевой
            if(windowsManager.getAuthorisationController() != null){
                //закрываем окно формы в потоке JavaFX
                Platform.runLater(() -> windowsManager.getAuthorisationController().hideWindow());
            }
            //если объект контроллера изменения пароля пользователя не нулевой
            if(windowsManager.getChangePasswordController() != null){
                //закрываем окно формы в потоке JavaFX
                Platform.runLater(() -> windowsManager.getChangePasswordController().hideWindow());
            }
        }
    }

    /**
     * Метод открывает окно регистрации.
     */
    public void setRegistrationFormMode(){
        //если объект контроллера регистрации не нулевой
        if(windowsManager.getAuthorisationController() != null){
            //закрываем окно формы в потоке JavaFX
            Platform.runLater(() -> windowsManager.getAuthorisationController().hideWindow());
        }
        //открываем окно регистрации с пустыми полями
        windowsManager.openRegistrationWindow();
    }

    /**
     * Метод открывает окно авторизации.
     */
    public void setAuthorizationFormMode() {
        //если объект контроллера регистрации не нулевой
        if(windowsManager.getRegistrationController() != null){
            //закрываем окно формы в потоке JavaFX
            Platform.runLater(() -> windowsManager.getRegistrationController().hideWindow());
        }
        //открываем окно авторизации с пустыми логином и паролем
        windowsManager.openAuthorisationWindow();
    }

    /**
     * Метод устанавливает режим "Зарегистрирован, но не авторизован" - скрывает
     * окно регистрации и открывает окно авторизации с логином и паролем,
     * сохраненными из регистрационной формы.
     */
    public void setRegisteredAndUnauthorisedMode() {
        //инициируем переменные межпотоковые переменные для логин аи пароля
        AtomicReference<String>  loginAtomic = new AtomicReference<>();
        AtomicReference<String>  passwordAtomic = new AtomicReference<>();
        //если объект контроллера регистрации не нулевой
        if(windowsManager.getRegistrationController() != null){
            //закрываем окно формы в потоке JavaFX
            Platform.runLater(() -> {
                //сохраняем логин и пароль из регистрационной формы
                assert false;
                loginAtomic.set(windowsManager.getRegistrationController().getLoginString());
                passwordAtomic.set(windowsManager.getRegistrationController().getPasswordString());
                //закрываем окно регистрации
                windowsManager.getRegistrationController().hideWindow();
            });
        }
        //делаем паузу мин. 100 ms, чтобы процесс успел завершиться до закрытия окна
        //без паузы мин 100 ms - значение loginAtomic.get() = null
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //открываем окно формы в потоке JavaFX //WORKS!
        Platform.runLater(() -> windowsManager.openAuthorisationWindow(loginAtomic.get(), passwordAtomic.get()));
    }

    /**
     * Метод выводит в отдельном потоке(не javaFX) переданное сообщение в метку уведомлений.
     * @param text - строка сообщения
     */
    public void showTextInGUI(String text){
        //в отдельном потоке запускаем обновление интерфейса
        Platform.runLater(() -> {
            //выводим сообщение в нижнюю метку GUI
            noticeLabel.setText(text);
        });
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    /**
     * Метод записывает новое значение абсолютного пути к корневой папке клиента.
     * @param newPathname - новое значение абсолютного пути
     */
    public void setNewRootPathname(String newPathname) {
        //если введенное имя пути пустое, то выходим
        if(newPathname.trim().isEmpty()){
            return;
        }
        Path newPath = Paths.get(newPathname);
        //если новая директория существует и это действительно директория
        if(newPath.toFile().exists() && newPath.toFile().isDirectory()){
            //записываем новое значение в переменную корневой директории
            CloudStorageClient.CLIENT_ROOT_PATH = newPath;
            //сохраняем новое значение абсолютного пути
            storageClient.saveClientRootPathProperty(newPath.toString());
            //устанавливаем текущей директорией директорию по умолчанию
            updateClientItemListInGUI(clientDefaultDirItem);
            //устанавливаем текст в метку уведомлений
            noticeLabel.setText("The Client Root path has been changed!");
        } else {
            noticeLabel.setText("Something wrong with a new Client Root path!");
        }
    }

    public Label getNoticeLabel() {
        return noticeLabel;
    }

    public CloudStorageClient getStorageClient() {
        return storageClient;
    }

    //Метод отправки запроса об отключении на сервер
    public void dispose() {
        //запускаем процесс отправки запроса серверу на разрыв соединения
        storageClient.demandDisconnect();
        //делаем паузу 2 сек, чтобы процесс успел завершиться до закрытия окна
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void writeToLog(String msg){
        storageClient.writeToLog(msg);
    }

}
