package com.hms.dao;

import com.hms.model.*;
import com.hms.notification.NotificationServer;
import com.hms.threads.BreakEnder;
import com.hms.utils.DurationParser;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

import static com.hms.dao.DatabaseConnection.getConnection;

public class DoctorScheduleDAO {

    public void setWorkingDays(DoctorSchedule ds ,List<Integer> offdays, LocalTime starting_hour, LocalTime ending_hour){
        for(int i=0;i<7;i++){
            if(!offdays.contains(i)){
                String sql = "INSERT INTO doctor_schedule (doctor_id,day_of_the_week, starting_hour, ending_hour) VALUES (?, ?,?, ?)";

                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setInt(1, ds.getDoctor_id());
                    stmt.setInt(2, i);
                    stmt.setTimestamp(3, Timestamp.valueOf(LocalDate.now().atTime(starting_hour)));
                    stmt.setTimestamp(4, Timestamp.valueOf(LocalDate.now().atTime(ending_hour)));
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public DoctorSchedule getDoctorSchedule(Doctor doctor) {

        String sql = "SELECT day_of_the_week, starting_hour, ending_hour, " +
                "\"isDoctorOnBreak\", \"breakStart\", \"breakDuration\" " +
                "FROM doctor_schedule WHERE doctor_id = ?";

        HashMap<Integer, TimeRange> workdays = new HashMap<>();
        boolean isDoctorOnBreak = false;
        TimeDateRange breakTimeRange = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctor.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int day = rs.getInt("day_of_the_week");
                    LocalTime start = rs.getTime("starting_hour").toLocalTime();
                    LocalTime end = rs.getTime("ending_hour").toLocalTime();
                    isDoctorOnBreak = rs.getBoolean("isDoctorOnBreak");
                    if (isDoctorOnBreak && !rs.wasNull()) {
                        Timestamp breakStart = rs.getTimestamp("breakStart");
                        // In getDoctorSchedule() method
                        String breakDurationStr = rs.getString("breakDuration");
                        Duration breakDuration = null;
                        if (breakDurationStr != null) {
                            breakDuration = DurationParser.parseFlexibleDuration(breakDurationStr);
                        }
                       // Duration breakDuration = rs.getObject("breakDuration", Duration.class);

                        if (breakStart != null && breakDuration != null) {
                            LocalDateTime breakStartTime = breakStart.toLocalDateTime();
                            LocalDateTime breakEndTime = breakStartTime.plus(breakDuration);
                            breakTimeRange = new TimeDateRange(breakStartTime, breakEndTime);
                        }
                    }
                    workdays.put(day, new TimeRange(start, end));
                }
                return new DoctorSchedule(doctor.getId(), workdays, isDoctorOnBreak, breakTimeRange);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isDoctorAvailable(DoctorSchedule ds ,int day , LocalTime time){
        if(ds.isDoctorOnBreak()) return false;
        HashMap<Integer,TimeRange> workdays = ds.getWorkdays();
        if(workdays.containsKey(day)){
            if(time.isBefore(workdays.get(day).getEndTime() )&& time.isAfter(workdays.get(day).getStartTime())){
                return true ;
            }
        }
        return false ;
    }

    public void goOnBreak(int doc_id, String breakDuration){
        String sql  = "UPDATE doctor_schedule SET  \"isDoctorOnBreak\" = 'TRUE', \"breakDuration\" = ?, \"breakStart\" = ? WHERE doctor_id = ?";
        try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
                stmt.setInt(3,doc_id);
                stmt.setString(1,breakDuration);
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                stmt.executeUpdate();
            DoctorDAO ddao = new DoctorDAO();
            DoctorSchedule ds = getDoctorSchedule(ddao.getDoctorById(doc_id));
            sendMessageToAll(ds.getDoctor_id(),ds.getBreakDuration(),breakDuration);
            postPoneAppointments(ds.getDoctor_id(),ds.getBreakDuration(),breakDuration);
           new BreakEnder(ds.getDoctor_id(),ds.getBreakDuration());

        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void sendMessageToAll(int doc_id, TimeDateRange break_time,String BreakDuration){
        System.out.println("Looking for appointments between " +
                break_time.getStart() + " and " + break_time.getEnd());

        String sql = "SELECT appointment_id, date_scheduled,patient_id FROM appointments " +
                "WHERE doctor_id = ? AND date_scheduled BETWEEN ? AND ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, doc_id);
            stmt.setTimestamp(2, Timestamp.valueOf(break_time.getStart()));
            stmt.setTimestamp(3, Timestamp.valueOf(break_time.getEnd()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Entering message query");
                Appointment app = new Appointment();
                app.setId(rs.getInt("appointment_id"));
                app.setDate_scheduled(rs.getTimestamp("date_scheduled").toLocalDateTime());
                System.out.println("Appointment ID: " + app.getId() + ", Scheduled Time: " + app.getDate_scheduled());
                LocalDateTime newTime = app.getDate_scheduled().plus(DurationParser.parseFlexibleDuration(BreakDuration));
                System.out.println("New Time: " + newTime);
                DoctorDAO dd = new DoctorDAO();
                Message confirmation = new Message();
                int patientId = rs.getInt("patient_id");
                confirmation.setRecipientId(patientId);
                confirmation.setSenderName(dd.getDoctorById(doc_id).getName());
                confirmation.setContent(String.format(
                        "Your appointment has been rescheduled to %s due to doctor unavailability.The new time is %s at %s.",
                        newTime.toLocalDate(),
                        newTime.toLocalTime(),
                        newTime.getDayOfWeek().toString().toLowerCase()
                ));
                confirmation.setTimestamp(LocalDateTime.now());

                NotificationServer.sendNotification(patientId, confirmation);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void postPoneAppointments(int doc_id, TimeDateRange break_time, String breakDuration) {
        System.out.println("Looking for appointments between " +
                break_time.getStart() + " and " + break_time.getEnd());

        String selectSql = "SELECT appointment_id, date_scheduled FROM appointments " +
                "WHERE doctor_id = ? AND date_scheduled BETWEEN ? AND ?";

        String updateSql = "UPDATE appointments SET date_scheduled = ? WHERE appointment_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            // Set parameters for select
            selectStmt.setInt(1, doc_id);
            selectStmt.setTimestamp(2, Timestamp.valueOf(break_time.getStart()));
            selectStmt.setTimestamp(3, Timestamp.valueOf(break_time.getEnd()));

            ResultSet rs = selectStmt.executeQuery();

            // Parse break duration
            Duration delay = DurationParser.parseFlexibleDuration(breakDuration);

            while (rs.next()) {
                System.out.println("Entering Query");
                int appointmentId = rs.getInt("appointment_id");
                LocalDateTime oldDateTime = rs.getTimestamp("date_scheduled").toLocalDateTime();
                LocalDateTime newDateTime = oldDateTime.plus(delay);
                System.out.println("Appointment ID: " + appointmentId + ", Old Time: " + oldDateTime + ", New Time: " + newDateTime);

                // Set new values for update
                updateStmt.setTimestamp(1, Timestamp.valueOf(newDateTime));
                updateStmt.setInt(2, appointmentId);
                updateStmt.addBatch();  // Batch for performance
            }

            updateStmt.executeBatch(); // Execute all updates

        } catch (SQLException e) {
            throw new RuntimeException("Failed to postpone appointments", e);
        }
    }

}
