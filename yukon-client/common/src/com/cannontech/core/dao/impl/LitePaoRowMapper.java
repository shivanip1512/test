/**
 * 
 */
package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;

public class LitePaoRowMapper implements ParameterizedRowMapper<LiteYukonPAObject> {
    
    public LiteYukonPAObject mapRow(ResultSet rs, int rowNum) throws SQLException {
        int paoID = rs.getInt("PAObjectID");
        String paoCategoryStr = rs.getString("Category").trim();
        String paoName = rs.getString("PAOName").trim();
        String paoTypeStr = rs.getString("Type").trim();
        String paoDescription = rs.getString("Description").trim();
        if (CtiUtilities.STRING_NONE.equals(paoDescription)) {
            paoDescription = paoDescription.intern();
        }
        String paoDisableFlag = rs.getString("DisableFlag").trim().intern();

        PaoType paoType = PaoType.getForId(PAOGroups.getPAOType(paoCategoryStr,
                                                                paoTypeStr));
        LiteYukonPAObject pao = new LiteYukonPAObject(new PaoIdentifier(paoID,
                                                                        paoType),
                                                      paoName,
                                                      paoDescription,
                                                      paoDisableFlag);
        
        int portId = rs.getInt("PORTID");
        if (!rs.wasNull()) {
            pao.setPortID(portId);
        }
        
        int address = rs.getInt("ADDRESS");
        if (!rs.wasNull()) {
            pao.setAddress(address);
        }
        
        int routeId = rs.getInt("routeid");
        if (!rs.wasNull()) {
            pao.setRouteID(routeId);
        }
        
        return pao;
    }
}