package com.cannontech.loadcontrol.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;


public class LMProgramGearHistoryMapper implements ParameterizedRowMapper<LMProgramGearHistory>{

    public LMProgramGearHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
    	
    	LMProgramGearHistory hist = new LMProgramGearHistory();
    	hist.setProgramGearhistoryId(rs.getInt("LMProgramGearHistoryId"));
    	hist.setProgramHistoryId(rs.getInt("LMProgramHistoryId"));
    	hist.setProgramName(rs.getString("ProgramName"));
    	hist.setEventTime(rs.getTimestamp("EventTime"));
    	hist.setAction(rs.getString("Action"));
    	hist.setUserName(rs.getString("UserName"));
    	hist.setGearName(rs.getString("GearName"));
    	hist.setGearId(rs.getInt("GearId"));
    	hist.setReason(rs.getString("Reason"));
    	
        return hist;
    }
}
