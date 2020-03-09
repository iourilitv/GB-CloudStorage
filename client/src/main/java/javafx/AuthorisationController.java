package javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * This class is a fxml-controller of the Authorisation Form.
 */
public class AuthorisationController {

    @FXML
    private VBox globParent;

    @FXML
    private TextField login;

    @FXML
    private PasswordField password;

    //принимаем объект главного контроллера GUI
    private GUIController backController;
    //принимаем объект проверяльщика форм
    private FormChecker formChecker = FormChecker.getInstance();

    /**
     * Метод отрабатывает клик линка "Registration" в авторизационной форме.
     * Открывает окно Регистрационной формы.
     * @param actionEvent - событие клик мыши
     */
    @FXML
    public void onRegistrationLinkClick(ActionEvent actionEvent) {
        //очищаем все поля формы авторизации/регистрации
        clearAuthorisationForm();
        //выводим сообщение в метку оповещения в GUI
        backController.showTextInGUI("Insert your new registration data please.");
        //переходим в режим регистрации
        backController.setRegistrationFormMode();
    }

    /**
     * Метод обрабатывает клик мыши по кнопке "Authorization" в диалоговом окне.
     * Запускает процесс отправки данных на сервер для автторизации.
     * @param actionEvent - клик мыши по кнопке "Authorization"
     */
    @FXML
    public void onAuthorizationBtnClick(ActionEvent actionEvent) {
        //если введенные логин и пароль корректны
        if(isLoginPasswordCorrect(login.getText(), password.getText())){
            //запускаем процесс авторизации
            backController.demandAuthorisation(login.getText(), password.getText());
        }
    }

    /**
     * Метод проверяет корректность введенной пары - логин и пароль
     * @param login - введенные логин
     * @param password - введенные пароль
     * @return - результат проверки корректности введенной пары - логин и пароль
     */
    private boolean isLoginPasswordCorrect(String login, String password){
        //если логин невалидный
        if(formChecker.isLoginNotValid(login)){
            //выводим соотвествующее предупреждение в лог и в метку сообщений в GUI
            writeToLog("AuthorisationController.isLoginPasswordCorrect() - " + formChecker.getMessage());
            showNoticeInGUI(formChecker.getMessage());
            return false;
            //если пароль невалидный
        } else if (formChecker.isPasswordNotValid(password)){
            writeToLog("AuthorisationController.isLoginPasswordCorrect() - " + formChecker.getMessage());
            showNoticeInGUI(formChecker.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Метод очистки всех полей авторизационной формы.
     */
    private void clearAuthorisationForm(){
        login.setText("");
        password.setText("");
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

    public String getLoginString() {
        return login.getText();
    }

    public String getPasswordString() {
        return password.getText();
    }

    public void setLoginString(String login) {
        this.login.setText(login);
    }

    public void setPasswordString(String password) {
        this.password.setText(password);
    }

    public void setBackController(GUIController backController) {
        this.backController = backController;
    }

    private void showNoticeInGUI(String notice){
        backController.getNoticeLabel().setText(notice);
    }

    private void writeToLog(String msg){
        backController.writeToLog(msg);
    }
}
