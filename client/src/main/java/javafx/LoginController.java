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

    //главный контроллер GUI
    public GUIController backController;

    /**
     * Метод обрабатывает клик мыши по кнопке "Authorization" в диалоговом окне ввода нового имени
     * @param actionEvent - клик мыши по кнопке "Authorization"
     */
    public void auth(ActionEvent actionEvent) {
        //если введенные логин и пароль корректны
        if(isLoginPasswordCorrect(login.getText(), password.getText())){
            //записываем введенные логин и пароль в соответствующие переменные главного контроллера
            backController.setLogin(login.getText());
            backController.setPassword(password.getText());
            //запускаем процесс авторизации
            backController.startAuthorisation();
            globParent.getScene().getWindow().hide();
        }
    }

    //FIXME
    private boolean isLoginPasswordCorrect(String login, String password){

        System.out.println("LoginController.isLoginPasswordCorrect() - login: " + login
                + ", password: " + password);

        return !login.trim().isEmpty() && !password.trim().isEmpty();
    }

    //FIXME
    public void onRegistrationLink(ActionEvent actionEvent) {
        System.out.println("LoginController.onRegistrationLink() - get registration");
    }

}
