package healthcare.users.view;

import healthcare.users.models.*;

public class PatientView {
    public void displayPatientMenu() {
        System.out.println("\n===== Patient Menu =====");
        System.out.println("1. View Medical Record");
        System.out.println("2. Update Personal Information");
        System.out.println("3. View Available Appointment Slots");
        System.out.println("4. Schedule an Appointment");
        System.out.println("5. Reschedule an Appointment");
        System.out.println("6. Cancel an Appointment");
        System.out.println("7. View Scheduled Appointments");
        System.out.println("8. View Past Appointment Outcome Records");
        System.out.println("9. Logout");
        System.out.println("===========================\n");
    }

    public void displayPatientDetails(PatientModel patient) {
        System.out.println("Patient Details:");
        System.out.println("----------------------------------------------------");
        System.out.printf(
                "Patient ID: %s%nName: %s%nDate of Birth: %s%nGender: %s%nBlood Type: %s%nEmail: %s%nContact Number: %s%n",
                patient.getPatientID(), patient.getName(), patient.getDateOfBirth(), patient.getGender(),
                patient.getBloodType(), patient.getEmail(), patient.getContactNumber());
        System.out.println("----------------------------------------------------");
    }

    public void displayUpdateInfoOptions() {
        System.out.println("Choose the information to update:");
        System.out.println("1. Email Address");
        System.out.println("2. Contact Number");
        System.out.print("Enter your choice (1 or 2): ");
    }

    public void displayInvalidChoiceMessage() {
        System.out.println("Invalid choice. Please try again.");
    }

    public void displayLogoutMessage() {
        System.out.println("Logging out...");
    }

    public void promptForEmail() {
        System.out.print("Enter new email address: ");
    }

    public void promptForContactNumber() {
        System.out.print("Enter new contact number: ");
    }
}
