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
    private static final String medicineListPath = "Medicine_List.csv";
    private static final String replenishmentRequestsPath = "Replenishment_Requests.csv";

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
            System.out.println("---------------------------------------------");

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
                    System.out.println("Type of Service: " + (data[6] != null ? data[6] : "N/A"));
                    System.out.println("Consultation Notes: " + (data[7] != null ? data[7] : "N/A"));
                    System.out.println("Prescribed Medications: " + (data[8] != null ? data[8] : "N/A"));
                    System.out.println("Quantity of Medications: " + (data[9] != null ? data[9] : "N/A"));
                    System.out.println("Medication Status: " + (data[10] != null ? data[10] : "N/A"));
                    System.out.println("---------------------------------------------");
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
    public void updateMedicationStatus() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the Appointment ID to update medication status to 'DISPENSED': ");
        String appointmentID = scanner.nextLine();
        
        // File paths
        String appointmentRequestsPath = "appointmentRequests.csv";
        String medicineListPath = "Medicine_List.csv";
        
        // Variables to hold the medication details
        String medicationName = "";
        int prescribedQuantity = 0;
        boolean appointmentFound = false;
        
        // Step 1: Locate the appointment and retrieve medication details
        List<String> appointments = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(appointmentRequestsPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(appointmentID) && details[10].equalsIgnoreCase("PENDING")) {
                    // Assuming medication name is at index 8 and quantity at index 9
                    medicationName = details[8];
                    prescribedQuantity = Integer.parseInt(details[9]);
                    appointmentFound = true;
                }
                appointments.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading appointment requests: " + e.getMessage());
            return;
        }
        
        if (!appointmentFound) {
            System.out.println("No pending appointment found with the given ID.");
            return;
        }
        
        // Step 2: Check if there is enough stock in Medicine_List.csv
        List<String> medicines = new ArrayList<>();
        boolean enoughStock = false;
    
        try (BufferedReader br = new BufferedReader(new FileReader(medicineListPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equalsIgnoreCase(medicationName)) {
                    int currentQuantity = Integer.parseInt(details[1].trim());
                    int lowStockIndicator = Integer.parseInt(details[2].trim()); // Assuming Low Stock Indicator is at index 2
                    
                    // Check if there is enough stock
                    if (currentQuantity >= prescribedQuantity) {
                        // Deduct the prescribed quantity
                        currentQuantity -= prescribedQuantity;
                        details[1] = String.valueOf(currentQuantity);
                        
                        // Check if the new quantity is equal to or below the low stock indicator
                        if (currentQuantity <= lowStockIndicator) {
                            details[3] = "Low Stock"; // Update the Stock Level to "Low Stock"
                            System.out.println("Stock level updated to 'Low Stock' for " + medicationName);
                        }
                        
                        enoughStock = true;
                        System.out.println("Updated " + medicationName + " stock. New quantity: " + currentQuantity);
                    } else {
                        System.out.println("Not enough stock for " + medicationName);
                        return;  // Exit if there is not enough stock
                    }
                }
                medicines.add(String.join(",", details));
            }
        } catch (IOException e) {
            System.out.println("Error reading medicine list: " + e.getMessage());
            return;
        }
    
        // Step 3: If enough stock, update the appointmentRequests.csv file with "DISPENSED" status
        if (enoughStock) {
            for (int i = 0; i < appointments.size(); i++) {
                String[] details = appointments.get(i).split(",");
                if (details[0].equals(appointmentID) && details[10].equalsIgnoreCase("PENDING")) {
                    details[10] = "DISPENSED";  // Update status to "DISPENSED"
                    appointments.set(i, String.join(",", details));
                    break;
                }
            }
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(appointmentRequestsPath))) {
                for (String updatedLine : appointments) {
                    bw.write(updatedLine);
                    bw.newLine();
                }
                System.out.println("Appointment status updated to 'DISPENSED'.");
            } catch (IOException e) {
                System.out.println("Error saving updated appointments: " + e.getMessage());
            }
        }
    
        // Step 4: Write the updated medicine quantities back to Medicine_List.csv
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(medicineListPath))) {
            for (String updatedLine : medicines) {
                bw.write(updatedLine);
                bw.newLine();
            }
            System.out.println("Medicine list updated successfully.");
        } catch (IOException e) {
            System.out.println("Error saving updated medicine list: " + e.getMessage());
        }
    }

    // View Medication Inventory
    public void viewMedicationInventory() {
        System.out.println("Medication Inventory:");
        System.out.println("---------------------------------------------------------");
        System.out.printf("%-20s %-15s %-10s%n", "Medicine Name", "Quantity", "Stock Level");
        System.out.println("---------------------------------------------------------");
    
        try {
            List<String> lines = Files.readAllLines(Paths.get("Medicine_List.csv"));
    
            // Skip the header line
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(",");
                if (parts.length >= 3) {  // Ensure there are at least 3 parts (Medicine Name, Quantity, Unit Price)
                    String medicineName = parts[0].trim();
                    String quantity = parts[1].trim();
                    String stockLevel = parts[3].trim();
    
                    // Print each line in a tabular format
                    System.out.printf("%-20s %-15s %-10s%n", medicineName, quantity, stockLevel);
                } else {
                    System.out.println("Invalid entry in inventory: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading medication inventory: " + e.getMessage());
        }
        System.out.println("---------------------------------------------------------");
    }

    // Submit Replenishment Request
    public void submitReplenishmentRequest() {
        Scanner scanner = new Scanner(System.in);

        // Prompt the pharmacist to enter the medication name
        System.out.print("Enter the name of the medication for replenishment request: ");
        String medicationName = scanner.nextLine().trim();

        // Prompt the pharmacist to enter the request date
        System.out.print("Enter the date of the replenishment request (YYYY-MM-DD): ");
        String requestDate = scanner.nextLine().trim();

        boolean requestSubmitted = false;

        // Step 1: Check if medication exists and needs replenishment in Medicine_List.csv
        try (BufferedReader reader = new BufferedReader(new FileReader(medicineListPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");

                // Assuming details[0] is the medication name and details[3] is the stock level
                if (details[0].equalsIgnoreCase(medicationName) && details[3].trim().equals("Low Stock")) {
                    // Prepare the request entry format for Replenishment_Requests.csv
                    String requestEntry = String.join(",", details[0], details[1], requestDate, "REQUESTED");

                    // Step 2: Append request to Replenishment_Requests.csv
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(replenishmentRequestsPath, true))) {
                        writer.write(requestEntry);
                        writer.newLine();
                        System.out.println("Replenishment request submitted for medication: " + medicationName);
                        requestSubmitted = true;
                    } catch (IOException e) {
                        System.out.println("Error writing to replenishment requests file: " + e.getMessage());
                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading medicine list: " + e.getMessage());
        }

        if (!requestSubmitted) {
            System.out.println("No replenishment request submitted. Either the medicine was not found or it is not marked as 'Low Stock'.");
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
                case 2 -> updateMedicationStatus();
                case 3 -> viewMedicationInventory();
                case 4 -> submitReplenishmentRequest();
                case 5 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice. Please select a valid option.");
            }
        } while (choice != 5);
    }

    public static void showPharmacistMenu() {
        System.out.println("\n===== Pharmacist Menu =====");
        System.out.println("1. View Appointment Outcome Record");
        System.out.println("2. Update Prescription Status");
        System.out.println("3. View Medication Inventory");
        System.out.println("4. Submit Replenishment Request");
        System.out.println("5. Logout");
        System.out.println("===========================\n");
    }
}
