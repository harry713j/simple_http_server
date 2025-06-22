package com.harihara.http_server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RequestHandler implements Runnable {
    private Socket socket;

    public RequestHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStream out = socket.getOutputStream();
        ) {
            // Read request line: "GET / HTTP/1.1"
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;

            String[] tokens = requestLine.split(" ");
            String method = tokens[0];
            String path = tokens[1];

            HttpRequest request = new HttpRequest(method, path);

            // parse header
            String line;

            while (!(line = in.readLine()).isEmpty()){
                String[] headerParts = line.split(":", 2);

                if (headerParts.length == 2){
                    request.addHeader(headerParts[0], headerParts[1]);
                }
            }

            String filePath = "src/public" + (request.getPath().equals("/") ? "/index.html" : request.getPath());

            HttpResponse response;

            if (Files.exists(Paths.get(filePath))){
                byte[] content = Files.readAllBytes(Paths.get(filePath));
                response = new HttpResponse(200, "OK", content);
                response.addHeader("Content-Type", "text/html");
            } else {
                String message = "<h1>404 Not Found</h1>"; // replace with html file
                response = new HttpResponse(404, "Not Found", message.getBytes());
                response.addHeader("Content-Type", "text/html");
            }

            response.write(out);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
