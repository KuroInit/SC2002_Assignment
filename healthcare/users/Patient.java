package healthcare.users;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import java.util.Random;

public class Patient {
    private String patientID;
    
    public Patient(String patientID) {
        this.patientID = patientID;
    }

    public static void showPatientMenu() {
        System.out.println("\n===== Patient Menu =====");
        System.out.println("1. View Medical Record");
        System.out.println("2. Update Personal Information");
        System.out.println("3. View Available Appointment Slots");
        System.out.println("4. Schedule an Appointment");
        System.out.println("5. Reschedule an Appointment");
        System.out.println("6. Cancel an Appointment");
        System.out.println("7. View Scheduled Appointments");
        System.out.println("8. View Past Appointment Outcome Records");
        System.out.println("9. Logout");
        System.out.println("===========================\n");
    }

    public void patientmenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            showPatientMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            System.out.println();
            switch (choice) {
                case 1:
                    viewMedicalRecords();
                    break;
                case 2:
                    updatePersonalInfo();
                    break;
                case 3:
                    viewAvailableAppointments();
                    break;
                case 4:
                    scheduleAppointment();
                    break;
                case 5:
                    rescheduleAppointment();
                    break;
                case 6:
                    cancelAppointment();
                    break;
                case 7:
                    viewScheduledAppointments();
                    break;
                case 8:
                    viewCompletedAppointments();
                    break;
                case 9:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 9);
    }

    public void viewMedicalRecords() {
        String patientListPath = "Patient_List.csv";
        String medicalRecordsPath = "medicalRecords.csv";
        String line;
        boolean recordFound = false;

        // Displaying patient details from Patient_List.csv
        try (BufferedReader patientListReader = new BufferedReader(new FileReader(patientListPath))) {
            System.out.println("Patient Details:");
            System.out.println("----------------------------------------------------");

            while ((line = patientListReader.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(patientID)) {
                    System.out.printf("Patient ID: %s%nName: %s%nDate of Birth: %s%nGender: %s%nBlood Type: %s%nEmail: %s%nContact Number: %s%n",
                        details[0], details[1], details[2], details[3], details[4], details[5], details[6]);
                    System.out.println("\n");
                    recordFound = true;
                    break;
                }
            }
            if (!recordFound) {
                System.out.println("No details found for this patient.");
                return;
            }
        } catch (IOException e) {
            System.out.println("An error occurred while accessing the patient list file.");
            e.printStackTrace();
        }

        // Displaying past diagnoses from medicalRecords.csv
        System.out.println("Past Diagnoses:");
        System.out.println("----------------------------------------------------");

        recordFound = false; // Reset recordFound for the medical records

        try (BufferedReader medicalRecordsReader = new BufferedReader(new FileReader(medicalRecordsPath))) {
            while ((line = medicalRecordsReader.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(patientID)) {
                    System.out.printf("Date of Diagnosis: %s%n", values[1]);
                    System.out.printf("Diagnosis: %s%n", values[2]);
                    System.out.printf("Treatment Plan: %s%n", values[3]);
                    System.out.println("----------------------------------------------------");
                    recordFound = true;
                }
            }
            
            if (!recordFound) {
                System.out.println("No medical records found for this patient.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while accessing the medical records file.");
            e.printStackTrace();
        }
    }


    public void updatePersonalInfo() {
        Scanner scanner = new Scanner(System.in);
    
        System.out.println("Choose the information to update:");
        System.out.println("1. Email Address");
        System.out.println("2. Contact Number");
        System.out.print("Enter your choice (1 or 2): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline
    
        if (choice == 1) {
            System.out.print("Enter new email address: ");
            String newEmail = scanner.nextLine();
            updatePatientDataInFile(newEmail, null); // Update email only
        } else if (choice == 2) {
            System.out.print("Enter new contact number: ");
            String newContactNumber = scanner.nextLine();
            updatePatientDataInFile(null, newContactNumber); // Update contact number only
        } else {
            System.out.println("Invalid choice. Please select either 1 or 2.");
            return;
        }
    
        System.out.println("Personal information updated successfully.");
    }
    
    public void updatePatientDataInFile(String newEmail, String newContactNumber) {
        List<String> lines = new ArrayList<>();
    
        try (BufferedReader reader = new BufferedReader(new FileReader("Patient_List.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].trim().equals(patientID)) {
                    // Update email if newEmail is provided
                    if (newEmail != null) {
                        data[5] = newEmail;
                    }
                    // Update contact number if newContactNumber is provided
                    if (newContactNumber != null) {
                        data[6] = newContactNumber;
                    }
                    line = String.join(",", data);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Patient_List.csv"))) {
            for (String updatedLine : lines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public void viewAvailableAppointments() {
        String doctorListPath = "Doctor_List.csv";
        String appointmentsPath = "availableAppointments.csv";
        List<String> doctorIds = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        // Display the list of doctors
        System.out.println("Available Doctors:");
        System.out.println("----------------------------------------------------");

        try (BufferedReader doctorReader = new BufferedReader(new FileReader(doctorListPath))) {
            String line;
            while ((line = doctorReader.readLine()) != null) {
                doctorReader.readLine();
                String[] details = line.split(",");
                String doctorId = details[0].trim();
                doctorIds.add(doctorId);

                System.out.printf("Doctor ID: %s%nName: %s%nGender: %s%nAge: %s%nSpecialisation: %s%n", 
                                doctorId, details[1], details[3], details[4], details[5]);
                System.out.println("----------------------------------------------------");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while accessing the doctor list.");
            e.printStackTrace();
            return;
        }

        // Select a doctor
        System.out.print("Enter the Doctor ID of the doctor you want to see: ");
        String selectedDoctorId = scanner.nextLine();

        if (!doctorIds.contains(selectedDoctorId)) {
            System.out.println("Invalid Doctor ID. Please try again.");
            return;
        }

        // Select a date
        System.out.print("Enter the date to view available appointments (YYYY-MM-DD): ");
        String selectedDate = scanner.nextLine();

        // Display available appointments for the selected doctor and date
        System.out.printf("Available Appointments for Doctor %s on %s:%n", selectedDoctorId, selectedDate);
        System.out.println("----------------------------------------------------");

        boolean appointmentsFound = false;

        try (BufferedReader appointmentsReader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            while ((line = appointmentsReader.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(selectedDoctorId) && values[1].equals(selectedDate)) {
                    System.out.printf("Time: %s%n", values[2]);
                    System.out.println("------------");
                    appointmentsFound = true;
                }
            }

            if (!appointmentsFound) {
                System.out.println("No available appointments found for this date.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while accessing the appointments list.");
            e.printStackTrace();
        }
    }

    public void scheduleAppointment() {
        String patientID = this.patientID;
        String doctorListPath = "Doctor_List.csv";
        String appointmentsPath = "availableAppointments.csv";
        String appointmentRequestsPath = "appointmentRequests.csv";
        Scanner scanner = new Scanner(System.in);
        List<String> doctorIds = new ArrayList<>();
        List<String> doctorNames = new ArrayList<>();
    
        // Display list of doctors with numbers
        System.out.println("Available Doctors:");
        System.out.println("----------------------------------------------------");
    
        try (BufferedReader doctorReader = new BufferedReader(new FileReader(doctorListPath))) {
            String line;
            int index = 1;
            while ((line = doctorReader.readLine()) != null) {
                doctorReader.readLine();
                String[] details = line.split(",");
                doctorIds.add(details[0].trim());
                doctorNames.add(details[1].trim());
    
                System.out.printf("%d. %s (Specialisation: %s)%n", index, details[1], details[5]);
                index++;
            }
        } catch (IOException e) {
            System.out.println("An error occurred while accessing the doctor list.");
            e.printStackTrace();
            return;
        }
    
        // Selecting a doctor
        System.out.print("Choose a doctor by entering the corresponding number: ");
        int doctorChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        if (doctorChoice < 1 || doctorChoice > doctorIds.size()) {
            System.out.println("Invalid choice. Please try again.");
            return;
        }
        String selectedDoctorId = doctorIds.get(doctorChoice - 1);
    
        boolean appointmentsFound = false;
        List<String> availableTimes = new ArrayList<>();
        String selectedDate = "";  // Declaring selectedDate outside the loop
    
        // Loop for selecting a date until appointments are found or the patient decides to exit
        while (!appointmentsFound) {
            System.out.print("Enter the date to view available appointments (YYYY-MM-DD) or type 'exit' to cancel: ");
            selectedDate = scanner.nextLine();  // Assigning selectedDate here
    
            if (selectedDate.equalsIgnoreCase("exit")) {
                System.out.println("Appointment scheduling cancelled.");
                return;
            }
    
            System.out.printf("Available Appointments for %s on %s:%n", doctorNames.get(doctorChoice - 1), selectedDate);
            System.out.println("----------------------------------------------------");
    
            availableTimes.clear();
    
            try (BufferedReader appointmentsReader = new BufferedReader(new FileReader(appointmentsPath))) {
                String line;
                while ((line = appointmentsReader.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values[0].equals(selectedDoctorId) && values[1].equals(selectedDate)) {
                        availableTimes.add(values[2].trim());
                        System.out.printf("%d. %s%n", availableTimes.size(), values[2].trim());
                        appointmentsFound = true;
                    }
                }
    
                if (!appointmentsFound) {
                    System.out.println("No available appointments found for this date. Please try a different date.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred while accessing the appointments list.");
                e.printStackTrace();
                return;
            }
        }
    
        // Selecting an appointment time
        System.out.print("Choose an appointment time by entering the corresponding number: ");
        int timeChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        if (timeChoice < 1 || timeChoice > availableTimes.size()) {
            System.out.println("Invalid choice. Please try again.");
            return;
        }
        String selectedTime = availableTimes.get(timeChoice - 1);
    
        // Generate a random appointment ID
        Random random = new Random();
        int appointmentID = 1000 + random.nextInt(9000);
    
        // Write the new appointment to appointmentRequests.csv
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appointmentRequestsPath, true))) {
            writer.write(String.format("%d,%s,%s,%s,%s,PENDING CONFIRMATION,N/A,N/A,N/A%n", appointmentID, selectedDoctorId, patientID, selectedDate, selectedTime));
            System.out.println("Appointment scheduled successfully with Appointment ID: " + appointmentID);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the appointment requests file.");
            e.printStackTrace();
        }
    
        // Remove the scheduled appointment from availableAppointments.csv
        List<String> updatedAppointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                // Only keep lines that are not the selected appointment
                if (!(values[0].equals(selectedDoctorId) && values[1].equals(selectedDate) && values[2].equals(selectedTime))) {
                    updatedAppointments.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading from the available appointments file.");
            e.printStackTrace();
            return;
        }
    
        // Rewrite the availableAppointments.csv file without the scheduled appointment
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appointmentsPath))) {
            for (String updatedLine : updatedAppointments) {
                writer.write(updatedLine);
                writer.newLine();
            }
            System.out.println("The selected appointment has been removed from the available appointments.");
        } catch (IOException e) {
            System.out.println("An error occurred while updating the available appointments file.");
            e.printStackTrace();
        }
    }
    

    public void rescheduleAppointment() {
        String patientID = this.patientID;
        String appointmentRequestsPath = "appointmentRequests.csv";
        String appointmentsPath = "availableAppointments.csv";
        Scanner scanner = new Scanner(System.in);
    
        // Step 1: Display existing appointments for this patient
        List<String[]> patientAppointments = new ArrayList<>();
        System.out.println("Your Scheduled Appointments:");
        System.out.println("----------------------------------------------------");
    
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentRequestsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details[2].equals(patientID) && (details[5].equalsIgnoreCase("PENDING CONFIRMATION") || details[5].equalsIgnoreCase("CONFIRMED"))) {
                    patientAppointments.add(details);
                    System.out.printf("ID: %s | Doctor ID: %s | Date: %s | Time: %s%n", details[0], details[1], details[3], details[4]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while accessing the appointment requests.");
            e.printStackTrace();
            return;
        }
    
        if (patientAppointments.isEmpty()) {
            System.out.println("You have no appointments to reschedule.");
            return;
        }
    
        // Step 2: Select an appointment to reschedule
        System.out.print("Enter the Appointment ID to reschedule: ");
        String appointmentID = scanner.nextLine();
        String[] appointmentToReschedule = null;
    
        for (String[] appointment : patientAppointments) {
            if (appointment[0].equals(appointmentID)) {
                appointmentToReschedule = appointment;
                break;
            }
        }
    
        if (appointmentToReschedule == null) {
            System.out.println("Invalid Appointment ID.");
            return;
        }
    
        String doctorId = appointmentToReschedule[1];
        String oldDate = appointmentToReschedule[3];
        String oldTime = appointmentToReschedule[4];
    
        // Step 3: Enter a new date and display available times
        List<String> availableTimes = new ArrayList<>();
        boolean appointmentsFound = false;
        String newDate = "";
    
        while (!appointmentsFound) {
            System.out.print("Enter the new date to view available appointments (YYYY-MM-DD) or type 'exit' to cancel: ");
            newDate = scanner.nextLine();
    
            if (newDate.equalsIgnoreCase("exit")) {
                System.out.println("Rescheduling cancelled.");
                return;
            }
    
            System.out.printf("Available Appointments for Doctor %s on %s:%n", doctorId, newDate);
            System.out.println("----------------------------------------------------");
    
            availableTimes.clear();
    
            try (BufferedReader appointmentsReader = new BufferedReader(new FileReader(appointmentsPath))) {
                String line;
                while ((line = appointmentsReader.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values[0].equals(doctorId) && values[1].equals(newDate)) {
                        availableTimes.add(values[2].trim());
                        System.out.printf("%d. %s%n", availableTimes.size(), values[2].trim());
                        appointmentsFound = true;
                    }
                }
    
                if (!appointmentsFound) {
                    System.out.println("No available appointments found for this date. Please try a different date.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred while accessing the appointments list.");
                e.printStackTrace();
                return;
            }
        }
    
        // Step 4: Select a new time slot
        System.out.print("Choose a new appointment time by entering the corresponding number: ");
        int timeChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        if (timeChoice < 1 || timeChoice > availableTimes.size()) {
            System.out.println("Invalid choice. Please try again.");
            return;
        }
        String newTime = availableTimes.get(timeChoice - 1);
    
        // Step 5: Update appointmentRequests.csv with the new date and time
        List<String> updatedRequests = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentRequestsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(appointmentID)) {
                    values[3] = newDate;
                    values[4] = newTime;
                    values[5] = "PENDING CONFIRMATION";
                    line = String.join(",", values);
                }
                updatedRequests.add(line);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while updating the appointment request.");
            e.printStackTrace();
            return;
        }
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appointmentRequestsPath))) {
            for (String updatedLine : updatedRequests) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the appointment requests file.");
            e.printStackTrace();
        }
    
        // Step 6: Update availableAppointments.csv
        List<String> updatedAppointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                // Only keep lines that are not the new appointment slot
                if (!(values[0].equals(doctorId) && values[1].equals(newDate) && values[2].equals(newTime))) {
                    updatedAppointments.add(line);
                }
            }
            // Add the old slot back to available appointments
            updatedAppointments.add(String.format("%s,%s,%s", doctorId, oldDate, oldTime));
        } catch (IOException e) {
            System.out.println("An error occurred while updating the available appointments.");
            e.printStackTrace();
            return;
        }
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appointmentsPath))) {
            for (String updatedLine : updatedAppointments) {
                writer.write(updatedLine);
                writer.newLine();
            }
            System.out.println("Rescheduling complete. The old slot has been added back to available appointments.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the available appointments file.");
            e.printStackTrace();
        }
    }    

    public void cancelAppointment() {
        String patientID = this.patientID;
        String appointmentRequestsPath = "appointmentRequests.csv";
        String appointmentsPath = "availableAppointments.csv";
        Scanner scanner = new Scanner(System.in);
    
        // Step 1: Display existing appointments for this patient
        List<String[]> patientAppointments = new ArrayList<>();
        System.out.println("Your Scheduled Appointments:");
        System.out.println("----------------------------------------------------");
    
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentRequestsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details[2].equals(patientID) && (details[5].equalsIgnoreCase("PENDING CONFIRMATION") || details[5].equalsIgnoreCase("CONFIRMED"))) {
                    patientAppointments.add(details);
                    System.out.printf("ID: %s | Doctor ID: %s | Date: %s | Time: %s%n", details[0], details[1], details[3], details[4]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while accessing the appointment requests.");
            e.printStackTrace();
            return;
        }
    
        if (patientAppointments.isEmpty()) {
            System.out.println("You have no appointments to cancel.");
            return;
        }
    
        // Step 2: Select an appointment to cancel
        System.out.print("Enter the Appointment ID to cancel: ");
        String appointmentID = scanner.nextLine();
        String[] appointmentToCancel = null;
    
        for (String[] appointment : patientAppointments) {
            if (appointment[0].equals(appointmentID)) {
                appointmentToCancel = appointment;
                break;
            }
        }
    
        if (appointmentToCancel == null) {
            System.out.println("Invalid Appointment ID.");
            return;
        }
    
        String doctorId = appointmentToCancel[1];
        String appointmentDate = appointmentToCancel[3];
        String appointmentTime = appointmentToCancel[4];
    
        // Step 3: Remove the appointment from appointmentRequests.csv
        List<String> updatedRequests = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentRequestsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (!values[0].equals(appointmentID)) {
                    updatedRequests.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while updating the appointment request.");
            e.printStackTrace();
            return;
        }
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appointmentRequestsPath))) {
            for (String updatedLine : updatedRequests) {
                writer.write(updatedLine);
                writer.newLine();
            }
            System.out.println("Appointment canceled successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the appointment requests file.");
            e.printStackTrace();
        }
    
        // Step 4: Add the canceled slot back to availableAppointments.csv
        List<String> updatedAppointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                updatedAppointments.add(line);
            }
            // Add the canceled appointment slot back to available appointments
            updatedAppointments.add(String.format("%s,%s,%s", doctorId, appointmentDate, appointmentTime));
        } catch (IOException e) {
            System.out.println("An error occurred while reading from the available appointments file.");
            e.printStackTrace();
            return;
        }
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appointmentsPath))) {
            for (String updatedLine : updatedAppointments) {
                writer.write(updatedLine);
                writer.newLine();
            }
            System.out.println("The canceled appointment slot has been added back to available appointments.");
        } catch (IOException e) {
            System.out.println("An error occurred while updating the available appointments file.");
            e.printStackTrace();
        }
    }
    
    public void viewScheduledAppointments() {
        String patientID = this.patientID;
        String appointmentRequestsPath = "appointmentRequests.csv";
        String doctorListPath = "Doctor_List.csv";
    
        // Load doctor names from Doctor_List.csv
        Map<String, String> doctorNames = new HashMap<>();
        try (BufferedReader doctorReader = new BufferedReader(new FileReader(doctorListPath))) {
            String line;
            while ((line = doctorReader.readLine()) != null) {
                String[] details = line.split(",");
                String doctorId = details[0].trim();
                String doctorName = details[1].trim();
                doctorNames.put(doctorId, doctorName);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while accessing the doctor list.");
            e.printStackTrace();
            return;
        }
    
        // Display scheduled appointments for the patient
        System.out.println("Your Scheduled Appointments:");
        System.out.println("----------------------------------------------------");
        System.out.println("ID  | Doctor Name       | Date       | Time   | Status");
    
        boolean hasAppointments = false;
    
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentRequestsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details[2].equals(patientID)) {  // Check if the appointment belongs to the current patient
                    String status = details[5].trim();
                    if (status.equalsIgnoreCase("PENDING CONFIRMATION") || status.equalsIgnoreCase("CONFIRMED") || status.equalsIgnoreCase("CANCELLED")) {
                        String appointmentID = details[0];
                        String doctorID = details[1];
                        String appointmentDate = details[3];
                        String appointmentTime = details[4];
                        String doctorName = doctorNames.getOrDefault(doctorID, "Unknown Doctor");
    
                        System.out.printf("%-4s | %-16s | %-10s | %-6s | %-10s%n", appointmentID, doctorName, appointmentDate, appointmentTime, status);
                        hasAppointments = true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while accessing the appointment requests.");
            e.printStackTrace();
            return;
        }
    
        if (!hasAppointments) {
            System.out.println("You have no scheduled appointments with the specified statuses.");
        }
    }

    public void viewCompletedAppointments() {
        System.out.println("Completed Appointments: ");
        System.out.println("----------------------------------------------------");
        boolean found = false;
    
        try (BufferedReader br = new BufferedReader(new FileReader("appointmentRequests.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                
                // Check if the line has at least the required number of fields to avoid index errors
                if (fields.length < 6) {
                    continue;  // Skip lines with insufficient data
                }
    
                // Check if the appointment is for this patient and has a COMPLETED status
                if (fields[2].equals(patientID) && fields[5].equalsIgnoreCase("COMPLETED")) {
                    System.out.println("Appointment ID: " + fields[0]);
                    System.out.println("Date: " + fields[3]);
                    System.out.println("Time: " + fields[4]);
                    System.out.println("Type of Service: " + fields[6]);
                    System.out.println("Consultation Notes: " + fields[7]);
                    System.out.println("Medications: " + fields[8]);
                    System.out.println("Quantity: " + fields[9]);
                    System.out.println("Medication Status: " + fields[10]);
                    System.out.println("----------------------------------------------------");
                    found = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading appointments.");
            e.printStackTrace();
        }
    
        if (!found) {
            System.out.println("No completed appointments found.");
        }
    }
}