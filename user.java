import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class user {
    public static Map<String, String> userPasswordStaffMap = new HashMap<>(); // Staff credentials
    public static Map<String, String> userPasswordPatientMap = new HashMap<>(); // Patient credentials
    public static Map<String, String> userRoleStaffMap = new HashMap<>(); // Staff roles
    public static Map<String, String> userRolePatientMap = new HashMap<>(); // Patient roles
    public static Map<String, String> userNameMapStaff = new HashMap<>(); // Staff names
    public static Map<String, String> userNameMapPatient = new HashMap<>(); // Patient names
    private static final String FILE_PATH = "staffs.txt"; // File path for staff data
    private static final String PATIENT_FILE_PATH = "patients.txt"; // File path for patient data
    private static final String STAFF_NAME_FILE_PATH = "staff_names.txt"; // File path for staff names
    private static final String PATIENT_NAME_FILE_PATH = "patient_names.txt"; // File path for patient names

    // Initialize user data (load from files or set default)
    public static void initializeUsers() throws IOException {
        File userFile = new File(FILE_PATH);
        File patientFile = new File(PATIENT_FILE_PATH);
        File staffNameFile = new File(STAFF_NAME_FILE_PATH);
        File patientNameFile = new File(PATIENT_NAME_FILE_PATH);

        if (userFile.exists()) {
            loadUserDataFromFile(); // Load staff data
        } else {
            System.out.println("Staff file does not exist. Adding default staff...");
            setDefaultUsers(); // Set default staff
            saveUserDataToFile(); // Save staff data to file
        }

        if (patientFile.exists()) {
            loadPatientDataFromFile(); // Load patient data
        } else {
            System.out.println("Patient file does not exist. Adding default patients...");
            setDefaultPatients(); // Set default patients
            savePatientDataToFile(); // Save patient data to file
        }

        // Load staff names
        if (staffNameFile.exists()) {
            loadStaffNamesFromFile(); // Load staff names
        } else {
            System.out.println("Staff names file does not exist. Adding default staff names...");
            setDefaultStaffNames(); // Set default staff names
            saveStaffNamesToFile(); // Save staff names to file
        }

        // Load patient names
        if (patientNameFile.exists()) {
            loadPatientNamesFromFile(); // Load patient names
        } else {
            System.out.println("Patient names file does not exist. Adding default patient names...");
            setDefaultPatientNames(); // Set default patient names
            savePatientNamesToFile(); // Save patient names to file
        }
    }

    // Method to change the user's password (for both staff and patients)
    public static void changeUserPassword(String hospitalId) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your new password: ");
        String newPassword = sc.nextLine();

        // Check if the user is a staff or patient
        if (userPasswordStaffMap.containsKey(hospitalId)) {
            userPasswordStaffMap.put(hospitalId, newPassword); // Update staff password
        } else if (userPasswordPatientMap.containsKey(hospitalId)) {
            userPasswordPatientMap.put(hospitalId, newPassword); // Update patient password
        }

        saveUserDataToFile(); // Save staff data
        savePatientDataToFile(); // Save patient data
        System.out.println("Password changed successfully!");
        sc.close();
    }

    // Set default staff users
    private static void setDefaultUsers() {
        userPasswordStaffMap.put("D001", "password"); // Dr. John Smith
        userPasswordStaffMap.put("D002", "password"); // Dr. Emily Clarke
        userPasswordStaffMap.put("A001", "password"); // Sarah Lee (Administrator)
        userPasswordStaffMap.put("PH001", "password"); // Mark Lee (Pharmacist)

        userRoleStaffMap.put("D001", "Doctor");
        userRoleStaffMap.put("D002", "Doctor");
        userRoleStaffMap.put("A001", "Administrator");
        userRoleStaffMap.put("PH001", "Pharmacist");
    }

    // Set default patient users
    private static void setDefaultPatients() {
        userPasswordPatientMap.put("P001", "password"); // Patient Alice Brown
        userPasswordPatientMap.put("P002", "password"); // Patient Bob Stone
        userPasswordPatientMap.put("P003", "password"); // Patient Charlie White

        userRolePatientMap.put("P001", "Patient");
        userRolePatientMap.put("P002", "Patient");
        userRolePatientMap.put("P003", "Patient");
    }

    // Set default staff names
    private static void setDefaultStaffNames() {
        userNameMapStaff.put("D001", "Dr. John Smith");
        userNameMapStaff.put("D002", "Dr. Emily Clarke");
        userNameMapStaff.put("A001", "Sarah Lee");
        userNameMapStaff.put("PH001", "Mark Lee");
    }

    // Set default patient names
    private static void setDefaultPatientNames() {
        userNameMapPatient.put("P001", "Alice Brown");
        userNameMapPatient.put("P002", "Bob Stone");
        userNameMapPatient.put("P003", "Charlie White");
    }

    // File handling methods (load and save data for staff and patients)
    private static void loadStaffNamesFromFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(STAFF_NAME_FILE_PATH));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            userNameMapStaff.put(parts[0], parts[1]);
        }
        reader.close();
    }

    private static void loadPatientNamesFromFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(PATIENT_NAME_FILE_PATH));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            userNameMapPatient.put(parts[0], parts[1]);
        }
        reader.close();
    }

    private static void saveStaffNamesToFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(STAFF_NAME_FILE_PATH));
        for (String id : userNameMapStaff.keySet()) {
            writer.write(id + "," + userNameMapStaff.get(id));
            writer.newLine();
        }
        writer.close();
    }

    private static void savePatientNamesToFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(PATIENT_NAME_FILE_PATH));
        for (String id : userNameMapPatient.keySet()) {
            writer.write(id + "," + userNameMapPatient.get(id));
            writer.newLine();
        }
        writer.close();
    }

    private static void loadUserDataFromFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            userPasswordStaffMap.put(parts[0], parts[1]);
            userRoleStaffMap.put(parts[0], parts[2]);
        }
        reader.close();
    }

    private static void loadPatientDataFromFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(PATIENT_FILE_PATH));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            userPasswordPatientMap.put(parts[0], parts[1]);
            userRolePatientMap.put(parts[0], parts[2]);
        }
        reader.close();
    }

    private static void saveUserDataToFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH));
        for (String id : userPasswordStaffMap.keySet()) {
            writer.write(id + "," + userPasswordStaffMap.get(id) + "," + userRoleStaffMap.get(id));
            writer.newLine();
        }
        writer.close();
    }

    private static void savePatientDataToFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(PATIENT_FILE_PATH));
        for (String id : userPasswordPatientMap.keySet()) {
            writer.write(id + "," + userPasswordPatientMap.get(id) + "," + userRolePatientMap.get(id));
            writer.newLine();
        }
        writer.close();
    }
}
