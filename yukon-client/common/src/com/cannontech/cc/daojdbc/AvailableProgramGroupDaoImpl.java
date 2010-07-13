package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.cc.dao.AvailableProgramGroupDao;
import com.cannontech.cc.dao.GroupDao;
import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.model.AvailableProgramGroup;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.Program;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class AvailableProgramGroupDaoImpl implements InitializingBean,
        AvailableProgramGroupDao {
    
    YukonJdbcTemplate yukonJdbcTemplate;
    SimpleTableAccessTemplate<AvailableProgramGroup> template;
    NextValueHelper nextValueHelper;
    ProgramDao programDao;
    GroupDao groupDao;

    @Override
    public AvailableProgramGroup getForId(Integer id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtProgramGroup");
        sql.append("where CCurtProgramGroupID").eq(id);
        
        AvailableProgramGroup result = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        return result;
    }

    @Override
    public List<AvailableProgramGroup> getAllForProgram(Program program) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtProgramGroup");
        sql.append("where CCurtProgramID").eq(program.getId());
        
        List<AvailableProgramGroup> result = yukonJdbcTemplate.query(sql, rowMapper);
        return result;
    }

    @Override
    public void save(AvailableProgramGroup programGroup) {
        template.save(programGroup);
    }

    @Override
    public void delete(AvailableProgramGroup programGroup) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from CCurtProgramGroup");
        sql.append("where CCurtProgramGroupID").eq(programGroup.getId());
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void deleteFor(Group group) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from CCurtProgramGroup");
        sql.append("where CCurtGroupID").eq(group.getId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void deleteFor(Program program) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from CCurtProgramGroup");
        sql.append("where CCurtProgramID").eq(program.getId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    private YukonRowMapper<AvailableProgramGroup> rowMapper = new YukonRowMapper<AvailableProgramGroup>() {
        public AvailableProgramGroup mapRow(YukonResultSet rs) throws SQLException {
            AvailableProgramGroup programGroup = new AvailableProgramGroup();
            programGroup.setId(rs.getInt("CCurtProgramGroupID"));
            Program program = programDao.getForId(rs.getInt("CCurtProgramID"));
            programGroup.setProgram(program);
            Group group = groupDao.getForId(rs.getInt("CCurtGroupID"));
            programGroup.setGroup(group);
            
            return programGroup;
        }
    };

    private FieldMapper<AvailableProgramGroup> programGroupFieldMapper = new FieldMapper<AvailableProgramGroup>() {
        public void extractValues(MapSqlParameterSource p, AvailableProgramGroup programGroup) {
            p.addValue("CCurtProgramID", programGroup.getProgram().getId());
            p.addValue("CCurtGroupID", programGroup.getGroup().getId());
        }
        public Number getPrimaryKey(AvailableProgramGroup programGroup) {
            return programGroup.getId();
        }
        public void setPrimaryKey(AvailableProgramGroup programGroup, int value) {
            programGroup.setId(value);
        }
    };

    @Override
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<AvailableProgramGroup>(yukonJdbcTemplate, nextValueHelper);
        template.withTableName("CCurtProgramGroup");
        template.withPrimaryKeyField("CCurtProgramGroupID");
        template.withFieldMapper(programGroupFieldMapper); 
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
    
    @Autowired
    public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }
}
