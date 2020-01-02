package javafx;

import control.CloudStorageClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.net.URL;
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

    private CloudStorageClient storageClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //инициируем объект клиента облачного хранилища
        storageClient = new CloudStorageClient(GUIController.this);
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
        updateClientItemListInGUI(storageClient.getClientDefaultRoot(),
                new File(storageClient.getClientDefaultRoot()).listFiles());
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

    @FXML
    public void btnClickSelectedClientFile(ActionEvent actionEvent) {
        label.setText(clientItemListView.getSelectionModel().getSelectedItem().getName());
    }

    @FXML
    public void btnClickSelectedServerFile(ActionEvent actionEvent) {
        label.setText(storageItemListView.getSelectionModel().getSelectedItem().getName());
    }

    /**
     * Метод обновляет список элементов списка в клиентской части
     * @param directory - заданная директория
     * @param fileObjs - массив объектов класса File(файлы и директории)
     */
    public void updateClientItemListInGUI(String directory, File[] fileObjs){
        Platform.runLater(() -> {
            //записываем в метку текущую директорию
            clientDirLabel.setText(directory);
            //очищаем список элементов
            clientItemListView.getItems().clear();
            //обновляем список элементов списка
            clientItemListView.getItems().addAll(fileObjs);
            clientItemListView.setCellFactory(itemListView -> new FileListCell());
        });
    }

    /**
     * Метод выводит в GUI список файлов и папок в корневой пользовательской директории
     * в сетевом хранилище.
     * @param directory - заданная пользовательская директория в сетевом хранилище
     * @param fileObjs - массив объектов класса File(файлы и директории)
     */
    public void updateStorageItemListInGUI(String directory, File[] fileObjs){
        Platform.runLater(() -> {
            //выводим текущую директорию в метку серверной части
            storageDirLabel.setText(directory);
            //очищаем список элементов
            storageItemListView.getItems().clear();
            //обновляем список элементов списка
            storageItemListView.getItems().addAll(fileObjs);
            storageItemListView.setCellFactory(itemListView -> new FileListCell());
        });
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

    /**
     * Метод обрабатывает событие клика мыши на элементе списка клиентской части
     * @param mouseEvent - событие клика мыши на элементе списка
     */
    @FXML
    public void onClickClientListFolderItem(MouseEvent mouseEvent) {
        //запоминаем кликнутый элемент списка
        File item = clientItemListView.getSelectionModel().getSelectedItem();
        //если двойной клик левой кнопкой мыши
        if(mouseEvent.getButton().name().equals("PRIMARY") &&
            mouseEvent.getClickCount() == 2){
            //если кликнутый элемент это директория
            if(item.isDirectory()){

                System.out.println("GUIController.onClickClientListFolderItem() - " +
//                        "mouseEvent.getEventType(): " + mouseEvent.getEventType() +
//                        ", mouseEvent.getButton().name(): " + mouseEvent.getButton().name() +
                        ", clientItemListView.getSelectionModel().getSelectedItem().getName(): " +
                        clientItemListView.getSelectionModel().getSelectedItem().getName()
                );
                //обновляем список элементов списка клиентской части
                updateClientItemListInGUI(item.getName(), item.listFiles());
            }
        //если клик правой кнопкой мыши
        } else if(mouseEvent.getButton().name().equals("SECONDARY")){
            System.out.println("GUIController.btnClickDirectory() - mouseEvent.getEventType(): " +
                    mouseEvent.getEventType() +
                    ", mouseEvent.getButton().name(): " + mouseEvent.getButton().name());
            //FIXME
            //метод вызова контекстного меню

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
//                        "mouseEvent.getEventType(): " + mouseEvent.getEventType() +
//                        ", mouseEvent.getButton().name(): " + mouseEvent.getButton().name() +
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
        //если клик правой кнопкой мыши
        } else if(mouseEvent.getButton().name().equals("SECONDARY")){
            System.out.println("GUIController.btnClickDirectory() - mouseEvent.getEventType(): " +
                    mouseEvent.getEventType() +
                    ", mouseEvent.getButton().name(): " + mouseEvent.getButton().name());
            //FIXME
            //метод вызова контекстного меню
        }
    }

    public ListView<File> getClientItemListView() {
        return clientItemListView;
    }
}
