package com.cannontech.common.dr.setup;

public class LMGearDto {

    private String gearName;
    private Integer gearNumber;

    public LMGearDto() {

    }

    public LMGearDto(Integer gearNumber, String gearName) {
        this.gearNumber = gearNumber;
        this.gearName = gearName;
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
}
