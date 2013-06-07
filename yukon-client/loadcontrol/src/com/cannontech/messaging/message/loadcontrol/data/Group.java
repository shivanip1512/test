package com.cannontech.messaging.message.loadcontrol.data;

import java.util.Date;

public interface Group {

    public Integer getYukonId();

    public String getName();

    public Boolean getDisableFlag();

    public String getGroupControlStateString();

    public Date getGroupTime();

    public String getStatistics();

    public Double getReduction();

    public Integer getOrder();

    public boolean isRampingIn();

    public boolean isRampingOut();
}
