package healthcare.users.view;

public class AdministratorView {
    public void displayMenu() {
        System.out.println("\n===== Administrator Menu =====");
        System.out.println("1. View and Manage Hospital Staff");
        System.out.println("2. View Appointment Details");
        System.out.println("3. View and Manage Medication Inventory");
        System.out.println("4. Approve Replenishment Requests");
        System.out.println("5. Logout");
        System.out.println("==============================\n");
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayLine(String line) {
        System.out.println(line);
    }
}
