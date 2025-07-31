package com.hms.myhospital;

import com.hms.client.HMSClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
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

    @FXML TextArea diagnosis;
    @FXML Button addMore;
    @FXML Button savePrescriptionBtn;
    @FXML Button cancelPrescriptionBtn;
    @FXML VBox medicationRowsBox;

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

        System.out.println("Prescription saved.");
        String diagnosisText = diagnosis.getText();
        List<medicationRowController.MedicationData> meds = new ArrayList<>(); // Collect medication data
        for (medicationRowController ctrl : medicationControllers) {
            meds.add(ctrl.getMedicationData());
        }

        //TODO: Logic to save then send that prescription to the patient
        //isValidMedicationData() to be used to validate the medication data

        // Log
        System.out.println("Diagnosis: " + diagnosisText);
        for (medicationRowController.MedicationData med : meds) {
            System.out.println("Medication: " + med.name + ", Morning: " + med.morning + ", Noon: " + med.noon + ", Night: " + med.night);
            System.out.println("Before/After Meals: " + med.beforeAfterMeals + ", Duration: " + med.duration + ", Comment: " + med.comment);
        }

        try {
            SceneSwitcher.switchScene("/com/hms/myhospital/doctorDashboard.fxml");

        } catch(IOException e) {
            System.out.println("Error saving prescription: " + e.getMessage());
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
