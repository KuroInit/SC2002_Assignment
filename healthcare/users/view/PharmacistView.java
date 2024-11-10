package healthcare.users.view;

public class PharmacistView {

    public void displayPharmacistMenu() {
        System.out.println("\n===== Pharmacist Menu =====");
        System.out.println("1. View Appointment Outcome Record");
        System.out.println("2. Update Prescription Status");
        System.out.println("3. View Medication Inventory");
        System.out.println("4. Submit Replenishment Request");
        System.out.println("5. Logout");
        System.out.println("===========================\n");
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayError(String errorMessage) {
        System.out.println("Error: " + errorMessage);
    }

    public void displayAppointmentOutcome(String appointmentDetails) {
        System.out.println(appointmentDetails);
    }

    public void displayMedicationInventory(String inventoryDetails) {
        System.out.println(inventoryDetails);
    }
}
