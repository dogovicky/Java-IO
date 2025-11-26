package buffered;

/*
 *
    ### ✅ **Task 2: CSV Parser Challenge**

    Create a program that:
    1. Creates a CSV file `students.csv` with headers: Name, Age, Grade
    2. Writes 5 student records
    3. Reads the file back and prints only students with Grade > 80

    Example output:
    ```
    High-performing students:
    Alice - Age 20 - Grade 85
    Bob - Age 22 - Grade 92
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Task {

    public void task() {

        System.out.println("========= Process Started ==========");
        createAndReadCSV();
    }

    public void createAndReadCSV() {

        List<List<String>> data = Arrays.asList(
                Arrays.asList("Name", "Age", "Grade"),
                Arrays.asList("Alice Johnson", "20", "74"),
                Arrays.asList("John Doe", "21", "81"),
                Arrays.asList("Jane Smith", "19", "65"),
                Arrays.asList("Bob Williams", "20", "85")
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("students.csv"))) {
            System.out.println("============ Writing line ===============");

            for (List<String> row : data) {
                String line = String.join(",", row);
                writer.write(line);
                writer.newLine();
            }
            System.out.println("============= Successfully wrote CSV File ===============");
        } catch (IOException ex) {
            System.err.println("An error occurred: " + ex.getMessage());
            ex.printStackTrace();
        }

        System.out.println("=========== Reading created file =============");

        try (BufferedReader reader = new BufferedReader(new FileReader("students.csv"))) {
            String line;
            reader.readLine();

            List<String> eligibleStudents = new ArrayList<>();

            while ((line = reader.readLine()) != null) {

                // Split the line into fields [Name, Age, Grade]
                String[] fields = line.split(",");

                // Ensure the line has the expected number of fields
                if (fields.length < 3) continue;

                try {
                    // Get Grade and convert it to an int
                    int grade = Integer.parseInt(fields[2].trim());

                    if (grade >= 80) {
                        eligibleStudents.add(fields[0] + " Grade: " + grade);
                    }
                } catch (NumberFormatException ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace();
                }

                System.out.print(line + "\n");
            }

            System.out.println(eligibleStudents);

            System.out.println("=========== Finished reading file ============");
        } catch (IOException ex) {
            System.err.println("Error occurred: " + ex.getMessage());
            ex.printStackTrace();
        }


    }

}
