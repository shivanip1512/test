package com.cannontech.common.config;

import java.util.EnumSet;
import java.util.Set;

/**
 * Mark unused master.cfg CPARMS deprecated.
 * 
 * If a deprecated key is found in master.cfg it will be commented out with '(DEPRECATED)' prepended.
 * When a key is added to this enum it must be removed from the other master config enum types.
 */
public enum MasterConfigDeprecatedKey {

    PIL_MACHINE,
    PIL_PORT,
    DISPATCH_MACHINE,
    DISPATCH_PORT,
    NOTIFICATION_MACHINE,
    NOTIFICATION_PORT,
    CAP_CONTROL_PORT,
    MACS_PORT,
    LOAD_MANAGEMENT_PORT,
    ;

    private static final Set<MasterConfigDeprecatedKey> enumSet = EnumSet.allOf(MasterConfigDeprecatedKey.class);

    /**
     * Returns true if the CPARM key is deprecated
     * 
     * Throws NullPointerException if keyStr is null
     */
    public static boolean isDeprecated(String keyStr) {
        try {
            MasterConfigDeprecatedKey key = MasterConfigDeprecatedKey.valueOf(keyStr);
            return enumSet.contains(key);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
