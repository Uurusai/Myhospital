package com.hms.threads;

import com.hms.model.TimeDateRange;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.hms.dao.DatabaseConnection.getConnection;

public class BreakEnder implements Runnable {

    int id;
    TimeDateRange time;
    Thread thread;

    public BreakEnder(int id, TimeDateRange time) {
        this.id = id;
        this.time = time;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {

        try {
            thread.sleep(time.getDuration());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String sql = "UPDATE doctor_schedule SET isDoctorOnBreak = 'FALSE', breakDuration = CAST(? AS interval), breakStart = ? WHERE doctor_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stmt.setInt(1, id);
                stmt.setString(5, null);
                stmt.setTimestamp(6, null);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
