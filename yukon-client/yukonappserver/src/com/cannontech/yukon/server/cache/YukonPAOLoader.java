package com.cannontech.yukon.server.cache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.StopWatch;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.device.DeviceRoutes;
import com.cannontech.database.db.pao.YukonPAObject;

/**
 * Creation date: (3/15/00 3:57:58 PM)
 * 
 * @author:
 */
public class YukonPAOLoader implements Runnable 
{
	private Map<Integer, LiteYukonPAObject> allPAOsMap = null;

	private List<LiteYukonPAObject> allPAObjects = null;
	
	public YukonPAOLoader(java.util.ArrayList pAObjectArray, Map paoMap_) 
	{
		super();
		this.allPAObjects = pAObjectArray;
		this.allPAOsMap = paoMap_;
	}

	/**
     * run method comment.
     */
	public void run() 
	{
        StopWatch sw = new StopWatch();
        sw.start();
        
		String sqlString = 
				"SELECT y.PAObjectID, y.Category, y.PAOName, " +
				"y.Type, y.PAOClass, y.Description, y.DisableFlag, d.PORTID, dcs.ADDRESS, dr.routeid " +
				"FROM " + YukonPAObject.TABLE_NAME+ " y left outer join " + DeviceDirectCommSettings.TABLE_NAME + " d " +
				"on y.paobjectid = d.deviceid " +
				"left outer join " + DeviceCarrierSettings.TABLE_NAME + " DCS ON Y.PAOBJECTID = DCS.DEVICEID " +		
                "left outer join " + DeviceRoutes.TABLE_NAME + " dr on y.paobjectid = dr.deviceid " + 
				"ORDER BY y.Category, y.PAOClass, y.PAOName";

        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        jdbcOps.query(sqlString, new RowCallbackHandler() {
           public void processRow(ResultSet rs) throws SQLException {
               LiteYukonPAObject pao = createLiteYukonPaObject(rs);
               allPAObjects.add(pao);
               allPAOsMap.put(pao.getYukonID(), pao);
           }
        });              
	
	CTILogger.info(sw.getElapsedTime()*.001 + 
	      " Secs for YukonPAObjectLoader (" + allPAObjects.size() + " loaded)" );
	}
    
    private LiteYukonPAObject createLiteYukonPaObject(ResultSet rset) throws SQLException {
        int paoID = rset.getInt(1);
        String paoCategory = rset.getString(2).trim();
        String paoName = rset.getString(3).trim();
        String paoType = rset.getString(4).trim();
        String paoClass = rset.getString(5).trim();
        String paoDescription = rset.getString(6).trim();
        if (CtiUtilities.STRING_NONE.equals(paoDescription)) {
            paoDescription = paoDescription.intern();
        }
        String paoDisableFlag = rset.getString(7).trim().intern();
               
        LiteYukonPAObject pao = new LiteYukonPAObject(
                    paoID, 
                    paoName, 
                    PAOGroups.getCategory(paoCategory),
                    PAOGroups.getPAOType(paoCategory, paoType),
                    PAOGroups.getPAOClass(paoCategory, paoClass),
                    paoDescription,
                    paoDisableFlag);

        int portId = rset.getInt(8);
        if(!rset.wasNull()) {
            pao.setPortID(portId);
        }
        
        int address = rset.getInt(9);
        if(!rset.wasNull()) {
            pao.setAddress(address);
        }

        int routeId = rset.getInt(10);
        if(!rset.wasNull()) {
            pao.setRouteID(routeId);
        }
        return pao;
    }
}