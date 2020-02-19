import control.CloudStorageServer;
import control.PropertiesHandler;
import utils.FileManager;

/**
 * The main class of server cloudstorage applet.
 */
public class ServerMain {

//    public static void main(String[] args) throws Exception {
//        CloudStorageServer css = new CloudStorageServer();
//        css.initConfiguration();
//        css.run();
//    }

    //TODO TEST
    //инициируем объект хендлера настроек приложения
    private static final PropertiesHandler propertiesHandler = PropertiesHandler.getOwnObject();
//    public static void main(String[] args) {
//        propertiesHandler.getResourceFromJar71();
//    }

    //TODO TEST
    public static void main(String[] args) {
        //Variant #6.4. OK!
        // (using [shared] utils/FileManager and
        // the file in the [server]src/main/resources/utils/)
        new FileManager().copyFileToRuntimeRoot64("readme2.txt");
        //see at [shared]FileManager.copyFileToRuntimeRoot64()
    }
}