package com.cannontech.notif.outputs;

import java.util.List;

public interface Callable {

    /**
     * @return An immutable List of ContactPhone objects.
     */
    public List getContactPhoneNumberList();

}