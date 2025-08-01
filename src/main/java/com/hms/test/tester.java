package com.hms.test;

import com.hms.client.HMSClient;
import com.hms.model.Appointment;
import com.hms.model.Doctor;
import com.hms.model.Patient;
import com.hms.model.TimeDateRange;
import com.hms.server.HMSServer;
import com.hms.threads.BreakEnder;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.util.List;

public class tester{
    public static void main(String[] args) {
        // Connect to the server (adjust host/port as needed)
        new Thread(()->new HMSServer().start()).start() ;
        HMSClient client = new HMSClient("192.168.0.104", 12345);

        int doctorId = 18;
        int patientId = 11;
//        List<Appointment> allAppointments = client.getAllAppointments();
//        for(Appointment app : allAppointments){
//            System.out.println("Appointment ID: " + app.getId() + ", Patient ID: " + app.getPatient().getId() + ", Doctor ID: " + app.getDoctor().getId() + ", Date Requested: " + app.getDate_requested());
//        }

        // Test autoscheduleAppointment
        boolean autoScheduled = client.autoscheduleAppointment(patientId, doctorId,"Headache and nausea,severe pain in forehead,irregular bowel activities");
        System.out.println("Auto-schedule appointment: " + (autoScheduled ? "Success" : "Failed"));

       //testing confirm appointment
        //Appointment app = client.getAppointmentById(patientId, doctorId);
        //client.markCompleted(app);
        //client.confirmAppointment(app.getId(),app.getDate_requested());
        // Test goOnBreak (assuming this method exists in your HMSClient)
          //client.sendMessageToAll(14,new TimeDateRange(LocalDateTime.now(), LocalDateTime.now().plusDays(3)),"30 minute");
            // client.goOnBreak(doctorId, "3 day");
            //new BreakEnder(doctorId, new TimeDateRange(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1)));

        // Test sendPrescription
       // String prescriptionFilePath = "C:/path/to/prescription.pdf"; // Replace with a valid file path
       //client.sendPrescription(patientId, "Smith",prescriptionFilePath);
        //System.out.println("Prescription sent to patient " + patientId);
    }
}