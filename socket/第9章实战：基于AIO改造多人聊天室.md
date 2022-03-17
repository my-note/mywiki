# 第9章实战：基于AIO改造多人聊天室


## ChatServer

服务器端启动

```java


import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private static final String LOCALHOST = "localhost";

    private static final int DEFAULT_PORT = 8000;

    private static final String QUIT = "quit";

    private static final int BUFFER = 1024;

    private static final int THREADPOOL_SIZE = 8;

    private AsynchronousChannelGroup channelGroup;

    private AsynchronousServerSocketChannel serverSocketChannel;

    private int port;

    /**
     * 在线用户列表
     */
    public static final List<ClientHandler> connectedClients = new ArrayList<>();

    public ChatServer(){
        this(DEFAULT_PORT);
    }

    public ChatServer(int port){
        this.port = port;
    }


    /**
     * 关闭资源
     */
    private void close(Closeable closeable){
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void start(){
        ExecutorService executorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
        try {
            //线程池组
            channelGroup = AsynchronousChannelGroup.withThreadPool(executorService);
            serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
            //server监听端口
            serverSocketChannel.bind(new InetSocketAddress(LOCALHOST, DEFAULT_PORT));

            System.out.println("aio服务器启动，监听端口：" + port);
            //持续监听客户端连接
            while (true){
                serverSocketChannel.accept(null, new AcceptHandler(serverSocketChannel));
                //阻塞主线程，不让主线程终止，小技巧
                System.in.read();
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(serverSocketChannel);
        }

    }






}


```

## AcceptHandler

异步处理客户端的连接

```java


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 处理连接的handler
 */
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {

    private AsynchronousServerSocketChannel serverSocketChannel;

    public AcceptHandler(AsynchronousServerSocketChannel serverSocketChannel){
        this.serverSocketChannel = serverSocketChannel;
    }

    /**
     * 成功连接
     */
    @Override
    public void completed(AsynchronousSocketChannel clientChannel, Object attachment) {
        //继续持续监听下一个连接
        if (serverSocketChannel.isOpen()){
            serverSocketChannel.accept(null, this);
        }

        //监听连接的读写信息
        if (clientChannel != null && clientChannel.isOpen()){


            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            ClientHandler clientHandler = new ClientHandler(clientChannel);

            //将新用户添加到在线用户列表中
            addClient(clientHandler, clientChannel);

            //将读到客户端信息写到buffer
            //1. 第一个参数是将从客户端读到的数据保存到buffer中
            //2. 第二个参数的作用是将对象信息传递给处理读写的handler
            //3. 第三个参数是异步处理读写的handler
            clientChannel.read(byteBuffer, byteBuffer, clientHandler);

        }


    }
    /**
     * 用户添加到在线用户列表中
     */
    private synchronized void addClient(ClientHandler clientHandler, AsynchronousSocketChannel clientChannel) {

        ChatServer.connectedClients.add(clientHandler);
        int port = -1;
        try {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) clientChannel.getRemoteAddress();
            port = inetSocketAddress.getPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("客户端【" + port + "】连接成功");

    }


    /**
     * 失败连接
     */
    @Override
    public void failed(Throwable exc, Object attachment) {
        System.out.println("客户端连接失败" + exc);
    }
}


```
## ClientHandler


异步处理客户端的读写

```java

package aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

/**
 * 处理客户端读写的handler
 */
public class ClientHandler implements CompletionHandler<Integer, Object> {

    private AsynchronousSocketChannel clientChannel;

    public ClientHandler(AsynchronousSocketChannel clientChannel){
        this.clientChannel = clientChannel;
    }

    /**
     *
     * @param result 读到客户端发来数据的长度
     * @param attachment 传递给读handler对象信息
     */
    @Override
    public void completed(Integer result, Object attachment) {

        ByteBuffer byteBuffer = (ByteBuffer) attachment;

        if (byteBuffer != null){
            if (result <= 0){
                //客户端异常
                //将客户从在线用户列表中移除
                removeClient(this);
            }else {
                byteBuffer.flip();
                String info = String.valueOf(StandardCharsets.UTF_8.decode(byteBuffer));

                int port = -1;
                try {
                    port = ((InetSocketAddress) clientChannel.getRemoteAddress()).getPort();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                System.out.println("客户端【" + port + "】：" + info);
                //将信息转发给其他在线用户
                forwardMessage(clientChannel, info);
                byteBuffer.clear();

                //quit 下线
                if ("quit".equals(info)){
                    removeClient(this);
                }else {
                    //持续接听
                    clientChannel.read(byteBuffer, byteBuffer, this);
                }
            }
        }


    }
    /**
     * 转发信息到其他的客户端
     */
    private synchronized void forwardMessage(AsynchronousSocketChannel clientChannel, String info) {
        for (ClientHandler handler : ChatServer.connectedClients) {
            if (!handler.clientChannel.equals(clientChannel)){
                int port = -1;
                try {
                    InetSocketAddress remoteAddress = (InetSocketAddress) clientChannel.getRemoteAddress();
                    port = remoteAddress.getPort();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode("客户端【"+port+"】：" + info + "\n");
                handler.clientChannel.write(byteBuffer, null, this);
            }
        }


    }

    private synchronized void removeClient(ClientHandler clientHandler) {
        ChatServer.connectedClients.remove(clientHandler);
        int port = -1;
        try {
            InetSocketAddress remoteAddress = (InetSocketAddress) clientChannel.getRemoteAddress();
            port = remoteAddress.getPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("客户端【" + port + "】下线");
        try {
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, Object attachment) {
        System.out.println("客户端读写失败");
    }
}


```


## ChatClient


客户端连接服务端、读写

```java

 aio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

public class ChatClient {


    private AsynchronousSocketChannel clientChannel;


    public void start(){

        try {
            clientChannel = AsynchronousSocketChannel.open();
            Future<Void> future = clientChannel.connect(new InetSocketAddress("127.0.0.1", 8000));
            future.get();


            //启动新线程处理用户输入
            new Thread(new Runnable() {
                @Override
                public void run() {

                    //获取用户输入信息
                    BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                    String msg = null;
                    try {
                        while (true){

                            String input = consoleReader.readLine();
                            if (input != null && input.length() > 0){

                                //向服务器发送消息
                                send(input);

                                //判断是否退出
                                if ("quit".equals(input)){
                                    break;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
            //读服务器端发来的消息
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            while (true){
                Future<Integer> readResult = clientChannel.read(byteBuffer);
                int result = readResult.get();
                if (result <= 0){
                    //服务器异常了
                    System.out.println("服务器异常了");
                    clientChannel.close();
                    System.exit(1);
                }else {
                    byteBuffer.flip();
                    String s = String.valueOf(StandardCharsets.UTF_8.decode(byteBuffer));
                    byteBuffer.clear();
                    System.out.println(s);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void send(String msg){
        if (msg.isEmpty()){
            return;
        }
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(msg);
        Future<Integer> writeResult = clientChannel.write(byteBuffer);
        try {
             writeResult.get();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("发送信息失败");
        }
    }




}


```

































