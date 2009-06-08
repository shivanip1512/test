package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cbc.dao.SubstationDao;
import com.cannontech.cbc.model.Area;
import com.cannontech.cbc.model.Substation;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.pao.YukonPAObject;

public class SubstationDaoImpl implements SubstationDao {
    
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    
    private static final ParameterizedRowMapper<Substation> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private PaoDao paoDao;
    
    static {
            insertSql = "INSERT INTO CAPCONTROLSUBSTATION "
				+ "(SubstationID,VoltReductionPointId) VALUES (?,?)";
            
            removeSql = "DELETE FROM CAPCONTROLSUBSTATION WHERE SubstationID = ?";
            
            updateSql = "UPDATE CAPCONTROLSUBSTATION " 
            	      + " SET VoltReductionPointId = ? WHERE SubstationID = ?";
            
            selectAllSql = "SELECT yp.PAOName,SubstationID,VoltReductionPointId "
				+ "FROM CAPCONTROLSUBSTATION, YukonPAObject yp ";
            
            selectByIdSql = selectAllSql + " WHERE SubstationID = yp.PAOName AND SubstationID = ?";
            
            rowMapper = SubstationDaoImpl.createRowMapper();
        }
    
    private static final ParameterizedRowMapper<Substation> createRowMapper() {
        ParameterizedRowMapper<Substation> rowMapper = new ParameterizedRowMapper<Substation>() {
            public Substation mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Substation station = new Substation();
                station.setId(rs.getInt("SubstationID"));
                station.setVoltReductionPointId(rs.getInt("VoltReductionPointId"));

                return station;
            }
        };
        return rowMapper;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Autowired
	public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add (Substation substation) {
		int rowsAffected = 0;
		YukonPAObject pao = new YukonPAObject();
		
		pao.setCategory(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoClass(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoName(substation.getName());
		pao.setType(CapControlTypes.STRING_CAPCONTROL_SUBSTATION);
		pao.setDescription("(none)");

		boolean ret = paoDao.add(pao);
		
		if (ret == false) {
			CTILogger.debug("Insert of Substation, " + substation.getName() + ", in YukonPAObject table failed.");
			return false;
		}
		
		substation.setId(pao.getPaObjectID());
		rowsAffected = simpleJdbcTemplate.update(insertSql, substation.getId(),
				substation.getVoltReductionPointId());
		
		boolean result = (rowsAffected == 1);
		
		if (result == false) {
			CTILogger.debug("Insert of Substation, " + substation.getName() + ", in CAPCONTROLSUBSTATION table failed.");
		}
		
		return result;
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove (Substation substation) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql,substation.getId() );
        boolean result = (rowsAffected == 1);
        
        if (result) {
    		YukonPAObject pao = new YukonPAObject();
    		pao.setPaObjectID(substation.getId());
    		
    		return paoDao.remove(pao);       	
        }
		
        return result;
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update (Substation substation) {
		int rowsAffected = 0;
		
		YukonPAObject pao = new YukonPAObject();
		
		pao.setCategory(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoClass(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoName(substation.getName());
		pao.setType(CapControlTypes.STRING_CAPCONTROL_SUBSTATION);
		pao.setDescription("(none)");
		
		List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(substation.getName(), false);
		
		if (paos.size() == 0) {
			CTILogger.debug("No results returned for Substation, " + substation.getName() + ", cannot update.");
			return false;
		}
		if (paos.size() > 1) {
			CTILogger.debug("Multiple paos with Substation name: " + substation.getName() + " cannot update.");
			return false;
		}
		
		LiteYukonPAObject litePao = paos.get(0);
		pao.setPaObjectID(litePao.getYukonID());
		
		boolean ret = paoDao.update(pao);

		if (ret == false) {
			CTILogger.debug("Update of Substation, " + substation.getName() + ", in YukonPAObject table failed.");
			return false;
		}

		//Added to YukonPAObject table, now add to CAPCONTROLSUBSTATION table
		substation.setId(pao.getPaObjectID());
		rowsAffected = simpleJdbcTemplate.update(updateSql, substation.getVoltReductionPointId(), substation.getId());
		
		boolean result = (rowsAffected == 1);
		
		if (result == false) {
			CTILogger.debug("Update of Substation, " + substation.getName() + ", in CAPCONTROLSUBSTATION table failed.");
		}
		
		return result;
    }
    
    @Override
    public Substation getSubstationById (int id) {
    	throw new UnsupportedOperationException();
    }
    
	@Override
	public boolean assignSubstation(Area area, Substation substation) {
		return assignSubstation(area.getId(), substation.getId());
	}

	@Override
	public boolean assignSubstation(int areaId, int substationId) {
		String getDisplayOrderSql = "SELECT max(DisplayOrder) FROM CCSUBAREAASSIGNMENT WHERE AreaID = ?";
		String assignStationSql = "INSERT INTO CCSUBAREAASSIGNMENT (AreaID,SubstationBusID,DisplayOrder) VALUES (?,?,?)";
		
		int displayOrder = simpleJdbcTemplate.queryForInt(getDisplayOrderSql, areaId);
		int rowsAffected = simpleJdbcTemplate.update(assignStationSql, areaId,substationId,++displayOrder);
		
		boolean result = (rowsAffected == 1);
		return result;
	}
	
	@Override
	public boolean unassignSubstation(Area area, Substation substation) {
		return unassignSubstation(area.getId(), substation.getId());
	}

	@Override
	public boolean unassignSubstation(int areaId, int substationId) {
		String unassignStationSql = "DELETE FROM CCSUBAREAASSIGNMENT WHERE AreaID = ? AND SubstationBusID = ?";
		
		int rowsAffected = simpleJdbcTemplate.update(unassignStationSql, areaId,substationId);
		
		boolean result = (rowsAffected == 1);
		return result;
	}
    
    public List<Integer> getAllUnassignedSubstationIds() {

        ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
                Integer i = new Integer ( rs.getInt("paobjectid") );
                return i;
            }
        };
        
        String query = "select PAObjectID from YukonPAObject where type like 'CCSUBSTATION' ";
        query += "and ";
        query += "PAObjectID not in (select SubstationBusID from CCSUBAREAASSIGNMENT)";
        
        List<Integer> listmap = simpleJdbcTemplate.query(query, mapper);
        
        return listmap;
    }
    
    public List<Integer> getAllSpecialAreaUnassignedSubstationIds (Integer areaId) {

        ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
                Integer i = new Integer ( rs.getInt("PAObjectID") );
                return i;
            }
        };
        
        String query = "select PAObjectID from YukonPAObject where type like 'CCSUBSTATION' ";
        query += "and ";
        query += "PAObjectID not in (select SubstationBusID from CCSUBSPECIALAREAASSIGNMENT where AreaID = "+areaId+ " )";
        
        List<Integer> listmap = simpleJdbcTemplate.query(query, mapper);
        
        return listmap;
    }

    public List<Integer> getAllSubstationIds() {
        //does not appear to be used
        ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
                Integer i = new Integer ( rs.getInt("SubstationID") );
                return i;
            }
        };
        
        String query = "select SubstationID from CAPCONTROLSUBSTATION";
        
        List<Integer> listmap = simpleJdbcTemplate.query(query, mapper);
        
        return listmap;
    }
    
    public Integer getSubstationIdByName(String name) throws EmptyResultDataAccessException{

        String query = "select SubstationID from CAPCONTROLSUBSTATION, YukonPAObject";
        query += "where SubstationID = PAObjectID and PAOName like "  + "'?'";

        Integer i = simpleJdbcTemplate.queryForInt(query, name);
        
        return i;
    }
	
    @Override
	public int getParentId(Substation station) {
		String getParentSql = "SELECT AreaID FROM CCSUBAREAASSIGNMENT WHERE SubstationBusId = ?";
		
		int id = simpleJdbcTemplate.queryForInt(getParentSql, station.getId());
		return id;
	}
}
