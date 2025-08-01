package com.hms.dao;

import com.hms.model.Appointment;
import com.hms.model.Doctor;
import com.hms.model.Patient;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.hms.dao.DatabaseConnection.getConnection;

public class DoctorDAO {

    // Add a new doctor to the database
    public Doctor getDoctorByName(String name) {
        String sql = "SELECT * FROM Doctors WHERE LOWER(name) = LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Doctor doctor = new Doctor(
                            rs.getString("name"),
                            rs.getInt("doctor_id"),
                            rs.getString("gender"),
                            rs.getString("email"),
                            rs.getString("speciality"),
                            rs.getInt("contact_no"),
                            rs.getString("addrress")
                    );
                    doctor.setPassword(rs.getString("password"));
                    doctor.setAccount_status(rs.getString("account_status"));
                    return doctor;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO Doctors (name, gender, email, speciality, contact_no, addrress,password,account_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?,?) RETURNING doctor_id";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doctor.getName());
            stmt.setString(2, doctor.getGender());
            stmt.setString(3, doctor.getEmail());
            stmt.setString(4, doctor.getSpeciality());
            stmt.setInt(5, doctor.getContactNo());
            stmt.setString(6, doctor.getAddress());
            stmt.setString(7,doctor.getPassword());
            stmt.setString(8,"pending");

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                doctor.setId(rs.getInt("doctor_id"));
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all doctors from the database
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM hospital.doctors ";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Doctor doctor = new Doctor(
                        rs.getString("name"),
                        rs.getInt("doctor_id"),
                        rs.getString("gender"),
                        rs.getString("email"),
                        rs.getString("speciality"),
                        rs.getInt("contact_no"),
                        rs.getString("addrress")
                );

                // Note: Patients and appointments would need separate queries to populate
                doctors.add(doctor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }

    // Get a doctor by ID
    public Doctor getDoctorById(int id) {
        String sql = "SELECT * FROM Doctors WHERE doctor_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Doctor doctor = new Doctor(
                            rs.getString("name"),
                            rs.getInt("doctor_id"),
                            rs.getString("gender"),
                            rs.getString("email"),
                            rs.getString("speciality"),
                            rs.getInt("contact_no"),
                            rs.getString("addrress")
                    );

                    // You might want to load patients and appointments here with separate methods

                    return doctor;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update a doctor's information
    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE Doctors SET name = ?, gender = ?, email = ?, speciality = ?, " +
                "contact_no = ?, addrress = ? WHERE doctor_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doctor.getName());
            stmt.setInt(2, doctor.getId());
            stmt.setString(3, doctor.getGender());
            stmt.setString(4, doctor.getEmail());
            stmt.setString(5, doctor.getSpeciality());
            stmt.setInt(6, doctor.getContactNo());
            stmt.setString(7, doctor.getAddress());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a doctor by ID
    public boolean deleteDoctor(int id) {
        String sql = "DELETE FROM Doctors WHERE doctor_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Additional methods to handle patients' and appointments relationships

    public List<Patient> getPatientsForDoctor(int doctorId) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT DISTINCT p.* FROM Patients p JOIN appointments a ON p.patient_id = a.patient_id WHERE a.doctor_id = ?";

        try(Connection conn= getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,doctorId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
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
                patients.add(patient);

            }
        }catch(SQLException e){
            e.printStackTrace() ;
        }

        return patients;
    }

    public List<Appointment> getAppointmentsForDoctor(int doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = """
        SELECT 
            a.appointment_id, a.date_created, a.date_requested, a.date_scheduled, a.status,
            p.patient_id, p.name AS patient_name, p.gender AS patient_gender, p.age AS patient_age,
            p.date_of_birth AS patient_date_of_birth, p.address AS patient_address, p.contact_no AS patient_contact, p.blood_type,
            d.doctor_id, d.name AS doctor_name, d.gender AS doctor_gender, d.email,
            d.speciality, d.contact_no AS doctor_contact, d.addrress AS doctor_address
        FROM appointments a
        JOIN Patients p ON a.patient_id = p.patient_id
        JOIN Doctors d ON a.doctor_id = d.doctor_id
        WHERE a.doctor_id = ?
    """;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
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
                Doctor doctor = new Doctor(
                        rs.getString("doctor_name"),
                        rs.getInt("doctor_id"),
                        rs.getString("doctor_gender"),
                        rs.getString("email"),
                        rs.getString("speciality"),
                        rs.getInt("doctor_contact"),
                        rs.getString("doctor_address")
                );
                LocalDateTime dateCreated = rs.getTimestamp("date_created") != null ? rs.getTimestamp("date_created").toLocalDateTime(): null;
                LocalDateTime dateScheduled = rs.getTimestamp("date_scheduled") != null ? rs.getTimestamp("date_scheduled").toLocalDateTime() : null;
                Appointment appointment = new Appointment(
                        rs.getInt("appointment_id"),
                        dateCreated,
                        rs.getTimestamp("date_requested").toLocalDateTime(),
                        dateScheduled,
                        patient,doctor,
                        rs.getString("status")
                );
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return appointments;
    }

    // Search doctors by name or speciality
    public List<Doctor> searchDoctors(String query) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM Doctors WHERE LOWER(name) LIKE LOWER(?) OR LOWER(speciality) LIKE LOWER(?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Doctor doctor = new Doctor(
                            rs.getString("name"),
                            rs.getInt("doctor_id"),
                            rs.getString("gender"),
                            rs.getString("email"),
                            rs.getString("speciality"),
                            rs.getInt("contact_no"),
                            rs.getString("addrress")
                    );
                    doctors.add(doctor);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }
}