package javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class RenameController {

    @FXML
    private VBox globParent;

    @FXML
    private TextField newName;

    private GUIController backController;

    /**
     * Метод обрабатывает клик мыши по кнопке "Confirm" в диалоговом окне ввода нового имени
     * @param actionEvent - клик мыши по кнопке "Confirm"
     */
    @FXML
    public void saveNewName(ActionEvent actionEvent) {
        //если введенное новое имя корректно
        if(isNewItemNameCorrect(newName.getText())){
            //записываем новое имя в соответствующую переменную главного контроллера
            backController.setNewName(newName.getText());
            //закрываем модальное окно
            globParent.getScene().getWindow().hide();
        }
    }

    /**
     * Метод проверяет правильность введенного нового имени объекта списка.
     * @param newName - введенное новое имя объекта списка(файла или папки)
     * @return - результат проверки
     */
    private boolean isNewItemNameCorrect(String newName){
        return !newName.trim().isEmpty();
    }

    public void setNewNameString(String text) {
        this.newName.setText(text);
    }

    public void setBackController(GUIController backController) {
        this.backController = backController;
    }
}
