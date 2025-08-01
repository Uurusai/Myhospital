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
        String sql = "INSERT INTO appointments (doctor_id, patient_id,date_requested,symptoms,date_created,complete_status,status) " +
                "VALUES (  ?, ?,?,?,?,'pending','requested')";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            //use setter after creating Appointment object in controller to set symptoms
            stmt.setInt(1,app.getDoctor().getId());
            stmt.setInt(2,app.getPatient().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(app.getDate_requested()));
            stmt.setString(4,app.getSymptoms());
            stmt.setTimestamp(5,Timestamp.valueOf(LocalDateTime.now()));


            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<Appointment>getAllAppointments(){
        List<Appointment> appointments = new ArrayList<>();
        String sql = """
            SELECT 
                a.appointment_id,a.date_created,a.date_requested, a.date_scheduled,a.status,a.symptoms,a.complete_status,
                
                -- patient fields
                p.patient_id,p.name AS patient_name, p.gender AS patient_gender, p.age AS patient_age,
                p.date_of_birth AS patient_date_of_birth, p.address AS patient_address, p.contact_no AS patient_contact,
                p.blood_type,
                --doctor fields
                d.doctor_id, d.name AS doctor_name, d.gender AS doctor_gender, d.email,
                d.speciality,d.contact_no AS doctor_contact,d.addrress AS doctor_address
            
            FROM appointments a
            JOIN Patients p ON a.patient_id = p.patient_id
            JOIN Doctors d ON a.doctor_id = d.doctor_id
                       
""";
        try(Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Patient patient = new Patient(
                        rs.getString("patient_name"),
                        rs.getInt("patient_id"),
                        rs.getString("patient_gender"),
                        rs.getInt("patient_age"),
                        rs.getTimestamp("patient_date_of_birth").toString(),
                        rs.getInt("patient_contact"),
                        rs.getString("patient_address"),
                        rs.getString("blood_type")
                );
                Doctor doctor = new  Doctor(
                        rs.getString("doctor_name"),
                        rs.getInt("doctor_id"),
                        rs.getString("doctor_gender"),
                        rs.getString("email"),
                        rs.getString("speciality"),
                        rs.getInt("doctor_contact"),
                        rs.getString("doctor_address")
                );
                //LocalDateTime dateCreated = rs.getTimestamp("date_created").toLocalDateTime();
                //LocalDateTime dateRequested = rs.getTimestamp("date_requested").toLocalDateTime();
                LocalDateTime dateScheduled = rs.getTimestamp("date_scheduled") != null ? rs.getTimestamp("date_scheduled").toLocalDateTime() : null;
                LocalDateTime dateCreated = rs.getTimestamp("date_created") != null ? rs.getTimestamp("date_created").toLocalDateTime() : null;
                Appointment appointment = new Appointment(
                        rs.getInt("appointment_id"),
                        dateCreated,
                        rs.getTimestamp("date_requested").toLocalDateTime(),
                        dateScheduled,
                        patient,doctor,
                        rs.getString("status")
                );
                appointment.setComplete(rs.getString("complete_status"));
               // appointment.setDate_made(rs.getTimestamp("date_created").toLocalDateTime());
                appointment.setSymptoms(rs.getString("symptoms"));
                appointments.add(appointment);
            }

        }catch(SQLException e){
            e.printStackTrace();
            // return null;
        }
        return appointments;
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

    public Appointment getAppointmentById(int patientId, int doctorId){
        List<Appointment> appointments = new ArrayList<>();
        String sql = """
            SELECT 
                a.appointment_id,a.date_created,a.date_requested, a.date_scheduled,a.status,a.symptoms,a.complete_status,
                
                -- patient fields
                p.patient_id,p.name AS patient_name, p.gender AS patient_gender, p.age AS patient_age,
                p.date_of_birth AS patient_date_of_birth, p.address AS patient_address, p.contact_no AS patient_contact,
                p.blood_type,
                --doctor fields
                d.doctor_id, d.name AS doctor_name, d.gender AS doctor_gender, d.email,
                d.speciality,d.contact_no AS doctor_contact,d.addrress AS doctor_address
            
            FROM appointments a
            JOIN Patients p ON a.patient_id = p.patient_id
            JOIN Doctors d ON a.doctor_id = d.doctor_id
            WHERE a.doctor_id = ? AND a.patient_id = ?  AND a.complete_status = ?                   
""";
        try(Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,doctorId);
            stmt.setInt(2,patientId);
            stmt.setString(3,"pending");
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                Patient patient = new Patient(
                        rs.getString("patient_name"),
                        rs.getInt("patient_id"),
                        rs.getString("patient_gender"),
                        rs.getInt("patient_age"),
                        rs.getTimestamp("patient_date_of_birth").toString(),
                        rs.getInt("patient_contact"),
                        rs.getString("patient_address"),
                        rs.getString("blood_type")
                );
                Doctor doctor = new  Doctor(
                        rs.getString("doctor_name"),
                        rs.getInt("doctor_id"),
                        rs.getString("doctor_gender"),
                        rs.getString("email"),
                        rs.getString("speciality"),
                        rs.getInt("doctor_contact"),
                        rs.getString("doctor_address")
                );
                LocalDateTime dateScheduled = rs.getTimestamp("date_scheduled") != null ? rs.getTimestamp("date_scheduled").toLocalDateTime() : null;
                Appointment appointment = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getTimestamp("date_created").toLocalDateTime(),
                        rs.getTimestamp("date_requested").toLocalDateTime(),
                        dateScheduled,
                        patient,doctor,
                        rs.getString("status")
                );
                appointment.setComplete(rs.getString("complete_status"));
               // appointment.setDate_made(rs.getTimestamp("date_created").toLocalDateTime());
                appointment.setSymptoms(rs.getString("symptoms"));
                appointments.add(appointment);
            }

        }catch(SQLException e){
            e.printStackTrace();
            // return null;
        }
        return appointments.stream().sorted((a1, a2) -> a2.getDate_made().compareTo(a1.getDate_made()))
                .findFirst()
                .orElse(null);

    }

    public boolean markCompleted(Appointment app){
        String sql = "UPDATE appointments SET complete_status = ? WHERE appointment_id = ?";

        try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
            stmt.setString(1, "completed");
            stmt.setInt(2,app.getId());

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

            stmt.setInt(1, appointmentId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //reschedule appointment
    public boolean rescheduleAppointment(int appointmentId, LocalDateTime newScheduledDate) {
        String sql = "UPDATE appointments SET date_scheduled = ?,status = 'rescheduled' WHERE appointment_id = ?";

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
    public boolean autoscheduleAppointment(int patientId, int doctor_id,String symptoms) {

        DoctorDAO dd = new  DoctorDAO();
        DoctorScheduleDAO dsd = new DoctorScheduleDAO() ;
        DoctorSchedule ds = dsd.getDoctorSchedule(dd.getDoctorById(doctor_id));
        //System.out.println("HERE!");

        LocalDateTime now = LocalDateTime.now() ;

        for(int daysAdded = 0 ;daysAdded <30 ;daysAdded++){

            LocalDate dt = now.toLocalDate().plusDays(daysAdded);

            int day_of_week = dt.getDayOfWeek().getValue();

            if(day_of_week <= ds.getWorkdays().size()){
               // System.out.println("Checking daysss");
                TimeRange working_hour = ds.getWorkdays().get(day_of_week - 1);
                if(working_hour != null){
                    LocalTime start = working_hour.getStartTime();
                    LocalTime end = working_hour.getEndTime();
                    if (dt.equals(now.toLocalDate())) {
                        if (now.toLocalTime().isAfter(end)) {
                            continue;
                        } else if (now.toLocalTime().isAfter(start)) {
                            start = now.toLocalTime().withSecond(0).withNano(0);
                        }
                    }
                    LocalTime current = start ;

                    while(current.isBefore(end)){

                        LocalDateTime proposed_schedule = LocalDateTime.of(dt,current);

                        if(isAppointmentAvailable(proposed_schedule) && dsd.isDoctorAvailable(ds,day_of_week,current)){

                            Appointment appointment = new Appointment();
                            PatientDAO pd = new PatientDAO() ;
                            appointment.setPatient(pd.getPatientbyId(patientId));
                            appointment.setDoctor(dd.getDoctorById(doctor_id));
                            appointment.setDate_requested(proposed_schedule);
                            appointment.setSymptoms(symptoms);
                            System.out.println("Here!!!!");

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
            stmt.setTimestamp(2, Timestamp.valueOf(appointmentTime));
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
                Appointment app = getAppointmentByOneId(appointmentId);
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
        public void sendPrescription(int patientId,String doctorName, String prescriptionFilePath) {
            try {
                Message confirmation = new Message();
                confirmation.setRecipientId(patientId);
                confirmation.setSenderName(doctorName);
                confirmation.setContent(String.format(
                        " your prescription has been sent!"
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

    public Appointment getAppointmentByOneId(int appointmentId) {
        //List<Appointment> appointments = new ArrayList<>();
        String sql = """
            SELECT 
                a.appointment_id,a.date_created,a.date_requested, a.date_scheduled,a.status,a.symptoms,a.complete_status,
                
                -- patient fields
                p.patient_id,p.name AS patient_name, p.gender AS patient_gender, p.age AS patient_age,
                p.date_of_birth AS patient_date_of_birth, p.address AS patient_address, p.contact_no AS patient_contact,
                p.blood_type,
            
                --doctor fields
                d.doctor_id, d.name AS doctor_name, d.gender AS doctor_gender, d.email,
                d.speciality,d.contact_no AS doctor_contact ,d.addrress AS doctor_address
            
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
                        rs.getString("patient_name"),
                        rs.getInt("patient_id"),
                        rs.getString("patient_gender"),
                        rs.getInt("patient_age"),
                        rs.getTimestamp("patient_date_of_birth").toString(),
                        rs.getInt("patient_contact"),
                        rs.getString("patient_address"),
                        rs.getString("blood_type")
                );
                Doctor doctor = new  Doctor(
                        rs.getString("doctor_name"),
                        rs.getInt("doctor_id"),
                        rs.getString("doctor_gender"),
                        rs.getString("email"),
                        rs.getString("speciality"),
                        rs.getInt("doctor_contact"),
                        rs.getString("doctor_address")
                );
                LocalDateTime dateScheduled = rs.getTimestamp("date_scheduled") != null ? rs.getTimestamp("date_scheduled").toLocalDateTime() : null;
                Appointment appointment = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getTimestamp("date_created").toLocalDateTime(),
                        rs.getTimestamp("date_requested").toLocalDateTime(),
                        dateScheduled,
                        patient,doctor,
                        rs.getString("status")
                );
                appointment.setComplete(rs.getString("complete_status"));
                //appointment.setDiagnosis(rs.getString("diagnosis"));
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
