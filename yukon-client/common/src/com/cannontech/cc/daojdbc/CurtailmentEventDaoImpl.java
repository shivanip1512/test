package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.cc.dao.CurtailmentEventDao;
import com.cannontech.cc.dao.CurtailmentEventParticipantDao;
import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.enums.CurtailmentEventState;
import com.cannontech.common.util.CachingDaoWrapper;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.incrementer.NextValueHelper;

public class CurtailmentEventDaoImpl implements InitializingBean, CurtailmentEventDao {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    private ProgramDao programDao;
    private CurtailmentEventParticipantDao curtailmentEventParticipantDao;
    private SimpleTableAccessTemplate<CurtailmentEvent> template;
    private NextValueHelper nextValueHelper;

    @Override
    public CurtailmentEvent getForId(Integer id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtCurtailmentEvent");
        sql.append("where CCurtCurtailmentEventID").eq(id);
        
        CurtailmentEvent result = yukonJdbcTemplate.queryForObject(sql, new CurtailmentEventRowMapper());
        return result;
    }

    @Override
    public List<CurtailmentEvent> getAllForCustomer(CICustomerStub customer) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ce.*");
        sql.append("from CCurtCurtailmentEvent ce");
        sql.append(  "join CCurtCEParticipant cep on cep.CCurtCurtailmentEventID = ce.CCurtCurtailmentEventID");
        sql.append("where cep.CustomerID").eq(customer.getId());
        
        List<CurtailmentEvent> result = yukonJdbcTemplate.query(sql, new CurtailmentEventRowMapper());
        return result;
    }

    @Override
    public List<CurtailmentEvent> getAllForEnergyCompany(LiteEnergyCompany energyCompany) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ce.*");
        sql.append("from CCurtCurtailmentEvent ce");
        sql.append(  "join CCurtProgram p on ce.CCurtProgramID = p.CCurtProgramID");
        sql.append(  "join CCurtProgramType pt on pt.CCurtProgramTypeID = p.CCurtProgramTypeID");
        sql.append("where pt.EnergyCompanyID").eq(energyCompany.getEnergyCompanyID());
        
        List<CurtailmentEvent> result = yukonJdbcTemplate.query(sql, new CurtailmentEventRowMapper());
        return result;
    }

    @Override
    public List<CurtailmentEvent> getAllForProgram(Program program) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtCurtailmentEvent");
        sql.append("where CCurtProgramID").eq(program.getId());
        
        List<CurtailmentEvent> result = yukonJdbcTemplate.query(sql, new CurtailmentEventRowMapper(program));
        return result;
    }

    @Override
    public void save(CurtailmentEvent curtailmentEvent) {
        template.save(curtailmentEvent);
    }

    @Override
    public void delete(CurtailmentEvent curtailmentEvent) {
        curtailmentEventParticipantDao.deleteForEvent(curtailmentEvent);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete");
        sql.append("from CCurtCurtailmentEvent");
        sql.append("where CCurtCurtailmentEventID").eq(curtailmentEvent.getId());
        
        yukonJdbcTemplate.update(sql);
    }

    private FieldMapper<CurtailmentEvent> curtailmentEventFieldMapper = new FieldMapper<CurtailmentEvent>() {
        public void extractValues(MapSqlParameterSource p, CurtailmentEvent curtailmentEvent) {
            p.addValue("CCurtProgramID", curtailmentEvent.getProgram().getId());
            p.addValue("NotificationTime", curtailmentEvent.getNotificationTime());
            p.addValue("Duration", curtailmentEvent.getDuration());
            p.addValue("Message", curtailmentEvent.getMessage());
            p.addValue("State", curtailmentEvent.getState().toString());
            p.addValue("StartTime", curtailmentEvent.getStartTime());
            p.addValue("IDENTIFIER", curtailmentEvent.getIdentifier());
        }
        public Number getPrimaryKey(CurtailmentEvent curtailmentEvent) {
            return curtailmentEvent.getId();
        }
        public void setPrimaryKey(CurtailmentEvent curtailmentEvent, int value) {
            curtailmentEvent.setId(value);
        }
    };

    @Override
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<CurtailmentEvent>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("CCurtCurtailmentEvent");
        template.setPrimaryKeyField("CCurtCurtailmentEventID");
        template.setFieldMapper(curtailmentEventFieldMapper); 
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }
    
    public void setCurtailmentEventParticipantDao(CurtailmentEventParticipantDao curtailmentEventParticipantDao) {
      this.curtailmentEventParticipantDao = curtailmentEventParticipantDao;
    }

    private class CurtailmentEventRowMapper implements YukonRowMapper<CurtailmentEvent> {
        private CachingDaoWrapper<Program> cachingProgramDao;
        
        public CurtailmentEventRowMapper(Program... initialItems) {
            cachingProgramDao = new CachingDaoWrapper<Program>(programDao, initialItems);
        }
        
        public CurtailmentEvent mapRow(YukonResultSet rs) throws SQLException {
            CurtailmentEvent curtailmentEvent = new CurtailmentEvent();
            curtailmentEvent.setId(rs.getInt("CCurtCurtailmentEventID"));
            Program program = cachingProgramDao.getForId(rs.getInt("CCurtProgramID"));
            curtailmentEvent.setProgram(program);
            Date notificationTime = rs.getDate("NotificationTime");
            curtailmentEvent.setNotificationTime(notificationTime);
            curtailmentEvent.setDuration(rs.getInt("Duration"));
            curtailmentEvent.setMessage(rs.getString("Message"));
            curtailmentEvent.setState(CurtailmentEventState.valueOf(rs.getString("State")));
            Date startTime = rs.getDate("StartTime");
            curtailmentEvent.setStartTime(startTime);
            curtailmentEvent.setIdentifier(rs.getInt("IDENTIFIER"));
            
            return curtailmentEvent;
        }
    }
}
