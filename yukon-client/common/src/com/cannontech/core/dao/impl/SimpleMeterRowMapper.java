package com.cannontech.core.dao.impl;

import java.sql.SQLException;

import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class SimpleMeterRowMapper implements YukonRowMapper<SimpleMeter> {

    @Override
    public SimpleMeter mapRow(YukonResultSet rs) throws SQLException {

        PaoIdentifier paoIdentifier = rs.getPaoIdentifier("paobjectId",  "type");
        String meterNumber = rs.getString("meterNumber");

        SimpleMeter simpleMeter = new SimpleMeter(paoIdentifier, meterNumber);       
        return simpleMeter;
    }
}