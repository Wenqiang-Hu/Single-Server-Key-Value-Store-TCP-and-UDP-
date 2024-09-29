package code;
import java.io.*;
import java.net.*;

/*
 * Client class can talk to either TCP or UDP server.
 * 
 * There is a 5 secs timeout mechanism in case server not responding
 * 
 * Client can either parse in 3 params to use pre-populated values
 * Or
 * Client can parse in 4 parms to use self-defined values
 */
public class Client {
    private String host;
    private int port;
    private String protocol;
    private static final int TIMEOUT = 5000; // 5 seconds

    public Client(String host, int port, String protocol) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
    }

    // Send a generic request
    public void sendRequest(String request) throws IOException {
        if (protocol.equalsIgnoreCase("TCP")) {
            sendTCPRequest(request);
        } else if (protocol.equalsIgnoreCase("UDP")) {
            sendUDPRequest(request);
        } else {
            System.out.println("Unsupported protocol: " + protocol);
        }
    }

    // Client talks to TCP server 
    private void sendTCPRequest(String request) throws IOException {
        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(TIMEOUT); // Set timeout
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(request);

            try {
                String response = in.readLine(); // Attempt to read response
                System.out.println("Response: " + response);
            } catch (SocketTimeoutException e) {
                System.out.println("TCP request timed out after 5 seconds");
            }
        }
    }

    // Client talks to UDP server 
    private void sendUDPRequest(String request) throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT); // Set timeout
            byte[] sendData = request.getBytes();
            InetAddress address = InetAddress.getByName(host);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(sendPacket);

            byte[] buffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);

            try {
                socket.receive(responsePacket); // Attempt to receive response
                String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                System.out.println("Response: " + response);
            } catch (SocketTimeoutException e) {
                System.out.println("UDP request timed out after 5 seconds");
            }
        }
    }

    // Method to pre-populate with 5 PUT requests
    public void prePopulateKeyValueStore() throws IOException {
        System.out.println("Pre-populating the Key-Value store with 5 PUT requests...");
        for (int i = 1; i <= 5; i++) {
            String key = "key" + i;
            String value = "value" + i;
            String request = "PUT " + key + " " + value;
            sendRequest(request);
        }
    }

    // Method to perform 5 GET requests
    public void performGetOperations() throws IOException {
        System.out.println("Performing 5 GET requests...");
        for (int i = 1; i <= 5; i++) {
            String key = "key" + i;
            String request = "GET " + key;
            sendRequest(request);
        }
    }

    // Method to perform 5 DELETE requests
    public void performDeleteOperations() throws IOException {
        System.out.println("Performing 5 DELETE requests...");
        for (int i = 1; i <= 5; i++) {
            String key = "key" + i;
            String request = "DELETE " + key;
            sendRequest(request);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3 || args.length > 4) {
            System.out.println("Usage: ");
            System.out.println("To use pre-populated command: java Client.java <host> <port> <protocol>");
            System.out.println("To use self-defined command: java Client.java <host> <port> <protocol> <command>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String protocol = args[2];
        Client client = new Client(host, port, protocol);
        
        if (args.length == 3) {
            // Pre-populate Key-Value store with 5 PUT requests
            client.prePopulateKeyValueStore();
            
            // Perform 5 GET requests
            client.performGetOperations();
            
            // Perform 5 DELETE requests
            client.performDeleteOperations();
            return;
        }
        
        String command = args[3];
        client.sendRequest(command);

    }
}
