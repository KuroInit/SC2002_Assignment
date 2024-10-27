import java.io.IOException;
import java.util.Scanner;

public class main {
    public static void main(String[] args) throws IOException {
        // Initialize users (IDs, roles, passwords, and names from files)
        user.initializeUsers();

        // Display the login screen
        showLoginScreen();
    }

    // Simulate user login based on role
    private static void showLoginScreen() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the Hospital Management System (HMS)");
        System.out.print("Enter Hospital ID: ");
        String hospitalId = sc.nextLine();

        // Check if user ID exists in userPasswordStaffMap or userPasswordPatientMap
        if (!user.userPasswordStaffMap.containsKey(hospitalId)
                && !user.userPasswordPatientMap.containsKey(hospitalId)) {
            System.out.println("Invalid Hospital ID. Please try again.");
            showLoginScreen();
            sc.close();
            return;
        }

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        // Validate the password from both maps
        if ((user.userPasswordStaffMap.containsKey(hospitalId)
                && !user.userPasswordStaffMap.get(hospitalId).equals(password)) ||
                (user.userPasswordPatientMap.containsKey(hospitalId)
                        && !user.userPasswordPatientMap.get(hospitalId).equals(password))) {
            System.out.println("Incorrect password. Please try again.");
            showLoginScreen();
            sc.close();
            return;
        }

        // Login successful
        System.out.println("Login successful!");

        // Fetch the user's name from either staff or patient maps
        String name = user.userNameMapStaff.containsKey(hospitalId) ? user.userNameMapStaff.get(hospitalId)
                : user.userNameMapPatient.get(hospitalId);
        System.out.println("Good Day " + name + "!"); // Personalized message

        System.out.print("Do you want to change your password? (yes/no): ");
        String changePassword = sc.nextLine();
        if (changePassword.equalsIgnoreCase("yes")) {
            user.changeUserPassword(hospitalId); // Calling the static method
        }
        sc.close();
        // Load role-specific menu based on the role in userRoleStaffMap or
        // userRolePatientMap
        String role = user.userRoleStaffMap.containsKey(hospitalId) ? user.userRoleStaffMap.get(hospitalId)
                : user.userRolePatientMap.get(hospitalId);
        switch (role) {
            case "Patient":
                patient.showPatientMenu();
                break;
            case "Doctor":
                doctor.showDoctorMenu();
                break;
            case "Pharmacist":
                Pharmacist.showPharmacistMenu();
                break;
            case "Administrator":
                admin.showAdministratorMenu();
                break;
            default:
                System.out.println("Invalid role! Exiting...");
                break;
        }
    }
}
