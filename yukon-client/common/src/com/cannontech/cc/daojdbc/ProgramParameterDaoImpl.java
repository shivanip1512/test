package com.cannontech.cc.daojdbc;

import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.dao.ProgramParameterDao;
import com.cannontech.cc.dao.UnknownParameterException;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameter;
import com.cannontech.cc.model.ProgramParameterKey;
import com.cannontech.common.util.CachingDaoWrapper;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class ProgramParameterDaoImpl implements ProgramParameterDao {

    private ProgramDao programDao;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private SimpleTableAccessTemplate<ProgramParameter> template;
    private NextValueHelper nextValueHelper;
   
    @Override
    public ProgramParameter getFor(Program program, ProgramParameterKey parameterKey)
            throws UnknownParameterException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtProgramParameter");
        sql.append("where CCurtProgramID").eq(program.getId());
        sql.append(  "and ParameterKey").eq(parameterKey);
        
        try {
            ProgramParameter result = yukonJdbcTemplate.queryForObject(sql, new ProgramParameterRowMapper(program));
            return result;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new UnknownParameterException(parameterKey);
        }
    }

    @Override
    public void save(ProgramParameter object) {
        template.save(object);
    }

    @Override
    public void deleteAllForProgram(Program program) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete");
        sql.append("from CCurtProgramParameter");
        sql.append("where CCurtProgramID").eq(program.getId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    private FieldMapper<ProgramParameter> programParameterFieldMapper = new FieldMapper<ProgramParameter>() {
        @Override
        public void extractValues(MapSqlParameterSource p, ProgramParameter programParameter) {
            p.addValue("ParameterValue",programParameter.getParameterValue());
            p.addValue("ParameterKey", programParameter.getParameterKey());
            p.addValue("CCurtProgramID", programParameter.getProgram().getId());
            
        }
        @Override
        public Number getPrimaryKey(ProgramParameter programParameter) {
            return programParameter.getId();
        }
        @Override
        public void setPrimaryKey(ProgramParameter programParameter, int value) {
            programParameter.setId(value);
        }
    };

    @PostConstruct
    public void init() throws Exception {
        template = new SimpleTableAccessTemplate<ProgramParameter>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("CCurtProgramParameter");
        template.setPrimaryKeyField("CCurtProgramParameterID");
        template.setFieldMapper(programParameterFieldMapper);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setProgramDaoImpl(ProgramDao programDao) {
        this.programDao = programDao;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    @Override
    public String getParameterValue(Program program, ProgramParameterKey key) {
        try {
            return getFor(program,key).getParameterValue();
        } catch (UnknownParameterException e) {
            return key.getDefaultValue();
        }
    }

    @Override
    public float getParameterValueFloat(Program program, ProgramParameterKey key) {
        return Float.parseFloat(getParameterValue(program,key));
    }

    @Override
    public int getParameterValueInt(Program program, ProgramParameterKey key) {
        return Integer.parseInt(getParameterValue(program,key));
    }

    private class ProgramParameterRowMapper implements YukonRowMapper<ProgramParameter> {
        CachingDaoWrapper<Program> cachingProgramDao;
        
        public ProgramParameterRowMapper(Program... initialItems) {
            cachingProgramDao = new CachingDaoWrapper<Program>(programDao, initialItems);
        }
        
        @Override
        public ProgramParameter mapRow(YukonResultSet rs) throws SQLException {
            ProgramParameter programParameter = new ProgramParameter();
            programParameter.setId(rs.getInt("CCurtProgramParameterID"));
            programParameter.setParameterValue(rs.getString("ParameterValue"));
            ProgramParameterKey parameterKey = rs.getEnum("ParameterKey", ProgramParameterKey.class);
            programParameter.setParameterKey(parameterKey);
            Program program = cachingProgramDao.getForId(rs.getInt("CCurtProgramID"));
            programParameter.setProgram(program);
            return programParameter;
        }
    }
}
