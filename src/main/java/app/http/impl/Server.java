package app.http.impl;

import app.config.ServerEnv;
import app.domain.response.ContentType;
import app.domain.request.Header;
import app.domain.request.HttpCall;
import app.domain.request.HttpMethod;
import app.domain.response.HttpResponse;
import app.domain.request.Request;
import app.domain.response.ResponseCode;
import app.exception.ServerException;
import app.exception.Severity;
import app.http.Http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Http {
    @Override
    public ServerSocket initServerSocket(ServerSocket serverSocket) throws ServerException {
        try {
            IO.println("Server listening to port: " + ServerEnv.PORT);
            serverSocket.setReuseAddress(ServerEnv.CAN_REUSE_ADDRESS);
            return serverSocket;
        } catch (IOException e) {
            throw new ServerException("Failed to init socket: " + e.getMessage(), Severity.HIGH);
        }
    }

    @Override
    public Socket awaitClientRequest(ServerSocket socket) throws ServerException {
        try {
            Socket clientRequest = socket.accept();
            IO.println("Accepted new connection");
            return clientRequest;
        } catch (IOException e) {
            throw new ServerException("Failed to accept client request: " + e.getMessage(), Severity.LOW);
        }
    }

    @Override
    public HttpCall parseClientRequest(Socket client) throws ServerException {
        try {
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String content;
            String host = null;
            String acceptMediaType = null;
            String userAgent = null;
            String request = null;
            // Stop on empty line (end of headers) or null (EOF). We must NOT read past the empty line:
            // the next read would be the request body; if the client hasn't sent it yet, readLine() blocks forever.
            while ((content = clientReader.readLine()) != null && !content.isEmpty()) {
                if (content.contains(": ")) {
                    String[] kv = content.split(": ", 2);
                    switch (kv[0]) {
                        case "Host" -> host = kv[1];
                        case "Accept" -> acceptMediaType = kv[1];
                        case "User-Agent" -> userAgent = kv[1];
                        default -> { /* unknown header, ignore */ }
                    }
                } else if (content.contains("HTTP")) {
                    request = content;
                }
            }

            assert request != null;
            String[] requestLine = request.split(" ");

            return new HttpCall(
                    new Request(HttpMethod.valueOf(requestLine[0]), requestLine[1], requestLine[2]),
                    new Header(host, acceptMediaType, userAgent)
            );
        } catch (IOException e) {
            throw new ServerException("Failed to parse client request: " + e.getMessage(), Severity.LOW);
        }
    }

    @Override
    public void handleClientRequest(HttpCall call, Socket client) throws ServerException {
        ResponseCode code;
        ContentType contentType;
        int contentLength = 0;
        String body = null;

        String[] path = call.request().endpoint().split("/");

        if(path.length == 0) {
            code = ResponseCode.OK;
            contentType = ContentType.NONE;
        }
        else {
            switch (path[1]) {
                case "echo" -> {
                    code = ResponseCode.OK;
                    contentType = ContentType.TEXT_PLAIN;
                    body = path[2];
                    contentLength = body.getBytes().length;
                }
                case "user-agent" -> {
                    code = ResponseCode.OK;
                    contentType = ContentType.TEXT_PLAIN;
                    body = call.header().userAgent();
                    contentLength = body.getBytes().length;
                }
                default -> {
                    code = ResponseCode.NOT_FOUND;
                    contentType = ContentType.NONE;
                }
            }
        }

        HttpResponse httpResponse = new HttpResponse(
                call.request().httpVersion(),
                code,
                contentType,
                contentLength,
                body
        );

        try {
            client.getOutputStream().write(httpResponse.getBytes());
        } catch (IOException e) {
            throw new ServerException("Failed to respond: " + e.getMessage(), Severity.LOW);
        }
    }
}
