package com.cannontech.stars.dr.appliance.model;

import org.apache.commons.lang.StringUtils;

import com.cannontech.i18n.MessageCodeGenerator;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.webconfiguration.model.WebConfiguration;

public class AssignedProgram {
    private int applianceCategoryId;
    private int assignedProgramId;
    private int programId;
    private String programName;
    private int chanceOfControlId;
    private int programOrder;
    private boolean isLast;

    private String displayName;
    private String shortName;
    private String description;
    private String savingsIcon;
    private String controlPercentIcon;
    private String environmentIcon;

    private WebConfiguration webConfiguration;

    public AssignedProgram() {
    }

    public AssignedProgram(int applianceCategoryId, int assignedProgramId,
            int programId, String programName, int chanceOfControlId,
            int programOrder, boolean isLast, WebConfiguration webConfiguration) {
        this.applianceCategoryId = applianceCategoryId;
        this.assignedProgramId = assignedProgramId;
        this.programId = programId;
        this.programName = programName;
        this.chanceOfControlId = chanceOfControlId;
        this.programOrder = programOrder;
        this.isLast = isLast;
        setWebConfiguration(webConfiguration);
    }

    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }

    public void setApplianceCategoryId(int applianceCategoryId) {
        this.applianceCategoryId = applianceCategoryId;
    }

    public int getAssignedProgramId() {
        return assignedProgramId;
    }

    public void setAssignedProgramId(int assignedProgramId) {
        this.assignedProgramId = assignedProgramId;
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        webConfiguration = null;
        this.displayName = displayName;
    }

    public String getDisplayNameKey() {
        return MessageCodeGenerator.generateCode(com.cannontech.stars.dr.program.model.Program.PROGAM_PREFIX, displayName);
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        webConfiguration = null;
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        webConfiguration = null;
        this.description = description;
    }

    public int getChanceOfControlId() {
        return chanceOfControlId;
    }

    public void setChanceOfControlId(int chanceOfControlId) {
        this.chanceOfControlId = chanceOfControlId;
    }

    public String getSavingsIcon() {
        return savingsIcon;
    }

    public void setSavingsIcon(String savingsIcon) {
        webConfiguration = null;
        this.savingsIcon = savingsIcon;
    }

    public SavingsIcon getSavingsIconEnum() {
        return SavingsIcon.getByFilename(savingsIcon);
    }

    public String getControlPercentIcon() {
        return controlPercentIcon;
    }

    public void setControlPercentIcon(String controlPercentIcon) {
        webConfiguration = null;
        this.controlPercentIcon = controlPercentIcon;
    }

    public ControlPercentIcon getControlPercentIconEnum() {
        return ControlPercentIcon.getByFilename(controlPercentIcon);
    }

    public String getEnvironmentIcon() {
        return environmentIcon;
    }

    public void setEnvironmentIcon(String environmentIcon) {
        webConfiguration = null;
        this.environmentIcon = environmentIcon;
    }

    public EnvironmentIcon getEnvironmentIconEnum() {
        return EnvironmentIcon.getByFilename(environmentIcon);
    }

    public int getProgramOrder() {
        return programOrder;
    }

    public void setProgramOrder(int programOrder) {
        this.programOrder = programOrder;
    }

    public boolean isFirst() {
        return programOrder == 1;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean isLast) {
        this.isLast = isLast;
    }

    public WebConfiguration getWebConfiguration() {
        if (webConfiguration == null) {
            updateWebConfig();
        }
        return webConfiguration;
    }

    public void setWebConfiguration(WebConfiguration webConfiguration) {
        this.webConfiguration = webConfiguration;
        if (webConfiguration == null) {
            return;
        }

        String[] names =
            StarsUtils.splitString(webConfiguration.getAlternateDisplayName(), ",");
        displayName = names[0];
        shortName = names[1];
        if (StringUtils.isEmpty(displayName)) {
            displayName = programName;
        }
        if (StringUtils.isEmpty(shortName)) {
            shortName = displayName;
        }

        description = webConfiguration.getDescription();

        String[] icons = ServletUtils.getImageNames(webConfiguration.getLogoLocation());
        savingsIcon = icons[0];
        controlPercentIcon = icons[1];
        environmentIcon = icons[2];
    }

    private void updateWebConfig() {
        WebConfiguration newWebConfiguration = new WebConfiguration();

        StringBuilder alternateDisplayName = new StringBuilder();
        if (displayName.indexOf(',') !=  -1) {
            alternateDisplayName.append('"');
            alternateDisplayName.append(displayName);
            alternateDisplayName.append('"');
        } else {
            alternateDisplayName.append(displayName);
        }
        alternateDisplayName.append(',');
        if (shortName.indexOf(',') !=  -1) {
            alternateDisplayName.append('"');
            alternateDisplayName.append(shortName);
            alternateDisplayName.append('"');
        } else {
            alternateDisplayName.append(shortName);
        }
        newWebConfiguration.setAlternateDisplayName(alternateDisplayName.toString());
        newWebConfiguration.setDescription(description);
        String logoLocation = savingsIcon + ',' + controlPercentIcon + ',' +
            environmentIcon;
        newWebConfiguration.setLogoLocation(logoLocation);

        webConfiguration = newWebConfiguration;
    }
}
