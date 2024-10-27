package healthcare.main;

import healthcare.users.User;
import healthcare.users.Patient;
import healthcare.users.Pharmacist;
import healthcare.users.Doctor;

import healthcare.users.Admin;

import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        // Initialize users (IDs, roles, passwords, and names from files)
        User.initializeUsers();

        // Display the login screen
        showLoginScreen();
    }

    private static Map<String, Patient> loadPatientsFromCSV() throws IOException {
        Map<String, Patient> patientMap = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get("patients.csv"));

        for (String line : lines) {
            String[] details = line.split(",");
            String patientID = details[0].trim();
            String name = details[1].trim();
            String dob = details[2].trim();
            String gender = details[3].trim();
            String bloodType = details[4].trim();
            String contactInfo = details[5].trim();

            Patient patient = new Patient(patientID, name, dob, gender, bloodType, contactInfo);
            patientMap.put(patientID, patient);
        }
        return patientMap;
    }

    private static void showLoginScreen() throws IOException {
        Scanner sc = new Scanner(System.in); // Open the scanner only once here

        System.out.println("Welcome to the Hospital Management System (HMS)");

        boolean loginSuccessful = false;
        String hospitalId = "";
        while (!loginSuccessful) {
            System.out.print("Enter Hospital ID: ");
            hospitalId = sc.nextLine();

            if (!User.userPasswordStaffMap.containsKey(hospitalId)
                    && !User.userPasswordPatientMap.containsKey(hospitalId)) {
                System.out.println("Invalid Hospital ID. Please try again.");
                continue; // Ask for the Hospital ID again
            }

            System.out.print("Enter Password: ");
            String password = sc.nextLine();

            // Validate the password from both maps
            if ((User.userPasswordStaffMap.containsKey(hospitalId)
                    && User.userPasswordStaffMap.get(hospitalId).equals(password)) ||
                    (User.userPasswordPatientMap.containsKey(hospitalId)
                            && User.userPasswordPatientMap.get(hospitalId).equals(password))) {
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
        String changePassword = sc.nextLine();
        if (changePassword.equalsIgnoreCase("yes")) {
            User.changeUserPassword(hospitalId);
        }

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
                Doctor.showDoctorMenu();
                break;
            case "Pharmacist":
                Pharmacist.showPharmacistMenu();
                break;
            case "Administrator":
                Admin.showAdministratorMenu();
                break;
            default:
                System.out.println("Invalid role! Exiting...");
                break;
        }

    }

}
