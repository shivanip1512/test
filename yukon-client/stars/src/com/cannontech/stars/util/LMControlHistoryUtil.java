package com.cannontech.stars.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.cannontech.database.db.pao.LMControlHistory;
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

    public LMControlHistoryUtil() {
    }

    private static void clearTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
    }

    public static Date getPeriodStartTime(StarsCtrlHistPeriod period, TimeZone tz) {
    	Date date = new Date(0);
    	Calendar cal = Calendar.getInstance();
    	cal.setTime( com.cannontech.util.ServletUtil.getToday(tz) );
    	
    	if (period.getType() == StarsCtrlHistPeriod.PASTDAY_TYPE) {
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

    public static com.cannontech.database.db.pao.LMControlHistory[] getLMControlHistory(Integer groupID, StarsCtrlHistPeriod period) {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        java.util.ArrayList ctrlHistList = new java.util.ArrayList();

        try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;

            Date dateFrom = getPeriodStartTime(period, TimeZone.getDefault());

			StringBuffer sql = new StringBuffer("SELECT LMCTRLHISTID");
			for (int i = 0; i < LMControlHistory.SETTER_COLUMNS.length; i++)
				sql.append(", ").append(LMControlHistory.SETTER_COLUMNS[i]);
            sql.append(" FROM ").append(LMControlHistory.TABLE_NAME)
               .append(" WHERE PAOBJECTID = ?");
            if (dateFrom != null)
            	sql.append(" AND STARTDATETIME > ?");
            sql.append(" ORDER BY LMCTRLHISTID");

            pstmt = conn.prepareStatement( sql.toString() );
            pstmt.setInt( 1, groupID.intValue() );
            if (dateFrom != null)
                pstmt.setTimestamp( 2, new java.sql.Timestamp(dateFrom.getTime()) );
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
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if( pstmt != null ) pstmt.close();
                if (conn != null) conn.close();
            }
            catch (Exception e) {
                com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
            }
        }

        com.cannontech.database.db.pao.LMControlHistory[] ctrlHists = new com.cannontech.database.db.pao.LMControlHistory[ ctrlHistList.size() ];
        ctrlHistList.toArray( ctrlHists );

        return ctrlHists;
    }

    public static com.cannontech.database.db.pao.LMControlHistory[] getLMControlHistory(Integer groupID, Integer startLMCtrlHistID) {
		StringBuffer sql = new StringBuffer("SELECT LMCTRLHISTID");
		for (int i = 0; i < LMControlHistory.SETTER_COLUMNS.length; i++)
			sql.append(", ").append(LMControlHistory.SETTER_COLUMNS[i]);
        sql.append(" FROM ").append(LMControlHistory.TABLE_NAME)
           .append(" WHERE PAOBJECTID = ").append(groupID)
		   .append(" AND LMCtrlHistID > ").append(startLMCtrlHistID)
		   .append(" ORDER BY LMCtrlHistID");

		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql.toString(), com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
				
		try {
			stmt.execute();
			com.cannontech.database.db.pao.LMControlHistory[] ctrlHists =
					new com.cannontech.database.db.pao.LMControlHistory[ stmt.getRowCount() ];
			
			for (int i = 0; i < ctrlHists.length; i++) {
				Object[] row = stmt.getRow(i);
				ctrlHists[i] = new com.cannontech.database.db.pao.LMControlHistory();
				
				ctrlHists[i].setLmCtrlHistID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				ctrlHists[i].setPaObjectID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				ctrlHists[i].setStartDateTime( (Date) row[2] );
				ctrlHists[i].setSoeTag( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
				ctrlHists[i].setControlDuration( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
				ctrlHists[i].setControlType( (String) row[5] );
				ctrlHists[i].setCurrentDailyTime( new Integer(((java.math.BigDecimal) row[6]).intValue()) );
				ctrlHists[i].setCurrentMonthlyTime( new Integer(((java.math.BigDecimal) row[7]).intValue()) );
				ctrlHists[i].setCurrentSeasonalTime( new Integer(((java.math.BigDecimal) row[8]).intValue()) );
				ctrlHists[i].setCurrentAnnualTime( new Integer(((java.math.BigDecimal) row[9]).intValue()) );
				ctrlHists[i].setActiveRestore( (String) row[10] );
				ctrlHists[i].setReductionValue( (Double) row[11] );
				ctrlHists[i].setStopDateTime( (Date) row[12] );
			}
			
			return ctrlHists;
		}
		catch (Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		
		return null;
    }

    public static com.cannontech.database.db.pao.LMControlHistory getLastLMControlHistory(Integer groupID) {
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rset = null;

        try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;

		StringBuffer sql = new StringBuffer("SELECT LMCTRLHISTID");
		for (int i = 0; i < LMControlHistory.SETTER_COLUMNS.length; i++)
			sql.append(", ").append(LMControlHistory.SETTER_COLUMNS[i]);
        sql.append(" FROM ").append(LMControlHistory.TABLE_NAME)
           .append(" WHERE LMCTRLHISTID = ")
           .append("(SELECT MAX(LMCTRLHISTID) FROM ").append(LMControlHistory.TABLE_NAME)
           .append(" WHERE PAOBJECTID = ").append(groupID).append(")");

            stmt = conn.createStatement();
            rset = stmt.executeQuery( sql.toString() );

            if (rset.next()) {
                com.cannontech.database.db.pao.LMControlHistory ctrlHist = new com.cannontech.database.db.pao.LMControlHistory();

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

                return ctrlHist;
            }
        }
        catch (Exception e) {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if( stmt != null ) stmt.close();
                if (conn != null) conn.close();
            }
            catch (Exception e) {
                com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
            }
        }

        return null;
    }

}