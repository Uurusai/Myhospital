package com.hms.client;

import com.hms.model.*;
import com.hms.server.commands.GenericDAOCommand;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class HMSClient {
    private final String serverAddress;
    private final int port;

    public HMSClient(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }


    //admin commands
    public boolean addAdmin(Admin admin){
        return executeCommand(new GenericDAOCommand<>(
                "admin", "addAdmin",
                new Object[]{admin},
                Boolean.class
        ));
    }
    public Admin getAdminByName(String name) {
        return executeCommand(new GenericDAOCommand<>(
                "admin", "getAdminByName",
                new Object[]{name},
                Admin.class
        ));
    }
    public List<Admin> searchAdminsByName(String name){
        return executeCommand(new GenericDAOCommand<>(
                "admin","searchAdminsByName",
                new Object[]{name},
                List.class
        ));
    }



    //doctor commands
    public boolean addDoctor(Doctor doctor) {
        return executeCommand(new GenericDAOCommand<>(
                "doctor", "addDoctor",
                new Object[]{doctor},
                Boolean.class
        ));
    }

    public Doctor getDoctorById(int id) {
        return executeCommand(new GenericDAOCommand<>(
                "doctor", "getDoctorById",
                new Object[]{id},
                Doctor.class
        ));
    }

    public List<Doctor> getAllDoctors() {
        return executeCommand(new GenericDAOCommand<>(
                "doctor", "getAllDoctors",
                new Object[]{},
                List.class
        ));
    }

    public boolean updateDoctor(Doctor doctor) {
        return executeCommand(new GenericDAOCommand<>(
                "doctor", "updateDoctor",
                new Object[]{doctor},
                Boolean.class
        ));
    }

    public boolean deleteDoctor(int id) {
        return executeCommand(new GenericDAOCommand<>(
                "doctor", "deleteDoctor",
                new Object[]{id},
                Boolean.class
        ));
    }
    public Doctor getDoctorByName(String name) {
        return executeCommand(new GenericDAOCommand<>(
                "doctor", "getDoctorByName",
                new Object[]{name},
                Doctor.class
        ));
    }

    public List<Patient> getPatientsForDoctor(int doctorId) {
        return executeCommand(new GenericDAOCommand<>(
                "doctor", "getPatientsForDoctor",
                new Object[]{doctorId},
                List.class
        ));
    }

    public List<Appointment> getAppointmentsForDoctor(int doctorId) {
        return executeCommand(new GenericDAOCommand<>(
                "doctor", "getAppointmentsForDoctor",
                new Object[]{doctorId},
                List.class
        ));
    }

    public List<Doctor> searchDoctors(String query) {
        return executeCommand(new GenericDAOCommand<>(
                "doctor", "searchDoctors",
                new Object[]{query},
                List.class
        ));
    }



    //doctor-schedule commands
    public void setWorkingDays(DoctorSchedule ds, List<Integer> offdays, LocalTime starting_hour, LocalTime ending_hour) {
        executeCommand(new GenericDAOCommand<>(
                "schedule", "setWorkingDays",
                new Object[]{ds, offdays, starting_hour, ending_hour},
                Void.class
        ));
    }
    public DoctorSchedule getDoctorSchedule(Doctor doctor) {
        return executeCommand(new GenericDAOCommand<>(
                "schedule", "getDoctorSchedule",
                new Object[]{doctor},
                DoctorSchedule.class
        ));
    }
    public boolean isDoctorAvailable(DoctorSchedule ds, int day, LocalTime time) {
        return executeCommand(new GenericDAOCommand<>(
                "schedule", "isDoctorAvailable",
                new Object[]{ds, day, time},
                Boolean.class
        ));
    }

    public void goOnBreak(int doc_id, String breakDuration) {
        executeCommand(new GenericDAOCommand<>(
                "schedule", "goOnBreak",
                new Object[]{doc_id, breakDuration},
                Void.class
        ));
    }
    public void sendMessageToAll(int doc_id, TimeDateRange break_time, String breakDuration) {
        executeCommand(new GenericDAOCommand<>(
                "schedule", "sendMessageToAll",
                new Object[]{doc_id, break_time, breakDuration},
                Void.class
        ));
    }
    public void postPoneAppointments(int doc_id, TimeDateRange break_time, String breakDuration) {
        executeCommand(new GenericDAOCommand<>(
                "schedule", "postPoneAppointments",
                new Object[]{doc_id, break_time, breakDuration},
                Void.class
        ));
    }



    //patient commands
    public boolean addPatient(Patient p) {
        return executeCommand(new GenericDAOCommand<>(
                "patient", "addPatient",
                new Object[]{p},
                Boolean.class
        ));
    }
    public List<Patient> getAllPatients() {
        return executeCommand(new GenericDAOCommand<>(
                "patient", "getAllPatients",
                new Object[]{},
                List.class
        ));
    }
    public Patient getPatientbyId(int id) {
        return executeCommand(new GenericDAOCommand<>(
                "patient", "getPatientbyId",
                new Object[]{id},
                Patient.class
        ));
    }
    public Patient getPatientByName(String name) {
        return executeCommand(new GenericDAOCommand<>(
                "patient", "getPatientByName",
                new Object[]{name},
                Patient.class
        ));
    }
    public boolean updatePatient(Patient patient) {
        return executeCommand(new GenericDAOCommand<>(
                "patient", "updatePatient",
                new Object[]{patient},
                Boolean.class
        ));
    }

    public boolean deletePatient(int id) {
        return executeCommand(new GenericDAOCommand<>(
                "patient", "deletePatient",
                new Object[]{id},
                Boolean.class
        ));
    }
    public List<Appointment> getAppointmentsForPatient(int patientId) {
        return executeCommand(new GenericDAOCommand<>(
                "patient", "getAppointmentsForPatient",
                new Object[]{patientId},
                List.class
        ));
    }
    public List<Appointment> getPendingAppointmentsForPatient(int patientId) {
        return executeCommand(new GenericDAOCommand<>(
                "patient", "getPendingAppointmentsForPatient",
                new Object[]{patientId},
                List.class
        ));
    }
    public List<Doctor> getDoctorsForPatient(int patientId) {
        return executeCommand(new GenericDAOCommand<>(
                "patient", "getDoctorsForPatient",
                new Object[]{patientId},
                List.class
        ));
    }



    //appointment commands
    public boolean addAppointment(Appointment app) {
        return executeCommand(new GenericDAOCommand<>(
                "appointment", "addAppointment",
                new Object[]{app},
                Boolean.class
        ));
    }

    public boolean updateAppointment(Appointment app) {
        return executeCommand(new GenericDAOCommand<>(
                "appointment", "updateAppointment",
                new Object[]{app},
                Boolean.class
        ));
    }

    public boolean markCompleted(Appointment app) {
        return executeCommand(new GenericDAOCommand<>(
                "appointment", "markCompleted",
                new Object[]{app},
                Boolean.class
        ));
    }

    public boolean confirmAppointment(int appointmentId, LocalDateTime newScheduledDate) {
        return executeCommand(new GenericDAOCommand<>(
                "appointment", "confirmAppointment",
                new Object[]{appointmentId, newScheduledDate},
                Boolean.class
        ));
    }

    public boolean rejectAppointment(int appointmentId) {
        return executeCommand(new GenericDAOCommand<>(
                "appointment", "rejectAppointment",
                new Object[]{appointmentId},
                Boolean.class
        ));
    }

    public boolean rescheduleAppointment(int appointmentId, LocalDateTime newScheduledDate) {
        return executeCommand(new GenericDAOCommand<>(
                "appointment", "rescheduleAppointment",
                new Object[]{appointmentId, newScheduledDate},
                Boolean.class
        ));
    }

    public Appointment getAppointmentById(int patientId, int doctorId) {
        return executeCommand(new GenericDAOCommand<>(
                "appointment", "getAppointmentById",
                new Object[]{patientId, doctorId},
                Appointment.class
        ));
    }

    public boolean autoscheduleAppointment(int patientId, int doctorId,String symptoms) {
        return executeCommand(new GenericDAOCommand<>(
                "appointment", "autoscheduleAppointment",
                new Object[]{patientId, doctorId,symptoms},
                Boolean.class
        ));
    }

    public boolean isAppointmentAvailable(LocalDateTime appointmentTime) {
        return executeCommand(new GenericDAOCommand<>(
                "appointment", "isAppointmentAvailable",
                new Object[]{appointmentTime},
                Boolean.class
        ));
    }

    public void sendAppointmentConfirmation(int appointmentId, LocalDateTime scheduledDate) {
        executeCommand(new GenericDAOCommand<>(
                "appointment", "sendAppointmentConfirmation",
                new Object[]{appointmentId, scheduledDate},
                Void.class
        ));
    }

    public void sendPrescription(int patientId,String doctorName, String prescriptionFilePath) {
        executeCommand(new GenericDAOCommand<>(
                "appointment", "sendPrescription",
                new Object[]{patientId, doctorName,prescriptionFilePath},
                Void.class
        ));
    }

    public Appointment getAppointmentByOneId(int appointmentId) {
        return executeCommand(new GenericDAOCommand<>(
                "appointment", "getAppointmentByOneId",
                new Object[]{appointmentId},
                Appointment.class
        ));
    }

    public List<Appointment> getAppointmentInTimeRange(TimeDateRange week){
        return executeCommand(new GenericDAOCommand<>(
                "appointment", "getAppointmentInTimeRange",
                new Object[]{week},
                List.class
        ));
    }

    public List<Appointment>getAllAppointments(){
        return executeCommand(new GenericDAOCommand<>(
                "appointment", "getAllAppointments",
                new Object[]{ },
                List.class
        ));
    }


    //message commands
    public List<Message> getUnreadMessages(int userId) throws SQLException {
        return executeCommand(new GenericDAOCommand<>(
                "message",
                "getUnreadMessages",
                new Object[]{userId},
                List.class
        ));
    }
    public int createMessage(Message message) throws SQLException {
        return executeCommand(new GenericDAOCommand<>(
                "message",
                "createMessage",
                new Object[]{message},
                Integer.class
        ));
    }
    public List<Message> getMessagesByRecipient(int recipientId) throws SQLException {
        return executeCommand(new GenericDAOCommand<>(
                "message",
                "getMessagesByRecipient",
                new Object[]{recipientId},
                List.class
        ));
    }
    public boolean markAsRead(int messageId) throws SQLException {
        return executeCommand(new GenericDAOCommand<>(
                "message",
                "markAsRead",
                new Object[]{messageId},
                Boolean.class
        ));
    }
    public boolean deleteMessage(int messageId) throws SQLException {
        return executeCommand(new GenericDAOCommand<>(
                "message",
                "deleteMessage",
                new Object[]{messageId},
                Boolean.class
        ));
    }




    private <T> T executeCommand(GenericDAOCommand<T> command) {
        try (Socket socket = new Socket(serverAddress, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            oos.writeObject(command);
            return (T) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Remote operation failed", e);
        }
    }


}