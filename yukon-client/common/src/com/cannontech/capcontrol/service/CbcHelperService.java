package com.cannontech.capcontrol.service;

import java.util.function.Consumer;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface CbcHelperService {

    /**
     * Retrieves the Capbank fixed/static text property for a LiteYukonUser.  If the user is not in the Cap Bank Display
     * role, the default value of "Fixed" is returned.
     */
    public String getFixedText(LiteYukonUser yukonUser);

    void updateLogicalPointName(String oldPointName, String newPointName, Consumer<String> pointNameCallback);

    void trimLogicalPointName(String pointName, Consumer<String> pointNameCallback, Consumer<String> deviceNameCallback);

}