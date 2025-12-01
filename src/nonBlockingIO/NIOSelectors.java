package nonBlockingIO;

/*
 * ### The Problem with Traditional Blocking I/O

    **Blocking Server (Thread-per-connection):**
    ```
    Client 1 → Thread 1 (waiting for data...) ← blocks!
    Client 2 → Thread 2 (waiting for data...) ← blocks!
    Client 3 → Thread 3 (waiting for data...) ← blocks!
    ...
    10,000 clients → 10,000 threads → System crash! 💥
    ```

    **Problem:** Each thread consumes 1-2MB of memory. 10,000 threads = 10-20GB RAM just for threads!
    *
    *
    ### The Solution: Selectors (Event-Driven I/O)
    *   Client 1 ─┐
        Client 2 ─┤
        Client 3 ─┼→ Selector → 1 Thread handles ALL
        Client 4 ─┤              (only processes when data is ready!)
        ...      ─┘
        10,000 clients → 1-10 threads → Scales beautifully! 🚀
         */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class NIOSelectors {

    // =============== NON BLOCKING SERVER ================
    private static class NonBlockingServer {
        private Selector selector;
        private ServerSocketChannel serverChannel;

        public void start(int port) throws IOException {
            // 1. Create Selector
            selector = Selector.open();
            System.out.println("✅ Selector Created");

            // 2. Create Server Socket Channel
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(port));
            serverChannel.configureBlocking(false); // NON-BLOCKING
            System.out.println("✅ Server listening on port: " + port);

            // 3. Register channel with Selector
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("✅ Server registered for ACCEPT events");
            System.out.println("\nWaiting for clients....\n");

            // 4. Event loop
            while (true) {
                // Wait for events (blocking but handles ALL connections)
                int readyChannels = selector.select();

                if (readyChannels == 0) {
                    continue;
                }

                // Get channels that have events
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    try {
                        if (key.isAcceptable()) {
                            handleAccept(key);
                        } else if (key.isReadable()) {
                            handleRead(key);
                        }
                    } catch (IOException ex) {
                        System.err.println("Error handling client: " + ex.getMessage());
                        key.cancel();
                        key.channel().close();
                    }
                    keyIterator.remove();
                }

            }

        }

        private void handleAccept(SelectionKey key) throws IOException {
            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
            SocketChannel clientChannel = serverChannel.accept();

            if (clientChannel != null) {
                clientChannel.configureBlocking(false);
                clientChannel.register(selector, SelectionKey.OP_READ);

                System.out.println("✅ New client connected: " + clientChannel.getRemoteAddress());
            }

        }

        private void handleRead(SelectionKey key) throws IOException {
            SocketChannel clientChannel = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            int bytesRead = clientChannel.read(byteBuffer);

            if (bytesRead == -1) {
                // Client disconnected
                System.out.println("❌ Client disconnected:" + clientChannel.getRemoteAddress());
                key.cancel();
                key.channel().close();
            }

            // Process data
            byteBuffer.flip();
            String message = StandardCharsets.UTF_8.decode(byteBuffer).toString();
            System.out.println("⬅\uFE0F Received: " + message.trim());

            // Echo back
            String response = "Echo: " + message;
            ByteBuffer bufferResponse = ByteBuffer.wrap(response.getBytes());
            clientChannel.write(bufferResponse);
            System.out.println("➡\uFE0F Sent: " + response.trim());

        }

    }


    // =============== NON BLOCKING CLIENT ============
    private static class NonBlockingClient {

        public void connect(String host, int port, String message) throws IOException{
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(host, port));

            // Wait for connection to complete
            while (!channel.finishConnect()) {
                System.out.println("Connecting....");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            System.out.println("✅ Connected to Server");

            // Send message
            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            channel.write(buffer);
            System.out.println("➡\uFE0F Sent: " + message);

            // Receive response
            buffer = ByteBuffer.allocate(1024);
            channel.read(buffer);
            buffer.flip();
            String response = StandardCharsets.UTF_8.decode(buffer).toString();
            System.out.println("⬅\uFE0F Received: " + response);

            channel.close();

        }

    }

    public static void comparisonDemo() {
        System.out.println("=== Blocking vs Non-Blocking Comparison ===\n");

        System.out.println("BLOCKING I/O:");
        System.out.println("  Thread per connection");
        System.out.println("  1,000 clients = 1,000 threads");
        System.out.println("  Each thread: ~1-2MB memory");
        System.out.println("  Total: ~1-2GB RAM");
        System.out.println("  Max connections: ~10,000 (limited by OS)");
        System.out.println("  Use case: Traditional web servers\n");

        System.out.println("NON-BLOCKING I/O (Selector):");
        System.out.println("  1 thread handles multiple connections");
        System.out.println("  1,000 clients = 1-10 threads");
        System.out.println("  Minimal memory overhead");
        System.out.println("  Total: ~10-20MB RAM");
        System.out.println("  Max connections: 100,000+ (C10K problem solved!)");
        System.out.println("  Use case: High-concurrency servers (Netty, WebFlux)");
    }

    public void nioSelectorsImpl() {
        comparisonDemo();

        System.out.println("\n\n=== Starting Non-Blocking Server Demo ===");
        System.out.println("To test:");
        System.out.println("1. Run this program (starts server on port 8080)");
        System.out.println("2. In terminal: telnet localhost 8080");
        System.out.println("3. Type messages and see echo responses\n");

        try {
            NonBlockingServer server = new NonBlockingServer();
            server.start(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
