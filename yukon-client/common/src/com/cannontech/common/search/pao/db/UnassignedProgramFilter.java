package com.cannontech.common.search.pao.db;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;


public class UnassignedProgramFilter implements SqlFilter {

    private Integer controlAreaId = null;
    
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlStatementBuilder programFilter = new SqlStatementBuilder();
        programFilter.append("PAObjectId IN (");
        programFilter.append("  SELECT DeviceId");
        programFilter.append("  FROM LMProgram lm");
        programFilter.append("  WHERE lm.deviceId NOT IN (SELECT cap.lmProgramDeviceId");
        programFilter.append("    FROM LMControlAreaProgram cap");
        if (controlAreaId != null) {
            programFilter.append("    WHERE cap.DeviceId").neq(controlAreaId);
        }
        programFilter.append("  )");
        programFilter.append(")");
        
        return programFilter;
    }
    
    public void setControlAreaId(int controlAreaId) {
        this.controlAreaId = controlAreaId;
    }
}
