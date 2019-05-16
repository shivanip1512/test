package com.cannontech.stars.dr.enrollment.model;

import java.util.Map;

public class EnrolledDevicePrograms {
    private String serialNumber;
    private Map<String, Integer> enrolledDevicePrograms;

    public EnrolledDevicePrograms(String serialNumber, Map<String, Integer> enrolledProgram) {

        this.serialNumber = serialNumber;
        this.enrolledDevicePrograms = enrolledProgram;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public Map<String, Integer> getEnrolledDevicePrograms() {
        return enrolledDevicePrograms;
    }
}
