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

public class CapControlUnsolicitedMessagesModel extends BareDatedReportModelBase<CapControlUnsolicitedMessagesModel.ModelRow> implements CapControlFilterable  {
    
    static public class ModelRow {
        public String area;
        public String substation;
        public String subbus;
        public String feeder;
        public String capbank;
        public String cbc;
        public String datetime;
        public String reason;
        public String state;
        public String address;
    }

    
        private List<ModelRow> data = new ArrayList<ModelRow>();
        private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        private Set<Integer> capBankIds;
        private Set<Integer> feederIds;
        private Set<Integer> subbusIds;
        private Set<Integer> substationIds;
        private Set<Integer> areaIds;
        private String orderBy = "area";
        
        public CapControlUnsolicitedMessagesModel() {
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
            return "Cap Control Unsolicited Messages Report";
        }

        public int getRowCount() {
            return data.size();
        }

        public void doLoadData() {
            StringBuffer sql = buildSQLStatement();
            CTILogger.info(sql.toString()); 
            Timestamp[] dateRange = {new java.sql.Timestamp(getStartDate().getTime()), new java.sql.Timestamp(getStopDate().getTime())};
            jdbcOps.query(sql.toString(), dateRange, new RowCallbackHandler() {
                public void processRow(ResultSet rs) throws SQLException {
                    CapControlUnsolicitedMessagesModel.ModelRow row = new CapControlUnsolicitedMessagesModel.ModelRow();
                        row.area = rs.getString("area");
                        row.substation = rs.getString("substation");
                        row.subbus = rs.getString("subbus");
                        row.feeder = rs.getString("feeder");
                        row.capbank = rs.getString("capbank");
                        row.cbc = rs.getString("cbc");
                        row.datetime = rs.getString("datetime");
                        String rc = rs.getString("reason");
                        String reason = rc.substring(rc.indexOf("-")+1, rc.indexOf("!"));
                        row.reason = reason;
                        row.state = rs.getString("state");
                        row.address = rs.getString("ipAddress");
                        data.add(row);
                }
            });
            
            CTILogger.info("Report Records Collected from Database: " + data.size());
        }
        
        public StringBuffer buildSQLStatement() {
            StringBuffer sql = new StringBuffer ("");
            sql.append("select yp.paoname area ");
            sql.append(", yp5.paoname substation ");
            sql.append(", yp1.paoname subbus ");
            sql.append(", yp2.paoname feeder ");
            sql.append(", yp3.paoname capbank ");
            sql.append(", yp4.paoname cbc ");
            sql.append(", el.datetime datetime");
            sql.append(", el.text reason ");
            sql.append(", st.text state ");
            sql.append(", el.additionalinfo ipAddress ");
            sql.append("from (select * from cceventlog ");
            sql.append("where text like '%unsolicited%' ");
            sql.append("and datetime > ? ");
            sql.append("and datetime <= ? ) el ");
            sql.append("join point p on p.pointid = el.pointid ");
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
            sql.append("left outer join (select paobjectid from yukonpaobject where type ='ccarea' ) ca on ca.paobjectid = sa.areaid ");
            sql.append("join state st on st.rawstate = el.value and st.stategroupid = 3 ");
            
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
                result = "yp.paobjectid in ( ";
                String wheres = SqlStatementBuilder.convertToSqlLikeList(areaIds);
                result += wheres;
                result += " ) ";
            }
            
            if (result != null) {
                sql.append(" where ");
                sql.append(result);
            }
            
            if(orderBy.equalsIgnoreCase("Area")) {
                sql.append("order by Area ");
            }else if (orderBy .equalsIgnoreCase("Substation Bus")) {
                sql.append("order by subBus ");
            }else if (orderBy .equalsIgnoreCase("Feeder")) {
                sql.append("order by feeder ");
            }else if (orderBy .equalsIgnoreCase("Cap Bank")) {
                sql.append("order by capbank ");
            }else if (orderBy .equalsIgnoreCase("CBC")) {
                sql.append("order by cbc ");
            }else if (orderBy .equalsIgnoreCase("Date/Time")) {
                sql.append("order by datetime ");
            }else if (orderBy .equalsIgnoreCase("Reason")) {
                sql.append("order by reason ");
            }else if (orderBy .equalsIgnoreCase("State")) {
                sql.append("order by state ");
            }else if (orderBy .equalsIgnoreCase("Ip Address")) {
                sql.append("order by ipAddress ");
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
        
        public void setOrderBy(String orderBy) {
            this.orderBy = orderBy;
        }
        
    }
