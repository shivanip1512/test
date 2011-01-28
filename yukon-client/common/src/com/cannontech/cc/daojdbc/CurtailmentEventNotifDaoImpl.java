package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.cc.dao.CurtailmentEventNotifDao;
import com.cannontech.cc.dao.CurtailmentEventParticipantDao;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventNotif;
import com.cannontech.cc.model.CurtailmentEventParticipant;
import com.cannontech.common.util.CachingDaoWrapper;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.enums.NotificationReason;
import com.cannontech.enums.NotificationState;

public class CurtailmentEventNotifDaoImpl implements CurtailmentEventNotifDao, InitializingBean {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    private SimpleTableAccessTemplate<CurtailmentEventNotif> template;
    private NextValueHelper nextValueHelper;
    private CurtailmentEventParticipantDao curtailmentEventParticipantDao;

    @Override
    public List<CurtailmentEventNotif> getForEvent(CurtailmentEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select cen.*");
        sql.append("from CCurtCENotif cen");
        sql.append(  "join CCurtCEParticipant cep on cep.CCurtCEParticipantID = cen.CCurtCEParticipantID");
        sql.append("where cep.CCurtCurtailmentEventID").eq(event.getId());
        
        List<CurtailmentEventNotif> result = yukonJdbcTemplate.query(sql, new CurtailmentEventNotifRowMapper());
        return result;
    }

    @Override
    public List<CurtailmentEventNotif> getForEventAndReason(CurtailmentEvent event, NotificationReason reason) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select cen.*");
        sql.append("from CCurtCENotif cen");
        sql.append(  "join CCurtCEParticipant cep on cep.CCurtCEParticipantID = cen.CCurtCEParticipantID");
        sql.append("where cep.CCurtCurtailmentEventID").eq(event.getId());
        sql.append(  "and cen.Reason").eq(reason);
        
        List<CurtailmentEventNotif> result = yukonJdbcTemplate.query(sql, new CurtailmentEventNotifRowMapper());
        return result;
    }

    @Override
    public List<CurtailmentEventNotif> getScheduledNotifs() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtCENotif");
        sql.append("where State").eq(NotificationState.SCHEDULED);
        
        List<CurtailmentEventNotif> result = yukonJdbcTemplate.query(sql, new CurtailmentEventNotifRowMapper());
        return result;
    }

    @Override
    public void save(CurtailmentEventNotif eventNotif) {
        template.save(eventNotif);
    }

    @Override
    public void delete(CurtailmentEventNotif eventNotif) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete");
        sql.append("from CCurtCENotif");
        sql.append("where CCurtCENotifID").eq(eventNotif.getId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void deleteForEvent(CurtailmentEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete");
        sql.append("from CCurtCENotif");
        sql.append("where CCurtCEParticipantID IN (");
        sql.append(    "select cen.CCurtCEParticipantID from CCurtCENotif cen");
        sql.append(      "join CCurtCEParticipant cep on cen.CCurtCEParticipantID = cep.CCurtCEParticipantID");
        sql.append(    "where cep.CCurtCurtailmentEventID").eq(event.getId());
        sql.append(")");
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void deleteForParticipant(CurtailmentEventParticipant participant) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete");
        sql.append("from CCurtCENotif");
        sql.append("where CCurtCEParticipantID").eq(participant.getId());
        
        yukonJdbcTemplate.update(sql);
    }

    
    private FieldMapper<CurtailmentEventNotif> curtailmentEventNotifFieldMapper = new FieldMapper<CurtailmentEventNotif>() {
        public void extractValues(MapSqlParameterSource p, CurtailmentEventNotif curtailmentEventNotif) {
            p.addValue("NotificationTime", curtailmentEventNotif.getNotificationTime());
            p.addValue("NotifTypeID", curtailmentEventNotif.getNotifTypeId());
            p.addValue("State", curtailmentEventNotif.getState());
            p.addValue("Reason", curtailmentEventNotif.getReason());
            p.addValue("CCurtCEParticipantID", curtailmentEventNotif.getParticipant().getId());
        }
        public Number getPrimaryKey(CurtailmentEventNotif curtailmentEventNotif) {
            return curtailmentEventNotif.getId();
        }
        public void setPrimaryKey(CurtailmentEventNotif curtailmentEventNotif, int value) {
            curtailmentEventNotif.setId(value);
        }
    };
    
    @Override
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<CurtailmentEventNotif>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("CCurtCENotif");
        template.setPrimaryKeyField("CCurtCENotifID");
        template.setFieldMapper(curtailmentEventNotifFieldMapper); 
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setCurtailmentEventParticipantDao(CurtailmentEventParticipantDao curtailmentEventParticipantDao) {
        this.curtailmentEventParticipantDao = curtailmentEventParticipantDao;
    }

    private class CurtailmentEventNotifRowMapper implements YukonRowMapper<CurtailmentEventNotif> {
        private CachingDaoWrapper<CurtailmentEventParticipant> cachingCurtailmentEventParticipantDao;
        
        public CurtailmentEventNotifRowMapper(CurtailmentEventParticipant... initialItems) {
            cachingCurtailmentEventParticipantDao = new CachingDaoWrapper<CurtailmentEventParticipant>(curtailmentEventParticipantDao, initialItems);
        }
        
        public CurtailmentEventNotif mapRow(YukonResultSet rs) throws SQLException {
            CurtailmentEventNotif curtailmentEventNotif = new CurtailmentEventNotif();
            curtailmentEventNotif.setId(rs.getInt("CCurtCENotifID"));
            Date notificationTime = rs.getDate("NotificationTime");
            curtailmentEventNotif.setNotificationTime(notificationTime);
            curtailmentEventNotif.setNotifTypeId(rs.getInt("NotifTypeID"));
            NotificationState state = rs.getEnum("State", NotificationState.class);
            curtailmentEventNotif.setState(state);
            NotificationReason reason = rs.getEnum("Reason", NotificationReason.class);
            curtailmentEventNotif.setReason(reason);
            CurtailmentEventParticipant participant = cachingCurtailmentEventParticipantDao.getForId(rs.getInt("CCurtCEParticipantID"));
            curtailmentEventNotif.setParticipant(participant);
            
            return curtailmentEventNotif;
        }
    }
}
