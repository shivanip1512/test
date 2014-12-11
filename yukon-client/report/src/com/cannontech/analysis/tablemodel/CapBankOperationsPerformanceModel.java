package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;


public class CapBankOperationsPerformanceModel extends BareDatedReportModelBase<CapBankOperationsPerformanceModel.ModelRow> implements CapControlFilterable {
    
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    private String queryType;
    private String queryPercent = "100";
    private HashMap<String, AttemptsRow> attemptsMap;
    private HashMap<String, ResultsRow> resultsMap;
    private JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate(); 
    
    public CapBankOperationsPerformanceModel() {
    }
    
    static public class ModelRow {
        public String bankName;
        public String cbcName;
        public String feederName;
        public String subbusName;
        public String region;
        public String text;
        public Integer qCount;
        public Integer totCount;
        public String qPercent;
    }
    
    static public class AttemptsRow {
        public String capBankName;
        public String cbcName;
        public String feederName;
        public String subBusName;
        public String region;
        public Integer totCount;
    }
    
    static public class ResultsRow {
        public String capBankName;
        public String cbcName;
        public String feederName;
        public String subBusName;
        public String region;
        public Integer count;
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
        return "Cap Bank Operations Performance Report";
    }

    public int getRowCount() {
        return data.size();
    }
    
    /**
     * Returns a new HashMap<String, AttemptsRow> data from the ccoperations_view.
     * 
     */
    @SuppressWarnings("unchecked")
    private HashMap<String, AttemptsRow> getAttempts(Timestamp start, Timestamp stop) {
        
        if(attemptsMap == null) {
            attemptsMap = new HashMap<String, AttemptsRow>();
            String attempts = "select CapBankName, count(*) as totCount, CBCName, FeederName, FeederId, SubBusName, SubBusId, SubstationId, Region, AreaId ";
            attempts += "from ccoperations_view "; 
            attempts += "where operation like '%Sent, %' ";
            attempts += "and opTime > ? "; 
            attempts += "and opTime <= ? "; 
            if(getFilters() != null) {
                attempts += getFilters();
            }
            attempts += "group by CapBankName, CBCName, FeederName, FeederId, SubBusName, SubBusId, SubstationId, Region, AreaId";
            
            final RowMapper mapper = new RowMapper() {
                public AttemptsRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AttemptsRow row = new AttemptsRow(); 
                    row.capBankName = rs.getString("CapBankName");
                    row.cbcName = rs.getString("CBCName");
                    row.feederName = rs.getString("FeederName");
                    row.subBusName = rs.getString("SubBusName");
                    row.region = rs.getString("Region");
                    row.totCount = rs.getInt("totCount");
                    return row;
                }
            };
    
            yukonTemplate.query(attempts, new Object[] {start, stop}, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    String key = rs.getString("CBCName")
                        + rs.getString("Region")
                        + rs.getString("SubBusName")
                        + rs.getString("FeederName")
                        + rs.getString("CapBankName");
                    AttemptsRow row = (AttemptsRow) mapper.mapRow(rs, rs.getRow());
                    attemptsMap.put(key, row);
                }
            });
        }
        return attemptsMap;
    }
    
    /**
     * Returns a new HashMap<String, AttemptsRow> data from the ccoperations_view.
     * 
     */
    @SuppressWarnings("unchecked")
    private HashMap<String, ResultsRow> getResults(Timestamp start, Timestamp stop) {
        
        if(resultsMap == null) {
            resultsMap = new HashMap<String, ResultsRow>();
            String resultQuery = "select CapBankName, count(*) as qCount, CBCName, FeederName, FeederId, SubBusName, SubBusId, SubstationId, Region, AreaId ";
            resultQuery += "from ccoperations_view "; 
            resultQuery += "where ";
            
            if(queryType.equalsIgnoreCase("Success")) {
                resultQuery +="(confstatus like '%, Close' or confstatus like '%, Closed' or confstatus like '%, Open') ";
            }else if (queryType.equalsIgnoreCase("Failed")){
                resultQuery +="confstatus like '%Fail%' ";
            }else if (queryType.equalsIgnoreCase("Questionable")) {
                resultQuery +="confstatus like '%Questionable%' ";
            }else if (queryType.equalsIgnoreCase("Failed-Questionable")) {
                resultQuery +="(confstatus like '%Questionable%' or confstatus like '%Failed%') ";
            }
            
            resultQuery += "and opTime > ? "; 
            resultQuery += "and opTime <= ? "; 
            if(getFilters() != null) {
                resultQuery += getFilters();
            }
            resultQuery += "group by CapBankName, CBCName, FeederName, FeederId, SubBusName, SubBusId, SubstationId, Region, AreaId";
            
            final RowMapper mapper = new RowMapper() {
                public ResultsRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ResultsRow row = new ResultsRow(); 
                    row.capBankName = rs.getString("CapBankName");
                    row.cbcName = rs.getString("CBCName");
                    row.feederName = rs.getString("FeederName");
                    row.subBusName = rs.getString("SubBusName");
                    row.region = rs.getString("Region");
                    row.count = rs.getInt("qCount");
                    return row;
                }
            };
    
            yukonTemplate.query(resultQuery, new Object[] {start, stop}, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    String key = rs.getString("CBCName")
                        + rs.getString("Region")
                        + rs.getString("SubBusName")
                        + rs.getString("FeederName")
                        + rs.getString("CapBankName");
                    ResultsRow row = (ResultsRow) mapper.mapRow(rs, rs.getRow());
                    resultsMap.put(key, row);
                }
            });
        }
        return resultsMap;
    }
    
    public void doLoadData() {
        
        for(String currentRow : getAttempts(new Timestamp(getStartDate().getTime()), new Timestamp(getStopDate().getTime())).keySet()) {
            CapBankOperationsPerformanceModel.ModelRow row = new CapBankOperationsPerformanceModel.ModelRow();
            row.bankName = attemptsMap.get(currentRow).capBankName;
            row.cbcName = attemptsMap.get(currentRow).cbcName;
            row.feederName = attemptsMap.get(currentRow).feederName;
            row.subbusName = attemptsMap.get(currentRow).subBusName;
            row.region = attemptsMap.get(currentRow).region;
            row.totCount = attemptsMap.get(currentRow).totCount;
            row.text = queryType;
            ResultsRow resultsRow = getResults(new Timestamp(getStartDate().getTime()), new Timestamp(getStopDate().getTime())).get(currentRow);
            if( resultsRow != null) {
                row.qCount = resultsRow.count;
            }else {
                row.qCount = 0;
            }
            
            double successRate = 0;
            if(row.totCount > 0 ){
                successRate = (row.qCount.doubleValue()  / (row.totCount.doubleValue())* 100.0);
            }
            DecimalFormat twoPlaces = new DecimalFormat("00.00");
            String successString = twoPlaces.format(successRate);
            successString += "%";
            row.qPercent = successString;
            if(successRate >= Integer.parseInt(queryPercent)) {
                data.add(row);
            }
        }
                            
        CTILogger.info("Report Records Collected from Database: " + data.size());
        return;
    }

    private String getFilters() {
        String result = null;

        if (capBankIds != null && !capBankIds.isEmpty()) {
            result = "CapBankId in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(capBankIds);
            result += wheres;
            result += " ) ";
        }
        if (feederIds != null && !feederIds.isEmpty()) {
            result = " FeederId in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }
        if (subbusIds != null && !subbusIds.isEmpty()) {
            result = " SubBusId in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }
        if (substationIds != null && !substationIds.isEmpty()) {
            result = " SubstationId in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(substationIds);
            result += wheres;
            result += " ) ";
        }
        if (areaIds != null && !areaIds.isEmpty()) {
            result = " AreaId in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(areaIds);
            result += wheres;
            result += " ) ";
        }

        if (result != null) {
            result = " and " + result;
        }
        return result;
    }
    
    @Override
    public void setCapBankIdsFilter(Set<Integer> capBankIds) {
        this.capBankIds = capBankIds;
    }
    
    @Override
    public void setFeederIdsFilter(Set<Integer> feederIds) {
        this.feederIds = feederIds;
    }
    
    @Override
    public void setSubbusIdsFilter(Set<Integer> subbusIds) {
        this.subbusIds = subbusIds;
    }
    
    @Override
    public void setAreaIdsFilter(Set<Integer> areaIds) {
        this.areaIds = areaIds;
    }

    public void setQueryType(String type_) {
        this.queryType = type_;
    }
    
    public void setQueryPercent(String percent_) {
        this.queryPercent = percent_;
    }
    
    @Override
    public void setSubstationIdsFilter(Set<Integer> substationIds) {
        this.substationIds = substationIds;
    }
    
	@Override
	public void setStrategyIdsFilter(Set<Integer> strategyIds) {
		//Not used
	}
}
