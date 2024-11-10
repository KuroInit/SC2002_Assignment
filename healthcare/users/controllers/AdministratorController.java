package healthcare.users.controllers;

import healthcare.users.models.*;
import healthcare.users.view.*;

import java.util.Scanner;
import java.util.List;

public class AdministratorController {
    private final AdministratorModel model;
    private final AdministratorView view;


    public AdministratorController(AdministratorModel model, AdministratorView view) {
        this.model = model;
        this.view = view;
    }

    public void manageStaff() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            view.displayStaffManagementMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewStaff();
                case 2 -> addStaff();
                case 3 -> updateStaff();
                case 4 -> removeStaff();
                case 5 -> {
                    view.displayExitMessage("staff management");
                    return;
                }
                default -> view.displayInvalidOption();
            }
        }
    }

    public void viewStaff() {
        String doctor = "doctor";
        String staff = "staff";
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

        List<String> doctorData = model.readDataFromFile(model.getFilePathForStaffType(doctor));
        List<String> staffData = model.readDataFromFile(model.getFilePathForStaffType(staff));

        view.displayFilteredDoctors(doctorData, filterField, filterValue);
        view.displayFilteredStaff(staffData, filterField, filterValue);
    }

    public void addStaff() {
        Scanner scanner = new Scanner(System.in);
        String filepath = model.getDoctorListPath();
        view.displayAddStaffMenu();
        int staffType = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String newEntry = view.collectStaffDetails(staffType);
        switch(staffType) {
            case 1 -> filepath = model.getDoctorListPath();
            case 2 -> filepath = model.getStaffListPath();
        }
        if (model.appendDataToFile(filepath, newEntry)) {
            view.displaySuccessMessage("Staff added successfully.");
        } else {
            view.displayErrorMessage("Error adding staff.");
        }
    }

    public void updateStaff() {
        Scanner scanner = new Scanner(System.in);
        String filepath = model.getDoctorListPath();
        view.displayUpdateStaffMenu();
        int staffType = scanner.nextInt();
        scanner.nextLine();
        switch(staffType) {
            case 1 -> filepath = model.getDoctorListPath();
            case 2 -> filepath = model.getStaffListPath();
        }

        if (model.updateEntry(filepath, view.promptIDInput(), 0, view.collectUpdatedStaffDetails(scanner, staffType))) {
            view.displaySuccessMessage("Staff updated successfully.");
        } else {
            view.displayErrorMessage("Staff ID not found or update failed.");
        }
    }

    public void removeStaff() {
        Scanner scanner = new Scanner(System.in);
        String filepath = model.getDoctorListPath();
        view.promptIsDoctor();
        Integer isDoctor = scanner.nextInt();
        switch(isDoctor) {
            case 1 -> filepath = model.getDoctorListPath();
            case 2 -> filepath = model.getStaffListPath();
        }
        boolean isRemoved = model.removeEntry(filepath, view.promptIDInput());

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
            scanner.nextLine(); // Consume newline
            statusFilter = view.getStatusFromChoice(statusChoice);
        }

        List<String> appointments = model.readDataFromFile(model.getAppointmentRequestsPath());
        view.displayAppointments(appointments, filterField, filterValue, statusFilter);
    }

    public void manageInventory() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
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
        if (model.appendDataToFile(model.getMedicineListPath(),newMedicineEntry)) {
            view.displaySuccessMessage("Medicine added successfully.");
        } else {
            view.displayErrorMessage("Error adding medicine.");
        }
    }

    private void removeMedicine() {
        Scanner scanner = new Scanner(System.in);
        view.promptMedicineName();
        String medicineName = scanner.nextLine();
        if (model.removeEntry(model.getMedicineListPath(),medicineName)) {
            view.displaySuccessMessage("Medicine removed successfully.");
        } else {
            view.displayErrorMessage("Medicine not found or removal failed.");
        }
    }

    private void updateStock() {
        Scanner scanner = new Scanner(System.in);
        view.promptMedicineName();
        String medicineName = scanner.nextLine();
        view.promptStockQuantity();
        String newQuantity = scanner.nextLine();
        scanner.nextLine(); // Consume newline
        if (model.updateEntry(model.getMedicineListPath(), medicineName, 0, newQuantity)) {
            view.displaySuccessMessage("Stock updated successfully.");
        } else {
            view.displayErrorMessage("Medicine not found or update failed.");
        }
    }

    private void updateLowStockIndicator() {
        Scanner scanner = new Scanner(System.in);
        view.promptMedicineName();
        String medicineName = scanner.nextLine();
        view.promptLowStockIndicator();
        String newIndicator = scanner.nextLine();
        scanner.nextLine(); // Consume newline
        if (model.updateEntry(model.getMedicineListPath(), medicineName, 0, newIndicator)) {
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
                case 1 -> view.displayReplenishmentRequests(model.readDataFromFile(model.getReplenishmentRequestsPath()));
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
        view.promptIncomingStock();
        String incomingStock = scanner.nextLine();
        scanner.nextLine(); // Consume newline

        if (model.updateEntry(model.getReplenishmentRequestsPath(), medicineName, 0, incomingStock)) {
            view.displaySuccessMessage("Medicine restocked successfully.");
            model.updateEntry(model.getReplenishmentRequestsPath(), medicineName, 0, "APPROVED");
        } else {
            view.displayErrorMessage("Medicine not found or restock failed.");
        }
    }
}
