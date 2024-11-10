package healthcare.users;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class User {
    public static Map<String, String> userPasswordStaffMap = new HashMap<>();
    public static Map<String, String> userPasswordPatientMap = new HashMap<>();
    public static Map<String, String> userRoleStaffMap = new HashMap<>();
    public static Map<String, String> userRolePatientMap = new HashMap<>();
    public static Map<String, String> userNameMapStaff = new HashMap<>();
    public static Map<String, String> userNameMapPatient = new HashMap<>();
    
    private static final String doctorPasswordsFile = "Doctor_Passwords.csv";
    private static final String patientPasswordsFile = "Patient_Passwords.csv";
    private static final String staffPasswordsFile = "Staff_Passwords.csv";
    private static final String doctorListFile = "Doctor_List.csv";
    private static final String patientListFile = "Patient_List.csv";
    private static final String staffListFile = "Staff_List.csv";

    public static void initializeUsers() {
        loadPasswordsAndRoles();
        loadNames();
    }

    private static void loadPasswordsAndRoles() {
        // Load doctors' passwords and roles
        loadPasswordFile(doctorPasswordsFile, userPasswordStaffMap, userRoleStaffMap, "Doctor");

        // Load patients' passwords and roles
        loadPasswordFile(patientPasswordsFile, userPasswordPatientMap, userRolePatientMap, "Patient");

        // Load staff passwords and roles (for Pharmacists, Administrators, etc.)
        loadPasswordFile(staffPasswordsFile, userPasswordStaffMap, userRoleStaffMap, "Staff");
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
        loadNameFile(doctorListFile, userNameMapStaff);
        loadNameFile(patientListFile, userNameMapPatient);
        loadNameFile(staffListFile, userNameMapStaff);
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

    // Method to change the user's password (for both staff and patients)
    public static void changeUserPassword(String hospitalId, String newPassword) throws IOException {
        String role = userRoleStaffMap.containsKey(hospitalId) ? userRoleStaffMap.get(hospitalId)
                : userRolePatientMap.get(hospitalId);

        switch (role) {
            case "Doctor":
                updatePasswordInFile(doctorPasswordsFile, hospitalId, newPassword);
                userPasswordStaffMap.put(hospitalId, newPassword);
                break;
            case "Pharmacist":
            case "Administrator":
                updatePasswordInFile(staffPasswordsFile, hospitalId, newPassword);
                userPasswordStaffMap.put(hospitalId, newPassword);
                break;
            case "Patient":
                updatePasswordInFile(patientPasswordsFile, hospitalId, newPassword);
                userPasswordPatientMap.put(hospitalId, newPassword);
                break;
            default:
                System.out.println("Role not recognized. Unable to change password.");
                break;
        }
    }

    private static void updatePasswordInFile(String filePath, String hospitalId, String newPassword) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            String[] details = line.split(",");
            if (details[0].equals(hospitalId)) {
                details[1] = newPassword; // Update the password field
                line = String.join(",", details);
            }
            updatedLines.add(line);
        }

        Files.write(Paths.get(filePath), updatedLines);
        System.out.println("Password updated successfully.");
    }
}