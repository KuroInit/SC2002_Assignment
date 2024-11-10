package healthcare.users.controllers;

import healthcare.users.models.User;
import healthcare.users.view.UserView;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserController {
    private User model;
    private UserView view;

    private static final String DOCTOR_PASSWORDS_FILE = "Doctor_Passwords.csv";
    private static final String PATIENT_PASSWORDS_FILE = "Patient_Passwords.csv";
    private static final String STAFF_PASSWORDS_FILE = "Staff_Passwords.csv";
    private static final String DOCTOR_LIST_FILE = "Doctor_List.csv";
    private static final String PATIENT_LIST_FILE = "Patient_List.csv";
    private static final String STAFF_LIST_FILE = "Staff_List.csv";

    public UserController(User model, UserView view) {
        this.model = model;
        this.view = view;
    }

    public static void initializeUsers() {
        loadPasswordsAndRoles();
        loadNames();
    }

    private static void loadPasswordsAndRoles() {
        loadPasswordFile(DOCTOR_PASSWORDS_FILE, User.userPasswordStaffMap, User.userRoleStaffMap, "Doctor");
        loadPasswordFile(PATIENT_PASSWORDS_FILE, User.userPasswordPatientMap, User.userRolePatientMap, "Patient");
        loadPasswordFile(STAFF_PASSWORDS_FILE, User.userPasswordStaffMap, User.userRoleStaffMap, "Staff");
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
        loadNameFile(DOCTOR_LIST_FILE, User.userNameMapStaff);
        loadNameFile(PATIENT_LIST_FILE, User.userNameMapPatient);
        loadNameFile(STAFF_LIST_FILE, User.userNameMapStaff);
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

    public void changeUserPassword(String hospitalId, String newPassword) throws IOException {
        String role = User.userRoleStaffMap.containsKey(hospitalId) ? User.userRoleStaffMap.get(hospitalId)
                : User.userRolePatientMap.get(hospitalId);

        switch (role) {
            case "Doctor" -> updatePasswordInFile(DOCTOR_PASSWORDS_FILE, hospitalId, newPassword);
            case "Pharmacist", "Administrator" -> updatePasswordInFile(STAFF_PASSWORDS_FILE, hospitalId, newPassword);
            case "Patient" -> updatePasswordInFile(PATIENT_PASSWORDS_FILE, hospitalId, newPassword);
            default -> view.displayMessage("Role not recognized. Unable to change password.");
        }
    }

    private void updatePasswordInFile(String filePath, String hospitalId, String newPassword) throws IOException {
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
        view.displayMessage("Password updated successfully.");
    }
}
