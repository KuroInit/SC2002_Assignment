package healthcare.users.models;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
    public static Map<String, String> userPasswordStaffMap = new HashMap<>();
    public static Map<String, String> userPasswordPatientMap = new HashMap<>();
    public static Map<String, String> userPasswordDoctorMap = new HashMap<>();
    public static Map<String, String> userRoleStaffMap = new HashMap<>();
    public static Map<String, String> userRolePatientMap = new HashMap<>();
    public static Map<String, String> userRoleDoctorMap = new HashMap<>();
    public static Map<String, String> userNameMapStaff = new HashMap<>();
    public static Map<String, String> userNameMapDoctor = new HashMap<>();
    public static Map<String, String> userNameMapPatient = new HashMap<>();

    private String userId;
    private String password;
    private String role;
    private String name;

    public UserModel(String userId, String password, String role, String name) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.name = name;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
