package healthcare.users;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import healthcare.records.MedicalRecord;

import java.util.Random;

public class Patient {
    private String patientID;
    private String name;
    private String dob;
    private String gender;
    private String bloodType;
    private String contactInfo;

    private List<Appointment> appointments;
    private List<MedicalRecord> medicalRecords;

    public Patient(String patientID, String name, String dob, String gender, String bloodType, String contactInfo) {
        this.patientID = patientID;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.bloodType = bloodType;
        this.contactInfo = contactInfo;
        this.appointments = loadAppointments();
        this.medicalRecords = loadMedicalRecords();
    }

    public static void showPatientMenu() {
        System.out.println();
        System.out.println("1. View Medical Record");
        System.out.println("2. Update Personal Information");
        System.out.println("3. View Available Appointment Slots");
        System.out.println("4. Schedule an Appointment");
        System.out.println("5. Reschedule an Appointment");
        System.out.println("6. Cancel an Appointment");
        System.out.println("7. View Scheduled Appointments");
        System.out.println("8. View Past Appointment Outcome Records");
        System.out.println("9. Logout");
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
                case 2:
                    updatePersonalInfo();
                case 3:
                    viewAvailableAppointments();
                case 4:
                    scheduleAppointment();
                case 5:
                    rescheduleAppointment();
                case 6:
                    cancelAppointment();
                case 7:
                    viewScheduledAppointments();
                case 8:
                    viewPastAppointmentOutcomeRec();
                case 9:
                    System.out.println("Logging out...");
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 9);
    }

    private void viewMedicalRecords() {
        System.out.println("Medical Record:");
        System.out.println("Patient ID: " + patientID);
        System.out.println("Name: " + name);
        System.out.println("Date of Birth: " + dob);
        System.out.println("Gender: " + gender);
        System.out.println("Blood Type: " + bloodType);
        System.out.println("Contact Information: " + contactInfo);

        for (MedicalRecord record : medicalRecords) {
            System.out.println(record);
        }
    }

    private void updatePersonalInfo() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new contact information: ");
        contactInfo = scanner.nextLine();
        System.out.println("Personal information updated successfully.");

        updatePatientDataInFile();
    }

    private void updatePatientDataInFile() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("patients.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].trim().equals(patientID)) {
                    data[5] = contactInfo;
                    line = String.join(",", data);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("patients.csv"))) {
            for (String updatedLine : lines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void viewAvailableAppointments() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the Doctor's name to view available slots: ");
        String selectedDoctorName = scanner.nextLine();

        Map<String, List<String>> doctorSlots = new HashMap<>();
        doctorSlots.put("John Smith", List.of("2024-10-30 10:00 AM", "2024-10-30 11:00 AM"));
        doctorSlots.put("Emily Clarke", List.of("2024-10-31 09:00 AM", "2024-10-31 10:30 AM"));

        if (doctorSlots.containsKey(selectedDoctorName)) {
            System.out.println("Available slots for Dr. " + selectedDoctorName + ":");
            for (String slot : doctorSlots.get(selectedDoctorName)) {
                System.out.println(" - " + slot);
            }
        } else {
            System.out.println("No available slots for Dr. " + selectedDoctorName + " or invalid doctor name entered.");
        }
    }

    private void scheduleAppointment() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Doctor's name: ");
        String doctorName = scanner.nextLine();

        Map<String, List<String>> doctorSlots = new HashMap<>();
        doctorSlots.put("John Smith", List.of("2024-10-30 10:00 AM", "2024-10-30 11:00 AM"));
        doctorSlots.put("Emily Clarke", List.of("2024-10-31 09:00 AM", "2024-10-31 10:30 AM"));

        if (doctorSlots.containsKey(doctorName)) {
            List<String> availableSlots = doctorSlots.get(doctorName);

            System.out.println("Available slots for Dr. " + doctorName + ":");
            for (int i = 0; i < availableSlots.size(); i++) {
                System.out.println((i + 1) + ". " + availableSlots.get(i));
            }

            System.out.print("Select a slot by entering the slot number: ");
            int slotChoice = scanner.nextInt();
            scanner.nextLine();

            if (slotChoice < 1 || slotChoice > availableSlots.size()) {
                System.out.println("Invalid choice. Appointment not scheduled.");
                return;
            }

            String selectedDateTime = availableSlots.get(slotChoice - 1);

            Appointment appointment = new Appointment(patientID, doctorName, selectedDateTime, "confirmed");
            appointments.add(appointment);
            saveAppointments();

            System.out.println("Appointment scheduled with Dr. " + doctorName + " on " + selectedDateTime);
            System.out.println("Your Appointment ID is: " + appointment.getAppointmentID());
        } else {
            System.out.println("No available slots for Dr. " + doctorName + " or invalid doctor name entered.");
        }
    }

    private void rescheduleAppointment() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Appointment ID to reschedule: ");
        String appointmentID = scanner.nextLine();

        Appointment existingAppointment = null;

        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentID().equals(appointmentID)) {
                existingAppointment = appointment;
                break;
            }
        }

        if (existingAppointment == null) {
            System.out.println("Appointment ID not found.");
            return;
        }

        String doctorName = existingAppointment.getDoctorID();

        Map<String, List<String>> doctorSlots = new HashMap<>();
        doctorSlots.put("John Smith", List.of("2024-10-30 10:00 AM", "2024-10-30 11:00 AM"));
        doctorSlots.put("Emily Clarke", List.of("2024-10-31 09:00 AM", "2024-10-31 10:30 AM"));

        if (doctorSlots.containsKey(doctorName)) {
            List<String> availableSlots = doctorSlots.get(doctorName);

            System.out.println("Available slots for Dr. " + doctorName + ":");
            for (int i = 0; i < availableSlots.size(); i++) {
                System.out.println((i + 1) + ". " + availableSlots.get(i));
            }

            System.out.print("Select a slot by entering the slot number: ");
            int slotChoice = scanner.nextInt();
            scanner.nextLine();

            if (slotChoice < 1 || slotChoice > availableSlots.size()) {
                System.out.println("Invalid choice. Rescheduling not completed.");
                return;
            }

            String selectedDateTime = availableSlots.get(slotChoice - 1);

            existingAppointment.setDateTime(selectedDateTime);

            updateAppointmentInFile(appointmentID, selectedDateTime);

            System.out.println("Appointment rescheduled successfully to " + selectedDateTime);
        } else {
            System.out.println("No available slots for Dr. " + doctorName + " or invalid doctor name entered.");
        }
    }

    private void updateAppointmentInFile(String appointmentID, String newDateTime) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("appointments.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[1].trim().equals(appointmentID)) {
                    data[3] = newDateTime;
                    line = String.join(",", data);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("appointments.csv"))) {
            for (String updatedLine : lines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cancelAppointment() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Appointment ID to cancel: ");
        String appointmentID = scanner.nextLine();

        boolean removed = appointments.removeIf(appointment -> appointment.getAppointmentID().equals(appointmentID));

        if (removed) {
            removeAppointmentFromFile(appointmentID);
            System.out.println("Appointment with ID " + appointmentID + " has been canceled.");
        } else {
            System.out.println("Appointment ID not found.");
        }
    }

    private void removeAppointmentFromFile(String appointmentID) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("appointments.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (!data[1].trim().equals(appointmentID)) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("appointments.csv"))) {
            for (String updatedLine : lines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void viewScheduledAppointments() {
        System.out.println("Your scheduled appointments:");
        for (Appointment appointment : appointments) {
            System.out.println(appointment);
        }
    }

    private void viewPastAppointmentOutcomeRec() {
        System.out.println("Past appointment outcomes:");
        for (MedicalRecord record : medicalRecords) {
            System.out.println(record.getAppointmentOutcome());
        }
    }

    private void savePatientData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("patients.csv", true))) {
            writer.write(patientID + "," + name + "," + dob + "," + gender + "," + bloodType + "," + contactInfo);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Appointment> loadAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("appointments.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length < 5 || !data[0].equals(patientID)) {
                    continue;
                }

                appointments.add(new Appointment(data[0], data[1], data[2], data[3], data[4]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    private void saveAppointments() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("appointments.csv", true))) {
            for (Appointment appointment : appointments) {
                writer.write(appointment.getPatientID() + "," + appointment.getAppointmentID() + "," +
                        appointment.getDoctorID() + "," + appointment.getDateTime() + "," +
                        appointment.getStatus());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<MedicalRecord> loadMedicalRecords() {
        List<MedicalRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("medical_records.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                records.add(new MedicalRecord(data[0], data[1], data[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    private void saveMedicalRecords() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("medical_records.csv"))) {
            for (MedicalRecord record : medicalRecords) {
                writer.write(
                        record.getDiagnosis() + "," + record.getTreatment() + "," + record.getAppointmentOutcome());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Appointment {
    private String patientID;
    private String appointmentID;
    private String doctorID;
    private String dateTime;
    private String status;

    public Appointment(String patientID, String doctorID, String dateTime, String status) {
        this.patientID = patientID;
        this.appointmentID = generateRandomID();
        this.doctorID = doctorID;
        this.dateTime = dateTime;
        this.status = status;
    }

    public Appointment(String patientID, String appointmentID, String doctorID, String dateTime, String status) {
        this.patientID = patientID;
        this.appointmentID = appointmentID;
        this.doctorID = doctorID;
        this.dateTime = dateTime;
        this.status = status;
    }

    private String generateRandomID() {
        Random random = new Random();
        int id = 1000 + random.nextInt(9000);
        return String.valueOf(id);
    }

    public String getPatientID() {
        return patientID;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Appointment with Doctor: " + doctorID + ", Date: " + dateTime + ", Status: " + status;
    }
}
