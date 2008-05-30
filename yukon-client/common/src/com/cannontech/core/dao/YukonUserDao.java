package com.cannontech.core.dao;

import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonUserDao {

    public void changeUsername(int userId, String username);

    public LiteYukonUser getLiteYukonUser(int userId);

    public LiteYukonUser getLiteYukonUser(String userName);

    public LiteContact getLiteContact(int userId);
}