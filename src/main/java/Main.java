import config.ServerEnv;

void main() {
    try {
        ServerSocket serverSocket = new ServerSocket(ServerEnv.PORT);
        IO.println("Server listening to port: " + ServerEnv.PORT);
        serverSocket.setReuseAddress(ServerEnv.CAN_REUSE_ADDRESS);

        Socket client = serverSocket.accept();
        IO.println("Accepted new connection");

        BufferedReader clientReader = new BufferedReader(
                new InputStreamReader(
                        client.getInputStream()
                )
        );

        String clientMessage = clientReader.readLine();
        String[] messageContent = clientMessage.split(" ");
        String status = messageContent[1].equals("/") ? "200 OK" : "404 Not Found";
        String response = "HTTP/1.1 " + status + "\r\n\r\n";

        client.getOutputStream().write(response.getBytes());
    } catch (IOException e) {
        IO.println("IOException: " + e.getMessage());
    }
}
