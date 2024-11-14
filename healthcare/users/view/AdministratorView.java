package healthcare.users.view;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class AdministratorView {

    public void showAdminMenu() {
        System.out.println("\n===== Administrator Menu =====");
        System.out.println("1. View and Manage Hospital Staff");
        System.out.println("2. View Appointment Details");
        System.out.println("3. View and Manage Medication Inventory");
        System.out.println("4. Approve Replenishment Requests");
        System.out.println("5. Logout");
        System.out.println("==============================\n");
    }
    
    public void displayStaffManagementMenu() {
        System.out.println("\nHospital Staff Management");
        System.out.println("1. View Staff");
        System.out.println("2. Add Staff");
        System.out.println("3. Update Staff");
        System.out.println("4. Remove Staff");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
    }

    public void displayViewStaffOptions() {
        System.out.println("\nStaff List:");
        System.out.println("1. View All");
        System.out.println("2. Filter by Role, Gender, or Age");
        System.out.print("Choose an option: ");
    }

    public void displayFilterOptions() {
        System.out.println("\nFilter Options:");
        System.out.println("1. Role (for staff only)");
        System.out.println("2. Gender");
        System.out.println("3. Age");
        System.out.print("Choose a filter option: ");
    }

    public void displayRoleSelectionMenu() {
        System.out.println("Select a Role:");
        System.out.println("1. Doctor");
        System.out.println("2. Pharmacist");
        System.out.println("3. Nurse");
        System.out.println("4. Administrator");
        System.out.print("Enter your choice: ");
    }

    public void displayGenderSelectionMenu() {
        System.out.println("Select Gender:");
        System.out.println("1. Male");
        System.out.println("2. Female");
        System.out.print("Enter your choice: ");
    }

    public String getRoleFromChoice(int choice) {
        return switch (choice) {
            case 1 -> "Doctor";
            case 2 -> "Pharmacist";
            case 3 -> "Nurse";
            case 4 -> "Administrator";
            default -> {
                System.out.println("Invalid role option.");
                yield "";
            }
        };
    }

    public String getGenderFromChoice(int choice) {
        return switch (choice) {
            case 1 -> "Male";
            case 2 -> "Female";
            default -> {
                System.out.println("Invalid gender option.");
                yield "";
            }
        };
    }

    public void promptAgeInput() {
        System.out.print("Enter age to filter by: ");
    }

    public void displayFilteredDoctors(List<String> doctorData, String filterField, String filterValue) {
        System.out.println("\nDoctor List:\n");
        System.out.printf("%-12s %-17s %-10s %-5s %-20s%n", "Doctor ID", "Name", "Gender", "Age", "Specialisation");
        System.out.println("--------------------------------------------------------------------");

        for (String line : doctorData) {
            String[] details = line.split(",");
            if (matchesFilter(details, filterField, filterValue)) {
                System.out.printf("%-12s %-17s %-10s %-5s %-20s%n", details[0], details[1], details[3], details[4],
                        details[5]);
            }
        }
        System.out.println("--------------------------------------------------------------------");
    }

    public void displayFilteredStaff(List<String> staffData, String filterField, String filterValue) {
        System.out.println("\nStaff List:\n");
        System.out.printf("%-10s %-17s %-15s %-10s %-5s%n", "Staff ID", "Name", "Role", "Gender", "Age");
        System.out.println("-------------------------------------------------------------------");

        for (String line : staffData) {
            String[] details = line.split(",");
            if (matchesFilter(details, filterField, filterValue)) {
                System.out.printf("%-10s %-17s %-15s %-10s %-5s%n", details[0], details[1], details[2], details[3],
                        details[4]);
            }
        }
        System.out.println("-------------------------------------------------------------------");
    }

    private boolean matchesFilter(String[] details, String filterField, String filterValue) {
        if (filterField.isEmpty())
            return true;
        switch (filterField) {
            case "Gender" -> {
                return details[3].equalsIgnoreCase(filterValue);
            }
            case "Age" -> {
                return details[4].equals(filterValue);
            }
            case "Role" -> {
                return details[2].equalsIgnoreCase(filterValue);
            }
            case "Specialisation" -> {
                return details[5].equalsIgnoreCase(filterValue);
            }
            default -> {
                return false;
            }
        }
    }

    public void displayAddStaffMenu() {
        System.out.println("Add Staff: ");
        System.out.println("1. Doctor");
        System.out.println("2. Other Staff");
        System.out.print("Choose the type of staff: ");
    }

    public String collectStaffDetails(int staffType) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder details = new StringBuilder();
        
        if (staffType == 1) {
            System.out.print("Enter ID: ");
            details.append(scanner.nextLine()).append(",");
            System.out.print("Enter Name: ");
            details.append(scanner.nextLine()).append(",");
            System.out.print("Enter Gender: ");
            details.append(scanner.nextLine()).append(",");
            System.out.print("Enter Age: ");
            details.append(scanner.nextLine()).append(",");
            System.out.print("Enter Specialisation: ");
            details.append(scanner.nextLine());
        } else {
            System.out.print("Enter ID: ");
            details.append(scanner.nextLine()).append(",");
            System.out.print("Enter Name: ");
            details.append(scanner.nextLine()).append(",");
            System.out.print("Enter Role: ");
            details.append(scanner.nextLine()).append(",");
            System.out.print("Enter Gender: ");
            details.append(scanner.nextLine()).append(",");
            System.out.print("Enter Age: ");
            details.append(scanner.nextLine());
        }
        scanner.close();
        return details.toString();
    }

    public void displayUpdateStaffMenu() {
        System.out.println("Update Staff: ");
        System.out.println("1. Doctor");
        System.out.println("2. Other Staff");
        System.out.print("Choose the type of staff to update: ");
    }

    public String collectUpdatedStaffDetails(String[] existingFields, int idIndex) {
        Scanner scanner = new Scanner(System.in);
        String[] fields = Arrays.copyOf(existingFields, existingFields.length); // Copy existing data
    
        System.out.println("Select the field to update:");
        System.out.println("1. Name");
        if (fields[2].equals("Other Staff")) System.out.println("2. Role");
        System.out.println("3. Gender");
        System.out.println("4. Age");
        if (fields[2].equals("Doctor")) System.out.println("5. Specialisation");
    
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
    
        // Update only the selected field based on choice
        switch (choice) {
            case 1 -> {
                System.out.print("Enter new Name: ");
                fields[1] = scanner.nextLine();
            }
            case 2 -> {
                if (fields[2].equals("Other Staff")) {
                    System.out.print("Enter new Role: ");
                    fields[2] = scanner.nextLine();
                } else {
                    System.out.println("Invalid choice for Doctor.");
                }
            }
            case 3 -> {
                System.out.print("Enter new Gender: ");
                fields[3] = scanner.nextLine();
            }
            case 4 -> {
                System.out.print("Enter new Age: ");
                fields[4] = scanner.nextLine();
            }
            case 5 -> {
                if (fields[2].equals("Doctor")) {
                    System.out.print("Enter new Specialisation: ");
                    fields[5] = scanner.nextLine();
                } else {
                    System.out.println("Invalid choice for Other Staff.");
                }
            }
            default -> System.out.println("Invalid choice.");
        }
    
        return String.join(",", fields);
    }
        

    public String promptIDInput() {
        System.out.print("Enter the Hospital ID: ");
        Scanner sc = new Scanner(System.in);
        String id = sc.nextLine();
        return id;
    }

    public void promptIsDoctor() {
        System.out.print("Is the staff a Doctor?: ");
        System.out.println("1. Yes");
        System.out.println("2. No");
    }

    public void displayAppointmentOptions() {
        System.out.println("\nAppointment Viewing Options:");
        System.out.println("1. View All Appointments");
        System.out.println("2. Filter Appointments");
        System.out.print("Choose an option: ");
    }

    public void displayAppointmentFilterOptions() {
        System.out.println("\nFilter By:");
        System.out.println("1. Patient ID");
        System.out.println("2. Doctor ID");
        System.out.println("3. Date");
        System.out.print("Choose a filter option: ");
    }

    public void promptPatientIDInput() {
        System.out.print("Enter Patient ID to filter by: ");
    }

    public void promptDoctorIDInput() {
        System.out.print("Enter Doctor ID to filter by: ");
    }

    public void promptDateInput() {
        System.out.print("Enter Date to filter by (YYYY-MM-DD): ");
    }

    public void displayStatusFilterOptions() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nAppointment Status Filter:");
        System.out.println("1. View All Statuses");
        System.out.println("2. Filter by Status");
        System.out.print("Choose an option: ");
    }

    public void displayStatusTypeOptions() {
        System.out.println("Select Appointment Status:");
        System.out.println("1. Pending Confirmation");
        System.out.println("2. Confirmed");
        System.out.println("3. Cancelled");
        System.out.println("4. Completed");
        System.out.print("Enter your choice: ");
    }

    public String getStatusFromChoice(int choice) {
        return switch (choice) {
            case 1 -> "Pending Confirmation";
            case 2 -> "Confirmed";
            case 3 -> "Cancelled";
            case 4 -> "Completed";
            default -> {
                System.out.println("Invalid status option.");
                yield "Completed";
            }
        };
    }

    public void displayAppointments(List<String> appointments, String filterField, String filterValue,
            String statusFilter) {
        System.out.println("\nFiltered Appointments:");
        System.out.println("----------------------------------------------------------");

        for (String appointment : appointments) {
            String[] details = appointment.split(",");
            if (matchesAppointmentFilter(details, filterField, filterValue, statusFilter)) {
                System.out.printf(
                        "Appointment ID: %s%nDoctor ID: %s%nPatient ID: %s%nDate: %s%nTime: %s%nStatus: %s%nService: %s%nConsultation Notes: %s%nPrescribed Medications: %s%nQuantity of Medications: %s%nMedication Status: %s%n",
                        details[0], details[1], details[2], details[3], details[4], details[5], details[6], details[7],
                        details[8], details[9], details[10]);
                System.out.println("----------------------------------------------------------");
            }
        }
    }

    private boolean matchesAppointmentFilter(String[] details, String filterField, String filterValue,
            String statusFilter) {
        boolean matchesFilter = true;
        if (!filterField.isEmpty()) {
            switch (filterField) {
                case "PatientID" -> matchesFilter = details[2].equals(filterValue);
                case "DoctorID" -> matchesFilter = details[1].equals(filterValue);
                case "Date" -> matchesFilter = details[3].equals(filterValue);
            }
        }
        if (matchesFilter && !statusFilter.isEmpty()) {
            matchesFilter = details[5].equalsIgnoreCase(statusFilter);
        }
        return matchesFilter;
    }

    public void inventoryMenu() {
        System.out.println("\nMedicine Inventory Management");
        System.out.println("1. View Inventory");
        System.out.println("2. Add Medicine");
        System.out.println("3. Remove Medicine");
        System.out.println("4. Update Stock");
        System.out.println("5. Update Low Stock Indicator");
        System.out.println("6. Exit");
        System.out.print("Choose an option: ");
    }

    public void displayInventory(List<String> inventory) {
        System.out.println("\nMedicine Inventory:");
        System.out.printf("%-15s %-10s %-20s %-15s%n", "Medicine Name", "Stock", "Low Stock Indicator", "Stock Status");
        System.out.println("--------------------------------------------------------------");
        for (String line : inventory) {
            String[] details = line.split(",");
            System.out.printf("%-15s %-10s %-20s %-15s%n", details[0], details[1], details[2], details[3]);
        }
        System.out.println("--------------------------------------------------------------");
    }

    public String collectMedicineDetails(Scanner scanner) {
        StringBuilder details = new StringBuilder();
        System.out.print("Enter Medicine Name: ");
        details.append(scanner.nextLine()).append(",");
        System.out.print("Enter Quantity: ");
        details.append(scanner.nextInt()).append(",");
        System.out.print("Enter Low Stock Indicator: ");
        details.append(scanner.nextInt()).append(",In Stock");
        scanner.nextLine(); // Consume newline
        return details.toString();
    }

    public void promptMedicineName() {
        System.out.print("Enter Medicine Name: ");
    }

    public void promptStockQuantity() {
        System.out.print("Enter new stock quantity: ");
    }

    public void promptLowStockIndicator() {
        System.out.print("Enter new low stock indicator: ");
    }

    public void promptIncomingStock() {
        System.out.print("Enter the incoming stock quantity: ");
    }

    public void displayReplenishmentMenu() {
        System.out.println("\nReplenishment Requests Management");
        System.out.println("1. View Replenishment Requests");
        System.out.println("2. Restock Medicine");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
    }

    public void displayReplenishmentRequests(List<String> requests) {
        System.out.println("\nReplenishment Requests:");
        System.out.printf("%-15s %-10s %-15s %-20s%n", "Medicine Name", "Stock", "Date", "Replenishment Request");
        System.out.println("-------------------------------------------------------------");
        for (String line : requests) {
            String[] details = line.split(",");
            System.out.printf("%-15s %-10s %-15s %-20s%n", details[0], details[1], details[2], details[3]);
        }
        System.out.println("-------------------------------------------------------------");
    }

    public void displaySuccessMessage(String message) {
        System.out.println(message);
    }

    public void displayErrorMessage(String message) {
        System.out.println(message);
    }

    public void displayExitMessage(String context) {
        System.out.println("Exiting " + context + ".");
    }

    public void displayInvalidOption() {
        System.out.println("Invalid option. Please try again.");
    }
}
