    package com.hms.myhospital;

    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.Initializable;
    import javafx.scene.control.Button;
    import javafx.scene.layout.AnchorPane;

    import java.net.URL;
    import java.util.ResourceBundle;


    public class AdminDash implements Initializable {

        @FXML private Button doctor_btn ;
        @FXML private Button db_btn ;
        @FXML private Button patient_btn ;
        @FXML private Button appointment_btn ;
        @FXML private AnchorPane patientsView;
        @FXML private AnchorPane doctorsView;
        @FXML private AnchorPane appointmentsView;
        @FXML private AnchorPane db_top;
        @FXML private AnchorPane db_btm;



        public void switchForm(ActionEvent event){
            if(event.getSource() == doctor_btn){
                setAllInvisible();
                doctorsView.setVisible(true);
            }
            else if(event.getSource() == patient_btn){
                setAllInvisible();
                patientsView.setVisible(true);
            }
            else if(event.getSource() == appointment_btn){
                setAllInvisible();
                appointmentsView.setVisible(true);
            }
            else if(event.getSource() == db_btn){
                setAllInvisible();
                db_btm.setVisible(true);
                db_top.setVisible(true);
            }
        }

        private void setAllInvisible() {
            db_btm.setVisible(false);
            db_top.setVisible(false);
            doctorsView.setVisible(false);
            patientsView.setVisible(false);
            appointmentsView.setVisible(false);
        }

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            setAllInvisible();
            db_btm.setVisible(false);
            db_top.setVisible(false);

        }
    }
