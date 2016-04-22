package com.cannontech.loadcontrol.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ProgramIdMapper implements RowMapper<Integer>{

    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("ProgramID");
    }
}
