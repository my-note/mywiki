# 第7章实战：使用NIO改造多人聊天室


## ChatServer

```java

package nio.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;


/**
 * 服务端acceptor线程,负责和客户端的连接
 * 添加客户端、移除客户端、启动服务端、关闭服务端、维护在线用户列表、转发消息给其他用户
 */
public class ChatServer {

    private static final int DEFAULT_PORT = 8000;

    private static final String QUIT = "quit";

    private static final int BUFFER = 1024;

    private ServerSocketChannel server;

    private Selector selector;

    private ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER);

    private ByteBuffer writeBuffer = ByteBuffer.allocate(BUFFER);

    private final int port;

    public ChatServer(){
        this(DEFAULT_PORT);
    }

    public ChatServer(int port){
        this.port = port;
    }

    /**
     * 启动入口
     */
    public void start(){

        try {
            server = ServerSocketChannel.open();
            //非阻塞模式channel
            server.configureBlocking(false);
            //绑定端口
            server.socket().bind(new InetSocketAddress(port));
            selector = Selector.open();
            //注册channel连接事件
            server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("启动服务器，监听端口：" + port + "...");
            //无事件就阻塞,循环取事件
            while (true){
                selector.select();
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                for (SelectionKey key : selectionKeySet) {
                    //处理被触发的事件
                    handlers(key);
                }
                selectionKeySet.clear();
            }

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("服务器启动异常");
        }finally {
            System.out.println("关闭serverSocketChannel");
            //只需要关闭selector就连带关闭注册在selector上的server和channel
            close(selector);
        }
    }

    /**
     * 处理事件
     */
    private void handlers(SelectionKey key) throws IOException {
        // ACCEPT事件 - 和客户端建立连接
        if(key.isAcceptable()){
            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
            SocketChannel clientChannel = server.accept();
            clientChannel.configureBlocking(false);
            //连接事件注册读事件
            clientChannel.register(selector, SelectionKey.OP_READ);
            System.out.println("客户端【" + clientChannel.socket().getPort() + "】已经连接上了");
        }
        // READ事件 - 客户端发送了消息
        else if (key.isReadable()) {
            SocketChannel client = (SocketChannel) key.channel();
            String info = receive(client);

            //客户端异常
            if (info.isEmpty()){
                //不再监听该事件
                key.cancel();
                //事件变动了，通知selecotr重新审视
                selector.wakeup();
            }else {
                //用户下线
                if (readyToQuit(info)){
                    key.cancel();
                    selector.wakeup();
                    System.out.println("客户端【" + client.socket().getPort() + "】已下线");
                }


                System.out.println("收到客户端【" + client.socket().getPort() + "】消息：" + info);
                //转发信息给其他的客户端
                forwardMessage(client, "客户端【" + client.socket().getPort() + "】：" + info);
            }

        }

    }

    private void forwardMessage(SocketChannel client, String s) throws IOException {
        //所有注册在 selector 上的事件
        Set<SelectionKey> keys = selector.keys();
        for (SelectionKey key : keys) {

            SelectableChannel connectedChannel = key.channel();

            if (connectedChannel instanceof ServerSocketChannel){
                continue;
            }
            if (key.isValid() && !client.equals(connectedChannel)){
                writeBuffer.clear();
                writeBuffer.put(StandardCharsets.UTF_8.encode(s + "\n"));
                writeBuffer.flip();
                while (writeBuffer.hasRemaining()){
                    ((SocketChannel)connectedChannel).write(writeBuffer);
                }
            }
        }


    }

    /**
     * 从客户端通道上读取消息
     */
    private String receive(SocketChannel client) throws IOException {

        readBuffer.clear();
        while (client.read(readBuffer) > 0);
        readBuffer.flip();
        return String.valueOf(StandardCharsets.UTF_8.decode(readBuffer));

    }

    /**
     * 是否退出
     */
    private boolean readyToQuit(String info){
        return QUIT.equals(info);
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



}


```


## ChatClient

```java

package nio.server;


import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class ChatClient {

    private static final String DEFAULT_SERVER_HOST = "127.0.0.1";

    private static final int DEFAULT_SERVER_PORT = 8000;

    private static final String QUIT = "quit";

    private String host;

    private Integer port;

    private SocketChannel socketChannel;

    private final ByteBuffer readBuffer = ByteBuffer.allocate(1024);

    private final ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

    private Selector selector;

    public ChatClient(String host, Integer port){
        this.host = host;
        this.port = port;
    }

    public ChatClient(){
        this(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);
    }

    public boolean readyToQuit(String info){
        return QUIT.equals(info);
    }

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
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

            selector = Selector.open();
            //client上selector监听的是connect事件
            socketChannel.register(selector, SelectionKey.OP_CONNECT);


            //开始连接
            socketChannel.connect(new InetSocketAddress(host, port));

            while (true){
                selector.select();
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                for (SelectionKey key : selectionKeySet) {
                    handlers(key);

                }
                //处理完成以后清空
                selectionKeySet.clear();
            }


        }catch (Exception e){
            e.printStackTrace();
            System.out.println("客户端连接异常");
        }finally {
            close(socketChannel);
        }
    }

    private void handlers(SelectionKey key) throws IOException {



        // connect事件 -- 连接就绪事件
        if (key.isConnectable()){

            SocketChannel socketChannel = (SocketChannel) key.channel();
            if (socketChannel.isConnectionPending()){
                socketChannel.finishConnect();
                // 处理用户输入信息
                new Thread(new UserInputHandler(this)).start();
            }
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);

        }
        // read事件 -- 服务器转发消息
        else if(key.isReadable()){
            SocketChannel socketChannel = (SocketChannel) key.channel();
            String msg = receive(socketChannel);
            if (msg.isEmpty()){
                //服务器端异常
                close(selector);
            } else {
                System.out.println(msg);
            }

        }
    }

    private String receive(SocketChannel socketChannel) throws IOException {

        readBuffer.clear();

        while (socketChannel.read(readBuffer) > 0);
        readBuffer.flip();
        return String.valueOf(StandardCharsets.UTF_8.decode(readBuffer));


    }


    /**
     * 发送消息给服务端
     */
    public void send(String input) throws IOException {
        //如果是空什么也不干 直接退出
        if (null == input || input.length() == 0){
            return;
        }
        //将内容写入到bytebuffer
        writeBuffer.clear();
        writeBuffer.put(StandardCharsets.UTF_8.encode(input));
        writeBuffer.flip();
        //将bytebuffer内容写入到channel
        while (writeBuffer.hasRemaining()){
            socketChannel.write(writeBuffer);
        }

        //检查用户是否准备退出
        if (readyToQuit(input)){
            close(selector);
            System.out.println("客户端已退出");
        }
    }


}


```

## UserInputHandler

```java


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 处理用户输入
 */
public class UserInputHandler implements Runnable {


    private ChatClient chatClient;

    public UserInputHandler(ChatClient chatClient){
        this.chatClient = chatClient;
    }


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
                    chatClient.send(input);

                    //判断是否退出
                    if (chatClient.readyToQuit(input)){
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

```
































