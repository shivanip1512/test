package com.cannontech.database.db.capcontrol;

import java.util.Date;

public interface RecentControls {

    public Date getTimestamp();

    public String getItem();

    public String getEvent();

    public String getUser();
}
