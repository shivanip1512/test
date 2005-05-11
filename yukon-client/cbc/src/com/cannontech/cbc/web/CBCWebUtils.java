package com.cannontech.cbc.web;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.util.ServletUtil;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;

/**
 * Generates a URL for a graph with the given cache and item ID
 * 
 * @author ryan
 */
public class CBCWebUtils
{
	private static final String GRAPH_30_DAY_URL
		= "/servlet/GraphGenerator?action=EncodeGraph";

	public static final String TYPE_PF = "pf";
	public static final String TYPE_VARWATTS = "vw";

	/**
	 * Creates a URL that will generate a graph  for the give FEEDER
	 *  or SUBUBUS id
	 * 
	 */
	public static synchronized String genGraphURL( int theId, CapControlCache theCache, String period, String type )
	{
		if( theCache == null )
			return null;
		
		String retURL = GRAPH_30_DAY_URL;
		if( period == null )
			retURL += "&period=" + ServletUtil.PREVTHIRTYDAYS;
		else
			retURL += "&period=" + period;
		
		String res = null;
		if( theCache.isSubBus(theId) )
		{
			res = _createSubBusGraphURL( theCache.getSubBus(new Integer(theId)), type );
			retURL = (res == null ? null : retURL + res);
		}
		else if( theCache.isFeeder(theId) )
		{
			res = _createFeederGraphURL( theCache.getFeeder(new Integer(theId)), type );
			retURL = (res == null ? null : retURL + res);
		}


		return retURL;
	}

	public static synchronized String genGraphURL( int theId, CapControlCache theCache, String type )
	{
		return genGraphURL( theId, theCache, ServletUtil.PREVTHIRTYDAYS, type );
	}

	/**
	 * Creates a URL for the given SubBus's points
	 * 
	 */
	private static synchronized String _createSubBusGraphURL( SubBus subBus, String type )
	{
		String temp = "";
		
		if( TYPE_PF.equals(type) )
		{
			temp += _getPointStr( subBus.getPowerFactorPointId() );		
			temp += _getPointStr( subBus.getEstimatedPowerFactorPointId() );
		}
		else
		{
			temp += _getPointStr( subBus.getCurrentVarLoadPointID() );		
			temp += _getPointStr( subBus.getCurrentWattLoadPointID() );
			temp += _getPointStr( subBus.getEstimatedVarLoadPointID() );
		}

		if( temp.length() > 0 )
			return "&pointid=" + temp.substring(1);
		else
			return null;
	}

	/**
	 * Creates a URL for the given feeders points
	 * 
	 */
	private static synchronized String _createFeederGraphURL( Feeder feeder, String type )
	{
		String temp = "";
		if( TYPE_PF.equals(type) )
		{
			temp += _getPointStr( feeder.getPowerFactorPointID() );		
			temp += _getPointStr( feeder.getEstimatedPowerFactorPointID() );
		}
		else
		{		
			temp += _getPointStr( feeder.getCurrentVarLoadPointID() );		
			temp += _getPointStr( feeder.getCurrentWattLoadPointID() );
			temp += _getPointStr( feeder.getEstimatedVarLoadPointID() );
		}
		
		if( temp.length() > 0 )
			return "&pointid=" + temp.substring(1);
		else
			return null;
	}

	/**
	 * Decides if a given PointID is valid or not.
	 * 
	 */
	private static boolean _isPointIDValid( Integer ptID )
	{
		return ptID != null && ptID.intValue() != CtiUtilities.NONE_ZERO_ID;
	}
	
	/**
	 * Returns the ptID as a string with a preceding comma
	 * 
	 */
	private static String _getPointStr( Integer ptID )
	{
		if( _isPointIDValid(ptID) )
			return ","+ptID;
		else
			return "";
	}

}