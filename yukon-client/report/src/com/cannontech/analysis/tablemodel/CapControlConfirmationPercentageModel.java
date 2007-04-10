package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PoolManager;
import com.cannontech.util.ServletUtil;

public class CapControlConfirmationPercentageModel extends BareReportModelBase<CapControlConfirmationPercentageModel.ModelRow> implements LoadableModel, CapControlFilterable  {

    private List<ModelRow> data = new ArrayList<ModelRow>();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Date startDate = null;
    private Date stopDate = null;
    private TimeZone timeZone = TimeZone.getDefault();
    
    static public class ModelRow {
        public String Region;
        public String OpCenter;
        public String TA;
        public String SubName;
        public String FeederName;
        public String BankName;
        public String CBCName;
        public String Attempts;
        public String Success;
        public String Questionable;
        public String Failure;
        public String SuccessPcnt;
        public String Protocol;
    }
    
    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    public void loadData() {
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString());

        java.sql.Connection conn = null;
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rs = null;

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            if (conn == null) {
                CTILogger.error(getClass() + ":  Error getting database connection.");
                return;
            } else {
                pstmt = conn.prepareStatement(sql.toString());

                pstmt.setTimestamp(1, new java.sql.Timestamp(getStartDate().getTime()));
                pstmt.setTimestamp(2, new java.sql.Timestamp(getStopDate().getTime()));
                pstmt.setTimestamp(3, new java.sql.Timestamp(getStartDate().getTime()));
                pstmt.setTimestamp(4, new java.sql.Timestamp(getStopDate().getTime()));
                pstmt.setTimestamp(5, new java.sql.Timestamp(getStartDate().getTime()));
                pstmt.setTimestamp(6, new java.sql.Timestamp(getStopDate().getTime()));
                pstmt.setTimestamp(7, new java.sql.Timestamp(getStartDate().getTime()));
                pstmt.setTimestamp(8, new java.sql.Timestamp(getStopDate().getTime()));
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    try {
                        CapControlConfirmationPercentageModel.ModelRow row = new CapControlConfirmationPercentageModel.ModelRow();
                        row.Region = rs.getString("Region");
                        row.OpCenter = rs.getString("OpCenter");
                        row.TA = rs.getString("TA");
                        row.SubName = rs.getString("SubName");
                        row.FeederName = rs.getString("FeederName");
                        row.BankName = rs.getString("BankName");
                        row.CBCName = rs.getString("CBCName");
                        row.Attempts = rs.getString("Attempts");
                        row.Success = rs.getString("Success");
                        row.Questionable = rs.getString("Questionable");
                        row.Failure = rs.getString("Failure");
                        row.SuccessPcnt = rs.getString("SuccessPcnt");
                        row.Protocol = rs.getString("Protocol");
                        data.add(row);
                    } catch (java.sql.SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        CTILogger.info("Report Records Collected from Database: " + data.size());
        return;
    }
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("select rs.Region, rs.OpCenter, rs.TA, rs.SubName, rs.FeederName, rs.BankName, RS.CBCName,  s.Attempts, s. Success, s.Questionable, s.Failure, s.SuccessPcnt, rs.Protocol ");
        sql.append("from (select T.CBCName, T.Attempts, isnull(F.Failure,0)Failure, isnull(q.Questionable,0)Questionable, isnull(SS.Success,0)Success, (T.Attempts - isnull(F.Failure,0)) * 100/ T.Attempts SuccessPcnt ");
        sql.append("from (select CBCName, count(*) Attempts from ccoperations_view where Optime  between ? and ? ");
        sql.append("group by CBCName ) as T ");
        sql.append("left outer join (select CBCName, count(*) Failure from ccoperations_view where Optime  between ? and ? ");
        sql.append("and (ConfStatus like '%CloseFail' or  ConfStatus like '%OpenFail') group by CBCName ) as F on T.CBCName = F.CBCName  ");
        sql.append("left outer join (select CBCName, count(*) Questionable from ccoperations_view where Optime  between ? and ? ");
        sql.append("and (ConfStatus like '%OpenQuestionable' or  ConfStatus like '%CloseQuestionable') group by CBCName ) as Q on T.CBCName = Q.CBCName ");
        sql.append("left outer join (select CBCName, count(*) Success from ccoperations_view where Optime  between ? and ? ");
         
        sql.append("and (ConfStatus like '%Closed' or  ConfStatus like '%Open') group by CBCName ) as SS on T.CBCName = SS.CBCName )S ");
        sql.append("inner join (Select Region , OpCenter, TA, SubName , subID, FeederName, FdrId, CBCName, cbcId, bankName, bankID, Protocol from ccinventory_view ) rs on S.CBCName = RS.CBCName  ");
        //sql.append("where ISNULL( rs.Region,0)  = ISNULL(@Region, ISNULL( rs.Region,0)) ");
        
        String result = null;
        
        if(capBankIds != null && !capBankIds.isEmpty()) {
            result = "rs.bankId in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(capBankIds);
            result += wheres;
            result += " ) ";
        }
        if(feederIds != null && !feederIds.isEmpty()) {
            result = "rs.feederId in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }
        if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "rs.subId in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }
        
        if (result != null) {
            sql.append(" where ");
            sql.append(result);
        }
        sql.append(" order by s.CBCName ");
        sql.append(";");
        return sql;
    }

    public void setCapBankIdsFilter(Set<Integer> capBankIds) {
        this.capBankIds = capBankIds;
    }

    public void setFeederIdsFilter(Set<Integer> feederIds) {
        this.feederIds = feederIds;
    }

    public void setSubbusIdsFilter(Set<Integer> subbusIds) {
        this.subbusIds = subbusIds;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getTitle() {
        return "CapControl Confirmation Percentage Report";
    }
    
    public Date getStartDate()
    {
        if (startDate == null)
        {
            startDate = ServletUtil.getYesterday(timeZone);
        }
        return startDate;
    }

    public Date getStopDate()
    {
        if( stopDate == null)
        {
            stopDate = ServletUtil.getTomorrow(timeZone);
        }
        return stopDate;        
    }
    /**
     * Set the startDate
     * @param Date date 
     */
    public void setStartDate(Date startDate_)
    {
        startDate = startDate_;
    }
    /**
     * Set the stopDate
     * @param Date date
     */
    public void setStopDate(Date stopDate_)
    {
        stopDate = stopDate_;
    }

}
