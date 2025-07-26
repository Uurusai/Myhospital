package com.hms.model;

import java.time.Duration;
import java.util.HashMap;

public class DoctorSchedule {
    private HashMap<Integer, TimeRange> workdays = new HashMap<>();
    private int doctor_id ;
    private boolean doctorOnBreak ;
    private TimeDateRange breakDuration ;

    public DoctorSchedule(int id){
        doctor_id = id;
    }

    public DoctorSchedule(int id, HashMap<Integer,TimeRange> wd){
        doctor_id = id;
        workdays = wd ;
    }
    public DoctorSchedule(int id, HashMap<Integer,TimeRange> wd,boolean ida,TimeDateRange tdr){
        doctor_id = id;
        workdays = wd ;
        doctorOnBreak = ida;
        breakDuration = tdr;
    }

    public void setDoctorOnBreak(boolean doctorOnBreak) {
        this.doctorOnBreak = doctorOnBreak;
    }

    public void setBreakDuration(TimeDateRange breakDuration) {
        this.breakDuration = breakDuration;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public HashMap<Integer, TimeRange> getWorkdays() {
        return workdays;
    }

    public TimeDateRange getBreakDuration() {
        return breakDuration;
    }

    public boolean isDoctorOnBreak() {
        return doctorOnBreak;
    }


}
