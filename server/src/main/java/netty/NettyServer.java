package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import control.CloudStorageServer;

public class NettyServer {
    //принимаем объект контроллера сетевого хранилища
    private final CloudStorageServer storageServer;
    //принимаем константу порта
    private final int port;

    public NettyServer(CloudStorageServer storageServer, int port) {
        this.storageServer = storageServer;
        this.port = port;
    }

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
                                    //сериализатор netty объекта сообщения в исходящии поток байтов
                                    new ObjectEncoder(),
                                    //входящий обработчик объектов-сообщений(команд) на авторизацию клиента(пользователя)
                                    new AuthGateway(storageServer),
                                    //входящий обработчик объектов-сообщений(команд) по управлению сетевым хранилищем
                                    new CommandMessageManager(storageServer)
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
        } finally {
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void onConnectionReady(ChannelFuture future) {
        printMsg("Server running...");
    }

    public void printMsg(String msg){
        storageServer.printMsg(msg);
    }
}
