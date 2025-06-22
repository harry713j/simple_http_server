package com.harihara.http_server;

import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private final int port;

    public HttpServer(int port){
        this.port = port;
    }

    public void start(){
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port + "...");

            while (true){
                Socket clientSocket = serverSocket.accept();
                // handle each client
                new Thread(new RequestHandler(clientSocket)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
