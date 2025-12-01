package channels;

/*
 * Rule of thumb: For most backend REST APIs → use Streams/NIO.2. For high-performance file processing → use Channels.
 */

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class FileChannelImpl {

    public void fileChannelImpl() throws IOException {
        // writeWithChannel();
        // readWithChannel();
        // copyFileWithChannel();
        performanceComparison();
    }

    // ================ WRITING WITH CHANNEL ===============
    public void writeWithChannel() throws IOException {
        System.out.println("======= Writing with FileChannel =======");

        // Open channel for writing
        try (FileChannel channel = FileChannel.open(Paths.get("channel_output.tsx"),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {

            String data = "Hello from FileChannel! \n This is a new line. \n This is line 3.";
            ByteBuffer byteBuffer = ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8));

            // Write entire buffer to a channel
            int byteWritten = channel.write(byteBuffer);
            System.out.println("Bytes written: " + byteWritten);

            // Write at a specific position
            byteBuffer = ByteBuffer.wrap("\n[INSERTED]".getBytes());
            channel.write(byteBuffer, 10); // Write at position 10

            System.out.println("File written successfully");

        }
    }

    // ========== READING WITH CHANNEL ============
    public void readWithChannel() throws IOException {
        System.out.println("======= Reading with FileChannel ========");

        try (FileChannel channel = FileChannel.open(Paths.get("channel_output.tsx"), StandardOpenOption.READ)) {

            // Allocate buffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            // Read from channel into buffer
            int bytesRead = channel.read(byteBuffer);
            System.out.println("Read: " + bytesRead + " bytes");

            // flip() to read mode
            byteBuffer.flip();

            // Convert buffer to String
            String content = StandardCharsets.UTF_8.decode(byteBuffer).toString();
            System.out.println("Full content:");
            System.out.println(content);

            // Read from a specific position
            byteBuffer.clear();
            channel.read(byteBuffer, 0);
            byteBuffer.flip();
            System.out.println("\nRead from position 0:");
            System.out.println(StandardCharsets.UTF_8.decode(byteBuffer).toString().substring(0, 20) + "....");

        }
    }

    // ============ COPYING FILES WITH CHANNEL ==============
    public void copyFileWithChannel() throws IOException {
        System.out.println("======== Copying files with FileChannel =========");

        // Create a source file
        try (FileOutputStream fileOutputStream = new FileOutputStream("source_large.dat")) {
            byte [] data = new byte[1024 * 1024]; // 1MB
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (i % 256);
            }
            fileOutputStream.write(data);
        }
        System.out.println("Created a 1MB Source File");

        // Copy using transferTo(), zero-copy optimization
        try (FileChannel sourceChannel = FileChannel.open(Paths.get("source_large.dat"), StandardOpenOption.READ);
             FileChannel destChannel = FileChannel.open(Paths.get("dest_large.dat"),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {

            long start = System.nanoTime();

            // zero-copy transfer. OS handles it directly
            long transferred = sourceChannel.transferTo(0, sourceChannel.size(), destChannel);

            long end = System.nanoTime();

            System.out.println("Transferred: " + transferred + " bytes");
            System.out.println("Time: " + (end - start) / 1_000_000 + " ms");
        }

        // Clean up
        new File("source_large.dat").delete();
        new File("dest_large.dat").delete();
    }

    //  ============= PERFORMANCE COMPARISON ============
    public void performanceComparison() throws IOException {
        System.out.println("========== Performance Comparison ===========");

        // Create test file
        File testFile = new File("perf_test.dat");
        int fileSize = 10 * 1024 * 1024; // 10MB

        try (FileOutputStream fos = new FileOutputStream(testFile)) {
            byte[] data = new byte[fileSize];
            fos.write(data);
        }

        // Test 1: Traditional InputStream
        long start = System.nanoTime();
        try (FileInputStream fis = new FileInputStream(testFile)) {
            byte[] buffer = new byte[8192];
            while(fis.read() != -1) {
                // System.out.print(Arrays.toString(buffer) + " ");
            }
        }
        long streamTime = System.nanoTime() - start;
        System.out.println("InputStream: " + streamTime / 1_000_000 + " ms");

        // Test 2: BufferedInputStream
        start = System.nanoTime();
        try (BufferedInputStream bis = new BufferedInputStream( new FileInputStream(testFile))) {
            byte[] buffer = new byte[8192];
            while (bis.read(buffer) != -1) {
                //System.out.println(Arrays.toString(buffer) + " ");
            }
        }
        long bufferedStreamTime = System.nanoTime() - start;
        System.out.println("BufferedInputStream: " + bufferedStreamTime / 1_000_000 + " ms");

        // Test 3: File Channel
        start = System.nanoTime();
        try (FileChannel channel = FileChannel.open(testFile.toPath(), StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            while (channel.read(buffer) > 0) {
                buffer.clear();
            }
        }
        long channelTime = System.nanoTime() - start;
        System.out.println("File Channel: " + channelTime / 1_000_000 + " ms");

        // Test 4: File Channel with direct buffer (fastest)
        start = System.nanoTime();
        try (FileChannel channel = FileChannel.open(testFile.toPath(), StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(8192); // Direct Buffer
            while (channel.read(buffer) > 0) {
                buffer.clear();
            }
        }
        long directBufferChannel = System.nanoTime() - start;
        System.out.println("File Channel (Direct Buffer): " + directBufferChannel / 1_000_000 + " ms");

        System.out.println("\nSpeed Up:");
        System.out.println("Direct Buffer vs InputStream: " + String.format("%.2fx faster", (double) streamTime / directBufferChannel));

        // Clean Up
        testFile.delete();
    }

}
