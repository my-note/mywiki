# 第5章实战：基于BIO的多人聊天室设计与实现



### ChatServer

```java

package socket.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 服务端acceptor线程,负责和客户端的连接
 * 添加客户端、移除客户端、启动服务端、关闭服务端、维护在线用户列表、转发消息给其他用户
 */
public class ChatServer {

    private static int DEFAULT_PORT = 8888;

    private static final String QUIT = "quit";
    /**
     * 服务端主线程，负责客户端连接
     */

    private ServerSocket serverSocket;
    /**
     * 当前在线的client
     * key 使用的是 client的socket的port, 用client-socket-port标识每个client用户
     */
    private final Map<Integer, Writer> connectedClients;



    public ChatServer(){
        this.connectedClients = new ConcurrentHashMap<>();
    }

    /**
     * 用户上线
     * 添加客户端到当前在线用户列表
     */
    public synchronized void addClient(Socket socket) throws IOException {

        if (socket != null){
            int port = socket.getPort();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            connectedClients.put(port, bufferedWriter);
            System.out.println("客户端【" + port + "】已近连接到服务器");
        }
    }

    /**
     * 用户下线
     * 从当前在线用户列表中移除用户
     */
    public synchronized void removeClient(Socket socket) throws IOException {

        if (socket != null){
            int port = socket.getPort();
            if (connectedClients.containsKey(port)){
                connectedClients.get(port).close();
            }
            connectedClients.remove(port);
            System.out.println("客户端【" + port + "】已近断开连接");
        }
    }

    /**
     * 转发消息给其他的用户
     */
    public synchronized void forwardMessage(Socket sourceSocket, String msg) throws IOException {
        for (Integer port : connectedClients.keySet()) {
            //转发msg给其他在线用户（不包含自己）
            if (!port.equals(sourceSocket.getPort())){
                Writer writer = connectedClients.get(port);
                writer.write(msg);
                writer.flush();
            }
        }
    }


    /**
     * 关闭服务端
     */
    public synchronized void close(){
        if (this.serverSocket != null){
            try {
                serverSocket.close();
                System.out.println("关闭了ServerSocket");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 检查用户是否要退出
     */
    public boolean readyToQuit(String msg){
        return QUIT.equals(msg);
    }

    /**
     * 服务器端主线程启动
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("服务器启动成功,端口: " + DEFAULT_PORT);
            while (true){
                //阻塞等待客户端连接。。。
                Socket socket = serverSocket.accept();
                //创建chatHandler线程，每个连接由一个单独的线程负责
                new Thread(new ChatHandler(this, socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("服务器启动失败");
        } finally {
            close();
        }


    }






}


```

### ChatHandler

```java
package socket.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * 由单独的线程来运行ChatHandler负责和client1对1交互
 */
public class ChatHandler implements Runnable {

    /**
     * 服务端socket
     */
    private ChatServer chatServer;

    /**
     * 当前用户的客户端socket
     */
    private Socket socket;

    public ChatHandler(ChatServer chatServer, Socket socket){
        this.chatServer = chatServer;
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            //存储新上线用户
            chatServer.addClient(socket);

            //读取用户发送的消息
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = null;
            //阻塞读，指导有转行符号“\n”
            while ((line = reader.readLine()) != null){
                System.out.println("客户端【" + socket.getPort() + "】：" + line);
                //将收到的消息转发给聊天室里其他的用户
                chatServer.forwardMessage(socket, "客户端【" + socket.getPort() + "】：" + line + "\n");

                //检查用户是否退出消息
                if (chatServer.readyToQuit(line)){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                chatServer.removeClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}


```




### ChatClient


```java

package socket.client;

import java.io.*;
import java.net.Socket;

public class ChatClient {


    private static final String DEFAULT_SERVER_HOST = "127.0.0.1";

    private static final int DEFAULT_SERVER_PORT = 8888;

    private static final String QUIT = "quit";

    private Socket socket;

    private BufferedReader reader;

    private BufferedWriter writer;

    public ChatClient(){
        try {
            socket = new Socket(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("和服务器端建立连接成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息给服务器
     */
    public void send(String msg) throws IOException {
        if (!socket.isOutputShutdown()){
            writer.write(msg + "\n");
            writer.flush();
        }
    }

    /**
     * 接受消息
     */
    public String receive() throws IOException {
        String msg = null;
        if (!socket.isInputShutdown()){
            msg = reader.readLine();
        }
        return msg;
    }
    /**
     * 检查用户是否退出
     */
    public boolean readyToQuit(String msg){
        return QUIT.equals(msg);
    }

    /**
     * 关闭socket和释放资源
     */
    public void close(){
        if (writer != null){
            try {
                System.out.println("关闭socket");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start(){

        //子线程发消息：处理用户的输入
        new Thread(new UserInputHandler(this)).start();

        //主线程：读取服务器转发的消息
        try {
            String msg = null;
            while ((msg = receive()) != null){
                System.out.println(msg);
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            close();
        }

    }



}


```
### UserInputHandler

```java
package socket.client;

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


### 控制客户端的连接数量

通过固定数量的线程池（线程数固定可以控制客户端的连接数）

服务端压力不止于过大，避免频繁的创建和销毁线程，也可以防止内存的OOM

```java

/**
 * 通过线程池控制客户端的连接数
 */
private ExecutorService executorService;



public ChatServer(){
    executorService = Executors.newFixedThreadPool(10);
    this.connectedClients = new ConcurrentHashMap<>();
}


//new Thread(new ChatHandler(this, socket)).start();
executorService.execute(new ChatHandler(this, socket));

```




















































