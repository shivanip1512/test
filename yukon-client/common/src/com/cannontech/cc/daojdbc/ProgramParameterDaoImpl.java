package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.dao.ProgramParameterDao;
import com.cannontech.cc.dao.UnknownParameterException;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameter;
import com.cannontech.cc.model.ProgramParameterKey;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class ProgramParameterDaoImpl implements InitializingBean, ProgramParameterDao {

    private ProgramDao programDao;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private SimpleTableAccessTemplate<ProgramParameter> template;
    private NextValueHelper nextValueHelper;
   
    @Override
    public ProgramParameter getFor(Program program, ProgramParameterKey parameterKey)
            throws UnknownParameterException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtProgramParameter");
        sql.append("where CCurtProgramID").eq(program.getId());
        sql.append(  "and ParameterKey").eq(parameterKey.name());
        
        try {
            ProgramParameter result = yukonJdbcTemplate.queryForObject(sql, rowMapper);
            return result;
        } catch (DataAccessException e) {
            throw new UnknownParameterException(parameterKey);
        }
    }

    @Override
    public ProgramParameter getForId(Integer id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtProgramParameter");
        sql.append("where CCurtProgramParameterID").eq(id);
        
        ProgramParameter result = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        return result;
    }
    
    @Override
    public List<ProgramParameter> getAllForProgram(Program program) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtProgramParameter");
        sql.append("where CCurtProgramID").eq(program.getId());
        
        List<ProgramParameter> result = yukonJdbcTemplate.query(sql, rowMapper);
        return result;
    }

    @Override
    public void save(ProgramParameter object) {
        template.save(object);
    }

    @Override
    public void delete(ProgramParameter programParameter) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from CCurtProgramParameter");
        sql.append("where CCurtProgramParameterID").eq(programParameter.getId());
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void deleteAllForProgram(Program program) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from CCurtProgramParameter");
        sql.append("where CCurtProgramID").eq(program.getId());
        
        yukonJdbcTemplate.update(sql);
    }

    private YukonRowMapper<ProgramParameter> rowMapper = new YukonRowMapper<ProgramParameter>() {
        public ProgramParameter mapRow(YukonResultSet rs) throws SQLException {
            ProgramParameter programParameter = new ProgramParameter();
            programParameter.setId(rs.getInt("CCurtProgramParameterID"));
            programParameter.setParameterValue(rs.getString("ParameterValue"));
            programParameter.setParameterKey(ProgramParameterKey.valueOf(rs.getString("ParameterKey")));
            programParameter.setProgram(programDao.getForId(rs.getInt("CCurtProgramID")));
            return programParameter;
        }
    };
    
    private FieldMapper<ProgramParameter> programParameterFieldMapper = new FieldMapper<ProgramParameter>() {
        public void extractValues(MapSqlParameterSource p, ProgramParameter programParameter) {
            p.addValue("ParameterValue",programParameter.getParameterValue());
            p.addValue("ParameterKey", programParameter.getParameterKey());
            p.addValue("CCurtProgramID", programParameter.getProgram().getId());
            
        }
        public Number getPrimaryKey(ProgramParameter programParameter) {
            return programParameter.getId();
        }
        public void setPrimaryKey(ProgramParameter programParameter, int value) {
            programParameter.setId(value);
        }
    };

    @Override
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<ProgramParameter>(yukonJdbcTemplate, nextValueHelper);
        template.withTableName("CCurtProgramParameter");
        template.withPrimaryKeyField("CCurtProgramParameterID");
        template.withFieldMapper(programParameterFieldMapper);
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
}
