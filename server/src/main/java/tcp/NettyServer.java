package tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.PrintStream;
import java.util.ArrayList;

public class NettyServer {
    //инициируем константу порта сервера
    private final int port = 8189;
    //инициируем переменную для печати сообщений в консоль
    private final PrintStream log = System.out;
    //создадим экземпляр ссылочного массива(список) установленных соединенией
    private final ArrayList<TCPConnection1> connections = new ArrayList<>();
    //инициируем строку названия директории облачного хранилища(сервера) для хранения файлов клиента
    private final String storageRoot = "storage/server_storage";

    public void run() throws Exception {
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
                                    //сериализатор netty объекта сообщения в исходящих поток байтов
                                    new ObjectEncoder(),
                                    //обработчик входных объектов-сообщений после декодера объектов
                                    new ServerInboundHandler(NettyServer.this)//TODO
                            );
                        }
                    })
                    //настраиваем опции для обрабатываемых каналов(клиентских соединений?)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //Bind and start to accept incoming connections
            //привязываем(клиента?) и начинаем принимать входящие сообщения
            ChannelFuture future = b.bind(port).sync();

            //если соединение установлено//TODO ?? что записывать в коллекцию?
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

    public ArrayList<TCPConnection1> getConnections() {
        return connections;
    }

    public String getStorageRoot() {
        return storageRoot;
    }

    public void printMsg(String msg){
        log.append(msg).append("\n");
    }
}
