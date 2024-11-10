package healthcare.users.models;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdministratorModel {
    private static final String STAFF_LIST_PATH = "Staff_List.csv";
    private static final String DOCTOR_LIST_PATH = "Doctor_List.csv";
    private static final String APPOINTMENT_REQUESTS_PATH = "appointmentRequests.csv";
    private static final String MEDICINE_LIST_PATH = "Medicine_List.csv";
    private static final String REPLENISHMENT_REQUESTS_PATH = "Replenishment_Requests.csv";

    public List<String> readFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    public void writeFile(String filePath, List<String> data) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : data) {
                bw.write(line);
                bw.newLine();
            }
        }
    }

    public void appendToFile(String filePath, String entry) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(entry);
            bw.newLine();
        }
    }

    public String getStaffListPath() {
        return STAFF_LIST_PATH;
    }

    public String getDoctorListPath() {
        return DOCTOR_LIST_PATH;
    }

    public String getAppointmentRequestsPath() {
        return APPOINTMENT_REQUESTS_PATH;
    }

    public String getMedicineListPath() {
        return MEDICINE_LIST_PATH;
    }

    public String getReplenishmentRequestsPath() {
        return REPLENISHMENT_REQUESTS_PATH;
    }
}
