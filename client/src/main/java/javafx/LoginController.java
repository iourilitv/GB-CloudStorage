package javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginController {
    @FXML
    TextField login;

    @FXML
    PasswordField password;

    @FXML
    VBox globParent;

    public int id;

    public GUIController backController;

    public void auth(ActionEvent actionEvent) {
        System.out.println(login.getText() + " " + password.getText());
        System.out.println("id = " + id);
        globParent.getScene().getWindow().hide();
    }
}
