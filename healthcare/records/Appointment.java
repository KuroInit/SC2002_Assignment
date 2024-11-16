package healthcare.records;

import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Appointment {

    private LocalDateTime appointmentDate;
    private TreatmentTypes treatment;
    private String appointmentID;
    private String doctorID;
    private String patientID;
    private List<Medication> prescribedMedication;
    private String consultationNotes;
    private AppointmentStatus appointmentStatus;
    private ServiceTypes serviceType;

    public Appointment(Date appointmentDate, TreatmentTypes type, String patientID, String doctorID) {
        this.appointmentDate = appointmentDate != null
                ? LocalDateTime.ofInstant(appointmentDate.toInstant(), ZoneId.systemDefault())
                : null;
        this.appointmentID = generateRandomID();
        this.treatment = type;
        this.doctorID = doctorID;
        this.patientID = patientID;
        this.prescribedMedication = new ArrayList<>();
        this.appointmentStatus = AppointmentStatus.PENDING;
        this.serviceType = ServiceTypes.CONSULTATION;
    }

    public String toCSV() {

        String medications = prescribedMedication.stream()
                .map(Medication::getMedicationName)
                .collect(Collectors.joining(";"));

        return String.join(",",
                appointmentID,
                doctorID,
                patientID,
                appointmentDate != null ? appointmentDate.toString() : "",
                serviceType != null ? serviceType.toString() : "",
                treatment != null ? treatment.toString() : "",
                appointmentStatus != null ? appointmentStatus.toString() : "",
                medications,
                consultationNotes != null ? consultationNotes : "");
    }

    private String generateRandomID() {
        Random random = new Random();
        int id = 1000 + random.nextInt(9000);
        return String.valueOf(id);
    }

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

    public LocalDateTime getAppointmentDate() {
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

    public void setStatus(AppointmentStatus appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public void setConsultationNotes(String notes) {
        this.consultationNotes = notes;
    }

    public void addMedication(String medName) {
        this.prescribedMedication.add(new Medication(medName));
    }

    public void setAppointmentStatus(AppointmentStatus status) {
        this.appointmentStatus = status;
    }

    public enum AppointmentStatus {
        PENDING, APPROVED, REJECTED, COMPLETED;
    }

    public enum TreatmentTypes {
        DENTAL, MEDICAL, SURGICAL, CONSULTATION;
    }

    public enum ServiceTypes {
        CONSULTATION, XRAY, BLOOD_TEST, SURGERY, OTHERS;
    }

    public class Medication {
        private String medicationName;
        private AppointmentStatus medicationStatus;

        public Medication(String name) {
            this.medicationName = name;
            this.medicationStatus = AppointmentStatus.PENDING;
        }

        public String getMedicationName() {
            return medicationName;
        }

        public AppointmentStatus getMedicationStatus() {
            return medicationStatus;
        }

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
