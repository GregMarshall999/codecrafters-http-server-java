package app;

import app.config.ServerEnv;
import app.domain.request.HttpCall;
import app.exception.ServerException;
import app.http.Http;
import app.http.impl.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private static final Http HTTP_SERVER = new Server();

    private HttpServer() {
        /* This utility class should not be instantiated */
    }

    public static void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(ServerEnv.PORT);
            try {
                serverSocket = HTTP_SERVER.initServerSocket(serverSocket);
                Socket clientRequest = HTTP_SERVER.awaitClientRequest(serverSocket);
                HttpCall call = HTTP_SERVER.parseClientRequest(clientRequest);
                HTTP_SERVER.handleClientRequest(call, clientRequest);
            } catch (ServerException e) {
                switch (e.getSeverity()) {
                    case HIGH -> {
                        IO.println("Major Exception: " + e.getMessage());
                        System.exit(0);
                    }
                    case LOW -> IO.println("Warning: " + e.getMessage());
                }
            } finally {
                if(serverSocket != null && !serverSocket.isClosed()) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        IO.println("Failed to close socket: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            IO.println("Unable to create Server Socket: " + e.getMessage());
        }
    }
}
