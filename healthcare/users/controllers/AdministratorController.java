package healthcare.users.controllers;

import healthcare.users.models.AdministratorModel;
import healthcare.users.view.AdministratorView;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class AdministratorController {
    private final AdministratorModel model;
    private final AdministratorView view;

    public AdministratorController(AdministratorModel model, AdministratorView view) {
        this.model = model;
        this.view = view;
    }

    public void runAdministratorMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            view.displayMenu();
            view.displayMessage("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> manageStaff();
                case 2 -> viewAppointments();
                case 3 -> manageInventory();
                case 4 -> manageReplenishmentRequests();
                case 5 -> view.displayMessage("Logging out...");
                default -> view.displayMessage("Invalid choice. Please select a valid option.");
            }
        } while (choice != 5);
    }

    public void manageStaff() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            view.displayMessage("\nHospital Staff Management");
            view.displayMessage("1. View Staff");
            view.displayMessage("2. Add Staff");
            view.displayMessage("3. Update Staff");
            view.displayMessage("4. Remove Staff");
            view.displayMessage("5. Exit");
            view.displayMessage("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewStaff();
                case 2 -> addStaff(scanner);
                case 3 -> updateStaff(scanner);
                case 4 -> removeStaff(scanner);
                case 5 -> {
                    view.displayMessage("Exiting staff management.");
                    return;
                }
                default -> view.displayMessage("Invalid option. Please try again.");
            }
        }
    }

    private void viewStaff() {
        try {
            List<String> staffList = model.readFile(model.getStaffListPath());
            List<String> doctorList = model.readFile(model.getDoctorListPath());

            view.displayMessage("\nStaff List:");
            for (String staff : staffList) {
                view.displayLine(staff);
            }

            view.displayMessage("\nDoctor List:");
            for (String doctor : doctorList) {
                view.displayLine(doctor);
            }

        } catch (IOException e) {
            view.displayMessage("Error reading staff list: " + e.getMessage());
        }
    }

    private void addStaff(Scanner scanner) {
        view.displayMessage("Add Staff:");
        view.displayMessage("1. Doctor");
        view.displayMessage("2. Other Staff");
        view.displayMessage("Choose the type of staff: ");
        int staffType = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String filePath;
        if (staffType == 1) {
            filePath = model.getDoctorListPath();
        } else if (staffType == 2) {
            filePath = model.getStaffListPath();
        } else {
            view.displayMessage("Invalid option.");
            return;
        }

        view.displayMessage("Enter ID: ");
        String id = scanner.nextLine();
        view.displayMessage("Enter Name: ");
        String name = scanner.nextLine();
        view.displayMessage("Enter Gender: ");
        String gender = scanner.nextLine();
        view.displayMessage("Enter Age: ");
        String age = scanner.nextLine();

        String newEntry;
        if (staffType == 1) {
            view.displayMessage("Enter Specialisation: ");
            String specialisation = scanner.nextLine();
            newEntry = id + "," + name + "," + gender + "," + age + "," + specialisation;
        } else {
            view.displayMessage("Enter Role: ");
            String role = scanner.nextLine();
            newEntry = id + "," + name + "," + role + "," + gender + "," + age;
        }

        try {
            model.appendToFile(filePath, newEntry);
            view.displayMessage("Entry added successfully.");
        } catch (IOException e) {
            view.displayMessage("Error adding entry: " + e.getMessage());
        }
    }

    private void updateStaff(Scanner scanner) {
        view.displayMessage("Update Staff:");
        view.displayMessage("1. Doctor");
        view.displayMessage("2. Other Staff");
        view.displayMessage("Choose the type of staff to update: ");
        int staffType = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String filePath;
        if (staffType == 1) {
            filePath = model.getDoctorListPath();
        } else if (staffType == 2) {
            filePath = model.getStaffListPath();
        } else {
            view.displayMessage("Invalid option.");
            return;
        }

        view.displayMessage("Enter the ID to update: ");
        String id = scanner.nextLine();

        try {
            List<String> entries = model.readFile(filePath);
            boolean entryFound = false;

            for (int i = 0; i < entries.size(); i++) {
                String[] details = entries.get(i).split(",");
                if (details[0].equals(id)) {
                    entryFound = true;
                    view.displayMessage("Select the field to update:");
                    view.displayMessage("1. Name");
                    if (staffType == 2)
                        view.displayMessage("2. Role");
                    view.displayMessage("3. Gender");
                    view.displayMessage("4. Age");
                    if (staffType == 1)
                        view.displayMessage("5. Specialisation");

                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    switch (choice) {
                        case 1:
                            view.displayMessage("Enter new Name: ");
                            details[1] = scanner.nextLine();
                            break;
                        case 2:
                            if (staffType == 2) {
                                view.displayMessage("Enter new Role: ");
                                details[2] = scanner.nextLine();
                                break;
                            }
                            view.displayMessage("Invalid choice for Doctor.");
                            return;
                        case 3:
                            view.displayMessage("Enter new Gender: ");
                            details[staffType == 1 ? 2 : 3] = scanner.nextLine();
                            break;
                        case 4:
                            view.displayMessage("Enter new Age: ");
                            details[staffType == 1 ? 3 : 4] = scanner.nextLine();
                            break;
                        case 5:
                            if (staffType == 1) {
                                view.displayMessage("Enter new Specialisation: ");
                                details[4] = scanner.nextLine();
                                break;
                            }
                            view.displayMessage("Invalid choice for Other Staff.");
                            return;
                        default:
                            view.displayMessage("Invalid choice.");
                            return;
                    }
                    entries.set(i, String.join(",", details));
                }
            }

            if (entryFound) {
                model.writeFile(filePath, entries);
                view.displayMessage("Details updated successfully.");
            } else {
                view.displayMessage("ID not found.");
            }

        } catch (IOException e) {
            view.displayMessage("Error updating staff: " + e.getMessage());
        }
    }

    private void removeStaff(Scanner scanner) {
        view.displayMessage("Remove Staff:");
        view.displayMessage("Is the staff a Doctor? (yes/no): ");
        String isDoctor = scanner.nextLine().trim().toLowerCase();
        String filePath = isDoctor.equals("yes") ? model.getDoctorListPath() : model.getStaffListPath();
        String idLabel = isDoctor.equals("yes") ? "Doctor ID" : "Staff ID";

        view.displayMessage("Enter the " + idLabel + " to remove: ");
        String id = scanner.nextLine();

        try {
            List<String> entries = model.readFile(filePath);
            boolean entryFound = false;

            for (int i = 0; i < entries.size(); i++) {
                String[] details = entries.get(i).split(",");
                if (details[0].equals(id)) {
                    entryFound = true;
                    entries.remove(i);
                    view.displayMessage(idLabel + " " + id + " removed.");
                    break;
                }
            }

            if (entryFound) {
                model.writeFile(filePath, entries);
            } else {
                view.displayMessage(idLabel + " not found.");
            }

        } catch (IOException e) {
            view.displayMessage("Error removing staff: " + e.getMessage());
        }
    }

    public void viewAppointments() {
        try {
            List<String> appointments = model.readFile(model.getAppointmentRequestsPath());
            view.displayMessage("\nAppointment List:");
            appointments.forEach(view::displayLine);
        } catch (IOException e) {
            view.displayMessage("Error reading appointments: " + e.getMessage());
        }
    }

    public void manageInventory() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            view.displayMessage("\nMedicine Inventory Management");
            view.displayMessage("1. View Inventory");
            view.displayMessage("2. Add Medicine");
            view.displayMessage("3. Remove Medicine");
            view.displayMessage("4. Update Stock");
            view.displayMessage("5. Update Low Stock Indicator");
            view.displayMessage("6. Exit");
            view.displayMessage("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewInventory();
                case 2 -> addMedicine(scanner);
                case 3 -> removeMedicine(scanner);
                case 4 -> updateStock(scanner);
                case 5 -> updateLowStockIndicator(scanner);
                case 6 -> {
                    view.displayMessage("Exiting inventory management.");
                    return;
                }
                default -> view.displayMessage("Invalid option. Please try again.");
            }
        }
    }

    private void viewInventory() {
        try {
            List<String> inventory = model.readFile(model.getMedicineListPath());
            view.displayMessage("\nMedicine Inventory:");
            inventory.forEach(view::displayLine);
        } catch (IOException e) {
            view.displayMessage("Error reading inventory: " + e.getMessage());
        }
    }

    private void addMedicine(Scanner scanner) {
        view.displayMessage("Enter Medicine Name: ");
        String name = scanner.nextLine();
        view.displayMessage("Enter Quantity: ");
        int quantity = scanner.nextInt();
        view.displayMessage("Enter Low Stock Indicator: ");
        int lowStockIndicator = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String newEntry = name + "," + quantity + "," + lowStockIndicator + ",In Stock";
        try {
            model.appendToFile(model.getMedicineListPath(), newEntry);
            view.displayMessage("Medicine added successfully.");
        } catch (IOException e) {
            view.displayMessage("Error adding medicine: " + e.getMessage());
        }
    }

    private void removeMedicine(Scanner scanner) {
        view.displayMessage("Enter Medicine Name to remove: ");
        String medicineName = scanner.nextLine();

        try {
            List<String> inventory = model.readFile(model.getMedicineListPath());
            boolean found = inventory.removeIf(line -> line.split(",")[0].equalsIgnoreCase(medicineName));

            if (found) {
                model.writeFile(model.getMedicineListPath(), inventory);
                view.displayMessage("Medicine " + medicineName + " removed.");
            } else {
                view.displayMessage("Medicine not found.");
            }
        } catch (IOException e) {
            view.displayMessage("Error reading inventory: " + e.getMessage());
        }
    }

    private void updateStock(Scanner scanner) {
        view.displayMessage("Enter Medicine Name to update stock: ");
        String medicineName = scanner.nextLine();
        view.displayMessage("Enter new stock quantity: ");
        int newQuantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            List<String> inventory = model.readFile(model.getMedicineListPath());
            boolean found = false;

            for (int i = 0; i < inventory.size(); i++) {
                String[] details = inventory.get(i).split(",");
                if (details[0].equalsIgnoreCase(medicineName)) {
                    details[1] = String.valueOf(newQuantity);
                    details[3] = newQuantity <= Integer.parseInt(details[2]) ? "Low Stock" : "In Stock";
                    inventory.set(i, String.join(",", details));
                    found = true;
                    break;
                }
            }

            if (found) {
                model.writeFile(model.getMedicineListPath(), inventory);
                view.displayMessage("Stock updated for " + medicineName + ".");
            } else {
                view.displayMessage("Medicine not found.");
            }
        } catch (IOException e) {
            view.displayMessage("Error updating stock: " + e.getMessage());
        }
    }

    private void updateLowStockIndicator(Scanner scanner) {
        view.displayMessage("Enter Medicine Name to update low stock indicator: ");
        String medicineName = scanner.nextLine();
        view.displayMessage("Enter new low stock indicator: ");
        int newLowStockIndicator = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            List<String> inventory = model.readFile(model.getMedicineListPath());
            boolean found = false;

            for (int i = 0; i < inventory.size(); i++) {
                String[] details = inventory.get(i).split(",");
                if (details[0].equalsIgnoreCase(medicineName)) {
                    details[2] = String.valueOf(newLowStockIndicator);
                    details[3] = Integer.parseInt(details[1]) <= newLowStockIndicator ? "Low Stock" : "In Stock";
                    inventory.set(i, String.join(",", details));
                    found = true;
                    break;
                }
            }

            if (found) {
                model.writeFile(model.getMedicineListPath(), inventory);
                view.displayMessage("Low stock indicator updated for " + medicineName + ".");
            } else {
                view.displayMessage("Medicine not found.");
            }
        } catch (IOException e) {
            view.displayMessage("Error updating low stock indicator: " + e.getMessage());
        }
    }

    public void manageReplenishmentRequests() {
        view.displayMessage("\nReplenishment Requests Management");
        try {
            List<String> requests = model.readFile(model.getReplenishmentRequestsPath());
            view.displayMessage("\nReplenishment Requests:");
            requests.forEach(view::displayLine);
        } catch (IOException e) {
            view.displayMessage("Error reading replenishment requests: " + e.getMessage());
        }
    }
}
