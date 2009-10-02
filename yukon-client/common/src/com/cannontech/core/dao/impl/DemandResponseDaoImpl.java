package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DemandResponseDao;
import com.cannontech.database.data.pao.PAOGroups;

public class DemandResponseDaoImpl implements DemandResponseDao {

    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    @Override
    public List<YukonPao> getControlAreasAndScenariosForProgram(YukonPao program) {
        
        int programId = program.getPaoIdentifier().getPaoId();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT y.PAObjectID, y.Category, y.Type"); 
        sql.append("FROM yukonpaobject y");
        sql.append("WHERE paObjectId IN (");
        sql.append("    SELECT deviceId FROM lmControlAreaProgram WHERE lmProgramDeviceId =");
        sql.appendArgument(programId);
        sql.append("    )");
        sql.append("    OR paObjectId IN (");
        sql.append("    SELECT scenarioId FROM lmControlScenarioProgram WHERE programId=");
        sql.appendArgument(programId);
        sql.append("    )");
        
        List<YukonPao> parentList = 
            simpleJdbcTemplate.query(sql.getSql(), new YukonPaoRowMapper(), sql.getArguments());
        
        return parentList;
    }

    @Override
    public List<YukonPao> getProgramsForGroup(YukonPao group) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT y.PAObjectID, y.Category, y.Type"); 
        sql.append("FROM yukonpaobject y");
        sql.append("WHERE paObjectId IN (");
        sql.append("    SELECT deviceId FROM lmProgramDirectGroup WHERE lmGroupDeviceId =");
        sql.appendArgument(group.getPaoIdentifier().getPaoId());
        sql.append("    )");
        
        List<YukonPao> programList = 
            simpleJdbcTemplate.query(sql.getSql(), new YukonPaoRowMapper(), sql.getArguments());
        
        return programList;
    }
    
    private class YukonPaoRowMapper implements ParameterizedRowMapper<YukonPao> {

        public YukonPao mapRow(ResultSet rs, int rowNum) throws SQLException {
            int paoID = rs.getInt("PAObjectID");
            String paoCategory = rs.getString("Category").trim();
            String paoType = rs.getString("Type").trim();

            int type = PAOGroups.getPAOType(paoCategory, paoType);
            PaoIdentifier paoIdentifier = new PaoIdentifier(paoID,
                                                            PaoType.getForId(type));

            return paoIdentifier;
        }
    }
    
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

}
