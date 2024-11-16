package healthcare.users.view;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class AdministratorView {

    public void showAdminMenu() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("             Administrator Menu            ");
        System.out.println("===========================================");
        System.out.println("|                                         |");
        System.out.println("|  1. View and Manage Hospital Staff      |");
        System.out.println("|  2. View Appointment Details            |");
        System.out.println("|  3. View and Manage Medication Inventory|");
        System.out.println("|  4. Approve Replenishment Requests      |");
        System.out.println("|  5. View Patient Feedback               |");
        System.out.println("|  6. Logout                              |");
        System.out.println("|                                         |");
        System.out.println("===========================================");
        System.out.print("Choose an option: ");
    }

    public void displayStaffManagementMenu() {
        Screen.clearConsole();
        System.out.println("=========================================");
        System.out.println("         Hospital Staff Management       ");
        System.out.println("=========================================");
        System.out.println("|                                       |");
        System.out.println("|  1. View Staff                        |");
        System.out.println("|  2. Add Staff                         |");
        System.out.println("|  3. Update Staff                      |");
        System.out.println("|  4. Remove Staff                      |");
        System.out.println("|  5. Exit                              |");
        System.out.println("|                                       |");
        System.out.println("=========================================");
        System.out.print("Choose an option: ");
    }

    public void displayViewStaffOptions() {
        Screen.clearConsole();
        System.out.println("=========================================");
        System.out.println("               Staff List                ");
        System.out.println("=========================================");
        System.out.println("|                                       |");
        System.out.println("|  1. View All                          |");
        System.out.println("|  2. Filter by Role, Gender, or Age    |");
        System.out.println("|                                       |");
        System.out.println("=========================================");
        System.out.print("Choose an option: ");
    }

    public void displayFilterOptions() {
        System.out.println("===========================================");
        System.out.println("              Filter Options               ");
        System.out.println("===========================================");
        System.out.println("|                                         |");
        System.out.println("|  1. Role (for staff only)               |");
        System.out.println("|  2. Gender                              |");
        System.out.println("|  3. Age                                 |");
        System.out.println("|                                         |");
        System.out.println("===========================================");
        System.out.print("Choose a filter option: ");
    }

    public void displayRoleSelectionMenu() {
        System.out.println("===========================================");
        System.out.println("              Select a Role                ");
        System.out.println("===========================================");
        System.out.println("|                                         |");
        System.out.println("|  1. Doctor                              |");
        System.out.println("|  2. Pharmacist                          |");
        System.out.println("|  3. Nurse                               |");
        System.out.println("|  4. Administrator                       |");
        System.out.println("|                                         |");
        System.out.println("===========================================");
        System.out.print("Enter your choice: ");
    }

    public void displayGenderSelectionMenu() {
        System.out.println("===========================================");
        System.out.println("             Select Gender                 ");
        System.out.println("===========================================");
        System.out.println("|                                         |");
        System.out.println("|  1. Male                                |");
        System.out.println("|  2. Female                              |");
        System.out.println("|                                         |");
        System.out.println("===========================================");
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
        Screen.clearConsole();
        System.out.println("============================================================================");
        System.out.println("                        Doctor List                            ");
        System.out.println("============================================================================");
        System.out.printf("| %-12s | %-17s | %-10s | %-5s | %-17s |%n", "Doctor ID", "Name", "Gender", "Age",
                "Specialisation");
        System.out.println("----------------------------------------------------------------------------");

        boolean found = false;
        for (String line : doctorData) {
            String[] details = line.split(",");
            if (matchesFilter(details, filterField, filterValue)) {
                System.out.printf("| %-12s | %-17s | %-10s | %-5s | %-17s |%n", details[0], details[1], details[3],
                        details[4], details[5]);
                found = true;
            }
        }

        if (!found) {
            System.out.println("|                    No matching records found                 |");
        }

        System.out.println("----------------------------------------------------------------------------");
        System.out.println("\nPress Enter to continue...");
        new Scanner(System.in).nextLine();
    }

    public void displayFilteredStaff(List<String> staffData, String filterField, String filterValue) {
        Screen.clearConsole();
        Scanner scanner = new Scanner(System.in);
        System.out.println("==========================================================================");
        System.out.println("                          Staff List                         ");
        System.out.println("==========================================================================");
        System.out.printf("| %-10s | %-17s | %-15s | %-10s | %-7s |%n", "Staff ID", "Name", "Role", "Gender", "Age");
        System.out.println("--------------------------------------------------------------------------");

        boolean found = false;
        for (String line : staffData) {
            String[] details = line.split(",");
            if (matchesFilter(details, filterField, filterValue)) {
                System.out.printf("| %-10s | %-17s | %-15s | %-10s | %-7s |%n", details[0], details[1], details[2],
                        details[3], details[4]);
                found = true;
            }
        }

        if (!found) {
            System.out.println("|                     No matching records found              |");
        }

        System.out.println("-------------------------------------------------------------------------");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        scanner.close();
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
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("                Add Staff                  ");
        System.out.println("===========================================");
        System.out.println("|                                         |");
        System.out.println("|  1. Doctor                              |");
        System.out.println("|  2. Other Staff                         |");
        System.out.println("|                                         |");
        System.out.println("===========================================");
        System.out.print("Choose the type of staff: ");
    }

    public String collectStaffDetails(int staffType) {
        Screen.clearConsole();
        Scanner scanner = new Scanner(System.in);
        StringBuilder details = new StringBuilder();

        System.out.println("===========================================");
        System.out.println("          Collect Staff Details            ");
        System.out.println("===========================================");

        // Common inputs

        // Special input for Doctor type
        if (staffType == 1) {
            System.out.print("   Enter ID: ");
            details.append(scanner.nextLine()).append(",");

            System.out.print("   Enter Name: ");
            details.append(scanner.nextLine()).append(",");

            details.append("DOCTOR").append(",");

            System.out.print("   Enter Gender: ");
            details.append(scanner.nextLine()).append(",");

            System.out.print("   Enter Age: ");
            details.append(scanner.nextLine());
            details.append(",");

            System.out.print("   Enter Specialisation: ");
            details.append(scanner.nextLine());
        } else {
            // Add Role input for other staff types
            System.out.print("   Enter ID: ");
            details.append(scanner.nextLine()).append(",");

            System.out.print("   Enter Name: ");
            details.append(scanner.nextLine()).append(",");

            System.out.print("   Enter Role: ");
            details.append(scanner.nextLine()).append(",");

            System.out.print("   Enter Gender: ");
            details.append(scanner.nextLine()).append(",");

            System.out.print("   Enter Age: ");
            details.append(scanner.nextLine());

        }

        System.out.println("===========================================");
        scanner.close();
        return details.toString();
    }

    public void displayUpdateStaffMenu() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("              Update Staff                 ");
        System.out.println("===========================================");
        System.out.println("|                                         |");
        System.out.println("|  1. Doctor                              |");
        System.out.println("|  2. Other Staff                         |");
        System.out.println("|                                         |");
        System.out.println("===========================================");
        System.out.print("Choose the type of staff to update: ");
    }

    public String collectUpdatedStaffDetails(String[] existingFields, int idIndex) {
        Screen.clearConsole();
        Scanner scanner = new Scanner(System.in);
        String[] fields = Arrays.copyOf(existingFields, existingFields.length); // Copy existing data

        System.out.println("===========================================");
        System.out.println("          Update Staff Details             ");
        System.out.println("===========================================");
        System.out.println("Select the field to update:");

        if (idIndex == 1) {
            System.out.println("  1. Name");
            System.out.println("  2. Age");
            System.out.println("  3. Specialisation");
            System.out.println("===========================================");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Update only the selected field based on choice
            System.out.println("===========================================");
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter new Name: ");
                    fields[1] = scanner.nextLine();
                }
                case 2 -> {
                    System.out.print("Enter new Age: ");
                    fields[4] = scanner.nextLine();
                }
                case 3 -> {
                    System.out.print("Enter new Specialization: ");
                    fields[5] = scanner.nextLine();
                }

                default -> System.out.println("Invalid choice.");
            }
            System.out.println("===========================================");

        } else if (idIndex == 2) {
            System.out.println("  1. Name");
            System.out.println("  2. Role");
            System.out.println("  3. Age");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Update only the selected field based on choice
            System.out.println("===========================================");
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter new Name: ");
                    fields[1] = scanner.nextLine();
                }
                case 2 -> {
                    System.out.print("Enter new Role: ");
                    fields[2] = scanner.nextLine();
                    System.out.print("Enter new ID: ");
                    fields[0] = scanner.nextLine();
                }
                case 3 -> {
                    System.out.print("Enter new Age: ");
                    fields[4] = scanner.nextLine();
                }

                default -> System.out.println("Invalid choice.");
            }
            System.out.println("===========================================");

        }

        scanner.close();
        return String.join(",", fields);

    }

    public String promptIDInput() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("              Hospital ID Input            ");
        System.out.println("===========================================");
        System.out.print("   Enter the Hospital ID: ");
        Scanner sc = new Scanner(System.in);
        String id = sc.nextLine();
        System.out.println("===========================================");
        sc.close();
        return id;
    }

    public void promptIsDoctor() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("             Staff Type Selection          ");
        System.out.println("===========================================");
        System.out.println("|                                         |");
        System.out.println("|  Is the staff a Doctor?                 |");
        System.out.println("|                                         |");
        System.out.println("|  1. Yes                                 |");
        System.out.println("|  2. No                                  |");
        System.out.println("|                                         |");
        System.out.println("===========================================");
        System.out.print("Choose an option: ");
    }

    public void displayAppointmentOptions() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("        Appointment Viewing Options        ");
        System.out.println("===========================================");
        System.out.println("|                                         |");
        System.out.println("|  1. View All Appointments               |");
        System.out.println("|  2. Filter Appointments                 |");
        System.out.println("|                                         |");
        System.out.println("===========================================");
        System.out.print("Choose an option: ");
    }

    public void displayAppointmentFilterOptions() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("                Filter By                 ");
        System.out.println("===========================================");
        System.out.println("|                                         |");
        System.out.println("|  1. Patient ID                          |");
        System.out.println("|  2. Doctor ID                           |");
        System.out.println("|  3. Date                                |");
        System.out.println("|                                         |");
        System.out.println("===========================================");
        System.out.print("Choose a filter option: ");
    }

    public void promptPatientIDInput() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("           Patient ID Input Prompt         ");
        System.out.println("===========================================");
        System.out.print("   Enter Patient ID to filter by: ");
    }

    public void promptDoctorIDInput() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("           Doctor ID Input Prompt          ");
        System.out.println("===========================================");
        System.out.print("   Enter Doctor ID to filter by: ");
    }

    public void promptDateInput() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("             Date Input Prompt             ");
        System.out.println("===========================================");
        System.out.print("   Enter Date to filter by (YYYY-MM-DD): ");
    }

    public void displayStatusFilterOptions() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("       Appointment Status Filter           ");
        System.out.println("===========================================");
        System.out.println("|                                         |");
        System.out.println("|  1. View All Statuses                   |");
        System.out.println("|  2. Filter by Status                    |");
        System.out.println("|                                         |");
        System.out.println("===========================================");
        System.out.print("Choose an option: ");
    }

    public void displayStatusTypeOptions() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("      Select Appointment Status            ");
        System.out.println("===========================================");
        System.out.println("|                                         |");
        System.out.println("|  1. Pending Confirmation                |");
        System.out.println("|  2. Confirmed                           |");
        System.out.println("|  3. Cancelled                           |");
        System.out.println("|  4. Completed                           |");
        System.out.println("|                                         |");
        System.out.println("===========================================");
        System.out.print("Enter your choice: ");
    }

    public String getStatusFromChoice(int choice) {
        return switch (choice) {
            case 1 -> "Pending";
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
        Screen.clearConsole();
        System.out.println("==========================================================");
        System.out.println("                  Appointments                    ");
        System.out.println("==========================================================");
        boolean found = false;
        for (String appointment : appointments) {
            String[] details = appointment.split(",");
            if (filterField == null && filterValue == null && statusFilter == null) {
                System.out.println("| Appointment Details                                     |");
                System.out.println("----------------------------------------------------------");
                System.out.printf("| Appointment ID:           %s%n", details[0]);
                System.out.printf("| Doctor ID:                %s%n", details[1]);
                System.out.printf("| Patient ID:               %s%n", details[2]);
                System.out.printf("| Date:                     %s%n", details[3]);
                System.out.printf("| Time:                     %s%n", details[4]);
                System.out.printf("| Status:                   %s%n", details[5]);
                System.out.printf("| Service:                  %s%n", details[6]);
                System.out.printf("| Consultation Notes:       %s%n", details[7]);
                System.out.printf("| Prescribed Medications:   %s%n", details[8]);
                System.out.printf("| Quantity of Medications:  %s%n", details[9]);
                System.out.printf("| Medication Status:        %s%n", details[10]);
                System.out.println("----------------------------------------------------------");
            } else if (matchesAppointmentFilter(details, filterField, filterValue, statusFilter)) {
                System.out.println("| Appointment Details                                     |");
                System.out.println("----------------------------------------------------------");
                System.out.printf("| Appointment ID:           %s%n", details[0]);
                System.out.printf("| Doctor ID:                %s%n", details[1]);
                System.out.printf("| Patient ID:               %s%n", details[2]);
                System.out.printf("| Date:                     %s%n", details[3]);
                System.out.printf("| Time:                     %s%n", details[4]);
                System.out.printf("| Status:                   %s%n", details[5]);
                System.out.printf("| Service:                  %s%n", details[6]);
                System.out.printf("| Consultation Notes:       %s%n", details[7]);
                System.out.printf("| Prescribed Medications:   %s%n", details[8]);
                System.out.printf("| Quantity of Medications:  %s%n", details[9]);
                System.out.printf("| Medication Status:        %s%n", details[10]);
                System.out.println("----------------------------------------------------------");
                found = true;

                if (!found) {
                    System.out.println("|                No matching appointments found           |");
                    System.out.println("----------------------------------------------------------");
                }
            }
        }
        System.out.println("==========================================================");
        System.out.println("\nPress Enter to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
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
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("      Medicine Inventory Management        ");
        System.out.println("===========================================");
        System.out.println("|                                         |");
        System.out.println("|  1. View Inventory                      |");
        System.out.println("|  2. Add Medicine                        |");
        System.out.println("|  3. Remove Medicine                     |");
        System.out.println("|  4. Update Stock                        |");
        System.out.println("|  5. Update Low Stock Indicator          |");
        System.out.println("|  6. Exit                                |");
        System.out.println("|                                         |");
        System.out.println("===========================================");
        System.out.print("Choose an option: ");
    }

    public void displayInventory(List<String> inventory) {
        Screen.clearConsole();
        System.out.println("=========================================================================");
        System.out.println("                  Medicine Inventory                          ");
        System.out.println("=========================================================================");
        System.out.printf("| %-15s | %-10s | %-20s | %-15s |%n", "Medicine Name", "Stock", "Low Stock Indicator",
                "Stock Status");
        System.out.println("-------------------------------------------------------------------------");

        if (inventory.isEmpty()) {
            System.out.println("|                 No medicines available                     |");
        } else {
            for (String line : inventory) {
                String[] details = line.split(",");
                System.out.printf("| %-15s | %-10s | %-20s | %-15s |%n", details[0], details[1], details[2],
                        details[3]);
            }
        }

        System.out.println("-------------------------------------------------------------------------");
        System.out.println("\nPress Enter to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
    }

    public String collectMedicineDetails(Scanner scanner) {
        Screen.clearConsole();
        StringBuilder details = new StringBuilder();

        System.out.println("===========================================");
        System.out.println("          Collect Medicine Details         ");
        System.out.println("===========================================");

        // Collect medicine name
        System.out.print("   Enter Medicine Name: ");
        details.append(scanner.nextLine()).append(",");

        // Collect quantity
        System.out.print("   Enter Quantity: ");
        String quantity = scanner.nextLine();
        details.append(quantity).append(",");

        // Collect low stock indicator
        System.out.print("   Enter Low Stock Indicator: ");
        String lowstock = scanner.nextLine();
        if (Integer.parseInt(quantity) < Integer.parseInt(lowstock)) {
            details.append(lowstock).append(",Low Stock");
        } else {
            details.append(lowstock).append(",In Stock");
        }

        scanner.nextLine(); // Consume newline

        System.out.println("===========================================");
        scanner.close();
        return details.toString();
    }

    public void promptMedicineName() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("           Medicine Name Input             ");
        System.out.println("===========================================");
        System.out.print("   Enter Medicine Name: ");
    }

    public void promptStockQuantity() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("           Stock Quantity Input            ");
        System.out.println("===========================================");
        System.out.print("   Enter new stock quantity: ");
    }

    public void promptLowStockIndicator() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("       Low Stock Indicator Input           ");
        System.out.println("===========================================");
        System.out.print("   Enter new low stock indicator: ");
    }

    public void promptIncomingStock() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("         Incoming Stock Quantity           ");
        System.out.println("===========================================");
        System.out.print("   Enter the incoming stock quantity: ");
    }

    public void displayReplenishmentMenu() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("    Replenishment Requests Management      ");
        System.out.println("===========================================");
        System.out.println("|                                         |");
        System.out.println("|  1. View Replenishment Requests         |");
        System.out.println("|  2. Restock Medicine                    |");
        System.out.println("|  3. Exit                                |");
        System.out.println("|                                         |");
        System.out.println("===========================================");
        System.out.print("Choose an option: ");
    }

    public void displayReplenishmentRequests(List<String> requests) {
        Screen.clearConsole();
        Scanner scanner = new Scanner(System.in);
        System.out.println("=========================================================================");
        System.out.println("               Replenishment Requests                        ");
        System.out.println("=========================================================================");
        System.out.printf("| %-15s | %-10s | %-15s | %-19s |%n", "Medicine Name", "Stock", "Date",
                "Replenishment Request");
        System.out.println("-------------------------------------------------------------------------");

        if (requests.isEmpty()) {
            System.out.println("|                   No replenishment requests found          |");
        } else {
            for (String line : requests) {
                String[] details = line.split(",");
                System.out.printf("| %-15s | %-10s | %-15s | %-20s |%n", details[0], details[1], details[2],
                        details[3]);
            }
        }

        System.out.println("-------------------------------------------------------------------------");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        scanner.close();
    }

    public void displaySuccessMessage(String message) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("===========================================");
        System.out.println("                SUCCESS                    ");
        System.out.println("===========================================");
        System.out.println("   " + message);
        System.out.println("===========================================");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        scanner.close();
    }

    public void displayErrorMessage(String message) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("===========================================");
        System.out.println("                 ERROR                     ");
        System.out.println("===========================================");
        System.out.println("   " + message);
        System.out.println("===========================================");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        scanner.close();
    }

    public void displayExitMessage(String context) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("===========================================");
        System.out.println("               EXIT MESSAGE                ");
        System.out.println("===========================================");
        System.out.println("   Exiting " + context + ".");
        System.out.println("===========================================");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        scanner.close();
    }

    public void displayInvalidOption() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("===========================================");
        System.out.println("            INVALID OPTION                 ");
        System.out.println("===========================================");
        System.out.println("   Invalid option. Please try again.");
        System.out.println("===========================================");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        scanner.close();
    }

}