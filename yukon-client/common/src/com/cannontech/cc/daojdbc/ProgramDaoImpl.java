package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.dao.ProgramNotificationGroupDao;
import com.cannontech.cc.dao.ProgramParameterDao;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class ProgramDaoImpl implements ProgramDao, InitializingBean {

    private ProgramTypeDaoImpl programTypeDao;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private SimpleTableAccessTemplate<Program> template;
    private NextValueHelper nextValueHelper;
    
    ProgramParameterDao programParameterDao;
    ProgramNotificationGroupDao programNotificationGroupDao;
    AvailableProgramGroupDaoImpl programGroupDao;
    
    @Override
    public Program getForId(Integer id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtProgram");
        sql.append("where CCurtProgramID").eq(id);
        
        Program result = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        return result;
    }

    @Override
    public List<Program> getProgramsForEnergyCompany(Integer energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select p.* from CCurtProgram p");
        sql.append(  "join CCurtProgramType pt on pt.CCurtProgramTypeID=p.CCurtProgramTypeID");
        sql.append("where pt.EnergyCompanyID").eq(energyCompanyId);
        
        List<Program> result = yukonJdbcTemplate.query(sql, rowMapper);
        return result;
    }

    @Override
    public List<Program> getProgramsForType(ProgramType programType) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtProgram");
        sql.append("where CCurtProgramTypeID").eq(programType.getId());
        
        List<Program> result = yukonJdbcTemplate.query(sql, rowMapper);
        return result;
    }

    @Override
    public void save(Program program) {
        template.save(program);
    }

    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public void delete(Program program) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from CCurtProgram");
        sql.append("where CCurtProgramID").eq(program.getId());
        programGroupDao.deleteFor(program);
        programNotificationGroupDao.deleteForProgram(program);
        programParameterDao.deleteAllForProgram(program);
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public Integer incrementAndReturnIdentifier(Program program) {
        Integer result = program.getLastIdentifier() + 1;
        program.setLastIdentifier(result);
        template.save(program);
        return result;
    }

    private YukonRowMapper<Program> rowMapper = new YukonRowMapper<Program>() {
        public Program mapRow(YukonResultSet rs) throws SQLException {
            Program program = new Program();
            program.setId(rs.getInt("CCurtProgramID"));
            program.setName(rs.getString("CCurtProgramName"));
            program.setProgramType(programTypeDao.getForId(rs.getInt("CCurtProgramTypeID")));
            program.setLastIdentifier(rs.getInt("LastIdentifier"));
            program.setIdentifierPrefix(rs.getString("IdentifierPrefix"));
            return program;
        }
    };
    
    private FieldMapper<Program> programFieldMapper = new FieldMapper<Program>() {
        public void extractValues(MapSqlParameterSource p, Program program) {
            p.addValue("CCurtProgramName", program.getName());
            p.addValue("CCurtProgramTypeID", program.getProgramType().getId());
            p.addValue("LastIdentifier", program.getLastIdentifier());
            p.addValue("IdentifierPrefix", program.getIdentifierPrefix());
            
        }
        public Number getPrimaryKey(Program program) {
            return program.getId();
        }
        public void setPrimaryKey(Program program, int value) {
            program.setId(value);
        }
    };
    
    @Override
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<Program>(yukonJdbcTemplate, nextValueHelper);
        template.withTableName("CCurtProgram");
        template.withPrimaryKeyField("CCurtProgramID");
        template.withFieldMapper(programFieldMapper); 
    }
    
    @Autowired
    public void setProgramTypeDao(ProgramTypeDaoImpl programTypeDao) {
        this.programTypeDao = programTypeDao;
    }
    
    @Autowired
    public void setProgramGroupDao(AvailableProgramGroupDaoImpl programGroupDao) {
        this.programGroupDao = programGroupDao;
    }
    
    @Autowired
    public void setProgramParameterDao(ProgramParameterDao programParameterDao) {
        this.programParameterDao = programParameterDao;
    }
    
    @Autowired
    public void setProgramNotificationGroupDao(ProgramNotificationGroupDao programNotificationGroupDao) {
        this.programNotificationGroupDao = programNotificationGroupDao;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

}
