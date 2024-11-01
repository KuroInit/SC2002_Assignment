package healthcare.users;

import healthcare.records.Appointment;
import healthcare.records.Appointment.Medication;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Pharmacist {
    private String pharmacistID;
    private List<Medication> inventory; // Inventory of medications

    public Pharmacist(String pharmacistID) {
        this.pharmacistID = pharmacistID;
        this.inventory = new ArrayList<>();
    }

    // View Appointment Outcome Record
    public void viewAppointmentOutcomeRecord(List<Appointment> appointments) {
        System.out.println("Appointment Outcome Record:");
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentStatus() == Appointment.AppointmentStatus.COMPLETED) {
                System.out.println(appointment.toString());
            }
        }
    }

    // Update Prescription Status
    public void updatePrescriptionStatus(Appointment appointment, String medicationName, Appointment.AppointmentStatus status) {
        for (Medication medication : appointment.getPrescribedMedications()) {
            if (medication.getMedicationName().equalsIgnoreCase(medicationName)) {
                medication.updateMedicationStatus(status);
                System.out.println("Updated status of " + medicationName + " to " + status);
                return;
            }
        }
        System.out.println("Medication not found in the appointment record.");
    }

    // View Medication Inventory
    public void viewMedicationInventory() {
        System.out.println("Medication Inventory:");
        for (Medication medication : inventory) {
            System.out.println(medication.toString());
        }
    }

    // Submit Replenishment Request
    public void submitReplenishmentRequest(String medicationName) {
        System.out.println("Replenishment request submitted for medication: " + medicationName);
        // You may add additional logic to notify administrators or update inventory here.
    }

    // Pharmacist Menu
    public void pharmacistMenu(List<Appointment> appointments) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            showPharmacistMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewAppointmentOutcomeRecord(appointments);
                case 2 -> {
                    System.out.print("Enter Appointment ID: ");
                    String appointmentID = scanner.nextLine();
                    System.out.print("Enter Medication Name: ");
                    String medicationName = scanner.nextLine();
                    System.out.print("Enter new status (e.g., DISPENSED): ");
                    String statusInput = scanner.nextLine();
                    Appointment.AppointmentStatus status = Appointment.AppointmentStatus.valueOf(statusInput.toUpperCase());

                    Appointment appointment = appointments.stream()
                            .filter(a -> a.getAppointmentID().equals(appointmentID))
                            .findFirst()
                            .orElse(null);

                    if (appointment != null) {
                        updatePrescriptionStatus(appointment, medicationName, status);
                    } else {
                        System.out.println("Appointment ID not found.");
                    }
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
