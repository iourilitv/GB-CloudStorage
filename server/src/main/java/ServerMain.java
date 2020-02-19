import control.CloudStorageServer;

/**
 * The main class of server cloudstorage applet.
 */
public class ServerMain {

    public static void main(String[] args) throws Exception {
        CloudStorageServer css = new CloudStorageServer();
        css.initConfiguration();
        css.run();
    }

}