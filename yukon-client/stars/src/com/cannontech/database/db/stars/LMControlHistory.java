package com.cannontech.database.db.stars;

import java.util.Calendar;
import java.util.Date;

import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class LMControlHistory {
	
	public static final String TABLE_NAME = "LMControlHistory";

    public LMControlHistory() {
    }

    private static void clearTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
    }

    private static Date getPeriodStartTime(StarsCtrlHistPeriod period) {
            Calendar nowCal = Calendar.getInstance();

            if (period.equals( StarsCtrlHistPeriod.PASTDAY )) {
                clearTime(nowCal);
            }
            else if (period.equals( StarsCtrlHistPeriod.PASTWEEK )) {
                nowCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                clearTime(nowCal);
            }
            else if (period.equals( StarsCtrlHistPeriod.PASTMONTH )) {
                nowCal.set(Calendar.DAY_OF_MONTH, 1);
                clearTime(nowCal);
            }
            else if (period.equals( StarsCtrlHistPeriod.PASTYEAR )) {
                nowCal.set(Calendar.MONTH, Calendar.JANUARY);
                nowCal.set(Calendar.DAY_OF_MONTH, 1);
                clearTime(nowCal);
            }
            else if (period.equals( StarsCtrlHistPeriod.ALL )) {
                nowCal = null;
            }

            if (nowCal != null) return nowCal.getTime();
            return null;
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

            Date dateFrom = getPeriodStartTime(period);

			StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM ")
               .append(TABLE_NAME)
               .append(" WHERE PAOBJECTID = ?");
            if (dateFrom != null)
            	sql.append(" AND STARTDATETIME > ?");
            sql.append(" ORDER BY LMCtrlHistID");

            pstmt = conn.prepareStatement( sql.toString() );
            pstmt.setInt( 1, groupID.intValue() );
            if (dateFrom != null)
                pstmt.setTimestamp( 2, new java.sql.Timestamp(dateFrom.getTime()) );
            rset = pstmt.executeQuery();

            while (rset.next()) {
	            com.cannontech.database.db.pao.LMControlHistory ctrlHist =
	            		new com.cannontech.database.db.pao.LMControlHistory();

                ctrlHist.setLmCtrlHistID( new Integer(rset.getInt("LMCTRLHISTID")) );
                ctrlHist.setPaObjectID( new Integer(rset.getInt("PAOBJECTID")) );
                ctrlHist.setStartDateTime( new java.util.Date(rset.getTimestamp("STARTDATETIME").getTime()) );
                ctrlHist.setSoeTag( new Integer(rset.getInt("SOE_TAG")) );
                ctrlHist.setControlDuration( new Integer(rset.getInt("CONTROLDURATION")) );
                ctrlHist.setControlType( rset.getString("CONTROLTYPE") );
                ctrlHist.setCurrentDailyTime( new Integer(rset.getInt("CURRENTDAILYTIME")) );
                ctrlHist.setCurrentMonthlyTime( new Integer(rset.getInt("CURRENTMONTHLYTIME")) );
                ctrlHist.setCurrentSeasonalTime( new Integer(rset.getInt("CURRENTSEASONALTIME")) );
                ctrlHist.setCurrentAnnualTime( new Integer(rset.getInt("CURRENTANNUALTIME")) );
                ctrlHist.setActiveRestore( rset.getString("ACTIVERESTORE") );
                ctrlHist.setReductionValue( new Double(rset.getDouble("REDUCTIONVALUE")) );
                ctrlHist.setStopDateTime( new java.util.Date(rset.getTimestamp("STOPDATETIME").getTime()) );

                ctrlHistList.add( ctrlHist );
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (rset != null) rset.close();
                if( pstmt != null ) pstmt.close();
                if (conn != null) conn.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        com.cannontech.database.db.pao.LMControlHistory[] ctrlHists = new com.cannontech.database.db.pao.LMControlHistory[ ctrlHistList.size() ];
        ctrlHistList.toArray( ctrlHists );

        return ctrlHists;
    }

    public static com.cannontech.database.db.pao.LMControlHistory[] getLMControlHistory(Integer groupID, Integer startLMCtrlHistID) {
		StringBuffer sql = new StringBuffer("SELECT * FROM ")
								.append(com.cannontech.database.db.pao.LMControlHistory.TABLE_NAME)
								.append(" WHERE PAObjectID = ").append(groupID)
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
			e.printStackTrace();
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

            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM ")
               .append(TABLE_NAME)
               .append(" WHERE LMCTRLHISTID IN (")
               .append("SELECT MAX(LMCTRLHISTID) FROM LMCONTROLHISTORY WHERE PAOBJECTID = ")
               .append(groupID)
               .append(")");

            stmt = conn.createStatement();
            rset = stmt.executeQuery( sql.toString() );

            if (rset.next()) {
                com.cannontech.database.db.pao.LMControlHistory ctrlHist = new com.cannontech.database.db.pao.LMControlHistory();

                ctrlHist.setLmCtrlHistID( new Integer(rset.getInt("LMCTRLHISTID")) );
                ctrlHist.setPaObjectID( new Integer(rset.getInt("PAOBJECTID")) );
                ctrlHist.setStartDateTime( new java.util.Date(rset.getTimestamp("STARTDATETIME").getTime()) );
                ctrlHist.setSoeTag( new Integer(rset.getInt("SOE_TAG")) );
                ctrlHist.setControlDuration( new Integer(rset.getInt("CONTROLDURATION")) );
                ctrlHist.setControlType( rset.getString("CONTROLTYPE") );
                ctrlHist.setCurrentDailyTime( new Integer(rset.getInt("CURRENTDAILYTIME")) );
                ctrlHist.setCurrentMonthlyTime( new Integer(rset.getInt("CURRENTMONTHLYTIME")) );
                ctrlHist.setCurrentSeasonalTime( new Integer(rset.getInt("CURRENTSEASONALTIME")) );
                ctrlHist.setCurrentAnnualTime( new Integer(rset.getInt("CURRENTANNUALTIME")) );
                ctrlHist.setActiveRestore( rset.getString("ACTIVERESTORE") );
                ctrlHist.setReductionValue( new Double(rset.getDouble("REDUCTIONVALUE")) );
                ctrlHist.setStopDateTime( new java.util.Date(rset.getTimestamp("STOPDATETIME").getTime()) );

                return ctrlHist;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (rset != null) rset.close();
                if( stmt != null ) stmt.close();
                if (conn != null) conn.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}