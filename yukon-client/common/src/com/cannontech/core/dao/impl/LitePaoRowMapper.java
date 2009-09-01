/**
 * 
 */
package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;

public class LitePaoRowMapper implements ParameterizedRowMapper<LiteYukonPAObject> {
    
    public LiteYukonPAObject mapRow(ResultSet rs, int rowNum) throws SQLException {
        int paoID = rs.getInt("PAObjectID");
        String paoCategory = rs.getString("Category").trim();
        String paoName = rs.getString("PAOName").trim();
        String paoType = rs.getString("Type").trim();
        String paoClass = rs.getString("PAOclass").trim();
        String paoDescription = rs.getString("Description").trim();
        if (CtiUtilities.STRING_NONE.equals(paoDescription)) {
            paoDescription = paoDescription.intern();
        }
        String paoDisableFlag = rs.getString("DisableFlag").trim().intern();

        LiteYukonPAObject pao = new LiteYukonPAObject(paoID,
                                                      paoName,
                                                      PAOGroups.getCategory(paoCategory),
                                                      PAOGroups.getPAOType(paoCategory,
                                                                           paoType),
                                                      PAOGroups.getPAOClass(paoCategory,
                                                                            paoClass),
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