package streams;

/*
 *
    ### ✅ **Task 1: Your Turn!**

    Create a program that:
    1. Writes your name, age, and favorite programming language to a file `profile.txt`
    2. Reads it back and prints to console
    3. Use **character streams** (FileWriter/FileReader)
 */

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Task {

    public void task() {
        System.out.println("========== Profile Info =========");

        // Create profile file and write the info in it
        try (FileWriter fileWriter = new FileWriter("profile.txt")) {
            fileWriter.write("Hi, im Dogo Vicky, a 21 year old Software Engineer.");
            fileWriter.write(" My favourite programming language is Java");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        // Read the file
        try (FileReader fileReader = new FileReader("profile.txt")) {
            int charData;
            while ((charData = fileReader.read()) != -1) {
                System.out.print((char) charData);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

}
