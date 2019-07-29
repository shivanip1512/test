package com.cannontech.loadcontrol.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;


public class LmProgramGearHistoryMapper implements RowMapper<LmProgramGearHistory>{

    public LmProgramGearHistory mapRow(ResultSet rs, int rowNum) throws SQLException {

        LmProgramGearHistory hist = new LmProgramGearHistory();
        hist.setProgramId(rs.getInt("ProgramId"));
        hist.setProgramName(rs.getString("ProgramName"));
        hist.setProgramGearHistoryId(rs.getInt("LMProgramGearHistoryId"));
        hist.setProgramHistoryId(rs.getInt("LMProgramHistoryId"));
        hist.setEventTime(rs.getTimestamp("EventTime"));
        hist.setAction(rs.getString("Action"));
        hist.setUserName(rs.getString("UserName"));
        hist.setGearName(rs.getString("GearName"));
        hist.setGearId(rs.getInt("GearId"));
        hist.setReason(rs.getString("Reason"));
        hist.setOriginSource(rs.getString("Origin"));

        return hist;
    }
}
