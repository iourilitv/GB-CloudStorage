package javafx;

import control.CloudStorageClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * The client class for operating with directoryMessages.
 */
public class GUIController implements Initializable {
    private enum Types{
        FOLDER,
        FILE
    }

    @FXML
    Label clientDirLabel, storageDirLabel;

    @FXML
//    ListView<Item> ClientItemListView;
    ListView<File> ClientItemListView;

//    @FXML
//    VBox clientDirsVBox;

//    @FXML
//    Button clientDirBtn;

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

//        //выводим в клиентской части интерфейса список объектов в директории по умолчанию
//        updateClientFilesAndFoldersListInGUI(storageClient.getClientDefaultRoot(),
//                new File(storageClient.getClientDefaultRoot()).listFiles());
//        //запрашиваем у сервера список объектов в директории по умолчанию в сетевом хранилище
//        serverFiles.getItems().addAll("S_File 1", "S_File 2", "S_File 3");
    }

    public void initializeClientItemListView() {
        //выводим в клиентской части интерфейса список объектов в директории по умолчанию
        updateClientFilesAndFoldersListInGUI(storageClient.getClientDefaultRoot(),
                new File(storageClient.getClientDefaultRoot()).listFiles());

//        ClientItemListView.getItems().addAll(new Item("folder"), new Item("file"));
//        ClientItemListView.setCellFactory(itemListView -> new FileListCell());
    }

    @FXML
    public void btnClickSelectedClientFile(ActionEvent actionEvent) {
//        label.setText(clientFiles.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void btnClickSelectedServerFile(ActionEvent actionEvent) {
//        label.setText(serverFiles.getSelectionModel().getSelectedItem());

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

    //FIXME
    private void onFolderBtnEvent(ActionEvent event, Button btn) {

        System.out.println("GUIController.onFolderBtnEvent() - btn.getText(): " + btn.getText());
        clientDirLabel.setText(btn.getText());
    }

//    public void updateClientFilesAndFoldersListInGUI(String directory, File[] fileObjs){
//
//        //TODO temporarily
//        System.out.println("[client]GUIController.updateClientFilesAndFoldersListInGUI directory: " +
//                directory + ". fileObjs.toString(): " + Arrays.toString(fileObjs));
//
//        Platform.runLater(() -> {
//            //записываем в метку текущую директорию
//            clientDirLabel.setText(directory);
//            //очищаем бокс кнопок
//            clientDirsVBox.getChildren().removeAll();
//            //очищаем список элементов в директории
//            clientFiles.getItems().clear();
//
//            //в цикле распределяем файловые объекты в заданной директории по коллекциям
//            for (File f: fileObjs) {
//                //если файловый объект - директория
//                if(f.isDirectory()){
//                    //создаем объект кнопки с названием папки
//                    Button btn = new Button(f.getName());
//                    //задаем всем кнопкам одинаковый идентификатор для события
//                    btn.setId("clientDirBtn");
//
//                    //FIXME установить на всю ширину бокса
////                    btn.setMaxWidth(HBox.setHgrow();
//                    //TODO check
//                    //устанавливаем обработчика события нажатие кнопки
//                    btn.setOnAction(event -> onFolderBtnEvent(event, btn));
//
//                    //добавляем объект кнопки в VBox
//                    clientDirsVBox.getChildren().add(btn);
//
//                    //TODO temporarily
//                    System.out.println("GUIController.updateStorageFilesAndFoldersListInGUI() - " +
//                            "btn.getText(): " + btn.getText());
//                    System.out.println("GUIController.updateStorageFilesAndFoldersListInGUI() - " +
//                            "clientDirsVBox.getChildren().toString(): " +
//                            clientDirsVBox.getChildren().toString());
//                } else {
//                    //добавляем новый список элементов в директории
//                    clientFiles.getItems().add(f.getName());
//                    clientFiles.refresh();
//                }
//            }
//
//            System.out.println("GUIController.updateStorageFilesAndFoldersListInGUI() - clientFiles: " +
//                    clientFiles.getItems().toString());
//
//        });
//    }
    public void updateClientFilesAndFoldersListInGUI(String directory, File[] fileObjs){

        //TODO temporarily
        System.out.println("[client]GUIController.updateClientFilesAndFoldersListInGUI directory: " +
                directory + ". fileObjs.toString(): " + Arrays.toString(fileObjs));

        Platform.runLater(() -> {
            //записываем в метку текущую директорию
            clientDirLabel.setText(directory);
            //обновляем спи
            ClientItemListView.getItems().addAll(fileObjs);
            ClientItemListView.setCellFactory(itemListView -> new FileListCell());

//            //в цикле распределяем файловые объекты в заданной директории по коллекциям
//            for (File f: fileObjs) {
//                //если файловый объект - директория
//                if(f.isDirectory()){
//
//
//                    //TODO temporarily
//                    System.out.println("GUIController.updateStorageFilesAndFoldersListInGUI() - " +
//                            "btn.getText(): " + btn.getText());
//                    System.out.println("GUIController.updateStorageFilesAndFoldersListInGUI() - " +
//                            "clientDirsVBox.getChildren().toString(): " +
//                            clientDirsVBox.getChildren().toString());
//                } else {
//                    //добавляем новый список элементов в директории
//                    clientFiles.getItems().add(f.getName());
//                    clientFiles.refresh();
//                }
//            }

//            System.out.println("GUIController.updateStorageFilesAndFoldersListInGUI() - clientFiles: " +
//                    clientFiles.getItems().toString());

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
     * @param namesList - список названий файлов и папок в заданной директории
     */
    public void updateStorageFilesAndFoldersListInGUI(String directory, String[] namesList){
        //FIXME передать в GUI

        //TODO temporarily
        System.out.println("[client]GUIController.updateStorageFilesListInGUI directory: " +
                directory +
                ". filesList: " + Arrays.toString(namesList));

        Platform.runLater(() -> {
//            //очищаем список элементов в директории
//            serverFiles.getItems().clear();
//            //добавляем новый список элементов в директории
//            serverFiles.getItems().addAll(namesList);
//            serverFiles.refresh();

//            System.out.println("GUIController.updateStorageFilesAndFoldersListInGUI() - serverFiles: " +
//                    serverFiles.getItems().toString());

            //выводим текущую директорию в метку серверной части
            storageDirLabel.setText(directory);
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
        return ClientItemListView;
    }
}
