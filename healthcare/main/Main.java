package healthcare.main;

import healthcare.users.Administrator;
import healthcare.users.Doctor;
import healthcare.users.Patient;
import healthcare.users.Pharmacist;
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
        User.initializeUsers();
        while (true) {
            showMainMenu();
        }
    }

    private static final Scanner sc = new Scanner(System.in);
    private static final String patientListFile = "Patient_List.csv";
    private static final String patientPasswordsFile = "Patient_Passwords.csv";

    // Utility method to hash passwords with SHA-256
    private static String hashPassword(String password) {
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

    private static Map<String, Patient> loadPatientsFromCSV() throws IOException {
        Map<String, Patient> patientMap = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get("Patient_List.csv"));
        for (String line : lines) {
            String[] details = line.split(",");
            String patientID = details[0].trim();
            Patient patient = new Patient(patientID);
            patientMap.put(patientID, patient);
        }
        return patientMap;
    }

    private static Map<String, Doctor> loadDoctorsFromCSV() {
        Map<String, Doctor> doctorMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Doctor_List.csv"))) {
            String line = reader.readLine(); // Skip header line if necessary
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[2].trim().equalsIgnoreCase("Doctor")) {
                    String doctorID = data[0];
                    String name = data[1];
                    String gender = data[3];
                    String age = data[4];
                    String specialisation = data[5].trim();
                    Doctor doctor = new Doctor(doctorID, name, gender, age, specialisation);
                    doctorMap.put(doctorID, doctor);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading doctor data: " + e.getMessage());
        }
        return doctorMap;
    }

    private static Map<String, Pharmacist> loadPharmacistsFromCSV() {
        Map<String, Pharmacist> pharmacistMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Staff_List.csv"))) {
            String line = reader.readLine(); // Skip header line if necessary
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[2].trim().equalsIgnoreCase("Pharmacist")) {
                    String pharmacistID = data[0];
                    String name = data[1];
                    String gender = data[3];
                    String age = data[4].trim();
                    Pharmacist pharmacist = new Pharmacist(pharmacistID, name, gender, age);
                    pharmacistMap.put(pharmacistID, pharmacist);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading pharmacist data: " + e.getMessage());
        }
        return pharmacistMap;
    }

    private static Map<String, Administrator> loadAdministratorsFromCSV() {
        Map<String, Administrator> administratorMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Staff_List.csv"))) {
            String line = reader.readLine(); // Skip header line if necessary
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[2].trim().equalsIgnoreCase("Administrator")) {
                    String administratorID = data[0];
                    String name = data[1];
                    String gender = data[3];
                    String age = data[4].trim();
                    Administrator administrator = new Administrator(administratorID, name, gender, age);
                    administratorMap.put(administratorID, administrator);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading administrator data: " + e.getMessage());
        }
        return administratorMap;
    }

    private static void showMainMenu() throws IOException {
        System.out.println("\n");
        System.out.println("Welcome to the Hospital Management System (HMS)");
        System.out.println("1. Register As Patient");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        int choice = sc.nextInt();
        sc.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                registerUser();
                break;
            case 2:
                showLoginScreen();
                break;
            case 3:
                System.out.println("Exiting the Hospital Management System. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
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

    private static void showLoginScreen() throws IOException {
        boolean loginSuccessful = false;
        String hospitalId = "";

        while (!loginSuccessful) {
            System.out.print("Enter Hospital ID: ");
            if (!sc.hasNextLine()) {
                System.out.println("No input found. Exiting program.");
                return;
            }
            hospitalId = sc.nextLine();

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

        Map<String, Doctor> doctorMap = loadDoctorsFromCSV();
        Map<String, Pharmacist> pharmacistMap = loadPharmacistsFromCSV();
        Map<String, Administrator> administratorMap = loadAdministratorsFromCSV();

        String role = User.userRoleStaffMap.containsKey(hospitalId) ? User.userRoleStaffMap.get(hospitalId)
                : User.userRolePatientMap.get(hospitalId);
        switch (role) {
            case "Patient":
                Map<String, Patient> patientMap = loadPatientsFromCSV();
                Patient patient = patientMap.get(hospitalId);
                if (patient != null) {
                    patient.patientmenu();
                } else {
                    System.out.println("Patient details not found.");
                }
                break;
            case "Doctor":       
                Doctor doctor = doctorMap.get(hospitalId);
                if (doctor != null) {
                    doctor.doctormenu();
                } else {
                    System.out.println("Doctor details not found.");
                }
                break;
            case "Pharmacist":
                Pharmacist pharmacist = pharmacistMap.get(hospitalId);
                if (pharmacist != null) {
                    pharmacist.pharmacistMenu();
                } else {
                    System.out.println("Pharmacist details not found.");
                }
                break;
            case "Administrator":
                Administrator administrator = administratorMap.get(hospitalId);
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
