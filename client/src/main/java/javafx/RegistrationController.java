package javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * This class is a fxml-controller of the Registration Form.
 */
public class RegistrationController {

    @FXML
    private VBox globParent;

    @FXML
    private TextField login, first_name, last_name, email;

    @FXML
    private PasswordField password, passwordConfirm;

    //принимаем объект главного контроллера GUI
    private GUIController backController;
    //принимаем объект проверяльщика форм
    private FormChecker formChecker = FormChecker.getInstance();

    /**
     * Метод отрабатывает клик кнопки на кнопку "Registration".
     * Открывает Авторизационную форму и запускает процесс отправки запроса на сервер
     * для регистрации в сетевом хранилище.
     * @param actionEvent - событие клик мыши
     */
    @FXML
    public void onRegistrationBtnClick(ActionEvent actionEvent) {
        //если введенные регистрационные данные корректны
        if(isRegistrationDataCorrect(login.getText(), first_name.getText(), last_name.getText(),
                email.getText(), password.getText(), passwordConfirm.getText())){
            //выводим сообщение в метку оповещения в GUI
            backController.showTextInGUI("Your registration data has been sent. Wait please...");
            //запускаем процесс регистрации в сетевом хранилище
            backController.demandRegistration(login.getText(),
                    first_name.getText(), last_name.getText(),
                    email.getText(), password.getText());
        }
    }

    /**
     * Метод отрабатывает клик линка "Authorization" в регистрационной форме.
     * Открывает Авторизационную форму.
     * @param actionEvent - событие клик мыши
     */
    @FXML
    public void onAuthorizationLinkClick(ActionEvent actionEvent) {
        //выводим сообщение в метку уведомлений
        backController.getNoticeLabel().setText("Insert your login and password please.");
        //очищаем все поля формы авторизации/регистрации
        clearRegistrationForm();
        //переходим в режим авторизации
        backController.setAuthorizationFormMode();
    }

    /**
     * Метод проверяет корректность введенных данных в регистрационной форме.
     * @param login - введенный логин
     * @param password - введенный пароль
     * @param passwordConfirm - введенный второй раз пароль
     * @return - результат проверки корректности введенных данных в регистрационной форме
     */
    private boolean isRegistrationDataCorrect(String login, String first_name,
                                              String last_name, String email, String password, String passwordConfirm){
        return isLoginPasswordCorrect(login, password) && !first_name.trim().isEmpty() &&
                !last_name.trim().isEmpty() && isEmailValid(email) &&
                !passwordConfirm.trim().isEmpty() && password.equals(passwordConfirm);
    }

    /**
     * Метод-прокладка запускает проверку корректности введенной пары - логин и пароль
     * @param login - введенные логин
     * @param password - введенные пароль
     * @return - результат проверки корректности введенной пары - логин и пароль
     */
    private boolean isLoginPasswordCorrect(String login, String password){
        //если логин невалидный
        if(formChecker.isLoginNotValid(login)){
            //выводим соотвествующее предупреждение в лог и в метку сообщений в GUI
            writeToLog("RegistrationController.isLoginPasswordCorrect() - " + formChecker.getMessage());
            showNoticeInGUI(formChecker.getMessage());
            return false;
        //если пароль невалидный
        } else if (formChecker.isPasswordNotValid(password)){
            writeToLog("RegistrationController.isLoginPasswordCorrect() - " + formChecker.getMessage());
            showNoticeInGUI(formChecker.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Метод-прокладка запускает проверку корректности введенного email.
     * @param email - введенный email
     * @return - результат проверки
     */
    private boolean isEmailValid(String email){
        //если email невалидный
        if(!formChecker.isEmailValid(email)){
            //выводим соотвествующее предупреждение в лог и в метку сообщений в GUI
            writeToLog("RegistrationController.isEmailValid() - " + formChecker.getMessage());
            showNoticeInGUI(formChecker.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Метод очистки полей в регистрационной/авторизационной форме.
     */
    private void clearRegistrationForm(){
        login.setText("");
        first_name.setText("");
        last_name.setText("");
        email.setText("");
        password.setText("");
        passwordConfirm.setText("");
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
