package healthcare.records;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecord {
    private String diagnosis;
    private String treatment;
    private String appointmentOutcome;
    private static final String RECORD_FILE = "medicalRecords.csv"; // Path to the CSV file

    public MedicalRecord(String diagnosis, String treatment, String appointmentOutcome) {
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.appointmentOutcome = appointmentOutcome;
    }

    // Getters
    public String getDiagnosis() { return diagnosis; }
    public String getTreatment() { return treatment; }
    public String getAppointmentOutcome() { return appointmentOutcome; }

    // Save the record to CSV
    public void saveRecordToCSV(int patientID) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RECORD_FILE, true))) {
            writer.write(patientID + "," + diagnosis + "," + treatment + "," + appointmentOutcome);
            writer.newLine();
            System.out.println("Medical record saved for Patient ID: " + patientID);
        } catch (IOException e) {
            System.out.println("Error saving medical record: " + e.getMessage());
        }
    }

    // Load all records for a specific patient ID
    public static List<MedicalRecord> loadRecordsByPatientID(int patientID) {
        List<MedicalRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(RECORD_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (Integer.parseInt(data[0]) == patientID) {
                    records.add(new MedicalRecord(data[1], data[2], data[3]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading medical records: " + e.getMessage());
        }
        return records;
    }

    @Override
    public String toString() {
        return "Diagnosis: " + diagnosis + ", Treatment: " + treatment + ", Outcome: " + appointmentOutcome;
    }
}
