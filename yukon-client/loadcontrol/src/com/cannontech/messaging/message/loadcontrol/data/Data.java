package com.cannontech.messaging.message.loadcontrol.data;

import com.cannontech.common.pao.PaoType;

public interface Data {
    String getYukonDescription();

    Integer getYukonId();

    String getYukonName();

    PaoType getYukonType();

    void setYukonDescription(String newYukonDescription);

    void setYukonId(Integer newYukonID);

    void setYukonName(String newYukonName);

    void setYukonType(PaoType newYukonType);
}
