package com.harihara.http_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

            if (!method.equals("GET")) {
                sendResponse(out, 405, "Method Not Allowed", "Only GET is supported.");
                return;
            }

            if (path.equals("/")) path = "/index.html";
            String filePath = "src/public" + path;

            if (Files.exists(Paths.get(filePath))) {
                byte[] content = Files.readAllBytes(Paths.get(filePath));
                sendResponse(out, 200, "OK", content, "text/html");
            } else {
                sendResponse(out, 404, "Not Found", "File not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(OutputStream out, int statusCode, String statusText, String body) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + body.length() + "\r\n\r\n" +
                body;
        out.write(response.getBytes());
    }

    private void sendResponse(OutputStream out, int statusCode, String statusText, byte[] body, String contentType) throws IOException {
        String headers = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + body.length + "\r\n\r\n";
        out.write(headers.getBytes());
        out.write(body);
    }
}
