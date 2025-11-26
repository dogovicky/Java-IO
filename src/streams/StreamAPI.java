package streams;

/*
 *A stream is a sequence of data flowing from a source to a destination. Think of it like a water pipe:
 * InputStream/Reader: Water flowing IN (reading data)
 * OutputStream/Writer: Water flowing OUT (writing data)
 */

import java.io.*;

public class StreamAPI {

    public void streamAPI() {
        System.out.println("================ Stream API Started ==============");
//        byteStreamImpl();
        characterStreamImpl();
    }

    // ============ BYTE STREAM ===============
    /* Used when working with raw binary data (images, videos, any file)
     * InputStream and OutputStream
     */

    public void byteStreamImpl() {
        System.out.println("========== Byte Stream Example ===========");

        // Writing bytes to a file
        try (FileOutputStream fos = new FileOutputStream("data.bin")) {
            byte[] data = { 65, 66, 67, 68, 69}; // ASCII: A, B, C, D, E
            fos.write(data);
            System.out.println("Written binary data to the data.bin file");

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        // Reading bytes from a file
        try (FileInputStream fis = new FileInputStream("data.bin")) {
            int byteData;
            System.out.println("====== Read Bytes =======");
            while ((byteData = fis.read()) != -1) {
                System.out.println(byteData + " ");
            }
            System.out.println();
        } catch (IOException ex) {
            System.out.println("Error Message: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ============ Character Stream ===============
    /*
     * Used for reading text files, logs, CSV, JSON
     * Use Reader and Writer
     */

    public void characterStreamImpl() {
        System.out.println("============== Character Stream Example =============");

        // Writing to a text file
        try (FileWriter fileWriter = new FileWriter("message.txt")) {
            fileWriter.write("Hello from Java IO \n");
            fileWriter.write("Character strings handle text encoding automatically");
            System.out.println("====== Written text to message.txt =========");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        // Reading text from a file
        try (FileReader fileReader = new FileReader("message.txt")) {
            int charData;
            System.out.println("==== Read Text: ");
            while ((charData = fileReader.read()) != -1) {
                System.out.print((char) charData);
            }
            System.out.println();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    /*
     *🔍 What's Happening?

     * try-with-resources: Automatically closes streams (prevents memory leaks)
     * read() returns -1: Signals end of stream
     * Byte vs Character: Notice how byte streams deal with raw numbers, character streams handle text
     */



}
