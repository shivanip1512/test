package com.cannontech.stars.xml.serialize;

import com.cannontech.stars.dr.program.service.ProgramEnrollment;

public class SULMProgram {
    
    public static final int ADDRESSING_GROUP_NOT_FOUND = -999999;

    private int _programID;
    private boolean _has_programID;
    private int _applianceCategoryID;
    private boolean _has_applianceCategoryID;
    private int _addressingGroupID;
    private boolean _has_addressingGroupID;
    private int _inventoryID;
    private boolean _has_inventoryID;
    private int _loadNumber;
    private boolean _has_loadNumber;

    public SULMProgram() {
        super();
    }
    
    public SULMProgram(ProgramEnrollment programEnrollment) {
        setProgramID(programEnrollment.getAssignedProgramId());
        if (programEnrollment.getApplianceCategoryId() > 0) {
            setApplianceCategoryID(programEnrollment.getApplianceCategoryId());
        }
        if (programEnrollment.getInventoryId() > 0) {
            setInventoryID(programEnrollment.getInventoryId());
        }            
        if (programEnrollment.getLmGroupId() != 0) {
            setAddressingGroupID(programEnrollment.getLmGroupId());
        } else {
            setAddressingGroupID(ADDRESSING_GROUP_NOT_FOUND);
        }
        if (programEnrollment.getRelay() > 0) {
            setLoadNumber(programEnrollment.getRelay());
        }
    }

    public void deleteAddressingGroupID() {
        this._has_addressingGroupID= false;
    }

    public void deleteApplianceCategoryID() {
        this._has_applianceCategoryID= false;
    }

    public void deleteInventoryID() {
        this._has_inventoryID = false;
    }

    public void deleteLoadNumber() {
        this._has_loadNumber = false;
    }

    public void deleteProgramID() {
        this._has_programID = false;
    }

    public int getAddressingGroupID() {
        return this._addressingGroupID;
    }

    public int getApplianceCategoryID() {
        return this._applianceCategoryID;
    }

    public int getInventoryID() {
        return this._inventoryID;
    }

    public int getLoadNumber() {
        return this._loadNumber;
    }

    public int getProgramID() {
        return this._programID;
    }

    public boolean hasAddressingGroupID() {
        return this._has_addressingGroupID;
    }

    public boolean hasApplianceCategoryID() {
        return this._has_applianceCategoryID;
    }

    public boolean hasInventoryID() {
        return this._has_inventoryID;
    }

    public boolean hasLoadNumber() {
        return this._has_loadNumber;
    } 

    public boolean hasProgramID() {
        return this._has_programID;
    }

    public void setAddressingGroupID(int addressingGroupID) {
        this._addressingGroupID = addressingGroupID;
        this._has_addressingGroupID = true;
    }
    
    public void setApplianceCategoryID(int applianceCategoryID) {
        this._applianceCategoryID = applianceCategoryID;
        this._has_applianceCategoryID = true;
    }

    public void setInventoryID(int inventoryID) {
        this._inventoryID = inventoryID;
        this._has_inventoryID = true;
    }

    public void setLoadNumber(int loadNumber) {
        this._loadNumber = loadNumber;
        this._has_loadNumber = true;
    }

    public void setProgramID(int programID) {
        this._programID = programID;
        this._has_programID = true;
    } 
}
