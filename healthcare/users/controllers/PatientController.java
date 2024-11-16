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
            scanner.nextLine();
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
                new Scanner(System.in).nextLine();
                return;
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while accessing the patient list.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine();
            return;
        }

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
            new Scanner(System.in).nextLine();
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while accessing medical records.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine();
        }
    }

    private void updatePersonalInfo(Scanner scanner) {
        patientView.displayUpdateInfoOptions();
        int choice = scanner.nextInt();
        scanner.nextLine();

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
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (isHeader) {
                    lines.add(line);
                    isHeader = false;
                    continue;
                }

                if (data[0].equals(patientModel.getPatientID())) {
                    if (newEmail != null) {
                        data[5] = newEmail;
                    }
                    if (newContactNumber != null) {
                        data[6] = newContactNumber;
                    }
                    line = String.join(",", data);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while updating personal information.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine();
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(patientListPath))) {
            for (String updatedLine : lines) {
                writer.write(updatedLine);
                writer.newLine();
            }
            System.out.println("===========================================");
            System.out.println("  Personal information updated successfully.");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine();
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while writing updated information.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine();
        }
    }

    private void viewAvailableAppointments(Scanner scanner) {
        String doctorListPath = "Doctor_List.csv";
        String appointmentsPath = "availableAppointments.csv";
        Map<String, String> doctors = new HashMap<>();

        System.out.println("===========================================");
        System.out.println("            Available Doctors             ");
        System.out.println("===========================================");
        try (BufferedReader reader = new BufferedReader(new FileReader(doctorListPath))) {
            String line;
            line = reader.readLine();
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

        System.out.print("Enter the Doctor ID of the doctor you want to see: ");
        String selectedDoctorId = scanner.nextLine().trim();

        if (!doctors.containsKey(selectedDoctorId)) {
            System.out.println("===========================================");
            System.out.println("           Invalid Doctor ID.             ");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        Set<String> availableDates = new TreeSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            line = reader.readLine();
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
                scanner.nextLine();
                return;
            } else {
                System.out.printf("Available Dates for Dr. %s:%n", doctors.get(selectedDoctorId));
                System.out.println("===========================================");
                int index = 1;
                List<String> dateList = new ArrayList<>(availableDates);
                for (String date : dateList) {
                    System.out.printf("%d. %s%n", index++, date);
                }

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

                System.out.printf("Available Time Slots for Dr. %s on %s:%n", doctors.get(selectedDoctorId),
                        selectedDate);
                System.out.println("===========================================");

                boolean appointmentsFound = false;
                List<String> times = new ArrayList<>();

                try (BufferedReader timeReader = new BufferedReader(new FileReader(appointmentsPath))) {
                    String timeLine;
                    timeLine = timeReader.readLine();
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
        scanner.nextLine();
    }

    private void scheduleAppointment(Scanner scanner) {
        String doctorListPath = "Doctor_List.csv";
        String appointmentsPath = "availableAppointments.csv";
        String appointmentRequestsPath = "appointmentRequests.csv";
        Map<String, String> doctors = new HashMap<>();

        System.out.println("===========================================");
        System.out.println("            Available Doctors             ");
        System.out.println("===========================================");
        try (BufferedReader reader = new BufferedReader(new FileReader(doctorListPath))) {
            String line;
            line = reader.readLine();
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

        String selectedDoctorId;
        do {
            System.out.print("Enter the Doctor ID you want to schedule an appointment with: ");
            selectedDoctorId = scanner.nextLine().trim();
            if (!doctors.containsKey(selectedDoctorId)) {
                System.out.println("Invalid Doctor ID. Please try again.");
            }
        } while (!doctors.containsKey(selectedDoctorId));

        Set<String> availableDates = new TreeSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            line = reader.readLine();
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

        List<String> availableTimes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            line = reader.readLine();
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

        String dateTimeStr = selectedDate + " " + selectedTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime appointmentDateTime = LocalDateTime.parse(dateTimeStr, formatter);
        Date appointmentDate = Date.from(appointmentDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Appointment appointment = new Appointment(appointmentDate, null, patientModel.getPatientID(), selectedDoctorId);

        String appointmentID = appointment.getAppointmentID();

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

        List<String> updatedAppointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            String header = reader.readLine();
            updatedAppointments.add(header);
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
            scanner.nextLine();
            return;
        }

        if (patientAppointments.isEmpty()) {
            System.out.println("===========================================");
            System.out.println("     You have no appointments to reschedule.");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

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
            scanner.nextLine();
            return;
        }

        String doctorId = appointmentToReschedule[1];
        String oldDate = appointmentToReschedule[3];
        String oldTime = appointmentToReschedule[4];

        List<String[]> availableSlots = new ArrayList<>();

        System.out.printf("Available Slots for Doctor %s:%n", doctorId);
        System.out.println("----------------------------------------------------");

        try (BufferedReader appointmentsReader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            while ((line = appointmentsReader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 3 && values[0].equals(doctorId)) {

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
            scanner.nextLine();
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

        int index = 1;
        for (String[] slot : availableSlots) {
            String date = slot[1];
            String time = slot[2];
            System.out.printf("%d. Date: %s | Time: %s%n", index++, date, time);
        }

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
            scanner.nextLine();
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
            scanner.nextLine();
            return;
        }

        List<String> updatedAppointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                if (!(values[0].equals(doctorId) && values[1].equals(newDate) && values[2].equals(newTime))) {
                    updatedAppointments.add(line);
                }
            }

            updatedAppointments.add(String.format("%s,%s,%s", doctorId, oldDate, oldTime));
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while updating the available appointments.");
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
            System.out.println("Rescheduling complete. The old slot has been added back to available appointments.");
            System.out.println("===========================================");
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while writing to the available appointments file.");
            System.out.println("===========================================");
            e.printStackTrace();
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void cancelAppointment(Scanner scanner) {
        String appointmentRequestsPath = "appointmentRequests.csv";
        String appointmentsPath = "availableAppointments.csv";

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
            scanner.nextLine();
            return;
        }

        if (patientAppointments.isEmpty()) {
            System.out.println("===========================================");
            System.out.println("You have no appointments to cancel.");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("===========================================");
        System.out.println("            Your Appointments             ");
        System.out.println("===========================================");
        for (int i = 0; i < patientAppointments.size(); i++) {
            String[] appointment = patientAppointments.get(i);
            System.out.printf("%d. Appointment ID: %s, Doctor ID: %s, Date: %s, Time: %s%n",
                    i + 1, appointment[0], appointment[1], appointment[3], appointment[4]);
        }
        System.out.println("===========================================");

        System.out.print("Enter the number corresponding to the appointment you want to cancel: ");
        int appointmentChoice = scanner.nextInt();
        scanner.nextLine();

        if (appointmentChoice < 1 || appointmentChoice > patientAppointments.size()) {
            System.out.println("===========================================");
            System.out.println("Invalid choice. Please try again.");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        String[] appointmentToCancel = patientAppointments.get(appointmentChoice - 1);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appointmentsPath, true))) {
            writer.write(String.join(",", appointmentToCancel[1], appointmentToCancel[3], appointmentToCancel[4]));
            writer.newLine();
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while updating available appointments.");
            e.printStackTrace();
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

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
            scanner.nextLine();
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
            scanner.nextLine();
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while writing updated appointments.");
            e.printStackTrace();
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private void viewScheduledAppointments() {
        String appointmentRequestsPath = "appointmentRequests.csv";
        String doctorListPath = "Doctor_List.csv";
        Map<String, String> doctorNames = new HashMap<>();

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
            new Scanner(System.in).nextLine();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentRequestsPath))) {
            String line;
            boolean hasAppointments = false;

            System.out.println("===========================================");
            System.out.println("         Your Scheduled Appointments       ");
            System.out.println("===========================================");
            while ((line = reader.readLine()) != null) {
                String[] appointment = line.split(",");
                if (appointment[2].equals(patientModel.getPatientID())
                        && (appointment[5].equalsIgnoreCase("CONFIRMED")
                                || appointment[5].equalsIgnoreCase("PENDING"))) {
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
            new Scanner(System.in).nextLine();
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while fetching appointments.");
            e.printStackTrace();
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine();
        }
    }

    private void viewCompletedAppointments() {
        String appointmentRequestsPath = "appointmentRequests.csv";

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
            new Scanner(System.in).nextLine();

        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while fetching completed appointments.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine();
        }
    }

}
