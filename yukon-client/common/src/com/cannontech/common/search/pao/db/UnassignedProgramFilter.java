package com.cannontech.common.search.pao.db;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;


public class UnassignedProgramFilter implements SqlFilter {

    private Integer controlAreaId = null;
    
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlStatementBuilder bySubBus = new SqlStatementBuilder();
        bySubBus.append("PAObjectId IN (");
        bySubBus.append("  SELECT DeviceId");
        bySubBus.append("  FROM LMProgram lm");
        bySubBus.append("  WHERE lm.deviceId NOT IN (SELECT cap.lmProgramDeviceId");
        bySubBus.append("    FROM LMControlAreaProgram cap");
        if (controlAreaId != null) {
            bySubBus.append("    WHERE cap.DeviceId").neq(controlAreaId);
        }
        bySubBus.append("  )");
        bySubBus.append(")");
        
        return bySubBus;
    }
    
    public void setControlAreaId(int controlAreaId) {
        this.controlAreaId = controlAreaId;
    }
}
