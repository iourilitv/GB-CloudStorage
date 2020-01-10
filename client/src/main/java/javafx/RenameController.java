package javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class RenameController {

    @FXML
    TextField newName;

    @FXML
    VBox globParent;

    public GUIController backController;

    public void printNewName(ActionEvent actionEvent) {
        System.out.println("RenameController.printNewName() - newName.getText(): " + newName.getText());

        backController.setNewName(newName.getText());

        globParent.getScene().getWindow().hide();
    }
}
