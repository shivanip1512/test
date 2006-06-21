package com.cannontech.core.dao;

import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonUserDao {

    public LiteYukonUser getLiteYukonUser(int userID_);

    public LiteYukonUser getLiteYukonUser(String userName_);

    public LiteContact getLiteContact(int userID_);

}