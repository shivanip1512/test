package com.cannontech.yukon.server.cache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStringStatementBuilder;
import com.cannontech.common.util.StopWatch;
import com.cannontech.core.dao.impl.LitePaoRowMapper;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.lite.LiteYukonPAObject;

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
	
	public YukonPAOLoader(List<LiteYukonPAObject> pAObjectArray, Map<Integer, LiteYukonPAObject> paoMap_) 
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

	    
	    SqlStringStatementBuilder sql = new SqlStringStatementBuilder();
	    sql.append("SELECT PAO.PAObjectId, PAO.Category, PAO.PAOName, PAO.Type, PAO.PAOClass, PAO.Description,");
	    sql.append("       PAO.DisableFlag, DDCS.PortId, DCS.Address, DR.RouteId ");
	    sql.append("FROM YukonPAObject PAO");
	    sql.append("  LEFT OUTER JOIN DeviceDirectCommSettings DDCS ON PAO.PAObjectId = DDCS.DeviceId");
	    sql.append("  LEFT OUTER JOIN DeviceCarrierSettings DCS ON PAO.PAObjectId = DCS.DeviceId");
	    sql.append("  LEFT OUTER JOIN DeviceRoutes DR ON PAO.PAObjectId = DR.DeviceId");
	    sql.append("ORDER BY PAO.Category, PAO.PAOClass, PAO.PAOName");
	    
	    JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
	    jdbcOps.query(sql.toString(), new RowCallbackHandler() {
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