package healthcare.users.controllers;

import healthcare.main.Main;
import healthcare.users.models.*;
import healthcare.users.view.*;
import java.util.List;
import java.util.Scanner;

public class AdministratorController {
    private final AdministratorModel model;
    private final AdministratorView view;

    public AdministratorController(AdministratorModel model, AdministratorView view) {
        this.model = model;
        this.view = view;
    }

    public void administratorMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            view.showAdminMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> manageStaff();
                case 2 -> viewAppointments();
                case 3 -> manageInventory();
                case 4 -> manageReplenishmentRequests();
                case 5 -> viewFeedback();
                case 6 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice. Please select a valid option.");
            }
        } while (choice != 5);
    }

    public void manageStaff() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            view.displayStaffManagementMenu();

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> viewStaff();
                    case 2 -> addStaff();
                    case 3 -> updateStaff();
                    case 4 -> removeStaff();
                    case 5 -> {
                        System.out.println("Exiting staff management...");
                        return; // Exit the method
                    }
                    default -> System.out.println("Invalid choice. Please select a valid option.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    public void viewStaff() {
        String doctorRole = "Doctor";
        String staffRole = "Staff";
        Scanner scanner = new Scanner(System.in);
        view.displayViewStaffOptions();
        int filterChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String filterField = "";
        String filterValue = "";

        if (filterChoice == 2) {
            view.displayFilterOptions();
            int filterOption = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (filterOption) {
                case 1 -> {
                    view.displayRoleSelectionMenu();
                    int roleChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    filterField = "Role";
                    filterValue = view.getRoleFromChoice(roleChoice);
                }
                case 2 -> {
                    filterField = "Gender";
                    view.displayGenderSelectionMenu();
                    int genderChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    filterValue = view.getGenderFromChoice(genderChoice);
                }
                case 3 -> {
                    filterField = "Age";
                    view.promptAgeInput();
                    filterValue = scanner.nextLine();
                }
                default -> view.displayInvalidOption();
            }
        }

        List<String> doctorData = model.readDataFromFile(model.getFilePathForStaffType(doctorRole));
        List<String> staffData = model.readDataFromFile(model.getFilePathForStaffType(staffRole));

        // Display the relevant list based on the filter field and value
        if (filterField.equals("Role")) {
            // Show either doctors or staff based on the selected role
            if (filterValue.equalsIgnoreCase(doctorRole)) {
                view.displayFilteredDoctors(doctorData, filterField, filterValue);
            } else {
                view.displayFilteredStaff(staffData, filterField, filterValue);
            }
        } else {
            // Apply other filters (Gender, Age) to both lists
            view.displayFilteredDoctors(doctorData, filterField, filterValue);
            view.displayFilteredStaff(staffData, filterField, filterValue);
        }
    }

    public void addStaff() {
        Scanner scanner = new Scanner(System.in);
        String filepath;
        String passwordFilePath;
        String role;

        view.displayAddStaffMenu();
        int staffType = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Collect staff details from the view
        String newEntry = view.collectStaffDetails(staffType);

        // Determine the file paths and role based on the staff type
        switch (staffType) {
            case 1 -> {
                filepath = model.getDoctorListPath();
                passwordFilePath = model.getDoctorPasswordsPath();
                role = "Doctor";
            }
            case 2 -> {
                filepath = model.getStaffListPath();
                passwordFilePath = model.getStaffPasswordsPath();
                role = "Staff";
            }
            default -> {
                view.displayErrorMessage("Invalid staff type.");
                return;
            }
        }

        // Append new staff data to the relevant file (Doctor_List.csv or
        // Staff_List.csv)
        if (model.appendDataToFile(filepath, newEntry)) {
            // Extract staff ID and prepare password entry
            String[] staffDetails = newEntry.split(",");
            String staffID = staffDetails[0];
            String defaultPassword = "password";
            String hashedPassword = Main.hashPassword(defaultPassword); // Use Main's hashPassword method

            // Construct password entry as ID, hashed password, and role
            String passwordEntry = staffID + "," + hashedPassword + "," + role;

            // Append to the appropriate password file (Doctor or Staff)
            if (model.appendDataToFile(passwordFilePath, passwordEntry)) {
                view.displaySuccessMessage("Staff added successfully and password set.");
            } else {
                view.displayErrorMessage("Error updating password file.");
            }
        } else {
            view.displayErrorMessage("Error adding staff.");
        }
    }

    public void updateStaff() {
        Scanner scanner = new Scanner(System.in);
        view.displayUpdateStaffMenu();
        int staffType = scanner.nextInt();
        scanner.nextLine();

        // Determine file path and header based on staff type
        String filepath;
        String header;
        switch (staffType) {
            case 1 -> {
                filepath = model.getDoctorListPath();
                header = model.getDoctorHeader();
            }
            case 2 -> {
                filepath = model.getStaffListPath();
                header = model.getStaffHeader();
            }
            default -> {
                System.out.println("Invalid staff type selected. Defaulting to Doctor.");
                filepath = model.getDoctorListPath();
                header = model.getDoctorHeader();
            }
        }

        // Prompt for ID and retrieve existing entry details
        String id = view.promptIDInput();
        String[] existingFields = model.getEntryById(filepath, id); // Retrieve existing fields

        if (existingFields == null) {
            view.displayErrorMessage("Staff ID not found.");
            return;
        }

        // Collect only the updated field(s) for the specific staff
        String updatedEntry = view.collectUpdatedStaffDetails(existingFields, staffType);
        if (model.updateEntry(filepath, id, 0, updatedEntry, header)) {
            view.displaySuccessMessage("Staff updated successfully.");
        } else {
            view.displayErrorMessage("Update failed.");
        }
    }

    public void removeStaff() {
        Scanner scanner = new Scanner(System.in);
        String filepath;
        String header;
        view.promptIsDoctor();
        Integer isDoctor = scanner.nextInt();
        switch (isDoctor) {
            case 1 -> {
                filepath = model.getDoctorListPath();
                header = model.getDoctorHeader();
            }
            case 2 -> {
                filepath = model.getStaffListPath();
                header = model.getStaffHeader();
            }
            default -> {
                System.out.println("Invalid staff type selected. Defaulting to Doctor.");
                filepath = model.getDoctorListPath();
                header = model.getDoctorHeader();
            }
        }
        boolean isRemoved = model.removeEntry(filepath, view.promptIDInput(), header);

        if (isRemoved) {
            view.displaySuccessMessage("Staff removed successfully.");
        } else {
            view.displayErrorMessage("Staff not found or removal failed.");
        }
    }

    public void viewAppointments() {
        Scanner scanner = new Scanner(System.in);
        view.displayAppointmentOptions();
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String filterField = "";
        String filterValue = "";
        String statusFilter = "";

        if (choice==1) {
            List<String> appointments = model.readDataFromFile(model.getAppointmentRequestsPath());
            view.displayAppointments(appointments, null, null, null);
        }
        
        if (choice == 2) {
            view.displayAppointmentFilterOptions();
            int filterOption = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (filterOption) {
                case 1 -> {
                    filterField = "PatientID";
                    view.promptPatientIDInput();
                    filterValue = scanner.nextLine();
                }
                case 2 -> {
                    filterField = "DoctorID";
                    view.promptDoctorIDInput();
                    filterValue = scanner.nextLine();
                }
                case 3 -> {
                    filterField = "Date";
                    view.promptDateInput();
                    filterValue = scanner.nextLine();
                }
                default -> view.displayInvalidOption();
            }

            view.displayStatusFilterOptions();
            int statusChoice = scanner.nextInt();
            scanner.nextLine();
            switch (statusChoice) {
                case 1 -> statusFilter = "";
                case 2 -> {
                    view.displayStatusTypeOptions();
                    int statusType = scanner.nextInt();
                    scanner.nextLine();
                    statusFilter = view.getStatusFromChoice(statusType);
                }
            }

            List<String> appointments = model.readDataFromFile(model.getAppointmentRequestsPath());
            view.displayAppointments(appointments, filterField, filterValue, statusFilter);
        }
    }

    public void manageInventory() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            view.inventoryMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> view.displayInventory(model.readDataFromFile(model.getMedicineListPath()));
                case 2 -> addMedicine();
                case 3 -> removeMedicine();
                case 4 -> updateStock();
                case 5 -> updateLowStockIndicator();
                case 6 -> {
                    view.displayExitMessage("inventory management");
                    return;
                }
                default -> view.displayInvalidOption();
            }
        }
    }

    private void addMedicine() {
        Scanner scanner = new Scanner(System.in);
        String newMedicineEntry = view.collectMedicineDetails(scanner);
        if (model.appendDataToFile(model.getMedicineListPath(), newMedicineEntry)) {
            view.displaySuccessMessage("Medicine added successfully.");
        } else {
            view.displayErrorMessage("Error adding medicine.");
        }
    }

    private void removeMedicine() {
        Scanner scanner = new Scanner(System.in);
        view.promptMedicineName();
        String header = model.getMedicineHeader();
        String medicineName = scanner.nextLine();
        if (model.removeEntry(model.getMedicineListPath(), medicineName, header)) {
            view.displaySuccessMessage("Medicine removed successfully.");
        } else {
            view.displayErrorMessage("Medicine not found or removal failed.");
        }
    }

    private void updateStock() {
        Scanner scanner = new Scanner(System.in);
        view.promptMedicineName();
        String medicineName = scanner.nextLine();

        // Retrieve the header and existing data for the medicine
        String header = model.getMedicineHeader();
        List<String> data = model.readDataFromFile(model.getMedicineListPath());

        String[] existingEntry = null;
        for (String line : data) {
            String[] fields = line.split(",");
            if (fields[0].equals(medicineName)) { // Assuming the medicine name is in the first column
                existingEntry = fields;
                break;
            }
        }

        // Check if the medicine was found
        if (existingEntry == null) {
            view.displayErrorMessage("Medicine not found.");
            return;
        }

        // Prompt for new stock quantity and update only the quantity field (index 1)
        view.promptStockQuantity();
        String newQuantity = scanner.nextLine();
        existingEntry[1] = newQuantity;
        if (Integer.parseInt(existingEntry[1]) < Integer.parseInt(existingEntry[2])) {
            existingEntry[3] = "Low Stock";
        } else {
            existingEntry[3] = "In Stock";
        }

        // Reconstruct the updated entry as a comma-separated string
        String updatedEntry = String.join(",", existingEntry);

        // Call updateEntry to replace the line in the file
        if (model.updateEntry(model.getMedicineListPath(), medicineName, 0, updatedEntry, header)) {
            view.displaySuccessMessage("Stock updated successfully.");
        } else {
            view.displayErrorMessage("Update failed.");
        }
    }

    private void updateLowStockIndicator() {
        Scanner scanner = new Scanner(System.in);
        view.promptMedicineName();
        String medicineName = scanner.nextLine();

        // Retrieve the header and existing data for the medicine
        String header = model.getMedicineHeader();
        List<String> data = model.readDataFromFile(model.getMedicineListPath());

        String[] existingEntry = null;
        for (String line : data) {
            String[] fields = line.split(",");
            if (fields[0].equals(medicineName)) { // Assuming the medicine name is in the first column
                existingEntry = fields;
                break;
            }
        }

        // Check if the medicine was found
        if (existingEntry == null) {
            view.displayErrorMessage("Medicine not found.");
            return;
        }

        // Prompt for new stock quantity and update only the quantity field (index 1)
        view.promptLowStockIndicator();
        String newIndicator = scanner.nextLine();
        existingEntry[2] = newIndicator;
        if (Integer.parseInt(existingEntry[1]) < Integer.parseInt(existingEntry[2])) {
            existingEntry[3] = "Low Stock";
        } else {
            existingEntry[3] = "In Stock";
        }

        // Reconstruct the updated entry as a comma-separated string
        String updatedEntry = String.join(",", existingEntry);

        // Call updateEntry to replace the line in the file
        if (model.updateEntry(model.getMedicineListPath(), medicineName, 0, updatedEntry, header)) {
            view.displaySuccessMessage("Low stock indicator updated successfully.");
        } else {
            view.displayErrorMessage("Medicine not found or update failed.");
        }
    }

    public void manageReplenishmentRequests() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            view.displayReplenishmentMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 ->
                    view.displayReplenishmentRequests(model.readDataFromFile(model.getReplenishmentRequestsPath()));
                case 2 -> restockMedicine();
                case 3 -> {
                    view.displayExitMessage("replenishment requests management");
                    return;
                }
                default -> view.displayInvalidOption();
            }
        }
    }

    private void restockMedicine() {
        Scanner scanner = new Scanner(System.in);
        view.promptMedicineName();
        String medicineName = scanner.nextLine();

        // Retrieve the header and data from the replenishment requests file
        String replenishmentHeader = model.getReplenishmentRequestsHeader();
        List<String> replenishmentData = model.readDataFromFile(model.getReplenishmentRequestsPath());

        String[] replenishmentEntry = null;
        for (String line : replenishmentData) {
            String[] fields = line.split(",");
            if (fields[0].equals(medicineName)) { // Assuming medicine name is at index 0
                replenishmentEntry = fields;
                break;
            }
        }

        // Check if the medicine was found in the replenishment requests
        if (replenishmentEntry == null) {
            view.displayErrorMessage("Medicine not found in replenishment requests.");
            return;
        }

        // Prompt for incoming stock quantity and update the stock field in
        // replenishment requests
        view.promptIncomingStock();
        String incomingStock = scanner.nextLine();

        int newTotalStock;
        try {
            int currentStock = Integer.parseInt(replenishmentEntry[1]); // Assuming stock is at index 1
            int additionalStock = Integer.parseInt(incomingStock);
            newTotalStock = currentStock + additionalStock;
            replenishmentEntry[1] = String.valueOf(newTotalStock); // Update the stock in replenishment requests
        } catch (NumberFormatException e) {
            view.displayErrorMessage("Invalid stock quantity.");
            return;
        }

        // Update the replenishment entry with the new stock and status "APPROVED"
        replenishmentEntry[3] = "APPROVED"; // Assuming status is at index 3
        String updatedReplenishmentEntry = String.join(",", replenishmentEntry);

        // Update the replenishment requests file
        if (model.updateEntry(model.getReplenishmentRequestsPath(), medicineName, 0, updatedReplenishmentEntry,
                replenishmentHeader)) {
            view.displaySuccessMessage("Replenishment request approved and stock updated.");

            // Update the main medicine list file with the new stock quantity
            String medicineHeader = model.getMedicineHeader();
            List<String> medicineData = model.readDataFromFile(model.getMedicineListPath());

            String[] medicineEntry = null;
            for (String line : medicineData) {
                String[] fields = line.split(",");
                if (fields[0].equals(medicineName)) { // Assuming medicine name is at index 0
                    medicineEntry = fields;
                    break;
                }
            }

            // Check if the medicine was found in the main medicine list
            if (medicineEntry == null) {
                view.displayErrorMessage("Medicine not found in the main medicine list.");
                return;
            }

            // Update the stock field in the main medicine list
            medicineEntry[1] = String.valueOf(newTotalStock); // Assuming stock is at index 1
            if (Integer.parseInt(medicineEntry[1]) < Integer.parseInt(medicineEntry[2])) {
                medicineEntry[3] = "Low Stock";
            } else {
                medicineEntry[3] = "In Stock";
            }
            String updatedMedicineEntry = String.join(",", medicineEntry);

            // Update the medicine list file
            model.updateEntry(model.getMedicineListPath(), medicineName, 0, updatedMedicineEntry, medicineHeader);
        } else {
            view.displayErrorMessage("Restock failed in the replenishment requests.");
        }
    }


    public void viewFeedback() {
        Screen.clearConsole();
        List<String[]> feedbackList = model.getFeedback();
    
        if (!feedbackList.isEmpty()) {
            System.out.println("===========================================");
            System.out.println("            Patient Feedback               ");
            System.out.println("===========================================");
            for (String[] feedback : feedbackList) {
                System.out.printf("Patient ID: %s%n", feedback[0]);
                System.out.printf("Feedback: %s%n", feedback[1]);
                System.out.printf("Rating: %s%n", feedback[2]);
                System.out.println("-------------------------------------------");
            }
        } else {
            System.out.println("No feedback found.");
        }
        System.out.println("\nPress Enter to continue...");
        new Scanner(System.in).nextLine();
        administratorMenu();

    }
}