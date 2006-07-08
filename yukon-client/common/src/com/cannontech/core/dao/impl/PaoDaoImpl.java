package com.cannontech.core.dao.impl;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
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
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.yukon.IDatabaseCache;

public final class PaoDaoImpl implements PaoDao 
{
    private static final String paoSql = 
        "SELECT y.PAObjectID, y.Category, y.PAOName, " +
        "y.Type, y.PAOClass, y.Description, d.PORTID, dcs.ADDRESS " +
        "FROM " + YukonPAObject.TABLE_NAME+ " y left outer join " + DeviceDirectCommSettings.TABLE_NAME + " d " +
        "on y.paobjectid = d.deviceid " +
        "left outer join " + DeviceCarrierSettings.TABLE_NAME + " DCS ON Y.PAOBJECTID = DCS.DEVICEID ";
    
    private JdbcOperations jdbcOps;    
    private IDatabaseCache databaseCache;
    private NextValueHelper nextValueHelper;
    
/* (non-Javadoc)
 * @see com.cannontech.core.dao.PaoDao#getAllPointIDsAndTypesForPAObject(int)
 */
// the format returned is :   
//			int[X][0] == id
//			int[X][1] == lite type
public int[][] getAllPointIDsAndTypesForPAObject( int deviceid )
{
	synchronized(databaseCache)
	{
		java.util.List points = databaseCache.getAllPoints();
		java.util.Collections.sort(points);
		
		int[][] ids = new int[points.size()][2];

		int pointCount = 0;
		for(int i=0;i<points.size();i++)
		{
			if( deviceid == ((LitePoint)points.get(i)).getPaobjectID() )
			{
				ids[pointCount][0] = ((LitePoint)points.get(i)).getPointID();
				ids[pointCount][1] = ((LitePoint)points.get(i)).getPointType();
				pointCount++;
			}
			
		}		

		int[][] realPts = new int[pointCount][2];
		System.arraycopy( ids, 0, realPts, 0, realPts.length );
		return realPts;
	}

}
/* (non-Javadoc)
 * @see com.cannontech.core.dao.PaoDao#getLitePointsForPAObject(int)
 */
public LitePoint[] getLitePointsForPAObject( int paoID )
{
	synchronized(databaseCache)
	{
		java.util.List points = databaseCache.getAllPoints();

		//a mutable lite point used for comparisons
		final LitePoint dummyLitePoint = 
						new LitePoint(Integer.MIN_VALUE,
							"**DUMMY**", 0, paoID, 0, 0 );
		
		//a Vector only needed to store temporary things
		java.util.List destList = new java.util.Vector(10);

		//binarySearchRepetition will sort and search the list
		com.cannontech.common.util.CtiUtilities.binarySearchRepetition(
					points,
					dummyLitePoint,
					com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator,
					destList );

		LitePoint[] litePoints = new LitePoint[0];
		return (LitePoint[])destList.toArray( litePoints );
	}

}
/* (non-Javadoc)
 * @see com.cannontech.core.dao.PaoDao#getLiteYukonPAO(int)
 */
public LiteYukonPAObject getLiteYukonPAO( int paoID )
{
	synchronized( databaseCache )
	{
		LiteYukonPAObject liteYukonPAObject = (LiteYukonPAObject) 
			databaseCache.getAllPAOsMap().get( new Integer(paoID) );
		return liteYukonPAObject;
	}
}

public List<LiteYukonPAObject> getLiteYukonPAObjectByType(int paoType) {
    String typeStr = PAOGroups.getPAOTypeString(paoType);
    String sql = paoSql;
    sql += "where type=" + typeStr;
    
    return null;
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
public int getMaxPAOid()
{
    return jdbcOps.queryForInt("select max(paObjectId) from YukonPaObject");
}

public int getNextPaoId() {
    return nextValueHelper.getNextValue("YukonPaObject");
}

public int[] getNextPaoIds(int count) {
    //TODO: Modify nextValueHelper to get multiple ids, this is expensive
    int[] ids = new int[count];
    for (int i = 0; i < ids.length; i++) {
        ids[i]= nextValueHelper.getNextValue("YukonPaObject");
    }
    return null;
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.PaoDao#getYukonPAOName(int)
 */
public String getYukonPAOName( int paoID )
{
	LiteYukonPAObject pao = getLiteYukonPAO( paoID );
	if( pao != null )
		return pao.getPaoName();
	else
		return null;
}
/* (non-Javadoc)
 * @see com.cannontech.core.dao.PaoDao#getAllLiteRoutes()
 */
public LiteYukonPAObject[] getAllLiteRoutes()
{
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

public void setDatabaseCache(IDatabaseCache databaseCache) {
    this.databaseCache = databaseCache;
}

public void setJdbcOps(JdbcOperations jdbcOps) {
    this.jdbcOps = jdbcOps;
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
    String sql = paoSql;
    
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
    List<LiteYukonPAObject> paos = jdbcOps.query(sql, new Object[]{ name }, new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createLiteYukonPAObject(rs);
        };
    });
    
    System.out.println("getLiteYukonPaoByName elapsed time: " + sw.getElapsedTime() + " size: " + paos.size());
    return paos;
}

    private LiteYukonPAObject createLiteYukonPAObject(java.sql.ResultSet rset)
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
            if (DaoFactory.getAuthDao().userHasAccessPAO(user, element.getLiteID()))
            {
                subList.add(element);           
            }
        }
        return subList;
    }

}
