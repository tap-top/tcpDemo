package org.tww;

public class Main {
    public static void main(String[] args) throws UnknownHostException, IOException {
        /*创建客服端，及客服端接受信息的地址及端口*/
        Socket client = new Socket("localhost", 8888);
        /*将相应资源发送到服务器，客服端输出流,TCL协议许发送字节流，将字节流转换为字符流*/
        BufferedWriter bo = new BufferedWriter(
                new OutputStreamWriter(client.getOutputStream()));
        String str = "客服端第一次请求";
        /*接受服务器信息，客户端输入流*/
        BufferedReader br = new BufferedReader(
                new InputStreamReader(client.getInputStream()));
        /*获取输入流内容*/
        bo.write(str);
        /*使用readLine()读取需要使用newLine()换行*/
        bo.newLine();
        bo.flush();
        String msg = br.readLine();
        System.out.println("客服端输出\n" + msg);

    }

}