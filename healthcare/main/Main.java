package healthcare.main;

import healthcare.users.controllers.AdministratorController;
import healthcare.users.controllers.DoctorController;
import healthcare.users.controllers.PatientController;
import healthcare.users.controllers.PharmacistController;
import healthcare.users.controllers.UserController;
import healthcare.users.models.PatientModel;
import healthcare.users.models.DoctorModel;
import healthcare.users.models.AdministratorModel;
import healthcare.users.models.PharmacistModel;
import healthcare.users.view.PatientView;
import healthcare.users.view.DoctorView;
import healthcare.users.view.AdministratorView;
import healthcare.users.view.PharmacistView;
import healthcare.users.User;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        UserController.initializeUsers();
        loadAllData(); // Load data once at startup
        while (true) {
            showMainMenu();
        }
    }

    private static final Scanner sc = new Scanner(System.in);
    private static final String patientListFile = "Patient_List.csv";
    private static final String doctorListFile = "Doctor_List.csv";
    private static final String staffListFile = "Staff_List.csv";
    private static final String patientPasswordsFile = "Patient_Passwords.csv";
    private static final String doctorPasswordsFile = "Doctor_Passwords.csv";
    private static final String staffPasswordsFile = "Staff_Passwords.csv";

    // Memoized data maps for controllers
    private static Map<String, PatientController> patientMap;
    private static Map<String, DoctorController> doctorMap;
    private static Map<String, PharmacistController> pharmacistMap;
    private static Map<String, AdministratorController> administratorMap;

    // Utility method to hash passwords with SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hashString = new StringBuilder();
            for (byte b : hashBytes) {
                hashString.append(String.format("%02x", b));
            }
            return hashString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available.");
        }
    }

    private static void loadAllData() throws IOException {
        // Load all data once to avoid repeated file I/O
        patientMap = loadPatientsFromCSV();
        doctorMap = loadDoctorsFromCSV();
        pharmacistMap = loadPharmacistsFromCSV();
        administratorMap = loadAdministratorsFromCSV();
    }

    private static Map<String, PatientController> loadPatientsFromCSV() throws IOException {
        if (patientMap != null) return patientMap; // Memoization check
        patientMap = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(patientListFile));
        for (String line : lines) {
            String[] details = line.split(",");
            String patientID = details[0].trim();
            PatientModel model = new PatientModel(patientID, details[1], details[2], details[3], details[4], details[5], details[6]);
            PatientView view = new PatientView();
            PatientController controller = new PatientController(model, view);
            patientMap.put(patientID, controller);
        }
        return patientMap;
    }

    private static Map<String, DoctorController> loadDoctorsFromCSV() throws IOException {
        if (doctorMap != null) return doctorMap; // Memoization check
        doctorMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(doctorListFile))) {
            String line = reader.readLine(); // Skip header line if necessary
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String doctorID = data[0];
                DoctorModel model = new DoctorModel(doctorID, data[1], data[2], data[3]);
                DoctorView view = new DoctorView();
                DoctorController controller = new DoctorController(model, view);
                doctorMap.put(doctorID, controller);
            }
        }
        return doctorMap;
    }

    private static Map<String, PharmacistController> loadPharmacistsFromCSV() throws IOException {
        if (pharmacistMap != null) return pharmacistMap; // Memoization check
        pharmacistMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(staffListFile))) {
            String line = reader.readLine(); // Skip header line if necessary
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[2].trim().equalsIgnoreCase("Pharmacist")) {
                    String pharmacistID = data[0];
                    PharmacistModel model = new PharmacistModel(pharmacistID, data[1], data[3], data[4]);
                    PharmacistView view = new PharmacistView();
                    PharmacistController controller = new PharmacistController(model, view);
                    pharmacistMap.put(pharmacistID, controller);
                }
            }
        }
        return pharmacistMap;
    }

    private static Map<String, AdministratorController> loadAdministratorsFromCSV() throws IOException {
        if (administratorMap != null) return administratorMap; // Memoization check
        administratorMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(staffListFile))) {
            String line = reader.readLine(); // Skip header line if necessary
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[2].trim().equalsIgnoreCase("Administrator")) {
                    String administratorID = data[0];
                    AdministratorModel model = new AdministratorModel(administratorID);
                    AdministratorView view = new AdministratorView();
                    AdministratorController controller = new AdministratorController(model, view);
                    administratorMap.put(administratorID, controller);
                }
            }
        }
        return administratorMap;
    }

    private static void showMainMenu() throws IOException {
        System.out.println("\nWelcome to the Hospital Management System (HMS)");
        System.out.println("1. Register As Patient");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        int choice = sc.nextInt();
        sc.nextLine(); // Consume newline
        switch (choice) {
            case 1 -> registerUser();
            case 2 -> showLoginScreen();
            case 3 -> System.exit(0);
            case 4 -> registerAdmin();
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private static void registerUser() {
        try {
            System.out.println("Registering a new patient:");
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Date of Birth (YYYY-MM-DD): ");
            String dob = sc.nextLine();
            System.out.print("Enter Gender: ");
            String gender = sc.nextLine();
            System.out.print("Enter Blood Type: ");
            String bloodType = sc.nextLine();
            System.out.print("Enter Email Address: ");
            String email = sc.nextLine();
            System.out.print("Enter Contact Number: ");
            String phoneNumber = sc.nextLine();

            String newPatientID = generateNewPatientID();
            String newEntry = newPatientID + "," + name + "," + dob + "," + gender + "," + bloodType + "," + email + "," + phoneNumber;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(patientListFile, true))) {
                writer.write(newEntry);
                writer.newLine();
                System.out.println("Registration successful! Your Patient ID is: " + newPatientID);
            }

            String defaultPassword = "password";
            String hashedPassword = hashPassword(defaultPassword);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(patientPasswordsFile, true))) {
                writer.write(newPatientID + "," + hashedPassword + ",Patient");
                writer.newLine();
                System.out.println("Your account has been created with the default password.");
            }

            User.initializeUsers();
            System.out.println("User data reloaded successfully.");
            
        } catch (IOException e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }

    private static String generateNewPatientID() {
        String lastPatientID = "";
        try {
            List<String> lines = Files.readAllLines(Paths.get(patientListFile));
            if (!lines.isEmpty()) {
                String lastLine = lines.get(lines.size() - 1);
                lastPatientID = lastLine.split(",")[0];
            }
        } catch (IOException e) {
            System.out.println("Error reading patient list: " + e.getMessage());
        }
        if (lastPatientID.isEmpty()) {
            return "P1001";
        }
        int lastIDNumber = Integer.parseInt(lastPatientID.substring(1));
        return "P" + (lastIDNumber + 1);
    }

    private static void registerAdmin() {
        try {
            System.out.println("Registering a new patient:");
            System.out.print("Enter ID: ");
            String id = sc.nextLine();
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Role: ");
            String dob = sc.nextLine();
            System.out.print("Enter Gender: ");
            String gender = sc.nextLine();
            System.out.print("Enter Age: ");
            String bloodType = sc.nextLine();

            String newEntry = id + "," + name + "," + dob + "," + gender + "," + bloodType;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(staffListFile, true))) {
                writer.write(newEntry);
                writer.newLine();
                System.out.println("Registration successful! Your Patient ID is: " + id);
            }

            String defaultPassword = "password";
            String hashedPassword = hashPassword(defaultPassword);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(staffPasswordsFile, true))) {
                writer.write(id + "," + hashedPassword + "," + dob);
                writer.newLine();
                System.out.println("Your account has been created with the default password.");
            }

            User.initializeUsers();
            System.out.println("User data reloaded successfully.");
            
        } catch (IOException e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }

    private static void showLoginScreen() throws IOException {
        boolean loginSuccessful = false;
        String hospitalId = "";
        User.initializeUsers();
        while (!loginSuccessful) {
            System.out.print("Enter Hospital ID: ");
            if (!sc.hasNextLine()) {
                System.out.println("No input found. Exiting program.");
                return;
            }
            hospitalId = sc.nextLine().toUpperCase();

            if (!User.userPasswordStaffMap.containsKey(hospitalId)
                    && !User.userPasswordPatientMap.containsKey(hospitalId)) {
                System.out.println("Invalid Hospital ID. Please try again.");
                continue;
            }

            System.out.print("Enter Password: ");
            if (!sc.hasNextLine()) {
                System.out.println("No input found. Exiting program.");
                return;
            }
            String password = sc.nextLine();
            String hashedPassword = hashPassword(password);

            if ((User.userPasswordStaffMap.containsKey(hospitalId)
                    && User.userPasswordStaffMap.get(hospitalId).equals(hashedPassword)) ||
                    (User.userPasswordPatientMap.containsKey(hospitalId)
                            && User.userPasswordPatientMap.get(hospitalId).equals(hashedPassword))) {
                loginSuccessful = true;
                System.out.println("Login successful!");
            } else {
                System.out.println("Incorrect password. Please try again.");
            }
        }

        String name = User.userNameMapStaff.containsKey(hospitalId) ? User.userNameMapStaff.get(hospitalId)
                : User.userNameMapPatient.get(hospitalId);
        System.out.println("Good Day " + name + "!");

        System.out.print("Do you want to change your password? (yes/no): ");
        if (!sc.hasNextLine()) {
            System.out.println("No input found. Exiting program.");
            return;
        }
        String changePassword = sc.nextLine();
        if (changePassword.equalsIgnoreCase("yes")) {
            System.out.print("Enter your new password: ");
            if (!sc.hasNextLine()) {
                System.out.println("No input found. Exiting program.");
                return;
            }
            String newPassword = sc.nextLine();
            String newHashedPassword = hashPassword(newPassword);
            User.changeUserPassword(hospitalId, newHashedPassword);
        }

        Map<String, DoctorController> doctorMap = loadDoctorsFromCSV();
        Map<String, PharmacistController> pharmacistMap = loadPharmacistsFromCSV();
        Map<String, AdministratorController> administratorMap = loadAdministratorsFromCSV();
        Map<String, PatientController> patientMap = loadPatientsFromCSV();

        String role = User.userRoleStaffMap.containsKey(hospitalId) ? User.userRoleStaffMap.get(hospitalId)
                : User.userRolePatientMap.get(hospitalId);
        switch (role) {
            case "Patient":
                PatientController patient = patientMap.get(hospitalId);
                if (patient != null) {
                    patient.showPatientMenu();
                } else {
                    System.out.println("Patient details not found.");
                }
                break;
            case "Doctor":       
                DoctorController doctor = doctorMap.get(hospitalId);
                if (doctor != null) {
                    doctor.showMenu();
                } else {
                    System.out.println("Doctor details not found.");
                }
                break;
            case "Pharmacist":
                PharmacistController pharmacist = pharmacistMap.get(hospitalId);
                if (pharmacist != null) {
                    pharmacist.showMenu();
                } else {
                    System.out.println("Pharmacist details not found.");
                }
                break;
            case "Administrator":
                AdministratorController administrator = administratorMap.get(hospitalId);
                if (administrator != null) {
                    administrator.administratorMenu();
                } else {
                    System.out.println("Administrator details not found.");
                }
                break;
            default:
                System.out.println("Invalid role! Exiting...");
                break;
        }
    }
}
