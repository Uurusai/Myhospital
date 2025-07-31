package com.hms.myhospital;

import com.hms.client.HMSClient;
import com.hms.notification.NotificationServer;
import com.hms.utils.Validator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import com.hms.utils.SceneSwitcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class prescriptionController
{
    private final HMSClient client;
    public prescriptionController(HMSClient client) {
        this.client = client;
    }

    private void initialize() {
        // Load all patients into the ChoiceBox
        List<String> patientNames = client.getAllPatients().stream()
                .map(patient -> patient.getName())
                .toList();
        allPatientsChoiceBox.getItems().addAll(patientNames);
        allPatientsChoiceBox.getSelectionModel().selectFirst();
    }

    @FXML TextArea diagnosis;
    @FXML Button addMore;
    @FXML Button savePrescriptionBtn;
    @FXML Button cancelPrescriptionBtn;
    @FXML VBox medicationRowsBox;
    @FXML ChoiceBox allPatientsChoiceBox;

    private final List<medicationRowController> medicationControllers = new ArrayList<>();

    @FXML
    private void addMedicationRow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/medicationRow.fxml"));
            Node row = loader.load();
            medicationRowController controller = loader.getController();
            medicationControllers.add(controller);
            medicationRowsBox.getChildren().add(row);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void savePrescription() {
        String diagnosisText = diagnosis.getText();
        boolean allValid = true;

        StringBuilder prescriptionText = new StringBuilder();
        prescriptionText.append("Diagnosis: ").append(diagnosisText).append("\nMedications:\n");

        for (medicationRowController ctrl : medicationControllers) {
            medicationRowController.MedicationData medData = ctrl.getMedicationData();
            if (!Validator.isValidMedicationData(medData)) {
                allValid = false;
                System.out.println("Invalid medication data: " + medData.name);
                break;
            }
            prescriptionText.append("- ")
                    .append(medData.name)
                    .append(" [")
                    .append(medData.morning ? "Morning " : "")
                    .append(medData.noon ? "Noon " : "")
                    .append(medData.night ? "Night" : "")
                    .append("] ")
                    .append(medData.beforeAfterMeals)
                    .append(", Duration: ").append(medData.duration)
                    .append(". Comments: ").append(medData.comment)
                    .append("\n");
        }


        if (!allValid) {
            System.out.println("Please correct invalid medication entries before saving.");
            return;
        }

        String doctorName = client.getDoctorById(HMSRunner.getCurrentUserId()).getName();
        String patientName = allPatientsChoiceBox.getValue().toString();
        com.hms.model.Message message = new com.hms.model.Message();
        message.setRecipientId(client.getPatientByName(patientName).getId());
        message.setSenderName(doctorName);
        message.setContent(prescriptionText.toString());
        message.setTimestamp(java.time.LocalDateTime.now());
        message.setRead(false);

        try {
           // client.createMessage(message);
            NotificationServer.sendNotification(message.getRecipientId(),message);
            System.out.println("Prescription sent as message.");
            SceneSwitcher.switchSceneWithClient("/com/hms/myhospital/doctorDashboard.fxml", client);
        } catch (Exception e) {
            System.out.println("Failed to send prescription: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void cancelPrescription() {

        diagnosis.clear();
        medicationRowsBox.getChildren().clear();
        medicationControllers.clear();
        System.out.println("Prescription cancelled.");

        try {
            SceneSwitcher.switchScene("/com/hms/myhospital/doctorDashboard.fxml");
        } catch (IOException e) {
            System.out.println("Error cancelling prescription: " + e.getMessage());
            e.printStackTrace();
        }
    } //done

}
