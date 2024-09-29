
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * TCP server uses self-defined keyValueStore(hashmap) to store
 * values.
 * 
 * Once the server is initiated, it will listen on defined port number.
 * 
 */
public class TCPServer {
    private KeyValueStore kvStore;
    // server will listen on the port number
    private int port; 
    private SimpleDateFormat sdf;

    public TCPServer(int port) {
        this.port = port;
        this.kvStore = new KeyValueStore();
        // timestamp format 
        this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TCP Server listening on port " + port);
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    String request = in.readLine();
                    // the server will process the request 
                    String response = processRequest(request);

                    // Logging the request and response with timestamp and client info
                    logRequest(clientSocket.getInetAddress(), clientSocket.getPort(), request, response);

                    out.println(response);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // tcp server will persume desired command 
    private String processRequest(String request) {
        String[] parts = request.split(" ", 3);
        String command = parts[0];
        String key = parts.length > 1 ? parts[1] : null;
        String value = parts.length > 2 ? parts[2] : null;
        
        // the tcp server supports three operations (PUT, GET, DELETE) 
        switch (command.toUpperCase()) {
            case "PUT":
                return kvStore.put(key, value);
            case "GET":
                return kvStore.get(key);
            case "DELETE":
                return kvStore.delete(key);
            default:
                return "The server only supports PUT, GET, DELETE command";
        }
    }

    // Each request, the server will log the receive and response time
    private void logRequest(InetAddress address, int port, String request, String response) {
        String timestamp = sdf.format(new Date(System.currentTimeMillis()));
        System.out.println(timestamp + " - Received request from " + address + ":" + port + " -> " + request);
        System.out.println(timestamp + " - Response sent: " + response);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TCPServer.java <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        TCPServer server = new TCPServer(port);
        server.start();
    }
}
