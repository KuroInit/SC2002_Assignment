package healthcare.users.view;

import java.util.List;
import java.util.Scanner;

public class DoctorView {

    public void showDoctorMenu() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("               Doctor Menu                 ");
        System.out.println("===========================================");
        System.out.println("|                                         |");
        System.out.println("|  1. View Patient Medical Records        |");
        System.out.println("|  2. Update Patient Medical Records      |");
        System.out.println("|  3. View Personal Schedule              |");
        System.out.println("|  4. Set Availability for Appointments   |");
        System.out.println("|  5. Accept or Decline Appointment Req.  |");
        System.out.println("|  6. View Upcoming Appointments          |");
        System.out.println("|  7. Record Appointment Outcome          |");
        System.out.println("|  8. Logout                              |");
        System.out.println("|                                         |");
        System.out.println("===========================================");
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayList(List<String> list) {
        for (String item : list) {
            System.out.println(item);
        }
    }

    public void displayPatientDetails(String[] details) {
        Screen.clearConsole();
        Scanner scanner = new Scanner(System.in);
        System.out.println("====================================================");
        System.out.println("                 Patient Details                    ");
        System.out.println("====================================================");
        System.out.printf("| %-20s : %s%n", "Patient ID", details[0]);
        System.out.printf("| %-20s : %s%n", "Name", details[1]);
        System.out.printf("| %-20s : %s%n", "Date of Birth", details[2]);
        System.out.printf("| %-20s : %s%n", "Gender", details[3]);
        System.out.printf("| %-20s : %s%n", "Blood Type", details[4]);
        System.out.printf("| %-20s : %s%n", "Email", details[5]);
        System.out.printf("| %-20s : %s%n", "Contact Number", details[6]);
        System.out.println("----------------------------------------------------");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        scanner.close();
    }

    public void displayAppointmentDetails(String[] appointmentDetails) {
        Screen.clearConsole();
        System.out.println("====================================================");
        System.out.println("               Appointment Details                  ");
        System.out.println("====================================================");
        System.out.printf("| %-20s : %s%n", "Appointment ID", appointmentDetails[0]);
        System.out.printf("| %-20s : %s%n", "Doctor ID", appointmentDetails[1]);
        System.out.printf("| %-20s : %s%n", "Patient ID", appointmentDetails[2]);
        System.out.printf("| %-20s : %s%n", "Date", appointmentDetails[3]);
        System.out.printf("| %-20s : %s%n", "Time", appointmentDetails[4]);
        System.out.printf("| %-20s : %s%n", "Status", appointmentDetails[5]);
        System.out.println("----------------------------------------------------");
    }

}
