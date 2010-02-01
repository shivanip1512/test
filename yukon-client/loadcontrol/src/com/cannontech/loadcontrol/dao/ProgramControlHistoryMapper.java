package com.cannontech.loadcontrol.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.loadcontrol.service.data.ProgramControlHistory;

public class ProgramControlHistoryMapper implements ParameterizedRowMapper<ProgramControlHistory>{

    public ProgramControlHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
    	
    	ProgramControlHistory pch = new ProgramControlHistory(rs.getInt("ProgramId"));
    	pch.setProgramName(rs.getString("ProgramName"));
    	pch.setGearName(rs.getString("GearName"));
    	pch.setStartDateTime(rs.getTimestamp("startTime"));
    	pch.setStopDateTime(rs.getTimestamp("stopTime"));
    	
        return pch;
    }
}