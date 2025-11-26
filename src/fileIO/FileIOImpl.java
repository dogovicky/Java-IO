package fileIO;

/*
 * The File class represents file paths and directories (not file content).
 * ⚠️ When NOT to Use File Class
    The File class has limitations:

    No proper exception handling (returns false on failure, no details why)
    Limited operations (can't copy, move efficiently)
    Symbolic links not handled well
 */

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileIOImpl {

    public void fileIOImpl() {
        // fileMetadata();
        // directoryOperations();
        // listingFiles();
        File dir = new File(".");
        walkDirectory(dir, 1);
    }

    // ============ FILE METADATA ============
    public void fileMetadata() {
        System.out.println("============ File Metadata =============");

        File file = new File("message.txt");

        if (file.exists()) {
            System.out.println("File exists: " + file.getName()); // Get File Name
            System.out.println("Absolute path: " + file.getAbsolutePath()); // Return the Path to the file
            System.out.println("Is file? " + file.isFile()); // Return the type of the file
            System.out.println("Is directory? " + file.isDirectory()); // Returns if file is directory or not
            System.out.println("Can read? " + file.canRead()); // Returns true if the file is readable
            System.out.println("Can write? " + file.canWrite()); // Returns true if the file is writable
            System.out.println("File size: " + file.length() + " bytes"); // Returns the length of the file

            // Last modified date
            long lastModifiedDate = file.lastModified();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("Last Modified: " + simpleDateFormat.format(new Date(lastModifiedDate)));
        } else {
            System.out.println("File does not exist");
        }

    }

    // =============== DIRECTORY OPERATIONS ==============
    public void directoryOperations() {
        System.out.println("============ Directory Operations =============");

        // Create a single directory
        File dir = new File("temp_folder");
        if (dir.mkdir()) {
            System.out.println("Created directory: " + dir.getName());
        }

        // Create nested directories
        File nestedDir = new File("parent/child/grandchild");
        if (nestedDir.mkdirs()) {
            System.out.println("Created nested directories: " + nestedDir.getPath());
        }

        // Create a file inside a directory
        File fileInDir = new File("temp_folder/test.txt");
        try {
            if (fileInDir.createNewFile()) {
                System.out.println("Created file: " + fileInDir.getPath());
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }

        // Delete a file
        if (fileInDir.delete()) {
            System.out.println("Deleted file: " + fileInDir.getName());
        }

        // Delete a directory (Must be empty)
        if (dir.delete()) {
            System.out.println("Deleted directory: " + dir.getName());
        }

    }

    // ============ LISTING FILES ===============
    public void listingFiles() {
        System.out.println("======== Listing files in current directory ==========");

        File currentDir = new File(".");

        // List all files and directories
        String[] fileNames = currentDir.list();
        if (fileNames != null) {
            System.out.println("Files and directories: ");
            for (String name : fileNames) {
                System.out.println(" - " + name);
            }
        }

        System.out.println("\n============== Filtering files ===============");

        // List only .txt files
        File[] txtFiles = currentDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (txtFiles != null) {
            System.out.println("Text files only: ");
            for (File file : txtFiles) {
                System.out.println(" - " + file.getName() + " (" + file.length() + " bytes)");
            }
        }

        // List only directories
        File[] directories = currentDir.listFiles(File::isDirectory);
        if (directories != null) {
            System.out.println("\nDirectories only: ");
            for (File dir : directories) {
                System.out.println(" - " + dir.getName());
            }
        }
    }


    // ============== REAL WORLD EXAMPLE: Recursive directory walking ===============
    public void walkDirectory(File dir, int indent) {
        if (!dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                for (int i = 0; i < indent; i++) {
                    System.out.println(" ");
                }

                System.out.println(file.getName());

                if (file.isDirectory()) {
                    walkDirectory(file, indent + 1);
                }
            }
        }

    }

    // ============== REAL WORLD EXAMPLE: Clean up log files ===============
    public void deleteOldLogs(String logDir, int daysOld) {
        File dir = new File(logDir);
        long cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60 * 60 * 1000);

        File[] logFiles = dir.listFiles((d, name) -> name.endsWith(".log"));
        if (logFiles != null) {
            for (File file : logFiles) {
                if (file.lastModified() < cutoffTime) {
                    if (file.delete()) {
                        System.out.println("Deleted file: " + file.getName());
                    }
                }
            }
        }
    }

}
