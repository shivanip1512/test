package com.cannontech.dbtools.controlhours;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;

/**
 * Insert the type's description here.
 * Creation date: (1/10/2001 11:18:45 PM)
 * @author: 
 */
public final class SeasonControlHoursReset 
{
	public SeasonControlHoursReset() 
	{
		super();
	}
	public static void main(String[] args) 
	{
		SeasonControlHoursReset thisObj = new SeasonControlHoursReset();
		int maxLMCtrlHistIdPlusOne = thisObj.retrieveMaxLMCtrlHistId() + 1;
		Vector groupIds = thisObj.retrieveHistoricalGroupIds();

		if( maxLMCtrlHistIdPlusOne >= 1 )
		{
			for(int i=0;i<groupIds.size();i++)
			{
				int currentGroupId = ((Integer)groupIds.get(i)).intValue();

				thisObj.resetGroupSeasonalHours(currentGroupId, maxLMCtrlHistIdPlusOne+i);
			}
		}
		else
		{
			CTILogger.info("Can not reset control hours because no entries were found in the LMControlHistory table!");
		}

		System.exit(0);
	}

	public int retrieveMaxLMCtrlHistId()
	{
		int maxLMCtrlHistId = -1;
		
		PreparedStatement preparedStatement = null;
		Connection conn = null;
		ResultSet rset = null;
		try
		{
			conn = PoolManager.getInstance().getConnection( com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

			String statement = ("SELECT MAX(LMCTRLHISTID) FROM LMCONTROLHISTORY");

			preparedStatement = conn.prepareStatement( statement );
			rset = preparedStatement.executeQuery();

			if( rset.next() )
			{
				maxLMCtrlHistId = rset.getInt(1);
			}
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if( preparedStatement != null )
					preparedStatement.close();
				if (rset != null)
					rset.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
		}

		return maxLMCtrlHistId;
	}

	public Vector retrieveHistoricalGroupIds()
	{
		Vector groupIds = new Vector();

		PreparedStatement preparedStatement = null;
		Connection conn = null;
		ResultSet rset = null;
		try
		{
			conn = PoolManager.getInstance().getConnection( com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

			String statement = ("SELECT DISTINCT PAOBJECTID FROM LMCONTROLHISTORY");

			preparedStatement = conn.prepareStatement( statement );
			rset = preparedStatement.executeQuery();

			while (rset.next())
			{
				groupIds.add(new Integer(rset.getInt(1)));
			}
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if( preparedStatement != null )
					preparedStatement.close();
				if (rset != null)
					rset.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
		}

		return groupIds;
	}
	
	public void resetGroupSeasonalHours(int groupId, int lmCtrlHistId)
	{
		CTILogger.info("Reseting seasonal hours for group with PAObjectID = " + Integer.toString(groupId));

		Vector tempRawPointHistoryVector = null;
		Vector rawPointHistoryVectorOfVectors = new Vector();

		PreparedStatement preparedStatement = null;
		Connection conn = null;
		ResultSet rset = null;
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
			preparedStatement =	conn.prepareStatement("SELECT PAOBJECTID, SOE_TAG, CURRENTDAILYTIME, "+
													  "CURRENTMONTHLYTIME, CURRENTANNUALTIME "+
													  "FROM LMCONTROLHISTORY WHERE PAOBJECTID = ? AND "+
													  "STOPDATETIME = (SELECT MAX(STOPDATETIME) FROM LMCONTROLHISTORY WHERE PAOBJECTID = ? "+
													  "AND (ACTIVERESTORE='M' OR ACTIVERESTORE='T' OR ACTIVERESTORE='L'))");

			preparedStatement.setInt(1, groupId);
			preparedStatement.setInt(2, groupId);
				
			rset = preparedStatement.executeQuery();

			Integer paobjectId = null;
			Integer soe_Tag = null;
			Integer currentDailyTime = null;
			Integer currentMonthlyTime = null;
			Integer currentAnnualTime = null;

			if( rset.next() )
			{
				paobjectId = new Integer(rset.getInt(1));
				soe_Tag = new Integer(rset.getInt(2));
				currentDailyTime = new Integer(rset.getInt(3));
				currentMonthlyTime = new Integer(rset.getInt(4));
				currentAnnualTime = new Integer(rset.getInt(5));
				/*CTILogger.info("currentDailyTime = " + currentDailyTime.toString());
				CTILogger.info("currentMonthlyTime = " + currentMonthlyTime.toString());
				CTILogger.info("currentAnnualTime = " + currentAnnualTime.toString());*/
			}

			if( paobjectId != null )
			{
				Date now = new Date();
				PreparedStatement preparedStatement2 = conn.prepareStatement("INSERT INTO LMCONTROLHISTORY VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	
				preparedStatement2.setInt(1, lmCtrlHistId);
				preparedStatement2.setInt(2, paobjectId.intValue());
				preparedStatement2.setTimestamp(3, new Timestamp(now.getTime()));
				preparedStatement2.setInt(4, soe_Tag.intValue()+1);
				preparedStatement2.setInt(5, 0);//controlDuration
				preparedStatement2.setString(6, "SEASON RESET");
				preparedStatement2.setInt(7, currentDailyTime.intValue());
				preparedStatement2.setInt(8, currentMonthlyTime.intValue());
				preparedStatement2.setInt(9, 0);//currentSeasonalTime
				preparedStatement2.setInt(10, currentAnnualTime.intValue());
				preparedStatement2.setString(11, "M");
				preparedStatement2.setDouble(12, 0.0);
				preparedStatement2.setTimestamp(13, new Timestamp(now.getTime()));

				/*CTILogger.info("currentDailyTime = " + currentDailyTime.toString());
				CTILogger.info("currentMonthlyTime = " + currentMonthlyTime.toString());
				CTILogger.info("currentAnnualTime = " + currentAnnualTime.toString());*/

				preparedStatement2.execute();
			}
			else
			{
				CTILogger.info("Control history not found for PAObjectID = " + Integer.toString(groupId));
			}
		}
		catch (java.sql.SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (preparedStatement != null)
					preparedStatement.close();
				if (rset != null)
					rset.close();
				if (conn != null)
					conn.close();
			}
			catch (java.sql.SQLException e)
			{
				e.printStackTrace();
			}
		}

		CTILogger.info("Seasonal control hours reset for group with PAObjectID = " + Integer.toString(groupId));
	}
}