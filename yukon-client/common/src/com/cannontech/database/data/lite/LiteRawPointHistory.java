package com.cannontech.database.data.lite;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;

/*
 * This class exists so Objects (specifically Vectors) of RawPointHistory do not need to be so heavy.
 * Reporting and trending can handle more data with the Lite object compared to the heavy object,
 *  avoiding OutOfMemory errors...or at least prolonging them!
 */
public class LiteRawPointHistory extends LiteBase
{
	private int changeID = 0;
	private int pointID = 0;
	private long timeStamp = CtiUtilities.get1990GregCalendar().getTimeInMillis();
	private int quality = PointQualities.INVALID_QUALITY;
	private double value = 0.0;
//	private short millis = 0;

	/**
	 * LitePoint
	 */
	public LiteRawPointHistory( int pntID ) 
	{
		super();
		setLiteID(pntID);
		setLiteType(LiteTypes.RAWPOINTHISTORY);
	}
	/**
	 * LiteRawPointHistory
	 */
	public LiteRawPointHistory( int changeID_, int pointID_, long timestamp_, int quality_, double value_) 
	{
		super();
		setLiteID( changeID_);
		setPointID(pointID_);
		setTimeStamp(timestamp_);
		setQuality(quality_);
		setValue(value_);
		setLiteType( LiteTypes.RAWPOINTHISTORY);
	}

	/**
	 * retrieve method comment.
	 */
	public void retrieve(String databaseAlias) 
	{
	   //nearly the same as the PointLoader's run() method
	   String sqlString = 
	            "SELECT POINTID, TIMESTAMP, QUALITY, VALUE " +
	            " FROM POINT " + 
	            " WHERE CHANGEID = " + Integer.toString(getChangeID());
	
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		
		try
		{
			conn = PoolManager.getInstance().getConnection( databaseAlias );
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);
	
			while( rset.next() )
			{
				setPointID(rset.getInt(1));
				Timestamp ts = rset.getTimestamp(2);
				setTimeStamp(ts.getTime());
				setQuality(rset.getInt(3));
				setValue(rset.getDouble(4));
			}
		}
		catch( java.sql.SQLException e )
		{
			CTILogger.info(e.getMessage());
		}
		finally
		{
			try
			{
				if( stmt != null )
					stmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				CTILogger.error( e.getMessage(), e );
			}
		}
	}
	
	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public String toString()
	{
		return PointFuncs.getPointName(getPointID()) + ":" + getTimeStamp() + ":" + getValue();
	}

	/**
	 * @return
	 */
	public int getChangeID()
	{
		return changeID;
	}

	/**
	 * @return
	 */
	public int getPointID()
	{
		return pointID;
	}

	/**
	 * @return
	 */
	public int getQuality()
	{
		return quality;
	}

	/**
	 * @return
	 */
	public long getTimeStamp()
	{
		return timeStamp;
	}

	/**
	 * @return
	 */
	public double getValue()
	{
		return value;
	}

	/**
	 * @param i
	 */
	public void setChangeID(int i)
	{
		changeID = i;
	}

	/**
	 * @param i
	 */
	public void setPointID(int i)
	{
		pointID = i;
	}

	/**
	 * @param i
	 */
	public void setQuality(int i)
	{
		quality = i;
	}

	/**
	 * @param l
	 */
	public void setTimeStamp(long l)
	{
		timeStamp = l;
	}

	/**
	 * @param d
	 */
	public void setValue(double d)
	{
		value = d;
	}

}
