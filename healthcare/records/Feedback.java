package healthcare.records;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Feedback {
    private String patientName;
    private String feedbackMessage;
    private int rating;

    public Feedback(String patientName, String feedbackMessage, int rating) {
        this.patientName = patientName;
        this.feedbackMessage = feedbackMessage;
        this.rating = rating;
    }

    public void writeFeedbackToCSV() {
        String fileName = "patient_feedback.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            writer.printf("\"%s\",\"%s\",%d%n", patientName, feedbackMessage, rating);
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}
