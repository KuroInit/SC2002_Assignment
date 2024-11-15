package healthcare.users.controllers;

import healthcare.users.models.*;
import healthcare.users.view.*;
import healthcare.records.*;

import java.io.*;
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
            line = reader.readLine(); // Read header line (if any)
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
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
            new Scanner(System.in).nextLine(); // Wait for Enter
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

        // Display list of doctors
        System.out.println("===========================================");
        System.out.println("            Available Doctors             ");
        System.out.println("===========================================");
        try (BufferedReader reader = new BufferedReader(new FileReader(doctorListPath))) {
            String line;
            line = reader.readLine(); // Read header line (if any)
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                doctors.put(details[0], details[1]);
                System.out.printf("Doctor ID: %s%nName: %s%nSpecialisation: %s%n", details[0], details[1], details[5]);
                System.out.println("-------------------------------------------");
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

        // Select a doctor
        System.out.print("Enter the Doctor ID of the doctor you want to see: ");
        String selectedDoctorId = scanner.nextLine();

        if (!doctors.containsKey(selectedDoctorId)) {
            System.out.println("===========================================");
            System.out.println("           Invalid Doctor ID.             ");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        // Select a date
        System.out.print("Enter the date to view available appointments (YYYY-MM-DD): ");
        String selectedDate = scanner.nextLine();

        // Display available appointments
        System.out.printf("Available Appointments for Dr. %s on %s:%n", doctors.get(selectedDoctorId), selectedDate);
        System.out.println("===========================================");

        boolean appointmentsFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            int index = 1;
            List<String> times = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] appointment = line.split(",");
                if (appointment[0].equals(selectedDoctorId) && appointment[1].equals(selectedDate)) {
                    System.out.printf("%d. Time: %s%n", index++, appointment[2]);
                    times.add(appointment[2]);
                    appointmentsFound = true;
                }
            }

            if (!appointmentsFound) {
                System.out.println("===========================================");
                System.out.println("   No available appointments found for this date.");
                System.out.println("===========================================");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine(); // Wait for Enter
                return;
            }

            // Optionally, allow the patient to select a time slot for immediate scheduling
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter

        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while accessing available appointments.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
        }
    }

    private void scheduleAppointment(Scanner scanner) {
        String doctorListPath = "Doctor_List.csv";
        String appointmentsPath = "availableAppointments.csv";
        String appointmentRequestsPath = "appointmentRequests.csv";
        Map<String, String> doctors = new HashMap<>();

        // Display list of doctors
        System.out.println("===========================================");
        System.out.println("            Available Doctors             ");
        System.out.println("===========================================");
        try (BufferedReader reader = new BufferedReader(new FileReader(doctorListPath))) {
            String line;
            int index = 1;
            line = reader.readLine(); // Read header line (if any)
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                doctors.put(details[0], details[1]);
                System.out.printf("%d. %s (ID: %s, Specialisation: %s)%n", index++, details[1], details[0], details[5]);
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

        // Select a doctor
        System.out.print("Enter the Doctor ID you want to schedule an appointment with: ");
        String selectedDoctorId = scanner.nextLine();

        if (!doctors.containsKey(selectedDoctorId)) {
            System.out.println("===========================================");
            System.out.println("          Invalid Doctor ID.               ");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        // Select a date
        System.out.print("Enter the date to view available appointments (YYYY-MM-DD): ");
        String selectedDate = scanner.nextLine();

        // Display available appointments
        List<String> availableTimes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            int index = 1;
            while ((line = reader.readLine()) != null) {
                String[] appointment = line.split(",");
                if (appointment[0].equals(selectedDoctorId) && appointment[1].equals(selectedDate)) {
                    System.out.printf("%d. Time: %s%n", index++, appointment[2]);
                    availableTimes.add(appointment[2]);
                }
            }
            if (availableTimes.isEmpty()) {
                System.out.println("===========================================");
                System.out.println(" No available appointments found for this date.");
                System.out.println("===========================================");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine(); // Wait for Enter
                return;
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while accessing available appointments.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        // Select a time slot
        System.out.print("Enter the number corresponding to the desired time slot: ");
        int timeChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (timeChoice < 1 || timeChoice > availableTimes.size()) {
            System.out.println("===========================================");
            System.out.println("            Invalid choice.               ");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        String selectedTime = availableTimes.get(timeChoice - 1);

        // Generate a unique appointment ID
        Random random = new Random();
        String appointmentID = String.valueOf(1000 + random.nextInt(9000));

        // Add the appointment to appointmentRequests.csv
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appointmentRequestsPath, true))) {
            writer.write(String.format("%s,%s,%s,%s,%s,PENDING CONFIRMATION,N/A,N/A,N/A,N/A,N/A%n",
                    appointmentID, selectedDoctorId, patientModel.getPatientID(), selectedDate, selectedTime));
            System.out.println("===========================================");
            System.out.println("Appointment scheduled successfully!");
            System.out.println("Appointment ID: " + appointmentID);
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while scheduling the appointment.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
            return;
        }

        // Remove the selected time slot from availableAppointments.csv
        List<String> updatedAppointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equals(String.join(",", selectedDoctorId, selectedDate, selectedTime))) {
                    updatedAppointments.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while updating available appointments.");
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
            System.out.println("Selected time slot removed from available appointments.");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while writing updated appointments.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
        }
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
                        && (details[5].equalsIgnoreCase("PENDING CONFIRMATION")
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

        // Step 3: Enter a new date and display available times
        List<String> availableTimes = new ArrayList<>();
        boolean appointmentsFound = false;
        String newDate = "";

        while (!appointmentsFound) {
            System.out
                    .print("Enter the new date to view available appointments (YYYY-MM-DD) or type 'exit' to cancel: ");
            newDate = scanner.nextLine();

            if (newDate.equalsIgnoreCase("exit")) {
                System.out.println("===========================================");
                System.out.println("           Rescheduling cancelled.         ");
                System.out.println("===========================================");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine(); // Wait for Enter
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
                System.out.println("===========================================");
                System.out.println("An error occurred while accessing the appointments list.");
                System.out.println("===========================================");
                e.printStackTrace();
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine(); // Wait for Enter
                return;
            }
        }

        // Step 4: Select a new time slot
        System.out.print("Choose a new appointment time by entering the corresponding number: ");
        int timeChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        if (timeChoice < 1 || timeChoice > availableTimes.size()) {
            System.out.println("===========================================");
            System.out.println("              Invalid choice.              ");
            System.out.println("===========================================");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
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
                // Only keep lines that are not the new appointment slot
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
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
        } catch (IOException e) {
            System.out.println("===========================================");
            System.out.println("An error occurred while writing to the available appointments file.");
            System.out.println("===========================================");
            e.printStackTrace();
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Wait for Enter
        }
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
                        (appointment[5].equalsIgnoreCase("PENDING CONFIRMATION") ||
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
                        && appointment[5].equalsIgnoreCase("CONFIRMED")) {
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
