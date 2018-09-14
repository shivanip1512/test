package com.cannontech.web.dr.model;

public class NestFileGenerationSetting {
    private String groupName;
    private Integer noOfRows;
    private Integer noOfThermostats;
    private boolean winterProgram;
    private boolean useFileForNest;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getNoOfRows() {
        return noOfRows;
    }

    public void setNoOfRows(Integer noOfRows) {
        this.noOfRows = noOfRows;
    }

    public Integer getNoOfThermostats() {
        return noOfThermostats;
    }

    public void setNoOfThermostats(Integer noOfThermostats) {
        this.noOfThermostats = noOfThermostats;
    }

    public boolean isWinterProgram() {
        return winterProgram;
    }

    public void setWinterProgram(boolean winterProgram) {
        this.winterProgram = winterProgram;
    }

    public boolean isUseFileForNest() {
        return useFileForNest;
    }

    public void setUseFileForNest(boolean useFileForNest) {
        this.useFileForNest = useFileForNest;
    }

}
