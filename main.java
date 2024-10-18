import java.io.IOException;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        // Initialize users (IDs, roles, passwords, and names from files)
        User.initializeUsers();

        // Display the login screen
        showLoginScreen();
    }

    // Simulate user login based on role
    private static void showLoginScreen() throws IOException
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the Hospital Management System (HMS)");
        System.out.print("Enter Hospital ID: ");
        String hospitalId = sc.nextLine();

        // Check if user ID exists in userPasswordStaffMap or userPasswordPatientMap
        if (!User.userPasswordStaffMap.containsKey(hospitalId) && !User.userPasswordPatientMap.containsKey(hospitalId))
        {
            System.out.println("Invalid Hospital ID. Please try again.");
            showLoginScreen();
            return;
        }

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        // Validate the password from both maps
        if ((User.userPasswordStaffMap.containsKey(hospitalId) && !User.userPasswordStaffMap.get(hospitalId).equals(password)) ||
                (User.userPasswordPatientMap.containsKey(hospitalId) && !User.userPasswordPatientMap.get(hospitalId).equals(password)))
        {
            System.out.println("Incorrect password. Please try again.");
            showLoginScreen();
            return;
        }

        // Login successful
        System.out.println("Login successful!");

        // Fetch the user's name from either staff or patient maps
        String name = User.userNameMapStaff.containsKey(hospitalId) ? User.userNameMapStaff.get(hospitalId) : User.userNameMapPatient.get(hospitalId);
        System.out.println("Good Day " + name + "!");  // Personalized message

        System.out.print("Do you want to change your password? (yes/no): ");
        String changePassword = sc.nextLine();
        if (changePassword.equalsIgnoreCase("yes"))
        {
            User.changeUserPassword(hospitalId);  // Calling the static method
        }

        // Load role-specific menu based on the role in userRoleStaffMap or userRolePatientMap
        String role = User.userRoleStaffMap.containsKey(hospitalId) ? User.userRoleStaffMap.get(hospitalId) : User.userRolePatientMap.get(hospitalId);
        switch (role) {
            case "Patient":
                Patient.showPatientMenu();
                break;
            case "Doctor":
                Doctor.showDoctorMenu();
                break;
            case "Pharmacist":
                Pharmacist.showPharmacistMenu();
                break;
            case "Administrator":
                Admin.showAdministratorMenu();
                break;
            default:
                System.out.println("Invalid role! Exiting...");
                break;
        }
    }
}
