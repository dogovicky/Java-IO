package nio;

/*
 * NIO is the modern way of handling File operations
 */

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Stream;

public class NIO2 {

    public void nio2Impl() throws IOException {
        // pathBasics();
        // readingWritingFiles();
        // copyMoveDelete();
        directoryWalking();
    }

    // ========= PATH BASICS ===========
    public void pathBasics() {
        System.out.println("=========== Path Basics ===========");

        // Creating Paths
        Path path1 = Paths.get("data", "files", "document.txt");
        System.out.println("Path 1: " + path1);

        // Path operations
        Path path2 = Paths.get("data/files/document.txt");
        System.out.println("Path 2: " + path2);

        // Path operations
        System.out.println("File name: " + path1.getFileName());
        System.out.println("Parent: " + path1.getParent());
        System.out.println("Root: " + path1.getRoot());

        // Absolute path
        Path absolutePath = path1.toAbsolutePath();
        System.out.println("Absolute Path: " + absolutePath);

        // Normalize: remove redundant elements
        Path messyPath = Paths.get("data/../data/./files/document.txt");
        System.out.println("Messy Path: " + messyPath);
        System.out.println("Normalized: " + messyPath.normalize());
    }

    // ============== READING AND WRITING FILES ================
    public void readingWritingFiles() throws IOException {
        System.out.println("============ Reading & Writing Files ===============");

        Path file = Paths.get("nio_test.txt");

        // Write all lines at once
        List<String> lines = List.of(
                "Line 1: Hello from NIO2",
                "Line 2: This is much cleaner.",
                "Line 3: Much better that the old File API"
        );
        Files.write(file, lines);
        System.out.println("File written: " + file);

        // Read all lines at once
        List<String> readLines = Files.readAllLines(file);
        System.out.println("Read line: ");
        readLines.forEach((line) -> System.out.println(" " + line));

        // Read entire file as string
        String content = Files.readString(file);
        System.out.println("\n Full Content: \n" + content);

        // Write String directly
        Files.writeString(file, "Overwritten original content with new content");
        System.out.println("\n File Overwritten: ");

        // Append to file
        Files.writeString(file, "\nAppended Line", StandardOpenOption.APPEND);
        System.out.println("Updated content: ");
        String updatedContent = Files.readString(file);
        System.out.println("\n" + updatedContent);
    }


    // ============== COPY, MOVE, DELETE ===============
    public void copyMoveDelete() throws IOException {
        System.out.println("====== Copy, Move and Delete =======");

        // Create source file
        Path source = Paths.get("source.txt");
        Files.writeString(source, "Original file content");
        System.out.println("Created source: " + source);

        // Copy file
        Path copy = Paths.get("copy.txt");
        Files.copy(source, copy, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Copied to: " + copy);

        // Move / Rename file
        Path moved = Paths.get("moved.txt");
        Files.move(copy, moved, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Moved to: " + moved);

        // Check if file exists
        System.out.println("Source exists? " + Files.exists(source));
        System.out.println("Moved exists? " + Files.exists(moved));

        // Get file attributes
        BasicFileAttributes fileAttributes = Files.readAttributes(moved, BasicFileAttributes.class);
        System.out.println("File size: " + fileAttributes.size() + " bytes");
        System.out.println("Is Directory: " + fileAttributes.isDirectory());
        System.out.println("Created At: " + fileAttributes.creationTime());

        // Delete files
        Files.deleteIfExists(source);
        Files.deleteIfExists(moved);
        System.out.println("Cleaned up files");
    }

    // ============= DIRECTORY WALKING ==============
    public void directoryWalking() throws IOException {
        System.out.println("======== Directory Walking ==========");

        // Create a test directory structure
        Path testDir = Paths.get("test_dir");
        Files.createDirectories(testDir.resolve("subDir1"));
        Files.createDirectories(testDir.resolve("subDir2"));
        Files.writeString(testDir.resolve("file1.txt"), "Test 1");
        Files.writeString(testDir.resolve("subDir1/test2.txt"), "Test 2 on sub directory 1");
        Files.writeString(testDir.resolve("subDir2/test3.txt"), "Test 3 on sub directory 2");

        System.out.println("Created directory structure\n");

        // ============ Walk file tree ===========
        System.out.println("All files and directories:");
        try (Stream<Path> paths = Files.walk(testDir)) {
            paths.forEach((path) -> {
                int depth = path.getNameCount() - testDir.getNameCount();
                String indent = " ".repeat(depth);
                System.out.println(indent + path.getFileName());
            });
        }

        // List directory contents, one level only
        System.out.println("\nDirect children of test_dir:");
        try (Stream<Path> paths = Files.walk(testDir)) {
            paths.forEach((path) -> System.out.println(" " + path.getFileName()));
        }

        // Find files matching pattern
        System.out.println("\n Find all .txt files");
        try(Stream<Path> paths = Files.find(testDir, 10,
                (path, attrs) -> path.toString().endsWith(".txt"))) {
            paths.forEach((path) -> System.out.println(" " + path));
        }

        // Clean up
        deleteDirectory(testDir);
        System.out.println("Cleaned up test directory.");

    }

    public void deleteDirectory(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walk(dir)
                    .sorted((p1, p2) -> -p1.compareTo(p2)) // Reverse order (files before directories)
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });

        }
    }

    // ============ REAL WORLD: Configuration File Manager =================
    public static class ConfigManager {
        private final Path config = Paths.get("config");

        public void initialize() throws IOException {
            Files.createDirectories(config);
        }

        public void saveConfig(String name, String content) throws IOException {
            Path configFile = config.resolve(name + ".properties");
            Files.writeString(configFile, content);
        }

        public String loadConfig(String name) throws IOException {
            Path configFile = config.resolve(name + ".properties");
            if (Files.exists(configFile)) {
                return Files.readString(configFile);
            }
            return null;
        }

        public List<String> listConfigs() throws IOException {
            try(Stream<Path> paths = Files.list(config)) {
                return paths
                        .filter(p -> p.toString().endsWith(".properties"))
                        .map(p -> p.getFileName().toString())
                        .toList();
            }
        }

    }

    // ============== REAL WORLD: Cache Manager ==============
    public static class CacheManager {
        private final Path cacheDir = Paths.get("cache");
        private final long maxAge = 24 * 60 * 60 * 1000; // 24 Hours

        public void cleanOldCache() throws IOException {
            if (!Files.exists(cacheDir)) return;

            long cutOff = System.currentTimeMillis() - maxAge;

            try (Stream<Path> paths = Files.walk(cacheDir)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> {
                            try {
                                return Files.getLastModifiedTime(path).toMillis() < cutOff;
                            } catch (IOException e) {
                                return false;
                            }
                        })
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                                System.out.println("Deleted old cache: " + path.getFileName());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }

        }

    }







}
