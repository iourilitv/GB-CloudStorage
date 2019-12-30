package javafx;

import control.CloudStorageClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * The client class for operating with directoryMessages.
 */
public class GUIController implements Initializable {
    @FXML
    ListView<String> clientFiles, serverFiles;

    @FXML
    Label label;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clientFiles.getItems().addAll("C_File 1", "C_File 2", "C_File 3");
        serverFiles.getItems().addAll("S_File 1", "S_File 2", "S_File 3");
    }

    @FXML
    public void btnClickSelectedClientFile(ActionEvent actionEvent) {
        label.setText(clientFiles.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void btnClickSelectedServerFile(ActionEvent actionEvent) {
        label.setText(serverFiles.getSelectionModel().getSelectedItem());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new CloudStorageClient(GUIController.this).run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

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

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //очищаем список элементов в директории
                serverFiles.getItems().clear();
                //добавляем новый список элементов в директории
                serverFiles.getItems().addAll(fileNamesList);
                serverFiles.refresh();

                System.out.println("GUIController.updateStorageFilesAndFoldersListInGUI() - serverFiles: " +
                        serverFiles.getItems().toString());


                label.setText(directory);
            }
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

}
