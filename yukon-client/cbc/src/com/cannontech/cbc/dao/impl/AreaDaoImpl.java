package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.cbc.dao.AreaDao;
import com.cannontech.cbc.model.Area;
import com.cannontech.cbc.model.SpecialArea;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.HolidayScheduleDao;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;

public class AreaDaoImpl implements AreaDao {

    private static final String insertSql;
    private static final String specialAreaInsertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    
    private static final ParameterizedRowMapper<Area> rowMapper;
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private SeasonScheduleDao seasonScheduleDao;
    private HolidayScheduleDao holidayScheduleDao;
    private NextValueHelper nextValueHelper;
    
	static {
            
    		insertSql = "INSERT INTO CAPCONTROlAREA " +
            "(AreaID,VoltReductionPointId) VALUES (?,?)";
    		
    		specialAreaInsertSql = "INSERT INTO CAPCONTROlSPECIALAREA " +
            "(AreaID,VoltReductionPointId) VALUES (?,?)";
    		
            removeSql = "DELETE FROM CAPCONTROlAREA WHERE AreaID = ?";
            
            updateSql = "UPDATE CAPCONTROlAREA " 
            	      + " SET VoltReductionPointId = ? WHERE AreaID = ?";
            
            selectAllSql = "SELECT yp.PAOName,AreaID,VoltReductionPointId FROM CAPCONTROlAREA, YukonPAObject yp ";
            
            selectByIdSql = selectAllSql + " WHERE AreaID = yp.PAObjectID AND AreaID = ?";
            
            rowMapper = AreaDaoImpl.createRowMapper();
        }
    
    private static final ParameterizedRowMapper<Area> createRowMapper() {
        ParameterizedRowMapper<Area> rowMapper = new ParameterizedRowMapper<Area>() {
            public Area mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Area area = new Area();
            	area.setId(rs.getInt("AreaID"));
            	area.setVoltReductionPointId(rs.getInt("VoltReductionPointID"));

                return area;
            }
        };
        return rowMapper;
    }

	@Override
	public void add(Area area) throws TransactionException {
		int newPaoId = nextValueHelper.getNextValue("YukonPaObject");

		YukonPAObject pao = new YukonPAObject();
		pao.setPaObjectID(newPaoId);
		pao.setCategory(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoClass(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoName(area.getName());
		pao.setType(CapControlType.AREA.getDisplayValue());
		pao.setDescription(area.getDescription());
		pao.setDisableFlag(area.getDisabled() ? 'Y' : 'N');
				
		Transaction.createTransaction(com.cannontech.database.Transaction.INSERT, pao).execute();
		
		//Added to YukonPAObject table, now add to CAPCONTROLAREA
		area.setId(pao.getPaObjectID());
		simpleJdbcTemplate.update(insertSql, area.getId(), area.getVoltReductionPointId());
		
		seasonScheduleDao.saveDefaultSeasonStrategyAssigment(area.getId());
		holidayScheduleDao.saveDefaultHolidayScheduleStrategyAssigment(area.getId());
	}
	
	@Override
    public void addSpecialArea(SpecialArea specialArea) throws TransactionException {
        int newPaoId = nextValueHelper.getNextValue("YukonPaObject");

        YukonPAObject pao = new YukonPAObject();
        pao.setPaObjectID(newPaoId);
        pao.setCategory(PAOGroups.STRING_CAT_CAPCONTROL);
        pao.setPaoClass(PAOGroups.STRING_CAT_CAPCONTROL);
        pao.setPaoName(specialArea.getName());
        pao.setType(CapControlType.SPECIAL_AREA.getDisplayValue());
        pao.setDescription(specialArea.getDescription());
        pao.setDisableFlag(specialArea.getDisabled() ? 'Y' : 'N');
                
        Transaction.createTransaction(com.cannontech.database.Transaction.INSERT, pao).execute();
        
        //Added to YukonPAObject table, now add to CAPCONTROLAREA
        specialArea.setId(pao.getPaObjectID());
        int rowsAffected = simpleJdbcTemplate.update(specialAreaInsertSql, specialArea.getId(), specialArea.getVoltReductionPointId());

        boolean result = (rowsAffected == 1);
        
        if (result == false) {
            CTILogger.debug("Insert of SpecialArea, " + specialArea.getName() + ", in CAPCONTROLSPECIALAREA table failed.");
        } else {
        
            seasonScheduleDao.saveDefaultSeasonStrategyAssigment(specialArea.getId());
            holidayScheduleDao.saveDefaultHolidayScheduleStrategyAssigment(specialArea.getId());
        }
    }

	@Override
	public boolean update(Area area) {
		int rowsAffected = 0;
		
		YukonPAObject pao = new YukonPAObject();
		
		pao.setCategory(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoClass(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoName(area.getName());
		pao.setType(CapControlType.AREA.getDisplayValue());
		pao.setDescription(area.getDescription());
		pao.setPaObjectID(area.getId());
				
		try {
			Transaction.createTransaction(com.cannontech.database.Transaction.UPDATE, pao).execute();
		} catch (TransactionException e) {
			CTILogger.error("Update of Area, " + area.getName() + ", in YukonPAObject table failed.");
			return false;
		}

		//Added to YukonPAObject table, now add to CAPCONTROLAREA
		rowsAffected = simpleJdbcTemplate.update(updateSql, area.getVoltReductionPointId(), area.getId());
		
		boolean result = (rowsAffected == 1);
		
		if (result == false) {
			CTILogger.debug("Insert of Area, " + area.getName() + ", in CAPCONTROLAREA table failed.");
		}
		
		return result;
	}
	
	@Override
	public Area getById(int id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Area area) {
		
        int rowsAffected = simpleJdbcTemplate.update(removeSql,area.getId() );
        boolean result = (rowsAffected == 1);
        
        if (result) {
    		YukonPAObject pao = new YukonPAObject();
    		pao.setPaObjectID(area.getId());
    		try {
    			Transaction.createTransaction(com.cannontech.database.Transaction.DELETE, pao).execute();
    		} catch (TransactionException e) {
    			CTILogger.error("Removal of Area, " + area.getName() + ", in YukonPAObject table failed.");
    			return false;
    		}
        }
		
        return result;
	}
    
    @Autowired
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
	
	@Autowired
	public void setSeasonScheduleDao(SeasonScheduleDao seasonScheduleDao) {
		this.seasonScheduleDao = seasonScheduleDao;
	}
	
	@Autowired
	public void setHolidayScheduleDao(HolidayScheduleDao holidayScheduleDao) {
		this.holidayScheduleDao = holidayScheduleDao;
	}
	
	@Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
		this.nextValueHelper = nextValueHelper;
	}

}
