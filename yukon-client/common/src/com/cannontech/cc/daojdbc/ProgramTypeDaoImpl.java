package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.cc.dao.ProgramTypeDao;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class ProgramTypeDaoImpl implements ProgramTypeDao, InitializingBean{
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    private SimpleTableAccessTemplate<ProgramType> template;
    private NextValueHelper nextValueHelper;

    @Override
    public ProgramType getForId(Integer id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtProgramType");
        sql.append("where CCurtProgramTypeID").eq(id);
        
        return yukonJdbcTemplate.queryForObject(sql, rowMapper);
    }

    @Override
    public List<ProgramType> getAllProgramTypes(Integer energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtProgramType");
        sql.append("where EnergyCompanyID").eq(energyCompanyId);
        
        return yukonJdbcTemplate.query(sql, rowMapper);
    }
    
    private FieldMapper<ProgramType> programTypeFieldMapper = new FieldMapper<ProgramType>() {
        public void extractValues(MapSqlParameterSource p, ProgramType programType) {
            p.addValue("CCurtProgramTypeID", programType.getId());
            p.addValue("EnergyCompanyID", programType.getEnergyCompanyId());
            p.addValue("CCurtProgramTypeStrategy", programType.getStrategy());
            p.addValue("CCurtProgramTypeName", programType.getName());
            
        }
        public Number getPrimaryKey(ProgramType programType) {
            return programType.getId();
        }
        public void setPrimaryKey(ProgramType programType, int value) {
            programType.setId(value);
        }
    };

    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<ProgramType>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("CCurtProgramType");
        template.setPrimaryKeyField("CCurtProgramTypeID");
        template.setFieldMapper(programTypeFieldMapper); 
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    private YukonRowMapper<ProgramType> rowMapper = new YukonRowMapper<ProgramType>() {
        public ProgramType mapRow(YukonResultSet rs) throws SQLException {
            ProgramType programType = new ProgramType();
            programType.setName(rs.getString("CCurtProgramTypeName"));
            programType.setStrategy(rs.getString("CCurtProgramTypeStrategy"));
            programType.setId(rs.getInt("CCurtProgramTypeID"));
            programType.setEnergyCompanyId(rs.getInt("EnergyCompanyID"));
            return programType;
        }
    };
}