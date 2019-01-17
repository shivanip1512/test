package com.cannontech.dr.itron.simulator.model;

import java.io.Serializable;

/**
 * Settings for the Itron Simulator that determine how it responds to requests.
 */
public class SimulatedItronSettings implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private EditHANDeviceError editHANDeviceError;
    private ItronBasicError addServicePointError;
    private ItronBasicError editServicePointError;
    private AddProgramError addProgramError;
    private ItronBasicError servicePointEnrollmentSetError;
    private ItronBasicError servicePointEnrollmentGetError;
    private ItronBasicError addHANLoadControlProgramEventError;
    
    public EditHANDeviceError getEditHANDeviceError() {
        return editHANDeviceError;
    }
    public void setEditHANDeviceError(EditHANDeviceError editHANDeviceError) {
        this.editHANDeviceError = editHANDeviceError;
    }
    public ItronBasicError getAddServicePointError() {
        return addServicePointError;
    }
    public void setAddServicePointError(ItronBasicError addServicePointError) {
        this.addServicePointError = addServicePointError;
    }
    public ItronBasicError getEditServicePointError() {
        return editServicePointError;
    }
    public void setEditServicePointError(ItronBasicError editServicePointError) {
        this.editServicePointError = editServicePointError;
    }
    public AddProgramError getAddProgramError() {
        return addProgramError;
    }
    public void setAddProgramError(AddProgramError addProgramError) {
        this.addProgramError = addProgramError;
    }
    public ItronBasicError getAddHANLoadControlProgramEventError() {
        return addHANLoadControlProgramEventError;
    }
    public void setAddHANLoadControlProgramEventError(ItronBasicError addHANLoadControlProgramEventError) {
        this.addHANLoadControlProgramEventError = addHANLoadControlProgramEventError;
    }
    public ItronBasicError getServicePointEnrollmentSetError() {
        return servicePointEnrollmentSetError;
    }
    public void setServicePointEnrollmentSetError(ItronBasicError servicePointEnrollmentSetError) {
        this.servicePointEnrollmentSetError = servicePointEnrollmentSetError;
    }
    public ItronBasicError getServicePointEnrollmentGetError() {
        return servicePointEnrollmentGetError;
    }
    public void setServicePointEnrollmentGetError(ItronBasicError servicePointEnrollmentGetError) {
        this.servicePointEnrollmentGetError = servicePointEnrollmentGetError;
    }

}
