package healthcare.users.view;

import java.util.List;

public class DoctorView {
    public void showDoctorMenu() {
        System.out.println("\n===== Doctor Menu =====");
        System.out.println("1. View Patient Medical Records");
        System.out.println("2. Update Patient Medical Records");
        System.out.println("3. View Personal Schedule");
        System.out.println("4. Set Availability for Appointments");
        System.out.println("5. Accept or Decline Appointment Requests");
        System.out.println("6. View Upcoming Appointments");
        System.out.println("7. Record Appointment Outcome");
        System.out.println("8. Logout");
        System.out.println("=======================\n");
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
        System.out.printf("Patient ID: %s%n", details[0]);
        System.out.printf("Name: %s%n", details[1]);
        System.out.printf("Date of Birth: %s%n", details[2]);
        System.out.printf("Gender: %s%n", details[3]);
        System.out.printf("Blood Type: %s%n", details[4]);
        System.out.printf("Email: %s%n", details[5]);
        System.out.printf("Contact Number: %s%n", details[6]);
        System.out.println("----------------------------------------------------\n");
    }

    public void displayAppointmentDetails(String[] appointmentDetails) {
        System.out.printf("Appointment ID: %s%n", appointmentDetails[0]);
        System.out.printf("Doctor ID: %s%n", appointmentDetails[1]);
        System.out.printf("Patient ID: %s%n", appointmentDetails[2]);
        System.out.printf("Date: %s%n", appointmentDetails[3]);
        System.out.printf("Time: %s%n", appointmentDetails[4]);
        System.out.printf("Status: %s%n", appointmentDetails[5]);
        System.out.println("----------------------------------------------------");
    }
}
