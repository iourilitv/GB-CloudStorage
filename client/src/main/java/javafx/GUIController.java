package javafx;

import control.CloudStorageClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * The client class for operating with directoryMessages.
 */
public class GUIController implements Initializable {

    @FXML
    Label clientDirLabel, storageDirLabel;

    @FXML
    VBox clientDirsVBox;

    @FXML
    Button clientDirBtn;

    @FXML
    ListView<String> clientFiles, serverFiles;

    @FXML
    Label label;

    private CloudStorageClient storageClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        storageClient = new CloudStorageClient(GUIController.this);
//        storageClient.updateGUIClientList(Paths.get(storageClient.getClientDefaultRoot()));
//        updateClientFilesAndFoldersListInGUI(storageClient.getClientDefaultRoot(),
//                new File(storageClient.getClientDefaultRoot()).list());//TODO может list File[]?
        updateClientFilesAndFoldersListInGUI(storageClient.getClientDefaultRoot(),
                new File(storageClient.getClientDefaultRoot()).listFiles());

//        clientFiles.getItems().addAll("C_File 1", "C_File 2", "C_File 3");
        serverFiles.getItems().addAll("S_File 1", "S_File 2", "S_File 3");
    }

    @FXML
    public void btnClickSelectedClientFile(ActionEvent actionEvent) {
        label.setText(clientFiles.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void btnClickSelectedServerFile(ActionEvent actionEvent) {
        label.setText(serverFiles.getSelectionModel().getSelectedItem());

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

//    public void updateClientFilesAndFoldersListInGUI(
//            String directory, ArrayList<String> foldersNames, ArrayList<String> filesNames
//            ){
//
//        //TODO temporarily
//        System.out.println("[client]GUIController.updateClientFilesAndFoldersListInGUI directory: " +
//                directory +
//                ". foldersNames.toString(): " + foldersNames.toString() +
//                ". filesNames.toString(): " + filesNames.toString());
//
//        Platform.runLater(() -> {
//            //записываем в метку текущую директорию
//            clientDirLabel.setText(directory);
//
//            //в цикле распределяем файловые объекты в заданной директории по коллекциям
//            for (File f: filesArray) {
//                //если файловый объект - директория
//                if(f.isDirectory()){
//                    //добавляем в коллекцию имен папок
//                    foldersNames.add(f.toString());
//                    //если - не директория
//                } else {
//                    //добавляем в коллекцию имен файлов
//                    filesNames.add(f.toString());
//                }
//            }
//            //создаем коллекцию объектов кнопок
//            Button[] foldersBtns = new Button[foldersNames.size()];//TODO может массив и не нужен
//            //пробегаем список имен папок
//            for (int i = 0; i < foldersNames.size(); i++) {
//                //создаем объект кнопки с названием папки
//                foldersBtns[i] = new Button(foldersNames.get(i));
//                //задаем всем кнопкам одинаковый идентификатор для события
//                foldersBtns[i].setId("clientDirBtn");
//                //добавляем объект кнопки в VBox
//                clientDirsVBox.getChildren().add(foldersBtns[i]);
//            }
//
//            //очищаем список элементов в директории
//            clientFiles.getItems().clear();
//            //добавляем новый список элементов в директории
//            clientFiles.getItems().addAll(filesNames);
//            clientFiles.refresh();
//
//            System.out.println("GUIController.updateStorageFilesAndFoldersListInGUI() - serverFiles: " +
//                    clientFiles.getItems().toString());
//
//        });
//    }
//    public void updateClientFilesAndFoldersListInGUI(String directory, String[] namesList){
//
//        //TODO temporarily
//        System.out.println("[client]GUIController.updateClientFilesAndFoldersListInGUI directory: " +
//                directory + ". namesList.toString(): " + Arrays.toString(namesList));
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
//            for (String n: namesList) {
//                //если файловый объект - директория
//                if(new File(n).isDirectory()){
//                    //создаем объект кнопки с названием папки
//                    Button btn = new Button(n);
//                    //задаем всем кнопкам одинаковый идентификатор для события
//                    btn.setId("clientDirBtn");
//                    //добавляем объект кнопки в VBox
//                    clientDirsVBox.getChildren().add(btn);
//
//                    //TODO temporarily
//                    System.out.println("GUIController.updateStorageFilesAndFoldersListInGUI() - " +
//                            "clientDirBtn.getText(): " + clientDirBtn.getText());
//                    System.out.println("GUIController.updateStorageFilesAndFoldersListInGUI() - " +
//                            "clientDirsVBox.getChildren().toString(): " +
//                            clientDirsVBox.getChildren().toString());
////                    //добавляем в коллекцию имен папок
////                    foldersNames.add(f.toString());
//
//                //если - не директория
//                } else {
//                    //добавляем новый список элементов в директории
//                    clientFiles.getItems().add(n);
//                    clientFiles.refresh();
////                    //добавляем в коллекцию имен файлов
////                    filesNames.add(f.toString());
//                }
//            }
////            //создаем коллекцию объектов кнопок
////            Button[] foldersBtns = new Button[foldersNames.size()];//TODO может массив и не нужен
////            //пробегаем список имен папок
////            for (int i = 0; i < foldersNames.size(); i++) {
////                //создаем объект кнопки с названием папки
////                foldersBtns[i] = new Button(foldersNames.get(i));
////                //задаем всем кнопкам одинаковый идентификатор для события
////                foldersBtns[i].setId("clientDirBtn");
////                //добавляем объект кнопки в VBox
////                clientDirsVBox.getChildren().add(foldersBtns[i]);
////            }
//
//            //очищаем список элементов в директории
////            clientFiles.getItems().clear();
////            //добавляем новый список элементов в директории
////            clientFiles.getItems().addAll(filesNames);
////            clientFiles.refresh();
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
            //очищаем бокс кнопок
            clientDirsVBox.getChildren().removeAll();
            //очищаем список элементов в директории
            clientFiles.getItems().clear();

            //в цикле распределяем файловые объекты в заданной директории по коллекциям
            for (File f: fileObjs) {
                //если файловый объект - директория
                if(f.isDirectory()){
                    //создаем объект кнопки с названием папки
                    Button btn = new Button(f.getName());
                    //задаем всем кнопкам одинаковый идентификатор для события
                    btn.setId("clientDirBtn");

                    //FIXME установить на всю ширину бокса
//                    btn.setMaxWidth(HBox.setHgrow();
                    //TODO check
                    //устанавливаем обработчика события нажатие кнопки
                    btn.setOnAction(event -> onFolderBtnEvent(event, btn));

                    //добавляем объект кнопки в VBox
                    clientDirsVBox.getChildren().add(btn);

                    //TODO temporarily
                    System.out.println("GUIController.updateStorageFilesAndFoldersListInGUI() - " +
                            "btn.getText(): " + btn.getText());
                    System.out.println("GUIController.updateStorageFilesAndFoldersListInGUI() - " +
                            "clientDirsVBox.getChildren().toString(): " +
                            clientDirsVBox.getChildren().toString());
                } else {
                    //добавляем новый список элементов в директории
                    clientFiles.getItems().add(f.getName());
                    clientFiles.refresh();
                }
            }

            System.out.println("GUIController.updateStorageFilesAndFoldersListInGUI() - clientFiles: " +
                    clientFiles.getItems().toString());

        });
    }

    //FIXME
    private void onFolderBtnEvent(ActionEvent event, Button btn) {

        System.out.println("GUIController.onFolderBtnEvent() - btn.getText(): " + btn.getText());
        label.setText(btn.getText());
    }

    /**
     * Метод выводит в GUI список файлов и папок в корневой пользовательской директории
     * в сетевом хранилище.
     * @param directory - заданная пользовательская директория в сетевом хранилище
     * @param fileNamesList - список названий файлов и папок в заданной директории
     */
    public void updateStorageFilesAndFoldersListInGUI(String directory, String[] fileNamesList){
        //FIXME передать в GUI

        //TODO temporarily
        System.out.println("[client]GUIController.updateStorageFilesListInGUI directory: " +
                directory +
                ". filesList: " + Arrays.toString(fileNamesList));

        Platform.runLater(() -> {
            //очищаем список элементов в директории
            serverFiles.getItems().clear();
            //добавляем новый список элементов в директории
            serverFiles.getItems().addAll(fileNamesList);
            serverFiles.refresh();

            System.out.println("GUIController.updateStorageFilesAndFoldersListInGUI() - serverFiles: " +
                    serverFiles.getItems().toString());


            label.setText(directory);
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

        System.out.println("GUIController.btnClickDirectory() - clientDirBtn.getText(): " +
                clientDirBtn.getText());

    }
}
