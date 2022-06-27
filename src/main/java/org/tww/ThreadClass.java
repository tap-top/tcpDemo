package org.tww;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ThreadClass implements Runnable{
    //定义创建客户端对象的Socket
    private Socket client=null;
    //定义服务器接收客户端信息的输入流
    private InputStream in=null;
    //定义写出数据的输出流对象
    private OutputStream out=null;
    //定义控制持续读取数据的变量
    private boolean flag=true;
    public ThreadClass(Socket client){
        this.client=client;
    }
    public void run() {
        try {
            in=client.getInputStream();
            out=client.getOutputStream();
            //定义保存客户端发送来的数据的字节数组
            byte data[]=new byte[1024];
            while(flag){
                int len=in.read(data);
                //将读取来的数据转换成字符串
                String str=new String(data,0,len);
                System.out.println("服务器接收的信息是---"+str);
                if(str.equals("exit")){
                    flag=false;
                }else {
                    str="server--"+str;
                }
                out.write(str.getBytes());
            }
            out.close();
            in.close();
            client.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}