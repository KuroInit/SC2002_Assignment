package healthcare.users.controllers;

import healthcare.users.models.*;
import healthcare.users.view.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PharmacistController {
    private PharmacistModel pharmacistModel;
    private PharmacistView pharmacistView;

    public PharmacistController(PharmacistModel pharmacistModel, PharmacistView pharmacistView) {
        this.pharmacistModel = pharmacistModel;
        this.pharmacistView = pharmacistView;
    }

    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            pharmacistView.displayPharmacistMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewAppointmentOutcomeRecord();
                case 2 -> updateMedicationStatus(scanner);
                case 3 -> viewMedicationInventory();
                case 4 -> submitReplenishmentRequest(scanner);
                case 5 -> {
                    pharmacistView.displayMessage("Logging out...");
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                }
                default -> {
                    pharmacistView.displayError("Invalid choice. Please select a valid option.");
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                }
            }
        } while (choice != 5);
    }

    private void viewAppointmentOutcomeRecord() {
        String filePath = "appointmentRequests.csv";
        StringBuilder completedAppointments = new StringBuilder(
                "Completed Appointments:\n-----------------------------------------------------\n");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip header
            String line;
            boolean found = false;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if ("COMPLETED".equalsIgnoreCase(data[5])) {
                    completedAppointments.append("Appointment ID: ").append(data[0]).append("\n")
                            .append("Doctor ID: ").append(data[1]).append("\n")
                            .append("Patient ID: ").append(data[2]).append("\n")
                            .append("Date: ").append(data[3]).append("\n")
                            .append("Time: ").append(data[4]).append("\n")
                            .append("Service: ").append(data[6] != null ? data[6] : "N/A").append("\n")
                            .append("Notes: ").append(data[7] != null ? data[7] : "N/A").append("\n")
                            .append("Medications: ").append(data[8] != null ? data[8] : "N/A").append("\n")
                            .append("Quantity: ").append(data[9] != null ? data[9] : "N/A").append("\n")
                            .append("Status: ").append(data[10] != null ? data[10] : "N/A").append("\n")
                            .append("-----------------------------------------------------\n");
                    found = true;
                }
            }

            if (!found) {
                completedAppointments.append("No completed appointments found.\n");
            }
        } catch (IOException e) {
            pharmacistView.displayError("Error reading appointments file: " + e.getMessage());
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine();
            return;
        }

        pharmacistView.displayAppointmentOutcome(completedAppointments.toString());
        System.out.println("\nPress Enter to continue...");
        new Scanner(System.in).nextLine();
    }

    private void updateMedicationStatus(Scanner scanner) {
        System.out.print("Enter the Appointment ID to update medication status to 'DISPENSED': ");
        String appointmentID = scanner.nextLine();

        String appointmentRequestsPath = "appointmentRequests.csv";
        String medicineListPath = "Medicine_List.csv";
        List<String> appointments = new ArrayList<>();
        boolean appointmentFound = false;
        String prescribedMedicine = null;
        int prescribedQuantity = 0;

        // Step 1: Locate the appointment and extract the medicine and quantity
        try (BufferedReader br = new BufferedReader(new FileReader(appointmentRequestsPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(appointmentID) && "PENDING".equalsIgnoreCase(details[10])) {
                    appointmentFound = true;
                    details[10] = "DISPENSED"; // Update status
                    prescribedMedicine = details[8];
                    prescribedQuantity = Integer.parseInt(details[9]);
                    appointments.add(String.join(",", details));
                } else {
                    appointments.add(line);
                }
            }
        } catch (IOException e) {
            pharmacistView.displayError("Error reading appointment requests: " + e.getMessage());
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        if (!appointmentFound) {
            pharmacistView.displayMessage("No pending appointment found with the given ID.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Step 2: Update the appointment file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(appointmentRequestsPath))) {
            for (String updatedLine : appointments) {
                bw.write(updatedLine);
                bw.newLine();
            }
            pharmacistView.displayMessage("Appointment status updated to 'DISPENSED'.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        } catch (IOException e) {
            pharmacistView.displayError("Error saving updated appointments: " + e.getMessage());
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Step 3: Update the medicine list by subtracting the prescribed quantity
        List<String> medicineData = new ArrayList<>();
        boolean medicineFound = false;

        try (BufferedReader br = new BufferedReader(new FileReader(medicineListPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equalsIgnoreCase(prescribedMedicine)) {
                    int currentStock = Integer.parseInt(details[1]);
                    int newStock = currentStock - prescribedQuantity;
                    if (newStock < 0) {
                        pharmacistView.displayError("Insufficient stock for " + prescribedMedicine);
                        System.out.println("\nPress Enter to continue...");
                        scanner.nextLine();
                        return;
                    }
                    details[1] = String.valueOf(newStock);

                    if (newStock < Integer.parseInt(details[2])) {
                        details[3] = "Low Stock";
                    }

                    medicineData.add(String.join(",", details));
                    medicineFound = true;
                } else {
                    medicineData.add(line);
                }
            }
        } catch (IOException e) {
            pharmacistView.displayError("Error reading medicine list: " + e.getMessage());
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        if (!medicineFound) {
            pharmacistView.displayError("Medicine not found in the medicine list.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Step 4: Save the updated medicine list back to the file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(medicineListPath))) {
            for (String updatedLine : medicineData) {
                bw.write(updatedLine);
                bw.newLine();
            }
            pharmacistView.displayMessage("Medicine list updated with new stock quantity and stock level.");
        } catch (IOException e) {
            pharmacistView.displayError("Error saving updated medicine list: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewMedicationInventory() {
        StringBuilder inventoryDetails = new StringBuilder(
                "Medication Inventory:\n---------------------------------------------------------\n")
                .append(String.format("%-20s %-15s %-10s%n", "Medicine Name", "Quantity", "Stock Level"))
                .append("---------------------------------------------------------\n");

        try {
            List<String> lines = Files.readAllLines(Paths.get("Medicine_List.csv"));
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    inventoryDetails.append(
                            String.format("%-20s %-15s %-10s%n", parts[0].trim(), parts[1].trim(), parts[3].trim()));
                }
            }
        } catch (IOException e) {
            pharmacistView.displayError("Error reading medication inventory: " + e.getMessage());
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine();
            return;
        }
        inventoryDetails.append("---------------------------------------------------------\n");
        pharmacistView.displayMedicationInventory(inventoryDetails.toString());
    }

    private void submitReplenishmentRequest(Scanner scanner) {
        System.out.print("Enter the name of the medication for replenishment request: ");
        String medicationName = scanner.nextLine().trim();
        System.out.print("Enter the date of the replenishment request (YYYY-MM-DD): ");
        String requestDate = scanner.nextLine().trim();

        boolean requestSubmitted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader("Medicine_List.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equalsIgnoreCase(medicationName) && "Low Stock".equalsIgnoreCase(details[3])) {
                    try (BufferedWriter writer = new BufferedWriter(
                            new FileWriter("Replenishment_Requests.csv", true))) {
                        writer.write(String.join(",", details[0], details[1], requestDate, "REQUESTED"));
                        writer.newLine();
                        pharmacistView
                                .displayMessage("Replenishment request submitted for medication: " + medicationName);
                        requestSubmitted = true;
                        System.out.println("\nPress Enter to continue...");
                        scanner.nextLine();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            pharmacistView.displayError("Error reading medicine list: " + e.getMessage());
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        if (!requestSubmitted) {
            pharmacistView.displayMessage(
                    "No replenishment request submitted. Either the medicine was not found or it is not marked as 'Low Stock'.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
}
