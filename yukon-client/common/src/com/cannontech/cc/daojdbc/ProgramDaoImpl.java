package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.dao.ProgramNotificationGroupDao;
import com.cannontech.cc.dao.ProgramParameterDao;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.common.util.CachingDaoWrapper;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import com.cannontech.core.dao.NotFoundException;

public class ProgramDaoImpl implements ProgramDao {

    @Autowired private ProgramTypeDaoImpl programTypeDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private ProgramParameterDao programParameterDao;
    @Autowired private ProgramNotificationGroupDao programNotificationGroupDao;
    @Autowired private AvailableProgramGroupDaoImpl programGroupDao;
    private SimpleTableAccessTemplate<Program> template;
    
    @PostConstruct
    public void init() {
        template = new SimpleTableAccessTemplate<Program>(jdbcTemplate, nextValueHelper);
        template.setTableName("CCurtProgram");
        template.setPrimaryKeyField("CCurtProgramID");
        template.setFieldMapper(programFieldMapper); 
    }
    
    @Override
    public Program getForId(Integer id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CCurtProgramID, CCurtProgramName, CCurtProgramTypeID, LastIdentifier, IdentifierPrefix");
        sql.append("FROM CCurtProgram");
        sql.append("WHERE CCurtProgramID").eq(id);
        
        Program result = jdbcTemplate.queryForObject(sql, new ProgramRowMapper());
        return result;
    }

    @Override
    public List<Program> getProgramsForEnergyCompany(Integer energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CCurtProgramID, CCurtProgramName, p.CCurtProgramTypeID, LastIdentifier, IdentifierPrefix");
        sql.append("FROM CCurtProgram p");
        sql.append(  "JOIN CCurtProgramType pt on pt.CCurtProgramTypeID = p.CCurtProgramTypeID");
        sql.append("WHERE pt.EnergyCompanyID").eq(energyCompanyId);
        
        List<Program> result = jdbcTemplate.query(sql, new ProgramRowMapper());
        return result;
    }

    @Override
    public List<Program> getProgramsForType(ProgramType programType) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CCurtProgramID, CCurtProgramName, CCurtProgramTypeID, LastIdentifier, IdentifierPrefix");
        sql.append("FROM CCurtProgram");
        sql.append("WHERE CCurtProgramTypeID").eq(programType.getId());
        
        List<Program> result = jdbcTemplate.query(sql, new ProgramRowMapper(programType));
        return result;
    }

    @Override
    public void save(Program program) {
        template.save(program);
    }

    @Override
    @Transactional
    public void delete(Program program) {
        programGroupDao.deleteFor(program);
        programNotificationGroupDao.deleteForProgram(program);
        programParameterDao.deleteAllForProgram(program);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE");
        sql.append("FROM CCurtProgram");
        sql.append("WHERE CCurtProgramID").eq(program.getId());
        
        jdbcTemplate.update(sql);
    }

    @Override
    public Integer incrementAndReturnIdentifier(Program program) {
        Integer result = program.getLastIdentifier() + 1;
        program.setLastIdentifier(result);
        template.save(program);
        return result;
    }
    
    public String findGearName(int programId, int gearNumber) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder(); 
            sql.append("SELECT GearName FROM LMProgramDirectGear");
            sql.append("WHERE DeviceId").eq(programId);
            sql.append("AND GearNumber").eq(gearNumber);
            String result = jdbcTemplate.queryForString(sql);
            if(result != null && !result.isEmpty())
            {
                return result; 
            }
            throw new NotFoundException("No GearName was found using: programId" + programId +" and gearNumber:" + gearNumber);
        }
        catch (IncorrectResultSizeDataAccessException e) {
               Log.debug(e.getMessage());
        }
        return null;
    }
    
    private FieldMapper<Program> programFieldMapper = new FieldMapper<Program>() {
        @Override
        public void extractValues(MapSqlParameterSource p, Program program) {
            p.addValue("CCurtProgramName", program.getName());
            p.addValue("CCurtProgramTypeID", program.getProgramType().getId());
            p.addValue("LastIdentifier", program.getLastIdentifier());
            p.addValue("IdentifierPrefix", program.getIdentifierPrefix());
            
        }
        @Override
        public Number getPrimaryKey(Program program) {
            return program.getId();
        }
        @Override
        public void setPrimaryKey(Program program, int value) {
            program.setId(value);
        }
    };

    private class ProgramRowMapper implements YukonRowMapper<Program> {
        CachingDaoWrapper<ProgramType> cachingProgramTypeDao;
        
        public ProgramRowMapper(ProgramType... initialItems) {
            cachingProgramTypeDao = new CachingDaoWrapper<ProgramType>(programTypeDao, initialItems);
        }
        
        @Override
        public Program mapRow(YukonResultSet rs) throws SQLException {
            Program program = new Program();
            program.setId(rs.getInt("CCurtProgramID"));
            program.setName(rs.getString("CCurtProgramName"));
            ProgramType programType = cachingProgramTypeDao.getForId(rs.getInt("CCurtProgramTypeID"));
            program.setProgramType(programType);
            program.setLastIdentifier(rs.getInt("LastIdentifier"));
            program.setIdentifierPrefix(rs.getString("IdentifierPrefix"));
            return program;
        }
    }
}
