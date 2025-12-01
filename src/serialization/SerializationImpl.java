package serialization;

/*
 * What is Serialization?
    Serialization: Convert object → byte stream (to save or send)
    Deserialization: Convert byte stream → object (to load or receive)
   Object in Memory → Serialize → Bytes → Send/Save
                              ↓
                         Deserialize
                              ↓
                      Object in Memory
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SerializationImpl {

    // ============ SERIALIZABLE CLASS =============
    static class User implements Serializable {
        private static final long serialVersionID = 1L; // Version control

        private String username;
        private String email;
        private transient String password; // NOT Serialized
        private int age;

        public User(String username, String email, String password, int age) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.age = age;
        }

        public String toString() {
            return "User{username='" + username + "', email='" + email +
                    "', password='" + password + "', age=" + age + "}";
        }

    }

    public void serializationImpl() throws FileNotFoundException {
        basicSerialization();
        collectionSerialization();
    }

    // ============= BASIC SERIALIZATION =============
    public void basicSerialization() throws FileNotFoundException {
        System.out.println("======== Basic Serialization =========");

        User user = new User("Dogo", "dogo@email.com", "secret123", 20);
        System.out.println("Original: " + user);

        // Serialize
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("user.ser"))) {
            outputStream.writeObject(user);
            System.out.println("✅ Serialized user to user.ser");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Deserialize
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("user.ser"))) {
            User deserializedUser = (User) inputStream.readObject();
            System.out.println("Deserialized user: " + deserializedUser);
            System.out.println("\n⚠️ Notice: password is null (transient field)");
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    // =========== SERIALIZING COLLECTIONS =============
    public void collectionSerialization() {
        System.out.println("======= Serializing collections =========");

        List<User> users = new ArrayList<>();
        users.add(new User("bob", "bob@example.com", "pass1", 30));
        users.add(new User("charlie", "charlie@example.com", "pass2", 28));
        users.add(new User("diana", "diana@example.com", "pass3", 32));

        // Serialize list
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("users.ser"))) {
            outputStream.writeObject(users);
            System.out.println("✅ Serialized " + users.size() + " users");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Deserialize list
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("users.ser"))) {
            @SuppressWarnings("unchecked")
            List<User> loadedUsers = (List<User>) inputStream.readObject();
            System.out.println("✅ Deserialized " + loadedUsers.size() + " users");
            loadedUsers.forEach(System.out::println);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    // ========== WHY SERIALIZATION IS RISKY ==========
    public static void whySerializationIsRisky() {
        System.out.println("=== Why Java Serialization is Risky ===\n");

        System.out.println("❌ PROBLEMS:");
        System.out.println("1. Security vulnerabilities");
        System.out.println("   - Deserialization can execute arbitrary code");
        System.out.println("   - Many CVEs related to Java serialization");
        System.out.println("   - Used in famous exploits (e.g., Log4Shell)\n");

        System.out.println("2. Versioning nightmares");
        System.out.println("   - Change class structure → breaks deserialization");
        System.out.println("   - serialVersionUID must match");
        System.out.println("   - Hard to maintain compatibility\n");

        System.out.println("3. Performance");
        System.out.println("   - Slow compared to modern alternatives");
        System.out.println("   - Large serialized size\n");

        System.out.println("4. Not human-readable");
        System.out.println("   - Can't inspect .ser files");
        System.out.println("   - Debugging is difficult\n");

        System.out.println("✅ BETTER ALTERNATIVES:");
        System.out.println("- JSON (Jackson, Gson) - Human readable, interoperable");
        System.out.println("- Protocol Buffers - Fast, compact, versioned");
        System.out.println("- MessagePack - Faster than JSON, smaller size");
        System.out.println("- Avro - Schema evolution, compression");
    }

    // ============ REAL WORLD ===============
    private static class SessionManager {

        // Risky
        public void saveSessionOldWay(HttpSession httpSession) throws IOException {
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("session.ser"))) {
                outputStream.writeObject(httpSession.getAttribute("user"));
            }
        }

        // Much better approach
        public void saveSessionModernWay(User user) {
            // Use JSON instead
            String json = toJson(user);
            // Save to redis, database, etc.
        }

        public String toJson(User user) {
            // In real app, use Jackson or Gson
            return "{\"username\":\"" + user.username +
                    "\",\"email\":\"" + user.email + "\"}";
        }

    }

    // Dummy class for example
    static class HttpSession {
        public Object getAttribute(String name) {
            return null;
        }
    }

}
