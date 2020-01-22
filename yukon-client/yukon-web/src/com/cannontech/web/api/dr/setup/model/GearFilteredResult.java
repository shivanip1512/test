package com.cannontech.web.api.dr.setup.model;

import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class GearFilteredResult {

    private Integer gearId;
    private String gearName;
    private Integer gearNumber;
    private LMDto loadProgram;
    private GearControlMethod controlMethod;
    private String gearDetails;

    
    public Integer getGearId() {
        return gearId;
    }

    public void setGearId(Integer gearId) {
        this.gearId = gearId;
    }

    public String getGearName() {
        return gearName;
    }

    public void setGearName(String gearName) {
        this.gearName = gearName;
    }

    public Integer getGearNumber() {
        return gearNumber;
    }

    public void setGearNumber(Integer gearNumber) {
        this.gearNumber = gearNumber;
    }

    public GearControlMethod getControlMethod() {
        return controlMethod;
    }

    public void setControlMethod(GearControlMethod controlMethod) {
        this.controlMethod = controlMethod;
    }
    
    public LMDto getLoadProgram() {
        return loadProgram;
    }

    public void setLoadProgram(LMDto loadProgram) {
        this.loadProgram = loadProgram;
    }

    public String getGearDetails() {
        return gearDetails;
    }

    public void setGearDetails(String gearDetails) {
        this.gearDetails = gearDetails;
    }

}
