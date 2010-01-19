package com.cannontech.core.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.constants.YukonListEntry;

public class YukonListEntryRowMapper implements ParameterizedRowMapper<YukonListEntry> {
 
	@Override
	public YukonListEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
		
        final YukonListEntry entry = new YukonListEntry();
        entry.setEntryID(rs.getInt("EntryID"));
        entry.setListID(rs.getInt("ListID"));
        entry.setEntryOrder(rs.getInt("EntryOrder"));
        entry.setEntryText(rs.getString("EntryText"));
        entry.setYukonDefID(rs.getInt("YukonDefinitionID"));
        return entry;
    }
}
