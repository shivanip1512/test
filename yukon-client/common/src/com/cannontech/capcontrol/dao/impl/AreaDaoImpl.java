package com.cannontech.capcontrol.dao.impl;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.AreaDao;
import com.cannontech.capcontrol.model.Area;
import com.cannontech.capcontrol.model.SpecialArea;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class AreaDaoImpl implements AreaDao {
	private static final Logger log = YukonLogManager.getLogger(AreaDaoImpl.class);

    private YukonJdbcTemplate yukonJdbcTemplate;
    
    private final YukonRowMapper<Area> areaMapper = new YukonRowMapper<Area>() {
		@Override
		public Area mapRow(YukonResultSet rs) throws SQLException {
			int paoId = rs.getInt("Y.PAObjectID");
			PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, PaoType.CAP_CONTROL_AREA);
			
			Area area = new Area(paoIdentifier);
			
			area.setDescription(rs.getString("Y.Description"));
			area.setName(rs.getString("Y.PAOName"));
			area.setVoltReductionPointId(rs.getInt("A.VoltReductionPointID"));
			area.setDisabled(rs.getEnum("Y.DisableFlag", YNBoolean.class).getBoolean());
			
			return area;
		}
	};
    
	@Override
	public void add(Area area) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		SqlParameterSink params = sql.insertInto("CapControlArea");
		params.addValue("AreaId", area.getId());
		params.addValue("VoltReductionPointId", area.getVoltReductionPointId());
		
		int rowsAffected = yukonJdbcTemplate.update(sql);
		
		boolean result = (rowsAffected == 1);
		
		if (result == false) {
            log.debug("Insert of Area, " + area.getName() + ", in CAPCONTROLAREA table failed.");
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
            log.debug("Insert of SpecialArea, " + specialArea.getName() + ", in CAPCONTROLSPECIALAREA table failed.");
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
			log.debug("Update of Area, " + area.getName() + ", in CAPCONTROLAREA table failed.");
		}
		
		return result;
	}
	
	@Override
	public Area getById(int id) {		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT Y.PAObjectID, Y.PAOName, Y.Description, Y.DisableFlag, A.VoltReductionPointID");
		sql.append("FROM YukonPAObject Y");
		sql.append("   LEFT JOIN CapControlArea A ON Y.PAObjectID = A.AreaID");
		sql.append("WHERE Y.PAObjectID").eq(id);
		
		Area area = yukonJdbcTemplate.queryForObject(sql, areaMapper);
		
		return area;
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
