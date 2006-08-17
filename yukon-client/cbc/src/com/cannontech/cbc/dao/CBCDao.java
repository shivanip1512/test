package com.cannontech.cbc.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface CBCDao {

    public  List getCBCPointTimeStamps(Integer cbcID);

    public  List getAllSubsForUser(LiteYukonUser user);

}