package com.cannontech.web.capcontrol.filter;

import java.sql.SQLException;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.db.pao.PaoScheduleAssignment;

public class ScheduleAssignmentRowMapper extends
        AbstractRowMapperWithBaseQuery<PaoScheduleAssignment> {

    @Override
    public SqlFragmentSource getBaseQuery() {
        SqlStatementBuilder builder = new SqlStatementBuilder();
        builder.append("SELECT sa.EventID, sa.ScheduleID, s.ScheduleName, s.NextRunTime,");
        builder.append(  "s.LastRunTime, sa.PaoID, po.PAOName, sa.Command, sa.disableOvUv");
        builder.append("FROM PAOScheduleAssignment sa");
        builder.append(  "JOIN PAOSchedule s on s.ScheduleID = sa.ScheduleID");
        builder.append(  "JOIN YukonPAObject po on sa.PaoID = po.PAObjectID");
        return builder;
    }

    @Override
    public PaoScheduleAssignment mapRow(YukonResultSet rs)
            throws SQLException {
        final PaoScheduleAssignment assignment = new PaoScheduleAssignment();
        String commandName = rs.getString("Command");
        assignment.setCommandName(commandName);
        String deviceName = rs.getString("PAOName");
        assignment.setDeviceName(deviceName);
        String disableOvUv = rs.getString("disableOvUv");
        assignment.setDisableOvUv(disableOvUv);
        int eventId = rs.getInt("EventID");
        assignment.setEventId(eventId);
        assignment.setLastRunTime(rs.getDate("LastRunTime"));
        assignment.setNextRunTime(rs.getDate("NextRunTime"));
        int paoId = rs.getInt("PaoID");
        assignment.setPaoId(paoId);
        int scheduleId = rs.getInt("ScheduleID");
        assignment.setScheduleId(scheduleId);
        String scheduleName = rs.getString("ScheduleName");
        assignment.setScheduleName(scheduleName);
        
        return assignment;
    }
    
    @Override
    public boolean needsWhere(){
        return true;
    }
}
