package healthcare.users.models;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdministratorModel {
    private String administratorID;

    public AdministratorModel(String administratorID) {
        this.administratorID = administratorID;
    }

    public String getAdministratorID() {
        return administratorID;
    }

    private static final String staffListPath = "Staff_List.csv";
    private static final String doctorListPath = "Doctor_List.csv";
    private static final String appointmentRequestsPath = "appointmentRequests.csv";
    private static final String medicineListPath = "Medicine_List.csv";
    private static final String replenishmentRequestsPath = "Replenishment_Requests.csv";
    private static final String staffPasswordsFile = "Staff_Passwords.csv";
    private static final String doctorPasswordsFile = "Doctor_Passwords.csv";

    private static final String DOCTOR_HEADER = "Doctor ID,Name,Role,Gender,Age,Specialisation";
    private static final String STAFF_HEADER = "Staff ID,Name,Role,Gender,Age";
    private static final String MEDICINE_HEADER = "Medicine Name,Stock,Low Stock Indicator,Stock Level";
    private static final String REPLENISHMENT_HEADER = "Medicine Name,Stock,Date,Replenishment Request";

    public List<String> readDataFromFile(String filePath) {
        List<String> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                data.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void writeDataToFile(String filePath, List<String> data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : data) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean appendDataToFile(String filePath, String entry) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(entry);
            bw.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> filterData(List<String> data, int fieldIndex, String filterValue) {
        List<String> filteredData = new ArrayList<>();
        for (String line : data) {
            String[] fields = line.split(",");
            if (fields.length > fieldIndex && fields[fieldIndex].equalsIgnoreCase(filterValue)) {
                filteredData.add(line);
            }
        }
        return filteredData;
    }

    public boolean updateEntry(String filePath, String id, int idIndex, String updatedEntry, String header) {
        List<String> data = readDataFromFile(filePath);
        boolean entryFound = false;
        List<String> updatedData = new ArrayList<>();

        if (!data.isEmpty() && data.get(0).equals(header)) {
            updatedData.add(data.remove(0));
        } else {
            updatedData.add(header);
        }

        for (String line : data) {
            String[] fields = line.split(",");
            if (fields[idIndex].equals(id)) {
                updatedData.add(updatedEntry);
                entryFound = true;
            } else {
                updatedData.add(line);
            }
        }

        writeDataToFile(filePath, updatedData);
        return entryFound;
    }

    public String[] getEntryById(String filePath, String id) {
        List<String> data = readDataFromFile(filePath);

        for (String line : data) {
            String[] fields = line.split(",");
            if (fields[0].equals(id)) {
                return fields;
            }
        }
        return null;
    }

    public boolean removeEntry(String filePath, String id, String header) {
        List<String> data = readDataFromFile(filePath);
        boolean entryFound = false;

        List<String> updatedData = new ArrayList<>();
        updatedData.add(header);
        for (String line : data) {
            String[] fields = line.split(",");
            if (!fields[0].equals(id)) {
                updatedData.add(line);
            } else {
                entryFound = true;
            }
        }

        if (entryFound) {
            writeDataToFile(filePath, updatedData);
        }

        return entryFound;
    }

    private static final String FEEDBACK_FILE = "patient_feedback.csv";

    public List<String[]> getFeedback() {
        List<String[]> feedbackList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FEEDBACK_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] feedbackDetails = line.split(",");
                feedbackList.add(feedbackDetails);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Feedback file not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading feedback file: " + e.getMessage());
        }

        return feedbackList;
    }

    public String getFilePathForStaffType(String staffType) {
        return switch (staffType.toLowerCase()) {
            case "doctor" -> doctorListPath;
            case "staff" -> staffListPath;
            default -> null;
        };
    }

    public static String getStaffListPath() {
        return staffListPath;
    }

    public static String getStaffPasswordsPath() {
        return staffPasswordsFile;
    }

    public static String getDoctorPasswordsPath() {
        return doctorPasswordsFile;
    }

    public static String getDoctorListPath() {
        return doctorListPath;
    }

    public static String getAppointmentRequestsPath() {
        return appointmentRequestsPath;
    }

    public static String getMedicineListPath() {
        return medicineListPath;
    }

    public static String getReplenishmentRequestsPath() {
        return replenishmentRequestsPath;
    }

    public static String getDoctorHeader() {
        return DOCTOR_HEADER;
    }

    public static String getStaffHeader() {
        return STAFF_HEADER;
    }

    public static String getMedicineHeader() {
        return MEDICINE_HEADER;
    }

    public static String getReplenishmentRequestsHeader() {
        return REPLENISHMENT_HEADER;
    }

}
