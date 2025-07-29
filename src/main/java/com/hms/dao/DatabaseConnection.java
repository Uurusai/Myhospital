package com.hms.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    public static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    public static final String USER = "postgres";
    public static final String PASSWORD = "Adiba4Ayman";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL,USER,PASSWORD);
        Statement stmt = conn.createStatement();
        stmt.execute("SET search_path TO hospital");
        return conn ;
    }

}
