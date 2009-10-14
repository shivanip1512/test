package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DemandResponseDao;
import com.cannontech.database.YukonJdbcTemplate;

public class DemandResponseDaoImpl implements DemandResponseDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public List<YukonPao> getControlAreasAndScenariosForProgram(YukonPao program) {
        
        int programId = program.getPaoIdentifier().getPaoId();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT y.PAObjectID, y.Type"); 
        sql.append("FROM yukonpaobject y");
        sql.append("WHERE paObjectId IN (");
        sql.append("    SELECT deviceId FROM lmControlAreaProgram WHERE lmProgramDeviceId =");
        sql.appendArgument(programId);
        sql.append("    )");
        sql.append("    OR paObjectId IN (");
        sql.append("    SELECT scenarioId FROM lmControlScenarioProgram WHERE programId=");
        sql.appendArgument(programId);
        sql.append("    )");
        
        List<YukonPao> parentList = new ArrayList<YukonPao>();
        yukonJdbcTemplate.query(sql, new YukonPaoRowMapper(), parentList);
        
        return parentList;
    }

    @Override
    public List<YukonPao> getProgramsForGroup(YukonPao group) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT y.PAObjectID, y.Type"); 
        sql.append("FROM yukonpaobject y");
        sql.append("WHERE paObjectId IN (");
        sql.append("    SELECT deviceId FROM lmProgramDirectGroup WHERE lmGroupDeviceId =");
        sql.appendArgument(group.getPaoIdentifier().getPaoId());
        sql.append("    )");
        
        List<YukonPao> programList = new ArrayList<YukonPao>();
        yukonJdbcTemplate.query(sql, new YukonPaoRowMapper(), programList);
        
        return programList;
    }
    
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}
