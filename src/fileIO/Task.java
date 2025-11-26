package fileIO;

/*
 * ✅ Task 3: File Manager Challenge
    Create a program that:

    Creates a directory structure: project/src, project/test, project/docs
    Creates empty files: src/Main.java, test/Test.java, docs/README.txt
    Lists all files recursively with their sizes
    Deletes all .java files
 */

import java.io.File;
import java.io.IOException;

public class Task {

    public void task() {
        System.out.println("=========== Task Kick Off ============");

        // Step 1: Create project folder
        File projectDir = new File("project");
        if (projectDir.mkdir()) {
            File srcDir = new File("project/src");
            File testDir = new File("project/test");
            File docsDir = new File("project/docs");

            if (srcDir.mkdir()) {
                System.out.println("Successfully created src folder: " + srcDir.getName());
            }

            if (testDir.mkdir()) {
                System.out.println("Successfully created test folder: " + testDir.getName());
            }

            if (docsDir.mkdir()) {
                System.out.println("Successfully created docs folder: " + docsDir.getName());
            }
        }

        System.out.println("======== Completed creation of directories ===========");

        // Step 2: Create files in each directory
        System.out.println("========= Add files in each directory ============");

        File mainJava = new File("project/src/Main.java");
        try {
            if (mainJava.createNewFile()) {
                System.out.println("Created Main file: " + mainJava.getPath());
            }
        } catch (IOException ex) {
            System.err.println("Error occurred: " + ex.getMessage());
            ex.printStackTrace();
        }

        File testJava = new File("project/test/Test.java");
        try {
            if (testJava.createNewFile()) {
                System.out.println("Created Main file: " + testJava.getPath());
            }
        } catch (IOException ex) {
            System.err.println("Error occurred: " + ex.getMessage());
            ex.printStackTrace();
        }

        File readMe = new File("project/docs/README.md");
        try {
            if (readMe.createNewFile()) {
                System.out.println("Created Main file: " + readMe.getPath());
            }
        } catch (IOException ex) {
            System.err.println("Error occurred: " + ex.getMessage());
            ex.printStackTrace();
        }

        System.out.println("======= Created Files successfully in each directory under project ===========");

        System.out.println("========= Listing all files in the project folder =========");
        listAllFilesWithSizes(projectDir, 0);

        System.out.println("========== Deleting all Java files in project folder ============");
        deleteAllJavaFiles(projectDir, 0);
    }

    // Step 3: List all files recursively with their sizes recursively
    public void listAllFilesWithSizes(File dir, int indent) {
        if (!dir.isDirectory()) {
            System.out.println("======= The name produced does not match any directory ========");
            return;
        }

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                for (int i = 0; i < indent; i++) {
                    System.out.println(" ");
                }
                System.out.print("File: " + file.getName() + "\n");

                if (file.isDirectory()) {
                    listAllFilesWithSizes(file, indent + 1);
                }
            }
        }

    }

    // Step 4: Delete all files ending with .java
    public void deleteAllJavaFiles(File path, int indent) {
        if (!path.isDirectory()) {
            System.out.println("======== Path produced is not a directory ========");
            return;
        }

        File[] files = path.listFiles();
        if (files != null) {
            for (File file : files) {
                for (int i = 0; i < indent; i++) {
                    System.out.println();
                }

                // If a Java file, delete
                if (file.isFile() && file.getName().endsWith(".java")) {
                    if (file.delete()) {
                        System.out.println("Successfully deleted file: " + file.getName());
                    }
                }

                if (file.isDirectory()) {
                    deleteAllJavaFiles(file, indent + 1);
                }
            }
        }

    }

}
