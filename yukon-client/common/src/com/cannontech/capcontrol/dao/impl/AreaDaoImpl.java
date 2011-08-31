package com.cannontech.capcontrol.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.AreaDao;
import com.cannontech.capcontrol.model.Area;
import com.cannontech.capcontrol.model.SpecialArea;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class AreaDaoImpl implements AreaDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
    
	@Override
	public void add(Area area) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto("CapControlArea");
		params.addValue("AreaId", area.getId());
		params.addValue("VoltReductionPointId", area.getVoltReductionPointId());
		
		int rowsAffected = yukonJdbcTemplate.update(sql);
		
		boolean result = (rowsAffected == 1);
		
		if (result == false) {
            CTILogger.debug("Insert of Area, " + area.getName() + ", in CAPCONTROLAREA table failed.");
        }
	}
	
	@Override
    public void addSpecialArea(SpecialArea specialArea) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto("CapControlSpecialArea");
		params.addValue("AreaId", specialArea.getId());
		params.addValue("VoltReductionPointId", specialArea.getVoltReductionPointId());
		
		int rowsAffected = yukonJdbcTemplate.update(sql);
		
		boolean result = (rowsAffected == 1);
		
		if (result == false) {
            CTILogger.debug("Insert of SpecialArea, " + specialArea.getName() + ", in CAPCONTROLSPECIALAREA table failed.");
        }
    }
	
	@Override
	public void removeSpecialArea(SpecialArea specialArea) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("DELETE FROM CAPCONTROLSPECIALAREA");
		sql.append("WHERE AreaId").eq(specialArea.getId());
		
		yukonJdbcTemplate.update(sql);
	}

	@Override
	public boolean update(Area area) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink params = sql.update("CapControlArea");
        params.addValue("VoltReductionPointId", area.getVoltReductionPointId());
        
        sql.append("WHERE AreaId").eq(area.getId());
        
		int rowsAffected = yukonJdbcTemplate.update(sql);
		
		boolean result = (rowsAffected == 1);
		
		if (result == false) {
			CTILogger.debug("Update of Area, " + area.getName() + ", in CAPCONTROLAREA table failed.");
		}
		
		return result;
	}
	
	@Override
	public Area getById(int id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Area area) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("DELETE FROM CapControlArea");
		sql.append("WHERE AreaId").eq(area.getId());
		
        int rowsAffected = yukonJdbcTemplate.update(sql);
        boolean result = (rowsAffected == 1);
		
        return result;
	}
    
    @Autowired
    public void setYukonJdbcTemplate(final YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
