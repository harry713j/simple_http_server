package com.harihara.http_server;


import java.io.OutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private int statusCode;
    private String statusMessage;
    private Map<String, String> headers = new HashMap<>();
    private byte[] body;

    public HttpResponse(int statusCode, String statusMessage, byte[] body) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.body = body;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void write(OutputStream out) throws IOException {
        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusMessage).append("\r\n");
        headers.put("Content-Length", String.valueOf(body.length));
        headers.putIfAbsent("Content-Type", "text/plain");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }

        response.append("\r\n");

        out.write(response.toString().getBytes());
        out.write(body);
    }
}


