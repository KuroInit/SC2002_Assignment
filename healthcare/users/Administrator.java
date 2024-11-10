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
    private static final String staffListPath = "Staff_List.csv";
    private static final String doctorListPath = "Doctor_List.csv";
    private static final String appointmentRequestsPath = "appointmentRequests.csv";
    private static final String medicineListPath = "Medicine_List.csv";
    private static final String replenishmentRequestsPath = "Replenishment_Requests.csv";

    public Administrator(String administratorID,String name,String gender,String age) {
        this.administratorID = administratorID;
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    public void manageStaff() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nHospital Staff Management");
            System.out.println("1. View Staff");
            System.out.println("2. Add Staff");
            System.out.println("3. Update Staff");
            System.out.println("4. Remove Staff");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewStaff();
                    break;
                case 2:
                    addStaff();
                    break;
                case 3:
                    updateStaff();
                    break;
                case 4:
                    removeStaff();
                    break;
                case 5:
                    System.out.println("Exiting staff management.");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private void viewStaff() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nStaff List:");
        System.out.println("1. View All");
        System.out.println("2. Filter by Role, Gender, or Age");
        System.out.print("Choose an option: ");
        
        int filterChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String filterField = "";
        String filterValue = "";

        if (filterChoice == 2) {
            System.out.println("\nFilter Options:");
            System.out.println("1. Role (for staff only)");
            System.out.println("2. Gender");
            System.out.println("3. Age");
            System.out.print("Choose a filter option: ");
            int filterOption = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (filterOption) {
                case 1:
                    System.out.println("Select a Role:");
                    System.out.println("1. Doctor");
                    System.out.println("2. Pharmacist");
                    System.out.println("3. Nurse");
                    System.out.println("4. Administrator");
                    System.out.print("Enter your choice: ");
                    int roleChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    if (roleChoice == 1) { // Doctor
                        System.out.println("Doctor Filter Options:");
                        System.out.println("1. View All Doctors");
                        System.out.println("2. Filter by Specialisation");
                        System.out.print("Choose an option: ");
                        int doctorFilterChoice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        if (doctorFilterChoice == 2) { // Filter by Specialisation
                            filterField = "Specialisation";
                            System.out.print("Enter specialization to filter by (e.g., Cardiology): ");
                            filterValue = scanner.nextLine();
                        } else {
                            // View all doctors, no filter applied to specialization
                            filterField = "Role";
                            filterValue = "Doctor";
                        }
                    } else { // Other Staff Roles
                        filterField = "Role";
                        switch (roleChoice) {
                            case 2: filterValue = "Pharmacist"; break;
                            case 3: filterValue = "Nurse"; break;
                            case 4: filterValue = "Administrator"; break;
                            default:
                                System.out.println("Invalid role option.");
                                return;
                        }
                    }
                    break;

                case 2:
                    filterField = "Gender";
                    System.out.println("Select Gender:");
                    System.out.println("1. Male");
                    System.out.println("2. Female");
                    System.out.print("Enter your choice: ");
                    int genderChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    switch (genderChoice) {
                        case 1: filterValue = "Male"; break;
                        case 2: filterValue = "Female"; break;
                        default:
                            System.out.println("Invalid gender option.");
                            return;
                    }
                    break;

                case 3:
                    filterField = "Age";
                    System.out.print("Enter age to filter by: ");
                    filterValue = scanner.nextLine();
                    break;

                default:
                    System.out.println("Invalid filter option.");
                    return;
            }
        }

        System.out.println("\nDoctors:\n");
        displayDoctorContent(doctorListPath, filterField, filterValue);
        System.out.println("-----------------------------------------------------------------------------------\n");
        System.out.println("Other Staff:\n");
        displayStaffContent(staffListPath, filterField, filterValue);
    }

    // Display doctors in a table format with optional filtering
    private void displayDoctorContent(String filePath, String filterField, String filterValue) {
        System.out.printf("%-12s %-17s %-10s %-5s %-20s%n", 
                "Doctor ID", "Name", "Gender", "Age", "Specialisation");
        System.out.println("--------------------------------------------------------------------");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length == 5) { // Ensure all fields are present
                    boolean matchesFilter = true;
                    if (!filterField.isEmpty()) {
                        switch (filterField) {
                            case "Gender":
                                matchesFilter = details[2].equalsIgnoreCase(filterValue);
                                break;
                            case "Age":
                                matchesFilter = details[3].equals(filterValue);
                                break;
                            case "Specialisation":
                                matchesFilter = details[4].equalsIgnoreCase(filterValue);
                                break;
                            default:
                                matchesFilter = false;
                        }
                    }
                    if (matchesFilter) {
                        System.out.printf("%-12s %-17s %-10s %-5s %-20s%n", 
                                details[0], details[1], details[2], details[3], details[4]);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading doctor file: " + e.getMessage());
        }
    }

    // Display other staff in a table format with optional filtering
    private void displayStaffContent(String filePath, String filterField, String filterValue) {
        System.out.printf("%-10s %-17s %-15s %-10s %-5s%n", 
                "Staff ID", "Name", "Role", "Gender", "Age");
        System.out.println("-------------------------------------------------------------------");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length == 5) { // Ensure all fields are present
                    boolean matchesFilter = true;
                    if (!filterField.isEmpty()) {
                        switch (filterField) {
                            case "Role":
                                matchesFilter = details[2].equalsIgnoreCase(filterValue);
                                break;
                            case "Gender":
                                matchesFilter = details[3].equalsIgnoreCase(filterValue);
                                break;
                            case "Age":
                                matchesFilter = details[4].equals(filterValue);
                                break;
                            default:
                                matchesFilter = false;
                        }
                    }
                    if (matchesFilter) {
                        System.out.printf("%-10s %-17s %-15s %-10s %-5s%n", 
                                details[0], details[1], details[2], details[3], details[4]);
                    }
                }
            }
            System.out.println("-------------------------------------------------------------------");
        } catch (IOException e) {
            System.out.println("Error reading staff file: " + e.getMessage());
        }
    }

    // Method to add a new staff member, specifying if it's a doctor or other staff
    private void addStaff() {
        Scanner scanner = new Scanner(System.in);
        String filePath;
        String idLabel;

        System.out.println("Add Staff: ");
        System.out.println("1. Doctor");
        System.out.println("2. Other Staff");
        System.out.print("Choose the type of staff: ");
        int staffType = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (staffType == 1) {
            filePath = doctorListPath;
            idLabel = "Doctor ID";
        } else if (staffType == 2) {
            filePath = staffListPath;
            idLabel = "Staff ID";
        } else {
            System.out.println("Invalid option.");
            return;
        }

        System.out.print("Enter " + idLabel + ": ");
        String id = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Gender: ");
        String gender = scanner.nextLine();
        System.out.print("Enter Age: ");
        String age = scanner.nextLine();

        String newEntry;
        if (staffType == 1) {
            System.out.print("Enter Specialisation: ");
            String specialisation = scanner.nextLine();
            newEntry = id + "," + name + "," + gender + "," + age + "," + specialisation;
        } else {
            System.out.print("Enter Role: ");
            String role = scanner.nextLine();
            newEntry = id + "," + name + "," + role + "," + gender + "," + age;
        }

        addToFile(newEntry, filePath);
    }

    private void addToFile(String entry, String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(entry);
            bw.newLine();
            System.out.println("Entry added successfully.");
        } catch (IOException e) {
            System.out.println("Error adding entry: " + e.getMessage());
        }
    }

    // Method to update an existing staff member, checking if they're a doctor
    private void updateStaff() {
        Scanner scanner = new Scanner(System.in);
        String filePath;
        String idLabel;

        System.out.println("Update Staff: ");
        System.out.println("1. Doctor");
        System.out.println("2. Other Staff");
        System.out.print("Choose the type of staff to update: ");
        int staffType = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (staffType == 1) {
            filePath = doctorListPath;
            idLabel = "Doctor ID";
        } else if (staffType == 2) {
            filePath = staffListPath;
            idLabel = "Staff ID";
        } else {
            System.out.println("Invalid option.");
            return;
        }

        System.out.print("Enter the " + idLabel + " to update: ");
        String id = scanner.nextLine();

        List<String> entries = new ArrayList<>();
        boolean entryFound = false;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(id)) {
                    entryFound = true;
                    System.out.println("Select the field to update:");
                    System.out.println("1. Name");
                    if (staffType == 2) System.out.println("2. Role");
                    System.out.println("3. Gender");
                    System.out.println("4. Age");
                    if (staffType == 1) System.out.println("5. Specialisation");

                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    switch (choice) {
                        case 1:
                            System.out.print("Enter new Name: ");
                            details[1] = scanner.nextLine();
                            break;
                        case 2:
                            if (staffType == 2) {
                                System.out.print("Enter new Role: ");
                                details[2] = scanner.nextLine();
                                break;
                            }
                            System.out.println("Invalid choice for Doctor.");
                            return;
                        case 3:
                            System.out.print("Enter new Gender: ");
                            details[staffType == 1 ? 2 : 3] = scanner.nextLine();
                            break;
                        case 4:
                            System.out.print("Enter new Age: ");
                            details[staffType == 1 ? 3 : 4] = scanner.nextLine();
                            break;
                        case 5:
                            if (staffType == 1) {
                                System.out.print("Enter new Specialisation: ");
                                details[4] = scanner.nextLine();
                                break;
                            }
                            System.out.println("Invalid choice for Other Staff.");
                            return;
                        default:
                            System.out.println("Invalid choice.");
                            return;
                    }
                    line = String.join(",", details);
                }
                entries.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading list: " + e.getMessage());
            return;
        }

        if (entryFound) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
                for (String updatedLine : entries) {
                    bw.write(updatedLine);
                    bw.newLine();
                }
                System.out.println("Details updated successfully.");
            } catch (IOException e) {
                System.out.println("Error updating list: " + e.getMessage());
            }
        } else {
            System.out.println(idLabel + " not found.");
        }
    }

    // Method to remove a staff member, checking if they're a doctor
    private void removeStaff() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Is the staff a Doctor? (yes/no): ");
        String isDoctor = scanner.nextLine().trim().toLowerCase();
        String filePath = isDoctor.equals("yes") ? doctorListPath : staffListPath;
        String idLabel = isDoctor.equals("yes") ? "Doctor ID" : "Staff ID";

        System.out.print("Enter the " + idLabel + " to remove: ");
        String id = scanner.nextLine();

        List<String> entries = new ArrayList<>();
        boolean entryFound = false;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(id)) {
                    entryFound = true;
                    System.out.println(idLabel + " " + id + " removed.");
                    continue; // Skip adding this line to the list to remove it
                }
                entries.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading list: " + e.getMessage());
            return;
        }
        if (entryFound) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
                for (String updatedLine : entries) {
                    bw.write(updatedLine);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error saving updated list: " + e.getMessage());
            }
        } else {
            System.out.println(idLabel + " not found.");
        }
    }

    // Filter Staff by Role, Gender, and Age
    // Appointment Management
    public void viewAppointments() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nAppointment Viewing Options:");
        System.out.println("1. View All Appointments");
        System.out.println("2. Filter Appointments");
        System.out.print("Choose an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        System.out.println("----------------------------------------------------------");

        String filterField = "";
        String filterValue = "";
        String statusFilter = "";

        if (choice == 2) {
            System.out.println("\nFilter By:");
            System.out.println("1. Patient ID");
            System.out.println("2. Doctor ID");
            System.out.println("3. Date");
            System.out.print("Choose a filter option: ");
            int filterOption = scanner.nextInt();
            scanner.nextLine();

            switch (filterOption) {
                case 1:
                    filterField = "PatientID";
                    System.out.print("Enter Patient ID to filter by: ");
                    filterValue = scanner.nextLine();
                    break;
                case 2:
                    filterField = "DoctorID";
                    System.out.print("Enter Doctor ID to filter by: ");
                    filterValue = scanner.nextLine();
                    break;
                case 3:
                    filterField = "Date";
                    System.out.print("Enter Date to filter by (YYYY-MM-DD): ");
                    filterValue = scanner.nextLine();
                    break;
                default:
                    System.out.println("Invalid filter option.");
                    return;
            }

            System.out.println("\nAppointment Status Filter:");
            System.out.println("1. View All Statuses");
            System.out.println("2. Filter by Status");
            System.out.print("Choose an option: ");
            int statusChoice = scanner.nextInt();
            scanner.nextLine();
            System.out.println("----------------------------------------------------------");

            if (statusChoice == 2) {
                System.out.println("Select Appointment Status:");
                System.out.println("1. Pending Confirmation");
                System.out.println("2. Confirmed");
                System.out.println("3. Cancelled");
                System.out.println("4. Completed");
                System.out.print("Enter your choice: ");
                int statusOption = scanner.nextInt();
                scanner.nextLine();
                System.out.println("----------------------------------------------------------");

                switch (statusOption) {
                    case 1: statusFilter = "Pending Confirmation"; break;
                    case 2: statusFilter = "Confirmed"; break;
                    case 3: statusFilter = "Cancelled"; break;
                    case 4: statusFilter = "Completed"; break;
                    default:
                        System.out.println("Invalid status option.");
                        return;
                }
            }
        }

        displayAppointments(filterField, filterValue, statusFilter);
    }

    private void displayAppointments(String filterField, String filterValue, String statusFilter) {
        boolean appointmentFound = false;
    
        try (BufferedReader br = new BufferedReader(new FileReader(appointmentRequestsPath))) {
            String line;
            line = br.readLine(); // Skip header
    
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
    
                boolean matchesFilter = true;
    
                // Check primary filter (PatientID, DoctorID, or Date)
                if (!filterField.isEmpty()) {
                    switch (filterField) {
                        case "PatientID":
                            matchesFilter = details[2].equals(filterValue);
                            break;
                        case "DoctorID":
                            matchesFilter = details[1].equals(filterValue);
                            break;
                        case "Date":
                            matchesFilter = details[3].equals(filterValue);
                            break;
                        default:
                            matchesFilter = false;
                    }
                }
    
                // Check status filter if applicable
                if (matchesFilter && !statusFilter.isEmpty()) {
                    matchesFilter = details[5].equalsIgnoreCase(statusFilter);
                }
    
                // Display results based on filter matches
                if (matchesFilter) {
                    appointmentFound = true;
                    
                    // Print each field on a new line
                    System.out.println("Appointment ID: " + details[0]);
                    System.out.println("Doctor ID: " + details[1]);
                    System.out.println("Patient ID: " + details[2]);
                    System.out.println("Date: " + details[3]);
                    System.out.println("Time: " + details[4]);
                    System.out.println("Status: " + details[5]);
                    System.out.println("Service: " + details[6]);
                    System.out.println("Consultation Notes: " + details[7]);
                    System.out.println("Prescribed Medications: " + details[8]);
                    System.out.println("Quantity of Medications: " + details[9]);
                    System.out.println("Medication Status: " + details[10]);
    
                    // Separator line between appointments
                    System.out.println("----------------------------------------------------------");
                }
            }
    
            // If no appointments were found, print a message
            if (!appointmentFound) {
                System.out.println("No appointments found for the specified filter.");
            }
    
        } catch (IOException e) {
            System.out.println("Error reading appointment requests file: " + e.getMessage());
        }
    }
    

    // Inventory Management
    public void manageInventory() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nMedicine Inventory Management");
            System.out.println("1. View Inventory");
            System.out.println("2. Add Medicine");
            System.out.println("3. Remove Medicine");
            System.out.println("4. Update Stock");
            System.out.println("5. Update Low Stock Indicator");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewInventory();
                    break;
                case 2:
                    addMedicine();
                    break;
                case 3:
                    removeMedicine();
                    break;
                case 4:
                    updateStock();
                    break;
                case 5:
                    updateLowStockIndicator();
                    break;
                case 6:
                    System.out.println("Exiting inventory management.");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // Method to view the entire inventory
    private void viewInventory() {
        System.out.println("\nMedicine Inventory:");
        System.out.printf("%-15s %-10s %-20s %-15s%n", "Medicine Name", "Stock", "Low Stock Indicator", "Stock Status");
        System.out.println("--------------------------------------------------------------");
    
        try (BufferedReader br = new BufferedReader(new FileReader(medicineListPath))) {
            String line;
            
            // Assuming the file has a header row and skipping it
            br.readLine();  // Skip the header line in the CSV file if present
            
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                
                if (details.length >= 4) { // Ensure the line has enough fields
                    System.out.printf("%-15s %-10s %-20s %-15s%n", 
                            details[0], details[1], details[2], details[3]);
                }
            }
            System.out.println("--------------------------------------------------------------");
        } catch (IOException e) {
            System.out.println("Error reading medicine inventory: " + e.getMessage());
        }
    }
    

    // Method to add a new medicine to the inventory
    private void addMedicine() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Medicine Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();
        System.out.print("Enter Low Stock Indicator: ");
        int lowStockIndicator = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String newEntry = name + "," + quantity + "," + lowStockIndicator + ",In Stock";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(medicineListPath, true))) {
            bw.write(newEntry);
            bw.newLine();
            System.out.println("Medicine added successfully.");
        } catch (IOException e) {
            System.out.println("Error adding medicine: " + e.getMessage());
        }
    }

    // Method to remove a medicine from the inventory
    private void removeMedicine() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Medicine Name to remove: ");
        String medicineName = scanner.nextLine();

        List<String> medicines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(medicineListPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (!details[0].equalsIgnoreCase(medicineName)) {
                    medicines.add(line);
                } else {
                    found = true;
                    System.out.println("Medicine " + medicineName + " removed.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading medicine inventory: " + e.getMessage());
            return;
        }

        if (found) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(medicineListPath))) {
                for (String medicine : medicines) {
                    bw.write(medicine);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error saving updated inventory: " + e.getMessage());
            }
        } else {
            System.out.println("Medicine not found.");
        }
    }

    // Method to update the stock quantity of a medicine
    private void updateStock() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Medicine Name to update stock: ");
        String medicineName = scanner.nextLine();
        System.out.print("Enter new stock quantity: ");
        int newQuantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        List<String> medicines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(medicineListPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equalsIgnoreCase(medicineName)) {
                    details[1] = String.valueOf(newQuantity);
                    details[3] = newQuantity <= Integer.parseInt(details[2]) ? "Low Stock" : "In Stock";
                    line = String.join(",", details);
                    found = true;
                    System.out.println("Stock updated for " + medicineName + ".");
                }
                medicines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading medicine inventory: " + e.getMessage());
            return;
        }

        if (found) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(medicineListPath))) {
                for (String medicine : medicines) {
                    bw.write(medicine);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error saving updated inventory: " + e.getMessage());
            }
        } else {
            System.out.println("Medicine not found.");
        }
    }

    // Method to update the low stock indicator of a medicine
    private void updateLowStockIndicator() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Medicine Name to update low stock indicator: ");
        String medicineName = scanner.nextLine();
        System.out.print("Enter new low stock indicator: ");
        int newLowStockIndicator = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        List<String> medicines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(medicineListPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equalsIgnoreCase(medicineName)) {
                    details[2] = String.valueOf(newLowStockIndicator);
                    details[3] = Integer.parseInt(details[1]) <= newLowStockIndicator ? "Low Stock" : "In Stock";
                    line = String.join(",", details);
                    found = true;
                    System.out.println("Low stock indicator updated for " + medicineName + ".");
                }
                medicines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading medicine inventory: " + e.getMessage());
            return;
        }

        if (found) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(medicineListPath))) {
                for (String medicine : medicines) {
                    bw.write(medicine);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error saving updated inventory: " + e.getMessage());
            }
        } else {
            System.out.println("Medicine not found.");
        }
    }

    // Approve Replenishment Requests
    public void manageReplenishmentRequests() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nReplenishment Requests Management");
            System.out.println("1. View Replenishment Requests");
            System.out.println("2. Restock Medicine");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewReplenishmentRequests();
                    break;
                case 2:
                    restockMedicine();
                    break;
                case 3:
                    System.out.println("Exiting replenishment requests management.");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // Method to view replenishment requests
    private void viewReplenishmentRequests() {
        System.out.println("\nReplenishment Requests:\n");
        System.out.printf("%-15s %-10s %-15s %-20s%n", "Medicine Name", "Stock", "Date", "Replenishment Request");
        System.out.println("-------------------------------------------------------------");

        try (BufferedReader br = new BufferedReader(new FileReader(replenishmentRequestsPath))) {
            String line;
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length >= 4) {
                    System.out.printf("%-15s %-10s %-15s %-20s%n", details[0], details[1], details[2], details[3]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading replenishment requests: " + e.getMessage());
        }
    }

    // Method to restock a specified medicine and update replenishment status
    private void restockMedicine() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the Medicine Name to restock: ");
        String medicineName = scanner.nextLine();
        
        System.out.print("Enter the incoming stock quantity: ");
        int incomingStock = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        boolean medicineFound = updateMedicineStock(medicineName, incomingStock);
        if (medicineFound) {
            updateReplenishmentRequestStatus(medicineName);
        } else {
            System.out.println("Medicine not found in inventory.");
        }
    }

    // Helper method to update the stock in Medicine_List.csv
    private boolean updateMedicineStock(String medicineName, int incomingStock) {
        List<String> medicines = new ArrayList<>();
        boolean medicineFound = false;

        try (BufferedReader br = new BufferedReader(new FileReader(medicineListPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equalsIgnoreCase(medicineName)) {
                    int currentStock = Integer.parseInt(details[1]);
                    int newStock = currentStock + incomingStock;
                    details[1] = String.valueOf(newStock);
                    details[3] = "In Stock";
                    line = String.join(",", details);
                    medicineFound = true;
                    System.out.println("Restocked " + medicineName + ". New stock quantity: " + newStock);
                }
                medicines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading medicine inventory: " + e.getMessage());
            return false;
        }

        if (medicineFound) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(medicineListPath))) {
                for (String medicine : medicines) {
                    bw.write(medicine);
                    bw.newLine();
                }
                System.out.println("Inventory updated successfully.");
            } catch (IOException e) {
                System.out.println("Error saving updated inventory: " + e.getMessage());
            }
        }
        
        return medicineFound;
    }

    // Helper method to update the replenishment request status to "Approved" in Replenishment_Requests.csv
    private void updateReplenishmentRequestStatus(String medicineName) {
        List<String> requests = new ArrayList<>();
        boolean requestUpdated = false;

        try (BufferedReader br = new BufferedReader(new FileReader(replenishmentRequestsPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equalsIgnoreCase(medicineName) && details[3].equalsIgnoreCase("REQUESTED")) {
                    details[3] = "APPROVED";  // Update status to "Approved"
                    line = String.join(",", details);
                    requestUpdated = true;
                    System.out.println("Replenishment request approved for " + medicineName + ".");
                }
                requests.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading replenishment requests: " + e.getMessage());
            return;
        }

        if (requestUpdated) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(replenishmentRequestsPath))) {
                for (String request : requests) {
                    bw.write(request);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error saving updated replenishment requests: " + e.getMessage());
            }
        } else {
            System.out.println("No pending replenishment request found for " + medicineName + ".");
        }
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
                case 1 -> manageStaff();
                case 2 -> viewAppointments();
                case 3 -> manageInventory();
                case 4 -> manageReplenishmentRequests();
                case 5 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice. Please select a valid option.");
            }
        } while (choice != 5);
    }

    public static void showAdminMenu() {
        System.out.println("\n===== Administrator Menu =====");
        System.out.println("1. View and Manage Hospital Staff");
        System.out.println("2. View Appointment Details");
        System.out.println("3. View and Manage Medication Inventory");
        System.out.println("4. Approve Replenishment Requests");
        System.out.println("5. Logout");
        System.out.println("==============================\n");
    }
}

