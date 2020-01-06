package javafx;

import control.CloudStorageClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * The client class for operating with directoryMessages.
 */
public class GUIController implements Initializable {

    @FXML
    Label clientDirLabel, storageDirLabel;

    @FXML
    ListView<File> clientItemListView, storageItemListView;

    @FXML
    Label label;

    //объявляем объект контроллера клиента облачного хранилища
    private CloudStorageClient storageClient;
    //получаем текущую папку списка файловых объектов в клиентской части GUI
    private String currentClientDir;
    //получаем текущую папку списка файловых объектов в серверной части GUI
    private String currentStorageDir;
    //объявляем переменную контекстного меню
    private ContextMenu contextMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //инициируем объект клиента облачного хранилища
        storageClient = new CloudStorageClient(GUIController.this);
        //получаем текущую папку списка файловых объектов в клиентской части GUI
        currentClientDir = storageClient.getClientDefaultDirectory();
        //получаем текущую папку списка файловых объектов в серверной части GUI
        currentStorageDir = storageClient.getStorageDefaultDirectory();
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
        updateClientItemListInGUI(clientDefaultDirectory(), clientFilesList(clientDefaultDirectory()));
    }

    /**
     * Метод инициирует в серверной части интерфейса список объектов в директории по умолчанию
     */
    public void initializeStorageItemListView() {
        //выводим в клиентской части интерфейса список объектов в директории по умолчанию
        updateStorageItemListInGUI("../",
                new File[]{new File("waiting for an item list from the server...")});
        //в отдельном потоке
        new Thread(() -> {
            try {
                //запускаем логику клиента облачного хранилища
                storageClient.run();//TODO
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML //TODO delete?
    public void btnClickSelectedClientFile(ActionEvent actionEvent) {
        label.setText(clientItemListView.getSelectionModel().getSelectedItem().getName());
    }

    @FXML //TODO delete?
    public void btnClickSelectedServerFile(ActionEvent actionEvent) {
        label.setText(storageItemListView.getSelectionModel().getSelectedItem().getName());
    }

    /**
     * Метод обновляет список элементов списка в клиентской части
     * @param directory - заданная директория
     * @param fileObjs - массив объектов класса File(файлы и директории)
     */
    public void updateClientItemListInGUI(String directory, File[] fileObjs){
        //обновляем текущую директорию
        currentClientDir = directory;
        //в отдельном потоке запускаем обновление интерфейса
        Platform.runLater(() -> {
            //записываем в метку текущую директорию
            clientDirLabel.setText(currentClientDir);

//            //очищаем список элементов
//            clientItemListView.getItems().clear();
//            //обновляем список элементов списка
//            clientItemListView.getItems().addAll(fileObjs);
//            //инициируем объект кастомизированного элемента списка
//            clientItemListView.setCellFactory(itemListView -> new FileListCell());
//            //инициируем контекстное меню
//            setContextMenu(clientItemListView);

            //обновляем заданный список файловых объектов
            updateListView(clientItemListView, fileObjs);
        });
    }

    /**
     * Метод выводит в GUI список файлов и папок в корневой пользовательской директории
     * в сетевом хранилище.
     * @param directory - заданная пользовательская директория в сетевом хранилище
     * @param fileObjs - массив объектов класса File(файлы и директории)
     */
    public void updateStorageItemListInGUI(String directory, File[] fileObjs){
        //обновляем текущую директорию
        currentStorageDir = directory;

        Platform.runLater(() -> {
            //выводим текущую директорию в метку серверной части
            storageDirLabel.setText(currentStorageDir);

//            //очищаем список элементов
//            storageItemListView.getItems().clear();
//            //обновляем список элементов списка
//            storageItemListView.getItems().addAll(fileObjs);
//            storageItemListView.setCellFactory(itemListView -> new FileListCell());
//            //инициируем контекстное меню
//            setContextMenu(storageItemListView);

            //обновляем заданный список файловых объектов
            updateListView(storageItemListView, fileObjs);
        });
    }

    /**
     * Метод обрабатывает событие клика мыши на элементе списка клиентской части
     * @param mouseEvent - событие клика мыши на элементе списка
     */
    @FXML
    public void onClickClientListFolderItem(MouseEvent mouseEvent) {
        //запоминаем кликнутый элемент списка
        File item = clientItemListView.getSelectionModel().getSelectedItem();
        //если контекстное меню показывается или кликнутый элемент пустой
        if(contextMenu.isShowing() || item == null){
            //сбрасываем выделение
            clientItemListView.getSelectionModel().clearSelection();
            //закрываем контекстное меню
            contextMenu.hide();
            return;
        }
        //если двойной клик левой кнопкой мыши
        if(mouseEvent.getButton().name().equals("PRIMARY") &&
            mouseEvent.getClickCount() == 2){
            //если кликнутый элемент это директория
            if(item.isDirectory()){
                //обновляем список элементов списка клиентской части
                updateClientItemListInGUI(item.getName(), item.listFiles());
            }
        }
    }

    /**
     * Метод обрабатывает событие клика мыши на элементе списка серверной части
     * @param mouseEvent - событие клика мыши на элементе списка
     */
    @FXML
    public void onClickStorageListFolderItem(MouseEvent mouseEvent) {
        //запоминаем кликнутый элемент списка
        File item = storageItemListView.getSelectionModel().getSelectedItem();
        //если двойной клик левой кнопкой мыши
        if(mouseEvent.getButton().name().equals("PRIMARY") &&
                mouseEvent.getClickCount() == 2){
            //если кликнутый элемент это директория
            if(item.isDirectory()){

                System.out.println("GUIController.onClickClientListFolderItem() - " +
                                ", storageItemListView.getSelectionModel().getSelectedItem().getName(): " +
                                storageItemListView.getSelectionModel().getSelectedItem().getName()
                );
                //отправляем на сервер запрос на получение списка элементов заданной директории
                //пользователя в сетевом хранилище
                storageClient.demandDirectoryItemList(item.getName());
                //выводим в клиентской части интерфейса пустой список с сообщенеем об ожидании ответа
                updateStorageItemListInGUI(item.getName(),
                        new File[]{new File("waiting for an item list from the server...")});
            }
        }

        //если клик правой кнопкой мыши
//        else if(mouseEvent.getButton().name().equals("SECONDARY")){
//            System.out.println("GUIController.btnClickDirectory() - mouseEvent.getEventType(): " +
//                    mouseEvent.getEventType() +
//                    ", mouseEvent.getButton().name(): " + mouseEvent.getButton().name());
//            //FIXME
//            //метод вызова контекстного меню
//        }
    }

    /**
     * Метод обновляет заданный список файловых объектов.
     * @param listView - коллекция файловых объектов
     * @param fileObjs - массив файловых объектов
     */
    private void updateListView(ListView<File> listView, File[] fileObjs) {
        //очищаем список элементов
        listView.getItems().clear();
        //обновляем список элементов списка
        listView.getItems().addAll(fileObjs);
        //инициируем объект кастомизированного элемента списка
        listView.setCellFactory(itemListView -> new FileListCell());
        //инициируем контекстное меню
        setContextMenu(listView);
    }

    /**
     * Метод инициирует контекстное меню для переданной в параметре коллекции файловых объектов.
     * @param listView - коллекция файловых объектов
     */
    private void setContextMenu(ListView<File> listView){
        //инициируем объект контестного меню
        contextMenu = new ContextMenu();
        //если текущий список клиентсткий
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
        //устаналиваем событие на клик правой кнопки мыши по элементу списка
        listView.setOnContextMenuRequested(event -> {
            //если кликнут не пустой элемент
            if(!listView.getSelectionModel().getSelectedItems().isEmpty()) {
                //если выбранный элемент это директория
                if(listView.getSelectionModel().getSelectedItem().isDirectory()){
                    // добавляем элемент в контестное меню
                    contextMenu.getItems().add(0, menuItemGetList(listView));
                }
                //показываем меню в точке клика(позиция левого-верхнего угла контекстного меню)
                contextMenu.show(listView, event.getScreenX(), event.getScreenY());
            }
        });
    }

    /**
     * Метод инициирует элемент контекстного меню "Получить список файловых объектов",
     * только для выбранной директории.
     * @param listView - текущий список файловых объектов
     * @return - объект элемента контекстного меню "GetList"
     */
    private MenuItem menuItemGetList(ListView<File> listView) {
        //инициируем пункт контекстного меню "Получить список файловых объектов"
        MenuItem menuItemGetList = new MenuItem("GetList");
        //устанавливаем обработчика нажатия на этот пункт контекстного меню
        menuItemGetList.setOnAction(event -> {
            //запоминаем кликнутый элемент списка
            File item = listView.getSelectionModel().getSelectedItem();
            //если кликнутый элемент это директория
//                if(item.isDirectory()){

            System.out.println("GUIController.onClickClientListFolderItem() - " +
                    ", listView.getSelectionModel().getSelectedItem().getName(): " +
                    listView.getSelectionModel().getSelectedItem().getName()
            );
            //обновляем список элементов списка клиентской части
            updateClientItemListInGUI(item.getName(), item.listFiles());
//                }
            //сбрасываем выделение после действия
            listView.getSelectionModel().clearSelection();
        });
        return menuItemGetList;
    }

    /**
     * Метод инициирует элемент контекстного меню "Загрузить в облачное хранилище"
     * @param listView - текущий список файловых объектов
     * @return - объект элемента контекстного меню "Upload"
     */
    private MenuItem menuItemUpload(ListView<File> listView) {
        //инициируем пункт контекстного меню "Загрузить в облачное хранилище"
        MenuItem menuItemUpload = new MenuItem("Upload");
        //устанавливаем обработчика нажатия на этот пункт контекстного меню
        menuItemUpload.setOnAction(event -> {
            System.out.println("GUIController.callContextMenu().menuItemUpload.setOnAction() - " +
                    "\nlistView.getSelectionModel().getSelectedItem(): " +
                    listView.getSelectionModel().getSelectedItem());

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
    private MenuItem menuItemDownload(ListView<File> listView) {
        //инициируем пункт контекстного меню "Скачать из облачного хранилища"
        MenuItem menuItemDownload = new MenuItem("Download");
        //устанавливаем обработчика нажатия на этот пункт контекстного меню
        menuItemDownload.setOnAction(event -> {
            System.out.println("GUIController.callContextMenu().menuItemDownload.setOnAction() - " +
                    "\nlistView.getSelectionModel().getSelectedItem(): " +
                    listView.getSelectionModel().getSelectedItem());

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
    private MenuItem menuItemRename(ListView<File> listView) {
        //инициируем пункт контекстного меню "Переименовать"
        MenuItem menuItemRename = new MenuItem("Rename");
        //устанавливаем обработчика нажатия на этот пункт контекстного меню
        menuItemRename.setOnAction(event -> {

            //TODO temporarily
            System.out.println("GUIController.callContextMenu().menuItemRename.setOnAction() - " +
                    "\nlistView.getSelectionModel().getSelectedItem(): " +
                    listView.getSelectionModel().getSelectedItem());

            //запоминаем выбранный элемент списка
            File origin = listView.getSelectionModel().getSelectedItem();

            //TODO добавить диалоговое окно - получить новое имя
            //получаем новое имя файлового объекта
            String newName = "Renamed" + origin.getName();

            //TODO
            System.out.println("GUIController.callContextMenu().menuItemRename.setOnAction() - " +
                    "origin.renameTo()): " +
                    origin.renameTo(new File(Paths.get(origin.getParent(),
                            newName).toString())));

            //сбрасываем выделение после действия
            listView.getSelectionModel().clearSelection();
            //обновляем список файловых объектов в текущей директории
            updateClientItemListInGUI(origin.getParent(), new File(origin.getParent()).listFiles());
        });
        return menuItemRename;
    }

    /**
     * Метод инициирует элемент контекстного меню "Удалить"
     * @param listView - текущий список файловых объектов
     * @return - объект элемента контекстного меню "Delete"
     */
    private MenuItem menuItemDelete(ListView<File> listView) {
        //инициируем пункт контекстного меню "Удалить"
        MenuItem menuItemDelete = new MenuItem("Delete");
        //устанавливаем обработчика нажатия на этот пункт контекстного меню
        menuItemDelete.setOnAction(event -> {
            System.out.println("GUIController.callContextMenu().menuItemDelete.setOnAction() - " +
                    "\nlistView.getSelectionModel().getSelectedItem(): " +
                    listView.getSelectionModel().getSelectedItem());

            //TODO добавить диалоговое окно - предупреждение-подтверждение

            //запоминаем выбранный элемент списка
            File origin = listView.getSelectionModel().getSelectedItem();
            //если это директория
            if(origin.isDirectory()){
                //очищаем и удаляем папку
                storageClient.deleteFolder(origin);
            } else{
                //удаляем файл
                System.out.println("GUIController.callContextMenu().menuItemDelete.setOnAction() - " +
                        "origin.delete(): " + origin.delete());
            }
            //сбрасываем выделение после действия
            listView.getSelectionModel().clearSelection();
            //обновляем список файловых объектов в текущей директории
            updateClientItemListInGUI(origin.getParent(), new File(origin.getParent()).listFiles());
        });
        return menuItemDelete;
    }

    public ListView<File> getClientItemListView() {
        return clientItemListView;
    }

    private String clientDefaultDirectory(){
        return storageClient.getClientDefaultDirectory();
    }

    private File[] clientFilesList(String currentDirectory) {
        return new File(realClientDirectory(currentDirectory)).listFiles();
    }

    private String realClientDirectory(String currentDirectory){
        //собираем путь к текущей папке(к директории по умолчанию) для получения списка объектов
        return Paths.get(CloudStorageClient.CLIENT_ROOT, currentDirectory).toString();
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
