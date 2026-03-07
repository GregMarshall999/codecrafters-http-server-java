void main() {
    IO.println("Logs from your program will appear here!");

    try {
        ServerSocket serverSocket = new ServerSocket(4221);
        serverSocket.setReuseAddress(true);

        Socket accept = serverSocket.accept();

        accept.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());

        IO.println("accepted new connection");
    } catch (IOException e) {
        IO.println("IOException: " + e.getMessage());
    }
}
