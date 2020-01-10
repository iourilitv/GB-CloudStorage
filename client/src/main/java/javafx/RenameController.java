package javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class RenameController {

    @FXML
    TextField newName;

    @FXML
    VBox globParent;

    public GUIController backController;

    public void saveNewName(ActionEvent actionEvent) {

        //TODO temporarily
        System.out.println("[client]RenameController.printNewName() - newName.getText(): " + newName.getText());

        //записываем новое имя в соответствующую переменную главного контроллера
        backController.setNewName(newName.getText());
        //закрываем модальное окно
        globParent.getScene().getWindow().hide();
    }
}
