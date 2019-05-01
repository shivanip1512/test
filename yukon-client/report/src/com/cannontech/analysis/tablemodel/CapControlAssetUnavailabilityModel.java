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
        public String area;
        public String substation;
        public String bus;
        public String feeder;
        public Integer busUtc;
        public Integer feederUtc;
        public Integer busUto;
        public Integer feederUto;
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
        return "Cap Control Asset Unavailability Report";
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
                row.area = rs.getString("Area");
                row.substation = rs.getString("Substation");
                row.bus = rs.getString("Bus");
                row.feeder = rs.getString("Feeder");
                row.busUtc = rs.getInt("BusUnableToClose");
                row.feederUtc = rs.getInt("FeederUnableToClose");
                row.busUto = rs.getInt("BusUnableToOpen");
                row.feederUto = rs.getInt("FeederUnableToOpen");
                
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer 
                  ("WITH Butc_CTE AS ( ");
        sql.append("    SELECT ");
        sql.append("        COUNT(*) AS BussesUtc, ");
        sql.append("        SubID ");
        sql.append("    FROM CCEventLog ");
        sql.append("    WHERE EventType = 3 ");
        sql.append("    AND DateTime > ? AND DateTime <= ? ");
        sql.append("    AND (Text LIKE '%Cannot Decrease Var%' OR Text LIKE '%Cannot Increase Volt%') ");
        sql.append("    GROUP BY SubID ");
        sql.append("), ");
        sql.append("Buto_CTE AS ( ");
        sql.append("    SELECT ");
        sql.append("        COUNT(*) AS BussesUto, ");
        sql.append("        SubID ");
        sql.append("    FROM CCEventLog ");
        sql.append("    WHERE EventType = 3 ");
        sql.append("    AND DateTime > ? AND DateTime <= ? ");
        sql.append("    AND (Text LIKE '%Cannot Increase Var%' OR Text LIKE '%Cannot Decrease Volt%') ");
        sql.append("    GROUP BY SubID ");
        sql.append("), ");
        sql.append("Futc_CTE AS ( ");
        sql.append("    SELECT ");
        sql.append("        COUNT(*) AS FeedersUtc, ");
        sql.append("        FeederID ");
        sql.append("    FROM CCEventLog ");
        sql.append("    WHERE EventType = 3 ");
        sql.append("    AND FeederID != 0 ");
        sql.append("    AND DateTime > ? AND DateTime <= ? ");
        sql.append("    AND (Text LIKE '%Cannot Decrease Var%' OR Text LIKE '%Cannot Increase Volt%') ");
        sql.append("    GROUP BY FeederID ");
        sql.append("), ");
        sql.append("Futo_CTE AS ( ");
        sql.append("    SELECT ");
        sql.append("        COUNT(*) AS FeedersUto, ");
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
        sql.append("    CASE WHEN Butc_CTE.SubID IS NULL THEN 0 ELSE Butc_CTE.BussesUtc END AS BusUnableToClose, ");
        sql.append("    CASE WHEN Futc_CTE.FeederID IS NULL THEN 0 ELSE Futc_CTE.FeedersUtc END AS FeederUnableToClose, ");
        sql.append("    CASE WHEN Buto_CTE.SubID IS NULL THEN 0 ELSE Buto_CTE.BussesUto END AS BusUnableToOpen, ");
        sql.append("    CASE WHEN Futo_CTE.FeederID IS NULL THEN 0 ELSE Futo_CTE.FeedersUto END AS FeederUnableToOpen ");
        sql.append("FROM YukonPAObject YPO ");
        sql.append("JOIN CCSUBAREAASSIGNMENT SA ON YPO.PAObjectID = SA.AreaID ");
        sql.append("JOIN CCSUBSTATIONSUBBUSLIST SS ON SA.SubstationBusID = SS.SubStationID ");
        sql.append("JOIN CCFeederSubAssignment FS on SS.SubStationBusID = FS.SubStationBusID ");
        sql.append("JOIN YukonPAObject YP1 ON YP1.PAObjectID = SA.SubstationBusID ");
        sql.append("JOIN YukonPAObject YP2 ON YP2.PAObjectID = SS.SubStationBusID ");
        sql.append("JOIN YukonPAObject YP3 ON YP3.PAObjectID = FS.FeederID ");
        sql.append("LEFT JOIN Butc_CTE ON Butc_CTE.SubID = FS.SubStationBusID ");
        sql.append("LEFT JOIN Buto_CTE ON Buto_CTE.SubID = FS.SubStationBusID ");
        sql.append("LEFT JOIN Futc_CTE ON Futc_CTE.FeederID = FS.FeederID ");
        sql.append("LEFT JOIN Futo_CTE ON Futo_CTE.FeederID = FS.FeederID ");
        sql.append("WHERE (Butc_CTE.BussesUtc IS NOT NULL ");
        sql.append("OR     Buto_CTE.BussesUto IS NOT NULL ");
        sql.append("OR     Futc_CTE.FeedersUtc IS NOT NULL ");
        sql.append("OR     Futo_CTE.FeedersUto IS NOT NULL) ");
        
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

