package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.capcontrol.LiteCapBankAdditional;
import com.cannontech.capcontrol.OrphanCBC;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.capcontrol.CCEventLog;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;

public interface CapControlDao {
    
    public Map<String, List<LitePoint>> getSortedCBCPointTimeStamps (Integer cbcID);

    public  List<LiteYukonPAObject> getAllSubsForUser(LiteYukonUser user);

    public List<LitePoint> getPaoPoints(YukonPAObject sub);

    public Integer getParentForController(int id);

    public Integer getParentForPoint(int id);
    
    public CapControlType getCapControlType(int id);
    
    public List<OrphanCBC> getOrphanedCBCs();
    
    public List<LiteCapBankAdditional> getCapBankAdditional (List<Integer> deviceIds);
    
    

    /**
     * Returns the events for the capcontrol object.
     */
    public List<CCEventLog> getEventsForPao(StreamableCapObject streamable, int prevDaysCount);

}
