import tcp.NettyServer;
//import tcp.TCPServer;

/**
 * The main class of server cloudstorage applet.
 */
public class Server {
//    public static void main(String[] args) {
//        new TCPServer();
//    }
    public static void main(String[] args) throws Exception {
        new NettyServer().run();
    }
}