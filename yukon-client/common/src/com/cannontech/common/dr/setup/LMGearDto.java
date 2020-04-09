package com.cannontech.common.dr.setup;

public class LMGearDto {

    private String gearName;
    private Integer gearNumber;

    public LMGearDto() {

    }

    public LMGearDto(Integer geraNumber, String gearName) {
        this.gearNumber = geraNumber;
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
