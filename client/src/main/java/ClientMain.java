import javafx.GUIController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main class of client cloudStorage applet.
 */
public class ClientMain extends Application {

    //создаем экземпляр контроллера
    GUIController contr;

    @Override
    public void start(Stage primaryStage) throws Exception{
        //чтобы получить доступ к контроллеру
        //лоадер вынесли отдельно, чтобы с ним удобнее было работать
        FXMLLoader loader = new FXMLLoader();
        //с помощью метода getResourceAsStream извлекаем данные из лоадера, чтобы
        //вызвать метод getController для получения контроллера
        Parent root = loader.load(getClass().getResourceAsStream("/MainClient.fxml"));
        contr = loader.getController();

        //определяем действия по событию закрыть окно по крестику через лямбда
        //лямбда здесь - это замена анонимного класса типа new Runnable
        //в лямбда event - аргумент(здесь некое событие), {тело лямбды - операции}
        primaryStage.setOnCloseRequest(event -> {
            contr.dispose();//dispose - располагать, размещать
            //сворачиваем окно
            Platform.exit();
            //указываем системе, что выход без ошибки
            System.exit(0);
        });

        primaryStage.setTitle("The Cloud Storage by LYS");
        Scene scene = new Scene(root, 1024, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}