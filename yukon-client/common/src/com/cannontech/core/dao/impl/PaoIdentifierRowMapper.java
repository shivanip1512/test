package com.cannontech.core.dao.impl;

import java.sql.SQLException;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class PaoIdentifierRowMapper implements YukonRowMapper<PaoIdentifier> {

    @Override
    public PaoIdentifier mapRow(YukonResultSet rs) throws SQLException {
        Integer paoId = rs.getInt("paObjectId");
        PaoType paoType = PaoType.getForDbString(rs.getString("type"));
        return new PaoIdentifier(paoId, paoType);
    }
    
}
