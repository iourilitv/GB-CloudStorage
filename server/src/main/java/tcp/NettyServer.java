package tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.PrintStream;
import java.util.ArrayList;

public class NettyServer {
    //инициируем переменную для печати сообщений в консоль
    private final PrintStream log = System.out;
    //создадим экземпляр ссылочного массива(список) установленных соединенией
    private final ArrayList<ChannelFuture> connections = new ArrayList<>();
    //инициируем строку названия директории облачного хранилища(сервера) для хранения файлов клиента
    private final String storageRoot = "storage/server_storage";

    public void run() throws Exception {
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(mainGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel)/* throws Exception*/ {
                            socketChannel.pipeline().addLast(
                                    new ObjectDecoder(50 * 1024 * 1024, ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new ServerInboundHandler(NettyServer.this)//TODO
                            );
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = b.bind(8189).sync();

            //если соединение установлено//TODO
            onConnectionReady(future);

            future.channel().closeFuture().sync();

            //если соединение прервано//TODO
            onDisconnect(future);

        } finally {
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void onConnectionReady(ChannelFuture future) {
        //если соединение установлено, то добавляем его в список
        connections.add(future);

        //TODO temporarily
        printMsg("NettyServer.onConnectionReady() - connections.size(): " + connections.size()
                + ", connections.toString(): " + connections.toString());

    }

    public void onDisconnect(ChannelFuture future) {
        //если соединение отвалилось, то удаляем его из списка
        connections.remove(future);

        //TODO temporarily
        printMsg("NettyServer.onDisconnect() - connections.size(): " + connections.size()
                + ", connections.toString(): " + connections.toString());

    }

    public String getStorageRoot() {
        return storageRoot;
    }

    public void printMsg(String msg){
        log.append(msg).append("\n");
    }
}
