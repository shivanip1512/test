package com.cannontech.stars.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.Validate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteLMConfiguration;
import com.cannontech.database.data.lite.stars.LiteLMControlHistory;
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
	
	private static Hashtable activeCtrlHist = new Hashtable();
	
	private static boolean isUpToDate(LiteStarsLMControlHistory liteCtrlHist) {
		return (System.currentTimeMillis() - liteCtrlHist.getLastSearchedStopTime()) * 0.001 < LMCtrlHistTimerTask.TIMER_PERIOD * 2;
	}
	
	private static boolean needUpdate(LiteStarsLMControlHistory liteCtrlHist, Date startDate) {
		return (startDate != null) && (!isUpToDate(liteCtrlHist) || StarsUtils.isDateBefore( startDate, new Date(liteCtrlHist.getLastSearchedStartTime()) ));
	}
	
	public synchronized static void addActiveControlHistory(LiteStarsLMControlHistory liteCtrlHist) {
		activeCtrlHist.put( new Integer(liteCtrlHist.getGroupID()), liteCtrlHist );
	}
	
	public synchronized static LiteStarsLMControlHistory getActiveControlHistory(int groupID) {
		return (LiteStarsLMControlHistory)activeCtrlHist.get( new Integer(groupID) );
	}
	
	public synchronized static void clearActiveControlHistory() {
		activeCtrlHist.clear();
	}

	public static Date getPeriodStartTime(StarsCtrlHistPeriod period, TimeZone tz) {
		Date date = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime( com.cannontech.util.ServletUtil.getToday(tz) );
    	
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

	public static com.cannontech.database.db.pao.LMControlHistory[] getLMControlHistory(int groupID, Date dateFrom, Date dateTo) {
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
        
		ArrayList ctrlHistList = new ArrayList();

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
				com.cannontech.database.db.pao.LMControlHistory ctrlHist =
						new com.cannontech.database.db.pao.LMControlHistory();

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
		
		ArrayList groupIDs = new ArrayList();
		
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
			ids[i] = ((Integer) groupIDs.get(i)).intValue();
		return ids;
	}
	
	public static StarsLMControlHistory getStarsLMControlHistory(int groupID, int accountId, Date startDate, TimeZone tz, LiteYukonUser currentUser) {
		StarsLMControlHistory starsCtrlHist = new StarsLMControlHistory();
		
		LiteStarsLMControlHistory liteCtrlHist = getActiveControlHistory( groupID );
		if (liteCtrlHist == null || needUpdate(liteCtrlHist, startDate)) {
			liteCtrlHist = StarsDatabaseCache.getInstance().getLMControlHistory( groupID, startDate );
			addActiveControlHistory( liteCtrlHist );
		}
		
        //New enrollment, opt out, and control history tracking
        //-------------------------------------------------------------------------------
        LMHardwareControlGroupDao lmHardwareControlGroupDao = (LMHardwareControlGroupDao) YukonSpringHook.getBean("lmHardwareControlGroupDao");
        List<LMHardwareControlGroup> enrollments = lmHardwareControlGroupDao.getByLMGroupIdAndAccountIdAndType(groupID, accountId, LMHardwareControlGroup.ENROLLMENT_ENTRY);
        List<LMHardwareControlGroup> optOuts = lmHardwareControlGroupDao.getByLMGroupIdAndAccountIdAndType(groupID, accountId, LMHardwareControlGroup.OPT_OUT_ENTRY);
        //-------------------------------------------------------------------------------
        
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
				if (!StarsUtils.isDateEqual(date, lastStartTime)) {
					// This is a new control
					lastStartTime = date;
					lastStopTime = new Date( lmCtrlHist.getStopDateTime() );
					if (StarsUtils.isDateBefore(date, startDate))
						histStartDate = startDate;
					else
						histStartDate = date;
                	
					hist = new ControlHistory();
					hist.setStartDateTime( histStartDate );
					hist.setControlDuration( 0 );
					starsCtrlHist.addControlHistory( hist );
				}
				else {	// This is the continuation of the last control
					lastStopTime = new Date( lmCtrlHist.getStopDateTime() );
				}
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
				if (StarsUtils.isDateEqual(date, lastStartTime)) {
					lastStopTime = new Date(lmCtrlHist.getStopDateTime());
					if (hist != null)
						hist.setControlDuration( (int)((lmCtrlHist.getStopDateTime() - histStartDate.getTime()) * 0.001) );
				}
				hist = null;
			}
		}
        
        if (lastStopTime != null)
			starsCtrlHist.setBeingControlled( StarsUtils.isDateAfter(lastStopTime, null) );
        
		ControlSummary summary = new ControlSummary();
		LiteLMControlHistory lastCtrlHist = liteCtrlHist.getLastControlHistory();
		
        /*
         * Doing a lot of narrowing conversions here from long to int, but for now, need to keep it
         * up to make sure Yao code stays happy.
         */
        
        if (lastCtrlHist != null) {
			summary.setSeasonalTime( (int)lastCtrlHist.getCurrentSeasonalTime() );
        	
			Date date = getPeriodStartTime( StarsCtrlHistPeriod.PASTYEAR, tz );
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeZone(tz);
            Date now = cal.getTime();
            
			if (lastCtrlHist.getStopDateTime() > date.getTime()) {
				long recordedAnnual = calculateRealControlPeriodTime(lastCtrlHist.getCurrentAnnualTime(), date, now, enrollments, optOuts);
                summary.setAnnualTime( (int)recordedAnnual );
				
                date = getPeriodStartTime( StarsCtrlHistPeriod.PASTSEASON, tz );
                if (lastCtrlHist.getStopDateTime() > date.getTime()) {
                    long recordedSeasonal = calculateRealControlPeriodTime(lastCtrlHist.getCurrentSeasonalTime(), date, now, enrollments, optOuts);
                    summary.setSeasonalTime( (int)recordedSeasonal);
                
    				date = getPeriodStartTime( StarsCtrlHistPeriod.PASTMONTH, tz );
    				if (lastCtrlHist.getStopDateTime() > date.getTime()) {
                        long recordedMonthly = calculateRealControlPeriodTime(lastCtrlHist.getCurrentMonthlyTime(), date, now, enrollments, optOuts);
                        summary.setMonthlyTime( (int)recordedMonthly);
    					
    					date = getPeriodStartTime( StarsCtrlHistPeriod.PASTDAY, tz );
    					if (lastCtrlHist.getStopDateTime() > date.getTime()) {
    						long recordedDaily = calculateRealControlPeriodTime(lastCtrlHist.getCurrentDailyTime(), date, now, enrollments, optOuts);
                            summary.setDailyTime( (int)recordedDaily);
                        }
    				}
                }
			}
		}
		
        StarsLMControlHistory currentStarsLMControlHistory = new StarsLMControlHistory();
        currentStarsLMControlHistory.setControlSummary(summary);
        //New enrollment, opt out, and control history tracking
        //-------------------------------------------------------------------------------
        /*
         * Wait until now to iterate through starsCtrlHist since the totals have already been
         * calculated from all types of ActiveRestore types
         */
        for(int j = 0; j < starsCtrlHist.getControlHistoryCount(); j++) {
            ControlHistory cntrlHist = starsCtrlHist.getControlHistory(j);
            Date stop = new Date(cntrlHist.getStartDateTime().getTime() + cntrlHist.getControlDuration());
            int newDuration = (int)calculateRealControlPeriodTime(cntrlHist.getControlDuration(), cntrlHist.getStartDateTime(), stop, enrollments, optOuts);
            if(newDuration != -1)  {
                cntrlHist.setControlDuration(newDuration);
                currentStarsLMControlHistory.addControlHistory(cntrlHist);
            }
        }
        //-------------------------------------------------------------------------------
		
		return currentStarsLMControlHistory;
	}
	
	public static void updateActiveControlHistory() {
		Hashtable ctrlHistMap = new Hashtable( activeCtrlHist );
		if (ctrlHistMap.size() == 0) return;
		
		long lastSearchedTime = System.currentTimeMillis();
		int startCtrlHistID = Integer.MAX_VALUE;
		
		Iterator it = ctrlHistMap.values().iterator();
		while (it.hasNext()) {
			LiteStarsLMControlHistory liteCtrlHist = (LiteStarsLMControlHistory) it.next();
			
			if (liteCtrlHist.getLastControlHistory() != null &&
				liteCtrlHist.getLastControlHistory().getLmCtrlHistID() < startCtrlHistID)
				startCtrlHistID = liteCtrlHist.getLastControlHistory().getLmCtrlHistID();
			if (isUpToDate(liteCtrlHist) && liteCtrlHist.getLastSearchedCtrlHistID() < startCtrlHistID)
				startCtrlHistID = liteCtrlHist.getLastSearchedCtrlHistID();
		}
		
		com.cannontech.database.db.pao.LMControlHistory[] ctrlHist = getLMControlHistory( startCtrlHistID );
		
		for (int i = 0; i < ctrlHist.length; i++) {
			LiteStarsLMControlHistory liteCtrlHist = (LiteStarsLMControlHistory) ctrlHistMap.get( ctrlHist[i].getPaObjectID() );
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
			LiteStarsLMControlHistory liteCtrlHist = (LiteStarsLMControlHistory) it.next();
			
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
        
        boolean notEnrolled = true;
        
        for(LMHardwareControlGroup enrollmentEntry : enrollments) {
            //wasn't enrolled at all at the time
            if(enrollmentEntry.getGroupEnrollStop().getTime() < periodStart.getTime() || enrollmentEntry.getGroupEnrollStart().getTime() > periodStop.getTime())
                continue;
            //enrollment started after the beginning of the period, subtract the difference
            if(enrollmentEntry.getGroupEnrollStart().getTime() > periodStart.getTime()) 
                controlHistoryTotal = controlHistoryTotal - (enrollmentEntry.getGroupEnrollStart().getTime() - periodStart.getTime());
            //enrollment stopped before the end of the period, subtract the difference
            if(enrollmentEntry.getGroupEnrollStop() != null && enrollmentEntry.getGroupEnrollStop().getTime() < periodStop.getTime())
                controlHistoryTotal = controlHistoryTotal - (periodStop.getTime() - enrollmentEntry.getGroupEnrollStop().getTime());
            notEnrolled = false;
        }
        
        if(notEnrolled)
            return -1;
        
        for(LMHardwareControlGroup optOutEntry : optOuts) {
            //opt falls cleanly in the period of time, subtract total opt out period
            if(optOutEntry.getOptOutStart().getTime() > periodStart.getTime() && optOutEntry.getOptOutStop().getTime() < periodStop.getTime()) {
                controlHistoryTotal = controlHistoryTotal - (optOutEntry.getOptOutStop().getTime() - optOutEntry.getOptOutStart().getTime());
            }
            //opt out started before the period, subtract the applicable opt out period 
            else if(optOutEntry.getOptOutStart().getTime() < periodStart.getTime()) {
                controlHistoryTotal = controlHistoryTotal - (optOutEntry.getOptOutStop().getTime() - periodStart.getTime());
            }
            //opt out keeps going after the period given, subtract the applicable opt out period 
            else if(optOutEntry.getOptOutStop().getTime() > periodStop.getTime()) {
                controlHistoryTotal = controlHistoryTotal - (periodStop.getTime() - optOutEntry.getOptOutStart().getTime());
            }
        }
        
        return controlHistoryTotal;
    }
    //-------------------------------------------------------------------------------
}