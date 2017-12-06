package com.cannontech.capcontrol.service;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CbcHelperService {

    /**
     * Retrieves the Capbank fixed/static text property for a LiteYukonUser.  If the user is not in the Cap Bank Display
     * role, the default value of "Fixed" is returned.
     */
    public String getFixedText(LiteYukonUser yukonUser);

    /** 
     * Returns the point ID of the CONTROL_POINT attribute on the specified CBC.
     */
    public int getControlPointIdForCbc(Integer controlDeviceID);

    /**
     * Returns an SQL statement that finds all CBCs with a CONTROL_POINT that are not assigned to a Cap Bank.
     */
    public SqlFragmentSource getOrphanSql();
}