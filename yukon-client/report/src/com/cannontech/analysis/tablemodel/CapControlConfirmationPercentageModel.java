package com.cannontech.analysis.tablemodel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;

public class CapControlConfirmationPercentageModel extends BareDatedReportModelBase<CapControlConfirmationPercentageModel.ModelRow> implements CapControlFilterable  {

    private List<ModelRow> data = new ArrayList<ModelRow>();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    
    static public class ModelRow {
        public String Region;
        public String OpCenter;
        public String TA;
        public String SubName;
        public String FeederName;
        public String BankName;
        public String CBCName;
        public Integer Attempts;
        public Integer Success;
        public Integer Questionable;
        public Integer Failure;
        public String SuccessPcnt;
        public Integer Refusals;
    }
    
    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    public void doLoadData() {
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
                pstmt.setTimestamp(9, new java.sql.Timestamp(getStartDate().getTime()));
                pstmt.setTimestamp(10, new java.sql.Timestamp(getStopDate().getTime()));
                rs = pstmt.executeQuery();
                DecimalFormat twoPlaces = new DecimalFormat("00.00");
                while (rs.next()) {
                    try {
                        CapControlConfirmationPercentageModel.ModelRow row = new CapControlConfirmationPercentageModel.ModelRow();
                        row.Region = rs.getString("Region");
                        row.OpCenter = rs.getString("OpCenter");
                        row.TA = rs.getString("TA");
                        row.SubName = rs.getString("SubBusName");
                        row.FeederName = rs.getString("FeederName");
                        row.BankName = rs.getString("capBankName");
                        row.CBCName = rs.getString("CBCName");
                        row.Attempts = rs.getInt("Attempts");
                        row.Success = rs.getInt("Success");
                        row.Questionable = rs.getInt("Questionable");
                        row.Failure = rs.getInt("Failure");
                        row.Refusals = rs.getInt("Refusals");
                        double successRate = (((row.Success.doubleValue() + row.Questionable.doubleValue()) / row.Attempts.doubleValue() - row.Refusals.doubleValue())* 100.0);
                        
                        String successString = twoPlaces.format(successRate);
                        successString += "%";
                        row.SuccessPcnt = successString;
                        
                        data.add(row);
                    } catch (java.sql.SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        } finally {
        	SqlUtils.close(rs, pstmt, conn );
        }
        CTILogger.info("Report Records Collected from Database: " + data.size());
        return;
    }
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("select rs.Region ");
        sql.append(", rs.OpCenter ");
        sql.append(", rs.TA ");
        sql.append(", rs.SubbusName ");
        sql.append(", rs.FeederName ");
        sql.append(", rs.capbankname ");
        sql.append(", RS.CBCName ");
        sql.append(", s.Attempts ");
        sql.append(", s. Success ");
        sql.append(", s.Questionable ");
        sql.append(", s.Failure ");
        sql.append(", S.Refusals ");
        sql.append("from (select T.CBCName ");
        sql.append(", T.Attempts, F.Failure, q.Questionable, SS.Success, RR.Refusals ");
        sql.append("from (select CBCName, count(*) Attempts ");
        sql.append("from ccoperations_view where Optime between ? and ? ");
        sql.append("group by CBCName ) T ");
        sql.append("left outer join (select CBCName, count(*) Failure ");
        sql.append("from ccoperations_view where Optime between ? and ? ");
        sql.append("and (ConfStatus like '%CloseFail' or ConfStatus like '%OpenFail' and ConfStatus not like '%rejected%') ");
        sql.append("group by CBCName ) F on T.CBCName = F.CBCName ");
        sql.append("left outer join (select CBCName, count(*) Questionable ");
        sql.append("from ccoperations_view where Optime between ? and ? ");
        sql.append("and (ConfStatus like '%OpenQuestionable' or ConfStatus like '%CloseQuestionable') and ConfStatus not like '%rejected%' ");
        sql.append("group by CBCName ) Q on T.CBCName = Q.CBCName ");
        sql.append("left outer join (select CBCName, count(*) Refusals ");
        sql.append("from ccoperations_view where Optime between ? and ? ");
        sql.append("and ConfStatus like '%rejected%' ");
        sql.append("group by CBCName ) RR on T.CBCName = RR.CBCName ");
        sql.append("left outer join (select CBCName, count(*) Success ");
        sql.append("from ccoperations_view where Optime between ? and ? ");
        sql.append("and (ConfStatus like '%Close' or ConfStatus like '%Open') and ConfStatus not like '%rejected%' ");
        sql.append("group by CBCName ) SS on T.CBCName = SS.CBCName )S ");
        sql.append("inner join (Select Region, AREAID, OpCenter, TA, SubbusName ");
        sql.append(", subID, subbusId, FeederName, FdrId, CBCName ");
        sql.append(", cbcId, capbankname, bankID ");
        sql.append("from ccinventory_view ) rs on S.CBCName = rs.CBCName ");
        sql.append("left outer join ccsubstationsubbuslist ssb on ssb.substationbusid = rs.subID ");
        sql.append("left outer join ccsubareaassignment saa on saa.substationbusid = ssb.substationid ");
        sql.append("left outer join (select paobjectid ");
        sql.append("from yukonpaobject where type ='CCAREA' ) ca on ca.paobjectid = saa.areaid  ");
        
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
            result = "rs.subbusId in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }
        if(substationIds != null && !substationIds.isEmpty()) {
            result = "rs.subid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(substationIds);
            result += wheres;
            result += " ) ";
        }
        if(areaIds != null && !areaIds.isEmpty()) {
            result = "rs.areaid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(areaIds);
            result += wheres;
            result += " ) ";
        }
        
        if (result != null) {
            sql.append(" where ");
            sql.append(result);
        }
        sql.append(" order by s.CBCName ");
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
    
    public void setSubstationIdsFilter(Set<Integer> substationIds) {
        this.substationIds = substationIds;
    }
    
    public void setAreaIdsFilter(Set<Integer> areaIds) {
        this.areaIds = areaIds;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getTitle() {
        return "Cap Control Confirmation Percentage Report";
    }
    
}
