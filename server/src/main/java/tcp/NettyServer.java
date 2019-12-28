package tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import utils.CloudStorageServer;
import utils.CommandMessageManager1;
import utils.handlers.UsersAuthController;

import java.nio.file.Path;

public class NettyServer {
    //принимаем объект контроллера сетевого хранилища
    private final CloudStorageServer storageServer;
    //принимаем константу порта
    private final int port;

    public NettyServer(CloudStorageServer storageServer, int port) {
        this.storageServer = storageServer;
        this.port = port;
    }

//    public enum Enum {
//        //инициируем константу неавторизованного подключившегося пользователя
//        UNKNOWN_USER;
//    }

//    //инициируем константу порта сервера
//    private final int PORT = 8189;
//    //инициируем переменную для печати сообщений в консоль
//    private final PrintStream log = System.out;

//    //создадим экземпляр ссылочного массива(список) установленных соединенией
//    private final ArrayList<TCPConnection1> connections = new ArrayList<>();

//    //инициируем строку названия директории облачного хранилища(сервера) для хранения файлов клиента
//    private final String storageRoot = "storage/server_storage";

//    //инициируем объект пути к корневой директории облачного хранилища(сервера) для хранения файлов клиентов
//    private final Path storageRoot = Paths.get("storage","server_storage");

//    //объявляем объект контроллера авторизации клиента
//    private UserController userController;

    public void run() throws Exception {
//        //инициируем объект сервисного хендлера
//        userController = new UsersAuthController(this);

        //инициируем пул потоков для приема входящих подключений
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        //инициируем пул потоков для обработки потоков данных
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //позволяет настроить сервер перед запуском
            ServerBootstrap b = new ServerBootstrap();
            //в параметрах mainGroup - parentGroup, workerGroup - childGroup
            b.group(mainGroup, workerGroup)
                    //Указываем использование класса NioServerSocketChannel для создания канала после того,
                    //как принято входящее соединение.
                    .channel(NioServerSocketChannel.class)
                    //Указываем обработчики, которые будем использовать для открытого канала (Channel или SocketChannel?) .
                    //ChannelInitializer помогает пользователю сконфигурировать новый канал
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel)/* throws Exception*/ {
                            //наполняем трубу обработчиками сообщений(потоков данных)
                            // для входящих - слева направо, для исходящих справа налево
                            socketChannel.pipeline().addLast(
                                    //десериализатор netty входящего потока байтов в объект сообщения
                                    new ObjectDecoder(50 * 1024 * 1024, ClassResolvers.cacheDisabled(null)),
                                    //сериализатор netty объекта сообщения в исходящии поток байтов
                                    new ObjectEncoder(),
                                    //входящий обработчик объектов-сообщений(команд) на авторизацию клиента(пользователя)
                                    new AuthGateway(storageServer, NettyServer.this),
                                    //входящий обработчик объектов-сообщений(команд) по управлению сетевым хранилищем
                                    new CommandMessageManager1(storageServer, NettyServer.this)
                            );
                        }
                    })
                    //настраиваем опции для обрабатываемых каналов(клиентских соединений?)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //Bind and start to accept incoming connections
            //привязываем(клиента?) и начинаем принимать входящие сообщения
            ChannelFuture future = b.bind(port).sync();

            //если соединение установлено
            onConnectionReady(future);

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully

            // shut down your server
            future.channel().closeFuture().sync();

//            //если соединение прервано//TODO НЕ выводится
//            onDisconnect(future);

        } finally {
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void onConnectionReady(ChannelFuture future) {
//        //если соединение установлено, то добавляем его в список
//        connections.add(future);

//        //TODO temporarily
//        printMsg("NettyServer.onConnectionReady() - connections.size(): " + connections.size()
//                + ", connections.toString(): " + connections.toString());

        printMsg("Server running...");

    }

//    public void onDisconnect(ChannelFuture future) {
//        //если соединение отвалилось, то удаляем его из списка
//        connections.remove(future);
//
//        //TODO temporarily
//        printMsg("NettyServer.onDisconnect() - connections.size(): " + connections.size()
//                + ", connections.toString(): " + connections.toString());
//
//    }

//    public ArrayList<TCPConnection1> getConnections() {
//        return connections;
//    }

//    public String getStorageRoot() {
//        return storageRoot;
//    }

//    public Path getStorageRoot() {
//        return storageRoot;
//    }
//
//    public UsersAuthController getUserController() {
//        return userController;
//    }

    //    public TCPConnection1 findTCPConnection(ChannelHandlerContext ctx){
//        for (TCPConnection1 c: connections) {
//            if(c.getCtx().equals(ctx)){
//                return c;
//            }
//        }
//        return null;
//    }

    public void printMsg(String msg){
        storageServer.printMsg(msg);
    }
}
