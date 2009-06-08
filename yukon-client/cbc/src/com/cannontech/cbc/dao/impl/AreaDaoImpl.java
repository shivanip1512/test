package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cbc.dao.AreaDao;
import com.cannontech.cbc.model.Area;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.pao.YukonPAObject;

public class AreaDaoImpl implements AreaDao {

    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    
    private static final ParameterizedRowMapper<Area> rowMapper;
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private PaoDao paoDao;
    
    static {
            
    		insertSql = "INSERT INTO CAPCONTROlAREA " +
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
	public boolean add(Area area) {
		int rowsAffected = 0;
		YukonPAObject pao = new YukonPAObject();
		
		pao.setCategory(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoClass(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoName(area.getName());
		pao.setType(CapControlTypes.STRING_CAPCONTROL_AREA);
		pao.setDescription("(none)");
		
		boolean ret = paoDao.add(pao);

		if (ret == false) {
			CTILogger.debug("Insert of Area, " + area.getName() + ", in YukonPAObject table failed.");
			return false;
		}

		//Added to YukonPAObject table, now add to CAPCONTROLAREA
		area.setId(pao.getPaObjectID());
		rowsAffected = simpleJdbcTemplate.update(insertSql, area.getId(),
				area.getVoltReductionPointId());

		boolean result = (rowsAffected == 1);
		
		if (result == false) {
			CTILogger.debug("Insert of Area, " + area.getName() + ", in CAPCONTROLAREA table failed.");
		}
		
		return result;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean update(Area area) {
		int rowsAffected = 0;
		
		YukonPAObject pao = new YukonPAObject();
		
		pao.setCategory(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoClass(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoName(area.getName());
		pao.setType(CapControlTypes.STRING_CAPCONTROL_AREA);
		pao.setDescription("(none)");
		
		List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(area.getName(), false);
		
		if (paos.size() == 0) {
			CTILogger.debug("No results returned for Area, " + area.getName() + ", cannot update.");
			return false;
		}
		if (paos.size() > 1) {
			CTILogger.debug("Multiple paos with Area name: " + area.getName() + " cannot update.");
			return false;
		}
		LiteYukonPAObject litePao = paos.get(0);
		pao.setPaObjectID(litePao.getYukonID());
		
		boolean ret = paoDao.update(pao);

		if (ret == false) {
			CTILogger.debug("Update of Area, " + area.getName() + ", in YukonPAObject table failed.");
			return false;
		}

		//Added to YukonPAObject table, now add to CAPCONTROLAREA
		area.setId(pao.getPaObjectID());
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
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean remove(Area area) {
		
        int rowsAffected = simpleJdbcTemplate.update(removeSql,area.getId() );
        boolean result = (rowsAffected == 1);
        
        if (result) {
    		YukonPAObject pao = new YukonPAObject();
    		pao.setPaObjectID(area.getId());
    		
    		return paoDao.remove(pao);       	
        }
		
        return result;
	}
}
