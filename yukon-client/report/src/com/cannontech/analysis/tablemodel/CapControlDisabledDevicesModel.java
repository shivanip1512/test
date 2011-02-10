package com.cannontech.analysis.tablemodel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class CapControlDisabledDevicesModel extends BareReportModelBase<CapControlDisabledDevicesModel.ModelRow> implements CapControlFilterable {

	// dependencies
    private YukonJdbcTemplate jdbcTemplate;
    
    // inputs
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    private String[] deviceTypes;
    
    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    public CapControlDisabledDevicesModel() {
    }
    
    static public class ModelRow {
    	
    	public ModelRow(String deviceName, String deviceType, String deviceParent, Date dateTime, String user, String comment) {
    		this.deviceName = deviceName;
    		this.deviceType = deviceType;
    		this.deviceParent = deviceParent;
    		this.dateTime = dateTime;
    		this.user = user;
    		this.comment = comment;
    	}
    	
        public String deviceName;
        public String deviceType;
        public String deviceParent;
        public Date dateTime;
        public String user;
        public String comment;
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
        return "Cap Control Disabled Devices Report";
    }

    public int getRowCount() {
        return data.size();
    }

    private List<CapControlDisabledDevicesModel.ModelRow> doLoadAreaData() {
    	final SqlStatementBuilder baseSql = new SqlStatementBuilder();
    	
    	baseSql.append("select yp.paoname devicename, yp.type deviceType, '---' area, '---' substation,");
	  	baseSql.append("'---' subbus, '---' feeder, '---' capbank, c.capcomment, c.commenttime, yu.username");
	  	baseSql.append("from (select * from yukonpaobject where type = 'CCAREA' and disableflag = 'Y') yp");
	  	baseSql.append("left outer join (select paoid, max(commenttime) as commenttime from capcontrolcomment group by paoid) ccc on ccc.PaoID = yp.paobjectid");
	  	baseSql.append("left outer join CAPCONTROLCOMMENT c on c.paoId = yp.paobjectid and c.commenttime = ccc.commenttime and c.action = 'DISABLED'");
	  	baseSql.append("left outer join yukonuser yu on yu.userid = c.userid");
    	              
    	ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
            	SqlStatementBuilder sql = new SqlStatementBuilder();
            	sql.append(baseSql);
            	if(!IterableUtils.isEmpty(areaIds)) {
            		sql.append("where yp.paobjectid").in(subList);
            	}
            	sql.append("order by yp.paoname");
            	
            	return sql;
            }
        };
        
        if(!IterableUtils.isEmpty(areaIds)) {
        	return template.query(sqlGenerator, areaIds, ccDisabledDevicesReportRowMapper);
        } else {
        	SqlStatementBuilder sql = new SqlStatementBuilder();
        	sql.append(baseSql);
            sql.append("order by yp.paoname");
        	return jdbcTemplate.query(sql, ccDisabledDevicesReportRowMapper);
        }
    }
    
    private List<CapControlDisabledDevicesModel.ModelRow> doLoadSubstationData() {
    	final SqlStatementBuilder baseSql = new SqlStatementBuilder();
    	
    	baseSql.append("select yp.paoname devicename, yp.type deviceType, pInfo.area area, '---' substation, '---' subbus, '---' feeder,");
		baseSql.append("'---' capbank, c.capcomment, c.commenttime, yu.username");
		baseSql.append("from (select * from yukonpaobject where type = 'CCSUBSTATION' and disableflag = 'Y') yp");
		baseSql.append("join (select ypa.paoname area, ypa.paobjectid areaId, yps.paobjectid");
  		baseSql.append("from yukonpaobject ypa, yukonpaobject yps, ccsubareaassignment sa, ccsubstationsubbuslist ss");
  		baseSql.append("where yps.paobjectid = ss.substationid and sa.substationbusid = ss.substationid and");
  		baseSql.append("sa.areaid = ypa.paobjectid and yps.type like 'CCSUBSTATION') pInfo");
  		baseSql.append("on yp.paobjectid = pInfo.paobjectid");
  		baseSql.append("left outer join (select paoid, max(commenttime) as commenttime from capcontrolcomment group by paoid) ccc on ccc.PaoID = yp.paobjectid");
  		baseSql.append("left outer join CAPCONTROLCOMMENT c on c.paoId = yp.paobjectid and c.commenttime = ccc.commenttime and c.action = 'DISABLED'");
  		baseSql.append("left outer join yukonuser yu on yu.userid = c.userid");
    	
    	ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
            	SqlStatementBuilder sql = new SqlStatementBuilder();
            	sql.append(baseSql);
                if(!IterableUtils.isEmpty(substationIds)) {
                	sql.append("where yp.paobjectid").in(subList);
                }else if(!IterableUtils.isEmpty(areaIds)) {
                	sql.append("where pInfo.areaId").in(subList);
                }
                sql.append("order by yp.paoname, yp.type");
            	
            	return sql;
            }
        };
        
        if(!IterableUtils.isEmpty(substationIds)) {
        	return template.query(sqlGenerator, substationIds, ccDisabledDevicesReportRowMapper);
        } else if(!IterableUtils.isEmpty(areaIds)) {
        	return template.query(sqlGenerator, areaIds, ccDisabledDevicesReportRowMapper);
        } else {
        	SqlStatementBuilder sql = new SqlStatementBuilder();
        	sql.append(baseSql);
            sql.append("order by yp.paoname, yp.type");
        	return jdbcTemplate.query(sql, ccDisabledDevicesReportRowMapper);
        }
    }
    
    private List<CapControlDisabledDevicesModel.ModelRow> doLoadSubBusData() {
    	final SqlStatementBuilder baseSql = new SqlStatementBuilder();
    	
    	baseSql.append("select yp.paoname devicename, yp.type deviceType, pInfo.area area, pInfo.substation substation, '---' subbus,");
		baseSql.append("'---' feeder, '---' capbank, c.capcomment, c.commenttime, yu.username");
		baseSql.append("from (select * from yukonpaobject where type = 'CCSUBBUS' and disableflag = 'Y') yp");
		baseSql.append("join (select ypa.paoname area, ypa.paobjectid areaId, yps.paoname substation, yps.paobjectId substationId, ypsb.paobjectid");
		baseSql.append("from yukonpaobject ypa, yukonpaobject yps, yukonpaobject ypsb, ccsubareaassignment sa, ccsubstationsubbuslist ss");
		baseSql.append("where ypsb.paobjectid = ss.substationbusid and yps.paobjectid = ss.substationid and sa.substationbusid = ss.substationid and");
		baseSql.append("sa.areaid = ypa.paobjectid and ypsb.type like 'CCSUBBUS') pInfo on yp.paobjectid = pInfo.paobjectid");
		baseSql.append("left outer join (select paoid, max(commenttime) as commenttime from capcontrolcomment group by paoid) ccc on ccc.PaoID = yp.paobjectid");
		baseSql.append("left outer join CAPCONTROLCOMMENT c on c.paoId = yp.paobjectid and c.commenttime = ccc.commenttime and c.action = 'DISABLED'");
		baseSql.append("left outer join yukonuser yu on yu.userid = c.userid");
    	
    	ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
            	SqlStatementBuilder sql = new SqlStatementBuilder();
            	sql.append(baseSql);
            	if(!IterableUtils.isEmpty(subbusIds)) {
                    sql.append("where yp.paobjectid").in(subList);
                }else if(!IterableUtils.isEmpty(substationIds)) {
                	sql.append("where pInfo.substationId").in(subList);
                }else if(!IterableUtils.isEmpty(areaIds)) {
                	sql.append("where pInfo.areaId").in(subList);
                }
                sql.append("order by yp.paoname, yp.type");
            	
            	return sql;
            }
        };
        
        if(!IterableUtils.isEmpty(subbusIds)) {
            return template.query(sqlGenerator, subbusIds, ccDisabledDevicesReportRowMapper);
        } else if(!IterableUtils.isEmpty(substationIds)) {
        	return template.query(sqlGenerator, substationIds, ccDisabledDevicesReportRowMapper);
        } else if(!IterableUtils.isEmpty(areaIds)) {
        	return template.query(sqlGenerator, areaIds, ccDisabledDevicesReportRowMapper);
        } else {
        	SqlStatementBuilder sql = new SqlStatementBuilder();
        	sql.append(baseSql);
            sql.append("order by yp.paoname, yp.type");
        	return jdbcTemplate.query(sql, ccDisabledDevicesReportRowMapper);
        }
    }
    
    private List<CapControlDisabledDevicesModel.ModelRow> doLoadFeederData() {
    	final SqlStatementBuilder baseSql = new SqlStatementBuilder();
    	
    	baseSql.append("select yp.paoname devicename, yp.type deviceType, pInfo.area area, pInfo.substation substation, pInfo.subbus subbus,");
    	baseSql.append("'---' feeder, '---' capbank, c.capcomment, c.commenttime, yu.username");
    	baseSql.append("from (select * from yukonpaobject where type = 'CCFEEDER' and disableflag = 'Y') yp");
    	baseSql.append("join (select ypa.paoname area, ypa.paobjectid areaId, yps.paoname substation, yps.paobjectId substationId, ypsb.paoname subbus,");
		baseSql.append("ypsb.paobjectid subbusId, ypf.paobjectid");
		baseSql.append("from yukonpaobject ypa, yukonpaobject yps, yukonpaobject ypsb, yukonpaobject ypf, ccsubareaassignment sa, ccsubstationsubbuslist ss,");
		baseSql.append("ccfeedersubassignment fs");
		baseSql.append("where ypf.paobjectid = fs.feederid and ypsb.paobjectid = fs.substationbusid and yps.paobjectid = ss.substationid and");
		baseSql.append("fs.substationbusid = ss.substationbusid and sa.substationbusid = ss.substationid and sa.areaid = ypa.paobjectid and");
		baseSql.append("ypf.type like 'CCFEEDER') pInfo");
	    baseSql.append("on yp.paobjectid = pInfo.paobjectid");
	    baseSql.append("left outer join (select paoid, max(commenttime) as commenttime from capcontrolcomment group by paoid) ccc on ccc.PaoID = yp.paobjectid");
	    baseSql.append("left outer join CAPCONTROLCOMMENT c on c.paoId = yp.paobjectid and c.commenttime = ccc.commenttime and c.action = 'DISABLED'");
	    baseSql.append("left outer join yukonuser yu on yu.userid = c.userid");

    	ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
            	SqlStatementBuilder sql = new SqlStatementBuilder();
            	sql.append(baseSql);
            	if(!IterableUtils.isEmpty(feederIds)) {
                    sql.append("where yp.paobjectid").in(subList);
                }else if(!IterableUtils.isEmpty(subbusIds)) {
                	sql.append("where pInfo.subbusId").in(subList);
                }else if(!IterableUtils.isEmpty(substationIds)) {
                	sql.append("where pInfo.substationId").in(subList);
                }else if(!IterableUtils.isEmpty(areaIds)) {
                	sql.append("where pInfo.areaId").in(subList);
                }
                sql.append("order by yp.paoname, yp.type");
            	
            	return sql;
            }
        };

        if(!IterableUtils.isEmpty(feederIds)) {
        	return template.query(sqlGenerator, feederIds, ccDisabledDevicesReportRowMapper);
    	} else if(!IterableUtils.isEmpty(subbusIds)) {
            return template.query(sqlGenerator, subbusIds, ccDisabledDevicesReportRowMapper);
        } else if(!IterableUtils.isEmpty(substationIds)) {
        	return template.query(sqlGenerator, substationIds, ccDisabledDevicesReportRowMapper);
        } else if(!IterableUtils.isEmpty(areaIds)) {
        	return template.query(sqlGenerator, areaIds, ccDisabledDevicesReportRowMapper);
        } else {
        	SqlStatementBuilder sql = new SqlStatementBuilder();
        	sql.append(baseSql);
            sql.append("order by yp.paoname, yp.type");
        	return jdbcTemplate.query(sql, ccDisabledDevicesReportRowMapper);
        }
    }
    	
    private List<CapControlDisabledDevicesModel.ModelRow> doLoadCapBankData() {
    	final SqlStatementBuilder baseSql = new SqlStatementBuilder();
    	
    	baseSql.append("select yp.paoname devicename, yp.type deviceType, pInfo.area area, pInfo.substation substation, pInfo.subbus subbus,");
		baseSql.append("pInfo.feeder feeder, '---' capbank, c.capcomment, c.commenttime, yu.username");
		baseSql.append("from (select * from yukonpaobject where type = 'CAP BANK' and disableflag = 'Y') yp");
		baseSql.append("join (select ypa.paoname area, ypa.PAObjectID areaId, yps.paoname substation, yps.paobjectId substationId, ypsb.paoname subbus,");
		baseSql.append("ypsb.PAObjectID subbusId, ypf.paoname feeder, ypf.paobjectid feederId, ypc.paobjectid");
		baseSql.append("from yukonpaobject ypa, yukonpaobject yps, yukonpaobject ypsb, yukonpaobject ypf, yukonpaobject ypc, ccsubareaassignment sa,");
		baseSql.append("ccsubstationsubbuslist ss, ccfeedersubassignment fs, ccfeederbanklist fb, capbank c");
		baseSql.append("where ypc.paobjectid = c.deviceid and fb.deviceid = c.deviceid and fb.feederid = fs.feederid and ypf.paobjectid = fb.feederid and");
		baseSql.append("ypsb.paobjectid = fs.substationbusid and yps.paobjectid = ss.substationid and fs.substationbusid = ss.substationbusid and");
		baseSql.append("sa.substationbusid = ss.substationid and sa.areaid = ypa.paobjectid and ypc.type like 'CAP BANK') pInfo");
		baseSql.append("on pInfo.paobjectid = yp.paobjectid");
		baseSql.append("left outer join (select paoid, max(commenttime) as commenttime from capcontrolcomment group by paoid) ccc on ccc.PaoID = yp.paobjectid");
		baseSql.append("left outer join CAPCONTROLCOMMENT c on c.paoId = yp.paobjectid and c.commenttime = ccc.commenttime and c.action = 'DISABLED'");
		baseSql.append("left outer join yukonuser yu on yu.userid = c.userid");
    	
    	ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
            	SqlStatementBuilder sql = new SqlStatementBuilder();
            	sql.append(baseSql);
            	if(!IterableUtils.isEmpty(capBankIds)) {
                    sql.append("where yp.paobjectid").in(subList);
                }else if(!IterableUtils.isEmpty(feederIds)) {
                	sql.append("where pInfo.feederId").in(subList);
                }else if(!IterableUtils.isEmpty(subbusIds)) {
                	sql.append("where pInfo.subbusId").in(subList);
                }else if(!IterableUtils.isEmpty(substationIds)) {
                	sql.append("where pInfo.substationId").in(subList);
                }else if(!IterableUtils.isEmpty(areaIds)) {
                	sql.append("where pInfo.areaId").in(subList);
                }
                sql.append("order by yp.paoname, yp.type");

            	return sql;
            }
        };
        
        if(!IterableUtils.isEmpty(capBankIds)) {
        	return template.query(sqlGenerator, capBankIds, ccDisabledDevicesReportRowMapper);
        } else if(!IterableUtils.isEmpty(feederIds)) {
        	return template.query(sqlGenerator, feederIds, ccDisabledDevicesReportRowMapper);
    	} else if(!IterableUtils.isEmpty(subbusIds)) {
            return template.query(sqlGenerator, subbusIds, ccDisabledDevicesReportRowMapper);
        } else if(!IterableUtils.isEmpty(substationIds)) {
        	return template.query(sqlGenerator, substationIds, ccDisabledDevicesReportRowMapper);
        } else if(!IterableUtils.isEmpty(areaIds)) {
        	return template.query(sqlGenerator, areaIds, ccDisabledDevicesReportRowMapper);
        } else {
        	SqlStatementBuilder sql = new SqlStatementBuilder();
        	sql.append(baseSql);
            sql.append("order by yp.paoname, yp.type");
        	return jdbcTemplate.query(sql, ccDisabledDevicesReportRowMapper);
        }
    }

    private List<CapControlDisabledDevicesModel.ModelRow> doLoadCbcData() {
    	final SqlStatementBuilder baseSql = new SqlStatementBuilder();
    	
    	baseSql.append("select yp.paoname devicename, yp.type deviceType, pInfo.area area, pInfo.substation substation,");
		baseSql.append("pInfo.subbus subbus, pInfo.feeder feeder, pInfo.capbank capbank, c.capcomment,");
		baseSql.append("c.commenttime, yu.username");
		baseSql.append("from (select * from yukonpaobject where type like 'CBC%' and disableflag = 'Y') yp");
		baseSql.append("join (select ypa.paoname area, ypa.PAObjectID areaId, yps.paoname substation,");
		baseSql.append("yps.PAObjectID substationId, ypsb.paobjectid subbusId, ypf.paobjectid feederId,");
		baseSql.append("ypc.paobjectid capbankId, ypsb.paoname subbus, ypf.paoname feeder, ypc.paoname capbank,");
		baseSql.append("yp.paobjectid");
		baseSql.append("from yukonpaobject yp, yukonpaobject ypa, yukonpaobject yps,");
		baseSql.append("yukonpaobject ypsb, yukonpaobject ypf, yukonpaobject ypc, ccsubareaassignment sa,");
		baseSql.append("ccsubstationsubbuslist ss, ccfeedersubassignment fs, ccfeederbanklist fb, capbank c");
		baseSql.append("where yp.paobjectid = c.controldeviceid and ypc.paobjectid = c.deviceid and");
		baseSql.append("fb.deviceid = c.deviceid and fb.feederid = fs.feederid and ypf.paobjectid = fb.feederid");
		baseSql.append("and ypsb.paobjectid = fs.substationbusid and yps.paobjectid = ss.substationid");
		baseSql.append("and fs.substationbusid = ss.substationbusid and sa.substationbusid = ss.substationid");
		baseSql.append("and sa.areaid = ypa.paobjectid and yp.type like 'CBC%') pInfo");
		baseSql.append("on yp.paobjectid = pInfo.paobjectid");
	    baseSql.append("left outer join (select paoid, max(commenttime) as commenttime from capcontrolcomment group by paoid) ccc on ccc.PaoID = yp.paobjectid");
	  	baseSql.append("left outer join CAPCONTROLCOMMENT c on c.paoId = yp.paobjectid and c.commenttime = ccc.commenttime and c.action = 'DISABLED'");
	  	baseSql.append("left outer join yukonuser yu on yu.userid = c.userid");
    	
    	ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
            	SqlStatementBuilder sql = new SqlStatementBuilder();
            	sql.append(baseSql);
            	if(!IterableUtils.isEmpty(capBankIds)) {
        			sql.append("where pInfo.capbankId").in(subList);
                }else if(!IterableUtils.isEmpty(feederIds)) {
                	sql.append("where pInfo.feederId").in(subList);
                }else if(!IterableUtils.isEmpty(subbusIds)) {
                	sql.append("where pInfo.subbusId").in(subList);
                }else if(!IterableUtils.isEmpty(substationIds)) {
                	sql.append("where pInfo.substationId").in(subList);
                }else if(!IterableUtils.isEmpty(areaIds)) {
                	sql.append("where pInfo.areaId").in(subList);
                }
                sql.append("order by yp.paoname, yp.type");
            	
            	return sql;
            }
        };
        
        if(!IterableUtils.isEmpty(capBankIds)) {
        	return template.query(sqlGenerator, capBankIds, ccDisabledDevicesReportRowMapper);
        } else if(!IterableUtils.isEmpty(feederIds)) {
        	return template.query(sqlGenerator, feederIds, ccDisabledDevicesReportRowMapper);
    	} else if(!IterableUtils.isEmpty(subbusIds)) {
            return template.query(sqlGenerator, subbusIds, ccDisabledDevicesReportRowMapper);
        } else if(!IterableUtils.isEmpty(substationIds)) {
        	return template.query(sqlGenerator, substationIds, ccDisabledDevicesReportRowMapper);
        } else if(!IterableUtils.isEmpty(areaIds)) {
        	return template.query(sqlGenerator, areaIds, ccDisabledDevicesReportRowMapper);
        } else {
        	SqlStatementBuilder sql = new SqlStatementBuilder();
        	sql.append(baseSql);
            sql.append("order by yp.paoname, yp.type");
        	return jdbcTemplate.query(sql, ccDisabledDevicesReportRowMapper);
        }
    }
    
    public void doLoadData() {
        List<String> deviceTypeList = Arrays.asList(deviceTypes);
        for(String type: deviceTypeList) {
        	if(type.equalsIgnoreCase("CBC")) {
        		data.addAll(doLoadCbcData());
        	} else if (type.equalsIgnoreCase("Cap Bank")) {
        		data.addAll(doLoadCapBankData());
        	} else if (type.equalsIgnoreCase("Feeder")) {
        		data.addAll(doLoadFeederData());
        	} else if (type.equalsIgnoreCase("Sub Bus")) {
        		data.addAll(doLoadSubBusData());
        	} else if (type.equalsIgnoreCase("Substation")) {
        		data.addAll(doLoadSubstationData());
        	} else if (type.equalsIgnoreCase("Area")) {
        		data.addAll(doLoadAreaData());
        	}
        }
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    // rowMapper
    private final YukonRowMapper<CapControlDisabledDevicesModel.ModelRow> ccDisabledDevicesReportRowMapper =
        new YukonRowMapper<CapControlDisabledDevicesModel.ModelRow>() {
        @Override
        public CapControlDisabledDevicesModel.ModelRow mapRow(YukonResultSet rs) throws SQLException {
        	
            String deviceName = rs.getString("deviceName");
            String deviceType = rs.getString("deviceType");
            String parent = "";
            
            if(deviceType.equalsIgnoreCase("CCAREA")) {
                parent = "---";
            }else if(deviceType.equalsIgnoreCase("CCSUBSTATION")) {
                parent = rs.getString("area");
            }else if(deviceType.equalsIgnoreCase("CCSUBBUS")) {
                parent = rs.getString("area");
                parent += "->";
                parent += rs.getString("substation");
            }else if(deviceType.equalsIgnoreCase("CCFEEDER")) {
                parent = rs.getString("area");
                parent += "->";
                parent += rs.getString("substation");
                parent += "->";
                parent += rs.getString("subbus");
            }else if(deviceType.equalsIgnoreCase("CAP BANK")) {
                parent = rs.getString("area");
                parent += "->";
                parent += rs.getString("substation");
                parent += "->";
                parent += rs.getString("subbus");
                parent += "->";
                parent += rs.getString("feeder");
            }else if(deviceType.startsWith("CBC")) {
                parent = rs.getString("area");
                parent += "->";
                parent += rs.getString("substation");
                parent += "->";
                parent += rs.getString("subbus");
                parent += "->";
                parent += rs.getString("feeder");
                parent += "->";
                parent += rs.getString("capbank");
            }
            String deviceParent = parent;
            
            Date dateTime = rs.getDate("commenttime"); 
            
            String user = rs.getString("username");
            if(user == null || user.length() < 1) {
                user = "---";
            }
            
            String comment = rs.getString("capcomment");
            if(comment == null || comment.length() < 1) {
                comment = "---";
            }
            
            return new CapControlDisabledDevicesModel.ModelRow(deviceName, deviceType, deviceParent, dateTime, user, comment);
        }
    };
    
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
    public void setSubstationIdsFilter(Set<Integer> substationIds) {
        this.substationIds = substationIds;
    }
    
    @Override
    public void setAreaIdsFilter(Set<Integer> areaIds) {
        this.areaIds = areaIds;
    }
    
    @Autowired
    public void setJdbcOps(YukonJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public void setDeviceTypes(String[] deviceTypes) {
        this.deviceTypes = deviceTypes;
    }
    
	@Override
	public void setStrategyIdsFilter(Set<Integer> strategyIds) {
		//Not used
	}
}
