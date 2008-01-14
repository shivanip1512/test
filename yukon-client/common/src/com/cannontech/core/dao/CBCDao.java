package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.CBCPointTimestampParams;

public interface CBCDao {
    
    public  List<CBCPointTimestampParams> getCBCPointTimeStamps(Integer cbcID);

    public  List<LiteYukonPAObject> getAllSubsForUser(LiteYukonUser user);

    public List<LitePoint> getPaoPoints(YukonPAObject sub);

    public Integer getParentForController(int id);

    public Integer getParentForPoint(int id);

}
