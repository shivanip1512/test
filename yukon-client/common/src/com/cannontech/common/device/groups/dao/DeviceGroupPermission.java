package com.cannontech.common.device.groups.dao;

/**
 * Enum value to determine if a Device Group is editable and/or modifiable, or not.
 * @author m_peterson
 *
 */
public enum DeviceGroupPermission {
    
    EDIT_NOMOD(true, false, false),
    NOEDIT_MOD(false, true, false),
    NOEDIT_NOMOD(false, false, false),
    EDIT_MOD(true, true, false),
    HIDDEN(false, true, true);
    
    private boolean editable;
    private boolean modifiable;
    private boolean hidden;
    
    DeviceGroupPermission(boolean editable, boolean modifiable, boolean hidden) {
        this.editable = editable;
        this.modifiable = modifiable;
        this.hidden = hidden;
    }
    
    public boolean isHidden() {
        return hidden;
    }
    
    public boolean isEditable() {
        return editable;
    }
    
    public boolean isModifiable() {
        return modifiable;
    }
    
}
