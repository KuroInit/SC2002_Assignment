package healthcare.users.controllers;

import healthcare.users.models.DoctorModel;
import healthcare.users.view.DoctorView;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DoctorController {
    private DoctorModel model;
    private DoctorView view;
    
    public DoctorController(DoctorModel model, DoctorView view) {
        this.model = model;
        this.view = view;
    }

    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            view.showDoctorMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewPatientMedicalRecords();
                case 2 -> addPatientMedicalRecord();
                case 3 -> viewSchedule();
                case 4 -> selectAvailableSlot();
                case 5 -> {
                    viewPendingAppointments();
                    updateAppointmentStatus();
                }
                case 6 -> viewUpcomingAppointments();
                case 7 -> recordAppointmentOutcome();
                case 8 -> view.displayMessage("Logging out...");
                default -> view.displayMessage("Invalid choice. Please choose a valid option.");
            }
        } while (choice != 8);
    }

    private void viewPatientMedicalRecords() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the Patient ID to view details: ");
        String patientID = scanner.nextLine();

        try {
            List<String> patientList = model.readCSV("Patient_List.csv");
            List<String> medicalRecords = model.readCSV("medicalRecords.csv");

            boolean foundPatient = false;
            for (String line : patientList) {
                String[] details = line.split(",");
                if (details[0].equals(patientID)) {
                    view.displayPatientDetails(details);
                    foundPatient = true;
                    break;
                }
            }

            if (!foundPatient) {
                view.displayMessage("No details found for this patient ID.");
                return;
            }

            view.displayMessage("Medical History:");
            view.displayMessage("----------------------------------------------------");
            boolean hasHistory = false;

            for (String record : medicalRecords) {
                String[] recordDetails = record.split(",");
                if (recordDetails[0].equals(patientID)) {
                    view.displayMessage("Date of Diagnosis: " + recordDetails[1]);
                    view.displayMessage("Diagnosis: " + recordDetails[2]);
                    view.displayMessage("Treatment Plan: " + recordDetails[3]);
                    view.displayMessage("Prescribed Medicine: " + recordDetails[4]);
                    view.displayMessage("----------------------------------------------------");
                    hasHistory = true;
                }
            }

            if (!hasHistory) {
                view.displayMessage("No medical history found for this patient.");
            }

        } catch (IOException e) {
            view.displayMessage("An error occurred while accessing the files.");
        }
    }

    private void addPatientMedicalRecord() {
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

        String newRecord = patientID + "," + dateOfRecord + "," + diagnosis + "," + treatmentPlan + ","
                + prescribedMedicine;

        try {
            model.appendToCSV("medicalRecords.csv", newRecord);
            view.displayMessage("Medical record added successfully for Patient ID: " + patientID);
        } catch (IOException e) {
            view.displayMessage("Error saving the medical record.");
        }
    }

    private void viewSchedule() {
        view.displayMessage("Doctor " + model.getName() + "'s Schedule:");
        view.displayMessage("Booked Appointments:");
        viewBookedAppointments();
        view.displayMessage("\nAvailable Appointments:");
        viewAvailableAppointments();
    }

    private void viewBookedAppointments() {
        try {
            List<String> appointments = model.readCSV("appointmentRequests.csv");
            boolean hasBookedAppointments = false;

            for (String line : appointments) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[1].equals(model.getDoctorID())
                        && "approved".equalsIgnoreCase(parts[5])) {
                    view.displayMessage("Date: " + parts[3] + ", Time: " + parts[4] + ", Patient ID: " + parts[2]);
                    hasBookedAppointments = true;
                }
            }

            if (!hasBookedAppointments) {
                view.displayMessage("No booked appointments found.");
            }
        } catch (IOException e) {
            view.displayMessage("Error reading CSV file.");
        }
    }

    private void viewAvailableAppointments() {
        try {
            List<String> availableAppointments = model.readCSV("availableAppointments.csv");
            boolean hasAvailableAppointments = false;

            for (String line : availableAppointments) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].equals(model.getDoctorID())) {
                    view.displayMessage("Date: " + parts[1] + ", Time: " + parts[2]);
                    hasAvailableAppointments = true;
                }
            }

            if (!hasAvailableAppointments) {
                view.displayMessage("No available appointments found.");
            }
        } catch (IOException e) {
            view.displayMessage("Error reading CSV file.");
        }
    }

    private void viewUpcomingAppointments() {
        view.displayMessage("Appointments for Dr. " + model.getName() + " (ID: " + model.getDoctorID() + ")");
        view.displayMessage("----------------------------------------------------");

        try {
            List<String> appointments = model.readCSV("appointmentRequests.csv");
            boolean found = false;

            for (String line : appointments) {
                String[] fields = line.split(",");
                if (fields.length >= 6 && fields[1].equals(model.getDoctorID())
                        && "CONFIRMED".equalsIgnoreCase(fields[5])) {
                    view.displayAppointmentDetails(fields);
                    found = true;
                }
            }

            if (!found) {
                view.displayMessage("No approved appointments found.");
            }
        } catch (IOException e) {
            view.displayMessage("Error reading appointments.");
        }
    }

    private void selectAvailableSlot() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the date (YYYY-MM-DD): ");
        String dateInput = scanner.nextLine();

        LocalDate date;
        try {
            date = LocalDate.parse(dateInput, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            view.displayMessage("Invalid date format. Please enter in YYYY-MM-DD format.");
            return;
        }

        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            view.displayMessage("Selected date is a Sunday. No available slots.");
            return;
        }

        HashSet<LocalTime> bookedSlots = getBookedSlots(date);
        List<LocalTime> allSlots = generateAvailableTimeSlots();

        view.displayMessage("Mark slots as unavailable by entering the slot number. Enter 'done' when finished.");
        for (int i = 0; i < allSlots.size(); i++) {
            view.displayMessage((i + 1) + ". " + allSlots.get(i));
        }

        List<LocalTime> unavailableSlots = new ArrayList<>();
        while (true) {
            System.out.print("Enter slot number to mark as unavailable, or 'done' to finish: ");
            String input = scanner.nextLine();

            if ("done".equalsIgnoreCase(input)) {
                break;
            }

            try {
                int slotIndex = Integer.parseInt(input) - 1;
                if (slotIndex >= 0 && slotIndex < allSlots.size()) {
                    LocalTime selectedTime = allSlots.get(slotIndex);
                    if (!bookedSlots.contains(selectedTime) && !unavailableSlots.contains(selectedTime)) {
                        unavailableSlots.add(selectedTime);
                        view.displayMessage("Marked slot as unavailable: " + selectedTime);
                    } else {
                        view.displayMessage("Slot already marked as booked or unavailable.");
                    }
                } else {
                    view.displayMessage("Invalid slot number. Try again.");
                }
            } catch (NumberFormatException e) {
                view.displayMessage("Invalid input. Please enter a slot number or 'done'.");
            }
        }

        saveAvailableSlots(date, allSlots, unavailableSlots, bookedSlots);
    }

    private HashSet<LocalTime> getBookedSlots(LocalDate date) {
        HashSet<LocalTime> bookedSlots = new HashSet<>();
        try {
            List<String> appointments = model.readCSV("appointmentRequests.csv");
            for (String line : appointments) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[1].equals(model.getDoctorID())
                        && LocalDate.parse(parts[3]).equals(date)) {
                    bookedSlots.add(LocalTime.parse(parts[4]));
                }
            }
        } catch (IOException e) {
            view.displayMessage("Error reading CSV file.");
        }
        return bookedSlots;
    }

    private List<LocalTime> generateAvailableTimeSlots() {
        List<LocalTime> allSlots = new ArrayList<>();
        LocalTime time = LocalTime.of(9, 0);
        while (time.isBefore(LocalTime.of(17, 0))) {
            if (!time.equals(LocalTime.of(13, 0))) {
                allSlots.add(time);
            }
            time = time.plusHours(1);
        }
        return allSlots;
    }

    private void saveAvailableSlots(LocalDate date, List<LocalTime> allSlots, List<LocalTime> unavailableSlots,
            HashSet<LocalTime> bookedSlots) {
        List<LocalDateTime> availableSlots = new ArrayList<>();
        for (LocalTime slotTime : allSlots) {
            if (!unavailableSlots.contains(slotTime) && !bookedSlots.contains(slotTime)) {
                availableSlots.add(LocalDateTime.of(date, slotTime));
            }
        }

        try {
            for (LocalDateTime slot : availableSlots) {
                model.appendToCSV("availableAppointments.csv", model.getDoctorID() + "," + slot.toLocalDate() + ","
                        + slot.toLocalTime().withSecond(0).withNano(0));
            }
            view.displayMessage("Schedule updated. Available slots saved to availableAppointments.csv.");
        } catch (IOException e) {
            view.displayMessage("Error writing to CSV file.");
        }
    }

    private void viewPendingAppointments() {
        view.displayMessage("Pending Appointments for Dr. " + model.getName() + ":");
        boolean found = false;

        try {
            List<String> appointments = model.readCSV("appointmentRequests.csv");
            for (String line : appointments) {
                String[] fields = line.split(",");
                if (fields.length >= 6 && fields[1].equals(model.getDoctorID())
                        && "PENDING CONFIRMATION".equalsIgnoreCase(fields[5])) {
                    view.displayAppointmentDetails(fields);
                    found = true;
                }
            }
        } catch (IOException e) {
            view.displayMessage("Error reading appointments.");
        }

        if (!found) {
            view.displayMessage("No pending appointments found.");
        }
    }

    private void updateAppointmentStatus() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the Appointment ID to update: ");
        String appointmentId = scanner.nextLine();

        System.out.println("Choose an option:");
        System.out.println("1. Confirm Appointment");
        System.out.println("2. Cancel Appointment");
        System.out.print("Enter your choice (1 or 2): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        String newStatus;
        if (choice == 1) {
            newStatus = "CONFIRMED";
        } else if (choice == 2) {
            newStatus = "CANCELLED";
        } else {
            view.displayMessage("Invalid choice. Please enter 1 or 2.");
            return;
        }

        try {
            List<String> appointments = model.readCSV("appointmentRequests.csv");
            List<String> updatedAppointments = new ArrayList<>();
            boolean updated = false;

            for (String line : appointments) {
                String[] fields = line.split(",");
                if (fields[0].equals(appointmentId) && fields[1].equals(model.getDoctorID())) {
                    fields[5] = newStatus;
                    line = String.join(",", fields);
                    updated = true;
                }
                updatedAppointments.add(line);
            }

            if (updated) {
                model.writeCSV("appointmentRequests.csv", updatedAppointments);
                view.displayMessage("Appointment status updated successfully to " + newStatus + ".");
            } else {
                view.displayMessage("Appointment not found or invalid Appointment ID provided.");
            }
        } catch (IOException e) {
            view.displayMessage("Error updating appointments.");
        }
    }

    private void recordAppointmentOutcome() {
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

        try {
            List<String> appointments = model.readCSV("appointmentRequests.csv");
            List<String> updatedAppointments = new ArrayList<>();
            boolean updated = false;

            for (String line : appointments) {
                String[] fields = line.split(",");
                if (fields[0].equals(appointmentId) && fields[1].equals(model.getDoctorID())
                        && "CONFIRMED".equalsIgnoreCase(fields[5])) {
                    fields[5] = "COMPLETED";
                    fields[6] = typeOfService;
                    fields[7] = consultationNotes;
                    fields[8] = prescribedMedications;
                    fields[9] = quantity;
                    fields[10] = medicationStatus;
                    line = String.join(",", fields);
                    updated = true;
                }
                updatedAppointments.add(line);
            }

            if (updated) {
                model.writeCSV("appointmentRequests.csv", updatedAppointments);
                view.displayMessage("Appointment outcome recorded successfully.");
            } else {
                view.displayMessage("Appointment not found or invalid Appointment ID provided.");
            }
        } catch (IOException e) {
            view.displayMessage("Error saving updated appointments.");
        }
    }
}
