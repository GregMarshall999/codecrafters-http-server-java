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
        while(true) {
            try {
                ServerSocket serverSocket = new ServerSocket(ServerEnv.PORT);
                try {
                    serverSocket = HTTP_SERVER.initServerSocket(serverSocket);
                    Socket clientRequest = HTTP_SERVER.awaitClientRequest(serverSocket);

                    Thread.startVirtualThread(() -> handleConnection(clientRequest));
                } catch (ServerException e) {
                    handleServerException(e);
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

    private static void handleConnection(Socket clientConnection) {
        try {
            HttpCall call = HTTP_SERVER.parseClientRequest(clientConnection);
            HTTP_SERVER.handleClientRequest(call, clientConnection);
        } catch (ServerException e) {
            handleServerException(e);
        }
    }

    private static void handleServerException(ServerException e) {
        switch (e.getSeverity()) {
            case HIGH -> {
                IO.println("Major Exception: " + e.getMessage());
                System.exit(0);
            }
            case LOW -> IO.println("Warning: " + e.getMessage());
        }
    }
}
