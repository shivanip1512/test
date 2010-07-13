package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.cc.dao.AccountingEventDao;
import com.cannontech.cc.dao.AccountingEventParticipantDao;
import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Program;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.incrementer.NextValueHelper;

public class AccountingEventDaoImpl implements InitializingBean,
        AccountingEventDao {
    
    YukonJdbcTemplate yukonJdbcTemplate;
    SimpleTableAccessTemplate<AccountingEvent> template;
    NextValueHelper nextValueHelper;
    ProgramDao programDao;
    AccountingEventParticipantDao accountingEventParticipantDao;

    @Override
    public AccountingEvent getForId(Integer id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtAcctEvent");
        sql.append("where CCurtAcctEventID").eq(id);
        
        AccountingEvent result = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        return result;
    }

    @Override
    public List<AccountingEvent> getAllForProgram(Program program) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtAcctEvent");
        sql.append("where CCurtProgramID").eq(program.getId());
        
       List<AccountingEvent> result = yukonJdbcTemplate.query(sql, rowMapper);
       return result;
    }

    @Override
    public List<AccountingEvent> getAllForCustomer(CICustomerStub customer) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ae.* from CCurtAcctEvent ae");
        sql.append(  "join CCurtAcctEventParticipant aep on aep.CCurtAcctEventID = ae.CCurtAcctEventID");
        sql.append("where aep.CustomerID").eq(customer.getId());
        
        List<AccountingEvent> result = yukonJdbcTemplate.query(sql, rowMapper);
        return result;
    }

    @Override
    public List<AccountingEvent> getAllForEnergyCompany(LiteEnergyCompany energyCompany) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ae.* from CCurtAcctEvent ae");
        sql.append(  "join CCurtProgram p on ae.CCurtProgramID = p.CCurtProgramID");
        sql.append(  "join CCurtProgramType pt on pt.CCurtProgramTypeID = p.CCurtProgramTypeID");
        sql.append("where pt.EnergyCompanyID").eq(energyCompany.getLiteID());
        
        List<AccountingEvent> result = yukonJdbcTemplate.query(sql, rowMapper);
        return result;
    }

    @Override
    public void save(AccountingEvent event) {
        template.save(event);
    }

    @Override
    public void delete(AccountingEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from CCurtAcctEvent");
        sql.append("where CCurtAcctEventID").eq(event.getId());
        
        accountingEventParticipantDao.deleteForEvent(event);
        yukonJdbcTemplate.update(sql);
    }

    private YukonRowMapper<AccountingEvent> rowMapper = new YukonRowMapper<AccountingEvent>() {
        public AccountingEvent mapRow(YukonResultSet rs) throws SQLException {
            AccountingEvent event = new AccountingEvent();
            event.setId(rs.getInt("CCurtAcctEventID"));
            Program program = programDao.getForId(rs.getInt("CCurtProgramID"));
            event.setProgram(program);
            event.setDuration(rs.getInt("Duration"));
            event.setReason(rs.getString("Reason"));
            Date startTime = rs.getTimestamp("StartTime");
            event.setStartTime(startTime);
            event.setIdentifier(rs.getInt("Identifier"));

            return event;
        }
    };
    
    private FieldMapper<AccountingEvent> accountingEventFieldMapper = new FieldMapper<AccountingEvent>() {
        public void extractValues(MapSqlParameterSource p, AccountingEvent event) {
            p.addValue("CCurtProgramID", event.getProgram().getId());
            p.addValue("Duration", event.getDuration());
            p.addValue("Reason", event.getReason());
            p.addValue("StartTime", event.getStartTime());
            p.addValue("Identifier", event.getIdentifier());
        }
        public Number getPrimaryKey(AccountingEvent event) {
            return event.getId();
        }
        public void setPrimaryKey(AccountingEvent event, int value) {
            event.setId(value);
        }
    };

    @Override
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<AccountingEvent>(yukonJdbcTemplate, nextValueHelper);
        template.withTableName("CCurtAcctEvent");
        template.withPrimaryKeyField("CCurtAcctEventID");
        template.withFieldMapper(accountingEventFieldMapper); 
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
    
    public void setAccountingEventParticipantDao(AccountingEventParticipantDao accountingEventParticipantDao) {
        this.accountingEventParticipantDao = accountingEventParticipantDao;
    }

}
