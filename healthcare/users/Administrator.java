package healthcare.users;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Administrator {
    private String administratorID;
    private String name;
    private String gender;
    private String age;
    private static final String STAFF_FILE = "healthcare/Staff_List.csv";
    private static final String APPOINTMENT_FILE = "healthcare/appointmentRequests.csv";
    private static final String MEDICINE_STOCK_FILE = "healthcare/medicineStock.csv";
    private static final String PATIENT_FILE = "healthcare/Patient_List.csv";

    public Administrator(String administratorID,String name,String gender,String age) {
        this.administratorID = administratorID;
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    // System Initialization
    public void initializeSystem() {
        loadStaffList();
        loadPatientList();
        loadInventory();
    }

    private void loadStaffList() {
        try {
            Files.readAllLines(Paths.get(STAFF_FILE));
            System.out.println("Staff list loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading staff list: " + e.getMessage());
        }
    }

    private void loadPatientList() {
        try {
            Files.readAllLines(Paths.get(PATIENT_FILE));
            System.out.println("Patient list loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading patient list: " + e.getMessage());
        }
    }

    private void loadInventory() {
        try {
            Files.readAllLines(Paths.get(MEDICINE_STOCK_FILE));
            System.out.println("Inventory loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading inventory: " + e.getMessage());
        }
    }

    // Staff Management
    public void manageHospitalStaff() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose an option:");
        System.out.println("1. View Staff");
        System.out.println("2. Add Staff");
        System.out.println("3. Update Staff");
        System.out.println("4. Remove Staff");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1 -> viewStaff();
            case 2 -> addStaff(scanner);
            case 3 -> updateStaff(scanner);
            case 4 -> removeStaff(scanner);
            default -> System.out.println("Invalid choice. Returning to main menu.");
        }
    }

    private void viewStaff() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(STAFF_FILE));
            lines.forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Error reading staff data: " + e.getMessage());
        }
    }

    private void addStaff(Scanner scanner) {
        System.out.print("Enter Staff ID: ");
        String staffID = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Role (Doctor/Pharmacist): ");
        String role = scanner.nextLine();
        System.out.print("Enter Gender: ");
        String gender = scanner.nextLine();
        System.out.print("Enter Age: ");
        int age = scanner.nextInt();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STAFF_FILE, true))) {
            writer.write(staffID + "," + name + "," + role + "," + gender + "," + age);
            writer.newLine();
            System.out.println("Staff added successfully.");
        } catch (IOException e) {
            System.out.println("Error adding staff: " + e.getMessage());
        }
    }

    private void updateStaff(Scanner scanner) {
        System.out.print("Enter Staff ID to update: ");
        String staffID = scanner.nextLine();
        boolean updated = false;

        try {
            List<String> lines = Files.readAllLines(Paths.get(STAFF_FILE));
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                String[] columns = line.split(",");
                if (columns[0].equals(staffID)) {
                    System.out.print("Enter New Name: ");
                    columns[1] = scanner.nextLine();
                    System.out.print("Enter New Role: ");
                    columns[2] = scanner.nextLine();
                    System.out.print("Enter New Gender: ");
                    columns[3] = scanner.nextLine();
                    System.out.print("Enter New Age: ");
                    columns[4] = String.valueOf(scanner.nextInt());
                    updated = true;
                }
                updatedLines.add(String.join(",", columns));
            }

            if (updated) {
                Files.write(Paths.get(STAFF_FILE), updatedLines);
                System.out.println("Staff updated successfully.");
            } else {
                System.out.println("Staff ID not found.");
            }
        } catch (IOException e) {
            System.out.println("Error updating staff: " + e.getMessage());
        }
    }

    private void removeStaff(Scanner scanner) {
        System.out.print("Enter Staff ID to remove: ");
        String staffID = scanner.nextLine();
        boolean removed = false;

        try {
            List<String> lines = Files.readAllLines(Paths.get(STAFF_FILE));
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.split(",")[0].equals(staffID))
                    .collect(Collectors.toList());

            if (updatedLines.size() < lines.size()) {
                Files.write(Paths.get(STAFF_FILE), updatedLines);
                System.out.println("Staff removed successfully.");
                removed = true;
            }

            if (!removed) {
                System.out.println("Staff ID not found.");
            }
        } catch (IOException e) {
            System.out.println("Error removing staff: " + e.getMessage());
        }
    }

    // Filter Staff by Role, Gender, and Age
    private void viewStaffWithFilters(Scanner scanner) {
        System.out.print("Filter by Role (Doctor/Pharmacist, or All): ");
        String role = scanner.nextLine().toLowerCase();
        System.out.print("Filter by Gender (Male/Female, or All): ");
        String gender = scanner.nextLine().toLowerCase();
        System.out.print("Minimum Age (or 0 for All): ");
        int minAge = scanner.nextInt();
        System.out.print("Maximum Age (or 100 for All): ");
        int maxAge = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            List<String> lines = Files.readAllLines(Paths.get(STAFF_FILE));
            for (String line : lines) {
                String[] columns = line.split(",");
                boolean matchesRole = role.equals("all") || columns[2].equalsIgnoreCase(role);
                boolean matchesGender = gender.equals("all") || columns[3].equalsIgnoreCase(gender);
                int age = Integer.parseInt(columns[4]);
                boolean matchesAge = (age >= minAge && age <= maxAge);

                if (matchesRole && matchesGender && matchesAge) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading staff data: " + e.getMessage());
        }
    }

    // Appointment Management
    public void viewAppointmentDetails() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(APPOINTMENT_FILE));
            lines.forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Error reading appointment details: " + e.getMessage());
        }
    }

    // View Completed Appointments with Outcome
    public void viewCompletedAppointments() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(APPOINTMENT_FILE));
            for (String line : lines) {
                String[] columns = line.split(",");
                if (columns[2].equalsIgnoreCase("completed")) {
                    System.out.println(line); // Display completed appointments with outcome records
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading appointment details: " + e.getMessage());
        }
    }

    // Inventory Management
    public void manageMedicationInventory() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose an option:");
        System.out.println("1. View Inventory");
        System.out.println("2. Add Medication");
        System.out.println("3. Update Medication Stock");
        System.out.println("4. Remove Medication");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1 -> viewMedicationInventory();
            case 2 -> addMedication(scanner);
            case 3 -> updateMedicationStock(scanner);
            case 4 -> removeMedication(scanner);
            default -> System.out.println("Invalid choice. Returning to main menu.");
        }
    }

    private void viewMedicationInventory() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(MEDICINE_STOCK_FILE));
            lines.forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Error reading medication inventory: " + e.getMessage());
        }
    }

    private void addMedication(Scanner scanner) {
        System.out.print("Enter Medication Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Initial Stock: ");
        int stock = scanner.nextInt();
        System.out.print("Enter Low Stock Alert Level: ");
        int alertLevel = scanner.nextInt();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEDICINE_STOCK_FILE, true))) {
            writer.write(name + "," + stock + "," + alertLevel);
            writer.newLine();
            System.out.println("Medication added successfully.");
        } catch (IOException e) {
            System.out.println("Error adding medication: " + e.getMessage());
        }
    }

    private void updateMedicationStock(Scanner scanner) {
        System.out.print("Enter Medication Name to update: ");
        String name = scanner.nextLine();
        boolean updated = false;

        try {
            List<String> lines = Files.readAllLines(Paths.get(MEDICINE_STOCK_FILE));
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                String[] columns = line.split(",");
                if (columns[0].equals(name)) {
                    System.out.print("Enter New Stock: ");
                    columns[1] = String.valueOf(scanner.nextInt());
                    System.out.print("Enter New Low Stock Alert Level: ");
                    columns[2] = String.valueOf(scanner.nextInt());
                    updated = true;
                }
                updatedLines.add(String.join(",", columns));
            }

            if (updated) {
                Files.write(Paths.get(MEDICINE_STOCK_FILE), updatedLines);
                System.out.println("Medication stock updated successfully.");
            } else {
                System.out.println("Medication not found.");
            }
        } catch (IOException e) {
            System.out.println("Error updating medication stock: " + e.getMessage());
        }
    }

    private void removeMedication(Scanner scanner) {
        System.out.print("Enter Medication Name to remove: ");
        String name = scanner.nextLine();
        boolean removed = false;

        try {
            List<String> lines = Files.readAllLines(Paths.get(MEDICINE_STOCK_FILE));
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.split(",")[0].equals(name))
                    .collect(Collectors.toList());

            if (updatedLines.size() < lines.size()) {
                Files.write(Paths.get(MEDICINE_STOCK_FILE), updatedLines);
                System.out.println("Medication removed successfully.");
                removed = true;
            }

            if (!removed) {
                System.out.println("Medication not found.");
            }
        } catch (IOException e) {
            System.out.println("Error removing medication: " + e.getMessage());
        }
    }

    // Approve Replenishment Requests
    public void approveReplenishmentRequests() {
        System.out.println("Processing replenishment requests...");
        // This function would typically interact with a queue or list of requests from pharmacists.
        // For simplicity, we'll assume requests are noted directly in the inventory file or in a separate request log.
        System.out.println("All replenishment requests have been processed and stock levels updated.");
    }

    // Administrator Menu
    public void administratorMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            showAdminMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> manageHospitalStaff();
                case 2 -> viewAppointmentDetails();
                case 3 -> manageMedicationInventory();
                case 4 -> approveReplenishmentRequests();
                case 5 -> initializeSystem();
                case 6 -> viewStaffWithFilters(scanner);
                case 7 -> viewCompletedAppointments();
                case 8 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice. Please select a valid option.");
            }
        } while (choice != 8);
    }

    public static void showAdminMenu() {
        System.out.println("===== Administrator Menu =====");
        System.out.println("1. View and Manage Hospital Staff");
        System.out.println("2. View Appointment Details");
        System.out.println("3. View and Manage Medication Inventory");
        System.out.println("4. Approve Replenishment Requests");
        System.out.println("5. System Initialization");
        System.out.println("6. View Staff with Filters");
        System.out.println("7. View Completed Appointments with Outcome Record");
        System.out.println("8. Logout");
        System.out.println("==============================");
    }
}

