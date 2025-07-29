package com.hms.dao;

public interface DAOProvider {
    DoctorDAO getDoctorDAO();
    PatientDAO getPatientDAO();
    DoctorScheduleDAO getDoctorScheduleDAO();
    AppointmentDAO getAppointmentDAO();

}
