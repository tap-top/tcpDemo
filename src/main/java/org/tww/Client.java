package org.tww;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception {
        //接受控制台数据的输入流
        BufferedReader buff=null;
        //定义保存服务器地址的对象【String】
        InetAddress serverip=null;
        //定义连接服务器的端口号
        int serverport=20001;
        //定义创建客户端对象的Socket
        Socket client=null;
        //定义发送信息的输出流对象
        OutputStream sout=null;
        //定义接收数据的输入流
        InputStream input=null;
        //定义保存被发送的数据
        String info=null;
        //定义持续输出的变量
        boolean flag=true;
        //定义保存客户端发送来的数据的字节数组
        byte data[]=new byte[1024];

        //返回包含有本机IP地址的InetAddress对象
        serverip=InetAddress.getByName("192.168.1.1");
        //Socket(InetAddress address,int prot)---创建流套接字并将其连接到指定IP地址的指定端口号
        client=new Socket(serverip,serverport);
        // getOutputStream()---返回客户端的输出流。【与服务器的输入流连接】
        sout=client.getOutputStream();
        input=client.getInputStream();
        buff=new BufferedReader(new InputStreamReader(System.in));
        while(flag){
            //GetVersion;\r\n
            System.out.println("请输入发送的数据");
            info=buff.readLine();
            System.out.println("录入的信息是"+info);
            info = info + "\r\n";
            System.out.println("录入的bytes信息是"+info.getBytes());

            sout.write(info.getBytes());

//            sout.write("GetVersion;\r\n".getBytes());

            int len=input.read(data);
            //将读取来保存在字节数组中的数据转换成字符串
            String str=new String(data,0,len);
            System.out.println("服务器接受的信息是---"+str);
            if(info.equals("exit")){
                flag=false;
            }
        }
        input.close();
        sout.close();
        buff.close();
        client.close();
    }
}