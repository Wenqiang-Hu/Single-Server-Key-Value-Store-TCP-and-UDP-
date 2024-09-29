
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * UDP server uses self-defined keyValueStore(hashmap) to store
 * values.
 * 
 * Once the server is initiated, it will listen on defined port number.
 * 
 */
public class UDPServer {
    private KeyValueStore kvStore;
    // server will listen on the port number
    private int port;
    private SimpleDateFormat sdf;

    public UDPServer(int port) {
        this.port = port;
        this.kvStore = new KeyValueStore();
        // timestamp format 
        this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    }

    public void start() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("UDP Server listening on port " + port);
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(requestPacket);

                String request = new String(requestPacket.getData(), 0, requestPacket.getLength());
                String response = processRequest(request);

                // Logging the request and response with timestamp and client info
                logRequest(requestPacket.getAddress(), requestPacket.getPort(), request, response);

                byte[] responseData = response.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(
                    responseData, responseData.length, requestPacket.getAddress(), requestPacket.getPort()
                );
                socket.send(responsePacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // UDP server will persume desired command 
    private String processRequest(String request) {
        String[] parts = request.split(" ", 3);
        String command = parts[0];
        String key = parts.length > 1 ? parts[1] : null;
        String value = parts.length > 2 ? parts[2] : null;

        // UDP server supports three operations (PUT, GET, DELETE) 
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
            System.out.println("Usage: java UDPServer.java <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        UDPServer server = new UDPServer(port);
        server.start();
    }
}
