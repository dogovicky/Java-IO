package networkingIO;

/*
 *
    What are Sockets?
    A **socket** is an endpoint for network communication.
    ```
    Client Socket ←→ Network ←→ Server Socket
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkingIO {

    // ============= SIMPLE BLOCKING SERVER ==============
    private static class BlockingServer {
        private int port;

        public BlockingServer(int port) {
            this.port = port;
        }

        public void start() {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("✅ Server started on port " + port);
                System.out.println("Waiting for clients...\n");

                while (true) {
                    // Accept connection (blocks until client connects)
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("✅ Client connected: " + clientSocket.getInetAddress().getHostAddress());

                    // Handle client (blocks current thread)
                    handleClient(clientSocket);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void handleClient(Socket socket) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {

                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("<- Received: " + message);

                    String response = "Echo: " + message;
                    out.println(response);
                    System.out.println("-> Sent: " + response);

                    if ("bye".equalsIgnoreCase(message)) {
                        break;
                    }
                }

                System.out.println("Client disconnected\n");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // ========== MULTI-THREADING SERVER ===========
    private static class MultiThreadedServer {
        private int port;
        private ExecutorService threadPool;

        public MultiThreadedServer(int port) {
            this.port = port;
            this.threadPool = Executors.newFixedThreadPool(10);
        }

        public void start() {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("✓ Multi-threaded server started on port " + port);
                System.out.println("Thread pool size: 10");
                System.out.println("Waiting for clients...\n");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("✓ Client connected: " + clientSocket.getInetAddress().getHostAddress());

                    // Handle each client in separate thread
                    threadPool.submit(() -> handleClient(clientSocket));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                threadPool.shutdown();
            }
        }

        public void handleClient(Socket socket) {
            String clientId = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

                writer.println("Welcome :) You are: " + clientId);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("[" + clientId + "] ← " + message);

                    String response = "Echo: " + message;
                    writer.println(response);

                    if ("bye".equalsIgnoreCase(message)) {
                        break;
                    }
                }
                System.out.println("[" + clientId + "] Disconnected\n");
            } catch (IOException e) {
                System.err.println("[" + clientId + "] Error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class SimpleHttpClient {

        public String fetch(String url) throws IOException {
            URL urlObj = new URL(url);
            String host = urlObj.getHost();
            int port = urlObj.getPort() == -1 ? 80 : urlObj.getPort();
            String path = urlObj.getPath().isEmpty() ? "/" : urlObj.getPath();

            try (Socket socket = new Socket(host, port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(
                         new InputStreamReader(socket.getInputStream()))) {

                // Send HTTP request
                out.println("GET " + path + " HTTP/1.1");
                out.println("Host: " + host);
                out.println("Connection: close");
                out.println(); // Empty line ends headers

                // Read HTTP response
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }

                return response.toString();
            }
        }
    }

    // ========== REAL-WORLD: MICROSERVICE COMMUNICATION ==========
    public static class MicroserviceClient {
        private final String serviceHost;
        private final int servicePort;

        public MicroserviceClient(String host, int port) {
            this.serviceHost = host;
            this.servicePort = port;
        }

        // Simulate calling another microservice
        public String callUserService(String userId) throws IOException {
            try (Socket socket = new Socket(serviceHost, servicePort);
                 ObjectOutputStream out = new ObjectOutputStream(
                         socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(
                         socket.getInputStream())) {

                // Send request
                out.writeObject("GET_USER:" + userId);

                // Receive response
                return (String) in.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException("Invalid response", e);
            }
        }
    }

    // ========== COMPARISON WITH SPRING BOOT ==========
    public static void springBootComparison() {
        System.out.println("=== Raw Sockets vs Spring Boot ===\n");

        System.out.println("RAW SOCKETS (What we just learned):");
        System.out.println("  ServerSocket → Accept → Socket → Streams");
        System.out.println("  Manual thread management");
        System.out.println("  Manual protocol handling (HTTP, etc.)");
        System.out.println("  Low-level control\n");

        System.out.println("SPRING BOOT (@RestController):");
        System.out.println("  Tomcat/Netty handles sockets");
        System.out.println("  Thread pool managed automatically");
        System.out.println("  HTTP protocol handled automatically");
        System.out.println("  You just write business logic!\n");

        System.out.println("UNDER THE HOOD:");
        System.out.println("  Your @GetMapping");
        System.out.println("       ↓");
        System.out.println("  Spring DispatcherServlet");
        System.out.println("       ↓");
        System.out.println("  Tomcat Thread Pool");
        System.out.println("       ↓");
        System.out.println("  ServerSocket.accept()");
        System.out.println("       ↓");
        System.out.println("  Socket + InputStream/OutputStream");
        System.out.println("\n→ Spring Boot abstracts away socket complexity!");
    }

    public void networkingIO() {
        System.out.println("\n\n=== Starting Multi-Threaded Server ===");
        System.out.println("To test:");
        System.out.println("1. Run this program");
        System.out.println("2. Open multiple terminals");
        System.out.println("3. In each: telnet localhost 9090");
        System.out.println("4. Type messages and see concurrent handling!\n");

        MultiThreadedServer server = new MultiThreadedServer(9090);
        server.start();
    }

}
