package com.cannontech.notif.outputs;

import java.util.List;

public interface Emailable {

    /**
     * @return An immutable List of Strings
     */
    public List getEmailList();

    /**
     * Determines if this Contactable has the bit corresponding to the specified
     * output type set.
     * @param type {email, voice}
     * @return True if this object should be notified by the specified method
     */
    public boolean hasNotificationMethod(String type);

}