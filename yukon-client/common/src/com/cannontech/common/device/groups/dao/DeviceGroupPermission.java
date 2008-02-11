package com.cannontech.common.device.groups.dao;

/**
 * Emnum value to determine if a Device Group is editable and/or modifible, or not.
 * @author m_peterson
 *
 */
public enum DeviceGroupPermission {

    EDIT_MOD,
    EDIT_NOMOD,
    NOEDIT_MOD,
    NOEDIT_NOMOD;
}
