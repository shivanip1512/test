package com.cannontech.message.activemq.impl;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.activemq.YukonTextMessageDao;

public class YukonTextMessageDaoImpl implements YukonTextMessageDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public void addMessageIdMapping(long externalMessageId, long yukonMessageId, LiteYukonUser user, Instant endTime) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink sink = sql.insertInto("ExternalToYukonMessageIdMapping");
        
        sink.addValue("ExternalMessageId", externalMessageId);
        sink.addValue("MessageEndTime", endTime);
        sink.addValue("YukonMessageId", yukonMessageId);
        sink.addValue("UserId", user.getLiteID());

        yukonJdbcTemplate.update(sql);
        
    }
}
