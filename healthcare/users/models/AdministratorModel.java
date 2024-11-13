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

    // Method to read data from a CSV file
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
    public boolean updateEntry(String filePath, String id, int idIndex, String updatedEntry, String header) {
        List<String> data = readDataFromFile(filePath);
        boolean entryFound = false;
        List<String> updatedData = new ArrayList<>();
    
        // Use the specified header, adding it if the file is empty or header is missing
        if (!data.isEmpty() && data.get(0).equals(header)) {
            updatedData.add(data.remove(0)); // Keep existing header and remove it from data list
        } else {
            updatedData.add(header); // Add correct header if missing
        }
    
        for (String line : data) {
            String[] fields = line.split(",");
            if (fields[idIndex].equals(id)) {
                updatedData.add(updatedEntry);  // Add the updated entry
                entryFound = true;
            } else {
                updatedData.add(line);  // Keep unchanged entry
            }
        }
    
        // Write updated data back to file
        writeDataToFile(filePath, updatedData);
        return entryFound;
    }
    
    
    
    public String[] getEntryById(String filePath, String id) {
        List<String> data = readDataFromFile(filePath);
    
        for (String line : data) {
            String[] fields = line.split(",");
            if (fields[0].equals(id)) {  // Assuming ID is at index 0
                return fields;  // Return existing fields if ID matches
            }
        }
        return null;  // Return null if ID is not found
    }
    

    // Method to remove an entry from a CSV file based on a specific ID
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

    // Method to get file paths based on the type of staff
    public String getFilePathForStaffType(String staffType) {
        return switch (staffType.toLowerCase()) {
            case "doctor" -> doctorListPath;
            case "staff" -> staffListPath;
            default -> null;
        };
    }

    // Getters for static file paths (useful for accessing them directly)
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
