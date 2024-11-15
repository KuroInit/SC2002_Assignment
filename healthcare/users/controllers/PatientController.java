package healthcare.users.controllers;

import healthcare.records.*;
import healthcare.users.models.*;
import healthcare.users.view.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PatientController {
    private PatientModel patientModel;
    private PatientView patientView;

    public PatientController(PatientModel patientModel, PatientView patientView) {
        this.patientModel = patientModel;
        this.patientView = patientView;
    }

    public void showPatientMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            Screen.clearConsole();
            patientView.displayPatientMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            System.out.println();
            handleMenuChoice(choice, scanner);
        } while (choice != 10);
    }

    private void handleMenuChoice(int choice, Scanner scanner) {
        switch (choice) {
            case 1:
                viewMedicalRecords();
                break;
            case 2:
                updatePersonalInfo(scanner);
                break;
            case 3:
                viewAvailableAppointments(scanner);
                break;
            case 4:
                scheduleAppointment(scanner);
                break;
            case 5:
                rescheduleAppointment(scanner);
                break;
            case 6:
                cancelAppointment(scanner);
                break;
            case 7:
                viewScheduledAppointments();
                break;
            case 8:
                viewCompletedAppointments();
                break;
            case 9:
                feedback();
                break;
            case 10:
                patientView.displayLogoutMessage();
                break;
            default:
                patientView.displayInvalidChoiceMessage();
        }
    }

    private void feedback() {
        Scanner s = new Scanner(System.in);
        Feedback.collectFeedback(s);
    }

    private void viewMedicalRecords() {
        String patientListPath = "Patient_List.csv";
        String medicalRecordsPath = "medicalRecords.csv";

        // Display patient details
        try (BufferedReader reader = new BufferedReader(new FileReader(patientListPath))) {
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equals(patientModel.getPatientID())) {
                    patientView.displayPatientDetails(new PatientModel(
                            details[0], details[1], details[2], details[3], details[4], details[5], details[6]));
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("===========================================");
                System.out.println("   No details found for this patient.      ");
                System.out.println("===========================================");
                System.out.println("\nPress Enter to continue...");
                new Scanner(System.in).nextLine(); // Wait for Enter
                return;
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while accessing the patient list.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine(); // Wait for Enter
            return;
        }

        // Display medical records
        try (BufferedReader reader = new BufferedReader(new FileReader(medicalRecordsPath))) {
            String line;
            boolean hasRecords = false;

            System.out.println("===========================================");
            System.out.println("              Medical Records              ");
            System.out.println("===========================================");
            while ((line = reader.readLine()) != null) {
                String[] record = line.split(",");
                if (record[0].equals(patientModel.getPatientID())) {
                    System.out.printf(
                            "Date of Diagnosis:      %s%n" +
                                    "Diagnosis:              %s%n" +
                                    "Treatment Plan:         %s%n" +
                                    "Prescribed Medicine:    %s%n",
                            record[1], record[2], record[3], record[4]);
                    System.out.println("-------------------------------------------");
                    hasRecords = true;
                }
            }

            if (!hasRecords) {
                System.out.println("===========================================");
                System.out.println("   No medical records found for this patient.");
                System.out.println("===========================================");
            }

            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine(); // Wait for Enter
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while accessing medical records.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine(); // Wait for Enter
        }
    }

    private void updatePersonalInfo(Scanner scanner) {
        patientView.displayUpdateInfoOptions();
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                patientView.promptForEmail();
                String newEmail = scanner.nextLine();
                patientModel.setEmail(newEmail);
                updatePatientFile(newEmail, null);
                break;
            case 2:
                patientView.promptForContactNumber();
                String newContactNumber = scanner.nextLine();
                patientModel.setContactNumber(newContactNumber);
                updatePatientFile(null, newContactNumber);
                break;
            default:
                patientView.displayInvalidChoiceMessage();
        }
    }

    private void updatePatientFile(String newEmail, String newContactNumber) {
        String patientListPath = "Patient_List.csv";
        List<String> lines = new ArrayList<>();
    
        try (BufferedReader reader = new BufferedReader(new FileReader(patientListPath))) {
            String line;
            boolean isHeader = true; // Flag to handle header line separately
    
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
    
                // Add header line as-is
                if (isHeader) {
                    lines.add(line); // Add header to the list
                    isHeader = false;
                    continue;
                }
    
                // Update patient data if the ID matches
                if (data[0].equals(patientModel.getPatientID())) {
                    if (newEmail != null) {
                        data[5] = newEmail;
                    }
                    if (newContactNumber != null) {
                        data[6] = newContactNumber;
                    }
                    line = String.join(",", data); // Join updated fields into a line
                }
                lines.add(line); // Add each line (updated or not) to the list
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while updating personal information.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine(); // Wait for Enter
            return;
        }
    
        // Write all lines back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(patientListPath))) {
            for (String updatedLine : lines) {
                writer.write(updatedLine);
                writer.newLine();
            }
            System.out.println("===========================================");
            System.out.println("  Personal information updated successfully.");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine(); // Wait for Enter
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while writing updated information.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine(); // Wait for Enter
        }
    }
    

    private void viewAvailableAppointments(Scanner scanner) {
        String doctorListPath = "Doctor_List.csv";
        String appointmentsPath = "availableAppointments.csv";
        Map<String, String> doctors = new HashMap<>();

        // Step 1: Display list of doctors
        System.out.println("===========================================");
        System.out.println("            Available Doctors             ");
        System.out.println("===========================================");
        try (BufferedReader reader = new BufferedReader(new FileReader(doctorListPath))) {
            String line;
            line = reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length >= 6) {
                    String doctorId = details[0].trim();
                    String doctorName = details[1].trim();
                    String specialization = details[5].trim();
                    doctors.put(doctorId, doctorName);
                    System.out.printf("Doctor ID: %s%nName: %s%nSpecialization: %s%n", doctorId, doctorName,
                            specialization);
                    System.out.println("-------------------------------------------");
                }
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while accessing the doctor list.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        // Step 2: User selects a doctor
        System.out.print("Enter the Doctor ID of the doctor you want to see: ");
        String selectedDoctorId = scanner.nextLine().trim();

        if (!doctors.containsKey(selectedDoctorId)) {
            System.out.println("===========================================");
            System.out.println("           Invalid Doctor ID.             ");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        // Step 3: Display available dates for the selected doctor
        Set<String> availableDates = new TreeSet<>(); // Use TreeSet to sort dates

        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            line = reader.readLine(); // Skip the header line
            while ((line = reader.readLine()) != null) {
                String[] appointment = line.split(",");
                if (appointment.length >= 3) {
                    String doctorId = appointment[0].trim();
                    String date = appointment[1].trim();
                    if (doctorId.equals(selectedDoctorId)) {
                        availableDates.add(date);
                    }
                }
            }

            if (availableDates.isEmpty()) {
                System.out.println("===========================================");
                System.out.println("   No available appointments found for this doctor.");
                System.out.println("===========================================");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine(); // Wait for Enter
                return;
            } else {
                System.out.printf("Available Dates for Dr. %s:%n", doctors.get(selectedDoctorId));
                System.out.println("===========================================");
                int index = 1;
                List<String> dateList = new ArrayList<>(availableDates);
                for (String date : dateList) {
                    System.out.printf("%d. %s%n", index++, date);
                }

                // Step 4: User selects a date
                int dateChoice = 0;
                do {
                    System.out.print("Select a date by entering the corresponding number: ");
                    String input = scanner.nextLine();
                    try {
                        dateChoice = Integer.parseInt(input);
                        if (dateChoice < 1 || dateChoice > dateList.size()) {
                            System.out.println("Invalid choice. Please select a valid number.");
                            dateChoice = 0;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
                    }
                } while (dateChoice == 0);

                String selectedDate = dateList.get(dateChoice - 1);

                // Step 5: Display available time slots for the selected date
                System.out.printf("Available Time Slots for Dr. %s on %s:%n", doctors.get(selectedDoctorId),
                        selectedDate);
                System.out.println("===========================================");

                boolean appointmentsFound = false;
                List<String> times = new ArrayList<>();

                // Reset the reader to read the appointments file again
                try (BufferedReader timeReader = new BufferedReader(new FileReader(appointmentsPath))) {
                    String timeLine;
                    timeLine = timeReader.readLine(); // Skip header line
                    int timeIndex = 1;
                    while ((timeLine = timeReader.readLine()) != null) {
                        String[] appointment = timeLine.split(",");
                        if (appointment.length >= 3) {
                            String doctorId = appointment[0].trim();
                            String date = appointment[1].trim();
                            String time = appointment[2].trim();
                            if (doctorId.equals(selectedDoctorId) && date.equals(selectedDate)) {
                                System.out.printf("%d. Time: %s%n", timeIndex++, time);
                                times.add(time);
                                appointmentsFound = true;
                            }
                        }
                    }

                    if (!appointmentsFound) {
                        System.out.println("===========================================");
                        System.out.println("   No available time slots found for this date.");
                        System.out.println("===========================================");
                    }
                } catch (IOException e) {
                    System.out.println("===========================================");
                    System.out.println("An error occurred while accessing available appointments.");
                    System.out.println("===========================================");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while accessing available appointments.");
            System.out.println("===========================================");
            e.printStackTrace();
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine(); // Wait for Enter
    }

    private void scheduleAppointment(Scanner scanner) {
        String doctorListPath = "Doctor_List.csv";
        String appointmentsPath = "availableAppointments.csv";
        String appointmentRequestsPath = "appointmentRequests.csv";
        Map<String, String> doctors = new HashMap<>();

        // Step 1: Display list of doctors
        System.out.println("===========================================");
        System.out.println("            Available Doctors             ");
        System.out.println("===========================================");
        try (BufferedReader reader = new BufferedReader(new FileReader(doctorListPath))) {
            String line;
            line = reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length >= 6) {
                    String doctorId = details[0].trim();
                    String doctorName = details[1].trim();
                    String specialization = details[5].trim();
                    doctors.put(doctorId, doctorName);
                    System.out.printf("Doctor ID: %s%nName: %s%nSpecialization: %s%n", doctorId, doctorName,
                            specialization);
                    System.out.println("-------------------------------------------");
                }
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while accessing the doctor list.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Step 2: User selects a doctor
        String selectedDoctorId;
        do {
            System.out.print("Enter the Doctor ID you want to schedule an appointment with: ");
            selectedDoctorId = scanner.nextLine().trim();
            if (!doctors.containsKey(selectedDoctorId)) {
                System.out.println("Invalid Doctor ID. Please try again.");
            }
        } while (!doctors.containsKey(selectedDoctorId));

        // Step 3: List available dates for the selected doctor
        Set<String> availableDates = new TreeSet<>(); // To store unique dates
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            line = reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] appointment = line.split(",");
                if (appointment.length >= 3) {
                    String doctorId = appointment[0].trim();
                    String date = appointment[1].trim();
                    if (doctorId.equals(selectedDoctorId)) {
                        availableDates.add(date);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while accessing available appointments.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        if (availableDates.isEmpty()) {
            System.out.println("===========================================");
            System.out.println("No available appointments found for this doctor.");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.printf("Available Dates for Dr. %s:%n", doctors.get(selectedDoctorId));
        System.out.println("===========================================");
        int index = 1;
        List<String> dateList = new ArrayList<>(availableDates);
        for (String date : dateList) {
            System.out.printf("%d. %s%n", index++, date);
        }

        // Step 4: User selects a date
        int dateChoice = 0;
        do {
            System.out.print("Select a date by entering the corresponding number: ");
            String input = scanner.nextLine();
            try {
                dateChoice = Integer.parseInt(input);
                if (dateChoice < 1 || dateChoice > dateList.size()) {
                    System.out.println("Invalid choice. Please select a valid number.");
                    dateChoice = 0;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        } while (dateChoice == 0);

        String selectedDate = dateList.get(dateChoice - 1);

        // Step 5: List available time slots for the selected date
        List<String> availableTimes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            line = reader.readLine(); // Skip header line
            int timeIndex = 1;
            while ((line = reader.readLine()) != null) {
                String[] appointment = line.split(",");
                if (appointment.length >= 3) {
                    String doctorId = appointment[0].trim();
                    String date = appointment[1].trim();
                    String time = appointment[2].trim();
                    if (doctorId.equals(selectedDoctorId) && date.equals(selectedDate)) {
                        System.out.printf("%d. Time: %s%n", timeIndex++, time);
                        availableTimes.add(time);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while accessing available appointments.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        if (availableTimes.isEmpty()) {
            System.out.println("===========================================");
            System.out.println("No available time slots found for this date.");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Step 6: User selects a time slot
        int timeChoice = 0;
        do {
            System.out.print("Enter the number corresponding to the desired time slot: ");
            String input = scanner.nextLine();
            try {
                timeChoice = Integer.parseInt(input);
                if (timeChoice < 1 || timeChoice > availableTimes.size()) {
                    System.out.println("Invalid choice. Please select a valid number.");
                    timeChoice = 0;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        } while (timeChoice == 0);

        String selectedTime = availableTimes.get(timeChoice - 1);

        // Step 7: Schedule the appointment
        // Create the appointment date and time
        String dateTimeStr = selectedDate + " " + selectedTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime appointmentDateTime = LocalDateTime.parse(dateTimeStr, formatter);
        Date appointmentDate = Date.from(appointmentDateTime.atZone(ZoneId.systemDefault()).toInstant());

        // Create the Appointment object
        Appointment appointment = new Appointment(appointmentDate, null, patientModel.getPatientID(), selectedDoctorId);

        // Generate a unique appointment ID
        String appointmentID = appointment.getAppointmentID();

        // Add the appointment to appointmentRequests.csv
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appointmentRequestsPath, true))) {
            writer.write(String.format("%s,%s,%s,%s,%s,%s,N/A,N/A,N/A,N/A,N/A%n",
                    appointmentID,
                    appointment.getDoctorID(),
                    appointment.getPatientID(),
                    appointment.getAppointmentDate().toLocalDate().toString(),
                    appointment.getAppointmentDate().toLocalTime().toString(),
                    appointment.getAppointmentStatus().toString()));
            System.out.println("===========================================");
            System.out.println("Appointment scheduled successfully!");
            System.out.println("Appointment ID: " + appointmentID);
            System.out.println("===========================================");
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while scheduling the appointment.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Step 8: Remove the selected time slot from availableAppointments.csv
        List<String> updatedAppointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            String header = reader.readLine(); // Read and store header line
            updatedAppointments.add(header); // Add header to updated list
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String doctorId = parts[0].trim();
                    String date = parts[1].trim();
                    String time = parts[2].trim();
                    if (!(doctorId.equals(selectedDoctorId) && date.equals(selectedDate)
                            && time.equals(selectedTime))) {
                        updatedAppointments.add(line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while updating available appointments.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appointmentsPath))) {
            for (String updatedLine : updatedAppointments) {
                writer.write(updatedLine);
                writer.newLine();
            }
            System.out.println("===========================================");
            System.out.println("Selected time slot removed from available appointments.");
            System.out.println("===========================================");
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while writing updated appointments.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void rescheduleAppointment(Scanner scanner) {
        String appointmentRequestsPath = "appointmentRequests.csv";
        String appointmentsPath = "availableAppointments.csv";

        // Step 1: Display existing appointments for this patient
        List<String[]> patientAppointments = new ArrayList<>();
        System.out.println("===========================================");
        System.out.println("        Your Scheduled Appointments        ");
        System.out.println("===========================================");

        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentRequestsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details[2].equals(patientModel.getPatientID())
                        && (details[5].equalsIgnoreCase("PENDING")
                                || details[5].equalsIgnoreCase("CONFIRMED"))) {
                    patientAppointments.add(details);
                    System.out.printf("ID: %s | Doctor ID: %s | Date: %s | Time: %s%n", details[0], details[1],
                            details[3], details[4]);
                }
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while accessing the appointment requests.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        if (patientAppointments.isEmpty()) {
            System.out.println("===========================================");
            System.out.println("     You have no appointments to reschedule.");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
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
            System.out.println("===========================================");
            System.out.println("           Invalid Appointment ID.         ");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        String doctorId = appointmentToReschedule[1];
        String oldDate = appointmentToReschedule[3];
        String oldTime = appointmentToReschedule[4];

        // Step 3: Show available slots that the user can swap to
        List<String[]> availableSlots = new ArrayList<>();

        System.out.printf("Available Slots for Doctor %s:%n", doctorId);
        System.out.println("----------------------------------------------------");

        try (BufferedReader appointmentsReader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            while ((line = appointmentsReader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 3 && values[0].equals(doctorId)) {
                    // Exclude the current appointment slot if it's in the available appointments
                    if (!(values[1].equals(oldDate) && values[2].equals(oldTime))) {
                        availableSlots.add(values);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while accessing the appointments list.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        if (availableSlots.isEmpty()) {
            System.out.println("===========================================");
            System.out.println("No available slots found for this doctor.");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Display the available slots
        int index = 1;
        for (String[] slot : availableSlots) {
            String date = slot[1];
            String time = slot[2];
            System.out.printf("%d. Date: %s | Time: %s%n", index++, date, time);
        }

        // Step 4: Select a new slot
        int slotChoice = 0;
        do {
            System.out.print("Choose a new slot by entering the corresponding number: ");
            String input = scanner.nextLine();
            try {
                slotChoice = Integer.parseInt(input);
                if (slotChoice < 1 || slotChoice > availableSlots.size()) {
                    System.out.println("Invalid choice. Please select a valid number.");
                    slotChoice = 0;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        } while (slotChoice == 0);

        String[] selectedSlot = availableSlots.get(slotChoice - 1);
        String newDate = selectedSlot[1];
        String newTime = selectedSlot[2];

        // Step 5: Update appointmentRequests.csv with the new date and time
        List<String> updatedRequests = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentRequestsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(appointmentID)) {
                    values[3] = newDate;
                    values[4] = newTime;
                    values[5] = "PENDING";
                    line = String.join(",", values);
                }
                updatedRequests.add(line);
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while updating the appointment request.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appointmentRequestsPath))) {
            for (String updatedLine : updatedRequests) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while writing to the appointment requests file.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        // Step 6: Update availableAppointments.csv
        List<String> updatedAppointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                // Remove the selected slot
                if (!(values[0].equals(doctorId) && values[1].equals(newDate) && values[2].equals(newTime))) {
                    updatedAppointments.add(line);
                }
            }
            // Add the old slot back to available appointments
            updatedAppointments.add(String.format("%s,%s,%s", doctorId, oldDate, oldTime));
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while updating the available appointments.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appointmentsPath))) {
            for (String updatedLine : updatedAppointments) {
                writer.write(updatedLine);
                writer.newLine();
            }
            System.out.println("===========================================");
            System.out.println("Rescheduling complete. The old slot has been added back to available appointments.");
            System.out.println("===========================================");
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while writing to the available appointments file.");
            System.out.println("===========================================");
            e.printStackTrace();
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine(); // Wait for Enter
    }

    private void cancelAppointment(Scanner scanner) {
        String appointmentRequestsPath = "appointmentRequests.csv";
        String appointmentsPath = "availableAppointments.csv";

        // Fetch existing appointments
        List<String[]> patientAppointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentRequestsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] appointment = line.split(",");
                if (appointment[2].equals(patientModel.getPatientID()) &&
                        (appointment[5].equalsIgnoreCase("PENDING") ||
                                appointment[5].equalsIgnoreCase("CONFIRMED"))) {
                    patientAppointments.add(appointment);
                }
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while fetching appointments.");
            e.printStackTrace();
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        if (patientAppointments.isEmpty()) {
            System.out.println("===========================================");
            System.out.println("You have no appointments to cancel.");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        // Display appointments
        System.out.println("===========================================");
        System.out.println("            Your Appointments             ");
        System.out.println("===========================================");
        for (int i = 0; i < patientAppointments.size(); i++) {
            String[] appointment = patientAppointments.get(i);
            System.out.printf("%d. Appointment ID: %s, Doctor ID: %s, Date: %s, Time: %s%n",
                    i + 1, appointment[0], appointment[1], appointment[3], appointment[4]);
        }
        System.out.println("===========================================");

        // Select an appointment to cancel
        System.out.print("Enter the number corresponding to the appointment you want to cancel: ");
        int appointmentChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (appointmentChoice < 1 || appointmentChoice > patientAppointments.size()) {
            System.out.println("===========================================");
            System.out.println("Invalid choice. Please try again.");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        String[] appointmentToCancel = patientAppointments.get(appointmentChoice - 1);

        // Add the slot back to availableAppointments.csv
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appointmentsPath, true))) {
            writer.write(String.join(",", appointmentToCancel[1], appointmentToCancel[3], appointmentToCancel[4]));
            writer.newLine();
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while updating available appointments.");
            e.printStackTrace();
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        // Remove the appointment from appointmentRequests.csv
        List<String> updatedAppointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentRequestsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(appointmentToCancel[0] + ",")) {
                    updatedAppointments.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while updating appointment requests.");
            e.printStackTrace();
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appointmentRequestsPath))) {
            for (String updatedLine : updatedAppointments) {
                writer.write(updatedLine);
                writer.newLine();
            }
            System.out.println("===========================================");
            System.out.println("Appointment canceled successfully.");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while writing updated appointments.");
            e.printStackTrace();
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
        }
    }

    private void viewScheduledAppointments() {
        String appointmentRequestsPath = "appointmentRequests.csv";
        String doctorListPath = "Doctor_List.csv";
        Map<String, String> doctorNames = new HashMap<>();

        // Load doctor names
        try (BufferedReader reader = new BufferedReader(new FileReader(doctorListPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] doctor = line.split(",");
                doctorNames.put(doctor[0], doctor[1]);
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while accessing the doctor list.");
            e.printStackTrace();
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine(); // Wait for Enter
            return;
        }

        // Fetch and display appointments
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentRequestsPath))) {
            String line;
            boolean hasAppointments = false;

            System.out.println("===========================================");
            System.out.println("         Your Scheduled Appointments       ");
            System.out.println("===========================================");
            while ((line = reader.readLine()) != null) {
                String[] appointment = line.split(",");
                if (appointment[2].equals(patientModel.getPatientID())
                        && (appointment[5].equalsIgnoreCase("CONFIRMED") || appointment[5].equalsIgnoreCase("PENDING"))) {
                    String doctorName = doctorNames.getOrDefault(appointment[1], "Unknown Doctor");
                    System.out.printf("Appointment ID: %s, Doctor: %s, Date: %s, Time: %s, Status: %s%n",
                            appointment[0], doctorName, appointment[3], appointment[4], appointment[5]);
                    hasAppointments = true;
                }
            }

            if (!hasAppointments) {
                System.out.println("===========================================");
                System.out.println("You have no scheduled appointments.");
                System.out.println("===========================================");
            }
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine(); // Wait for Enter
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while fetching appointments.");
            e.printStackTrace();
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine(); // Wait for Enter
        }
    }

    private void viewCompletedAppointments() {
        String appointmentRequestsPath = "appointmentRequests.csv";

        // Fetch and display completed appointments
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentRequestsPath))) {
            String line;
            boolean hasCompletedAppointments = false;

            System.out.println("===========================================");
            System.out.println("           Completed Appointments          ");
            System.out.println("===========================================");
            while ((line = reader.readLine()) != null) {
                String[] appointment = line.split(",");
                if (appointment[2].equals(patientModel.getPatientID())
                        && appointment[5].equalsIgnoreCase("COMPLETED")) {
                    System.out.printf(
                            "| Appointment ID:         %s%n" +
                                    "| Date:                   %s%n" +
                                    "| Time:                   %s%n" +
                                    "| Type of Service:        %s%n" +
                                    "| Consultation Notes:     %s%n" +
                                    "| Medications:            %s%n" +
                                    "| Quantity:               %s%n" +
                                    "| Medication Status:      %s%n",
                            appointment[0], appointment[3], appointment[4], appointment[6], appointment[7],
                            appointment[8], appointment[9], appointment[10]);
                    System.out.println("----------------------------------------------------");
                    hasCompletedAppointments = true;
                }
            }

            if (!hasCompletedAppointments) {
                System.out.println("===========================================");
                System.out.println("   You have no completed appointments.     ");
                System.out.println("===========================================");
            }
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine(); // Wait for Enter

        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while fetching completed appointments.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine(); // Wait for Enter
        }
    }

}
