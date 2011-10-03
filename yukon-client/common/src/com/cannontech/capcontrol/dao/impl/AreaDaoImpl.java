package com.cannontech.capcontrol.dao.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.AreaDao;
import com.cannontech.capcontrol.model.Area;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class AreaDaoImpl implements AreaDao {
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    private final YukonRowMapper<Area> areaMapper = new YukonRowMapper<Area>() {
		@Override
		public Area mapRow(YukonResultSet rs) throws SQLException {
			int paoId = rs.getInt("Y.PAObjectID");
			PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, PaoType.CAP_CONTROL_AREA);
			
			Area area = new Area(paoIdentifier);
			
			area.setName(rs.getString("Y.PAOName"));
			area.setVoltReductionPointId(rs.getInt("A.VoltReductionPointID"));
			
			return area;
		}
	};
	
	@Override
	public Area getById(int id) {		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT Y.PAObjectId, Y.PAOName, A.VoltReductionPointId");
		sql.append("FROM YukonPAObject Y");
		sql.append("  JOIN CapControlArea A ON Y.PAObjectId = A.AreaId");
		sql.append("WHERE Y.PAObjectId").eq(id);
		
		Area area = yukonJdbcTemplate.queryForObject(sql, areaMapper);
		
		return area;
	}
    
    @Autowired
    public void setYukonJdbcTemplate(final YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}