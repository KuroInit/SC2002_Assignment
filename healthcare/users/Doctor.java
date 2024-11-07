package healthcare.users;

import healthcare.records.Appointment;
import healthcare.records.Appointment.Medication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Scanner;

public class Doctor {
    private String doctorID;
    private String name;
    private String gender;
    private String age;
    private List<Appointment> appointments;
    private List<LocalDate> availableDates;

    public Doctor(String doctorID, String name, String gender, String age) {
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
        for (Appointment appointment : appointments) {
            System.out.println(appointment.printAppointments());
        }
    }

    public void viewPatientMedicalRecords() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the Patient ID to view medical records: ");
        String patientID = scanner.nextLine();

        System.out.println("Medical Records for Patient ID: " + patientID);
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader("medicalRecords.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                
                // Check if the line has at least 5 fields to avoid index errors
                if (fields.length < 5) {
                    continue;  // Skip lines with insufficient data
                }

                // Check if the record matches the specified patientID
                if (fields[0].equals(patientID)) {
                    System.out.println("Date: " + fields[1]);
                    System.out.println("Diagnoses: " + fields[2]);
                    System.out.println("Treatment Plan: " + fields[3]);
                    System.out.println("Prescribed Medicine: " + fields[4]);
                    System.out.println("---------------");
                    found = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading medical records.");
        }

        if (!found) {
            System.out.println("No medical records found for Patient ID: " + patientID);
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
        System.out.println("Approved Appointments for Dr. " + name + " (ID: " + doctorID + ")");
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader("appointmentRequests.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                
                // Check if the line has at least 5 fields to avoid index errors
                if (fields.length < 5) {
                    continue;  // Skip lines with insufficient data
                }

                // Check if the appointment is for this doctor and has an APPROVED status
                if (fields[1].equals(doctorID) && fields[5].equals("APPROVED")) {
                    System.out.println("AppointmentID: " + fields[0] + ", PatientID: " + fields[2] +
                            ", Date: " + fields[3] + ", Time: " + fields[4] + ", Status: " + fields[5] +
                            ", Type of Service: " + (fields.length > 6 ? fields[6] : "") +
                            ", Medications: " + (fields.length > 7 ? fields[7] : "") +
                            ", Consultation Notes: " + (fields.length > 8 ? fields[8] : ""));
                    found = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading appointments.");
        }

        if (!found) {
            System.out.println("No approved appointments found.");
        }
    }

    public void addAvailability() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the Date for the Appointment Slot (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter the Time for the Appointment Slot (HH:MM): ");
        String time = scanner.nextLine();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("availableAppointments.csv", true))) {
            // Write the new slot in the format: DoctorID, Date, Time
            bw.write(doctorID + "," + date + "," + time + "\n");
            System.out.println("Appointment time slot added successfully.");
        } catch (IOException e) {
            System.out.println("Error saving the appointment time slot.");
        }
    }

    public void viewPendingAppointments() {
        System.out.println("Pending Appointments for Dr. " + name + ":");
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader("appointmentRequests.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[1].equals(doctorID) && fields[5].equals("PENDING")) {  // Matching DoctorID and PENDING status
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
        System.out.print("Enter the Appointment ID to approve or reject: ");
        String appointmentId = scanner.nextLine();
        System.out.print("Enter new status (APPROVED/REJECTED): ");
        String newStatus = scanner.nextLine().toUpperCase();

        if (!newStatus.equals("APPROVED") && !newStatus.equals("REJECTED")) {
            System.out.println("Invalid status. Please enter either APPROVED or REJECTED.");
            return;
        }

        List<String> appointments = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader br = new BufferedReader(new FileReader("appointmentRequests.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(appointmentId) && fields[1].equals(doctorID)) {  // Matching AppointmentID and DoctorID
                    // Update the status field
                    fields[5] = newStatus;  // Assuming status is the 5th field in CSV
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
                System.out.println("Appointment status updated successfully to " + newStatus + ".");
            } catch (IOException e) {
                System.out.println("Error saving updated appointments.");
            }
        } else {
            System.out.println("Appointment not found or invalid Appointment ID provided.");
        }
    }

    // acceptAppointments
    public void acceptAppointment(Appointment appointment) {
        if (availableDates.contains(appointment.getAppointmentDate())) {
            appointment.setStatus(Appointment.AppointmentStatus.APPROVED);
            appointments.add(appointment);
            System.out.println("Appointment approved for: " + appointment.getAppointmentDate());
        } else {
            System.out.println("Appointment date is not available.");
        }
    }

    // decline APpointments
    public void declineAppointment(Appointment appointment) {
        appointment.setStatus(Appointment.AppointmentStatus.REJECTED);
        System.out.println("Appointment declined for: " + appointment.getAppointmentDate());
    }

    // record Appointment Outcomes
    public void recordAppointmentOutcome() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the Appointment ID for which you want to record the outcome: ");
        String appointmentId = scanner.nextLine();

        System.out.print("Enter Type of Service: ");
        String typeOfService = scanner.nextLine();
        System.out.print("Enter Prescribed Medications: ");
        String prescribedMedications = scanner.nextLine();
        System.out.print("Enter Consultation Notes: ");
        String consultationNotes = scanner.nextLine();

        List<String> appointments = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader br = new BufferedReader(new FileReader("appointmentRequests.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(appointmentId) && fields[1].equals(doctorID) && fields[5].equals("APPROVED")) {
                    // Update the status to COMPLETED and add outcome details
                    fields[5] = "COMPLETED";  // Status field
                    fields[6] = typeOfService;  // Type of Service field
                    fields[7] = prescribedMedications;  // Prescribed Medications field
                    fields[8] = consultationNotes;  // Consultation Notes field
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
                System.out.println("Appointment outcome recorded successfully with status COMPLETED.");
            } catch (IOException e) {
                System.out.println("Error saving updated appointments.");
            }
        } else {
            System.out.println("Appointment not found or invalid Appointment ID provided.");
        }
    }

    // show all completed Appointments
    public void viewCompletedAppointments() {
        System.out.println("Completed Appointments for Doctor " + name + ":");
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentStatus() == Appointment.AppointmentStatus.COMPLETED) {
                System.out.println(appointment.toString());
            }
        }
    }

    // function to search by doctorID
    public void queryAppointmentsByDoctorID(String doctorID) {
        File file = new File("database/appointmentRequests.csv");

        if (!file.exists()) {
            System.out.println("The appointment file does not exist.");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(file.getPath()));

            List<String> matchingAppointments = lines.stream()
                    .skip(1)
                    .filter(line -> line.split(",")[1].equals(doctorID))
                    .collect(Collectors.toList());

            if (matchingAppointments.isEmpty()) {
                System.out.println("No appointments found for Doctor ID: " + doctorID);
            } else {
                System.out.println("Appointments for Doctor ID: " + doctorID);
                for (String appointment : matchingAppointments) {
                    System.out.println(appointment);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
    }

    // write Appointment object to CSV
    public void writeAppointmentToCSV(Appointment appointment) {
        File file = new File("database/appointmentRequests.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            // Construct the CSV line from the appointment details
            String csvLine = String.join(",",
                    appointment.getAppointmentID(),
                    appointment.getDoctorID(),
                    appointment.getPatientID(),
                    String.valueOf(appointment.getAppointmentDate()),
                    appointment.getServiceType().toString(),
                    appointment.getTreatmentType().toString(),
                    appointment.getAppointmentStatus().toString(),
                    appointment.getConsultationNotes(),
                    appointment.getPrescribedMedications().stream()
                            .map(Medication::getMedicationName)
                            .collect(Collectors.joining(";")));

            writer.write(csvLine);
            writer.newLine();
            System.out.println("Appointment written to CSV: " + csvLine);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Doctor ID: " + doctorID + "\nName: " + name;
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
                    // View Personal Schedule
                    System.out.println("Doctor " + name + "'s Schedule:");
                    viewSchedule();
                    break;
                case 4:
                    addAvailability();
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
        System.out.println("===== Doctor Menu =====");
        System.out.println("1. View Patient Medical Records");
        System.out.println("2. Update Patient Medical Records");
        System.out.println("3. View Personal Schedule");
        System.out.println("4. Set Availability for Appointments");
        System.out.println("5. Accept or Decline Appointment Requests");
        System.out.println("6. View Upcoming Appointments");
        System.out.println("7. Record Appointment Outcome");
        System.out.println("8. Logout");
        System.out.println("=======================");
    }
}
