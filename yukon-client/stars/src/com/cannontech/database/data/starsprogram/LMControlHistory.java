package com.cannontech.database.data.starsprogram;

import java.util.*;
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

            String sql = "SELECT * FROM LMCONTROLHISTORY WHERE PAOBJECTID = ?";
            if (dateFrom != null) sql += " AND STARTDATETIME > ?";
            sql += " ORDER BY STARTDATETIME";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt( 1, groupID.intValue() );
            if (dateFrom != null)
                pstmt.setTimestamp( 2, new java.sql.Timestamp(dateFrom.getTime()) );
            rset = pstmt.executeQuery();

            boolean newControl = true;
            com.cannontech.database.db.pao.LMControlHistory ctrlHist = null;

            while (rset.next()) {
                String activeRestore = rset.getString("ActiveRestore");

                /*
                 * ActiveRestore is defined as below:
                 * N - This is the first entry for any new control.
                 * C - Previous command was repeated extending the current control interval.
                 * T - Control terminated based on time set in load group.
                 * M - Control terminated because of an active restore or terminate command being sent.
                 * O - Control terminated because a new command of a different nature was sent to this group.
                 */

                if (activeRestore.equals("N")) {
                    newControl = false;
                    ctrlHist = new com.cannontech.database.db.pao.LMControlHistory();
                    ctrlHistList.add( ctrlHist );

                    ctrlHist.setLmCtrlHistID( new Integer(rset.getInt("LMCTRLHISTID")) );
                    ctrlHist.setPaObjectID( new Integer(rset.getInt("PAOBJECTID")) );
                    ctrlHist.setStartDateTime( new java.util.Date(rset.getTimestamp("STARTDATETIME").getTime()) );
                    ctrlHist.setSoeTag( new Integer(rset.getInt("SOE_TAG")) );
                    ctrlHist.setControlType( rset.getString("CONTROLTYPE") );
                    ctrlHist.setActiveRestore( rset.getString("ACTIVERESTORE") );
                    ctrlHist.setReductionValue( new Double(rset.getDouble("REDUCTIONVALUE")) );

                    ctrlHist.setControlDuration( new Integer(rset.getInt("CONTROLDURATION")) );
                    ctrlHist.setCurrentDailyTime( new Integer(rset.getInt("CURRENTDAILYTIME")) );
                    ctrlHist.setCurrentMonthlyTime( new Integer(rset.getInt("CURRENTMONTHLYTIME")) );
                    ctrlHist.setCurrentSeasonalTime( new Integer(rset.getInt("CURRENTSEASONALTIME")) );
                    ctrlHist.setCurrentAnnualTime( new Integer(rset.getInt("CURRENTANNUALTIME")) );
                }
                else if (activeRestore.equals("C")) {
                    if (newControl) {   // activeRestore = 'C' is considered as start of control ONLY in the old server version
                        newControl = false;
                        ctrlHist = new com.cannontech.database.db.pao.LMControlHistory();
                        ctrlHistList.add( ctrlHist );

                        ctrlHist.setLmCtrlHistID( new Integer(rset.getInt("LMCTRLHISTID")) );
                        ctrlHist.setPaObjectID( new Integer(rset.getInt("PAOBJECTID")) );
                        ctrlHist.setStartDateTime( new java.util.Date(rset.getTimestamp("STARTDATETIME").getTime()) );
                        ctrlHist.setSoeTag( new Integer(rset.getInt("SOE_TAG")) );
                        ctrlHist.setControlType( rset.getString("CONTROLTYPE") );
                        ctrlHist.setActiveRestore( rset.getString("ACTIVERESTORE") );
                        ctrlHist.setReductionValue( new Double(rset.getDouble("REDUCTIONVALUE")) );
                    }

                    ctrlHist = (com.cannontech.database.db.pao.LMControlHistory) ctrlHistList.get(ctrlHistList.size() - 1);
                    ctrlHist.setControlDuration( new Integer(rset.getInt("CONTROLDURATION")) );
                    ctrlHist.setCurrentDailyTime( new Integer(rset.getInt("CURRENTDAILYTIME")) );
                    ctrlHist.setCurrentMonthlyTime( new Integer(rset.getInt("CURRENTMONTHLYTIME")) );
                    ctrlHist.setCurrentSeasonalTime( new Integer(rset.getInt("CURRENTSEASONALTIME")) );
                    ctrlHist.setCurrentAnnualTime( new Integer(rset.getInt("CURRENTANNUALTIME")) );
                }
                else if (activeRestore.equals("M") || activeRestore.equals("T")) {
                    if (newControl) {   // 'M' and 'T' can, by itself, be a control period
                        ctrlHist = new com.cannontech.database.db.pao.LMControlHistory();
                        ctrlHistList.add( ctrlHist );

                        ctrlHist.setLmCtrlHistID( new Integer(rset.getInt("LMCTRLHISTID")) );
                        ctrlHist.setPaObjectID( new Integer(rset.getInt("PAOBJECTID")) );
                        ctrlHist.setStartDateTime( new java.util.Date(rset.getTimestamp("STARTDATETIME").getTime()) );
                        ctrlHist.setSoeTag( new Integer(rset.getInt("SOE_TAG")) );
                        ctrlHist.setControlType( rset.getString("CONTROLTYPE") );
                        ctrlHist.setActiveRestore( rset.getString("ACTIVERESTORE") );
                        ctrlHist.setReductionValue( new Double(rset.getDouble("REDUCTIONVALUE")) );
                    }

                    newControl = true;
                    ctrlHist = (com.cannontech.database.db.pao.LMControlHistory) ctrlHistList.get(ctrlHistList.size() - 1);

                    ctrlHist.setControlDuration( new Integer(rset.getInt("CONTROLDURATION")) );
                    ctrlHist.setCurrentDailyTime( new Integer(rset.getInt("CURRENTDAILYTIME")) );
                    ctrlHist.setCurrentMonthlyTime( new Integer(rset.getInt("CURRENTMONTHLYTIME")) );
                    ctrlHist.setCurrentSeasonalTime( new Integer(rset.getInt("CURRENTSEASONALTIME")) );
                    ctrlHist.setCurrentAnnualTime( new Integer(rset.getInt("CURRENTANNUALTIME")) );
                }
/*                else if (!newControl && activeRestore.equals("O")) {
                    ctrlHist = new com.cannontech.database.db.pao.LMControlHistory();
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

                    ctrlHistList.add( ctrlHist );
                }*/
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (conn != null) conn.close();
                if( pstmt != null ) pstmt.close();
                if (rset != null) rset.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        com.cannontech.database.db.pao.LMControlHistory[] ctrlHists = new com.cannontech.database.db.pao.LMControlHistory[ ctrlHistList.size() ];
        ctrlHistList.toArray( ctrlHists );

        Arrays.sort( ctrlHists,
            new Comparator() {
                public int compare(Object o1, Object o2) {
                    com.cannontech.database.db.pao.LMControlHistory ctrlHist1 = (com.cannontech.database.db.pao.LMControlHistory) o1;
                    com.cannontech.database.db.pao.LMControlHistory ctrlHist2 = (com.cannontech.database.db.pao.LMControlHistory) o2;
                    return (ctrlHist2.getStartDateTime().compareTo( ctrlHist1.getStartDateTime() ));
                }
            });

        return ctrlHists;
    }

    public static com.cannontech.database.db.pao.LMControlHistory getLastLMControlHistory(Integer groupID) {
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rset = null;

        try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;

            StringBuffer sql = new StringBuffer(128);
            sql.append("SELECT * FROM LMCONTROLHISTORY WHERE LMCTRLHISTID IN (")
               .append("SELECT MAX(LMCTRLHISTID) FROM LMCONTROLHISTORY WHERE PAOBJECTID = ")
               .append(groupID)
               .append(")");
            System.out.println("*** Execute query: " + sql.toString());

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

                return ctrlHist;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (conn != null) conn.close();
                if( stmt != null ) stmt.close();
                if (rset != null) rset.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}