package com.hms.dao;

import com.hms.model.Doctor;
import com.hms.model.Appointment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static com.hms.dao.DatabaseConnection.getConnection;

public class AppointmentDAO {

    //adding appointment
    public boolean addAppointment(Appointment app) {
        String sql = "INSERT INTO appointments (appointment_id, doctor_id, patient_id,date_requested,date_scheduld,date_made,status) " +
                "VALUES (?, ?, ?, ?, ?, ?,?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, app.getId());
            stmt.setInt(2,app.getDoctor().getId());
            stmt.setInt(3,app.getPatient().getId());
            stmt.setTimestamp(4, Timestamp.valueOf(app.getDate_requested()));
            stmt.setTimestamp(5,Timestamp.valueOf(app.getDate_scheduled()));
            stmt.setTimestamp(6,Timestamp.valueOf(app.getDate_made()));
            stmt.setString(7,app.getStatus());

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
            stmt.setInt(7, app.getId());  // WHERE clause condition

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //update date scheduled
    public boolean updateAppointmentScheduleDate(int appointmentId, LocalDateTime newScheduledDate) {
        String sql = "UPDATE appointments SET date_scheduld = ? WHERE appointment_id = ?";

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

    //update status
    public boolean updateAppointmentStatus(int appointmentId, String newStatus) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, appointmentId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
