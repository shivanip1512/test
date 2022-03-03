package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.cc.dao.EconomicEventNotifDao;
import com.cannontech.cc.dao.EconomicEventParticipantDao;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventNotif;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.support.IdentifiableUtils;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.cc.service.NotificationReason;
import com.cannontech.cc.service.NotificationState;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class EconomicEventNotifDaoImpl implements EconomicEventNotifDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
    private EconomicEventParticipantDao economicEventParticipantDao;
    private SimpleTableAccessTemplate<EconomicEventNotif> template;
    private NextValueHelper nextValueHelper;

    @Override
    public List<EconomicEventNotif> getForEventAndReason(EconomicEvent event, NotificationReason reason) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtEconomicEventNotif een");
        sql.append(  "join CCurtEEParticipant eep on eep.CCurtEEParticipantID = een.CCurtEconomicParticipantID");
        sql.append("where eep.CCurtEconomicEventID").eq(event.getId());
        sql.append(  "and een.Reason").eq(reason);
        
        List<EconomicEventNotif> result = yukonJdbcTemplate.query(sql, rowMapper);
        setParticipantAndRevisionForNotifsAndEvents(result, event);
        return result;
    }

    @Override
    public List<EconomicEventNotif> getForParticipant(EconomicEventParticipant participant) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtEconomicEventNotif");
        sql.append("where CCurtEconomicParticipantID").eq(participant.getId());
        
        List<EconomicEventNotif> result = yukonJdbcTemplate.query(sql, rowMapper);
        setParticipantAndRevisionForNotifsAndParticipants(result, Collections.singletonList(participant));
        return result;
    }

    @Override
    public List<EconomicEventNotif> getScheduledNotifs() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM CCurtEconomicEventNotif");
        sql.append("WHERE State").eq(NotificationState.SCHEDULED);
        sql.append(  "AND NotificationTime").lte(new Date());
        
        List<EconomicEventNotif> result = yukonJdbcTemplate.query(sql, rowMapper);
        if(result.size() > 0) {
            List<EconomicEventParticipant> participantList = economicEventParticipantDao.getForNotifs(result);
            
            setParticipantAndRevisionForNotifsAndParticipants(result, participantList);
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
        sql.append("delete");
        sql.append("from CCurtEconomicEventNotif");
        sql.append("where CCurtEconomicEventNotifID").eq(notif.getId());
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void deleteForEvent(EconomicEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete");
        sql.append("from CCurtEconomicEventNotif");
        sql.append("where CCurtEconomicParticipantID in (");
        sql.append(  "select CCurtEEParticipantID from CCurtEEParticipant");
        sql.append(  "where CCurtEconomicEventID").eq(event.getId());
        sql.append(")");
        
        yukonJdbcTemplate.update(sql);
    }

    private FieldMapper<EconomicEventNotif> economicEventNotifFieldMapper = 
        new FieldMapper<EconomicEventNotif>() {
        @Override
        public void extractValues(MapSqlParameterSource p, EconomicEventNotif notif) {
            p.addValue("NotificationTime", notif.getNotificationTime());
            p.addValue("NotifTypeID", notif.getNotifTypeId());
            p.addValue("State", notif.getState());
            p.addValue("Reason", notif.getReason());
            p.addValue("CCurtEEPricingID", notif.getRevision().getId());
            p.addValue("CCurtEconomicParticipantID", notif.getParticipant().getId());
        }

        @Override
        public Number getPrimaryKey(EconomicEventNotif notif) {
            return notif.getId();
        }

        @Override
        public void setPrimaryKey(EconomicEventNotif notif, int value) {
            notif.setId(value);
        }
    };

    @PostConstruct
    public void init() throws Exception {
        template = new SimpleTableAccessTemplate<EconomicEventNotif>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("CCurtEconomicEventNotif");
        template.setPrimaryKeyField("CCurtEconomicEventNotifID");
        template.setFieldMapper(economicEventNotifFieldMapper);
    }
    
    private void setParticipantAndRevisionForNotifsAndEvents(List<EconomicEventNotif> notifList, EconomicEvent event) {
        List<EconomicEventParticipant> participantList = economicEventParticipantDao.getForEvent(event);
        Collection<EconomicEventPricing> pricingCol = event.getRevisions().values();
        
        ImmutableMap<Integer, EconomicEventParticipant> participantLookup = IdentifiableUtils.getMap(participantList);
        ImmutableMap<Integer, EconomicEventPricing> pricingLookup = IdentifiableUtils.getMap(pricingCol);
        setParticipantAndRevision(notifList, pricingLookup, participantLookup);
    }
    
    private void setParticipantAndRevisionForNotifsAndParticipants(List<EconomicEventNotif> notifList, Iterable<EconomicEventParticipant> participants) {
        Set<EconomicEventPricing> pricingSet = Sets.newHashSet();
        for (EconomicEventParticipant participant : participants) {
            pricingSet.addAll(participant.getEvent().getRevisions().values());
        }
        
        ImmutableMap<Integer, EconomicEventPricing> pricingLookup = IdentifiableUtils.getMap(pricingSet);
        ImmutableMap<Integer, EconomicEventParticipant> participantLookup = IdentifiableUtils.getMap(participants);
        setParticipantAndRevision(notifList, pricingLookup, participantLookup);
    }
    
    private void setParticipantAndRevision(List<EconomicEventNotif> notifList, 
                                           Map<Integer, EconomicEventPricing> pricingLookup, 
                                           Map<Integer, EconomicEventParticipant> participantLookup) {
        for (EconomicEventNotif notif : notifList) {
            EconomicEventParticipant participant = participantLookup.get(notif.getParticipant().getId());
            notif.setParticipant(participant);
            
            EconomicEventPricing revision = pricingLookup.get(notif.getRevision().getId());
            notif.setRevision(revision);
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

    private YukonRowMapper<EconomicEventNotif> rowMapper = new YukonRowMapper<EconomicEventNotif>() {
        @Override
        public EconomicEventNotif mapRow(YukonResultSet rs) throws SQLException {
            EconomicEventNotif notif = new EconomicEventNotif();
            notif.setId(rs.getInt("CCurtEconomicEventNotifID"));
            Date notificationTime = rs.getDate("NotificationTime");
            notif.setNotificationTime(notificationTime);
            notif.setNotifTypeId(rs.getInt("NotifTypeID"));
            NotificationState state = rs.getEnum("State", NotificationState.class);
            notif.setState(state);
            NotificationReason reason = rs.getEnum("Reason", NotificationReason.class);
            notif.setReason(reason);
            EconomicEventParticipant participant = new EconomicEventParticipant();
            participant.setId(rs.getInt("CCurtEconomicParticipantID"));
            notif.setParticipant(participant);
            EconomicEventPricing revision = new EconomicEventPricing();
            revision.setId(rs.getInt("CCurtEEPricingID"));
            notif.setRevision(revision);
            return notif;
        }
    };
}
