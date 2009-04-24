package com.cannontech.core.dao.impl;

/**
 * SQL Result RowMapper for YukonPao object
 */
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.pao.PAOGroups;

public class YukonPaoRowMapper implements ParameterizedRowMapper<YukonPao> {

    public YukonPao mapRow(ResultSet rs, int rowNum) throws SQLException {
        int paoID = rs.getInt("PAObjectID");
        String paoCategory = rs.getString("Category").trim();
        String paoType = rs.getString("Type").trim();

        YukonPao pao = new YukonPao(paoID, PAOGroups.getPAOType(paoCategory,
                                                                paoType));

        return pao;
    }
}