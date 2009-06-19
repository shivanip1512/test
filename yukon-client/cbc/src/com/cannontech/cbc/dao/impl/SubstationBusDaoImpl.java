package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cbc.dao.SubstationBusDao;
import com.cannontech.cbc.model.LiteCapControlObject;
import com.cannontech.cbc.model.Substation;
import com.cannontech.cbc.model.SubstationBus;
import com.cannontech.cbc.point.CBCPointFactory;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.HolidayScheduleDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.util.Validator;

public class SubstationBusDaoImpl implements SubstationBusDao {
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    
    private static final ParameterizedRowMapper<SubstationBus> rowMapper;
    private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private PaoDao paoDao;
    private SeasonScheduleDao seasonScheduleDao;
    private HolidayScheduleDao holidayScheduleDao;
    
    static {
            insertSql = "INSERT INTO CAPCONTROLSUBSTATIONBUS (SubstationBusID,CurrentVarLoadPointID," +
            " CurrentWattLoadPointID,MapLocationID,CurrentVoltLoadPointID,AltSubID,SwitchPointID," + 
            "DualBusEnabled,MultiMonitorControl,usephasedata,phaseb,phasec,ControlFlag,VoltReductionPointId) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            
            removeSql = "DELETE FROM CAPCONTROLSUBSTATIONBUS WHERE SubstationBusID = ?";
            
            updateSql = "UPDATE CAPCONTROLSUBSTATIONBUS SET CurrentVarLoadPointID = ?," + 
            "CurrentWattLoadPointID = ?,MapLocationID = ?,CurrentVoltLoadPointID = ?,AltSubID = ?, " + 
            "SwitchPointID = ?,DualBusEnabled = ?,MultiMonitorControl = ?,usephasedata = ?,phaseb = ?, " + 
            "phasec = ?, ControlFlag = ?, VoltReductionPointId = ? WHERE SubstationBusID = ?";
            
            selectAllSql = "SELECT yp.PAOName, SubstationBusID,CurrentVarLoadPointID,CurrentWattLoadPointID," + 
            "MapLocationID,CurrentVoltLoadPointID,AltSubID,SwitchPointID,DualBusEnabled," + 
            "MultiMonitorControl,usephasedata,phaseb,phasec,ControlFlag,VoltReductionPointId " + 
            "FROM CapControlSubstationBus, YukonPAObject yp ";
            
            selectByIdSql = selectAllSql + " WHERE SubstationBusID = yp.PAObjectID AND SubstationBusID = ?";
                        
            rowMapper = SubstationBusDaoImpl.createRowMapper();
            liteCapControlObjectRowMapper = CapbankControllerDaoImpl.createLiteCapControlObjectRowMapper();
        }
    
    private static final ParameterizedRowMapper<SubstationBus> createRowMapper() {
        ParameterizedRowMapper<SubstationBus> rowMapper = new ParameterizedRowMapper<SubstationBus>() {
            public SubstationBus mapRow(ResultSet rs, int rowNum) throws SQLException {
                SubstationBus bus = new SubstationBus();
                bus.setId(rs.getInt("SubstationBusID"));
                bus.setCurrentVarLoadPointId(rs.getInt("CurrentVarLoadPointID"));
                bus.setCurrentWattLoadPointId(rs.getInt("CurrentWattLoadPointID"));
                bus.setMapLocationId(rs.getString("MapLocationID"));
                bus.setCurrentVoltLoadPointId(rs.getInt("CurrentVoltLoadPointID"));
                bus.setAltSubId(rs.getInt("AltSubID"));
                bus.setSwitchPointId(rs.getInt("SwitchPointID"));
                String data = rs.getString("DualBusEnabled");
                Validator.isNotNull(data);
                bus.setDualBusEnabled(data);
                data = rs.getString("MultiMonitorControl");
                Validator.isNotNull(data);
                bus.setMultiMonitorControl(data);
                data = rs.getString("usephasedata");
                Validator.isNotNull(data);
                bus.setUsephasedata(data);
                bus.setPhaseb(rs.getInt("phaseb"));
                bus.setPhasec(rs.getInt("phasec"));
                data = rs.getString("DualBusEnabled");
                Validator.isNotNull(data);
                bus.setControlFlag(data);
                bus.setVoltReductionPointId(rs.getInt("VoltReductionPointId"));
                
                return bus;
            }
        };
        return rowMapper;
    }

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(SubstationBus bus) {
		int rowsAffected = 0;
		YukonPAObject pao = new YukonPAObject();
		
		pao.setCategory(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoClass(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoName(bus.getName());
		pao.setType(CapControlTypes.STRING_CAPCONTROL_SUBBUS);
		pao.setDescription(CtiUtilities.STRING_NONE);
		
		boolean ret = paoDao.add(pao);
		
		if (ret == false) {
			CTILogger.debug("Insert of Subbus, " + bus.getName() + ", in YukonPAObject table failed.");
			return false;
		}
		
		//Added to YukonPAObject table, now add to CAPCONTROLSUBSTATIONBUS
		bus.setId(pao.getPaObjectID());
    	rowsAffected = simpleJdbcTemplate.update(insertSql, bus.getId(),
                                                     bus.getCurrentVarLoadPointId(),
                                                     bus.getCurrentWattLoadPointId(),
                                                     bus.getMapLocationId(),
                                                     bus.getCurrentVoltLoadPointId(),
                                                     bus.getAltSubId(),
                                                     bus.getSwitchPointId(),
                                                     bus.getDualBusEnabled(),
                                                     bus.getMultiMonitorControl(),
                                                     bus.getUsephasedata(),
                                                     bus.getPhaseb(),
                                                     bus.getPhasec(),
                                                     bus.getControlFlag(),
                                                     bus.getVoltReductionPointId());
        boolean result = (rowsAffected == 1);
        
		if (result == false) {
			CTILogger.debug("Insert of Subbus, " + bus.getName() + ", in CAPCONTROLSUBSTATIONBUS table failed.");
			return false;
		}

		SmartMultiDBPersistent smartMulti = CBCPointFactory.createPointsForPAO(pao.getPaObjectID(), pao.getDbConnection());
		
		try {
			smartMulti.add();
		} catch (SQLException e) {
			CTILogger.debug("Inserting Points for Subbus, " + bus.getName() + " failed.");
			return false;
		}
		
		seasonScheduleDao.saveDefaultSeasonStrategyAssigment(bus.getId());
		holidayScheduleDao.saveDefaultHolidayScheduleStrategyAssigment(bus.getId());
		
        return result;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(SubstationBus bus) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql,bus.getId());
        boolean result = (rowsAffected == 1);
        
        if (result) {
    		YukonPAObject pao = new YukonPAObject();
    		pao.setPaObjectID(bus.getId());
    		
    		return paoDao.remove(pao);       	
        }
		
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(SubstationBus bus) {
		int rowsAffected = 0;
		YukonPAObject pao = new YukonPAObject();
		
		pao.setCategory(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoClass(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoName(bus.getName());
		pao.setType(CapControlTypes.STRING_CAPCONTROL_SUBBUS);
		pao.setDescription(CtiUtilities.STRING_NONE);
		
		pao.setPaObjectID(bus.getId());
		
		boolean ret = paoDao.update(pao);
		
		if (ret == false) {
			CTILogger.debug("Update of Subbus, " + bus.getName() + ", in YukonPAObject table failed.");
			return false;
		}
		
		//Added to YukonPAObject table, now add to CAPCONTROLSUBSTATIONBUS
    	rowsAffected = simpleJdbcTemplate.update(updateSql, bus.getCurrentVarLoadPointId(),
                                                     bus.getCurrentWattLoadPointId(),
                                                     bus.getMapLocationId(),
                                                     bus.getCurrentVoltLoadPointId(),
                                                     bus.getAltSubId(),
                                                     bus.getSwitchPointId(),
                                                     bus.getDualBusEnabled(),
                                                     bus.getMultiMonitorControl(),
                                                     bus.getUsephasedata(),
                                                     bus.getPhaseb(),
                                                     bus.getPhasec(),
                                                     bus.getControlFlag(),
                                                     bus.getVoltReductionPointId(),
                                                     bus.getId());
        boolean result = (rowsAffected == 1);
        
		if (result == false) {
			CTILogger.debug("Update of Subbus, " + bus.getName() + ", in CAPCONTROLSUBSTATIONBUS table failed.");
		}
        
        return result;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public SubstationBus getById(int id){
        SubstationBus s = simpleJdbcTemplate.queryForObject(selectByIdSql, rowMapper, id);
        return s;
    }
    
    public List<Integer> getAllUnassignedBuses () {
    
        ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
                Integer i = new Integer ( rs.getInt("substationBusID") );
                return i;
            }
        };
        
        String orphanedSubs = "SELECT substationBusID FROM CapControlSubstationBus WHERE ";
        orphanedSubs += "substationBusID NOT IN (SELECT substationBusId FROM CCSubstationSubBusList) ";
        orphanedSubs += "ORDER BY substationBusID";
        List<Integer> listmap = simpleJdbcTemplate.query(orphanedSubs, mapper);
        
        return listmap;
    }

	@Override
	public List<LiteCapControlObject> getOrphans() {
        List<Integer> ids = getAllUnassignedBuses();
		
		ChunkingSqlTemplate<Integer> template = new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);
		final List<LiteCapControlObject> unassignedObjects = template.query(new SqlGenerator<Integer>() {
            @Override
            public String generate(List<Integer> subList) {
                SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
                sqlBuilder.append("SELECT PAObjectID,PAOName,TYPE,Description FROM YukonPAObject WHERE PAObjectID IN (");
                sqlBuilder.append(subList);
                sqlBuilder.append(")");
                String sql = sqlBuilder.toString();
                return sql;
            }
        }, ids, liteCapControlObjectRowMapper);
		
		return unassignedObjects;
	}
	
    @Override
    public boolean assignSubstationBus(Substation substation, SubstationBus substationBus) {
    	return assignSubstationBus(substation.getId(),substationBus.getId());
    }
    
    @Override
    public boolean assignSubstationBus(int substationId, int substationBusId) {
    	String getDisplayOrderSql = "SELECT max(DisplayOrder) FROM CCSUBSTATIONSUBBUSLIST WHERE SubStationID = ?";
    	String insertAssignmentSql = "INSERT INTO CCSUBSTATIONSUBBUSLIST (SubStationID,SubStationBusID,DisplayOrder) VALUES (?,?,?)";
    	
		//remove any existing assignment
    	unassignSubstationBus(substationId,substationBusId);
    	
		int displayOrder = simpleJdbcTemplate.queryForInt(getDisplayOrderSql, substationId);
		int rowsAffected = simpleJdbcTemplate.update(insertAssignmentSql, substationId,substationBusId,++displayOrder);
		
		boolean result = (rowsAffected == 1);
		return result;
    }

    @Override
    public boolean unassignSubstationBus(Substation substation, SubstationBus substationBus) {
    	return unassignSubstationBus(substation.getId(),substationBus.getId());
    }
    
    @Override
    public boolean unassignSubstationBus(int substationId, int substationBusId) {
    	String deleteAssignmentSql = "DELETE FROM CCSUBSTATIONSUBBUSLIST WHERE SubStationID = ? AND SubstationBusID = ?";
    	
		int rowsAffected = simpleJdbcTemplate.update(deleteAssignmentSql, substationId,substationBusId);
		
		boolean result = (rowsAffected == 1);
		return result;
    }
    
	@Override
	public int getParentId(SubstationBus stationBus) {
		String getParentSql = "SELECT SubStationID FROM CCSUBSTATIONSUBBUSLIST WHERE SubstationBusId= ?";
		
		int id = simpleJdbcTemplate.queryForInt(getParentSql, stationBus.getId());
		return id;
	}
	    
    @Autowired
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Autowired
	public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}
    
    @Autowired
	public void setSeasonScheduleDao(SeasonScheduleDao seasonScheduleDao) {
		this.seasonScheduleDao = seasonScheduleDao;
	}

    @Autowired
	public void setHolidayScheduleDao(HolidayScheduleDao holidayScheduleDao) {
		this.holidayScheduleDao = holidayScheduleDao;
	}

}
