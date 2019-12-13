package javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    @FXML
    ListView<String> clientFiles, serverFiles;

    @FXML
    Label label;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clientFiles.getItems().addAll("C_File 1", "C_File 2", "C_File 3");
        serverFiles.getItems().addAll("S_File 1", "S_File 2", "S_File 3");
    }

    public void btnClickSelectedClientFile(ActionEvent actionEvent) {
        label.setText(clientFiles.getSelectionModel().getSelectedItem());
    }

    public void btnClickSelectedServerFile(ActionEvent actionEvent) {
        label.setText(serverFiles.getSelectionModel().getSelectedItem());
    }
}
