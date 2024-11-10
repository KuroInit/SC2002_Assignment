package healthcare.users.view;

public class UserView {
    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayUserDetails(String userId, String name, String role) {
        System.out.println("User ID: " + userId);
        System.out.println("Name: " + name);
        System.out.println("Role: " + role);
    }
}
