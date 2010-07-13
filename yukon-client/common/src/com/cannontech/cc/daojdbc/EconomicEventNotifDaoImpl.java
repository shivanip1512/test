package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.cc.dao.EconomicEventNotifDao;
import com.cannontech.cc.dao.EconomicEventParticipantDao;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventNotif;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.enums.NotificationReason;
import com.cannontech.enums.NotificationState;

public class EconomicEventNotifDaoImpl implements EconomicEventNotifDao,
        InitializingBean {

    YukonJdbcTemplate yukonJdbcTemplate;
    EconomicEventParticipantDao economicEventParticipantDao;
    SimpleTableAccessTemplate<EconomicEventNotif> template;
    NextValueHelper nextValueHelper;
    
    @Override
    public EconomicEventNotif getForId(Integer id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtEconomicEventNotif");
        sql.append("where CCurtEconomicEventNotifID").eq(id);
        
        List<EconomicEventNotif> result = yukonJdbcTemplate.query(sql, rowMapper);
        List<EconomicEventParticipant> participantList = economicEventParticipantDao.getForNotifs(result);
        
        setParticipantAndRevision(result, participantList.get(0).getEvent());
        return result.get(0);
    }
    
    @Override
    public List<EconomicEventNotif> getForEvent(EconomicEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select een.* from CCurtEconomicEventNotif een");
        sql.append(  "join CCurtEconomicParticipant eep on eep.CCurtEconomicParticipantID = een.CCurtEconomicParticipantID");
        sql.append("where eep.CustomerID").eq(event.getId());
        
        List<EconomicEventNotif> result = yukonJdbcTemplate.query(sql, rowMapper);
        setParticipantAndRevision(result, event);
        return result;
    }

    @Override
    public List<EconomicEventNotif> getForEventAndReason(EconomicEvent event, NotificationReason reason) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtEconomicEventNotif een");
        sql.append(  "join CCurtEEParticipant eep on eep.CCurtEEParticipantID = een.CCurtEconomicParticipantID");
        sql.append("where eep.CustomerID").eq(event.getId());
        sql.append(  "and een.Reason").eq(reason);
        
        List<EconomicEventNotif> result = yukonJdbcTemplate.query(sql, rowMapper);
        setParticipantAndRevision(result, event);
        return result;
    }

    @Override
    public List<EconomicEventNotif> getForParticipant(EconomicEventParticipant participant) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtEconomicEventNotif");
        sql.append("where CCurtEconomicParticipantID").eq(participant.getId());
        
        List<EconomicEventNotif> result = yukonJdbcTemplate.query(sql, rowMapper);
        setParticipantAndRevision(result, participant.getEvent());
        return result;
    }

    @Override
    public List<EconomicEventNotif> getScheduledNotifs() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtEconomicEventNotif");
        sql.append("where State").eq(NotificationState.SCHEDULED);
        
        List<EconomicEventNotif> result = yukonJdbcTemplate.query(sql, rowMapper);
        if(result.size() > 0) {
            List<EconomicEventParticipant> participantList = economicEventParticipantDao.getForNotifs(result);
            
            for (EconomicEventParticipant participant : participantList) {
                setParticipantAndRevision(result, participant.getEvent());
            }
        }
        return result;
    }

    @Override
    public void save(EconomicEventNotif notif) {
        template.save(notif);
    }

    @Override
    public void delete(EconomicEventNotif notif) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from CCurtEconomicEventNotif");
        sql.append("where CCurtEconomicEventNotifID").eq(notif.getId());
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void deleteForEvent(EconomicEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from CCurtEconomicEventNotif");
        sql.append("where CCurtEconomicParticipantID in (");
        sql.append(    "select CCurtEconomicParticipantID from CCurtEEParticipant");
        sql.append(    "where CCurtEconomicEventID").eq(event.getId());
        sql.append(")");
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void deleteForParticipant(EconomicEventParticipant participant) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from CCurtEconomicEventNotif");
        sql.append("where CCurtEconomicParticipantID").eq(participant.getId());
        
        yukonJdbcTemplate.update(sql);
    }

    private YukonRowMapper<EconomicEventNotif> rowMapper = new YukonRowMapper<EconomicEventNotif>() {
        public EconomicEventNotif mapRow(YukonResultSet rs) throws SQLException {
            EconomicEventNotif notif = new EconomicEventNotif();
            notif.setId(rs.getInt("CCurtEconomicEventNotifID"));
            Date notificationTime = rs.getTimestamp("NotificationTime");
            notif.setNotificationTime(notificationTime);
            notif.setNotifTypeId(rs.getInt("NotifTypeID"));
            NotificationState state = NotificationState.valueOf(rs.getString("State"));
            notif.setState(state);
            NotificationReason reason = NotificationReason.valueOf(rs.getString("Reason"));
            notif.setReason(reason);
            //dummy participant
            EconomicEventParticipant participant = new EconomicEventParticipant();
            participant.setId(rs.getInt("CCurtEconomicParticipantID"));
            notif.setParticipant(participant);
            //dummy pricing
            EconomicEventPricing revision = new EconomicEventPricing();
            revision.setId(rs.getInt("CCurtEEPricingID"));
            notif.setRevision(revision);
            return notif;
        }
    };
    
    private FieldMapper<EconomicEventNotif> economicEventNotifFieldMapper = 
        new FieldMapper<EconomicEventNotif>() {
        public void extractValues(MapSqlParameterSource p, EconomicEventNotif notif) {
            p.addValue("NotificationTime", notif.getNotificationTime());
            p.addValue("NotifTypeID", notif.getNotifTypeId());
            p.addValue("State", notif.getState());
            p.addValue("Reason", notif.getReason());
            p.addValue("CCurtEEPricingID", notif.getRevision().getId());
            p.addValue("CCurtEconomicParticipantID", notif.getParticipant().getId());
        }

        public Number getPrimaryKey(EconomicEventNotif notif) {
            return notif.getId();
        }

        public void setPrimaryKey(EconomicEventNotif notif, int value) {
            notif.setId(value);
        }
    };

    @Override
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<EconomicEventNotif>(yukonJdbcTemplate, nextValueHelper);
        template.withTableName("CCurtEconomicEventNotif");
        template.withPrimaryKeyField("CCurtEconomicEventNotifID");
        template.withFieldMapper(economicEventNotifFieldMapper);
    }
    
    private void setParticipantAndRevision(List<EconomicEventNotif> notifList, EconomicEvent event) {
        List<EconomicEventParticipant> participantList = economicEventParticipantDao.getForEvent(event);
        Collection<EconomicEventPricing> pricingCol = event.getRevisions().values();
        for (EconomicEventNotif notif : notifList) {
            //set participant
            for (EconomicEventParticipant participant : participantList) {
                if(participant.getId().intValue() == notif.getParticipant().getId().intValue()){
                    notif.setParticipant(participant);
                    continue;
                }
            }
            //set revision
            for (EconomicEventPricing revision : pricingCol) {
                if(revision.getId().intValue() == notif.getRevision().getId().intValue()) {
                    notif.setRevision(revision);
                    continue;
                }
            }
        }
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setEconomicEventParticipantDao(EconomicEventParticipantDao economicEventParticipantDao) {
        this.economicEventParticipantDao = economicEventParticipantDao;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

}
