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


public class CapControlAssetUnavailabilityModel extends BareDatedReportModelBase<CapControlAssetUnavailabilityModel.ModelRow> implements CapControlFilterable {
    
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    @SuppressWarnings("unused")
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    
    public CapControlAssetUnavailabilityModel() {
    }
    
    static public class ModelRow {
        public String Area;
        public String Substation;
        public String Bus;
        public String Feeder;
        public Integer BusUTC;
        public Integer FeederUTC;
        public Integer BusUTO;
        public Integer FeederUTO;
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
        return "Cap Control Asset Unvailability Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        
        Timestamp[] dateRange = {
                new java.sql.Timestamp(getStartDate().getTime()), new java.sql.Timestamp(getStopDate().getTime()),
                new java.sql.Timestamp(getStartDate().getTime()), new java.sql.Timestamp(getStopDate().getTime()),
                new java.sql.Timestamp(getStartDate().getTime()), new java.sql.Timestamp(getStopDate().getTime()),
                new java.sql.Timestamp(getStartDate().getTime()), new java.sql.Timestamp(getStopDate().getTime())};
        jdbcOps.query(sql.toString(), dateRange, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                CapControlAssetUnavailabilityModel.ModelRow row = new CapControlAssetUnavailabilityModel.ModelRow();
                row.Area = rs.getString("Area");
                row.Substation = rs.getString("Substation");
                row.Bus = rs.getString("Bus");
                row.Feeder = rs.getString("Feeder");
                row.BusUTC = rs.getInt("BusUnableToClose");
                row.FeederUTC = rs.getInt("FeederUnableToClose");
                row.BusUTO = rs.getInt("BusUnableToOpen");
                row.FeederUTO = rs.getInt("FeederUnableToOpen");
                
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer 
                  ("WITH BUTC AS ( ");
        sql.append("    SELECT ");
        sql.append("        COUNT(*) AS BussesUTC, ");
        sql.append("        SubID ");
        sql.append("    FROM CCEventLog ");
        sql.append("    WHERE EventType = 3 ");
        sql.append("    AND DateTime > ? AND DateTime <= ? ");
        sql.append("    AND (Text LIKE '%Cannot Decrease Var%' OR Text LIKE '%Cannot Increase Volt%') ");
        sql.append("    GROUP BY SubID ");
        sql.append("), ");
        sql.append("BUTO AS ( ");
        sql.append("    SELECT ");
        sql.append("        COUNT(*) AS BussesUTO, ");
        sql.append("        SubID ");
        sql.append("    FROM CCEventLog ");
        sql.append("    WHERE EventType = 3 ");
        sql.append("    AND DateTime > ? AND DateTime <= ? ");
        sql.append("    AND (Text LIKE '%Cannot Increase Var%' OR Text LIKE '%Cannot Decrease Volt%') ");
        sql.append("    GROUP BY SubID ");
        sql.append("), ");
        sql.append("FUTC AS ( ");
        sql.append("    SELECT ");
        sql.append("        COUNT(*) AS FeedersUTC, ");
        sql.append("        FeederID ");
        sql.append("    FROM CCEventLog ");
        sql.append("    WHERE EventType = 3 ");
        sql.append("    AND FeederID != 0 ");
        sql.append("    AND DateTime > ? AND DateTime <= ? ");
        sql.append("    AND (Text LIKE '%Cannot Decrease Var%' OR Text LIKE '%Cannot Increase Volt%') ");
        sql.append("    GROUP BY FeederID ");
        sql.append("), ");
        sql.append("FUTO AS ( ");
        sql.append("    SELECT ");
        sql.append("        COUNT(*) AS FeedersUTO, ");
        sql.append("        FeederID ");
        sql.append("    FROM CCEventLog ");
        sql.append("    WHERE EventType = 3 ");
        sql.append("    AND FeederID != 0 ");
        sql.append("    AND DateTime > ? AND DateTime <= ? ");
        sql.append("    AND (Text LIKE '%Cannot Increase Var%' OR Text LIKE '%Cannot Decrease Volt%') ");
        sql.append("    GROUP BY FeederID ");
        sql.append(") ");
        sql.append("SELECT ");
        sql.append("    YPO.PAOName Area, ");
        sql.append("    YP1.PAOName Substation, ");
        sql.append("    YP2.PAOName Bus,");
        sql.append("    YP3.PAOName Feeder, ");
        sql.append("    CASE WHEN BUTC.SubID IS NULL THEN 0 ELSE BUTC.BussesUTC END AS BusUnableToClose, ");
        sql.append("    CASE WHEN FUTC.FeederID IS NULL THEN 0 ELSE FUTC.FeedersUTC END AS FeederUnableToClose, ");
        sql.append("    CASE WHEN BUTO.SubID IS NULL THEN 0 ELSE BUTO.BussesUTO END AS BusUnableToOpen, ");
        sql.append("    CASE WHEN FUTO.feederid IS NULL THEN 0 ELSE FUTO.FeedersUTO END AS FeederUnableToOpen ");
        sql.append("FROM YukonPAObject YPO ");
        sql.append("JOIN CCSUBAREAASSIGNMENT SA ON YPO.PAObjectID = SA.AreaID ");
        sql.append("JOIN CCSUBSTATIONSUBBUSLIST SS ON SA.SubstationBusID = SS.SubStationID ");
        sql.append("JOIN CCFeederSubAssignment FS on SS.SubStationBusID = FS.SubStationBusID ");
        sql.append("JOIN YukonPAObject YP1 ON YP1.PAObjectID = SA.SubstationBusID ");
        sql.append("JOIN YukonPAObject YP2 ON YP2.PAObjectID = SS.SubStationBusID ");
        sql.append("JOIN YukonPAObject YP3 ON YP3.PAObjectID = FS.FeederID ");
        sql.append("LEFT JOIN BUTC ON BUTC.SubID = FS.SubStationBusID ");
        sql.append("LEFT JOIN BUTO ON BUTO.SubID = FS.SubStationBusID ");
        sql.append("LEFT JOIN FUTC ON FUTC.FeederID = FS.FeederID ");
        sql.append("LEFT JOIN FUTO ON FUTO.FeederID = FS.FeederID ");
        sql.append("WHERE (BUTC.BussesUTC IS NOT NULL ");
        sql.append("OR     BUTO.BussesUTO IS NOT NULL ");
        sql.append("OR     FUTC.FeedersUTC IS NOT NULL ");
        sql.append("OR     FUTO.FeedersUTO IS NOT NULL) ");
        
        String result = null;
        
         if(feederIds != null && !feederIds.isEmpty()) {
            result = "YP3.PAObjectID IN ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }else if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "YP2.PAObjectID IN ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }else if(substationIds != null && !substationIds.isEmpty()) {
            result = "YP1.PAObjectID IN ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(substationIds);
            result += wheres;
            result += " ) ";
        }else if(areaIds != null && !areaIds.isEmpty()) {
            result = "YPO.PAObjectID IN ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(areaIds);
            result += wheres;
            result += " ) ";
        }
        
        if (result != null) {
            sql.append(" AND ");
            sql.append(result);
        }

        sql.append("ORDER BY Area, Substation, Bus, Feeder ");
        
        return sql;
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
    
    @Override
    public void setSubstationIdsFilter(Set<Integer> substationIds) {
        this.substationIds = substationIds;
    }
    
	@Override
	public void setStrategyIdsFilter(Set<Integer> strategyIds) {
		//Not used
	}
}

