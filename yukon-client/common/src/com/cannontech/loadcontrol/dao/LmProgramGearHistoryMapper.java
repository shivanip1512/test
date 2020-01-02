package com.cannontech.loadcontrol.dao;

import java.sql.SQLException;

import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.loadcontrol.dao.LmProgramGearHistory.GearAction;


public class LmProgramGearHistoryMapper implements YukonRowMapper<LmProgramGearHistory>{

    @Override
    public LmProgramGearHistory mapRow(YukonResultSet rs) throws SQLException {
        LmProgramGearHistory hist = new LmProgramGearHistory();
        hist.setProgramId(rs.getInt("ProgramId"));
        hist.setProgramName(rs.getString("ProgramName"));
        hist.setProgramGearHistoryId(rs.getInt("LMProgramGearHistoryId"));
        hist.setProgramHistoryId(rs.getInt("LMProgramHistoryId"));
        hist.setEventTime(rs.getDate("EventTime"));
        hist.setAction(rs.getEnum("Action", GearAction.class));
        hist.setUserName(rs.getString("UserName"));
        hist.setGearName(rs.getString("GearName"));
        hist.setGearId(rs.getInt("GearId"));
        hist.setReason(rs.getString("Reason"));
        hist.setOriginSource(rs.getString("Origin"));

        return hist;
    }
}
