package com.tunnell.akkademy;

import akka.event.LoggingAdapter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by TunnellZhao on 2017/5/4.
 *
 * Socket server for testing
 */
public class TestingSocketServer {

    static void createServer(int port, LoggingAdapter log, Runnable command) throws IOException, InterruptedException {
        assert command != null;

        ServerSocket server = new ServerSocket(port);

        log.info(">>>[TestingSocketServer]> Socket server started on port [{}].", port);

        acceptLoop:
        while (true) {
            Socket socket = null;
            try {
                //创建一个流套接字并将其连接到指定主机上的指定端口号
                log.info("Accepting...");
                socket = server.accept();

                log.info("Connection established from [{}]. Ready to process.", socket.getInetAddress());

                //读取服务器端数据
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //向服务器端发送数据
                PrintStream out = new PrintStream(socket.getOutputStream());

                log.info("Processing...");

                String request = input.readLine();
                switch (request) {
                    case "execute": {
                        log.info("Get command [execute]. Ready to execute command.");
                        out.println("Get command [execute]. Ready to execute command.");
                        command.run();

                        break;
                    }

                    case "stop": {
                        log.info("Get command [stop]. Ready to shutdown server.");
                        out.println("Get command [stop]. Ready to shutdown server.");
                        break acceptLoop;
                    }

                    default: {
                        log.info("Get unknown command [{}]. Will ignored.", request);
                        out.println("Get unknown command [{}]. Will ignored.");
                        break;
                    }
                }
                log.info("Finished...");

                out.close();
                input.close();
            } catch (Exception e) {
                log.error("Server has encountered an error. Due to: ", e);
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        log.error("Failed to close socket server. Due to: ", e);
                    }
                }
            }

            Thread.sleep(300);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        for (int i = 0; i < 10000; i++) {
            try (Socket socket = new Socket("127.0.0.1", 23333)) {

                OutputStream stream = socket.getOutputStream();
                stream.write("execute".getBytes("UTF-8"));
                stream.flush();

            }
        }
    }
}
