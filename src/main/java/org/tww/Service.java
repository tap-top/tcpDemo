package org.tww;

import java.net.ServerSocket;
import java.net.Socket;

public class Service {
    public static void main(String[] args) throws Exception {
        //定义连接服务器的端口号
        int serverport=3000;
        //定义服务器对象
        ServerSocket server=null;
        //定义保存连接到的服务器的客户端对象
        //Socket    accept() -侦听要连接到此套接字并接受它。
        Socket client=null;
        //定义控制持续读取数据的变量
        boolean getflag=true;
        server=new ServerSocket(serverport);
        System.out.println("服务器已经启动，等待客户端连接......");
        while(getflag){
            //accept() 侦听要连接到此套接字并接受它。 【阻塞主线程运行】
            client=server.accept();
            //开辟出控制每一个客户端独立读写的线程
            ThreadClass thc=new ThreadClass(client);
            Thread thread=new Thread(thc);
            thread.start();
            System.out.println("新进入一个连接"+thread);
        }
        server.close();
    }
}