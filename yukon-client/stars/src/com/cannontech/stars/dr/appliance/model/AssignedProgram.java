package com.cannontech.stars.dr.appliance.model;


import com.cannontech.i18n.MessageCodeGenerator;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.webconfiguration.model.WebConfiguration;

public class AssignedProgram {
    private int applianceCategoryId;
    private int assignedProgramId;
    private int programId;
    private int chanceOfControlId;
    private int programOrder;
    private boolean isLast;

    private AssignedProgramName name;

    private String description;
    private String savingsIcon;
    private String controlPercentIcon;
    private String environmentIcon;

    private WebConfiguration webConfiguration;

    public AssignedProgram() {
        name = new AssignedProgramName();
    }

    public AssignedProgram(int applianceCategoryId, int assignedProgramId,
            int programId, String programName, int chanceOfControlId,
            int programOrder, boolean isLast, WebConfiguration webConfiguration) {
        this.applianceCategoryId = applianceCategoryId;
        this.assignedProgramId = assignedProgramId;
        this.programId = programId;
        name = new AssignedProgramName(programName); 
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

    /**
     * Get the STARS program id.
     * @return the STARS program id.
     */
    public int getAssignedProgramId() {
        return assignedProgramId;
    }

    public void setAssignedProgramId(int assignedProgramId) {
        this.assignedProgramId = assignedProgramId;
    }

    /**
     * Get the PAO program id
     * @return the program id
     */
    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        if (programId == 0 ) {
            return name.getDisplayName();
        }
        return name.getProgramName();
    }

    public void setProgramName(String programName) {
        name.setProgramName(programName);
    }

    public String getDisplayName() {
        return name.getDisplayName();
    }

    public void setDisplayName(String displayName) {
        webConfiguration = null;
        name.setDisplayName(displayName);
    }

    public String getDisplayNameKey() {
        return MessageCodeGenerator.generateCode(com.cannontech.stars.dr.program.model.Program.PROGAM_PREFIX, name.getDisplayName());
    }

    public String getShortName() {
        return name.getShortName();
    }

    public void setShortName(String shortName) {
        webConfiguration = null;
        name.setShortName(shortName);
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

    public AssignedProgramName getName() {
        return name;
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

        name.setAlternateDisplayName(webConfiguration.getAlternateDisplayName());

        description = webConfiguration.getDescription();

        String[] icons = ServletUtils.getImageNames(webConfiguration.getLogoLocation());
        savingsIcon = icons[0];
        controlPercentIcon = icons[1];
        environmentIcon = icons[2];
    }

    private void updateWebConfig() {
        WebConfiguration newWebConfiguration = new WebConfiguration();

        String alternateDisplayName = name.getAlternateDisplayName();
        newWebConfiguration.setAlternateDisplayName(alternateDisplayName);
        newWebConfiguration.setDescription(description);
        String logoLocation = savingsIcon + ',' + controlPercentIcon + ',' +
            environmentIcon;
        newWebConfiguration.setLogoLocation(logoLocation);

        webConfiguration = newWebConfiguration;
    }
}
