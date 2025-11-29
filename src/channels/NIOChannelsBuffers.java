package channels;

/*
 * What Are Channels and Buffers?
    Traditional I/O (Streams):
    File → InputStream → read byte by byte → Your Program
    NIO (Channels + Buffers):
    File → FileChannel → read into Buffer (bulk data) → Your Program
    Key Difference:
      Streams: Unidirectional (read OR write), byte-by-byte
      Channels: Bidirectional (read AND write), buffer-based, faster!

    * A buffer is a container for data in memory. Think of it as an array with special pointers.
    * Buffer: [_, _, _, _, _, _, _, _]
         ↑           ↑        ↑
      position    limit    capacity

      capacity: Total size (doesn't change)
      limit: How much data you can read/write
      position: Current read/write location

   Buffer States:
    Writing mode: Put data into buffer
    Flip: Switch to reading mode
    Reading mode: Get data from buffer
    Clear/Compact: Prepare for next write
 */

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class NIOChannelsBuffers {

    public void nioChannelsAndBuffers() {
        byteBufferBasics();
        bufferFlipDemo();
        intBufferExample();
    }

    // ============= BYTE BUFFER BASICS =============
    public void byteBufferBasics() {
        System.out.println("========= Byte Buffer Basics ===========");

        // Create a buffer with capacity 10 bytes
        ByteBuffer buffer = ByteBuffer.allocate(10);

        System.out.println("Initial State: ");
        printBufferState(buffer);

        // Writing data to buffer:
        buffer.put((byte) 'H');
        buffer.put((byte) 'e');
        buffer.put((byte) 'l');
        buffer.put((byte) 'l');
        buffer.put((byte) 'o');

        System.out.println("\nAfter writing hello:");
        printBufferState(buffer);

        // Can't read yet! Position is at 5, limit is at 10
        // We need to flip() to switch from write mode to read mode

        buffer.flip();
        System.out.println("\nAfter flip(), ready to read:");
        printBufferState(buffer);

        // Reading data from buffer
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            System.out.print((char) b);
        }
        System.out.println();

        System.out.println("\nAfter reading all");
        printBufferState(buffer);

        // Prepare for next write cycle
        buffer.clear();
        System.out.println("\nAfter clear() - Ready for a new write:");
        printBufferState(buffer);
    }

    //================= UNDERSTANDING FLIP ================
    public void bufferFlipDemo() {
        System.out.println("======== Understanding flip() ===========");

        ByteBuffer buffer = ByteBuffer.allocate(8);

        // Write some data
        buffer.put("ABC".getBytes());
        System.out.println("After writing ABC:");
        System.out.println(" Position: " + buffer.position() + " (next write location)");
        System.out.println(" Limit: " + buffer.limit() + "(max write location)");

        /*
         * What flip() does
         *  1. Sets limit = position (3)
         *  2. Sets position = 8
         * Now we can read from 8 to 3
         */

        buffer.flip();
        System.out.println("After flip():");
        System.out.println(" Position: " + buffer.position() + " (next read location)");
        System.out.println(" Limit: " + buffer.limit() + "(max read location)");

        System.out.println("Reading: ");
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            System.out.print((char) b);
        }
        System.out.println();

        // The maximum we can read in up-to the location we have written

    }

    // ================ TYPED BUFFERS =================
    public void intBufferExample() {
        System.out.println("=========== IntBuffer Example ============");

        // Buffers for different types
        IntBuffer intBuffer = IntBuffer.allocate(5);

        // Write integers
        intBuffer.put(10);
        intBuffer.put(20);
        intBuffer.put(30);
        intBuffer.put(40);

        System.out.println("Wrote 4 integers");
        System.out.println(" Position: " +  intBuffer.position());

        // Flip to read()
        intBuffer.flip();

        // Read integers
        System.out.println("Reading: ");
        while (intBuffer.hasRemaining()) {
            int i = intBuffer.get();
            System.out.print(i + " ");
        }
        System.out.println();

        // Rewind: reset the position to 0, keep limit
        intBuffer.rewind();
        System.out.println("\nAfter rewinding: ");
        while (intBuffer.hasRemaining()) {
            int i = intBuffer.get();
            System.out.print(i + " ");
        }
    }

    // =========== BUFFER OPERATIONS SUMMARY =============
    public static void bufferOperationsSummary() {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        // flip(): Write mode → Read mode
        // Sets limit = position, position = 0
        buffer.flip();

        // clear(): Prepare for new write
        // Sets position = 0, limit = capacity
        // Data still there, just not visible!
        buffer.clear();

        // rewind(): Read again from start
        // Sets position = 0, keeps limit
        buffer.rewind();

        // compact(): Keep unread data, prepare for write
        // Moves unread data to beginning
        buffer.compact();

        // mark() and reset(): Bookmark a position
        buffer.mark();      // Remember current position
        buffer.get();       // Read some data
        buffer.reset();     // Go back to marked position
    }


    // Helper method to print buffer state
    private static void printBufferState(ByteBuffer buffer) {
        System.out.println(" Position: " + buffer.position() +
                ", Limit: " + buffer.limit() +
                ", Capacity: " + buffer.capacity() +
                ", Remaining: " + buffer.remaining());
    }











}
