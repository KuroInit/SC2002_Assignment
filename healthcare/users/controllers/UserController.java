package healthcare.users.controllers;

import healthcare.users.models.UserModel;
import healthcare.users.view.UserView;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserController {
    private UserModel model;
    private UserView view;

    private static final String DOCTOR_PASSWORDS_FILE = "Doctor_Passwords.csv";
    private static final String PATIENT_PASSWORDS_FILE = "Patient_Passwords.csv";
    private static final String STAFF_PASSWORDS_FILE = "Staff_Passwords.csv";
    private static final String DOCTOR_LIST_FILE = "Doctor_List.csv";
    private static final String PATIENT_LIST_FILE = "Patient_List.csv";
    private static final String STAFF_LIST_FILE = "Staff_List.csv";

    public UserController(UserModel model, UserView view) {
        this.model = model;
        this.view = view;
    }

    public static void initializeUsers() {
        loadPasswordsAndRoles();
        loadNames();
    }

    private static void loadPasswordsAndRoles() {
        loadPasswordFile(DOCTOR_PASSWORDS_FILE, UserModel.userPasswordStaffMap, UserModel.userRoleStaffMap, "Doctor");
        loadPasswordFile(PATIENT_PASSWORDS_FILE, UserModel.userPasswordPatientMap, UserModel.userRolePatientMap, "Patient");
        loadPasswordFile(STAFF_PASSWORDS_FILE, UserModel.userPasswordStaffMap, UserModel.userRoleStaffMap, "Staff");
    }

    private static void loadPasswordFile(String filePath, Map<String, String> passwordMap,
            Map<String, String> roleMap, String defaultRole) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length >= 3) {
                    String id = details[0].trim();
                    String password = details[1].trim();
                    String role = details[2].trim().isEmpty() ? defaultRole : details[2].trim();

                    passwordMap.put(id, password);
                    roleMap.put(id, role);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading password data from " + filePath + ": " + e.getMessage());
        }
    }

    private static void loadNames() {
        loadNameFile(DOCTOR_LIST_FILE, UserModel.userNameMapStaff);
        loadNameFile(PATIENT_LIST_FILE, UserModel.userNameMapPatient);
        loadNameFile(STAFF_LIST_FILE, UserModel.userNameMapStaff);
    }

    private static void loadNameFile(String filePath, Map<String, String> nameMap) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length >= 2) {
                    String id = details[0].trim();
                    String name = details[1].trim();
                    nameMap.put(id, name);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading name data from " + filePath + ": " + e.getMessage());
        }
    }

    public static void registerUser(String userId, String name, String dob, String gender, String bloodType, 
                                    String email, String phoneNumber, String role, String hashedPassword) throws IOException {
        String filePath;
        Map<String, String> passwordMap;
        Map<String, String> roleMap;
        Map<String, String> nameMap;

        // Determine the file and map to update based on the role
        switch (role) {
            case "Patient":
                filePath = "Patient_List.csv";
                passwordMap = UserModel.userPasswordPatientMap;
                roleMap = UserModel.userRolePatientMap;
                nameMap = UserModel.userNameMapPatient;
                break;
            case "Doctor":
                filePath = "Doctor_List.csv"; // Separate file for doctors
                passwordMap = UserModel.userPasswordStaffMap;
                roleMap = UserModel.userRoleStaffMap;
                nameMap = UserModel.userNameMapStaff;
                break;
            case "Pharmacist":
            case "Administrator":
                filePath = "Staff_List.csv";
                passwordMap = UserModel.userPasswordStaffMap;
                roleMap = UserModel.userRoleStaffMap;
                nameMap = UserModel.userNameMapStaff;
                break;
            default:
                throw new IllegalArgumentException("Invalid role provided");
        }

        // Add user details to maps
        passwordMap.put(userId, hashedPassword);
        roleMap.put(userId, role);
        nameMap.put(userId, name);

        // Write user data to the appropriate file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(userId + "," + name + "," + dob + "," + gender + "," + bloodType + "," + email + "," + phoneNumber);
            writer.newLine();
        }

        // Write password data to the respective password file
        String passwordFilePath = role.equals("Patient") ? "Patient_Passwords.csv" : 
                                  (role.equals("Doctor") ? "Doctor_Passwords.csv" : "Staff_Passwords.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(passwordFilePath, true))) {
            writer.write(userId + "," + hashedPassword + "," + role);
            writer.newLine();
        }
    }

    public static void changeUserPassword(String hospitalId, String newHashedPassword) throws IOException {
        String role = UserModel.userRoleStaffMap.containsKey(hospitalId) ? 
                    UserModel.userRoleStaffMap.get(hospitalId) : 
                    UserModel.userRolePatientMap.get(hospitalId);
        String filePath;

        switch (role) {
            case "Doctor":
                filePath = "Doctor_Passwords.csv";
                UserModel.userPasswordStaffMap.put(hospitalId, newHashedPassword);
                break;
            case "Pharmacist":
            case "Administrator":
                filePath = "Staff_Passwords.csv";
                UserModel.userPasswordStaffMap.put(hospitalId, newHashedPassword);
                break;
            case "Patient":
                filePath = "Patient_Passwords.csv";
                UserModel.userPasswordPatientMap.put(hospitalId, newHashedPassword);
                break;
            default:
                System.out.println("Role not recognized. Unable to change password.");
                return;
        }

    // Update the password in the file
        updatePasswordInFile(filePath, hospitalId, newHashedPassword);
    }

// Helper method to update password in the file
    private static void updatePasswordInFile(String filePath, String hospitalId, String newHashedPassword) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            String[] details = line.split(",");
            if (details[0].equals(hospitalId)) {
                details[1] = newHashedPassword; // Update the password field
                line = String.join(",", details);
            }
            updatedLines.add(line);
        }

        Files.write(Paths.get(filePath), updatedLines);
    }
}
