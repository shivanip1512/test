package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;


public class CapBankOperationsPerformanceModel extends BareDatedReportModelBase<CapBankOperationsPerformanceModel.ModelRow> implements CapControlFilterable {
    
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> areaIds;
    private String queryType;
    private String queryPercent = "100";
    
    public CapBankOperationsPerformanceModel() {
    }
    
    static public class ModelRow {
        public String bankName;
        public String cbcName;
        public String feederName;
        public String subName;
        public String region;
        public String text;
        public String qCount;
        public String totCount;
        public Double qPercent;
    }
    
    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }
    
    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }
    
    public String getTitle() {
        return "CapBank Operations Performance Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        
        Timestamp[] dateRange = {new java.sql.Timestamp(getStartDate().getTime()), new java.sql.Timestamp(getStopDate().getTime()),
                new java.sql.Timestamp(getStartDate().getTime()), new java.sql.Timestamp(getStopDate().getTime()),
                new java.sql.Timestamp(getStartDate().getTime()), new java.sql.Timestamp(getStopDate().getTime())};
        jdbcOps.query(sql.toString(), dateRange, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                CapBankOperationsPerformanceModel.ModelRow row = new CapBankOperationsPerformanceModel.ModelRow();

                row.bankName = rs.getString("bankName");
                row.cbcName = rs.getString("cbcName");
                row.feederName = rs.getString("feederName");
                row.subName = rs.getString("subName");
                row.region = rs.getString("region");
                row.text = rs.getString("text");
                row.qCount = rs.getString("qCount");
                row.totCount = rs.getString("totCount");
                row.qPercent = rs.getDouble("qPercent");
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("select d.bankname, cbcname,  feedername, subname, region, '" + queryType+"' as text, qCount, totCount, qPercent from (select tot.bankname, q.qCount, tot.totCount, cast(q.qCount as float) / cast(tot.totCount as float) * 100 as qPercent from ");
        sql.append("(select bankname, count(*) as totCount from ccoperations_view where operation like '%Sent, %'  and opTime > ? and opTime <= ? group by bankname) as tot ");
        sql.append("left outer join (select bankname, count(*) as qCount from ccoperations_view where ");
        if(queryType.equalsIgnoreCase("Success")) {
            sql.append("(confstatus like '%, Close' or confstatus like '%, Closed' or confstatus like '%, Open') ");
        }else if (queryType.equalsIgnoreCase("Failed")){
           sql.append("confstatus like '%Fail%' ");
        }else if (queryType.equalsIgnoreCase("Questionable")) {
            sql.append("confstatus like '%Questionable%' ");
        }else if (queryType.equalsIgnoreCase("Failed-Questionable")) {
            sql.append("(confstatus like '%Questionable%' or confstatus like '%Failed%') ");
        }
        sql.append("and opTime > ? and opTime <= ? group by bankname) as q on tot.bankname = q.bankname ) as abc ");
        sql.append("left outer join (select yp.paoname, yp.paobjectid, s.text as text from dynamiccccapbank dcb ");
        sql.append("join state s on s.rawstate = dcb.controlstatus and s.stategroupid = 3 ");
        sql.append("join yukonpaobject yp on yp.paobjectid = dcb.capbankid) as status on status.paoname = abc.bankname ");
        sql.append("join (select distinct (bankname), cbcname, feedername, feederid, subname, subbusid, region from ccoperations_view ");
        sql.append("where  operation like '%Sent, %'  and opTime > ? and opTime <= ?) as d on abc.bankname = d.bankname ");
        sql.append("left outer join ccsubareaassignment saa on saa.substationbusid = d.subbusid ");
        sql.append("left outer join (select paobjectid from yukonpaobject where type ='ccarea' ) as ca on ca.paobjectid = saa.areaid ");
        sql.append("where abc.qPercent >= " + queryPercent + " ");
        
        String result = null;
        
        if(capBankIds != null && !capBankIds.isEmpty()) {
            result = "status.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(capBankIds);
            result += wheres;
            result += " ) ";
        }else if(feederIds != null && !feederIds.isEmpty()) {
            result = "d.feederid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }else if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "d.subbusid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }else if(areaIds != null && !areaIds.isEmpty()) {
            result = "ca.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(areaIds);
            result += wheres;
            result += " ) ";
        }
        
        if (result != null) {
            sql.append(" and ");
            sql.append(result);
        }
        
        sql.append(";");
        return sql;
    }

    /* (non-Javadoc)
     * @see com.cannontech.analysis.tablemodel.CapControlFilterable#setCapBankIdsFilter(java.util.Set)
     */
    public void setCapBankIdsFilter(Set<Integer> capBankIds) {
        this.capBankIds = capBankIds;
    }

    /* (non-Javadoc)
     * @see com.cannontech.analysis.tablemodel.CapControlFilterable#setFeederIdsFilter(java.util.Set)
     */
    public void setFeederIdsFilter(Set<Integer> feederIds) {
        this.feederIds = feederIds;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.analysis.tablemodel.CapControlFilterable#setSubbusIdsFilter(java.util.Set)
     */
    public void setSubbusIdsFilter(Set<Integer> subbusIds) {
        this.subbusIds = subbusIds;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.analysis.tablemodel.CapControlFilterable#setAreaIdsFilter(java.util.Set)
     */
    public void setAreaIdsFilter(Set<Integer> areaIds) {
        this.areaIds = areaIds;
    }

    public void setQueryType(String type_) {
        this.queryType = type_;
    }
    
    public void setQueryPercent(String percent_) {
        this.queryPercent = percent_;
    }
}
