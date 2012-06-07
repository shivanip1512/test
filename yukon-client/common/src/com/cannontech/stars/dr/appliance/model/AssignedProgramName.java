package com.cannontech.stars.dr.appliance.model;

import org.apache.commons.lang.StringUtils;

import com.cannontech.stars.util.StarsUtils;

public class AssignedProgramName {
    public AssignedProgramName() {
        this(null);
    }

    public AssignedProgramName(String programName) {
        this.programName = programName;
        this.displayName = programName;
        this.shortName = programName;
    }

    public AssignedProgramName(String programName, String alternateDisplayName) {
        this.programName = programName;
        setAlternateDisplayName(alternateDisplayName);
    }

    private String programName;
    private String displayName;
    private String shortName;

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
        this.displayName = displayName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    
    public String getAlternateDisplayName() {
        StringBuilder alternateDisplayName = new StringBuilder();
        if (displayName != null && displayName.indexOf(',') !=  -1) {
            alternateDisplayName.append('"');
            alternateDisplayName.append(displayName);
            alternateDisplayName.append('"');
        } else if (displayName != null) {
            alternateDisplayName.append(displayName);
        }
        alternateDisplayName.append(',');
        if (shortName != null && shortName.indexOf(',') !=  -1) {
            alternateDisplayName.append('"');
            alternateDisplayName.append(shortName);
            alternateDisplayName.append('"');
        } else if (shortName != null) {
            alternateDisplayName.append(shortName);
        }
        return alternateDisplayName.toString();
    }
    
    public void setAlternateDisplayName(String alternateDisplayName) {
        String[] names =
            StarsUtils.splitString(alternateDisplayName, ",");
        displayName = names[0];
        if (StringUtils.isEmpty(displayName)) {
            displayName = programName;
        }

        if (names.length > 1 && !StringUtils.isEmpty(names[1])) {
            shortName = names[1];
        } else {
            shortName = displayName;
        }
    }
}
