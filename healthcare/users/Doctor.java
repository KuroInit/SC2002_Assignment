package healthcare.users;

import healthcare.records.Appointment;
import healthcare.records.Appointment.Medication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Doctor {
    private String doctorID;
    private String name;
    private String specialization;
    private List<Appointment> appointments;
    private List<LocalDate> availableDates;

    public Doctor(String doctorID, String name, String specialization) {
        this.doctorID = doctorID;
        this.name = name;
        this.specialization = specialization;
        this.appointments = new ArrayList<>();
        this.availableDates = new ArrayList<>();
    }

    public String getDoctorID() {
        return doctorID;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public List<LocalDate> getAvailableDates() {
        return availableDates;
    }

    public void addAvailability(LocalDate date) {
        availableDates.add(date);
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
    public void recordAppointmentOutcome(Appointment appointment, Appointment.ServiceTypes serviceType,
            String consultationNotes, List<String> medications) {
        if (appointments.contains(appointment)) {
            appointment.setServiceTypes(serviceType);
            appointment.setConsultationNotes(consultationNotes);

            for (String med : medications) {
                appointment.addMedication(med);
            }

            appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
            System.out.println("Appointment outcome recorded for appointment ID: " + appointment.getAppointmentID());
        } else {
            System.out.println("Appointment not found in schedule.");
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
        File file = new File("database/doctorAppointment.csv");

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
        File file = new File("database/doctorAppointment.csv");

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
        return "Doctor ID: " + doctorID + "\nName: " + name + "\nSpecialization: " + specialization;
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
                    viewSchedule();
                case 2:
                    getAppointments();
                default:
                    System.out.println("Invalid choice. Please choose a valid option.");
            }
        } while (choice != 9);
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
