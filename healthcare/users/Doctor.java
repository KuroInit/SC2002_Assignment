package healthcare.users;

import healthcare.records.Appointment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Doctor {
    private String doctorID;
    private String name;
    private String gender;
    private String age;
    private List<Appointment> appointments;
    private List<LocalDate> availableDates;

    public Doctor(String doctorID, String name, String gender, String age, String specialisation) {
        this.doctorID = doctorID;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.appointments = new ArrayList<>();
        this.availableDates = new ArrayList<>();
    }

    public String getDoctorID() {
        return doctorID;
    }

    public String getName() {
        return name;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public List<LocalDate> getAvailableDates() {
        return availableDates;
    }

    public void removeAvailability(LocalDate date) {
        availableDates.remove(date);
    }

    public void viewSchedule() {
        System.out.println("Doctor " + name + "'s Schedule:");
        System.out.println("Booked Appointments:");
        viewBookedAppointments();
        
        System.out.println("\nAvailable Appointments:");
        viewAvailableAppointments();
    }

    private void viewBookedAppointments() {
        String filePath = "appointmentRequests.csv"; // Updated file path as per your request
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // Skip the header row
            boolean hasBookedAppointments = false;
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) { // Ensure there are enough columns in each row
                    String currentDoctorID = parts[1]; // Doctor ID at index 1
                    String patientID = parts[2];       // Patient ID at index 2
                    LocalDate date = LocalDate.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE); // Date at index 3
                    LocalTime time = LocalTime.parse(parts[4]); // Time at index 4
                    String status = parts[5]; // Status at index 5
                    
                    // Check if the appointment is for this doctor and is approved
                    if (currentDoctorID.equals(doctorID) && "approved".equalsIgnoreCase(status)) {
                        System.out.println("Date: " + date + ", Time: " + time + ", Patient ID: " + patientID);
                        hasBookedAppointments = true;
                    }
                }
            }
            if (!hasBookedAppointments) {
                System.out.println("No booked appointments found.");
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        }
    }    

    private void viewAvailableAppointments() {
        String filePath = "availableAppointments.csv";
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // Skip the header row
            boolean hasAvailableAppointments = false;
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String currentDoctorID = parts[0];
                    LocalDate date = LocalDate.parse(parts[1], DateTimeFormatter.ISO_LOCAL_DATE);
                    LocalTime time = LocalTime.parse(parts[2]);
                    
                    // Only show available appointments for this doctor
                    if (currentDoctorID.equals(doctorID)) {
                        System.out.println("Date: " + date + ", Time: " + time);
                        hasAvailableAppointments = true;
                    }
                }
            }
            if (!hasAvailableAppointments) {
                System.out.println("No available appointments found.");
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        }
    }

    public void viewPatientMedicalRecords() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the Patient ID to view details: ");
        String patientID = scanner.nextLine();
        System.out.println("\n");
    
        String patientListPath = "Patient_List.csv";
        String medicalRecordsPath = "medicalRecords.csv";
        boolean foundPatient = false;
    
        // Step 1: Display patient details from Patient_List.csv
        System.out.println("Patient Details:");
        System.out.println("----------------------------------------------------");
    
        try (BufferedReader patientReader = new BufferedReader(new FileReader(patientListPath))) {
            String line;
            while ((line = patientReader.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(patientID)) {
                    System.out.printf("Patient ID: %s%n", details[0]);
                    System.out.printf("Name: %s%n", details[1]);
                    System.out.printf("Date of Birth: %s%n", details[2]);
                    System.out.printf("Gender: %s%n", details[3]);
                    System.out.printf("Blood Type: %s%n", details[4]);
                    System.out.printf("Email: %s%n", details[5]);
                    System.out.printf("Contact Number: %s%n", details[6]);
                    System.out.println("----------------------------------------------------\n");
                    foundPatient = true;
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while accessing the patient list.");
            e.printStackTrace();
            return;
        }
    
        if (!foundPatient) {
            System.out.println("No details found for this patient ID.");
            return;
        }
    
        // Step 2: Display medical history from medicalRecords.csv
        System.out.println("Medical History:");
        System.out.println("----------------------------------------------------");
    
        boolean hasHistory = false;
    
        try (BufferedReader medicalReader = new BufferedReader(new FileReader(medicalRecordsPath))) {
            String line;
            while ((line = medicalReader.readLine()) != null) {
                String[] record = line.split(",");
                if (record[0].equals(patientID)) {
                    System.out.printf("Date of Diagnosis: %s%n", record[1]);
                    System.out.printf("Diagnosis: %s%n", record[2]);
                    System.out.printf("Treatment Plan: %s%n", record[3]);
                    System.out.printf("Prescribed Medicine: %s%n", record[4]);
                    System.out.println("----------------------------------------------------");
                    hasHistory = true;
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while accessing the medical records.");
            e.printStackTrace();
        }
    
        if (!hasHistory) {
            System.out.println("No medical history found for this patient.");
        }
    }

    public void addPatientMedicalRecord() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter the Patient ID: ");
        String patientID = scanner.nextLine();

        System.out.print("Enter Date of Record (YYYY-MM-DD): ");
        String dateOfRecord = scanner.nextLine();
        
        System.out.print("Enter Diagnosis: ");
        String diagnosis = scanner.nextLine();
        
        System.out.print("Enter Treatment Plan: ");
        String treatmentPlan = scanner.nextLine();
        
        System.out.print("Enter Prescribed Medicine: ");
        String prescribedMedicine = scanner.nextLine();

        // Append the new record to medicalRecords.csv
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("medicalRecords.csv", true))) {
            // Write the new record in the format: PatientID, Date, Diagnosis, Treatment Plan, Prescribed Medicine
            bw.write(patientID + "," + dateOfRecord + "," + diagnosis + "," + treatmentPlan + "," + prescribedMedicine + "\n");
            System.out.println("Medical record added successfully for Patient ID: " + patientID);
        } catch (IOException e) {
            System.out.println("Error saving the medical record.");
        }
    }

    public void viewUpcomingAppointments() {
        System.out.println("Appointments for Dr. " + name + " (ID: " + doctorID + ")");
        System.out.println("----------------------------------------------------");
        boolean found = false;
        String appointmentRequestsPath = "appointmentRequests.csv";
        String patientListPath = "Patient_List.csv";

        // Load patient names from Patient_List.csv
        Map<String, String> patientNames = new HashMap<>();
        try (BufferedReader patientReader = new BufferedReader(new FileReader(patientListPath))) {
            String line;
            while ((line = patientReader.readLine()) != null) {
                String[] details = line.split(",");
                String patientId = details[0].trim();
                String patientName = details[1].trim();
                patientNames.put(patientId, patientName);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while accessing the patient list.");
            e.printStackTrace();
            return;
        }

    // Display upcoming approved appointments for this doctor

        try (BufferedReader br = new BufferedReader(new FileReader(appointmentRequestsPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                
                // Ensure there are enough fields to avoid index errors
                if (fields.length < 5) {
                    continue;
                }

                // Check if the appointment is for this doctor and has an APPROVED status
                if (fields[1].equals(doctorID) && fields[5].equalsIgnoreCase("CONFIRMED")) {
                    String appointmentID = fields[0];
                    String patientID = fields[2];
                    String date = fields[3];
                    String time = fields[4];
                    String patientName = patientNames.getOrDefault(patientID, "Unknown");

                    System.out.println("Appointment ID: " + appointmentID);
                    System.out.println("Patient ID: " + patientID);
                    System.out.println("Patient Name: " + patientName);
                    System.out.println("Date: " + date);
                    System.out.println("Time: " + time);
                    System.out.println("----------------------------------------------------");
                    found = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading appointments.");
            e.printStackTrace();
        }

        if (!found) {
            System.out.println("No approved appointments found.");
        }
    }

    public void selectAvailableSlot() {
        Scanner scanner = new Scanner(System.in);
    
        // Prompt the doctor to enter a date
        System.out.print("Enter the date (YYYY-MM-DD): ");
        String dateInput = scanner.nextLine();
    
        LocalDate date;
        try {
            date = LocalDate.parse(dateInput, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            System.out.println("Invalid date format. Please enter in YYYY-MM-DD format.");
            return;
        }
    
        // Check if the entered date is a Sunday
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            System.out.println("Selected date is a Sunday. No available slots.");
            return;
        }
    
        // Load booked slots for the given date and doctor ID from CSV
        HashSet<LocalTime> bookedSlots = getBookedSlots(date, this.doctorID);
    
        // Generate all possible slots for the given date, excluding 1 PM to 2 PM
        List<LocalTime> allSlots = new ArrayList<>();
        LocalTime time = LocalTime.of(9, 0);
        while (time.isBefore(LocalTime.of(17, 0))) {
            if (!time.equals(LocalTime.of(13, 0))) {  // Exclude 1 PM to 2 PM
                allSlots.add(time);
            }
            time = time.plusHours(1);
        }
    
        // Display all available slots and let the doctor mark slots as unavailable
        List<LocalTime> unavailableSlots = new ArrayList<>();
        System.out.println("Mark slots as unavailable by entering the slot number. Enter 'done' when finished.");
        for (int i = 0; i < allSlots.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, allSlots.get(i));
        }
    
        while (true) {
            System.out.print("Enter slot number to mark as unavailable, or 'done' to finish: ");
            String input = scanner.nextLine();
    
            if (input.equalsIgnoreCase("done")) {
                break;
            }
    
            try {
                int slotIndex = Integer.parseInt(input) - 1;
                if (slotIndex >= 0 && slotIndex < allSlots.size()) {
                    LocalTime selectedTime = allSlots.get(slotIndex);
                    if (!bookedSlots.contains(selectedTime) && !unavailableSlots.contains(selectedTime)) {
                        unavailableSlots.add(selectedTime);
                        System.out.println("Marked slot as unavailable: " + selectedTime);
                    } else {
                        System.out.println("Slot already marked as booked or unavailable.");
                    }
                } else {
                    System.out.println("Invalid slot number. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a slot number or 'done'.");
            }
        }
    
        // Determine available slots by excluding the unavailable ones
        List<LocalDateTime> availableSlots = new ArrayList<>();
        for (LocalTime slotTime : allSlots) {
            if (!unavailableSlots.contains(slotTime) && !bookedSlots.contains(slotTime)) {
                availableSlots.add(LocalDateTime.of(date, slotTime));
            }
        }
    
        // Write the available slots to availableAppointments.csv
        saveAvailableSlots(availableSlots);
        System.out.println("Schedule updated. Available slots saved to availableAppointments.csv.");
    }
    
    private HashSet<LocalTime> getBookedSlots(LocalDate date, String doctorID) {
        HashSet<LocalTime> bookedSlots = new HashSet<>();
        String filePath = "appointmentRequests.csv";
    
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String currentDoctorID = parts[1];
                    LocalDate bookedDate = LocalDate.parse(parts[3]);
                    LocalTime bookedTime = LocalTime.parse(parts[4]);
                    if (bookedDate.equals(date) && currentDoctorID.equals(doctorID)) {
                        bookedSlots.add(bookedTime);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        }
        return bookedSlots;
    }
    
    private void saveAvailableSlots(List<LocalDateTime> availableSlots) {
        String filePath = "availableAppointments.csv";
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            for (LocalDateTime slot : availableSlots) {
                writer.write(doctorID + "," + slot.toLocalDate() + "," + slot.toLocalTime().withSecond(0).withNano(0));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    public void viewPendingAppointments() {
        System.out.println("Pending Appointments for Dr. " + name + ":");
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader("appointmentRequests.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[1].equals(doctorID) && fields[5].equals("PENDING CONFIRMATION")) {  // Matching DoctorID and PENDING status
                    System.out.println("AppointmentID: " + fields[0] + ", PatientID: " + fields[2] +
                            ", Date: " + fields[3] + ", Time: " + fields[4] + ", Status: " + fields[5]);
                    found = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading appointments.");
        }

        if (!found) {
            System.out.println("No pending appointments found.");
        }
    }

    public void updateAppointmentStatus() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the Appointment ID to update: ");
        String appointmentId = scanner.nextLine();
    
        System.out.println("Choose an option:");
        System.out.println("1. Confirm Appointment");
        System.out.println("2. Cancel Appointment");
        System.out.print("Enter your choice (1 or 2): ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline
    
        String newStatus;
        if (choice == 1) {
            newStatus = "CONFIRMED";
        } else if (choice == 2) {
            newStatus = "CANCELLED";
        } else {
            System.out.println("Invalid choice. Please enter 1 or 2.");
            return;
        }
    
        List<String> appointments = new ArrayList<>();
        boolean updated = false;
    
        try (BufferedReader br = new BufferedReader(new FileReader("appointmentRequests.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(appointmentId)) {  // Matching AppointmentID
                    // Update the status field
                    fields[5] = newStatus;  // Assuming status is the 5th field in CSV
                    line = String.join(",", fields);
                    updated = true;
                }
                appointments.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading appointments.");
            e.printStackTrace();
        }
    
        if (updated) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("appointmentRequests.csv"))) {
                for (String appointment : appointments) {
                    bw.write(appointment + "\n");
                }
                System.out.println("Appointment status updated successfully to " + newStatus + ".");
            } catch (IOException e) {
                System.out.println("Error saving updated appointments.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Appointment not found or invalid Appointment ID provided.");
        }
    }

    // record Appointment Outcomes
    public void recordAppointmentOutcome() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the Appointment ID for which you want to record the outcome: ");
        String appointmentId = scanner.nextLine();

        System.out.print("Enter Type of Service: ");
        String typeOfService = scanner.nextLine();
        System.out.print("Enter Consultation Notes: ");
        String consultationNotes = scanner.nextLine();
        System.out.print("Enter Prescribed Medications: ");
        String prescribedMedications = scanner.nextLine();
        System.out.print("Enter Quantity of Medications: ");
        String quantity = scanner.nextLine();

        String medicationStatus = "PENDING";

        List<String> appointments = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader br = new BufferedReader(new FileReader("appointmentRequests.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(appointmentId) && fields[1].equals(doctorID) && fields[5].equals("CONFIRMED")) {
                    // Update the status to COMPLETED and add outcome details
                    fields[5] = "COMPLETED";  // Status field
                    fields[6] = typeOfService;  // Type of Service field
                    fields[7] = consultationNotes;
                    fields[8] = prescribedMedications;  // Prescribed Medications field
                    fields[9] = quantity;
                    fields[10] = medicationStatus;
                    line = String.join(",", fields);
                    updated = true;
                }
                appointments.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading appointments.");
        }

        if (updated) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("appointmentRequests.csv"))) {
                for (String appointment : appointments) {
                    bw.write(appointment + "\n");
                }
                System.out.println("Appointment outcome recorded successfully.");
            } catch (IOException e) {
                System.out.println("Error saving updated appointments.");
            }
        } else {
            System.out.println("Appointment not found or invalid Appointment ID provided.");
        }
    }

    public void doctormenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            showDoctorMenu();
            System.out.println("Enter your choice: ");
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    viewPatientMedicalRecords();
                    break;
                case 2:
                    addPatientMedicalRecord();
                    break;
                case 3:
                    viewSchedule();
                    break;
                case 4:
                    selectAvailableSlot();
                    break;
                case 5:
                    viewPendingAppointments();
                    updateAppointmentStatus();
                    break;
                case 6:
                    viewUpcomingAppointments();
                    break;
                case 7:
                    recordAppointmentOutcome();
                    break;
                case 8:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Please choose a valid option.");
            }
        } while (choice != 8);
    }

    // Helper method to find an appointment by ID
    public Appointment findAppointmentByID(String appointmentID) {
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentID().equals(appointmentID)) {
                return appointment;
            }
        }
        return null;
    }

    // Helper method to write/update an appointment to the CSV file
    public void writeOrUpdateAppointmentToCSV(Appointment appointment) {
        File file = new File("appointmentRequests.csv");
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            boolean exists = false;
            for (int i = 0; i < lines.size(); i++) {
                String[] data = lines.get(i).split(",");
                if (data[0].equals(appointment.getAppointmentID())) {
                    lines.set(i, appointment.toCSV());
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                lines.add(appointment.toCSV());
            }
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            System.out.println("Error writing to CSV: " + e.getMessage());
        }
    }


    public static void showDoctorMenu() {
        System.out.println("\n===== Doctor Menu =====");
        System.out.println("1. View Patient Medical Records");
        System.out.println("2. Update Patient Medical Records");
        System.out.println("3. View Personal Schedule");
        System.out.println("4. Set Availability for Appointments");
        System.out.println("5. Accept or Decline Appointment Requests");
        System.out.println("6. View Upcoming Appointments");
        System.out.println("7. Record Appointment Outcome");
        System.out.println("8. Logout");
        System.out.println("=======================\n");
    }
}
