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

    /**
     * Метод обрабатывает клик мыши по кнопке "Confirm" в диалоговом окне ввода нового имени
     * @param actionEvent - клик мыши по кнопке "Confirm"
     */
    @FXML
    public void saveNewName(ActionEvent actionEvent) {
        //если введенное новое имя корректно
        if(isNewNameCorrect(newName.getText())){
            //записываем новое имя в соответствующую переменную главного контроллера
            backController.setNewName(newName.getText());
            //закрываем модальное окно
            globParent.getScene().getWindow().hide();
        }
    }

    //FIXME
    private boolean isNewNameCorrect(String newName){
        return !newName.trim().isEmpty();
    }
}
