package com.hms.dao;

import com.hms.model.*;
import com.hms.notification.NotificationServer;
import com.hms.threads.BreakEnder;

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
                "isDoctorOnBreak, breakStart, breakDuration " +
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
                        Duration breakDuration = rs.getObject("breakDuration", Duration.class);

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
        String sql  = "UPDATE doctor_schedule SET isDoctorOnBreak = 'TRUE', breakDuration = CAST(? AS interval), breakStart = ? WHERE doctor_id = ?";
        try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                stmt.setInt(1,doc_id);
                stmt.setString(5,breakDuration);
                stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                stmt.executeUpdate();
            }
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
        String sql = "SELECT id, date_scheduled,patient_id FROM appointments " +
                "WHERE doctor_id = ? AND date_scheduled BETWEEN ? AND ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, doc_id);
            stmt.setTimestamp(2, Timestamp.valueOf(break_time.getStart()));
            stmt.setTimestamp(3, Timestamp.valueOf(break_time.getEnd()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Appointment app = new Appointment();
                app.setId(rs.getInt("id"));
                app.setDate_scheduled(rs.getTimestamp("date_scheduled").toLocalDateTime());
                LocalDateTime newTime = calculateNewDateTime(app.getDate_scheduled(), BreakDuration);
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

    public void postPoneAppointments(int doc_id, TimeDateRange break_time, String breakDuration){

        String postponeSql = "UPDATE appointments " +
                "SET date_scheduled = date_scheduled + CAST(? AS INTERVAL) " +
                "WHERE doctor_id = ? " +
                "AND date_scheduled BETWEEN ? AND ?";

        try (PreparedStatement postponeStmt = getConnection().prepareStatement(postponeSql)) {
            postponeStmt.setString(1, breakDuration);
            postponeStmt.setInt(2, doc_id);
            postponeStmt.setTimestamp(3, Timestamp.valueOf(break_time.getStart()));
            postponeStmt.setTimestamp(4, Timestamp.valueOf(break_time.getEnd()));
            postponeStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private LocalDateTime calculateNewDateTime(LocalDateTime original, String breakDuration) {
        try {
            if (breakDuration.matches("\\d+ hour(s)?")) {
                int hours = Integer.parseInt(breakDuration.replaceAll("[^0-9]", ""));
                return original.plusHours(hours);
            }
            else if (breakDuration.matches("\\d+ day(s)?")) {
                int days = Integer.parseInt(breakDuration.replaceAll("[^0-9]", ""));
                return original.plusDays(days);
            }
            else if (breakDuration.matches("\\d+ week(s)?")) {
                int weeks = Integer.parseInt(breakDuration.replaceAll("[^0-9]", ""));
                return original.plusWeeks(weeks);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return original;
    }

}
