package com.cannontech.yukon.server.cache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.StopWatch;
import com.cannontech.core.dao.impl.LitePaoRowMapper;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.lite.LiteYukonPAObject;
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
	private LitePaoRowMapper rowMapper = new LitePaoRowMapper();
	
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
	        private int row = 0;
	        public void processRow(ResultSet rs) throws SQLException {
	            LiteYukonPAObject pao = rowMapper.mapRow(rs, row++);
	            allPAObjects.add(pao);
	            allPAOsMap.put(pao.getYukonID(), pao);
	        }
	    });              

	    CTILogger.info(sw.getElapsedTime()*.001 + 
	                   " Secs for YukonPAObjectLoader (" + allPAObjects.size() + " loaded)" );
	}
    
}