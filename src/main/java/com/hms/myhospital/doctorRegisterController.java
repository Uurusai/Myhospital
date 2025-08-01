package com.hms.myhospital;

import com.hms.client.HMSClient;
import com.hms.model.DoctorSchedule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import com.hms.utils.SceneSwitcher;

import java.io.IOException;

import static com.hms.utils.Validator.*;
import com.hms.dao.DoctorDAO;
import com.hms.model.Doctor;
import javafx.stage.Stage;


public class doctorRegisterController {

    private final HMSClient client;
    public doctorRegisterController(HMSClient client) {
        this.client = client;
    }

    @FXML private TextField doctorName;
    @FXML private DatePicker doctorDateOfBirth;
    @FXML private TextField specialization;
    @FXML private TextField doctorPhoneNumber;
    @FXML private TextArea doctorAddress;
    @FXML private CheckBox doctorIsMale;
    @FXML private CheckBox doctorIsFemale;

    @FXML private void handleGenderSelection() {
        if (doctorIsMale.isSelected()) {
            doctorIsFemale.setSelected(false);
        } else if (doctorIsFemale.isSelected()) {
            doctorIsMale.setSelected(false);
        }
    }

    @FXML private TextField doctorEmail;
    @FXML private PasswordField doctorSetPassword;
    @FXML private PasswordField doctorConfirmPassword;

    @FXML private Label doctorPerInfoError;
    @FXML private Label doctorAccInfoError;


    @FXML private StackPane doctorRegisterBtn;
    @FXML private StackPane doctorRegisterCancelBtn;

    private boolean validateDoctorInfo() {
        // Personal Information Validation
        if (isNullOrEmpty(doctorName.getText())) {
            doctorPerInfoError.setText("First name required!");
            return false;
        }
        if (!isValidName(doctorName.getText())) {
            doctorPerInfoError.setText("Invalid first name!");
            return false;
        }

        if (doctorDateOfBirth.getValue() == null) {
            doctorPerInfoError.setText("Date of birth required!");
            return false;
        }
        if(!isValidDateOfBirth(doctorDateOfBirth.getValue().toString())) {
            doctorPerInfoError.setText("Invalid date of birth!");
            return false;
        }

        if (isNullOrEmpty(specialization.getText())) {
            doctorPerInfoError.setText("Specialization required!");
            return false;
        }

        if (isNullOrEmpty(doctorAddress.getText())) {
            doctorPerInfoError.setText("Address required!");
            return false;
        }

        if(!doctorIsMale.isSelected() && !doctorIsFemale.isSelected()) {
            doctorPerInfoError.setText("Gender selection is required.");
        }

//        if (isNullOrEmpty(doctorPhoneNumber.getText())) {
//            doctorPerInfoError.setText("Phone number required!");
//            return false;
//        }
//        if (!isValidPhoneNumber(doctorPhoneNumber.getText())) {
//            doctorPerInfoError.setText("Invalid phone number!");
//            return false;
//        }

        // Account Information Validation

        if (isNullOrEmpty(doctorEmail.getText())) {
            doctorAccInfoError.setText("Email address required!");
            return false;
        }
        if (!isValidEmailAddress(doctorEmail.getText())) {
            doctorAccInfoError.setText("Invalid email address!");
            return false;
        }

        if (isNullOrEmpty(doctorSetPassword.getText())) {
            doctorAccInfoError.setText("Set your password!");
            return false;
        }
        if (!isValidPassword(doctorSetPassword.getText())) {
            doctorAccInfoError.setText("Password must be at least 8 characters long (containing 0-9, a-z, A-Z and some special characters)");
            return false;
        }

        if (isNullOrEmpty(doctorConfirmPassword.getText())) {
            doctorAccInfoError.setText("Confirm your password!");
            return false;
        }
        if (!isValidPassword(doctorConfirmPassword.getText())) {
            doctorAccInfoError.setText("Password must be at least 8 characters long (containing 0-9, a-z, A-Z and some special characters)");
            return false;
        }

        if (!doctorSetPassword.getText().equals(doctorConfirmPassword.getText())) {
            doctorAccInfoError.setText("Passwords do not match!");
            return false;
        }

        return true;
    }

    @FXML private void handleDoctorRegister() {

        System.out.println("Doctor registration attempted!");

//        if(!validateDoctorInfo()) {
//            System.out.println("Sorry!");
//            return;
//        }

        //TODO: Database logic handling

        String name = doctorName.getText().trim();

        // Check if username already exists (case-insensitive)
        boolean usernameExists = client.getAllDoctors().stream()
                .anyMatch(d -> d.getName().equalsIgnoreCase(name));
        if (usernameExists) {
            doctorAccInfoError.setText("Username already exists!");
            return;
        }

        String hashedPassword = com.hms.utils.PasswordUtil.hashPassword(doctorSetPassword.getText());
        String gender = doctorIsMale.isSelected()? "Male" : "Female";
        String email = doctorEmail.getText().trim();
        String speciality = specialization.getText().trim();
        int contactNo = Integer.parseInt(doctorPhoneNumber.getText().trim());
        String address = doctorAddress.getText().trim();

        com.hms.model.Doctor newDoctor = new com.hms.model.Doctor(
                name, // or combine first and last name if needed
                0, // id will be set by DB
                gender,
                email,
                speciality,
                contactNo,
                address
        );

        newDoctor.setPassword(hashedPassword);
        newDoctor.setAccount_status("pending");

        boolean success = client.addDoctor(newDoctor);
        System.out.println(success);
        if (success) {
            try {
                Doctor savedDoctor = client.getDoctorByName(name);
                HMSRunner.setCurrentUser(savedDoctor.getId(),"doctor");
                // Load the doctor-schedule.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hms/myhospital/doctor-schedule.fxml"));
                Parent root = loader.load();

                // Get the controller and pass HMSClient + DoctorSchedule
                DoctorScheduleController scheduleController;
                try{
                     scheduleController = loader.getController();
                }catch(NullPointerException e){
                    System.out.println("Error: HMSClient or DoctorScheduleController is null.");
                    return;
                }


                scheduleController.initialize(client, new DoctorSchedule(savedDoctor.getId()));

                // Switch to the new scene
                Scene scene = new Scene(root);
                Stage stage = (Stage) doctorAccInfoError.getScene().getWindow(); // Assuming doctorAccInfoError is in the current scene
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error loading doctor schedule scene!");
            }
        } else {
            doctorAccInfoError.setText("Registration failed. Try again.");
        }
    }

    @FXML private void handleDoctorRegisterCancel() {

        System.out.println("Doctor register cancelled!");

        try {
            SceneSwitcher.switchScene("/com/hms/myhospital/welcome.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error switching to welcome scene!");
        }
    }
}
