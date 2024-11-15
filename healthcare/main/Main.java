package healthcare.main;

import healthcare.records.Obfuscation;
import healthcare.users.controllers.AdministratorController;
import healthcare.users.controllers.DoctorController;
import healthcare.users.controllers.PatientController;
import healthcare.users.controllers.PharmacistController;
import healthcare.users.controllers.UserController;
import healthcare.users.models.AdministratorModel;
import healthcare.users.models.DoctorModel;
import healthcare.users.models.PatientModel;
import healthcare.users.models.PharmacistModel;
import healthcare.users.models.UserModel;
import healthcare.users.view.AdministratorView;
import healthcare.users.view.DoctorView;
import healthcare.users.view.PatientView;
import healthcare.users.view.PharmacistView;
import healthcare.users.view.Screen;
import healthcare.users.view.UserView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        UserController.initializeUsers();
        if (patientMap != null)
            return patientMap; // Memoization check
        patientMap = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(patientListFile));
        for (String line : lines) {
            String[] details = line.split(",");
            String patientID = details[0].trim();
            PatientModel model = new PatientModel(patientID, details[1], details[2], details[3], details[4], details[5],
                    details[6]);
            PatientView view = new PatientView();
            PatientController controller = new PatientController(model, view);
            patientMap.put(patientID, controller);
        }
        return patientMap;
    }

    private static Map<String, DoctorController> loadDoctorsFromCSV() throws IOException {
        UserController.initializeUsers();
        if (doctorMap != null)
            return doctorMap; // Memoization check
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
        UserController.initializeUsers();
        if (pharmacistMap != null)
            return pharmacistMap; // Memoization check
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
        UserController.initializeUsers();
        if (administratorMap != null)
            return administratorMap; // Memoization check
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
        Screen.clearConsole();
        System.out.println("=================================================");
        System.out.println("░▒▓█▓▒  ▒▓█▓▒ ▒▓██████████████▓▒░ ░▒▓███████▓▒░");
        System.out.println("░▒▓█▓▒  ▒▓█▓▒ ▒▓█▓▒  ▒▓█▓▒  ▒▓█▓▒ ▒▓█▓▒░        ");
        System.out.println("░▒▓█▓▒  ▒▓█▓▒ ▒▓█▓▒  ▒▓█▓▒  ▒▓█▓▒ ▒▓█▓▒░        ");
        System.out.println("░▒▓████████▓▒ ▒▓█▓▒  ▒▓█▓▒  ▒▓█▓▒ ░▒▓██████▓▒░  ");
        System.out.println("░▒▓█▓▒  ▒▓█▓▒ ▒▓█▓▒  ▒▓█▓▒  ▒▓█▓▒       ░▒▓█▓▒░ ");
        System.out.println("░▒▓█▓▒  ▒▓█▓▒ ▒▓█▓▒  ▒▓█▓▒  ▒▓█▓▒       ░▒▓█▓▒░ ");
        System.out.println("░▒▓█▓▒  ▒▓█▓▒ ▒▓█▓▒  ▒▓█▓▒  ▒▓█▓▒ ▒▓███████▓▒░  ");
        System.out.println("=================================================");
        System.out.println("\nWelcome to the Hospital Management System (HMS)");
        System.out.println("1. Register As Patient");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.println("=================================================");
        System.out.print("Choose an option: ");
        int choice = sc.nextInt();
        sc.nextLine(); // Consume newline
        switch (choice) {
            case 1 -> registerUser();
            case 2 -> showLoginScreen();
            case 3 -> exitApp();
            case 4 -> registerAdmin();
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private static void exitApp() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("               EXITING APP                 ");
        System.out.println("===========================================");
        System.out.println("   Thank you for using the application!");
        System.out.println("===========================================");
        System.out.println("\nPress Enter to exit...");
        new Scanner(System.in).nextLine();

        // Exit the application
        System.exit(0);
    }

        private static void registerUser() {
            try {
                Screen.clearConsole(); // Clear the console at the start
                System.out.println("===============================================");
                System.out.println("          Register a New Patient              ");
                System.out.println("===============================================");
    
                // Name input
                System.out.println("-> Please enter the following details:");
                System.out.print("   Name: ");
                String name = sc.nextLine();
                if (!name.matches("[a-zA-Z ]+")) {
                    System.out.println("Error: Name must contain only letters and spaces.");
                    displayMenu(); // Call your method to display the menu again
                    return;
                }
    
                // Date of Birth input
                System.out.print("   Date of Birth (YYYY-MM-DD): ");
                String dob = sc.nextLine();
                if (!isValidDate(dob)) {
                    System.out.println("Error: Invalid date format. Please use YYYY-MM-DD.");
                    displayMenu();
                    return;
                }
    
                // Gender input
                System.out.print("   Gender: ");
                String gender = sc.nextLine();
                if (!gender.matches("(?i)Male|Female|Other")) {
                    System.out.println("Error: Gender must be 'Male', 'Female', or 'Other'.");
                    displayMenu();
                    return;
                }
    
                // Blood Type input
                System.out.print("   Blood Type: ");
                String bloodType = sc.nextLine();
                if (!bloodType.matches("(?i)(A|B|AB|O)[+-]")) {
                    System.out.println("Error: Blood type must be A+, A-, B+, B-, AB+, AB-, O+, or O-.");
                    displayMenu();
                    return;
                }
    
                // Email input
                System.out.print("   Email Address: ");
                String email = sc.nextLine();
                if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                    System.out.println("Error: Invalid email format.");
                    displayMenu();
                    return;
                }
    
                // Phone Number input
                System.out.print("   Contact Number: ");
                String phoneNumber = sc.nextLine();
                if (!phoneNumber.matches("\\d{8}")) {
                    System.out.println("Error: Contact number must contain 8 digits.");
                    displayMenu();
                    return;
                }
    
    
                // Generate new patient ID and set a default password
                String newPatientID = "P" + (UserModel.userNameMapPatient.size() + 1000);
                String defaultPassword = "password";
                String hashedPassword = Obfuscation.hashPassword(defaultPassword);
                String role = "Patient";
    
                // Create new UserModel, Controller, and View
                UserModel model = new UserModel(newPatientID, hashedPassword, role, name);
                UserView view = new UserView();
                UserController controller = new UserController(model, view);
    
                // Store new user data
                controller.registerUser(newPatientID, name, dob, gender, bloodType, email, phoneNumber, role, hashedPassword);
    
                Screen.clearConsole();
                System.out.println("===============================================");
                System.out.println(" Registration Successful!                      ");
                System.out.println(" Your Patient ID is: " + newPatientID);
                System.out.println(" Your account has been created with a default  ");
                System.out.println(" password. Please log in to change it.         ");
                System.out.println("===============================================");
    
                System.out.println("\nPress Enter to go back to the main menu...");
                sc.nextLine();
                Screen.clearConsole();
                showMainMenu(); // Show the main menu again
    
            } catch (IOException e) {
                System.out.println("Error during registration: " + e.getMessage());
                displayMenu(); // Show the main menu again after an error
            }
        }
    
        // Helper method for date validation
        private static boolean isValidDate(String date) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                sdf.parse(date);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }
    
        private static void displayMenu() {
            while (true) {
                System.out.println("===============================================");
                System.out.println("           Registration Menu                   ");
                System.out.println("===============================================");
                System.out.println("1. Restart Registration");
                System.out.println("2. Exit");
                System.out.print("Enter your choice: ");
    
                int choice = sc.nextInt();
                sc.nextLine(); // Consume the newline character
    
                switch (choice) {
                    case 1:
                        registerUser();
                        break;
    
                    case 2:
                        System.out.println("Exiting the program. Goodbye!");
                        System.exit(0); // Exit the program
    
                    default:
                        System.out.println("Invalid choice. Please select 1 or 2.");
                }
            }
        }

    private static void registerAdmin() {
        try {
            System.out.println("===========================================");
            System.out.println("           Register a New Patient          ");
            System.out.println("===========================================");

            // Collecting user details
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

            // Creating new entry for the staff list
            String newEntry = id + "," + name + "," + dob + "," + gender + "," + bloodType;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(staffListFile, true))) {
                writer.write(newEntry);
                writer.newLine();
                System.out.println("===========================================");
                System.out.println("Registration successful! Your Patient ID is: " + id);
            }

            // Creating a default password and storing it in the password file
            String defaultPassword = "password";
            String hashedPassword = hashPassword(defaultPassword);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(staffPasswordsFile, true))) {
                writer.write(id + "," + hashedPassword + "," + dob);
                writer.newLine();
                System.out.println("Your account has been created with the default password.");
                System.out.println("===========================================");
            }

            // Reloading user data
            UserController.initializeUsers();
            System.out.println("User data reloaded successfully.");
            System.out.println("===========================================");

        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("Error during registration: " + e.getMessage());
            System.out.println("===========================================");
        }
    }

    private static void showLoginScreen() throws IOException {
        boolean loginSuccessful = false;
        String hospitalId = "";
        UserController.initializeUsers(); // Initialize users at the start

        while (!loginSuccessful) {
            Screen.clearConsole();
            System.out.println("===========================================");
            System.out.println("              Login Screen                ");
            System.out.println("===========================================");
            System.out.print("Enter Hospital ID: ");
            if (!sc.hasNextLine()) {
                System.out.println("No input found. Exiting program.");
                return;
            }
            hospitalId = sc.nextLine().toUpperCase();

            // Check if the hospital ID exists in either staff or patient maps
            if (!UserModel.userPasswordStaffMap.containsKey(hospitalId)
                    && !UserModel.userPasswordPatientMap.containsKey(hospitalId)) {
                System.out.println("Invalid Hospital ID. Please try again.");
                continue;
            }

            // Get the password securely using the getPasswordInput method
            String password;
            try {
                password = Obfuscation.getPasswordInput("Enter Password: ");
            } catch (RuntimeException e) {
                // Fallback if Console is not available (e.g., when running in some IDEs)
                System.out.print("Enter Password (fallback to visible input): ");
                password = sc.nextLine();
            }
            String hashedPassword = Obfuscation.hashPassword(password); // Hash the entered password

            // Check if the hashed password matches the stored hashed password for the user
            if ((UserModel.userPasswordStaffMap.containsKey(hospitalId)
                    && UserModel.userPasswordStaffMap.get(hospitalId).equals(hashedPassword))
                    || (UserModel.userPasswordPatientMap.containsKey(hospitalId)
                            && UserModel.userPasswordPatientMap.get(hospitalId).equals(hashedPassword))) {
                loginSuccessful = true;
                System.out.println("Login successful!");
            } else {
                System.out.println("Incorrect password. Please try again.");
            }
        }

        // Retrieve and display the user's name
        String name = UserModel.userNameMapStaff.containsKey(hospitalId)
                ? UserModel.userNameMapStaff.get(hospitalId)
                : UserModel.userNameMapPatient.get(hospitalId);
        System.out.println("Good Day " + name + "!");

        // Offer the option to change the password
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
            String newHashedPassword = Obfuscation.hashPassword(newPassword); // Hash the new password
            UserController.changeUserPassword(hospitalId, newHashedPassword);
            System.out.println("Password updated successfully.");
        }

        // Load maps for different user roles
        Map<String, DoctorController> doctorMap = loadDoctorsFromCSV();
        Map<String, PharmacistController> pharmacistMap = loadPharmacistsFromCSV();
        Map<String, AdministratorController> administratorMap = loadAdministratorsFromCSV();
        Map<String, PatientController> patientMap = loadPatientsFromCSV();

        // Determine the role of the user and display the appropriate menu
        String role = UserModel.userRoleStaffMap.containsKey(hospitalId)
                ? UserModel.userRoleStaffMap.get(hospitalId)
                : UserModel.userRolePatientMap.get(hospitalId);

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