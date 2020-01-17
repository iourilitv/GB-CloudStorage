package javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML
    VBox authorizationVBox, registrationVBox;

    @FXML
    TextField login;

    @FXML
    PasswordField password, passwordConfirm;

    @FXML
    VBox globParent;

    //главный контроллер GUI
    public GUIController backController;

    /**
     * Метод обрабатывает клик мыши по кнопке "Authorization" в диалоговом окне ввода нового имени
     * @param actionEvent - клик мыши по кнопке "Authorization"
     */
    @FXML
    public void onAuthorizationBtnClick(ActionEvent actionEvent) {
        //если введенные логин и пароль корректны
        if(isLoginPasswordCorrect(login.getText(), password.getText())){
            //записываем введенные логин и пароль в соответствующие переменные главного контроллера
            backController.setLogin(login.getText());
            backController.setPassword(password.getText());
            backController.setFlagWindow(true);
            //запускаем процесс авторизации
            backController.startAuthorisation();
            globParent.getScene().getWindow().hide();
        }
    }

    //FIXME
    @FXML
    public void onRegistrationLinkClick(ActionEvent actionEvent) {
        //очищаем все поля формы авторизации/регистрации
        clearRegAuthFields();
        System.out.println("LoginController.onRegistrationLink() - get registration");
        backController.showTextInGUI("Insert your new registration data.");
        setRegistrationMode(true);
    }

    @FXML
    public void onRegistrationBtnClick(ActionEvent actionEvent) {
        if(isRegistrationDataCorrect(login.getText(), password.getText(), passwordConfirm.getText())){

            backController.showTextInGUI("Your registration data has been sent. Wait please...");
            setRegistrationMode(false);
            passwordConfirm.setText("");
        }
    }

    @FXML
    public void onAuthorizationLinkClick(ActionEvent actionEvent) {
        backController.noticeLabel.setText("Insert your login and password");
        //очищаем все поля формы авторизации/регистрации
        clearRegAuthFields();
        //возвращаемся в режим авторизации
        setRegistrationMode(false);
    }

    private boolean isLoginPasswordCorrect(String login, String password){

        System.out.println("LoginController.isLoginPasswordCorrect() - login: " + login
                + ", password: " + password);
        //FIXME
        return !login.trim().isEmpty() && !password.trim().isEmpty();
    }

    private boolean isRegistrationDataCorrect(String login, String password, String passwordConfirm){

        System.out.println("LoginController.isLoginPasswordCorrect() - login: " + login
                + ", password: " + password + ", passwordConfirm: " + passwordConfirm);
        //FIXME
        return !login.trim().isEmpty() && !password.trim().isEmpty() && !passwordConfirm.trim().isEmpty() &&
                password.equals(passwordConfirm);
    }

    /**
     * Метод устанавливает GUI в режим авторизован или нет, в зависимости от параметра
     * @param isRegMode - true - сервер авторизовал пользователя
     */
    private void setRegistrationMode(boolean isRegMode) {
        //скрываем и деактивируем(если isRegMode = true) кнопку подключения к серверу
        authorizationVBox.setManaged(!isRegMode);
        authorizationVBox.setVisible(!isRegMode);
        //показываем и активируем(если isRegMode = true) список объектов в сетевом хранилище
        registrationVBox.setManaged(isRegMode);
        registrationVBox.setVisible(isRegMode);
    }

    private void clearRegAuthFields(){
        login.setText("");
        password.setText("");
        passwordConfirm.setText("");
    }

}
