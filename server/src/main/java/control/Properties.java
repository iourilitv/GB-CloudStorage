package control;

/**
 * This class is a storage for the app's properties.
 */
public class Properties {
    private final int PORT_DEFAULT = 8189;
    private final String ROOT_DEFAULT = "storage";

    private int port_custom;
    private String root_absolute = "";

    public int getPORT_DEFAULT() {
        return PORT_DEFAULT;
    }

    public String getROOT_DEFAULT() {
        return ROOT_DEFAULT;
    }

    public int getPort_custom() {
        return port_custom;
    }

    public void setPort_custom(int port_custom) {
        this.port_custom = port_custom;
    }

    public String getRoot_absolute() {
        return root_absolute;
    }

    public void setRoot_absolute(String root_absolute) {
        this.root_absolute = root_absolute;
    }

    @Override
    public String toString() {
        return "Properties{" +
                "PORT_DEFAULT=" + PORT_DEFAULT +
                ", ROOT_DEFAULT='" + ROOT_DEFAULT + '\'' +
                ", port_custom=" + port_custom +
                ", root_absolute='" + root_absolute + '\'' +
                '}';
    }
}
//ATTENTION!!!
//"root_absolute" should be used only with "\\" instead of "\"!
//for instance:
//D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target>java -jar CloudStorageServer_LYS-jar-with-dependencies.jar
//Properties.setConfiguration() - fromJsonString: {"PORT_DEFAULT":8189,"ROOT_DEFAULT":"storage","port_custom":9999,"root_absolute":"D:\\GeekBrains\\20191130_GB-Разработка_сетевого_хранилища_на_Java\\cloudstorage\\server\\target"}
//Properties.readConfiguration() - properties.toString()Properties{PORT_DEFAULT=8189, ROOT_DEFAULT='storage', port_custom=9999, root_absolute='D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target'}
//CloudStorageClient() - PORT: 9999
//CloudStorageClient() - STORAGE_ROOT_PATH: D:\GeekBrains\20191130_GB-Разработка_сетевого_хранилища_на_Java\cloudstorage\server\target