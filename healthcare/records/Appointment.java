package healthcare.records;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Appointment {

    private LocalDate appointmentDate;
    private TreatmentTypes treatment;
    private String appointmentID;
    private String doctorID;
    private String patientID;
    private List<Medication> prescribedMedication;
    private String consultationNotes;
    private AppointmentStatus appointmentStatus;
    private ServiceTypes serviceType;

    // init
    public Appointment(Date appointmenDate, TreatmentTypes type, String patientID, String doctorID) {
        this.appointmentDate = appointmentDate;
        this.appointmentID = generateRandomID();
        this.treatment = TreatmentTypes.CONSULTATION;
        this.doctorID = doctorID;
        this.patientID = patientID;
        this.prescribedMedication = new ArrayList<>();
        this.consultationNotes = "No Notes";
        this.appointmentStatus = AppointmentStatus.PENDING;
        this.serviceType = ServiceTypes.CONSULTATION;
    }

    private String generateRandomID() {
        Random random = new Random();
        int id = 1000 + random.nextInt(9000);
        return String.valueOf(id);
    }

    // patient methods
    public String getPatientID() {
        return patientID;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public AppointmentStatus getAppointmentStatus() {
        return appointmentStatus;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public TreatmentTypes getTreatmentType() {
        return treatment;
    }

    public List<Medication> getPrescribedMedications() {
        return prescribedMedication;
    }

    public String getConsultationNotes() {
        return consultationNotes;
    }

    public ServiceTypes getServiceType() {
        return serviceType;
    }

    public void setServiceTypes(ServiceTypes serviceType) {
        this.serviceType = serviceType;
    }

    public String printAppointments() {
        return "Appointment with Doctor: " + doctorID + ", Date: " + appointmentDate + ", Status: "
                + appointmentStatus.toString();
    }

    // doctor class
    public void setStatus(AppointmentStatus appointmenStatus) {
        this.appointmentStatus = appointmenStatus;
    }

    public void setConsultationNotes(String notes) {
        this.consultationNotes = notes;
    }

    public void addMedication(String medName) {
        this.prescribedMedication.add(new Medication(medName));
    }

    // enums
    public enum AppointmentStatus {
        PENDING, APPROVED, REJECTED, COMPLETED;
    }

    public enum TreatmentTypes {
        DENTAL, MEDICAL, SURGICAL, CONSULTATION;
    }

    public enum ServiceTypes {
        CONSULTATION, XRAY, BLOOD_TEST, SURGERY, OTHERS;
    }

    // medication class
    public class Medication {
        private String medicationName;
        private AppointmentStatus medicationStatus;

        // init
        public Medication(String name) {
            this.medicationName = name;
            this.medicationStatus = medicationStatus.PENDING;
        }

        public String getMedicationName() {
            return medicationName;
        }

        public AppointmentStatus getMedicationStatus() {
            return medicationStatus;
        }

        // Update status for medication
        public void updateMedicationStatus(AppointmentStatus status) {
            this.medicationStatus = status;
        }

        @Override
        public String toString() {
            return "Medication: " + medicationName + ", Status: " + medicationStatus;
        }

    }

    @Override
    public String toString() {
        return "Appointment ID: " + appointmentID +
                "\nDoctor ID: " + doctorID +
                "\nPatient ID: " + patientID +
                "\nDate: " + appointmentDate +
                "\nService Type: " + serviceType +
                "\nTreatment Type: " + treatment +
                "\nStatus: " + appointmentStatus +
                "\nConsultation Notes: " + consultationNotes +
                "\nPrescribed Medications: " + prescribedMedication;
    }

}