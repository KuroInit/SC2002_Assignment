package healthcare.users.models;

import healthcare.records.Appointment;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class DoctorModel {
    private String doctorID;
    private String name;
    private List<Appointment> appointments;
    private List<LocalDate> availableDates;

    public DoctorModel(String doctorID, String name, String gender, String age) {
        this.doctorID = doctorID;
        this.name = name;
        this.appointments = new ArrayList<>();
        this.availableDates = new ArrayList<>();
    }

    public String getDoctorID() {
        return doctorID;
    }

    public String getName() {
        return name;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public List<LocalDate> getAvailableDates() {
        return availableDates;
    }

    public List<String> readCSV(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath));
    }

    public void writeCSV(String filePath, List<String> data) throws IOException {
        Files.write(Paths.get(filePath), data);
    }

    public void appendToCSV(String filePath, String data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(data);
            writer.newLine();
        }
    }

    public void removeAvailability(LocalDate date) {
        availableDates.remove(date);
    }
}
