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

public class CapControlConfirmationPercentageModel extends BareDatedReportModelBase<CapControlConfirmationPercentageModel.ModelRow> implements CapControlFilterable  {

    private List<ModelRow> data = new ArrayList<ModelRow>();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    private HashMap<String, RefusalRow> refusalMap;
    private HashMap<String, SuccessRow> successMap;
    private HashMap<String, AttemptsRow> attemptsMap;
    private HashMap<String, FailureRow> failureMap;
    private HashMap<String, QuestionableRow> questionableMap;
    private JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate(); 
    
    static public class ModelRow {
        public String Region;
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
    
    static public class AttemptsRow {
        public String cbcName;
        public Integer attempts;
        public String region;
        public String subbusName;
        public String feederName;
        public String capBankName;
    }
    
    static public class FailureRow {
        public String cbcName;
        public Integer failure;
    }
    
    static public class QuestionableRow {
        public String cbcName;
        public Integer questionable;
    }
    
    static public class SuccessRow {
        public String cbcName;
        public Integer success;
    }
    
    static public class RefusalRow {
        public String cbcName;
        public Integer refusal;
    }
    
    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }
    
    /**
     * Returns a new HashMap<String, AttemptsRow> data from the ccoperations_view.
     * 
     */
    @SuppressWarnings("unchecked")
    private HashMap<String, AttemptsRow> getAttempts(Timestamp start, Timestamp stop) {
        
        if(attemptsMap == null) {
            attemptsMap = new HashMap<String, AttemptsRow>();
            String attempts = "select CBCName, Region, SubBusName, FeederName, CapBankName, count (*) Attempts "; 
            attempts += "from ccoperations_view where Optime between ? and ? ";
            if(getFilters() != null) {
                attempts += getFilters();
            }
            attempts += "group by CBCName, region, subbusname, feedername, capbankname";
            
            final RowMapper mapper = new RowMapper() {
                public AttemptsRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AttemptsRow row = new AttemptsRow(); 
                    row.cbcName = rs.getString("CBCName");
                    row.region = rs.getString("Region");
                    row.subbusName = rs.getString("SubBusName");
                    row.feederName = rs.getString("FeederName");
                    row.capBankName = rs.getString("CapBankName");
                    row.attempts = rs.getInt("Attempts");
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
     * Returns a new HashMap<String, FailureRow> data from the ccoperations_view.
     * 
     */
    @SuppressWarnings("unchecked")
    private HashMap<String, FailureRow> getFailures(Timestamp start, Timestamp stop) {
        
        if(failureMap == null) {
            failureMap = new HashMap<String, FailureRow>();
            String failures = "select CBCName, Region, SubBusName, FeederName, CapBankName, count(*) Failure from ccoperations_view where Optime between ? and ? "; 
            failures += "and (ConfStatus like '%CloseFail' or ConfStatus like '%OpenFail') and ConfStatus not like '%rejected%' ";
            if(getFilters() != null) {
                failures += getFilters();
            }
            failures += "group by CBCName, region, subbusname, feedername, capbankname";
            final RowMapper mapper = new RowMapper() {
                public FailureRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FailureRow row = new FailureRow(); 
                    row.cbcName = rs.getString("CBCName");
                    row.failure = rs.getInt("Failure");
                    return row;                     
                }
            };
    
            yukonTemplate.query(failures, new Object[] {start, stop}, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    String key = rs.getString("CBCName")
                    + rs.getString("Region")
                    + rs.getString("SubBusName")
                    + rs.getString("FeederName")
                    + rs.getString("CapBankName");
                    FailureRow row = (FailureRow) mapper.mapRow(rs, rs.getRow());
                    failureMap.put(key, row);
                }
            });
        }
        return failureMap;
    }
    
    /**
     * Returns a new HashMap<String, QuestionableRow> data from the ccoperations_view.
     * 
     */
    @SuppressWarnings("unchecked")
    private HashMap<String, QuestionableRow> getQuestionable(Timestamp start, Timestamp stop) {
        
        if(questionableMap == null) {
            questionableMap = new HashMap<String, QuestionableRow>();
            String questionable = "select CBCName, Region, SubBusName, FeederName, CapBankName, count(*) Questionable from ccoperations_view where Optime between ? and ? ";
            questionable += "and (ConfStatus like '%OpenQuestionable' or ConfStatus like '%CloseQuestionable') and ConfStatus not like '%rejected%' "; 
            if(getFilters() != null) {
                questionable += getFilters();
            }
            questionable += "group by CBCName, region, subbusname, feedername, capbankname";
            
            final RowMapper mapper = new RowMapper() {
                public QuestionableRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                    QuestionableRow row = new QuestionableRow(); 
                    row.cbcName = rs.getString("CBCName");
                    row.questionable = rs.getInt("Questionable");
                    return row;                     
                }
            };
    
            yukonTemplate.query(questionable, new Object[] {start, stop}, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    String key = rs.getString("CBCName")
                    + rs.getString("Region")
                    + rs.getString("SubBusName")
                    + rs.getString("FeederName")
                    + rs.getString("CapBankName");
                    QuestionableRow row = (QuestionableRow) mapper.mapRow(rs, rs.getRow());
                    questionableMap.put(key, row);
                }
            });
        }
        return questionableMap;
    }
    
    /**
     * Returns a new HashMap<String, SuccessRow> data from the ccoperations_view.
     * 
     */
    @SuppressWarnings("unchecked")
    private HashMap<String, SuccessRow> getSuccess(Timestamp start, Timestamp stop) {
        
        if(successMap == null) {
            successMap = new HashMap<String, SuccessRow>();
            String success = "select CBCName, Region, SubBusName, FeederName, CapBankName, count(*) Success from ccoperations_view where Optime between ? and ? "; 
            success += "and (ConfStatus like '%Closed' or ConfStatus like '%Close' or ConfStatus like '%Open') and ConfStatus not like '%rejected%' "; 
            if(getFilters() != null) {
                success += getFilters();
            }
            success += "group by CBCName, region, subbusname, feedername, capbankname";
            
            final RowMapper mapper = new RowMapper() {
                public SuccessRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SuccessRow row = new SuccessRow(); 
                    row.cbcName = rs.getString("CBCName");
                    row.success = rs.getInt("Success");
                    return row;
                }
            };
    
            yukonTemplate.query(success, new Object[] {start, stop}, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    String key = rs.getString("CBCName")
                    + rs.getString("Region")
                    + rs.getString("SubBusName")
                    + rs.getString("FeederName")
                    + rs.getString("CapBankName");
                    SuccessRow row = (SuccessRow) mapper.mapRow(rs, rs.getRow());
                    successMap.put(key, row);
                }
            });
        }
        return successMap;
    }
    
    /**
     * Returns a new HashMap<String, RefusalRow> data from the ccoperations_view.
     * 
     */
    @SuppressWarnings("unchecked")
    private HashMap<String, RefusalRow> getRefusals(Timestamp start, Timestamp stop) {
        if(refusalMap == null) {
            refusalMap = new HashMap<String, RefusalRow>();
            String refusals = "select CBCName, Region, SubBusName, FeederName, CapBankName, count(*) Refusals from ccoperations_view where Optime between ? and ? "; 
            refusals += "and ConfStatus like '%rejected%' "; 
            if(getFilters() != null) {
                refusals += getFilters();
            }
            refusals += "group by CBCName, region, subbusname, feedername, capbankname";
            
            final RowMapper mapper = new RowMapper() {
                public RefusalRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                    RefusalRow row = new RefusalRow(); 
                    row.cbcName = rs.getString("CBCName");
                    row.refusal = rs.getInt("Refusals");
                    return row;                     
                }
            };
    
            yukonTemplate.query(refusals, new Object[] {start, stop}, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    String key = rs.getString("CBCName")
                    + rs.getString("Region")
                    + rs.getString("SubBusName")
                    + rs.getString("FeederName")
                    + rs.getString("CapBankName");
                    RefusalRow row = (RefusalRow) mapper.mapRow(rs, rs.getRow());
                    refusalMap.put(key, row);
                }
            });
        }
        return refusalMap;
    }

    public void doLoadData() {
        
        for(String currentRow : getAttempts(new Timestamp(getStartDate().getTime()), new Timestamp(getStopDate().getTime())).keySet()) {
            CapControlConfirmationPercentageModel.ModelRow row = new CapControlConfirmationPercentageModel.ModelRow();
            row.Region = attemptsMap.get(currentRow).region;
            row.SubName = attemptsMap.get(currentRow).subbusName;
            row.FeederName = attemptsMap.get(currentRow).feederName;
            row.BankName = attemptsMap.get(currentRow).capBankName;
            row.CBCName = attemptsMap.get(currentRow).cbcName;
            row.Attempts = attemptsMap.get(currentRow).attempts;
            SuccessRow successRow = getSuccess(new Timestamp(getStartDate().getTime()), new Timestamp(getStopDate().getTime())).get(currentRow);
            if( successRow != null) {
                row.Success = successRow.success;
            }else {
                row.Success = 0;
            }
            QuestionableRow questionableRow = getQuestionable(new Timestamp(getStartDate().getTime()), new Timestamp(getStopDate().getTime())).get(currentRow);
            if(questionableRow != null) {
                row.Questionable = questionableRow.questionable;
            }else {
                row.Questionable = 0;
            }
            FailureRow failureRow = getFailures(new Timestamp(getStartDate().getTime()), new Timestamp(getStopDate().getTime())).get(currentRow);
            if(failureRow != null) {
                row.Failure = failureRow.failure;
            }else {
                row.Failure = 0;
            }
            RefusalRow refusalRow = getRefusals(new Timestamp(getStartDate().getTime()), new Timestamp(getStopDate().getTime())).get(currentRow); 
            if(refusalRow != null) {
                row.Refusals = refusalRow.refusal;
            }else {
                row.Refusals = 0;
            }
            double successRate = 0;
            if(row.Attempts.doubleValue() - row.Refusals.doubleValue() > 0) {
                successRate = ((row.Success.doubleValue() + row.Questionable.doubleValue()) / (row.Attempts.doubleValue() - row.Refusals.doubleValue())* 100.0);
            }
            
            DecimalFormat twoPlaces = new DecimalFormat("00.00");
            String successString = twoPlaces.format(successRate);
            successString += "%";
            row.SuccessPcnt = successString;
            data.add(row);
        }
                            
        CTILogger.info("Report Records Collected from Database: " + data.size());
        return;
    }
    
    private String getFilters() {
        String result = null;

        if (capBankIds != null && !capBankIds.isEmpty()) {
            result = "bankId in ( ";
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