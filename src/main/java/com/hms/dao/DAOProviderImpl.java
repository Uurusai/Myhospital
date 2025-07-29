package com.hms.dao;

public class DAOProviderImpl implements DAOProvider {
    @Override
    public DoctorDAO getDoctorDAO() {
        return new DoctorDAO();
    }

    @Override
    public PatientDAO getPatientDAO() {
        return new PatientDAO();
    }

    @Override
    public DoctorScheduleDAO getDoctorScheduleDAO() {
        return new DoctorScheduleDAO();
    }

    @Override
    public AppointmentDAO getAppointmentDAO() {
        return new AppointmentDAO();
    }

    @Override
    public AdminDAO getAdminDAO() {
        return new AdminDAO();
    }

}