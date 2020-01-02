package javafx;

import control.CloudStorageClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * The client class for operating with directoryMessages.
 */
public class GUIController implements Initializable {

    @FXML
    Label clientDirLabel, storageDirLabel;

    @FXML
    ListView<File> clientItemListView, storageItemListView;

//    @FXML
//    ListView<String> clientFiles, serverFiles;

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

//        //выводим в клиентской части интерфейса список объектов в директории по умолчанию
//        updateClientFilesAndFoldersListInGUI(storageClient.getClientDefaultRoot(),
//                new File(storageClient.getClientDefaultRoot()).listFiles());
//        //запрашиваем у сервера список объектов в директории по умолчанию в сетевом хранилище
//        serverFiles.getItems().addAll("S_File 1", "S_File 2", "S_File 3");
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
        updateStorageFilesAndFoldersListInGUI("../",
                new File[]{new File("waiting an item list from the server...")});

        new Thread(() -> {
            try {

                //TODO temporarily
                //запускаем тест
                storageClient.run();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void btnClickSelectedClientFile(ActionEvent actionEvent) {
//        label.setText(clientFiles.getSelectionModel().getSelectedItem());
        label.setText(clientItemListView.getSelectionModel().getSelectedItem().getName());
    }

    @FXML
    public void btnClickSelectedServerFile(ActionEvent actionEvent) {
//        label.setText(serverFiles.getSelectionModel().getSelectedItem());
        label.setText(storageItemListView.getSelectionModel().getSelectedItem().getName());
//        new Thread(() -> {
//            try {
//
//                //TODO temporarily
//                //запускаем тест
//                storageClient.run();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();

    }

    //FIXME
    private void onFolderBtnEvent(ActionEvent event, Button btn) {

        System.out.println("GUIController.onFolderBtnEvent() - btn.getText(): " + btn.getText());
        clientDirLabel.setText(btn.getText());
    }

    /**
     * Метод обновляет список элементов списка в клиентской части
     * @param directory - заданная директория
     * @param fileObjs - массив объектов класса File(файлы и директории)
     */
    public void updateClientItemListInGUI(String directory, File[] fileObjs){

        //TODO temporarily
        System.out.println("[client]GUIController.updateClientFilesAndFoldersListInGUI directory: " +
                directory + ". fileObjs.toString(): " + Arrays.toString(fileObjs));

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

//    /**
//     * Метод выводит в GUI список файлов и папок в корневой пользовательской директории
//     * в сетевом хранилище.
//     * @param directory - заданная пользовательская директория в сетевом хранилище
//     * @param namesList - список названий файлов и папок в заданной директории
//     */
//    public void updateStorageFilesAndFoldersListInGUI(String directory, String[] namesList){
//        //FIXME передать в GUI
//
//        //TODO temporarily
//        System.out.println("[client]GUIController.updateStorageFilesListInGUI directory: " +
//                directory +
//                ". filesList: " + Arrays.toString(namesList));
//
//        Platform.runLater(() -> {
//            //очищаем список элементов в директории
//            serverFiles.getItems().clear();
//            //добавляем новый список элементов в директории
//            serverFiles.getItems().addAll(namesList);
//            serverFiles.refresh();
//
//            System.out.println("GUIController.updateStorageFilesAndFoldersListInGUI() - serverFiles: " +
//                    serverFiles.getItems().toString());
//
//            //выводим текущую директорию в метку серверной части
//            storageDirLabel.setText(directory);
//        });
//    }
    /**
     * Метод выводит в GUI список файлов и папок в корневой пользовательской директории
     * в сетевом хранилище.
     * @param directory - заданная пользовательская директория в сетевом хранилище
     * @param fileObjs - массив объектов класса File(файлы и директории)
     */
    public void updateStorageFilesAndFoldersListInGUI(String directory, File[] fileObjs){

        //TODO temporarily
        System.out.println("[client]GUIController.updateStorageFilesListInGUI directory: " +
                directory +
                ". filesList: " + Arrays.toString(fileObjs));

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

    @FXML
    public void btnClickDirectory(ActionEvent actionEvent) {

//        System.out.println("GUIController.btnClickDirectory() - clientDirBtn.getText(): " +
//                clientDirBtn.getText());

    }

    public ListView<File> getClientItemListView() {
        return clientItemListView;
    }
}
