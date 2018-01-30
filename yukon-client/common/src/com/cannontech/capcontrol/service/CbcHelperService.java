package com.cannontech.capcontrol.service;

import java.util.List;
import java.util.Map;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CbcHelperService {

    /**
     * Retrieves the Capbank fixed/static text property for a LiteYukonUser.  If the user is not in the Cap Bank Display
     * role, the default value of "Fixed" is returned.
     */
    public String getFixedText(LiteYukonUser yukonUser);

    /** 
     * Returns the point ID of the CONTROL_POINT attribute on the specified CBC.
     * @throws NotFoundException if not found.
     */
    public int getControlPointIdForCbc(int controlDeviceID);

    /** 
     * @return the point ID of the CONTROL_POINT attribute on the specified CBC, or null if it does not exist.
     */
    public Integer findControlPointIdForCbc(int controlDeviceID);

    /** 
     * @param paoType the paoType of the device to check.
     * @param litePoints the points to map.
     * @return the mapping of point IDs to their associated format strings.
     */
    public Map<Integer, String> getPaoTypePointFormats(PaoType paoType, List<LitePoint> litePoints);

    /**
     * Returns an SQL statement that finds all CBCs with a CONTROL_POINT that are not assigned to a Cap Bank.
     */
    public SqlFragmentSource getOrphanSql();
}