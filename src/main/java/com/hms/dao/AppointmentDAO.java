package com.hms.dao;

import com.hms.model.*;
import com.hms.notification.NotificationServer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.hms.dao.DatabaseConnection.getConnection;

public class AppointmentDAO {

    //adding appointment
    public boolean addAppointment(Appointment app) {
        String sql = "INSERT INTO appointments (doctor_id, patient_id,date_requested,symptoms,complete_status,status,diagnosis) " +
                "VALUES (  ?, ?,?,?,'pending','requested','pending')";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            //use setter after creating Appointment object in controller to set symptoms
            stmt.setInt(1,app.getDoctor().getId());
            stmt.setInt(2,app.getPatient().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(app.getDate_requested()));
            stmt.setString(4,app.getSymptoms());


            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //update appointment info
    public boolean updateAppointment(Appointment app) {
        String sql = "UPDATE appointments SET doctor_id = ?, patient_id = ?, date_requested = ?, " +
                "date_scheduld = ?, date_made = ?, status = ? WHERE appointment_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, app.getDoctor().getId());
            stmt.setInt(2, app.getPatient().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(app.getDate_requested()));
            stmt.setTimestamp(4, Timestamp.valueOf(app.getDate_scheduled()));
            stmt.setTimestamp(5, Timestamp.valueOf(app.getDate_made()));
            stmt.setString(6, app.getStatus());
            stmt.setInt(7, app.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markCompleted(Appointment app){
        String sql = "UPDATE appointments SET complete_status = 'completed' WHERE appointment_id = ?";

        try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
            stmt.setInt(1,app.getId());

            return stmt.executeUpdate() > 0 ;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //confirm appointment
    public boolean confirmAppointment(int appointmentId, LocalDateTime newScheduledDate) {
        String sql = "UPDATE appointments SET date_scheduled = ?,status = 'confirmed' WHERE appointment_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(newScheduledDate));
            stmt.setInt(2, appointmentId);

            boolean success = stmt.executeUpdate() > 0;

            if (success) {
                sendAppointmentConfirmation(appointmentId, newScheduledDate);
            }

            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //reject appointment
    public boolean rejectAppointment(int appointmentId) {
        String sql = "UPDATE appointments SET status = 'rejected' WHERE appointment_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(2, appointmentId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //reschedule appointment
    public boolean rescheduleAppointment(int appointmentId, LocalDateTime newScheduledDate) {
        String sql = "UPDATE appointments SET date_scheduld = ?,status = 'rescheduled' WHERE appointment_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(newScheduledDate));
            stmt.setInt(2, appointmentId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //autoschedule appointments
    public boolean autoscheduleAppointment(int patientId, int doctor_id) {

        DoctorDAO dd = new  DoctorDAO();
        DoctorScheduleDAO dsd = new DoctorScheduleDAO() ;
        DoctorSchedule ds = dsd.getDoctorSchedule(dd.getDoctorById(doctor_id));

        LocalDateTime now = LocalDateTime.now() ;

        for(int daysAdded = 0 ;daysAdded <30 ;daysAdded++){

            LocalDate dt = now.toLocalDate().plusDays(daysAdded);

            int day_of_week = dt.getDayOfWeek().getValue();

            if(day_of_week <= ds.getWorkdays().size()){
                TimeRange working_hour = ds.getWorkdays().get(day_of_week - 1);
                if(working_hour != null){
                    LocalTime start = working_hour.getStartTime();
                    LocalTime end = working_hour.getEndTime();
                    LocalTime current = start ;

                    while(current.isBefore(end)){

                        LocalDateTime proposed_schedule = LocalDateTime.of(dt,current);

                        if(isAppointmentAvailable(proposed_schedule) && dsd.isDoctorAvailable(ds,day_of_week,current)){

                            Appointment appointment = new Appointment();
                            PatientDAO pd = new PatientDAO() ;
                            appointment.setPatient(pd.getPatientbyId(patientId));
                            appointment.setDoctor(dd.getDoctorById(doctor_id));
                            appointment.setDate_requested(proposed_schedule);

                            return addAppointment(appointment);
                        }

                        current = current.plusMinutes(15);
                    }
                }
            }
        }

        return false;
    }

    public boolean isAppointmentAvailable(LocalDateTime appointmentTime) {

        String sql = "SELECT appointment_id FROM appointments where date_scheduled = ? OR date_requested = ?";
        try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
            stmt.setTimestamp(1, Timestamp.valueOf(appointmentTime));
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){

                return false ;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return true ;
    }

    //sending confirmation message
    public void sendAppointmentConfirmation(int appointmentId, LocalDateTime scheduledDate) {
            try {
                Appointment app = getAppointmentById(appointmentId);
                int patientId = app.getPatient().getId();
                int doctorId = app.getDoctor().getId();
                String doctorName = app.getDoctor().getName();

                Message confirmation = new Message();
                confirmation.setRecipientId(patientId);
                confirmation.setSenderName(doctorName);
                confirmation.setContent(String.format(
                        "Your appointment has been confirmed for %s at %s",
                        scheduledDate.toLocalDate(),
                        scheduledDate.toLocalTime()
                ));
                confirmation.setTimestamp(LocalDateTime.now());
                confirmation.setRead(false);
                MessageDAO md = new MessageDAO();
                md.createMessage(confirmation);
                NotificationServer.sendNotification(patientId, confirmation);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //sending prescription
        public void sendPrescription(int appointmentID, String prescriptionFilePath) {
            try {
                // Get appointment details
                Appointment app = getAppointmentById(appointmentID);
                int patientId = app.getPatient().getId();
                int doctorId = app.getDoctor().getId();
                String doctorName = app.getDoctor().getName();

                Message confirmation = new Message();
                confirmation.setRecipientId(patientId);
                confirmation.setSenderName(doctorName);
                confirmation.setContent(String.format(
                        " your prescription has been sent for appointment no %d", appointmentID
                ));
                confirmation.setTimestamp(LocalDateTime.now());


                if (prescriptionFilePath != null) {
                    Path path = Paths.get(prescriptionFilePath);
                    String fileName = path.getFileName().toString();
                    byte[] fileData = Files.readAllBytes(path);

                    confirmation.setAttachmentName(fileName);
                    confirmation.setAttachmentData(fileData);
                }
                NotificationServer.sendNotification(patientId, confirmation);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    public Appointment getAppointmentById(int appointmentId) {
        //List<Appointment> appointments = new ArrayList<>();
        String sql = """
            SELECT 
                a.appointment_id,a.date_made,a.date_requested, a.date_scheduled,a.status,a.symptoms,a.completed_status,a.diagnosis
                
                -- patient fields
                p.patient_id,p.name AS patient_name, p.gender AS patient_gender, p.age AS patient_age,
                p.date_of_birth AS patient_date_of_birth, p.address AS patient_address, p.contact_no AS patient_contact
                p.payment_status AS patient_payment_status, p.visitor_type AS patient_visitor_type
            
                --doctor fields
                d.doctor_id, d.name AS doctor_name, d.gender AS doctor_gender, d.e-mail,
                d.speciality,d.contact no AS doctor_contact d.addrress AS doctor_address
            
            FROM appointments a
            JOIN Patients p ON a.patient_id = p.patient_id
            JOIN Doctors d ON a.doctor_id = d.doctor_id
            WHERE a.appointment_id = ?
                       
""";
        try(Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,appointmentId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                Patient patient = new Patient(
                        rs.getString("name"),
                        rs.getInt("patient_id"),
                        rs.getString("gender"),
                        rs.getInt("age"),
                        rs.getString("date_of_birth"),
                        rs.getInt("contact_no"),
                        rs.getString("address"),
                        rs.getString("blood_type")
                );
                Doctor doctor = new  Doctor(
                        rs.getString("doctor_name"),
                        rs.getInt("doctor_id"),
                        rs.getString("doctor_gender"),
                        rs.getString("e-mail"),
                        rs.getString("speciality"),
                        rs.getInt("doctor_contact"),
                        rs.getString("doctor_address")
                );
                Appointment appointment = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getTimestamp("date_made").toLocalDateTime(),
                        rs.getTimestamp("date_requested").toLocalDateTime(),
                        rs.getTimestamp("date_scheduled").toLocalDateTime(),
                        patient,doctor,
                        rs.getString("status")
                );
                appointment.setComplete(rs.getBoolean("completed_status"));
                appointment.setDiagnosis(rs.getString("diagnosis"));
                appointment.setSymptoms(rs.getString("symptoms"));
                return appointment;
            }

        }catch(SQLException e){
            e.printStackTrace();
           // return null;
        }
       return null;
    }
    public List<Appointment> getAppointmentInTimeRange(TimeDateRange week){
        List<Appointment> apps = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE date_scheduled BETWEEN ? AND ?";

        try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
            stmt.setTimestamp(1, Timestamp.valueOf(week.getStart()));
            stmt.setTimestamp(2, Timestamp.valueOf(week.getEnd()));
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Appointment a = new Appointment();
                a.setId(rs.getInt("appointment_id"));
                apps.add(a);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return apps;
    }

}
