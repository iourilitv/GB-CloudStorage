package javafx;

import control.CloudStorageClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
    ListView<File> clientItemListView, storageItemListView;
    @FXML
    Label label;//TODO Зачем?
    //объявляем объект контроллера клиента облачного хранилища
    private CloudStorageClient storageClient;
    //получаем текущую папку списка файловых объектов в клиентской части GUI
    private String currentClientDir;
    //получаем текущую папку списка файловых объектов в серверной части GUI
    private String currentStorageDir;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //инициируем объект клиента облачного хранилища
        storageClient = new CloudStorageClient(GUIController.this);
        //получаем текущую папку списка файловых объектов в клиентской части GUI
        currentClientDir = storageClient.getClientDefaultDirectory();//""
        //получаем текущую папку списка файловых объектов в серверной части GUI
        currentStorageDir = storageClient.getStorageDefaultDirectory();//""
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
        updateClientItemListInGUI(clientDefaultDirectory());

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
                storageClient.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Метод обновляет список элементов списка в заданной директории клиентской части
     * @param directory - заданная директория относительно корневой
     */
    public void updateClientItemListInGUI(String directory) {
        //обновляем текущую директорию
        currentClientDir = directory;
        //в отдельном потоке запускаем обновление интерфейса
        Platform.runLater(() -> {
            //записываем в метку текущую директорию
            clientDirLabel.setText(currentClientDir);
            //обновляем заданный список файловых объектов
            updateListView(clientItemListView, clientFilesList(currentClientDir));
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
        //в отдельном потоке запускаем обновление интерфейса
        Platform.runLater(() -> {
            //выводим текущую директорию в метку серверной части
            storageDirLabel.setText(currentStorageDir);
            //обновляем заданный список файловых объектов
            updateListView(storageItemListView, fileObjs);
        });
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
    private MenuItem menuItemGetInto(ListView<File> listView) {
        //инициируем пункт контекстного меню "Получить список файловых объектов"
        MenuItem menuItemGetInto = new MenuItem("Get into");
        //устанавливаем обработчика нажатия на этот пункт контекстного меню
        menuItemGetInto.setOnAction(event -> {
            //запоминаем кликнутый элемент списка
            File item = listView.getSelectionModel().getSelectedItem();
            //если текущий список клиентский
            if(listView.equals(clientItemListView)){
                //обновляем список элементов списка клиентской части
                updateClientItemListInGUI(item.getName());
            //если текущий список облачного хранилища
            } else if(listView.equals(storageItemListView)){
                //отправляем на сервер запрос на получение списка элементов заданной директории
                //пользователя в сетевом хранилище
                storageClient.demandDirectoryItemList(item.getName());
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
    private MenuItem menuItemUpload(ListView<File> listView) {
        //инициируем пункт контекстного меню "Загрузить в облачное хранилище"
        MenuItem menuItemUpload = new MenuItem("Upload");
        //устанавливаем обработчика нажатия на этот пункт контекстного меню
        menuItemUpload.setOnAction(event -> {
            //запоминаем кликнутый элемент списка
            File item = listView.getSelectionModel().getSelectedItem();

            System.out.println("GUIController.callContextMenu().menuItemUpload().setOnAction() - " +
                    "\nitem: " + item +
                    ", item.getParent(): " + item.getParent());

            try {
                //отправляем на сервер запрос на загрузку файла в облачное хранилище
                storageClient.demandUploadFile(item.getParent(), currentStorageDir, item.getName());
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
    private MenuItem menuItemDownload(ListView<File> listView) {
        //инициируем пункт контекстного меню "Скачать из облачного хранилища"
        MenuItem menuItemDownload = new MenuItem("Download");
        //устанавливаем обработчика нажатия на этот пункт контекстного меню
        menuItemDownload.setOnAction(event -> {
            //запоминаем кликнутый элемент списка
            File item = listView.getSelectionModel().getSelectedItem();

//            System.out.println("GUIController.callContextMenu().menuItemDownload.setOnAction() - " +
//                    "\nitem: " + item +
//                    ", currentStorageDir: " + currentStorageDir +
//                    ", currentClientDir: " + currentClientDir +
//                    ", item.getName(): " + item.getName());

            //отправляем на сервер запрос на скачивание файла из облачного хранилища
//            storageClient.demandDownloadFile(currentStorageDir,
//                    realClientDirectory(currentClientDir), item.getName());
            storageClient.demandDownloadFile(currentStorageDir,
                    currentClientDir, item.getName());

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
//            updateClientItemListInGUI(origin.getParent(), new File(origin.getParent()).listFiles());
            updateClientItemListInGUI(currentClientDir);

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
            File item = listView.getSelectionModel().getSelectedItem();
            //если текущий список клиентский
            if(listView.equals(clientItemListView)){
                //удаляем файл или папку в текущей директории на клиенте
                storageClient.deleteItem(item);
                //обновляем список элементов списка клиентской части
                updateClientItemListInGUI(currentClientDir);
            //если текущий список облачного хранилища
            } else if(listView.equals(storageItemListView)){
                //отправляем на сервер запрос на получение списка элементов заданной директории
                //пользователя в сетевом хранилище
                storageClient.demandDeleteItem(currentStorageDir, item.getName());
            }
            //сбрасываем выделение после действия
            listView.getSelectionModel().clearSelection();
        });
        return menuItemDelete;
    }

    private String clientDefaultDirectory(){
        return storageClient.getClientDefaultDirectory();
    }

    private File[] clientFilesList(String currentDirectory) {
        return new File(realClientDirectory(currentDirectory)).listFiles();
    }

    public String realClientDirectory(String currentDirectory){
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
