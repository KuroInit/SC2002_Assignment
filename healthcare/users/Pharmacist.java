package healthcare.users;

import healthcare.records.Appointment;
import healthcare.records.Appointment.AppointmentStatus;
import healthcare.records.Appointment.Medication;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Pharmacist {
    private String pharmacistID;
    private String name;
    private String gender;
    private String age;
    private static final String APPOINTMENT_FILE = "appointmentRequests.csv";
    private static final String MEDICINE_STOCK_FILE = "medicineStock.csv";

    public Pharmacist(String pharmacistID,String name,String gender,String age) {
        this.pharmacistID = pharmacistID;
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    // View Appointment Outcome Record
    public void viewAppointmentOutcomeRecord() {
        String filePath = "appointmentRequests.csv";
        String line;
        boolean foundCompleted = false;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Read the header
            br.readLine();
            
            System.out.println("Completed Appointments:");
            System.out.println("-----------------------");

            // Read each line and display completed appointments
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String status = data[5].trim();  // Assuming 6th column is AppointmentStatus

                if ("COMPLETED".equalsIgnoreCase(status)) {
                    foundCompleted = true;
                    System.out.println("Appointment ID: " + data[0]);
                    System.out.println("Doctor ID: " + data[1]);
                    System.out.println("Patient ID: " + data[2]);
                    System.out.println("Appointment Date: " + data[3]);
                    System.out.println("Appointment Time: " + data[4]);
                    System.out.println("Status: " + data[5]);
                    System.out.println("Type of Service: " + (data[6] != null ? data[6] : "N/A"));
                    System.out.println("Prescribed Medications: " + (data[7] != null ? data[7] : "N/A"));
                    System.out.println("Consultation Notes: " + (data[8] != null ? data[8] : "N/A"));
                    System.out.println("---------------");
                }
            }

            if (!foundCompleted) {
                System.out.println("No completed appointments found.");
            }
        } catch (IOException e) {
            System.out.println("Error reading appointments file: " + e.getMessage());
        }
    }

    // Update Prescription Status
    public void updatePrescriptionStatus(String appointmentID, String medicationName, AppointmentStatus newStatus) {
        List<String> updatedLines = new ArrayList<>();
        boolean updated = false;

        try {
            List<String> lines = Files.readAllLines(Paths.get(APPOINTMENT_FILE));
            for (String line : lines) {
                String[] columns = line.split(",");
                if (columns[0].equals(appointmentID)) {
                    List<String> medications = Arrays.asList(columns[columns.length - 1].split(";"));
                    List<String> updatedMedications = medications.stream()
                            .map(med -> med.equals(medicationName) ? medicationName + ":" + newStatus : med)
                            .collect(Collectors.toList());
                    columns[columns.length - 1] = String.join(";", updatedMedications);
                    updated = true;
                }
                updatedLines.add(String.join(",", columns));
            }

            if (updated) {
                Files.write(Paths.get(APPOINTMENT_FILE), updatedLines);
                System.out.println("Updated prescription status for medication: " + medicationName);
            } else {
                System.out.println("Appointment or medication not found.");
            }

        } catch (IOException e) {
            System.out.println("Error updating prescription status: " + e.getMessage());
        }
    }

    // View Medication Inventory
    public void viewMedicationInventory() {
        System.out.println("Medication Inventory:");
        try {
            List<String> lines = Files.readAllLines(Paths.get(MEDICINE_STOCK_FILE));
            lines.forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Error reading medication inventory: " + e.getMessage());
        }
    }

    // Submit Replenishment Request
    public void submitReplenishmentRequest(String medicationName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEDICINE_STOCK_FILE, true))) {
            writer.write(medicationName + ",LOW_STOCK,Replenishment requested");
            writer.newLine();
            System.out.println("Replenishment request submitted for medication: " + medicationName);
        } catch (IOException e) {
            System.out.println("Error submitting replenishment request: " + e.getMessage());
        }
    }

    // Pharmacist Menu
    public void pharmacistMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            showPharmacistMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewAppointmentOutcomeRecord();
                case 2 -> {
                    System.out.print("Enter Appointment ID: ");
                    String appointmentID = scanner.nextLine();
                    System.out.print("Enter Medication Name: ");
                    String medicationName = scanner.nextLine();
                    System.out.print("Enter new status (e.g., DISPENSED): ");
                    String statusInput = scanner.nextLine();
                    AppointmentStatus status = AppointmentStatus.valueOf(statusInput.toUpperCase());

                    updatePrescriptionStatus(appointmentID, medicationName, status);
                }
                case 3 -> viewMedicationInventory();
                case 4 -> {
                    System.out.print("Enter Medication Name to request replenishment: ");
                    String medicationName = scanner.nextLine();
                    submitReplenishmentRequest(medicationName);
                }
                case 5 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice. Please select a valid option.");
            }
        } while (choice != 5);
    }

    public static void showPharmacistMenu() {
        System.out.println("===== Pharmacist Menu =====");
        System.out.println("1. View Appointment Outcome Record");
        System.out.println("2. Update Prescription Status");
        System.out.println("3. View Medication Inventory");
        System.out.println("4. Submit Replenishment Request");
        System.out.println("5. Logout");
        System.out.println("===========================");
    }
}
