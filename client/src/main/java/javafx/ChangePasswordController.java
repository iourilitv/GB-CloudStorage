package javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * This class is responsible for the "change password form" controlling.
 */
public class ChangePasswordController {

    @FXML
    private VBox globParent;

    @FXML
    private TextField login;

    @FXML
    private PasswordField password, newPassword, newPasswordConfirm;

    //главный контроллер GUI
    private GUIController backController;

    /**
     * Метод отрабатывает клик кнопки на кнопку "Confirm".
     * Запускает процесс изменения пароля пользователя в сетевое хранилище.
     * @param actionEvent - событие клик мыши
     */
    @FXML
    public void onConfirmBtnClick(ActionEvent actionEvent) {
        //если введенные данные корректны
        if(isNewDataCorrect(login.getText(), password.getText(),
                newPassword.getText(), newPasswordConfirm.getText())){
            //выводим сообщение в метку оповещения в GUI
            backController.showTextInGUI("Your new password has been sent. Wait please...");
            //запускаем процесс изменения пароля пользователя в сетевое хранилище
            backController.demandChangePassword(login.getText(),
                    password.getText(), newPassword.getText());
            //если введенные данные не корректны
        } else {
            //выводим сообщение в метку оповещения в GUI
            backController.showTextInGUI("Uncorrected data! Fill it again please.");
        }
    }

    /**
     * Метод отрабатывает клик кнопки на кнопку "Cancel".
     * Закрывает форму смены пароля и очищает поля.
     * @param actionEvent - событие клик мыши
     */
    @FXML
    public void onCancelBtnClick(ActionEvent actionEvent) {
        //очищаем поля форму
        clearFormFields();
        //закрываем окно формы
        hideWindow();
    }

    /**
     * Метод проверяет корректность введенных данных в форме.
     * @param login - введенный логин
     * @param password - введенный текущий пароль
     * @param newPassword - введенный новый пароль
     * @param newPasswordConfirm - введенный второй раз новый пароль
     * @return - результат проверки корректности введенных данных
     */
    private boolean isNewDataCorrect(String login, String password, String newPassword,
                                     String newPasswordConfirm){
        return !login.trim().isEmpty() && !password.trim().isEmpty() &&
                !newPassword.trim().isEmpty() && !newPasswordConfirm.trim().isEmpty() &&
                !password.equals(newPassword) && newPassword.equals(newPasswordConfirm);
    }

    /**
     * Метод очистки всех полей формы.
     */
    private void clearFormFields(){
        login.setText("");
        password.setText("");
        newPassword.setText("");
        newPasswordConfirm.setText("");
    }

    /**
     * Метод закрывает окно.
     */
    public void hideWindow(){
        //если окно показывается
        if(globParent.getScene().getWindow().isShowing()){
            //закрываем окно
            globParent.getScene().getWindow().hide();
        }
    }

    public void setBackController(GUIController backController) {
        this.backController = backController;
    }
}
