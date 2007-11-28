package com.cannontech.core.dao;

import java.util.TimeZone;

import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonUserDao {

    public LiteYukonUser getLiteYukonUser(int userId);

    public LiteYukonUser getLiteYukonUser(String userName);

    public LiteContact getLiteContact(int userId);
    
    public TimeZone getUserTimeZone(LiteYukonUser user);

}