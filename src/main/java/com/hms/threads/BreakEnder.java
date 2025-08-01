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
            System.out.println("Going to sleep until its time to end break");
            thread.sleep(time.getDuration());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Ending break for doctor with ID: " + id);
        String sql = "UPDATE doctor_schedule SET \"isDoctorOnBreak\" = 'FALSE', \"breakDuration\" = ?, \"breakStart\" = ? WHERE doctor_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
                stmt.setObject(3, id);
                stmt.setString(1, null);
                stmt.setTimestamp(2, null);
                stmt.executeUpdate();
            System.out.println("Break ended for doctor with ID: " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
