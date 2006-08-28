package com.cannontech.cbc.dao;

import java.util.List;

import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;

public interface CBCDao {

    public  List getCBCPointTimeStamps(Integer cbcID);

    public  List getAllSubsForUser(LiteYukonUser user);

    public List<LitePoint> getPaoPoints(YukonPAObject sub);

}