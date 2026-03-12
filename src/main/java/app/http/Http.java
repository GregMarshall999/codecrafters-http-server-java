package app.http;

import app.domain.request.HttpCall;
import app.exception.ServerException;

import java.net.ServerSocket;
import java.net.Socket;

public interface Http {
    ServerSocket initServerSocket() throws ServerException;
    Socket awaitClientRequest(ServerSocket socket) throws ServerException;
    HttpCall parseClientRequest(Socket client) throws ServerException;
    void handleClientRequest(HttpCall call, Socket client) throws ServerException;
}
