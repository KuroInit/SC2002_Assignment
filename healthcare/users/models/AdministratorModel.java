package healthcare.users.models;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdministratorModel {
    private static final String staffListPath = "Staff_List.csv";
    private static final String doctorListPath = "Doctor_List.csv";
    private static final String appointmentRequestsPath = "appointmentRequests.csv";
    private static final String medicineListPath = "Medicine_List.csv";
    private static final String replenishmentRequestsPath = "Replenishment_Requests.csv";

    // Method to read data from a CSV file
    public List<String> readDataFromFile(String filePath) {
        List<String> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Method to write data to a CSV file
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

    // Method to append data to a CSV file
    public void appendDataToFile(String filePath, String entry) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(entry);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to filter data based on a specific field and value
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

    // Method to update an entry in the CSV file based on a specific ID
    public boolean updateEntry(String filePath, String id, int idIndex, String[] updatedFields) {
        List<String> data = readDataFromFile(filePath);
        boolean entryFound = false;

        for (int i = 0; i < data.size(); i++) {
            String[] fields = data.get(i).split(",");
            if (fields[idIndex].equals(id)) {
                data.set(i, String.join(",", updatedFields));
                entryFound = true;
                break;
            }
        }

        if (entryFound) {
            writeDataToFile(filePath, data);
        }

        return entryFound;
    }

    // Method to remove an entry from a CSV file based on a specific ID
    public boolean removeEntry(String filePath, String id, int idIndex) {
        List<String> data = readDataFromFile(filePath);
        boolean entryFound = false;

        List<String> updatedData = new ArrayList<>();
        for (String line : data) {
            String[] fields = line.split(",");
            if (!fields[idIndex].equals(id)) {
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

    // Method to get file paths based on the type of staff
    public String getFilePathForStaffType(String staffType) {
        return switch (staffType.toLowerCase()) {
            case "doctor" -> doctorListPath;
            case "pharmacist", "nurse", "administrator" -> staffListPath;
            default -> null;
        };
    }

    // Getters for static file paths (useful for accessing them directly)
    public static String getStaffListPath() {
        return staffListPath;
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
}
