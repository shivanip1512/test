package com.cannontech.core.dao.impl;

import java.sql.SQLException;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class PaoNameDisplayablePaoRowMapper implements
        YukonRowMapper<DisplayablePao> {

    public DisplayablePao mapRow(YukonResultSet rs) throws SQLException {
        String paoName = rs.getString("paoName");
        int paoID = rs.getInt("paobjectId");
        String paoTypeStr = rs.getString("type");
        PaoType paoType = PaoType.getForDbString(paoTypeStr);
        
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoID, paoType);
        return new DisplayablePaoBase(paoIdentifier, paoName);
    }
}
