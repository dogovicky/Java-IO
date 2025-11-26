package buffered;

/*
 * ### Why Buffering Matters
    **Without buffering:**
    ```
    read() → disk access → read() → disk access → read() → disk access
    ```
    Each read/write hits the disk = SLOW! 💀

    **With buffering:**
    ```
    read() → reads 8KB chunk into memory → 8000 reads from memory (FAST!) → next disk access
 */

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BufferingIO {

    public void bufferingIOImpl() {
        createTestFile();

        System.out.println("=========== Speed Comparison ==========");
        unbufferedReading();
        System.out.println();
        bufferedReading();
        System.out.println();
        bufferedReadingByLine();

        // Reading CSV File
        List<String[]> records = readCSV("csv.txt");
        System.out.println(records);

        // Writing Logs
        writeLog("An error occurred. Please contact Admin for support.");

    }

    // Create a test file with 10,000 lines
    public void createTestFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("large_file.txt"))) {
            for (int i = 0; i <= 10000; i++) {
                writer.write("This is line number " + i + " in our test file.\n");
            }
            System.out.println("Created large_file.txt with 10,000 lines");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // ======== File Reading =========
    // ========= UNBUFFERED READING (Slow) =========
    public void unbufferedReading() {
        long start = System.currentTimeMillis();

        try (FileReader reader = new FileReader("large_file.txt")) {
            int charCount = 0;
            while (reader.read() != -1) {
                charCount++;
            }
            long end = System.currentTimeMillis();
            System.out.println("Unbuffered: Read " + charCount + " characters");
            System.out.println("Time taken: " + (end - start) + "ms");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ========= BUFFERED READING (Fast) ===========
    public void bufferedReading() {
        long start = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(new FileReader("large_file.txt"))) {
            int charCount = 0;
            while (reader.read() != -1) {
                charCount++;
            }
            long end = System.currentTimeMillis();
            System.out.println("Buffered: Read " + charCount + " characters");
            System.out.println("Time taken: " + (end - start) + "ms");
            System.out.println("Speedup: ~" + (float)(end - start) / (end - start) + "x faster!");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ========== BEST PRACTICE: BUFFERED READING BY LINE =============
    public void bufferedReadingByLine() {
        long start = System.currentTimeMillis();
        System.out.println("======== Reading Line By Line (Best Practice) ==========");

        try (BufferedReader reader = new BufferedReader(new FileReader("large_file.txt"))) {
            String line;
            int lineCount = 0;

            // Most common pattern
            while ((line = reader.readLine()) != null) {
                lineCount++;

                // Process each line here
                if (lineCount <= 3) {
                    System.out.println("Line " + lineCount + ": " + line);
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("Total time taken to read line by line: " + (end - start) + "ms");
            System.out.println("... (skipped " + (lineCount - 3) + " lines)");
            System.out.println("Total lines read: " + lineCount);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ========== REAL-WORLD EXAMPLE: Processing CSV ==========
    public List<String[]> readCSV(String fileName) {
        List<String[]> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                records.add(fields);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        return records;
    }

    // ============= REAL-WORLD EXAMPLE: Writing Logs =============
    public void writeLog(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("app.log", true))) {
            // true = append mode (don't overwrite)
            String timestamp = LocalDateTime.now().toString();
            writer.write("[ " + timestamp + " ]" + message);
            writer.newLine();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

}
