package com.cannontech.stars.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.Validate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteLMConfiguration;
import com.cannontech.database.data.lite.stars.LiteLMControlHistory;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMControlHistory;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.pao.LMControlHistory;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.util.task.LMCtrlHistTimerTask;
import com.cannontech.stars.xml.serialize.ControlHistory;
import com.cannontech.stars.xml.serialize.ControlSummary;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class LMControlHistoryUtil {
	
	public static final int TOTAL_CONTROL_TIME = 0;
    public static final int TOTAL_OPTOUT_EVENTS = 1;
    public static final int TOTAL_OPTOUT_TIME = 2;
    public static final int TOTAL_CONTROL_DURING_OPTOUT_TIME = 3;
    
    private static Hashtable<Integer, LiteStarsLMControlHistory> activeCtrlHist = 
    	new Hashtable<Integer, LiteStarsLMControlHistory>();
	
	private static boolean isUpToDate(LiteStarsLMControlHistory liteCtrlHist) {
		return (System.currentTimeMillis() - liteCtrlHist.getLastSearchedStopTime()) * 0.001 < LMCtrlHistTimerTask.TIMER_PERIOD * 2;
	}
	
	public synchronized static void addActiveControlHistory(LiteStarsLMControlHistory liteCtrlHist) {
		activeCtrlHist.put( new Integer(liteCtrlHist.getGroupID()), liteCtrlHist );
	}
	
	public synchronized static LiteStarsLMControlHistory getActiveControlHistory(int groupID) {
		return activeCtrlHist.get( new Integer(groupID) );
	}
	
	public synchronized static void clearActiveControlHistory() {
		activeCtrlHist.clear();
	}

	public static Date getPeriodStartTime(StarsCtrlHistPeriod period, TimeZone tz) {
		Date date = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(TimeUtil.getMidnight(tz));

		if (period.getType() == StarsCtrlHistPeriod.ALL_TYPE) {
			date = new Date(0);
		}
		else if (period.getType() == StarsCtrlHistPeriod.PASTDAY_TYPE) {
			date = cal.getTime();
		}
		else if (period.getType() == StarsCtrlHistPeriod.PASTWEEK_TYPE) {
			cal.add( Calendar.WEEK_OF_YEAR, -1 );
			date = cal.getTime();
		}
		else if (period.getType() == StarsCtrlHistPeriod.PASTMONTH_TYPE) {
			cal.add( Calendar.MONTH, -1 );
			date = cal.getTime();
		}
		else if (period.getType() == StarsCtrlHistPeriod.PASTYEAR_TYPE) {
			cal.add( Calendar.YEAR, -1 );
			date = cal.getTime();
		}
    	
		return date;
	}

	public static LMControlHistory[] getLMControlHistory(int groupID, Date dateFrom, Date dateTo) {
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
        
		List<LMControlHistory> ctrlHistList = new ArrayList<LMControlHistory>();

		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			if (conn == null) return null;
            
			StringBuffer sql = new StringBuffer("SELECT LMCTRLHISTID");
			for (int i = 0; i < LMControlHistory.SETTER_COLUMNS.length; i++)
				sql.append(", ").append(LMControlHistory.SETTER_COLUMNS[i]);
			sql.append(" FROM ").append(LMControlHistory.TABLE_NAME)
			   .append(" WHERE PAOBJECTID = ?");
			if (dateFrom != null)
				sql.append(" AND STOPDATETIME > ?");
			if (dateTo != null)
				sql.append(" AND STARTDATETIME < ?");
			sql.append(" ORDER BY LMCTRLHISTID");
            
			pstmt = conn.prepareStatement( sql.toString() );
			pstmt.setInt( 1, groupID );
			int paramIdx = 2;
			if (dateFrom != null)
				pstmt.setTimestamp( paramIdx++, new java.sql.Timestamp(dateFrom.getTime()) );
			if (dateTo != null)
				pstmt.setTimestamp( paramIdx, new java.sql.Timestamp(dateTo.getTime()) );
            
			rset = pstmt.executeQuery();
			while (rset.next()) {
				LMControlHistory ctrlHist = new LMControlHistory();

				ctrlHist.setLmCtrlHistID( new Integer(rset.getInt(1)) );
				ctrlHist.setPaObjectID( new Integer(rset.getInt(2)) );
				ctrlHist.setStartDateTime( new java.util.Date(rset.getTimestamp(3).getTime()) );
				ctrlHist.setSoeTag( new Integer(rset.getInt(4)) );
				ctrlHist.setControlDuration( new Integer(rset.getInt(5)) );
				ctrlHist.setControlType( rset.getString(6) );
				ctrlHist.setCurrentDailyTime( new Integer(rset.getInt(7)) );
				ctrlHist.setCurrentMonthlyTime( new Integer(rset.getInt(8)) );
				ctrlHist.setCurrentSeasonalTime( new Integer(rset.getInt(9)) );
				ctrlHist.setCurrentAnnualTime( new Integer(rset.getInt(10)) );
				ctrlHist.setActiveRestore( rset.getString(11) );
				ctrlHist.setReductionValue( new Double(rset.getDouble(12)) );
				ctrlHist.setStopDateTime( new java.util.Date(rset.getTimestamp(13).getTime()) );

				ctrlHistList.add( ctrlHist );
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		finally {
			try {
				if (rset != null) rset.close();
				if( pstmt != null ) pstmt.close();
				if (conn != null) conn.close();
			}
			catch (Exception e) {
				CTILogger.error( e.getMessage(), e );
			}
		}

		com.cannontech.database.db.pao.LMControlHistory[] ctrlHists = new com.cannontech.database.db.pao.LMControlHistory[ ctrlHistList.size() ];
		ctrlHistList.toArray( ctrlHists );

		return ctrlHists;
	}
    
	public static com.cannontech.database.db.pao.LMControlHistory[] getLMControlHistory(int groupID, int startCtrlHistID) {
		StringBuffer sql = new StringBuffer("SELECT LMCTRLHISTID");
		for (int i = 0; i < LMControlHistory.SETTER_COLUMNS.length; i++)
			sql.append(", ").append(LMControlHistory.SETTER_COLUMNS[i]);
		sql.append(" FROM ").append(LMControlHistory.TABLE_NAME)
		   .append(" WHERE PAOBJECTID = ").append(groupID);
		if (startCtrlHistID > 0)
			sql.append(" AND LMCTRLHISTID > ").append(startCtrlHistID);
		
		SqlStatement stmt = new SqlStatement( sql.toString(), CtiUtilities.getDatabaseAlias() );

		try {
			stmt.execute();
			
			com.cannontech.database.db.pao.LMControlHistory[] ctrlHist =
					new com.cannontech.database.db.pao.LMControlHistory[ stmt.getRowCount() ];

			for (int i = 0; i < stmt.getRowCount(); i++) {
				Object[] row = stmt.getRow(i);
				ctrlHist[i] = new com.cannontech.database.db.pao.LMControlHistory();

				ctrlHist[i].setLmCtrlHistID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				ctrlHist[i].setPaObjectID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				ctrlHist[i].setStartDateTime( new Date(((java.sql.Timestamp) row[2]).getTime()) );
				ctrlHist[i].setSoeTag( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
				ctrlHist[i].setControlDuration( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
				ctrlHist[i].setControlType( (String) row[5] );
				ctrlHist[i].setCurrentDailyTime( new Integer(((java.math.BigDecimal) row[6]).intValue()) );
				ctrlHist[i].setCurrentMonthlyTime( new Integer(((java.math.BigDecimal) row[7]).intValue()) );
				ctrlHist[i].setCurrentSeasonalTime( new Integer(((java.math.BigDecimal) row[8]).intValue()) );
				ctrlHist[i].setCurrentAnnualTime( new Integer(((java.math.BigDecimal) row[9]).intValue()) );
				ctrlHist[i].setActiveRestore( (String) row[10] );
				ctrlHist[i].setReductionValue( (Double) row[11] );
				ctrlHist[i].setStopDateTime( new Date(((java.sql.Timestamp) row[12]).getTime()) );
			}
			
			return ctrlHist;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return new com.cannontech.database.db.pao.LMControlHistory[0];
	}

	public static com.cannontech.database.db.pao.LMControlHistory getLastLMControlHistory(int groupID, int startCtrlHistID) {
		StringBuffer sql = new StringBuffer("SELECT LMCTRLHISTID");
		for (int i = 0; i < LMControlHistory.SETTER_COLUMNS.length; i++)
			sql.append(", ").append(LMControlHistory.SETTER_COLUMNS[i]);
		sql.append(" FROM ").append(LMControlHistory.TABLE_NAME)
		   .append(" WHERE LMCTRLHISTID = ")
		   .append("(SELECT MAX(LMCTRLHISTID) FROM ").append(LMControlHistory.TABLE_NAME)
		   .append(" WHERE PAOBJECTID = ").append(groupID);
		if (startCtrlHistID > 0)
			sql.append(" AND LMCTRLHISTID > ").append(startCtrlHistID);
		sql.append(")");
		
		SqlStatement stmt = new SqlStatement( sql.toString(), CtiUtilities.getDatabaseAlias() );

		try {
			stmt.execute();

			if (stmt.getRowCount() > 0) {
				Object[] row = stmt.getRow(0);
				com.cannontech.database.db.pao.LMControlHistory ctrlHist = new com.cannontech.database.db.pao.LMControlHistory();

				ctrlHist.setLmCtrlHistID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				ctrlHist.setPaObjectID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				ctrlHist.setStartDateTime( new Date(((java.sql.Timestamp) row[2]).getTime()) );
				ctrlHist.setSoeTag( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
				ctrlHist.setControlDuration( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
				ctrlHist.setControlType( (String) row[5] );
				ctrlHist.setCurrentDailyTime( new Integer(((java.math.BigDecimal) row[6]).intValue()) );
				ctrlHist.setCurrentMonthlyTime( new Integer(((java.math.BigDecimal) row[7]).intValue()) );
				ctrlHist.setCurrentSeasonalTime( new Integer(((java.math.BigDecimal) row[8]).intValue()) );
				ctrlHist.setCurrentAnnualTime( new Integer(((java.math.BigDecimal) row[9]).intValue()) );
				ctrlHist.setActiveRestore( (String) row[10] );
				ctrlHist.setReductionValue( (Double) row[11] );
				ctrlHist.setStopDateTime( new Date(((java.sql.Timestamp) row[12]).getTime()) );

				return ctrlHist;
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return null;
	}
    
	public static int getLastLMCtrlHistID() {
		String sql = "SELECT MAX(LMCTRLHISTID) FROM " + LMControlHistory.TABLE_NAME;
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
    	
		try {
			stmt.execute();
			if (stmt.getRowCount() > 0)
				return ((java.math.BigDecimal)stmt.getRow(0)[0]).intValue();
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return 0;
	}
    
	public static com.cannontech.database.db.pao.LMControlHistory[] getLMControlHistory(int startCtrlHistID) {
		StringBuffer sql = new StringBuffer("SELECT LMCTRLHISTID");
		for (int i = 0; i < LMControlHistory.SETTER_COLUMNS.length; i++)
			sql.append(", ").append(LMControlHistory.SETTER_COLUMNS[i]);
		sql.append(" FROM ").append(LMControlHistory.TABLE_NAME);
		if (startCtrlHistID > 0)
			sql.append(" WHERE LMCTRLHISTID > ").append(startCtrlHistID);
		
		SqlStatement stmt = new SqlStatement( sql.toString(), CtiUtilities.getDatabaseAlias() );

		try {
			stmt.execute();
			
			com.cannontech.database.db.pao.LMControlHistory[] ctrlHist =
					new com.cannontech.database.db.pao.LMControlHistory[ stmt.getRowCount() ];

			for (int i = 0; i < stmt.getRowCount(); i++) {
				Object[] row = stmt.getRow(i);
				ctrlHist[i] = new com.cannontech.database.db.pao.LMControlHistory();

				ctrlHist[i].setLmCtrlHistID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				ctrlHist[i].setPaObjectID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				ctrlHist[i].setStartDateTime( new Date(((java.sql.Timestamp) row[2]).getTime()) );
				ctrlHist[i].setSoeTag( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
				ctrlHist[i].setControlDuration( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
				ctrlHist[i].setControlType( (String) row[5] );
				ctrlHist[i].setCurrentDailyTime( new Integer(((java.math.BigDecimal) row[6]).intValue()) );
				ctrlHist[i].setCurrentMonthlyTime( new Integer(((java.math.BigDecimal) row[7]).intValue()) );
				ctrlHist[i].setCurrentSeasonalTime( new Integer(((java.math.BigDecimal) row[8]).intValue()) );
				ctrlHist[i].setCurrentAnnualTime( new Integer(((java.math.BigDecimal) row[9]).intValue()) );
				ctrlHist[i].setActiveRestore( (String) row[10] );
				ctrlHist[i].setReductionValue( (Double) row[11] );
				ctrlHist[i].setStopDateTime( new Date(((java.sql.Timestamp) row[12]).getTime()) );
			}
			
			return ctrlHist;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return new com.cannontech.database.db.pao.LMControlHistory[0];
	}
	
	/**
	 * Based on the hardware addressing and relay number, get all the load groups
	 * that could control the corresponding load.
	 */
	public static int[] getControllableGroupIDs(LiteLMConfiguration liteCfg, int relayNo) {
		if (relayNo <= 0) return new int[0];
		
		List<Integer> groupIDs = new ArrayList<Integer>();
		
		try {
			if (liteCfg.getExpressCom() != null) {
				String sql = "SELECT LMGroupID, addr1.Address, addr2.Address, addr3.Address, ZipCodeAddress, UDAddress, addr4.Address, SplinterAddress, AddressUsage, RelayUsage"
						+ " FROM LMGroupExpresscom, LMGroupExpresscomAddress addr1, LMGroupExpresscomAddress addr2, LMGroupExpresscomAddress addr3, LMGroupExpresscomAddress addr4, LMGroupExpresscomAddress addr5"
						+ " WHERE SerialNumber = 0 AND ServiceProviderID = addr5.AddressID AND addr5.Address = " + liteCfg.getExpressCom().getServiceProvider()
						+ " AND GeoID = addr1.AddressID AND SubstationID = addr2.AddressID AND FeederID = addr3.AddressID AND ProgramID = addr4.AddressID";
				
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					int groupID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
					int geoAddress = ((java.math.BigDecimal) stmt.getRow(i)[1]).intValue();
					int substationAddress = ((java.math.BigDecimal) stmt.getRow(i)[2]).intValue();
					int feederAddress = ((java.math.BigDecimal) stmt.getRow(i)[3]).intValue();
					int zipCodeAddress = ((java.math.BigDecimal) stmt.getRow(i)[4]).intValue();
					int udAddress = ((java.math.BigDecimal) stmt.getRow(i)[5]).intValue();
					int programAddress = ((java.math.BigDecimal) stmt.getRow(i)[6]).intValue();
					int splinterAddress = ((java.math.BigDecimal) stmt.getRow(i)[7]).intValue();
					String addressUsage = (String) stmt.getRow(i)[8];
					String relayUsage = (String) stmt.getRow(i)[9];
					
					if (addressUsage.indexOf("G") >= 0 && liteCfg.getExpressCom().getGEO() != geoAddress) continue;
					if (addressUsage.indexOf("B") >= 0 && liteCfg.getExpressCom().getSubstation() != substationAddress) continue;
					if (addressUsage.indexOf("F") >= 0 && liteCfg.getExpressCom().getFeeder() != feederAddress) continue;
					if (addressUsage.indexOf("Z") >= 0 && liteCfg.getExpressCom().getZip() != zipCodeAddress) continue;
					if (addressUsage.indexOf("U") >= 0 && liteCfg.getExpressCom().getUserAddress() != udAddress) continue;
					if (addressUsage.indexOf("L") >= 0) {
						if (relayUsage.indexOf( Character.forDigit(relayNo, 10) ) < 0) continue;
					}
					else {
						if (addressUsage.indexOf("P") >= 0) {
							int program = 0;
							String[] programs = liteCfg.getExpressCom().getProgram().split(",");
							if (programs.length >= relayNo && programs[relayNo-1].length() > 0)
								program = Integer.parseInt( programs[relayNo-1] );
							if (program != programAddress) continue;
						}
						if (addressUsage.indexOf("R") >= 0) {
							int splinter = 0;
							String[] splinters = liteCfg.getExpressCom().getSplinter().split(",");
							if (splinters.length >= relayNo && splinters[relayNo-1].length() > 0)
								splinter = Integer.parseInt( splinters[relayNo-1] );
							if (splinter != splinterAddress) continue;
						}
					}
					
					groupIDs.add( new Integer(groupID) );
				}
			}
			else if (liteCfg.getVersaCom() != null) {
				String sql = "SELECT DeviceID, SectionAddress, classAddress, divisionAddress, AddressUsage, RelayUsage"
						+ " FROM LMGroupVersacom WHERE SerialAddress = 0"
						+ " AND UtilityAddress = " + liteCfg.getVersaCom().getUtilityID();
				
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					int groupID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
					int sectionAddress = ((java.math.BigDecimal) stmt.getRow(i)[1]).intValue();
					int classAddress = ((java.math.BigDecimal) stmt.getRow(i)[2]).intValue();
					int divisionAddress = ((java.math.BigDecimal) stmt.getRow(i)[3]).intValue();
					String addressUsage = (String) stmt.getRow(i)[4];
					String relayUsage = (String) stmt.getRow(i)[5];
					
					if (addressUsage.indexOf("S") >= 0 && liteCfg.getVersaCom().getSection() != sectionAddress) continue;
					if (addressUsage.indexOf("C") >= 0 && liteCfg.getVersaCom().getClassAddress() != classAddress) continue;
					if (addressUsage.indexOf("D") >= 0 && liteCfg.getVersaCom().getDivisionAddress() != divisionAddress) continue;
					if (relayUsage.indexOf( Character.forDigit(relayNo, 10) ) < 0) continue;
					
					groupIDs.add( new Integer(groupID) );
				}
			}
			else if (liteCfg.getSA205() != null) {
				String sql = "SELECT GroupID, OperationalAddress FROM LMGroupSA205105 WHERE ";
				if (relayNo == 1)
					sql += "LoadNumber='Load 1' OR LoadNumber='Load 1,2' OR LoadNumber='Load 1,2,3' OR LoadNumber='Load 1,2,3,4'";
				else if (relayNo == 2)
					sql += "LoadNumber='Load 2' OR LoadNumber='Load 1,2' OR LoadNumber='Load 1,2,3' OR LoadNumber='Load 1,2,3,4'";
				else if (relayNo == 3)
					sql += "LoadNumber='Load 3' OR LoadNumber='Load 1,2,3' OR LoadNumber='Load 1,2,3,4'";
				else if (relayNo == 4)
					sql += "LoadNumber='Load 4' OR LoadNumber='Load 1,2,3,4'";
				else
					return new int[0];
				
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					int groupID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
					int operationalAddress = ((java.math.BigDecimal) stmt.getRow(i)[1]).intValue();
					
					if (operationalAddress == liteCfg.getSA205().getSlot1()
						|| operationalAddress == liteCfg.getSA205().getSlot2()
						|| operationalAddress == liteCfg.getSA205().getSlot3()
						|| operationalAddress == liteCfg.getSA205().getSlot4()
						|| operationalAddress == liteCfg.getSA205().getSlot5()
						|| operationalAddress == liteCfg.getSA205().getSlot6())
						groupIDs.add( new Integer(groupID) );
				}
			}
			else if (liteCfg.getSA305() != null) {
				String sql = "SELECT GroupID, AddressUsage, GroupAddress, DivisionAddress, SubstationAddress, LoadNumber"
						+ " FROM LMGroupSA305 WHERE AddressUsage <> 'U' AND LoadNumber <> ''"
						+ " AND UtilityAddress = " + liteCfg.getSA305().getUtility()
						+ " AND RateFamily = " + liteCfg.getSA305().getRateFamily()
						+ " AND RateMember = " + liteCfg.getSA305().getRateMember()
						+ " AND RateHierarchy = " + liteCfg.getSA305().getRateHierarchy();
				
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					int groupID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
					String addressUsage = (String) stmt.getRow(i)[1];
					int groupAddress = ((java.math.BigDecimal) stmt.getRow(i)[2]).intValue();
					int divisionAddress = ((java.math.BigDecimal) stmt.getRow(i)[3]).intValue();
					int substationAddress = ((java.math.BigDecimal) stmt.getRow(i)[4]).intValue();
					String loadNumber = (String) stmt.getRow(i)[5];
					
					if (addressUsage.indexOf("G") >= 0 && liteCfg.getSA305().getGroup() != groupAddress) continue;
					if (addressUsage.indexOf("D") >= 0 && liteCfg.getSA305().getDivision() != divisionAddress) continue;
					if (addressUsage.indexOf("S") >= 0 && liteCfg.getSA305().getSubstation() != substationAddress) continue;
					if (loadNumber.indexOf( Character.forDigit(relayNo, 10) ) < 0) continue;
					
					groupIDs.add( new Integer(groupID) );
				}
			}
			else if (liteCfg.getSASimple() != null) {
				String sql = "SELECT GroupID FROM LMGroupSASimple " +
					"WHERE OperationalAddress = '" + liteCfg.getSASimple().getOperationalAddress() + "'";
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					int groupID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
					groupIDs.add( new Integer(groupID) );
				}
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			return null;
		}
		
		int[] ids = new int[ groupIDs.size() ];
		for (int i = 0; i < groupIDs.size(); i++)
			ids[i] = groupIDs.get(i);
		return ids;
	}
	
	public static StarsLMControlHistory getStarsLMControlHistory(int groupID, int accountId, StarsCtrlHistPeriod period, TimeZone tz, LiteYukonUser currentUser) {
        /*
         * Don't forget that startDate is simply based on whether it was an ALL, WEEKLY, or MONTHLY choice on the UI page.
         * It isn't a true control period start date of any kind.  Example:  ALL means a startDate value of new Date()
         * Now that the summary is calculated by looking at all control history entries for a group and account each time we
         * need to do away with passing in this startDate, we need to override this every time with a value of ALL.
         */ 
        Date startDate = LMControlHistoryUtil.getPeriodStartTime( period, tz );
        Date oneYearAgoDate = LMControlHistoryUtil.getPeriodStartTime( StarsCtrlHistPeriod.PASTYEAR, tz );

		LiteStarsLMControlHistory liteCtrlHist;
		if(period.getType() == StarsCtrlHistPeriod.ALL_TYPE)
		    liteCtrlHist = StarsDatabaseCache.getInstance().getLMControlHistory( groupID, new Date(0) );
        /*Try to help performance a little bit here if we can.  loading a year is better than loading all entries if we don't need to.*/
        else
            liteCtrlHist = StarsDatabaseCache.getInstance().getLMControlHistory( groupID, oneYearAgoDate );
		addActiveControlHistory( liteCtrlHist );
		
        //New enrollment, opt out, and control history tracking
        //-------------------------------------------------------------------------------
        LMHardwareControlGroupDao lmHardwareControlGroupDao = (LMHardwareControlGroupDao) YukonSpringHook.getBean("lmHardwareControlGroupDao");
        List<LMHardwareControlGroup> enrollments = lmHardwareControlGroupDao.getByLMGroupIdAndAccountIdAndType(groupID, accountId, LMHardwareControlGroup.ENROLLMENT_ENTRY);
        List<LMHardwareControlGroup> optOuts = lmHardwareControlGroupDao.getByLMGroupIdAndAccountIdAndType(groupID, accountId, LMHardwareControlGroup.OPT_OUT_ENTRY);
        
        StarsLMControlHistory starsCtrlHist = buildStarsControlHistoryForPeriod(liteCtrlHist, period, tz);
        /*
         * Wait until now to iterate through starsCtrlHist since the totals have already been
         * calculated from all types of ActiveRestore types
         */
        StarsLMControlHistory currentStarsLMControlHistory = adjustLMControlHistoryByEnrollmentAndOptOutTimes(starsCtrlHist, startDate, enrollments, optOuts);
        
        ControlSummary summaryForToday = new ControlSummary();
        
        if(period.getType() == StarsCtrlHistPeriod.PASTDAY_TYPE) {
            /*have to do each period separately here, otherwise carry-over control gets missed since StarsLMControlHistory
             * objects are constructed differently from the original control history database entries depending on the period.
             * Example: Control that started at 10 pm yesterday and ran until 7 this morning should show up for today's period
             * as 7 hours of control even though it was 10 hours of total control starting yesterday.
             */
            StarsLMControlHistory periodCtrlHist = buildStarsControlHistoryForPeriod(liteCtrlHist, StarsCtrlHistPeriod.PASTYEAR, tz);
            summaryForToday.setAnnualTime(
                                          calculateSummaryControlValueForPeriod(periodCtrlHist, StarsCtrlHistPeriod.PASTYEAR, tz, enrollments, optOuts));
            
            periodCtrlHist = buildStarsControlHistoryForPeriod(liteCtrlHist, StarsCtrlHistPeriod.PASTMONTH, tz);
            summaryForToday.setMonthlyTime(
                                          calculateSummaryControlValueForPeriod(periodCtrlHist, StarsCtrlHistPeriod.PASTMONTH, tz, enrollments, optOuts));
            
            periodCtrlHist = buildStarsControlHistoryForPeriod(liteCtrlHist, StarsCtrlHistPeriod.PASTDAY, tz);
            summaryForToday.setDailyTime(
                                          calculateSummaryControlValueForPeriod(periodCtrlHist, StarsCtrlHistPeriod.PASTDAY, tz, enrollments, optOuts));
        }
        
        currentStarsLMControlHistory.setControlSummary(summaryForToday);
        currentStarsLMControlHistory.setBeingControlled(starsCtrlHist.getBeingControlled());
        //-------------------------------------------------------------------------------
		
        return currentStarsLMControlHistory;
	}
	
	public static void updateActiveControlHistory() {
		Hashtable<Integer, LiteStarsLMControlHistory> ctrlHistMap = 
			new Hashtable<Integer, LiteStarsLMControlHistory> ( activeCtrlHist );
		if (ctrlHistMap.size() == 0) return;
		
		long lastSearchedTime = System.currentTimeMillis();
		int startCtrlHistID = Integer.MAX_VALUE;
		
		Iterator<LiteStarsLMControlHistory> it = ctrlHistMap.values().iterator();
		while (it.hasNext()) {
			LiteStarsLMControlHistory liteCtrlHist = it.next();
			
			if (liteCtrlHist.getLastControlHistory() != null &&
				liteCtrlHist.getLastControlHistory().getLmCtrlHistID() < startCtrlHistID)
				startCtrlHistID = liteCtrlHist.getLastControlHistory().getLmCtrlHistID();
			if (isUpToDate(liteCtrlHist) && liteCtrlHist.getLastSearchedCtrlHistID() < startCtrlHistID)
				startCtrlHistID = liteCtrlHist.getLastSearchedCtrlHistID();
		}
		
		com.cannontech.database.db.pao.LMControlHistory[] ctrlHist = getLMControlHistory( startCtrlHistID );
		
		for (int i = 0; i < ctrlHist.length; i++) {
			LiteStarsLMControlHistory liteCtrlHist = ctrlHistMap.get( ctrlHist[i].getPaObjectID() );
			if (liteCtrlHist == null) continue;
			
			LiteLMControlHistory lastCtrlHist = liteCtrlHist.getLastControlHistory();
			if (lastCtrlHist != null && lastCtrlHist.getLmCtrlHistID() < ctrlHist[i].getLmCtrlHistID().intValue())
			{
				lastCtrlHist.setCurrentDailyTime( ctrlHist[i].getCurrentDailyTime().longValue() );
				lastCtrlHist.setCurrentMonthlyTime( ctrlHist[i].getCurrentMonthlyTime().longValue() );
				lastCtrlHist.setCurrentSeasonalTime( ctrlHist[i].getCurrentSeasonalTime().longValue() );
				lastCtrlHist.setCurrentAnnualTime( ctrlHist[i].getCurrentAnnualTime().longValue() );
			}
			
			if (liteCtrlHist.getLastSearchedCtrlHistID() >= startCtrlHistID &&
				liteCtrlHist.getLastSearchedCtrlHistID() < ctrlHist[i].getLmCtrlHistID().intValue())
			{
				liteCtrlHist.getLmControlHistory().add( StarsLiteFactory.createLite(ctrlHist[i]) );
			}
		}
		
		int lastSearchedID = 0;
		if (ctrlHist.length > 0)
			lastSearchedID = ctrlHist[ctrlHist.length -1].getLmCtrlHistID().intValue();
		
		it = ctrlHistMap.values().iterator();
		while (it.hasNext()) {
			LiteStarsLMControlHistory liteCtrlHist = it.next();
			
			if (liteCtrlHist.getLastControlHistory() != null && lastSearchedID > 0)
				liteCtrlHist.getLastControlHistory().setLmCtrlHistID( lastSearchedID );
			
			if (liteCtrlHist.getLastSearchedCtrlHistID() >= startCtrlHistID) {
				liteCtrlHist.setLastSearchedStopTime( lastSearchedTime );
				if (lastSearchedID > 0)
					liteCtrlHist.setLastSearchedCtrlHistID( lastSearchedID );
			}
		}
	}
    
	//New enrollment, opt out, and control history tracking
    //-------------------------------------------------------------------------------
    private static long calculateRealControlPeriodTime(long controlHistoryTotal, Date periodStart, Date periodStop, List<LMHardwareControlGroup> enrollments, List<LMHardwareControlGroup> optOuts) {
        Validate.notNull(periodStart);
        Validate.notNull(periodStop);
        Validate.notNull(enrollments);
        Validate.notNull(optOuts);
        
        boolean neverEnrolledDuringThisPeriod = true;
        
        for(LMHardwareControlGroup enrollmentEntry : enrollments) {
            //wasn't enrolled with this entry at the time
            if(enrollmentEntry.getGroupEnrollStart().getTime() > periodStop.getTime() ||
                    (enrollmentEntry.getGroupEnrollStop() != null && enrollmentEntry.getGroupEnrollStop().getTime() < periodStart.getTime())) {
                continue;
            }
            else {
                //period falls cleanly within the enrollment range, total remains the same
                if(enrollmentEntry.getGroupEnrollStart().getTime() < periodStart.getTime() &&
                        (enrollmentEntry.getGroupEnrollStop() == null || enrollmentEntry.getGroupEnrollStop().getTime() > periodStop.getTime())) {
                    neverEnrolledDuringThisPeriod = false;
                }
                //enrollment started after the beginning of the period, subtract the difference
                else if(enrollmentEntry.getGroupEnrollStart().getTime() > periodStart.getTime()) { 
                    controlHistoryTotal = controlHistoryTotal - (enrollmentEntry.getGroupEnrollStart().getTime() - periodStart.getTime());
                    neverEnrolledDuringThisPeriod = false;
                }
                //enrollment stopped before the end of the period, subtract the difference
                else if(enrollmentEntry.getGroupEnrollStop() != null && enrollmentEntry.getGroupEnrollStop().getTime() < periodStop.getTime()) {
                    controlHistoryTotal = controlHistoryTotal - (periodStop.getTime() - enrollmentEntry.getGroupEnrollStop().getTime());
                    neverEnrolledDuringThisPeriod = false;
                }
            }
        }
        
        if(neverEnrolledDuringThisPeriod)
            return -1;
        
        for(LMHardwareControlGroup optOutEntry : optOuts) {
            //control occurred entirely during an opt out.  Discard it.
            if(optOutEntry.getOptOutStart().getTime() < periodStart.getTime() && optOutEntry.getOptOutStop() != null &&
                    optOutEntry.getOptOutStop().getTime() > periodStop.getTime()) {
                return -1;
            }
            //An opt out started during control, control stopped before opt out ended
            //Subtract the difference of opt out start and the period's stop from duration.
            else if(optOutEntry.getOptOutStart().getTime() > periodStart.getTime() && optOutEntry.getOptOutStart().getTime() < periodStop.getTime() && 
                    (optOutEntry.getOptOutStop() == null || optOutEntry.getOptOutStop().getTime() > periodStop.getTime())) {
                controlHistoryTotal = controlHistoryTotal - (periodStop.getTime() - optOutEntry.getOptOutStart().getTime());
            }
            //control occurred during an already active opt out, that opt out then ended before control was complete
            //subtract the difference of opt out stop and period start.
            else if(optOutEntry.getOptOutStart().getTime() < periodStart.getTime() && optOutEntry.getOptOutStop() != null &&
                    optOutEntry.getOptOutStop().getTime() > periodStart.getTime() && optOutEntry.getOptOutStop().getTime() < periodStop.getTime()) {
                controlHistoryTotal = controlHistoryTotal - (optOutEntry.getOptOutStop().getTime() - periodStart.getTime());
            }
            //an opt out occurred cleanly in the middle of a control period
            //subtract the entire opt out duration from the control history total
            else if(optOutEntry.getOptOutStart().getTime() > periodStart.getTime() && optOutEntry.getOptOutStop() != null &&
                    optOutEntry.getOptOutStop().getTime() < periodStop.getTime()) {
                controlHistoryTotal = controlHistoryTotal - (optOutEntry.getOptOutStop().getTime() - optOutEntry.getOptOutStart().getTime());
            }
        }
        
        if(controlHistoryTotal < 0)
            return -1;
        return controlHistoryTotal;
    }
    
    private static StarsLMControlHistory buildStarsControlHistoryForPeriod(LiteStarsLMControlHistory liteCtrlHist, StarsCtrlHistPeriod period, TimeZone tz) {
        StarsLMControlHistory starsCtrlHist = new StarsLMControlHistory();
        /*TODO: is using the time zone here appropriate or will this make us miss control history since the 
         *server time is what we care about as far as control history?
         */
        Date startDate = LMControlHistoryUtil.getPeriodStartTime( period, tz );
        
        ControlHistory hist = null;
        Date lastStartTime = null;
        Date lastStopTime = null;
        Date histStartDate = null;
        
        for (int i = 0; i < liteCtrlHist.getLmControlHistory().size(); i++) {
            LiteLMControlHistory lmCtrlHist = (LiteLMControlHistory) liteCtrlHist.getLmControlHistory().get(i);
            
            if (StarsUtils.isDateBefore( new Date(lmCtrlHist.getStopDateTime()), startDate )) continue;
                
            Date date = new Date( lmCtrlHist.getStartDateTime() );
            
            /*
             * ActiveRestore is defined as below:
             * N - This is the first entry for any new control.
             * C - Previous command was repeated extending the current control interval.
             * T - Control terminated based on time set in load group.
             * M - Control terminated because of an active restore or terminate command being sent.
             * O - Control terminated because a new command of a different nature was sent to this group.
             * L - Time log
             */
            if (lmCtrlHist.getActiveRestore().equals("N")) {
                hist = new ControlHistory();
                if (!StarsUtils.isDateEqual(date, lastStartTime)) {
                    // This is a new control
                    lastStartTime = date;
                    lastStopTime = new Date( lmCtrlHist.getStopDateTime() );
                    if (StarsUtils.isDateBefore(date, startDate))
                        histStartDate = startDate;
                    else
                        histStartDate = date;
                    
                    hist.setStartDateTime( histStartDate );
                    hist.setControlDuration( 0 );
                    starsCtrlHist.addControlHistory( hist );
                }
                else {  // This is the continuation of the last control
                    lastStopTime = new Date( lmCtrlHist.getStopDateTime() );
                }
                hist.setIsCurrentlyControlling(true);
            }
            else if (lmCtrlHist.getActiveRestore().equals("C")
                    || lmCtrlHist.getActiveRestore().equals("L"))
            {
                if (hist == null && StarsUtils.isDateBefore(date, startDate)) {
                    // This is a new control
                    lastStartTime = date;
                    lastStopTime = new Date( lmCtrlHist.getStopDateTime() );
                    histStartDate = startDate;
                    
                    hist = new ControlHistory();
                    hist.setStartDateTime( histStartDate );
                    hist.setControlDuration( 0 );
                    starsCtrlHist.addControlHistory( hist );
                }
                else if (StarsUtils.isDateEqual(date, lastStartTime)) {
                    if (hist != null)
                        hist.setControlDuration( (int)((lmCtrlHist.getStopDateTime() - histStartDate.getTime()) * 0.001) );
                }
            }
            else if (lmCtrlHist.getActiveRestore().equals("M")
                    || lmCtrlHist.getActiveRestore().equals("T")
                    || lmCtrlHist.getActiveRestore().equals("O"))
            {
                if (hist != null) {
                    hist.setIsCurrentlyControlling(false);
                }
                if (StarsUtils.isDateEqual(date, lastStartTime)) {
                    lastStopTime = new Date(lmCtrlHist.getStopDateTime());
                    if (hist != null) {
                        hist.setControlDuration( (int)((lmCtrlHist.getStopDateTime() - histStartDate.getTime()) * 0.001) );
                    }
                }
                //remember, this is a reference to what is already in starsCtrlHist's list.  he's just nulling out the reference
                hist = null;
            }
        }
        
        if (lastStopTime != null)
            starsCtrlHist.setBeingControlled( StarsUtils.isDateAfter(lastStopTime, null) );
        
        return starsCtrlHist;
    }
    
    private static int calculateSummaryControlValueForPeriod(StarsLMControlHistory starsCtrlHist, StarsCtrlHistPeriod period, TimeZone tz, List<LMHardwareControlGroup> enrollments, List<LMHardwareControlGroup> optOuts) {
        Date periodStartDate = getPeriodStartTime( period, tz );
        int recordedControl = 0;
        
        for(int j = 0; j < starsCtrlHist.getControlHistoryCount(); j++) {
            ControlHistory cntrlHist = starsCtrlHist.getControlHistory(j);
            Date stop = new Date(cntrlHist.getStartDateTime().getTime() + cntrlHist.getControlDuration());
            int newDuration = (int)calculateRealControlPeriodTime(cntrlHist.getControlDuration(), cntrlHist.getStartDateTime(), stop, enrollments, optOuts);
            if(newDuration != -1)  {
                cntrlHist.setControlDuration(newDuration);
                /*Calculate summary.  This used to be done using the values straight from the LMControlHistory table on the most recent entry*/
                long controlEnd = cntrlHist.getStartDateTime().getTime() + cntrlHist.getControlDuration();
                if( controlEnd > periodStartDate.getTime()) 
                    recordedControl = recordedControl + newDuration;
            }
        }
        
        return recordedControl;
    }
    
    public static StarsLMControlHistory getSTARSFormattedLMControlHistory(int groupID, Date start, int energyCompanyId) {
        LiteStarsEnergyCompany liteEC = StarsDatabaseCache.getInstance().getEnergyCompany( energyCompanyId );
        Date oneYearAgoDate = LMControlHistoryUtil.getPeriodStartTime( StarsCtrlHistPeriod.PASTYEAR, liteEC.getDefaultTimeZone() );
        Date oneMonthAgoDate = LMControlHistoryUtil.getPeriodStartTime( StarsCtrlHistPeriod.PASTMONTH, liteEC.getDefaultTimeZone() );
        Date oneWeekAgoDate = LMControlHistoryUtil.getPeriodStartTime( StarsCtrlHistPeriod.PASTWEEK, liteEC.getDefaultTimeZone() );
        LiteStarsLMControlHistory liteCtrlHist; 
        StarsCtrlHistPeriod period;
        if(start.getTime() >= oneWeekAgoDate.getTime()) {
            liteCtrlHist = StarsDatabaseCache.getInstance().getLMControlHistory( groupID, oneWeekAgoDate );
            period = StarsCtrlHistPeriod.PASTWEEK;
        }
        else if(start.getTime() >= oneMonthAgoDate.getTime()) {
            liteCtrlHist = StarsDatabaseCache.getInstance().getLMControlHistory( groupID, oneMonthAgoDate );
            period = StarsCtrlHistPeriod.PASTMONTH;
        }
        else if(start.getTime() >= oneYearAgoDate.getTime()) {
            liteCtrlHist = StarsDatabaseCache.getInstance().getLMControlHistory( groupID, oneYearAgoDate );
            period = StarsCtrlHistPeriod.PASTYEAR;
        }
        else {
            liteCtrlHist = StarsDatabaseCache.getInstance().getLMControlHistory( groupID, new Date(0) );
            period = StarsCtrlHistPeriod.ALL;
        }
        return buildStarsControlHistoryForPeriod(liteCtrlHist, period, liteEC.getDefaultTimeZone());
    }
    
    public static StarsLMControlHistory adjustLMControlHistoryByEnrollmentAndOptOutTimes(StarsLMControlHistory unadjustedCtrlHist, Date startDate, List<LMHardwareControlGroup> enrollments, List<LMHardwareControlGroup> optOuts) {
        StarsLMControlHistory adjustedLMControlHistory = new StarsLMControlHistory();
        
        for(int j = 0; j < unadjustedCtrlHist.getControlHistoryCount(); j++) {
            ControlHistory cntrlHist = unadjustedCtrlHist.getControlHistory(j);
            Date stop = new Date(cntrlHist.getStartDateTime().getTime() + cntrlHist.getControlDuration());
            int newDuration = (int)calculateRealControlPeriodTime(cntrlHist.getControlDuration(), cntrlHist.getStartDateTime(), stop, enrollments, optOuts);
            if(newDuration != -1)  {
                cntrlHist.setControlDuration(newDuration);
                /*Calculate summary.  This used to be done using the values straight from the LMControlHistory table on the most recent entry*/
                long controlEnd = cntrlHist.getStartDateTime().getTime() + cntrlHist.getControlDuration();
                if(controlEnd > startDate.getTime())  
                    adjustedLMControlHistory.addControlHistory(cntrlHist);
            }
        }
        
        return adjustedLMControlHistory;
    }
    
    /*
     * This method should be general enough that it can be used for both the LMControlDetail report AND the LMControlSummary report
     */
    public static long[] calculateCumulativeCustomerControlValues(StarsLMControlHistory starsCtrlHist, Date startDate, Date stopDate, List<LMHardwareControlGroup> enrollments, List<LMHardwareControlGroup> optOuts) {
        Validate.notNull(starsCtrlHist);
        Validate.notNull(startDate);
        Validate.notNull(stopDate);
        Validate.notNull(enrollments);
        Validate.notNull(optOuts);
        
        long[] totals = new long[4];
        totals[TOTAL_CONTROL_TIME] = 0;
        totals[TOTAL_OPTOUT_TIME] = 0;
        totals[TOTAL_CONTROL_DURING_OPTOUT_TIME] = 0;
        totals[TOTAL_OPTOUT_EVENTS] = 0;
        
        for(int j = 0; j < starsCtrlHist.getControlHistoryCount(); j++) {
            ControlHistory cntrlHist = starsCtrlHist.getControlHistory(j);
            Date start = cntrlHist.getStartDateTime();
            Date stop = new Date(cntrlHist.getStartDateTime().getTime() + (cntrlHist.getControlDuration() * 1000));
            long newDuration = cntrlHist.getControlDuration();
            long newOptOutControlTime = 0;
            long totalOptOutTime = 0;
            boolean neverEnrolled = true;
            
            for(LMHardwareControlGroup enrollmentEntry : enrollments) {
                //wasn't enrolled at all at the time, at least this entry
                if(enrollmentEntry.getGroupEnrollStart().getTime() > stop.getTime() ||
                        (enrollmentEntry.getGroupEnrollStop() != null && enrollmentEntry.getGroupEnrollStop().getTime() < start.getTime())) {
                    continue;
                }
                else {
                    //period falls cleanly within the enrollment range, total remains the same
                    if(enrollmentEntry.getGroupEnrollStart().getTime() < start.getTime() &&
                            (enrollmentEntry.getGroupEnrollStop() == null || enrollmentEntry.getGroupEnrollStop().getTime() > stop.getTime())) {
                        neverEnrolled = false;
                    }
                    //enrollment started after the beginning of the period, subtract the difference
                    else if(enrollmentEntry.getGroupEnrollStart().getTime() > start.getTime()) { 
                        newDuration = newDuration - (enrollmentEntry.getGroupEnrollStart().getTime() - start.getTime());
                        neverEnrolled = false;
                    }
                    //enrollment stopped before the end of the period, subtract the difference
                    else if(enrollmentEntry.getGroupEnrollStop() != null && enrollmentEntry.getGroupEnrollStop().getTime() < stop.getTime()) {
                        newDuration = newDuration - (stop.getTime() - enrollmentEntry.getGroupEnrollStop().getTime());
                        neverEnrolled = false;
                    }
                }
            }
            
            if(!neverEnrolled)
            {
                long optOutEvent = 0;
                for(LMHardwareControlGroup optOutEntry : optOuts) {
                    //before worrying about control periods, let's calculate the total opt out time
                    //opt out started after our start date
                    Date optOutStart = optOutEntry.getOptOutStart();
					Date optOutStop = optOutEntry.getOptOutStop();
					
					if(optOutStart.getTime() >= startDate.getTime()) {
                        //opt out stopped within our specified range
                        if(optOutStop != null && optOutStop.getTime() < stopDate.getTime()) {
                            totalOptOutTime = totalOptOutTime + (optOutStop.getTime() - optOutStart.getTime());
                            optOutEvent++;
                        }
                        //opt out stopped outside of our specified range or is ongoing
                        else {
                        	// make sure this opt out started inside our report date range
                        	if(optOutStart.getTime() < stopDate.getTime()){
	                            totalOptOutTime = totalOptOutTime + (stopDate.getTime() - optOutStart.getTime());
	                            optOutEvent++;
                        	}
                        }
                    }
                    //opt out started before our specified range but stopped
                    else if(optOutStart.getTime() < startDate.getTime() && optOutStop != null 
                            && optOutStop.getTime() > startDate.getTime()) {
                            totalOptOutTime = totalOptOutTime + (optOutStop.getTime() - startDate.getTime());
                            optOutEvent++;
                    }
                    
                    //if this opt out fits into our specified range, proceed
                    if((optOutStart.getTime() > start.getTime() && optOutStart.getTime() < stop.getTime()) 
                    	|| 
                        (optOutStart.getTime() < start.getTime() && 
                        		(optOutStop == null || optOutStop.getTime() > startDate.getTime()))) {
                    	
                        //period falls entirely within an opt out period.  Discard it.
                        if(optOutStart.getTime() < start.getTime() && (optOutStop == null || 
                        		(optOutStop.getTime() > stop.getTime()))) {
                            newDuration = -1;
                            newOptOutControlTime += (stop.getTime() - start.getTime());
                        }
                        //opt out started during a control period and period stopped during the opt out.  
                        //Subtract the difference of opt out start and the period's stop from duration.
                        else if(optOutStart.getTime() > start.getTime() && optOutStart.getTime() < stop.getTime() && 
                                (optOutStop == null || optOutStop.getTime() > stop.getTime())) {
                            newDuration = newDuration - ((stop.getTime() - optOutStart.getTime())/ 1000);
                            newOptOutControlTime = newOptOutControlTime + (stop.getTime() - optOutStart.getTime());
                        }
                        //opt out ended during a control period that started during the opt out
                        //subtract the difference of opt out stop and period start.
                        else if(optOutStart.getTime() < start.getTime() && optOutStop != null &&
                                optOutStop.getTime() > start.getTime() && optOutStop.getTime() < stop.getTime()) {
                            newDuration = newDuration - ((optOutStop.getTime() - start.getTime())/ 1000);
                            newOptOutControlTime = newOptOutControlTime + (optOutStop.getTime() - start.getTime());
                        }
                        //an opt out occurred cleanly in the middle of a control period
                        //subtract the entire opt out duration from the control history total
                        else if(optOutStart.getTime() > start.getTime() && optOutStop != null &&
                                optOutStop.getTime() < stop.getTime()) {
                            newDuration = newDuration - ((optOutStop.getTime() - optOutStart.getTime()) / 1000);
                            newOptOutControlTime = newOptOutControlTime + (optOutStop.getTime() - optOutStart.getTime());
                        }
                    }
                }
            
                if(totalOptOutTime > 0) 
                    totals[TOTAL_OPTOUT_TIME] = totalOptOutTime;
                
                totals[TOTAL_OPTOUT_EVENTS] = optOutEvent;
                
                if(newDuration > 0)  {
                    long controlEnd = cntrlHist.getStartDateTime().getTime() + (cntrlHist.getControlDuration() * 1000);
                    if( controlEnd >= startDate.getTime() && controlEnd <= stopDate.getTime()) {
                        totals[TOTAL_CONTROL_TIME] = totals[TOTAL_CONTROL_TIME] + newDuration;
                        if(newOptOutControlTime < 0) 
                            newOptOutControlTime = 0;
                        totals[TOTAL_CONTROL_DURING_OPTOUT_TIME] = totals[TOTAL_CONTROL_DURING_OPTOUT_TIME] + newOptOutControlTime;
                    }
                }else if(newOptOutControlTime > -1){
                	if(newOptOutControlTime < 0) 
                        newOptOutControlTime = 0;
                    totals[TOTAL_CONTROL_DURING_OPTOUT_TIME] = totals[TOTAL_CONTROL_DURING_OPTOUT_TIME] + newOptOutControlTime;
                }
            }
        }
                
        return totals;
    }
    
    //-------------------------------------------------------------------------------
}