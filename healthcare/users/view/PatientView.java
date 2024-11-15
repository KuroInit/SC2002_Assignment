package healthcare.users.view;

import java.util.Scanner;

import healthcare.users.models.*;

public class PatientView {
    public void displayPatientMenu() {
        System.out.println("===========================================");
        System.out.println("               Patient Menu                ");
        System.out.println("===========================================");
        System.out.println("|                                         |");
        System.out.println("|  1. View Medical Record                 |");
        System.out.println("|  2. Update Personal Information         |");
        System.out.println("|  3. View Available Appointment Slots    |");
        System.out.println("|  4. Schedule an Appointment             |");
        System.out.println("|  5. Reschedule an Appointment           |");
        System.out.println("|  6. Cancel an Appointment               |");
        System.out.println("|  7. View Scheduled Appointments         |");
        System.out.println("|  8. View Past Appointment Outcome Recs  |");
        System.out.println("|  9. Feedback                            |");
        System.out.println("| 10. Logout                              |");
        System.out.println("|                                         |");
        System.out.println("===========================================");
    }

    public void displayPatientDetails(PatientModel patient) {
        Screen.clearConsole();
        System.out.println("====================================================");
        System.out.println("                  Patient Details                   ");
        System.out.println("====================================================");
        System.out.printf("| %-20s : %s%n", "Patient ID", patient.getPatientID());
        System.out.printf("| %-20s : %s%n", "Name", patient.getName());
        System.out.printf("| %-20s : %s%n", "Date of Birth", patient.getDateOfBirth());
        System.out.printf("| %-20s : %s%n", "Gender", patient.getGender());
        System.out.printf("| %-20s : %s%n", "Blood Type", patient.getBloodType());
        System.out.printf("| %-20s : %s%n", "Email", patient.getEmail());
        System.out.printf("| %-20s : %s%n", "Contact Number", patient.getContactNumber());
        System.out.println("----------------------------------------------------");
        System.out.println("\nPress Enter to continue...");
        new java.util.Scanner(System.in).nextLine(); // Wait for Enter
    }

    public void displayUpdateInfoOptions() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("      Update Personal Information          ");
        System.out.println("===========================================");
        System.out.println("|                                         |");
        System.out.println("|  1. Email Address                       |");
        System.out.println("|  2. Contact Number                      |");
        System.out.println("|                                         |");
        System.out.println("===========================================");
        System.out.print("Enter your choice (1 or 2): ");
    }

    public void displayInvalidChoiceMessage() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("             Invalid Choice                ");
        System.out.println("===========================================");
        System.out.println("   Invalid choice. Please try again.");
        System.out.println("===========================================");
        System.out.println("\nPress Enter to continue...");
        new Scanner(System.in).nextLine(); // Wait for Enter
    }

    public void displayLogoutMessage() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("              Logging Out...               ");
        System.out.println("===========================================");
        System.out.println("\nPress Enter to continue...");
        new Scanner(System.in).nextLine(); // Wait for Enter
    }

    public void promptForEmail() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("              Update Email                 ");
        System.out.println("===========================================");
        System.out.print("Enter new email address: ");
    }

    public void promptForContactNumber() {
        Screen.clearConsole();
        System.out.println("===========================================");
        System.out.println("         Update Contact Number             ");
        System.out.println("===========================================");
        System.out.print("Enter new contact number: ");
    }
}
