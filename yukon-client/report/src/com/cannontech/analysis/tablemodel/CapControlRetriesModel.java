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

public class CapControlRetriesModel extends BareDatedReportModelBase<CapControlRetriesModel.ModelRow> implements CapControlFilterable  {

    private List<ModelRow> data = new ArrayList<ModelRow>();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    
    static public class ModelRow {
        public String Region;
        public String SubBus;
        public String Feeder;
        public String CapBank;
        public String CBC;
        public Integer numRetries;
        public Integer numAttempts;
        public String successPercent;
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
   
                rs = pstmt.executeQuery();
                DecimalFormat twoPlaces = new DecimalFormat("00.00");
                while (rs.next()) {
                    try {
                        CapControlRetriesModel.ModelRow row = new CapControlRetriesModel.ModelRow();
                        row.Region = rs.getString("Region");
                        row.SubBus = rs.getString("SubBus");
                        row.Feeder = rs.getString("Feeder");
                        row.CapBank = rs.getString("CapBank");
                        row.CBC = rs.getString("CBC");
                        row.numRetries = rs.getInt("NumRetries");
                        Integer attempts = rs.getInt("numAttempts");
                        row.numAttempts = attempts;
                        Integer success = rs.getInt("success");
                        double successRate = (success.doubleValue() / attempts.doubleValue()* 100.0);
                        
                        String successString = twoPlaces.format(successRate);
                        successString += "%";
                        row.successPercent = successString;
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
        StringBuffer sql = new StringBuffer ("select yp.paoname region");
        sql.append(", yp1.paoname subbus ");
        sql.append(", yp2.paoname feeder ");
        sql.append(", yp3.paoname capbank ");
        sql.append(", yp4.paoname cbc ");
        sql.append(", el.ct numRetries ");
        sql.append(", el2.ct numAttempts ");
        sql.append(", el3.ct success ");
        sql.append("from (select pointid, count(*) ct from cceventlog ");
        sql.append("where text like '%Resending%' ");
        sql.append("and datetime > ? ");
        sql.append("and datetime <= ? ");
        sql.append("group by pointid) el ");
        sql.append("join point p on p.pointid = el.pointid ");
        sql.append("join (select pointid, count(*) ct from cceventlog ");
        sql.append("where (text like '%Open sent%' or text like '%Close sent%') ");
        sql.append("and datetime > ? ");
        sql.append("and datetime <= ? ");
        sql.append("group by pointid) el2 on el2.pointid = el.pointid ");
        sql.append("join (select pointid, count(*) ct from cceventlog ");
        sql.append("where (text like 'Var:%, Open' or text like 'Var:%, Close' ");
        sql.append("or text like 'Var:%Questionable') ");
        sql.append("and datetime > ? ");
        sql.append("and datetime <= ? ");
        sql.append("group by pointid) el3 on el3.pointid = el.pointid ");
        sql.append("join capbank cb on p.paobjectid = cb.deviceid ");
        sql.append("join yukonpaobject yp4 on yp4.paobjectid = cb.controldeviceid ");
        sql.append("join yukonpaobject yp3 on yp3.paobjectid = cb.deviceid ");
        sql.append("join ccfeederbanklist fb on fb.deviceid = cb.deviceid ");
        sql.append("join yukonpaobject yp2 on yp2.paobjectid = fb.feederid ");
        sql.append("join ccfeedersubassignment fs on fs.feederid = fb.feederid ");
        sql.append("join yukonpaobject yp1 on yp1.paobjectid = fs.substationbusid ");
        sql.append("join ccsubstationsubbuslist ss on ss.substationbusid = fs.substationbusid ");
        sql.append("join yukonpaobject yp5 on yp5.paobjectid = ss.substationid ");
        sql.append("join ccsubstationsubbuslist ssb on ssb.substationbusid = fs.substationbusid ");
        sql.append("join ccsubareaassignment sa on sa.substationbusid = ssb.substationid ");
        sql.append("join yukonpaobject yp on yp.paobjectid = sa.areaid ");
        sql.append("left outer join (select paobjectid from yukonpaobject where type ='CCAREA' ) ca on ca.paobjectid = sa.areaid ");
        
        String result = null;
        
        if(capBankIds != null && !capBankIds.isEmpty()) {
            result = "yp3.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(capBankIds);
            result += wheres;
            result += " ) ";
        }
        if(feederIds != null && !feederIds.isEmpty()) {
            result = "yp2.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }
        if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "yp1.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }
        if(substationIds != null && !substationIds.isEmpty()) {
            result = "yp5.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(substationIds);
            result += wheres;
            result += " ) ";
        }
        if(areaIds != null && !areaIds.isEmpty()) {
            result = "ca.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(areaIds);
            result += wheres;
            result += " ) ";
        }
        
        if (result != null) {
            sql.append(" where ");
            sql.append(result);
        }
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
        return "Cap Control Retries Report";
    }
    
}
