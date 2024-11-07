package healthcare.main;

import healthcare.users.User;
import healthcare.users.Patient;
import healthcare.users.Pharmacist;
import healthcare.users.Doctor;
import healthcare.users.Administrator;

import java.io.BufferedReader;
import java.io.FileReader;
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
        List<String> lines = Files.readAllLines(Paths.get("Patient_List.csv"));

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

    private static Map<String, Doctor> loadDoctorsFromCSV() {
        Map<String, Doctor> doctorMap = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("Staff_List.csv"))) {
            String line = reader.readLine(); // Skip header line if necessary
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                
                // Check if the role is "Doctor"
                if (data[2].trim().equalsIgnoreCase("Doctor")) {
                    String doctorID = data[0];
                    String name = data[1];
                    String gender = data[3];
                    String age = data[4].trim();

                    // Assuming Doctor class has a suitable constructor
                    Doctor doctor = new Doctor(doctorID, name, gender, age);
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
                
                // Check if the role is "Doctor"
                if (data[2].trim().equalsIgnoreCase("Pharmacist")) {
                    String pharmacistID = data[0];
                    String name = data[1];
                    String gender = data[3];
                    String age = data[4].trim();

                    // Assuming Doctor class has a suitable constructor
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
                
                // Check if the role is "Doctor"
                if (data[2].trim().equalsIgnoreCase("Administrator")) {
                    String administratorID = data[0];
                    String name = data[1];
                    String gender = data[3];
                    String age = data[4].trim();

                    // Assuming Doctor class has a suitable constructor
                    Administrator administrator = new Administrator(administratorID, name, gender, age);
                    administratorMap.put(administratorID, administrator);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading administrator data: " + e.getMessage());
        }

        return administratorMap;
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

        sc.close();
    }

}
