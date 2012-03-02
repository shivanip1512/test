package com.cannontech.message.activemq;

import org.joda.time.Instant;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonTextMessageDao {
    public void addMessageIdMapping(long externalMessageId, long yukonMessageId, LiteYukonUser user, Instant endTime);
}
