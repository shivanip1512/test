package com.cannontech.analysis.tablemodel;

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
    private Set<Integer> areaIds;
    
    static public class ModelRow {
        public String Region;
        public String SubBus;
        public String Feeder;
        public String CapBank;
        public String CBC;
        public String numRetries;
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
   
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    try {
                        CapControlRetriesModel.ModelRow row = new CapControlRetriesModel.ModelRow();
                        row.Region = rs.getString("Region");
                        row.SubBus = rs.getString("SubBus");
                        row.Feeder = rs.getString("Feeder");
                        row.CapBank = rs.getString("CapBank");
                        row.CBC = rs.getString("CBC");
                        row.numRetries = rs.getString("NumRetries");

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
        StringBuffer sql = new StringBuffer ("select yp.paoname as region, yp1.paoname as subbus, yp2.paoname as feeder, yp3.paoname as capbank, yp4.paoname as cbc, el.ct as numRetries ");
        sql.append("from (select pointid, count(*) as ct from cceventlog where text like '%resending%' ");
        sql.append("and datetime > ? ");
        sql.append("and datetime <= ? ");
        sql.append("group by pointid) as el ");
        sql.append("join point p on p.pointid = el.pointid ");
        sql.append("join capbank cb on p.paobjectid = cb.deviceid ");
        sql.append("join yukonpaobject yp4 on yp4.paobjectid = cb.controldeviceid ");
        sql.append("join yukonpaobject yp3 on yp3.paobjectid = cb.deviceid ");
        sql.append("join ccfeederbanklist fb on fb.deviceid = cb.deviceid ");
        sql.append("join yukonpaobject yp2 on yp2.paobjectid = fb.feederid ");
        sql.append("join ccfeedersubassignment fs on fs.feederid = fb.feederid ");
        sql.append("join yukonpaobject yp1 on yp1.paobjectid = fs.substationbusid ");
        sql.append("join ccsubareaassignment sa on sa.substationbusid = fs.substationbusid ");
        sql.append("join  yukonpaobject yp on yp.paobjectid = sa.areaid ");
        sql.append("left outer join (select paobjectid from yukonpaobject where type ='ccarea' ) as ca on ca.paobjectid = sa.areaid ");
        
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
    
    /* (non-Javadoc)
     * @see com.cannontech.analysis.tablemodel.CapControlFilterable#setAreaIdsFilter(java.util.Set)
     */
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
