package javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * This class is responsible for control of the "new client root path window" operations.
 */
public class ChangeRootController {

    @FXML
    private TextField newPathname;

    @FXML
    private VBox globParent;

    private GUIController backController;

    /**
     * Метод обрабатывает клик мыши по кнопке "Confirm" в диалоговом окне ввода нового имени
     * @param actionEvent - клик мыши по кнопке "Confirm"
     */
    @FXML
    public void saveNewPathname(ActionEvent actionEvent) {
        //если введенное новое имя корректно
        if(isNewPathnameCorrect(newPathname.getText())){
            //записываем новое имя в соответствующую переменную главного контроллера
            backController.setNewRootPathname(newPathname.getText());
            //закрываем модальное окно
            globParent.getScene().getWindow().hide();
        }
    }

    /** //FIXME Upd 31. Добавить проверку через Path
     * Метод проверяет правильность введенной строки абсолютного пути к новой директории.
     * @param newPathname - строка абсолютного пути к новой директории
     * @return - результат проверки
     */
    private boolean isNewPathnameCorrect(String newPathname){
        return !newPathname.trim().isEmpty();
    }

    public void setNewPathnameText(String newPathname) {
        this.newPathname.setText(newPathname);
    }

    public GUIController getBackController() {
        return backController;
    }

    public void setBackController(GUIController guiController) {
        backController = guiController;
    }
}
