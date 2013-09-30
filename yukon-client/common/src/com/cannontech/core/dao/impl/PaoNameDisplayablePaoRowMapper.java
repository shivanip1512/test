package com.cannontech.core.dao.impl;

import java.sql.SQLException;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class PaoNameDisplayablePaoRowMapper implements
        YukonRowMapper<DisplayablePao> {
    @Override
    public DisplayablePao mapRow(YukonResultSet rs) throws SQLException {
        return new DisplayablePaoBase(rs.getPaoIdentifier("paobjectId", "type"), rs.getString("paoName"));
    }
}
