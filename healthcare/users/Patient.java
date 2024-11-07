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

    public Patient(String patientID, String name, String dob, String gender, String bloodType, String contactInfo) {
        this.patientID = patientID;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.bloodType = bloodType;
        this.contactInfo = contactInfo;
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

                // Check if the record is for this patient
                if (fields[0].equals(patientID)) {
                    System.out.println("Date: " + fields[1] + 
                            ", Diagnoses: " + fields[2] + 
                            ", Treatment Plan: " + fields[3] + 
                            ", Prescribed Medicine: " + fields[4]);
                    found = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading medical records.");
        }

        if (!found) {
            System.out.println("No medical records found for this patient.");
        }
    }

    public void updatePersonalInfo() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new contact information: ");
        contactInfo = scanner.nextLine();
        System.out.println("Personal information updated successfully.");

        updatePatientDataInFile();
    }

    public void updatePatientDataInFile() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Patient_List.csv"))) {
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
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Doctor ID: ");
        String doctorId = sc.nextLine();

        try (BufferedReader br = new BufferedReader(new FileReader("availableAppointments.csv"))) {
            String line;
            System.out.println("Available Slots for Doctor ID " + doctorId + ":");
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(doctorId)) {  // Assuming doctorId is the first field
                    System.out.println("Date: " + fields[1] + ", Time: " + fields[2]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading available appointments.");
        }
    }

    public void scheduleAppointment() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Doctor ID for Appointment: ");
        String doctorId = scanner.nextLine();
        System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter Appointment Time (HH:MM): ");
        String time = scanner.nextLine();
    
        // Generate a random appointment ID for uniqueness
        String appointmentId = String.valueOf(new Random().nextInt(9000) + 1000); // 4-digit ID
        String status = "PENDING";
        String typeOfService = "N/A";
        String prescribedMedications = "N/A";
        String consultationNotes = "N/A";
    
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("appointmentRequests.csv", true))) {
            bw.write(appointmentId + "," + doctorId + "," + patientID + "," + date + "," + time + "," + status + "," + typeOfService + "," +
                     prescribedMedications + "," + consultationNotes + "\n");
            System.out.println("Appointment scheduled successfully and marked as pending.");
        } catch (IOException e) {
            System.out.println("Error scheduling appointment.");
        }
    }
    

    public void rescheduleAppointment() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the Appointment ID you want to reschedule: ");
        String appointmentId = scanner.nextLine();
        System.out.print("Enter the new Appointment Date (YYYY-MM-DD): ");
        String newDate = scanner.nextLine();
        System.out.print("Enter the new Appointment Time (HH:MM): ");
        String newTime = scanner.nextLine();
    
        List<String> appointments = new ArrayList<>();
        boolean rescheduled = false;
    
        try (BufferedReader br = new BufferedReader(new FileReader("appointmentRequests.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(appointmentId) && fields[2].equals(patientID)) {  // Matching AppointmentID and PatientID
                    // Update the appointment with new date and time while keeping other fields the same
                    fields[3] = newDate; // Update the date field
                    fields[4] = newTime; // Add new time if required
                    line = String.join(",", fields);
                    rescheduled = true;
                }
                appointments.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading appointments.");
        }
    
        if (rescheduled) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("appointmentRequests.csv"))) {
                for (String appointment : appointments) {
                    bw.write(appointment + "\n");
                }
                System.out.println("Appointment rescheduled successfully.");
            } catch (IOException e) {
                System.out.println("Error saving updated appointments.");
            }
        } else {
            System.out.println("Appointment not found or invalid Appointment ID provided.");
        }
    }    

    public void cancelAppointment() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the Appointment ID of the appointment you want to cancel: ");
        String appointmentId = scanner.nextLine();
    
        List<String> appointments = new ArrayList<>();
        boolean cancelled = false;
    
        try (BufferedReader br = new BufferedReader(new FileReader("appointmentRequests.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(appointmentId) && fields[2].equals(patientID)) {  // Matching AppointmentID and PatientID
                    // Update the appointment status to "CANCELLED"
                    fields[5] = "CANCELLED";  // Assuming status is the 5th field in CSV
                    line = String.join(",", fields);
                    cancelled = true;
                }
                appointments.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading appointments.");
        }
    
        if (cancelled) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("appointmentRequests.csv"))) {
                for (String appointment : appointments) {
                    bw.write(appointment + "\n");
                }
                System.out.println("Appointment cancelled successfully.");
            } catch (IOException e) {
                System.out.println("Error saving updated appointments.");
            }
        } else {
            System.out.println("Appointment not found or invalid Appointment ID provided.");
        }
    }
    
    public void viewScheduledAppointments() {
        System.out.println("Appointments for Patient ID: " + patientID);
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader("appointmentRequests.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                
                // Check if the line has at least 5 fields to avoid index errors
                if (fields.length < 5) {
                    continue;  // Skip lines with insufficient data
                }

                // Check if the appointment is for this patient and has an allowed status
                if (fields[2].equals(patientID) && 
                    (fields[5].equals("APPROVED") || fields[5].equals("REJECTED") || fields[5].equals("PENDING"))) {
                        System.out.println("AppointmentID: " + fields[0] + ", DoctorID: " + fields[1] +
                        ", Date: " + fields[3] + ", Date: " + fields[4] + ", Status: " + fields[5] +
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
            System.out.println("No appointments found with status APPROVED, REJECTED, or PENDING.");
        }
    }

    public void viewCompletedAppointments() {
        System.out.println("Completed Appointments for Patient ID: " + patientID);
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader("appointmentRequests.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                
                // Check if the line has at least 5 fields to avoid index errors
                if (fields.length < 6) {
                    continue;  // Skip lines with insufficient data
                }

                // Check if the appointment is for this patient and has a COMPLETED status
                if (fields[2].equals(patientID) && fields[5].equals("COMPLETED")) {
                    System.out.println("AppointmentID: " + fields[0] + ", DoctorID: " + fields[1] +
                            ", Date: " + fields[3] + ", Date: " + fields[4] + ", Status: " + fields[5] +
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
            System.out.println("No completed appointments found.");
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
