package com.cannontech.common.device.definition.model;

/**
 * Class which contains device definition data
 */
public class DeviceDefinition {

    private int type = -1;
    private String displayName = null;
    private String displayGroup = null;
    private String javaConstant = null;
    private boolean changeable = false;
    private String changeGroup = null;

    public DeviceDefinition(int type, String displayName, String displayGroup, String javaConstant,
            String changeGroup) {
        this.type = type;
        this.displayName = displayName;
        this.displayGroup = displayGroup;
        this.javaConstant = javaConstant;
        this.changeGroup = changeGroup;
        this.changeable = changeGroup != null;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isChangeable() {
        return changeable;
    }

    public void setChangeable(boolean changeable) {
        this.changeable = changeable;
    }

    public String getDisplayGroup() {
        return displayGroup;
    }

    public void setDisplayGroup(String displayGroup) {
        this.displayGroup = displayGroup;
    }

    public String getJavaConstant() {
        return javaConstant;
    }

    public void setJavaConstant(String javaConstant) {
        this.javaConstant = javaConstant;
    }

    public String getChangeGroup() {
        return changeGroup;
    }

    public void setChangeGroup(String changeGroup) {
        this.changeGroup = changeGroup;
    }

    public String toString() {
        return this.displayName;
    }
}
