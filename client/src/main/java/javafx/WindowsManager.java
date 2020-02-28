package javafx;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.Item;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is responsible for operations with modal windows in GUI.
 */
public class WindowsManager {
    private static final WindowsManager ownInstance = new WindowsManager();

    public static WindowsManager getInstance() {
        return ownInstance;
    }

    private GUIController guiController;
    //объявляем переменную стадии приложения
    private Stage stage;
    //объявляем объект контроллера окна регистрации
    private RegistrationController registrationController;
    //объявляем объект контроллера окна авторизации
    private AuthorisationController authorisationController;
    //объявляем объект контроллера окна изменения пароля пользователя
    private ChangePasswordController changePasswordController;

    public void init(GUIController guiController){
        this.guiController = guiController;
    }

    /**
     * Перегруженный метод открывает модальное окно для ввода логина и пароля пользователя.
     */
    void openAuthorisationWindow() {
        //вызываем перегруженный метод с пустыми логином и паролем
        openAuthorisationWindow("", "");
    }

    /**
     * Перегруженный метод открывает модальное окно для ввода логина и пароля пользователя.
     * @param login - логин пользователя
     * @param password - пароль пользователя
     */
    void openAuthorisationWindow(String login, String password) {
        //выводим сообщение в нижнюю метку GUI
        guiController.showTextInGUI("Server has connected, insert login and password.");

        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/authorisation.fxml"));
            Parent root = loader.load();
            authorisationController = loader.getController();
            //сохраняем ссылку на контроллер открываемого окна авторизации/регистрации
            authorisationController.setBackController(guiController);
            //устанавливаем значения из формы регистрации
            authorisationController.setLoginString(login);
            authorisationController.setPasswordString(password);

            //определяем действия по событию закрыть окно по крестику через лямбда
            stage.setOnCloseRequest(event -> {
                //вызываем разрыв соединения, если выйти по крестику
                guiController.setAuthorizedMode(false);
            });

            stage.setTitle("Authorisation to the Cloud Storage by LYS");
            stage.setScene(new Scene(root, 300, 200));
            stage.isAlwaysOnTop();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод открывает модальное окно с регистрационной формой пользователя.
     */
    void openRegistrationWindow() {
        //выводим сообщение в нижнюю метку GUI
        guiController.showTextInGUI("Insert your data to get registration please.");

        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/registration_form.fxml"));
            Parent root = loader.load();
            registrationController = loader.getController();
            //сохраняем ссылку на контроллер открываемого окна авторизации/регистрации
            registrationController.setBackController(guiController);

            //определяем действия по событию закрыть окно по крестику через лямбда
            stage.setOnCloseRequest(event -> {
                //вызываем разрыв соединения, если выйти по крестику
                guiController.setAuthorizedMode(false);
            });

            stage.setTitle("Registration to the Cloud Storage by LYS");
            stage.setScene(new Scene(root, 300, 300));
            stage.isAlwaysOnTop();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** //FIXME Убрать задвоение - вызвать универсальный метод открытия модального окна
     * Метод открывает модальное окно с формой изменения пароля.
     */
    void openChangingPasswordWindow() {
        //выводим сообщение в нижнюю метку GUI
        guiController.showTextInGUI("Fill a Changing Password Form please.");
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/changePassword.fxml"));
            Parent root = loader.load();
            changePasswordController = loader.getController();
            //сохраняем ссылку на контроллер открываемого окна авторизации/регистрации
            changePasswordController.setBackController(guiController);

            //определяем действия по событию закрыть окно по крестику через лямбда
            stage.setOnCloseRequest(event -> {
                //вызываем разрыв соединения, если выйти по крестику
                guiController.setAuthorizedMode(true);
            });

            stage.setTitle("Changing Password Form to the Cloud Storage by LYS");
            stage.setScene(new Scene(root, 300, 200));
            stage.isAlwaysOnTop();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** //FIXME Убрать задвоение - вызвать перегруженный метод openNewNameWindow(Item origin)
     * Перегруженный метод открывает модальное окно для ввода нового имени элемента списка.
     */
    void openNewNameWindow() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rename.fxml"));
            Parent root = loader.load();
            RenameController renameController = loader.getController();//TODO вынести в класс
            //запоминаем текущий контроллер для возврата
            renameController.setBackController(guiController);

            //определяем действия по событию закрыть окно по крестику через лямбда
            stage.setOnCloseRequest(event -> {
                writeToLog("GUIController.menuItemRename() - " +
                        "the newNameWindow was closed forcibly!");
                //сбрасываем текстовое поле имени
                guiController.setNewName("");
            });

            stage.setTitle("insert a new name");
            stage.setScene(new Scene(root, 200, 50));
            stage.isAlwaysOnTop();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Перегруженный метод открывает модальное окно для ввода нового имени элемента списка.
     * @param origin - объект элемента - оригинал
     * @return false - если закрыть окно принудительно, true - при штатном вводе
     */
    boolean openNewNameWindow(Item origin) {
        AtomicBoolean flag = new AtomicBoolean(false);
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rename.fxml"));
            Parent root = loader.load();
            RenameController renameController = loader.getController();//TODO вынести в класс
            //запоминаем текущий контроллер для возврата
            renameController.setBackController(guiController);
            //записываем текущее имя в текстовое поле
            renameController.setNewNameString(origin.getItemName());

            //определяем действия по событию закрыть окно по крестику через лямбда
            stage.setOnCloseRequest(event -> {
                writeToLog("GUIController.menuItemRename() - " +
                        "the newNameWindow was closed forcibly!");
                //сбрасываем флаг
                flag.set(false);
                //сбрасываем текстовое поле имени
                guiController.setNewName("");
            });

            stage.setTitle("insert a new name");
            stage.setScene(new Scene(root, 200, 50));
            stage.isAlwaysOnTop();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Метод открывает сцену с информацией о программе.
     */
    void openAboutScene() {
        //получаем объект стадии приложения
        stage = (Stage) guiController.getNoticeLabel().getScene().getWindow();
        //сохраням объект гланой сцены
        Scene primaryScene = guiController.getNoticeLabel().getScene();
        //инициируем элементы сцены
        VBox pane = new VBox(10);
        pane.setAlignment(Pos.TOP_CENTER);
        Label label = new Label(new AboutText().getText());
        label.setStyle("-fx-font-size: 14");
        label.setWrapText(true);
        Button backBtn = new Button("Return");
        //устанавливаем лисенер на кнопку, чтобы вернуться с основной сцене
        backBtn.setOnAction(e -> stage.setScene(primaryScene));
        pane.getChildren().addAll(label, backBtn);
        Scene aboutScene = new Scene(pane, 410, 220);
        stage.setScene(aboutScene);
    }

    public Stage getStage() {
        return stage;
    }

    public RegistrationController getRegistrationController() {
        return registrationController;
    }

    public AuthorisationController getAuthorisationController() {
        return authorisationController;
    }

    public ChangePasswordController getChangePasswordController() {
        return changePasswordController;
    }

    private void writeToLog(String msg){
        guiController.writeToLog(msg);
    }
}
