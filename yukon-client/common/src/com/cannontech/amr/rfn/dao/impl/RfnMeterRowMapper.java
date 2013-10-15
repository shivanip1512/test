package com.cannontech.amr.rfn.dao.impl;

import java.sql.SQLException;

import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class RfnMeterRowMapper implements YukonRowMapper<RfnMeter> {

    @Override
    public RfnMeter mapRow(YukonResultSet rs) throws SQLException {
        PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PAObjectID",  "Type");

        String meterNumber = rs.getString("meterNumber");
        String paoName = rs.getString("paoName");
        char disabledChar = rs.getString("disableFlag").charAt(0);
        boolean disabled = CtiUtilities.isTrue(disabledChar);
       
        RfnIdentifier rfnIdentifier = new RfnIdentifier(rs.getStringSafe("SerialNumber"), 
                                                        rs.getStringSafe("Manufacturer"), 
                                                        rs.getStringSafe("Model"));
        
        RfnMeter rfnMeter = new RfnMeter(paoIdentifier, rfnIdentifier, meterNumber, paoName, disabled);
        return rfnMeter;
    }

}