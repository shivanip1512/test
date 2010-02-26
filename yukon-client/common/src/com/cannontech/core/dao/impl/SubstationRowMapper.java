/**
 * 
 */
package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.model.Substation;

public final class SubstationRowMapper implements
		ParameterizedRowMapper<Substation> {
	public Substation mapRow(ResultSet rs, int rowNum) throws SQLException {
	    Substation substation = new Substation(rs.getInt("SubstationID"),
	                              rs.getString("SubstationName"),
	                              rs.getInt("LMRouteID"));
		return substation;
	}
}