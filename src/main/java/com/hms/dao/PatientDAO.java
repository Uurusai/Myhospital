package com.hms.dao;

import com.hms.model.Appointment;
import com.hms.model.Doctor;
import com.hms.model.Patient;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.hms.dao.DatabaseConnection.getConnection;

public class PatientDAO {

    private Patient extractPatientFromResultSet(ResultSet rs) throws SQLException {
        return new Patient(
                rs.getString("name"),
                rs.getInt("patient_id"),
                rs.getString("gender"),
                rs.getInt("age"),
                rs.getTimestamp("date_of_birth").toString(),
                rs.getInt("contact_no"),
                rs.getString("address"),
                rs.getString("blood_type")
        );
    }


    //adding new Patient to db
    public boolean addPatient(Patient p){
        String sql = "INSERT INTO Patients(name,gender,age,date_of_birth,address,contact_no,created_at,blood_type,account_status,pssword)"+
                "VALUES(?,?,?,?,?,?,?,?,?,?) RETURNING patient_id";
        try(Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){

            //stmt.setInt(1,p.getId());
            stmt.setString(1,p.getName());
            stmt.setString(2,p.getGender());
            stmt.setString(5,p.getAddress());
            stmt.setTimestamp(4,Timestamp.valueOf(p.getDate_of_birth()));
            stmt.setInt(6,p.getContactNo());
            stmt.setInt(3,p.getAge());
            stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(8,p.getBlood_type());
            stmt.setString(9,"pending");
            stmt.setString(10,p.getPassword());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                p.setId(rs.getInt("patient_id"));
                return true;
            } else {
                return false;
            }
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    //Get all patients from database
    public List<Patient> getAllPatients(){
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patients";

        try(Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs  = stmt.executeQuery()){

            while(rs.next()){
                Patient patient = new Patient(
                        rs.getString("name"),
                        rs.getInt("patient_id"),
                        rs.getString("gender"),
                        rs.getInt("age"),
                        rs.getTimestamp("date_of_birth").toString(),
                        rs.getInt("contact-no"),
                        rs.getString("address"),
                        rs.getString("blood_type")
                );
                patients.add(patient);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return patients ;
    }
    //get all pending patients
    //unneeded ???
    public List<Patient> getAllPendingPatients(){
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patients WHERE status = 'pending'";

        try(Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs  = stmt.executeQuery()){

            while(rs.next()){
                Patient patient = new Patient(
                        rs.getString("name"),
                        rs.getInt("patient_id"),
                        rs.getString("gender"),
                        rs.getInt("age"),
                        rs.getTimestamp("date_of_birth").toString(),
                        rs.getInt("contact-no"),
                        rs.getString("address"),
                        rs.getString("blood_type")
                );
                patients.add(patient);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return patients ;
    }

    //Get a patient by id
    public Patient getPatientbyId(int id){
        String sql = "SELECT * FROM Patients WHERE patient_id = ? AND status = 'approved'";
        try(Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql) ){
            stmt.setInt(1,id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return extractPatientFromResultSet(rs);
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null ;
    }
    //get patient by name
    public Patient getPatientByName(String name) {
        String sql = "SELECT * FROM Patients WHERE LOWER(name) = LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Patient p = extractPatientFromResultSet(rs);
                    p.setPassword(rs.getString("password"));
                    return p ;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    //update patient info
    public Boolean updatePatients(Patient patient){

        String sql = "UPDATE Patients SET name = ?, gender = ? , contact_no = ?, date_of_birth = ? , address = ? , age = ?,blood_type = ?,account_status = ? WHERE patient_id = ?";

        try(Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,patient.getId());
            stmt.setString(2,patient.getName());
            stmt.setString(3,patient.getGender());
            stmt.setString(6,patient.getAddress());
            stmt.setString(5,patient.getDate_of_birth());
            stmt.setInt(7,patient.getContactNo());
            stmt.setInt(4,patient.getAge());
            stmt.setString(9,patient.getBlood_type());
            stmt.setString(10,patient.getAccount_status());

            return stmt.executeUpdate()>0 ;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }


    //Delete a patient by id
    public boolean deletePatient(int id){

        String sql = "DELETE FROM Patients WHERE patient_id = ?";

        try(Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    //get appointment-history for patient
    public List<Appointment> getAppointmentsForDoctor(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = """
            SELECT 
                a.appointment_id,a.date_made,a.date_requested, a.date_scheduled,a.status
                
                -- patient fields
                p.patient_id,p.name AS patient_name, p.gender AS patient_gender, p.age AS patient_age,
                p.date_of_birth AS patient_date_of_birth, p.address AS patient_address, p.contact_no AS patient_contact
                p.blood_type 
            
                --doctor fields
                d.doctor_id, d.name AS doctor_name, d.gender AS doctor_gender, d.e-mail,
                d.speciality,d.contact no AS doctor_contact d.addrress AS doctor_address
            
            FROM appointments a
            JOIN Patients p ON a.patient_id = p.patient_id
            JOIN Doctors d ON a.doctor_id = d.doctor_id
            WHERE a.patient_id = ?
                       
""";
        try(Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,patientId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Patient patient =  new Patient(
                        rs.getString("patient_name"),
                        rs.getInt("patient_id"),
                        rs.getString("patient_gender"),
                        rs.getInt("patient_age"),
                        rs.getTimestamp("patient_date_of_birth").toString(),
                        rs.getInt("patient_contact_no"),
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
                Appointment appointment = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getTimestamp("date_made").toLocalDateTime(),
                        rs.getTimestamp("date_requested").toLocalDateTime(),
                        rs.getTimestamp("date_scheduled").toLocalDateTime(),
                        patient,doctor,
                        rs.getString("status")
                );
                appointments.add(appointment);
            }

        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return appointments;
    }

    //get pending appointments for patient
    public List<Appointment> getPendingAppointmentsForPatient(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT " +
                "a.appointment_id,a.doctor_id,a.date_scheduled,a.status,a.symptoms" +
                "FROM" +
                "appointments WHERE patient_id = ? AND complete_status = pending";
        DoctorDAO dd = new DoctorDAO();
        try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
            stmt.setInt(1,patientId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Appointment app = new Appointment();
                app.setId(rs.getInt("appointment_id"));
                app.setDoctor(dd.getDoctorById(rs.getInt("doctor_id")));
                app.setDate_scheduled(rs.getTimestamp("date_scheduled").toLocalDateTime());
                app.setStatus(rs.getString("status"));
                app.setSymptoms(rs.getString("symptoms"));
                appointments.add(app);
            }
            return appointments;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null ;
    }


    //get doctors for patient
    public List<Doctor> getDoctorsForPatient(int patientId) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT DISTINCT d.* FROM Doctors d JOIN appointments a ON d.doctor_id = a.doctor_id WHERE a.patient_id = ?";

        try(Connection conn= getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,patientId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Doctor doctor = new Doctor(
                        rs.getString("name"),
                        rs.getInt("doctor_id"),
                        rs.getString("gender"),
                        rs.getString("email"),
                        rs.getString("speciality"),
                        rs.getInt("contact no"),
                        rs.getString("addrress")
                );
                doctors.add(doctor);

            }
        }catch(SQLException e){
            e.printStackTrace() ;
        }
        return doctors;
    }
}
