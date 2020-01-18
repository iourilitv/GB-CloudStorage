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
     * Метод отрабатывает клик линка "Registration" в авторизационной форме.
     * Открывает Регистрационную форму.
     * @param actionEvent - событие клик мыши
     */
    @FXML
    public void onRegistrationLinkClick(ActionEvent actionEvent) {
        //очищаем все поля формы авторизации/регистрации
        clearRegAuthFields();
        //выводим сообщение в метку оповещения в GUI
        backController.showTextInGUI("Insert your new registration data.");
        //устанавливаем режим отображения "Регистрационная форма"
        setRegistrationMode(true);
    }

    /**
     * Метод отрабатывает клик кнопки на кнопку "Registration".
     * Открывает Авторизационную форму и запускает процесс регистрации в сетевом хранилище.
     * @param actionEvent - событие клик мыши
     */
    @FXML
    public void onRegistrationBtnClick(ActionEvent actionEvent) {
        //если введенные регистрационные данные корректны
        if(isRegistrationDataCorrect(login.getText(), password.getText(), passwordConfirm.getText())){
            //выводим сообщение в метку оповещения в GUI
            backController.showTextInGUI("Your registration data has been sent. Wait please...");
            //запускаем процесс регистрации в сетевом хранилище
            backController.demandRegistration(login.getText(), password.getText());
            //устанавливаем режим отображения "Регистрационная форма"
            setRegistrationMode(false);
            //очищаем текстовое поле "подтверждение пароля" регистрационной формы
            passwordConfirm.setText("");
        }
    }

    /**
     * Метод отрабатывает клик линка "Authorization" в регистрационной форме.
     * Открывает Авторизационную форму.
     * @param actionEvent - событие клик мыши
     */
    @FXML
    public void onAuthorizationLinkClick(ActionEvent actionEvent) {
        backController.noticeLabel.setText("Insert your login and password");
        //очищаем все поля формы авторизации/регистрации
        clearRegAuthFields();
        //возвращаемся в режим авторизации
        setRegistrationMode(false);
    }

    /**
     * Метод обрабатывает клик мыши по кнопке "Authorization" в диалоговом окне ввода нового имени
     * @param actionEvent - клик мыши по кнопке "Authorization"
     */
    @FXML
//    public void onAuthorizationBtnClick(ActionEvent actionEvent) {
//        //если введенные логин и пароль корректны
//        if(isLoginPasswordCorrect(login.getText(), password.getText())){
//            //записываем введенные логин и пароль в соответствующие переменные главного контроллера
//            backController.setLogin(login.getText());
//            backController.setPassword(password.getText());
//            //устанавливаем флаг штатного закрытия окна
//            backController.setFlagWindow(true);
//            //запускаем процесс авторизации
//            backController.startAuthorisation();
//            //закрываем окно
//            globParent.getScene().getWindow().hide();
//        }
//    }
    public void onAuthorizationBtnClick(ActionEvent actionEvent) {
        //если введенные логин и пароль корректны
        if(isLoginPasswordCorrect(login.getText(), password.getText())){
            //запускаем процесс авторизации
            backController.demandAuthorisation(login.getText(), password.getText());
            //закрываем окно
            globParent.getScene().getWindow().hide();
        }
    }

    /**
     * Метод проверяет корректность введенной пары - логин и пароль
     * @param login - введенные логин
     * @param password - введенные пароль
     * @return - результат проверки корректности введенной пары - логин и пароль
     */
    private boolean isLoginPasswordCorrect(String login, String password){

        System.out.println("LoginController.isLoginPasswordCorrect() - login: " + login
                + ", password: " + password);
        //FIXME усилить проверку
        return !login.trim().isEmpty() && !password.trim().isEmpty();
    }

    /**
     * Метод проверяет корректность введенных данных в регистрационной форме.
     * @param login - введенный логин
     * @param password - введенный пароль
     * @param passwordConfirm - введенный второй раз пароль
     * @return - результат проверки корректности введенных данных в регистрационной форме
     */
    private boolean isRegistrationDataCorrect(String login, String password, String passwordConfirm){

        System.out.println("LoginController.isLoginPasswordCorrect() - login: " + login
                + ", password: " + password + ", passwordConfirm: " + passwordConfirm);
        //FIXME усилить проверку
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

    /**
     * Метод очистки полей в регистрационной/авторизационной форме.
     */
    private void clearRegAuthFields(){
        login.setText("");
        password.setText("");
        passwordConfirm.setText("");
    }

}
