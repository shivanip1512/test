package com.cannontech.stars.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteLMConfiguration;
import com.cannontech.database.data.lite.stars.LiteLMControlHistory;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMControlHistory;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.pao.LMControlHistory;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.util.model.CustomerControlTotals;
import com.cannontech.stars.util.task.LMCtrlHistTimerTask;
import com.cannontech.stars.xml.serialize.ControlHistory;
import com.cannontech.stars.xml.serialize.ControlSummary;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

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
	    Instant now = new Instant();
	    Instant lastSearchedStopped = new Instant(liteCtrlHist.getLastSearchedStopTime());

	    Duration durationSinceLastSearchStoped = new Duration(lastSearchedStopped, now);
	    Duration ctrlHistTimerPeriodDuration = new Duration(LMCtrlHistTimerTask.TIMER_PERIOD * 2);

	    return durationSinceLastSearchStoped.isShorterThan(ctrlHistTimerPeriodDuration);
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

	/**
	 * This method returns a dateTime object that contains the earliest
	 * possible time for the supplied period and dateTimeZone.
	 */
	public static DateTime getPeriodStartTime(StarsCtrlHistPeriod period, 
	                                            DateTimeZone dateTimeZone) {
		DateTime dateTime = null;

		if (period.getType() == StarsCtrlHistPeriod.ALL_TYPE) {
			dateTime = new DateTime(0);
		} else if (period.getType() == StarsCtrlHistPeriod.PASTDAY_TYPE) {
		    DateMidnight pastDay = new DateMidnight(dateTimeZone);
		    dateTime = pastDay.toDateTime();
		} else if (period.getType() == StarsCtrlHistPeriod.PASTWEEK_TYPE) {
		    DateMidnight pastWeek = new DateMidnight(dateTimeZone).minusWeeks(1);
			dateTime = pastWeek.toDateTime();
		} else if (period.getType() == StarsCtrlHistPeriod.PASTMONTH_TYPE) {
			DateMidnight pastMonth = new DateMidnight(dateTimeZone).minusMonths(1);
			dateTime = pastMonth.toDateTime();
		} else if (period.getType() == StarsCtrlHistPeriod.PASTYEAR_TYPE) {
			DateMidnight pastYear = new DateMidnight(dateTimeZone).minusYears(1);
			dateTime = pastYear.toDateTime();
		}
    	
		return dateTime;
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
				String sql = "SELECT LMGroupID, geo.Address, ss.Address, fdr.Address, zip.Address, usr.Address, pgm.Address, spl.Address, AddressUsage, RelayUsage "
						+ " FROM LMGroupExpresscom, LMGroupExpresscomAddress sp, LMGroupExpresscomAddress geo, LMGroupExpresscomAddress ss, LMGroupExpresscomAddress fdr, " 
						+ " LMGroupExpresscomAddress zip, LMGroupExpresscomAddress usr, LMGroupExpresscomAddress pgm, LMGroupExpresscomAddress spl "
						+ " WHERE SerialNumber = 0 AND ServiceProviderID = sp.AddressID AND sp.Address = " + liteCfg.getExpressCom().getServiceProvider()
						+ " AND GeoID = geo.AddressID AND SubstationID = ss.AddressID AND FeederID = fdr.AddressID " 
						+ " AND ZipID = zip.AddressID AND UserID = usr.AddressID AND ProgramID = pgm.AddressID "
				        + " AND SplinterID = spl.AddressID ";
				
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
	
	
    public static StarsLMControlHistory getStarsLMControlHistory(int groupID, 
                                                                   int inventoryId,
                                                                   int accountId, 
                                                                   StarsCtrlHistPeriod period, 
                                                                   YukonUserContext yukonUserContext) {
        /*
         * Don't forget that startDate is simply based on whether it was an ALL, WEEKLY, 
         * or MONTHLY choice on the UI page.  It isn't a true control period start date of any kind.
         * 
         * Example:  ALL means a startDate value of new Date() Now that the summary is calculated 
         * by looking at all control history entries for a group and account each time we need to do
         * away with passing in this startDate, we need to override this every time with a value of ALL.
         */ 
        DateTime startDate = getPeriodStartTime(period, yukonUserContext.getJodaTimeZone());
        DateTime oneYearAgoDate = getPeriodStartTime(StarsCtrlHistPeriod.PASTYEAR, 
                                                     yukonUserContext.getJodaTimeZone());

        LiteStarsLMControlHistory liteCtrlHist;
        if (period.getType() == StarsCtrlHistPeriod.ALL_TYPE) {
            Instant startInstant = new Instant(0);
            liteCtrlHist = 
                StarsDatabaseCache.getInstance().getLMControlHistory(groupID,
                                                                     startInstant);
        /* Try to help performance a little bit here if we can.  
         * loading a year is better than loading all entries if we don't need to.*/
        } else {
            liteCtrlHist = 
                StarsDatabaseCache.getInstance().getLMControlHistory(groupID, oneYearAgoDate);
        }
        addActiveControlHistory( liteCtrlHist );
        
        //New enrollment, opt out, and control history tracking
        //-------------------------------------------------------------------------------
        LMHardwareControlGroupDao lmHardwareControlGroupDao = 
            (LMHardwareControlGroupDao) YukonSpringHook.getBean("lmHardwareControlGroupDao");
        List<LMHardwareControlGroup> enrollments = 
            lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndAccountIdAndType(
                                          inventoryId, 
                                          groupID, 
                                          accountId, 
                                          LMHardwareControlGroup.ENROLLMENT_ENTRY);
        List<LMHardwareControlGroup> optOuts = 
            lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndAccountIdAndType(
                                          inventoryId, 
                                          groupID, 
                                          accountId, 
                                          LMHardwareControlGroup.OPT_OUT_ENTRY);
        
        StarsLMControlHistory starsCtrlHist = 
            buildStarsControlHistoryForPeriod(liteCtrlHist, period, yukonUserContext.getJodaTimeZone());
        /*
         * Wait until now to iterate through starsCtrlHist since the totals have already been
         * calculated from all types of ActiveRestore types
         */
        StarsLMControlHistory currentStarsLMControlHistory = 
            adjustLMControlHistoryByEnrollmentAndOptOutTimes(starsCtrlHist, 
                                                             startDate, 
                                                             enrollments, 
                                                             optOuts);
        
        ControlSummary summaryForToday = new ControlSummary();
        
        if(period.getType() == StarsCtrlHistPeriod.PASTDAY_TYPE) {
            /* have to do each period separately here, otherwise carry-over control gets missed 
             * since StarsLMControlHistory objects are constructed differently from the original 
             * control history database entries depending on the period.
             * 
             * Example: Control that started at 10 pm yesterday and ran until 7 this morning should
             * show up for today's period as 7 hours of control even though it was 10 hours of total
             * control starting yesterday.
             */
            StarsLMControlHistory periodCtrlHist = 
                buildStarsControlHistoryForPeriod(liteCtrlHist, StarsCtrlHistPeriod.PASTYEAR, 
                                                  yukonUserContext.getJodaTimeZone());
            summaryForToday.setAnnualTime(
                                calculateSummaryControlValueForPeriod(periodCtrlHist, 
                                                                      StarsCtrlHistPeriod.PASTYEAR, 
                                                                      yukonUserContext.getJodaTimeZone(),
                                                                      enrollments));
            
            periodCtrlHist = 
                buildStarsControlHistoryForPeriod(liteCtrlHist, StarsCtrlHistPeriod.PASTMONTH, 
                                                  yukonUserContext.getJodaTimeZone());
            summaryForToday.setMonthlyTime(
                                calculateSummaryControlValueForPeriod(periodCtrlHist, 
                                                                      StarsCtrlHistPeriod.PASTMONTH, 
                                                                      yukonUserContext.getJodaTimeZone(),
                                                                      enrollments));
            
            periodCtrlHist = 
                buildStarsControlHistoryForPeriod(liteCtrlHist, StarsCtrlHistPeriod.PASTDAY, 
                                                  yukonUserContext.getJodaTimeZone());
            summaryForToday.setDailyTime(
                                calculateSummaryControlValueForPeriod(periodCtrlHist, 
                                                                      StarsCtrlHistPeriod.PASTDAY, 
                                                                      yukonUserContext.getJodaTimeZone(),
                                                                      enrollments));
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
    
	/**
     * This method returns the control duration for a given control history event and takes
     * in account opt outs and enrollments during the control history event's time period.
     */
    private static Duration calculateRealControlPeriodTime(Duration controlHistoryTotal, 
                                                             ReadableInstant controlHistoryStartDateTime, 
                                                             ReadableInstant controlHistoryStopDateTime,
                                                             List<LMHardwareControlGroup> enrollments,
                                                             List<LMHardwareControlGroup> optOuts) {
        Validate.notNull(controlHistoryStartDateTime);
        Validate.notNull(controlHistoryStopDateTime);
        Validate.notNull(enrollments);
        Validate.notNull(optOuts);
        
        boolean neverEnrolledDuringThisPeriod = true;
        
        for(LMHardwareControlGroup enrollmentEntry : enrollments) {
            //wasn't enrolled with this entry at the time
            if (enrollmentEntry.getGroupEnrollStart().isAfter(controlHistoryStopDateTime) ||
                (enrollmentEntry.getGroupEnrollStop() != null && 
                 enrollmentEntry.getGroupEnrollStop().isBefore(controlHistoryStartDateTime))) {
                continue;
            
            } else {
                //period falls cleanly within the enrollment range, total remains the same
                if (enrollmentEntry.getGroupEnrollStart().isBefore(controlHistoryStartDateTime) &&
                    (enrollmentEntry.getGroupEnrollStop() == null || 
                     enrollmentEntry.getGroupEnrollStop().isAfter(controlHistoryStopDateTime))) {

                    neverEnrolledDuringThisPeriod = false;
                    
                //enrollment started after the beginning of the period, subtract the difference
                } else if (enrollmentEntry.getGroupEnrollStart().isAfter(controlHistoryStartDateTime)) { 

                    Duration priorEnrollmentDuration = 
                        new Duration(controlHistoryStartDateTime, enrollmentEntry.getGroupEnrollStart());
                    controlHistoryTotal = controlHistoryTotal.minus(priorEnrollmentDuration);

                    neverEnrolledDuringThisPeriod = false;
                }
                
                //enrollment stopped before the end of the period, subtract the difference
                else if(enrollmentEntry.getGroupEnrollStop() != null && 
                         enrollmentEntry.getGroupEnrollStop().isBefore(controlHistoryStopDateTime)) {

                    Duration postEnrollmentDuration =
                        new Duration(enrollmentEntry.getGroupEnrollStop(), controlHistoryStopDateTime);
                    controlHistoryTotal = controlHistoryTotal.minus(postEnrollmentDuration);

                    neverEnrolledDuringThisPeriod = false;
                }

            }
        }
        
        if(neverEnrolledDuringThisPeriod) {
            return Duration.ZERO;
        }

        for (LMHardwareControlGroup optOutEntry : optOuts) {
            // Control occurred entirely during an opt out.  Discard it.
            if (optOutEntry.getOptOutStart().isBefore(controlHistoryStartDateTime) && 
                (optOutEntry.getOptOutStop() == null || 
                 optOutEntry.getOptOutStop().isAfter(controlHistoryStopDateTime))) {
                return Duration.ZERO;
            }

            // An opt out started during control, control stopped before opt out
            // ended.  Subtract the difference of opt out start and the period's
            // stop from duration.
            else if (optOutEntry.getOptOutStart().isAfter(controlHistoryStartDateTime) && 
                      optOutEntry.getOptOutStart().isBefore(controlHistoryStopDateTime) && 
                      (optOutEntry.getOptOutStop() == null || 
                       optOutEntry.getOptOutStop().isAfter(controlHistoryStopDateTime))) {

                Duration priorOptOutDuration =
                    new Duration(optOutEntry.getOptOutStart(), controlHistoryStopDateTime);
                controlHistoryTotal = controlHistoryTotal.minus(priorOptOutDuration);
            }
            
            // Control occurred during an already active opt out.  That opt out
            // then ended before control was complete.  Subtract the difference
            // of opt out stop and period start.
            else if (optOutEntry.getOptOutStart().isBefore(controlHistoryStartDateTime) && 
                      optOutEntry.getOptOutStop() != null && 
                      optOutEntry.getOptOutStop().isAfter(controlHistoryStartDateTime) && 
                      optOutEntry.getOptOutStop().isBefore(controlHistoryStopDateTime)) {
                
                Duration postOptOutDuration = 
                    new Duration(controlHistoryStartDateTime, optOutEntry.getOptOutStop());
                controlHistoryTotal = controlHistoryTotal.minus(postOptOutDuration);
            }
            
            // An opt out occurred completely in the middle of a control period.
            // Subtract the entire opt out duration from the control history
            // total.
            else if (optOutEntry.getOptOutStart().isAfter(controlHistoryStartDateTime) && 
                      optOutEntry.getOptOutStop() != null && 
                      optOutEntry.getOptOutStop().isBefore(controlHistoryStopDateTime)) {
                
                Duration postOptOut =
                    new Duration(optOutEntry.getOptOutStart(), optOutEntry.getOptOutStop());
                controlHistoryTotal = controlHistoryTotal.minus(postOptOut);
            }
        }
        
        if (controlHistoryTotal.isShorterThan(Duration.ZERO)) {
            return Duration.ZERO;
        }
        
        return controlHistoryTotal;
    }
    
    private static StarsLMControlHistory 
         buildStarsControlHistoryForPeriod(LiteStarsLMControlHistory liteCtrlHist, 
                                           StarsCtrlHistPeriod period, 
                                           DateTimeZone dateTimeZone) {

        StarsLMControlHistory starsCtrlHist = new StarsLMControlHistory();
        DateTime periodStartDateTime = getPeriodStartTime( period, dateTimeZone );
        
        ControlHistory hist = null;
        Instant lastStartTime = null;
        Instant lastStopTime = null;
        Instant histStartDate = null;
        
        for (int i = 0; i < liteCtrlHist.getLmControlHistory().size(); i++) {
            LiteLMControlHistory lmCtrlHist = 
                (LiteLMControlHistory) liteCtrlHist.getLmControlHistory().get(i);
            
            
            if (StarsUtils.isReadableInsantBefore(lmCtrlHist.getStopDateInstant(),
                                                  periodStartDateTime)) {
                continue;
            }
                
            Instant lmCtrlHistStart = lmCtrlHist.getStartDateInstant();
            Instant lmCtrlHistStop = lmCtrlHist.getStopDateInstant();
            
            /*
             * ActiveRestore is defined as below:
             * N - This is the first entry for any new control.
             * C - Previous command was repeated extending the current control interval.
             * T - Control terminated based on time set in load group.
             * M - Control terminated because of an active restore or terminate command 
             *     being sent.
             * O - Control terminated because a new command of a different nature was 
             *     sent to this group.
             * L - Time log
             */
            if (lmCtrlHist.getActiveRestore().equals("N")) {
                hist = new ControlHistory();

                // This is a new control
                if (!StarsUtils.isReadableInsantEqual(lmCtrlHistStart, lastStartTime)) {
                    lastStartTime = lmCtrlHistStart;
                    lastStopTime = lmCtrlHistStop;

                    if (StarsUtils.isReadableInsantBefore(lmCtrlHistStart,
                                                          periodStartDateTime)) {
                        histStartDate = periodStartDateTime.toInstant();
                    } else {
                        histStartDate = lmCtrlHistStart;
                    }
                    
                    hist.setStartInstant(histStartDate);
                    hist.setControlDuration(new Duration(0));
                    starsCtrlHist.addControlHistory( hist );

                // This is the continuation of the last control
                } else {  
                    lastStopTime = lmCtrlHistStop;
                }
                hist.setCurrentlyControlling(true);

            } else if (lmCtrlHist.getActiveRestore().equals("C") || 
                        lmCtrlHist.getActiveRestore().equals("L")) {
                
                // This is a new control
                if (hist == null && 
                    StarsUtils.isReadableInsantBefore(lmCtrlHistStart,
                                                      periodStartDateTime)) {
                    
                    lastStartTime = lmCtrlHistStart;
                    lastStopTime = lmCtrlHistStop;

                    histStartDate = periodStartDateTime.toInstant();
                    
                    hist = new ControlHistory();
                    hist.setStartInstant(histStartDate);
                    hist.setControlDuration(new Duration(0));
                    starsCtrlHist.addControlHistory( hist );

                } else if (StarsUtils.isReadableInsantEqual(lastStartTime, lmCtrlHistStart)) {
                    if (hist != null) {
                        Duration controlHistoryDuration = new Duration(histStartDate, lmCtrlHistStop);
                        hist.setControlDuration(controlHistoryDuration);
                    }
                }

            } else if (lmCtrlHist.getActiveRestore().equals("M") || 
                        lmCtrlHist.getActiveRestore().equals("T") || 
                        lmCtrlHist.getActiveRestore().equals("O")) {
                if (hist != null) {
                    hist.setCurrentlyControlling(false);
                }
                if (StarsUtils.isReadableInsantEqual(lastStartTime, lmCtrlHistStart)) {
                    lastStopTime = lmCtrlHistStop;
                    
                    if (hist != null) {
                        Duration controlHistoryDuration = new Duration(histStartDate,lmCtrlHistStop);

                        hist.setControlDuration(controlHistoryDuration);
                    }
                }
                // Remember, this is a reference to what is already in starsCtrlHist's list.  
                // He's just nulling out the reference
                hist = null;
            }
        }
        
        if (lastStopTime != null) {
            starsCtrlHist.setBeingControlled(lastStopTime.isAfter(new DateTime()));
        }

        return starsCtrlHist;
    }
    
    /**
     * This method returns the duration for the supplied period.  This history does not
     * include customer opt outs.  It only shows a summary of the control history for 
     * the program while the user was enrolled.
     */
    private static Duration 
         calculateSummaryControlValueForPeriod(StarsLMControlHistory starsCtrlHist, 
                                               StarsCtrlHistPeriod period, 
                                               DateTimeZone dateTimeZone, 
                                               List<LMHardwareControlGroup> enrollments) {

        DateTime periodStartDate = getPeriodStartTime( period, dateTimeZone );
        Duration recordedControl = Duration.ZERO;
        
        // Iterate over all the controlHistoryEntries and fix the control duration
        for(int j = 0; j < starsCtrlHist.getControlHistoryCount(); j++) {
            ControlHistory cntrlHist = starsCtrlHist.getControlHistory(j);

            Instant stopInstant = 
                cntrlHist.getStartInstant().plus(cntrlHist.getControlDuration());

            ArrayList<LMHardwareControlGroup> emptyOptOutList = Lists.newArrayList();
            Duration newDuration = calculateRealControlPeriodTime(cntrlHist.getControlDuration(), 
                                                                  cntrlHist.getStartInstant(), 
                                                                  stopInstant, 
                                                                  enrollments, 
                                                                  emptyOptOutList);

            
            if(!newDuration.isEqual(Duration.ZERO)) {
                cntrlHist.setControlDuration(newDuration);
                /* Calculate summary.  This used to be done using the values straight 
                 * from the LMControlHistory table on the most recent entry
                 */
                Instant controlEndInstant = 
                    cntrlHist.getStartInstant().plus(newDuration);
                if (controlEndInstant.isAfter(periodStartDate)) {
                    recordedControl = recordedControl.plus(newDuration);
                }
            }
        }
        
        return recordedControl;
    }
    
    /**
     * @param groupID - The load group Id for the control history we would like to see
     * @param startDateTime - The DateTime of the user submitting the request.
     * @param energyCompanyId - The energy company id.
     * @return
     */
    public static StarsLMControlHistory getSTARSFormattedLMControlHistory(int groupID, 
                                                                          ReadableInstant startDateTime, 
                                                                          int energyCompanyId) {
        LiteStarsEnergyCompany liteEC = StarsDatabaseCache.getInstance().getEnergyCompany( energyCompanyId );
        
        DateTime oneYearAgoDate = getPeriodStartTime(StarsCtrlHistPeriod.PASTYEAR,
                                                     liteEC.getDefaultDateTimeZone());
        DateTime oneMonthAgoDate = getPeriodStartTime(StarsCtrlHistPeriod.PASTMONTH,
                                                      liteEC.getDefaultDateTimeZone());
        DateTime oneWeekAgoDate = getPeriodStartTime(StarsCtrlHistPeriod.PASTWEEK,
                                                     liteEC.getDefaultDateTimeZone());
        
        LiteStarsLMControlHistory liteCtrlHist; 
        StarsCtrlHistPeriod period;
        
        if(oneWeekAgoDate.isBefore(startDateTime)) {
            liteCtrlHist = StarsDatabaseCache.getInstance().getLMControlHistory( groupID, oneWeekAgoDate );
            period = StarsCtrlHistPeriod.PASTWEEK;
        } else if(oneMonthAgoDate.isBefore(startDateTime)) {
            liteCtrlHist = StarsDatabaseCache.getInstance().getLMControlHistory( groupID, oneMonthAgoDate );
            period = StarsCtrlHistPeriod.PASTMONTH;
        } else if(oneYearAgoDate.isBefore(startDateTime)) {
            liteCtrlHist = StarsDatabaseCache.getInstance().getLMControlHistory( groupID, oneYearAgoDate );
            period = StarsCtrlHistPeriod.PASTYEAR;
        } else {
            liteCtrlHist = StarsDatabaseCache.getInstance().getLMControlHistory( groupID, new DateTime());
            period = StarsCtrlHistPeriod.ALL;
        }
        
        return buildStarsControlHistoryForPeriod(liteCtrlHist, period, liteEC.getDefaultDateTimeZone());
    }
    
    /**
     * This method goes through the supplied list of control history entries and 
     * fixes the entries to account for optOuts and enrollments.
     */
    public static StarsLMControlHistory 
         adjustLMControlHistoryByEnrollmentAndOptOutTimes(StarsLMControlHistory unadjustedCtrlHist,
                                                          ReadableInstant startInstant,
                                                          List<LMHardwareControlGroup> enrollments,
                                                          List<LMHardwareControlGroup> optOuts) {

        StarsLMControlHistory adjustedLMControlHistory = new StarsLMControlHistory();
        
        for(int j = 0; j < unadjustedCtrlHist.getControlHistoryCount(); j++) {
            ControlHistory cntrlHist = unadjustedCtrlHist.getControlHistory(j);
            
            Instant stopInstant = 
                cntrlHist.getStartInstant().plus(cntrlHist.getControlDuration());
            Duration newDuration = 
                calculateRealControlPeriodTime(cntrlHist.getControlDuration(), 
                                               cntrlHist.getStartInstant(), 
                                               stopInstant, 
                                               enrollments, 
                                               optOuts);

            if (!newDuration.isEqual(Duration.ZERO)) {
                cntrlHist.setControlDuration(newDuration);
                /* Calculate summary.  This used to be done using the values straight from the 
                 * LMControlHistory table on the most recent entry
                 */
                Instant controlEndInstant = 
                    cntrlHist.getStartInstant().plus(cntrlHist.getControlDuration());
                if (controlEndInstant.isAfter(startInstant)) {
                    adjustedLMControlHistory.addControlHistory(cntrlHist);
                }
            }
        }
        
        return adjustedLMControlHistory;
    }
    
    /**
     * This method should be general enough that it can be used for both the LMControlDetail report
     * and the LMControlSummary report
     */
    public static CustomerControlTotals 
         calculateCumulativeCustomerControlValues(StarsLMControlHistory starsCtrlHist, 
                                                  ReadableInstant startDateTime, 
                                                  ReadableInstant stopDateTime, 
                                                  List<LMHardwareControlGroup> enrollments, 
                                                  List<LMHardwareControlGroup> optOuts) {
        Validate.notNull(starsCtrlHist);
        Validate.notNull(startDateTime);
        Validate.notNull(stopDateTime);
        Validate.notNull(enrollments);
        Validate.notNull(optOuts);
        
        CustomerControlTotals totals = new CustomerControlTotals();
        
        for(int j = 0; j < starsCtrlHist.getControlHistoryCount(); j++) {
            ControlHistory cntrlHist = starsCtrlHist.getControlHistory(j);

            Instant ctrlHistStartDateTime = cntrlHist.getStartInstant();
            Instant ctrlHistStopDateTime = 
                cntrlHist.getStartInstant().plus(cntrlHist.getControlDuration());

            Duration newDuration = cntrlHist.getControlDuration();
            Duration newOptOutControlTime = Duration.ZERO;
            Duration totalOptOutTime = Duration.ZERO;
            boolean neverEnrolled = true;
            
            for(LMHardwareControlGroup enrollmentEntry : enrollments) {
                //wasn't enrolled at all at the time, at least this entry
                if (enrollmentEntry.getGroupEnrollStart().isAfter(ctrlHistStopDateTime) ||
                    (enrollmentEntry.getGroupEnrollStop() != null && 
                     enrollmentEntry.getGroupEnrollStop().isBefore(ctrlHistStartDateTime))) {
                    continue;

                } else {
                    //period falls cleanly within the enrollment range, total remains the same
                    if(enrollmentEntry.getGroupEnrollStart().isBefore(ctrlHistStartDateTime) &&
                       (enrollmentEntry.getGroupEnrollStop() == null || 
                        enrollmentEntry.getGroupEnrollStop().isAfter(ctrlHistStopDateTime))) {

                        neverEnrolled = false;

                    //enrollment started after the beginning of the period, subtract the difference
                    } else if (enrollmentEntry.getGroupEnrollStart().isAfter(ctrlHistStartDateTime)) { 
                        Duration priorEnrollmentDuration = 
                            new Duration(ctrlHistStartDateTime, enrollmentEntry.getGroupEnrollStart());
 
                        newDuration = newDuration.minus(priorEnrollmentDuration);
                        neverEnrolled = false;

                    //enrollment stopped before the end of the period, subtract the difference
                    } else if (enrollmentEntry.getGroupEnrollStop() != null && 
                                enrollmentEntry.getGroupEnrollStop().isBefore(ctrlHistStopDateTime)) {
                        Duration postEnrollmentDuration =
                            new Duration(enrollmentEntry.getGroupEnrollStop(), ctrlHistStopDateTime);
 
                        newDuration = newDuration.minus(postEnrollmentDuration);
                        neverEnrolled = false;
                    }
                }
            }
            
            if (!neverEnrolled) {
                int optOutEvent = 0;
                for (LMHardwareControlGroup optOutEntry : optOuts) {
                    //before worrying about control periods, let's calculate the total opt out time
                    //opt out started after our start date
                    ReadableInstant optOutStartInstant = optOutEntry.getOptOutStart();
					ReadableInstant optOutStopInstant = optOutEntry.getOptOutStop();
					
					if (startDateTime.isBefore(optOutStartInstant)) {
                        //opt out stopped within our specified range
                        if (optOutStopInstant != null && optOutStopInstant.isBefore(stopDateTime)) {
                            Duration optOutDuration =
                                new Duration(optOutStartInstant, optOutStopInstant);
                            
                            totalOptOutTime = totalOptOutTime.plus(optOutDuration);
                            optOutEvent++;

                        //opt out stopped outside of our specified range or is ongoing
                        } else {
                        	// make sure this opt out started inside our report date range
                        	if (optOutStartInstant.isBefore(stopDateTime)) {
                        	    Duration optOutDurationDuringControl =
                        	        new Duration(optOutStartInstant, stopDateTime);
                        	    
                        	    totalOptOutTime = totalOptOutTime.plus(optOutDurationDuringControl);
	                            optOutEvent++;
                        	}
                        }
                    }
                    //opt out started before our specified range but stopped
                    else if (optOutStartInstant.isBefore(startDateTime) && 
                              optOutStopInstant != null && 
                              optOutStopInstant.isAfter(startDateTime)) {
                        Duration optOutDurationDuringControl = 
                            new Duration(startDateTime, optOutStopInstant);
                        
                        totalOptOutTime = totalOptOutTime.plus(optOutDurationDuringControl);
                        optOutEvent++;
                    }
                    
                    //if this opt out fits into our specified range, proceed
                    if((optOutStartInstant.isAfter(ctrlHistStartDateTime) && 
                        optOutStartInstant.isBefore(ctrlHistStopDateTime)) || 
                       (optOutStartInstant.isBefore(ctrlHistStartDateTime) && 
                        (optOutStopInstant == null || 
                         optOutStopInstant.isAfter(startDateTime)))) {
                    	
                        //period falls entirely within an opt out period.  Discard it.
                        if (optOutStartInstant.isBefore(ctrlHistStartDateTime) && 
                            (optOutStopInstant == null || 
                        	 optOutStopInstant.isAfter(ctrlHistStopDateTime))) {

                            newDuration = Duration.ZERO;
                            
                            Duration controlDuration =
                                new Duration(ctrlHistStartDateTime, ctrlHistStopDateTime);
                            newOptOutControlTime = newOptOutControlTime.plus(controlDuration);

                        //opt out started during a control period and period stopped during the opt out.  
                        //Subtract the difference of opt out start and the period's stop from duration.
                        } else if (optOutStartInstant.isAfter(ctrlHistStartDateTime) && 
                                    optOutStartInstant.isBefore(ctrlHistStopDateTime) && 
                                    (optOutStopInstant == null || 
                                     optOutStopInstant.isAfter(ctrlHistStopDateTime))) {

                            Duration optOutDurationDuringControl = 
                                new Duration(optOutStartInstant, ctrlHistStopDateTime);
                            
                            newDuration = newDuration.minus(optOutDurationDuringControl);
                            newOptOutControlTime = 
                                newOptOutControlTime.plus(optOutDurationDuringControl);

                        //opt out ended during a control period that started during the opt out
                        //subtract the difference of opt out stop and period start.
                        } else if (optOutStartInstant.isBefore(ctrlHistStartDateTime) && 
                                    optOutStopInstant != null &&
                                    optOutStopInstant.isAfter(ctrlHistStartDateTime) && 
                                    optOutStopInstant.isBefore(ctrlHistStopDateTime)) {
                            
                            Duration optOutDurationDuringControl =
                                new Duration(ctrlHistStartDateTime, optOutStopInstant);
                            
                            newDuration = newDuration.minus(optOutDurationDuringControl);
                            newOptOutControlTime = newOptOutControlTime.plus(optOutDurationDuringControl);

                        //an opt out occurred cleanly in the middle of a control period
                        //subtract the entire opt out duration from the control history total
                        } else if (optOutStartInstant.isAfter(ctrlHistStartDateTime) && 
                                    optOutStopInstant != null &&
                                    optOutStopInstant.isBefore(ctrlHistStopDateTime)) {
                            Duration optOutDuration =
                                new Duration(optOutStartInstant, optOutStopInstant);
                            
                            newDuration = newDuration.minus(optOutDuration);
                            newOptOutControlTime = newOptOutControlTime.plus(optOutDuration);
                        }
                    }
                }
            
                if (totalOptOutTime.isLongerThan(Duration.ZERO)) {
                    totals.setTotalOptOutTime(totalOptOutTime);
                }
                
                totals.setTotalOptOutEvents(optOutEvent);
                
                if (newDuration.isLongerThan(Duration.ZERO)) {
                    Instant controlEndInstant = 
                        cntrlHist.getStartInstant().plus(cntrlHist.getControlDuration());
                    if (startDateTime.isBefore(controlEndInstant) && 
                        stopDateTime.isAfter(controlEndInstant)) {

                        totals.setTotalControlTime(totals.getTotalControlTime().plus(newDuration));
                        if (newOptOutControlTime.isShorterThan(Duration.ZERO)) {
                            newOptOutControlTime = Duration.ZERO;
                        }
                        totals.setTotalControlDuringOptOutTime(
                            totals.getTotalControlDuringOptOutTime().plus(newOptOutControlTime));
                    }
                }
            }
        }
                
        return totals;
    }
    
}