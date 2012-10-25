package com.cannontech.core.dao.impl;

import java.sql.SQLException;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class YukonMeterRowMapper implements YukonRowMapper<YukonMeter> {

    @Override
    public YukonMeter mapRow(YukonResultSet rs) throws SQLException {
        int paoID = rs.getInt("PAObjectID");
        String paoType = rs.getString("Type").trim();

        PaoType type = PaoType.getForDbString(paoType);
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoID, type);

        String meterNumber = rs.getString("meterNumber");
        String paoName = rs.getString("paoName");
        char disabledChar = rs.getString("disableFlag").charAt(0);
        boolean disabled = CtiUtilities.isTrue(disabledChar);
       
        YukonMeter yukonMeter = new YukonMeter(paoIdentifier, meterNumber, paoName, disabled);       
        return yukonMeter;
    }
}