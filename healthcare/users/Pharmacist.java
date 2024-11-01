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
    private static final String APPOINTMENT_FILE = "healthcare/database/doctorAppointment.csv";
    private static final String MEDICINE_STOCK_FILE = "healthcare/database/medicineStock.csv";

    public Pharmacist(String pharmacistID) {
        this.pharmacistID = pharmacistID;
    }

    // View Appointment Outcome Record
    public void viewAppointmentOutcomeRecord() {
        System.out.println("Appointment Outcome Record:");
        try {
            List<String> lines = Files.readAllLines(Paths.get(APPOINTMENT_FILE));
            lines.forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Error reading appointment records: " + e.getMessage());
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

    private void showPharmacistMenu() {
        System.out.println("===== Pharmacist Menu =====");
        System.out.println("1. View Appointment Outcome Record");
        System.out.println("2. Update Prescription Status");
        System.out.println("3. View Medication Inventory");
        System.out.println("4. Submit Replenishment Request");
        System.out.println("5. Logout");
        System.out.println("===========================");
    }
}
