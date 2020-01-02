import javafx.GUIController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A sample of using JavaFX features.
 * List of chat participants with images;
 * Table of users with columns: name, e-mail;
 * Listview of strings;
 * Drag'drop area to get a file pathname;
 * Button to print to console a chosen element of listview;
 * Button to able / disable the actions of the print element button;
 * Button "Show Alert" to open a window with an alert message that requires a confirmation on it;
 * Button "Show Modal" to open a window for authorization(login, password text fields and
 * a Authorization button);
 * Button "Show 2 Scene Stage" to open a window with a "SCENE 1/2" label and
 * a "Switch to Scene 2/1" button.
 * @author Alexander Fisunov
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        Parent root = fxmlLoader.load();
        GUIController controller = fxmlLoader.getController();
        // Parent root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("JavaFX CookBook");
        Scene scene = new Scene(root, 800, 800);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
