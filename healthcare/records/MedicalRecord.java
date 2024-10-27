package healthcare.records;

public class MedicalRecord {
    private String diagnosis;
    private String treatment;
    private String appointmentOutcome;

    public MedicalRecord(String diagnosis, String treatment, String appointmentOutcome) {
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.appointmentOutcome = appointmentOutcome;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public String getAppointmentOutcome() {
        return appointmentOutcome;
    }

    @Override
    public String toString() {
        return "Diagnosis: " + diagnosis + ", Treatment: " + treatment + ", Outcome: " + appointmentOutcome;
    }
}
