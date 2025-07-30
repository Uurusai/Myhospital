package com.hms.myhospital;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class medicationRowController {
    @FXML private TextField medication;
    @FXML private CheckBox morningCheck;
    @FXML private CheckBox noonCheck;
    @FXML private CheckBox nightCheck;
    @FXML private ChoiceBox<String> beforeAfterMealsChoice;
    @FXML private ChoiceBox<String> durationChoice;
    @FXML private TextArea comment;

    @FXML public void initialize() {
        // Initialize choices for before/after meals and duration
        beforeAfterMealsChoice.getItems().addAll("Before Meals", "After Meals", "Anytime");
        beforeAfterMealsChoice.setValue("Anytime"); // Default value

        durationChoice.getItems().addAll("As per doctor's suggestion", "3 days", "1 week", "2 weeks", "1 month", "3 months", "6 months", "1 year");
        durationChoice.setValue("1 week"); // Default value

        medication.setText("Napa 500mg"); //haha
        comment.setText("None");
    }

    public MedicationData getMedicationData() {
        return new MedicationData(
            medication.getText(),
            morningCheck.isSelected(),
            noonCheck.isSelected(),
            nightCheck.isSelected(),
            beforeAfterMealsChoice.getValue(),
            durationChoice.getValue(),
            comment.getText()
        );
    }

    // public void setMedicationData(MedicationData data) {
    // }

    public static class MedicationData {
        public final String name, beforeAfterMeals, duration, comment;
        public final boolean morning, noon, night;

        public MedicationData(String name, boolean morning, boolean noon, boolean night,
                              String beforeAfterMeals, String duration, String comment) {
            this.name = name;
            this.morning = morning;
            this.noon = noon;
            this.night = night;
            this.beforeAfterMeals = beforeAfterMeals;
            this.duration = duration;
            this.comment = comment;
        }
    }
}
