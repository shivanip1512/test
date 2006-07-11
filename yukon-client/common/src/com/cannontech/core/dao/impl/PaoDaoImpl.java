package com.cannontech.core.dao.impl;

/**
 * Implementation of PaoDao
 * Creation date: (7/1/2006 9:40:33 AM)
 * @author: alauinger
 */
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.util.StopWatch;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.yukon.IDatabaseCache;

public final class PaoDaoImpl implements PaoDao  
{
    private static final String litePaoSql = 
        "SELECT y.PAObjectID, y.Category, y.PAOName, " +
        "y.Type, y.PAOClass, y.Description, d.PORTID, dcs.ADDRESS " +
        "FROM yukonpaobject y left outer join devicedirectcommsettings d " +
        "on y.paobjectid = d.deviceid " +
        "left outer join devicecarriersettings DCS ON Y.PAOBJECTID = DCS.DEVICEID ";
    
    private static final RowMapper litePaoRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createLiteYukonPAObject(rs);
        };
    };
    
    private JdbcOperations jdbcOps;    
    private IDatabaseCache databaseCache;
    private NextValueHelper nextValueHelper;
    private AuthDao authDao;
    
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.PaoDao#getLiteYukonPAO(int)
     */
    public LiteYukonPAObject getLiteYukonPAO( int paoID ) {
        String sql = litePaoSql + "where y.paobjectid=?";
        LiteYukonPAObject pao = (LiteYukonPAObject) 
                jdbcOps.queryForObject(sql, new Object[] { paoID }, litePaoRowMapper);
        return pao;
    }
    
    public List<LiteYukonPAObject> getLiteYukonPAObjectByType(int paoType) {
        String typeStr = PAOGroups.getPAOTypeString(paoType);
        String sql = litePaoSql;
        sql += "where type=? ";
        
        List<LiteYukonPAObject> paos = jdbcOps.query(sql, new Object[] { typeStr }, litePaoRowMapper );
        return paos;
    }
    
    public List<LiteYukonPAObject> getLiteYukonPAObjectBy(
            Integer[] paoType, Integer[] paoCategory, 
            Integer[] paoClass, Integer[] pointTypes, Integer[] uOfMId) {
    
        StringBuilder sql = new StringBuilder(litePaoSql);
        sql.append(
            "left outer join point p on y.paobjectid=p.paobjectid " + 
            "left outer join pointunit pu on p.pointid=pu.pointid ");
    
        String[] pointTypesStr = PointTypes.convertPointTypes(pointTypes);
        SqlUtil.buildInClause("where", "y", "type", paoType, sql);
        SqlUtil.buildInClause("and", "y", "category", paoCategory, sql);
        SqlUtil.buildInClause("and", "y", "paoclass", paoClass, sql);
        SqlUtil.buildInClause("and", "p", "pointtype", pointTypesStr, sql);
        SqlUtil.buildInClause("and", "pu", "uomid", uOfMId, sql);
         
        List<LiteYukonPAObject> paos = jdbcOps.query(sql.toString(), litePaoRowMapper);
        return paos;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.PaoDao#getAllCapControlSubBuses()
     */
    public List getAllCapControlSubBuses() {
    	List subBusList = null;
    	synchronized (databaseCache)
    	{
    		subBusList = databaseCache.getAllCapControlSubBuses();
    	}
    
    	return subBusList;
    }
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.PaoDao#getMaxPAOid()
     */
    public int getMaxPAOid() {
        return jdbcOps.queryForInt("select max(paObjectId) from YukonPaObject");
    }
    
    public int getNextPaoId() {
        return nextValueHelper.getNextValue("YukonPaObject");
    }
    
    public int[] getNextPaoIds(int count) {
        int[] ids = new int[count];
        for (int i = 0; i < ids.length; i++) {
            ids[i]= nextValueHelper.getNextValue("YukonPaObject");
        }
        return ids;
    }

    public String getYukonPAOName(int paoID) {
        String sql = "select paoname from yukonpaobject where paobjectid=?";
        String name = (String) 
            jdbcOps.queryForObject(sql, new Object[] { paoID }, String.class);
        return name;
    }

    public LiteYukonPAObject[] getAllLiteRoutes() {
    	//Get an instance of the databaseCache.
    	java.util.ArrayList routeList = new java.util.ArrayList(10);
    	synchronized(databaseCache)
    	{
    		java.util.List routes = databaseCache.getAllRoutes();
    		java.util.Collections.sort( routes, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
    		
    		for (int i = 0; i < routes.size(); i++)
    		{
    			LiteYukonPAObject litePao = (LiteYukonPAObject)routes.get(i);
    			routeList.add(litePao);	
    		}
    	}
    	LiteYukonPAObject retVal[] = new LiteYukonPAObject[routeList.size()];
    	routeList.toArray( retVal );
    	return retVal;
    }

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.PaoDao#getRoutesByType(int[])
     */
    public LiteYukonPAObject[] getRoutesByType(int[] routeTypes) 
    {   
    	java.util.ArrayList routeList = new java.util.ArrayList(10);
    	synchronized(databaseCache)
    	{
    		java.util.List routes = databaseCache.getAllRoutes();
    		java.util.Collections.sort( routes, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
          
    		for( int i = 0; i < routes.size(); i++ )
    		{      
    			LiteYukonPAObject litePao = (LiteYukonPAObject)routes.get(i);
    			
    			for( int j = 0; j < routeTypes.length; j++ )
    				if( litePao.getType() != routeTypes[j] )
    				{
    					routeList.add( litePao);
    					break;
    				}
    		}
    	}
    
    	LiteYukonPAObject retVal[] = new LiteYukonPAObject[ routeList.size() ];
    	routeList.toArray( retVal );
       
    	return retVal;
    }

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.PaoDao#getAllUnusedCCPAOs(java.lang.Integer)
     */
    public LiteYukonPAObject[] getAllUnusedCCPAOs( Integer ignoreID ) {
    		
    	synchronized( databaseCache ) {
    
    		List lPaos = databaseCache.getAllUnusedCCDevices();
    		
    		LiteYukonPAObject retVal[] = (LiteYukonPAObject[])
    			lPaos.toArray( new LiteYukonPAObject[ lPaos.size() + 1 ] );
    
    		retVal[lPaos.size()] = getLiteYukonPAO( ignoreID.intValue() );   
    		return retVal;
    	}
    }

    public int countLiteYukonPaoByName(String name, boolean partialMatch) {
        
        String sql;
        if(partialMatch) {
            sql = "select count(*) from YukonPAObject where paoname like '" + name + "%'";    
        }
        else {
            sql = "select count(*) from YukonPAObject where paoname='" + name + "'";    
        }
        
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        return (Integer) jdbcOps.queryForObject(sql,Integer.class);
    }

    public List<LiteYukonPAObject> getLiteYukonPaoByName(String name, boolean partialMatch) {
            
        StopWatch sw = new StopWatch();
        sw.start();
        String sql = litePaoSql;
        
        if(partialMatch) {
          sql += "where y.PAOName like ? ";
        }
        else {
            sql += "where y.PAOName=? ";
            
        }
        
        //sql += "where y.PAOName=? ";
        sql += "ORDER BY y.Category, y.PAOClass, y.PAOName";
        
        
        if(partialMatch) {
            name += "%";
        }
        
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        List<LiteYukonPAObject> paos = jdbcOps.query(sql, new Object[]{ name }, litePaoRowMapper);
        return paos;
    }

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
    
    public void setJdbcOps(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
    }
    
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    
    private static LiteYukonPAObject createLiteYukonPAObject(java.sql.ResultSet rset)
            throws SQLException {
        int paoID = rset.getInt(1);
        String paoCategory = rset.getString(2).trim();
        String paoName = rset.getString(3).trim();
        String paoType = rset.getString(4).trim();
        String paoClass = rset.getString(5).trim();
        String paoDescription = rset.getString(6).trim();

        // this column may be null!!
        BigDecimal portID = (BigDecimal) rset.getObject(7);
        // this column may be null!!
        BigDecimal address = (BigDecimal) rset.getObject(8);

        LiteYukonPAObject pao = new LiteYukonPAObject(paoID, paoName, PAOGroups
                .getCategory(paoCategory), PAOGroups.getPAOType(paoCategory,
                paoType), PAOGroups.getPAOClass(paoCategory, paoClass),
                paoDescription);

        if (portID != null)
            pao.setPortID(portID.intValue());

        if (address != null)
            pao.setAddress(address.intValue());

        return pao;

    }
    
    public  List getAllSubsForUser (LiteYukonUser user) {
        List subList = new ArrayList(10);
        List temp = getAllCapControlSubBuses();
        for (Iterator iter = temp.iterator(); iter.hasNext();) {
            LiteYukonPAObject element = (LiteYukonPAObject) iter.next();
            if (authDao.userHasAccessPAO(user, element.getLiteID()))
            {
                subList.add(element);           
            }
        }
        return subList;
    }
    
}
