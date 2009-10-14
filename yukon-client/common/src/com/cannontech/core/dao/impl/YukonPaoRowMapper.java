package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;

public class YukonPaoRowMapper implements ParameterizedRowMapper<PaoIdentifier> {

    public PaoIdentifier mapRow(ResultSet rs, int rowNum) throws SQLException {
        int paoID = rs.getInt("PAObjectID");
        String paoType = rs.getString("Type").trim();

        PaoType type = PaoType.getForDbString(paoType);
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoID, type);

        return paoIdentifier;
    }
}