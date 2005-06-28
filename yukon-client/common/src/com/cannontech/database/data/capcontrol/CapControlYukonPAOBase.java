package com.cannontech.database.data.capcontrol;

import java.util.HashMap;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;

/**
 * This type was created in VisualAge.
 */
public abstract class CapControlYukonPAOBase extends com.cannontech.database.data.pao.YukonPAObject
{
	public static final String DEFAULT_MAPLOCATION_ID = "0";
/**
 * TwoWayDevice constructor comment.
 */
public CapControlYukonPAOBase() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 3:15:43 PM)
 */
public static String[] getAllUsedCapControlMapIDs()
{
	return getAllUsedCapControlMapIDs( CapControlYukonPAOBase.DEFAULT_MAPLOCATION_ID );
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 3:15:43 PM)
 */
public static String[] getAllUsedCapControlMapIDs( String currentMapLocID )
{
	java.sql.Connection conn = null;
	java.sql.Statement pstmt = null;
	java.sql.ResultSet rset = null;
	HashMap mapIDs = new HashMap(64);
		
	try
	{		
		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection.");
		}
		else
		{
			String[] tableNames = 
			{
				com.cannontech.database.db.capcontrol.CapBank.TABLE_NAME,
				com.cannontech.database.db.capcontrol.CapControlFeeder.TABLE_NAME,
				com.cannontech.database.db.capcontrol.CapControlSubstationBus.TABLE_NAME
			};

			pstmt = conn.createStatement();

			for( int i = 0; i < tableNames.length; i++ )
			{
				rset = pstmt.executeQuery("select distinct maplocationid " +
						"from " + tableNames[i] +
						" order by maplocationid");

				String value = null;
				while( rset.next() )
				{
					value = rset.getString(1);

					if( !mapIDs.containsKey(value) && !value.equalsIgnoreCase(currentMapLocID) 
						 && !value.equalsIgnoreCase(CapControlYukonPAOBase.DEFAULT_MAPLOCATION_ID) )
						mapIDs.put( value, value );
				}

			}
			
		}		
	}
	catch( java.sql.SQLException e )
	{
		CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( pstmt != null ) pstmt.close();
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}

	String[] vals = new String[ mapIDs.size() ];
	vals = (String[])mapIDs.keySet().toArray( vals );
	java.util.Arrays.sort(vals);
	
	return vals;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 3:15:43 PM)
 * @param name Integer
 */
public Integer getCapControlPAOID()
{
	return getPAObjectID();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 4:27:13 PM)
 */
public Character getDisableFlag()
{
	return getYukonPAObject().getDisableFlag();
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 3:15:43 PM)
 * @param name java.lang.String
 */
public String getGeoAreaName()
{
	return getYukonPAObject().getDescription();
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 3:15:43 PM)
 */
public abstract void setCapControlPAOID(Integer paoID);
//{
	//setPAObjectID( paoID );
//}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 3:15:43 PM)
 * @param name java.lang.String
 */
public void setCapControlType(String type)
{
	getYukonPAObject().setType( type );
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 4:27:13 PM)
 */
public void setDisableFlag( Character flag )
{
	getYukonPAObject().setDisableFlag( flag );
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 3:15:43 PM)
 * @param name java.lang.String
 */
public void setGeoAreaName(String geoName)
{
	getYukonPAObject().setDescription( geoName );
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 3:15:43 PM)
 * @param name java.lang.String
 */
public void setName(String name) 
{
	getYukonPAObject().setPaoName( name );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() 
{
	if( getPAOName() != null )
		return getPAOName();
	else
		return null;
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException{
	super.update();
}
}
