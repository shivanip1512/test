package com.cannontech.common.userpage.dao;

import java.util.List;

import com.cannontech.common.userpage.model.UserMonitor;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface UserMonitorDao {

    public boolean contains(UserMonitor page);
    public UserMonitor save(UserMonitor page);
    public void delete(UserMonitor page);
    public List<UserMonitor> getMonitorsForUser(LiteYukonUser user);
}
