package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.pao.PAOGroups;

public class YukonPaoRowMapper implements ParameterizedRowMapper<PaoIdentifier> {

    public PaoIdentifier mapRow(ResultSet rs, int rowNum) throws SQLException {
        int paoID = rs.getInt("PAObjectID");
        String paoCategory = rs.getString("Category").trim();
        String paoType = rs.getString("Type").trim();

        int type = PAOGroups.getPAOType(paoCategory, paoType);
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoID, PaoType.getForId(type), PaoCategory.valueOf(paoCategory));

        return paoIdentifier;
    }
}