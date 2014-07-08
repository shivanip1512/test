package com.cannontech.capcontrol.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.Substation;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;

public class SubstationDaoImpl implements SubstationDao {
    
    private static final YukonRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private PaoDao paoDao;
    
    static {
        liteCapControlObjectRowMapper = CapbankControllerDaoImpl.createLiteCapControlObjectRowMapper();
    }
    
    @Override
    public Substation findSubstationById (final int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.PAOName, ypo.Type, ypo.Description, ypo.DisableFlag, s.MapLocationId, s.voltReductionPointId");
        sql.append("FROM YukonPAObject ypo");
        sql.append("JOIN CapControlSubstation s ON s.SubstationID = ypo.PAObjectID");
        sql.append("WHERE PAObjectID").eq(id);
        
        return yukonJdbcTemplate.queryForObject(sql, new YukonRowMapper<Substation>() {
            @Override
            public Substation mapRow(YukonResultSet rs) throws SQLException {
                Substation sub = new Substation(new PaoIdentifier(id, rs.getEnum("type", PaoType.class)));
                sub.setName(rs.getString("paoName"));
                sub.setDisabled(rs.getBooleanYN("DisableFlag"));
                sub.setDescription(rs.getString("Description"));
                sub.setMapLocationId(rs.getString("MapLocationId"));
                sub.setVoltReductionPointId(rs.getInt("voltReductionPointId"));
                return sub;
            }
        });
    }
    
    @Override 
    public boolean assignSubstation(int substationId, String areaName) {
        YukonPao pao = paoDao.findYukonPao(areaName, PaoType.CAP_CONTROL_AREA);
        return (pao == null) ? false : assignSubstation(pao.getPaoIdentifier().getPaoId(), substationId);
    };

	@Override
	public boolean assignSubstation(int areaId, int substationId) {
		SqlStatementBuilder displaySql = new SqlStatementBuilder();
		
		displaySql.append("SELECT MAX(DisplayOrder)");
		displaySql.append("FROM CCSubAreaAssignment");
		displaySql.append("WHERE AreaId").eq(areaId);

		int displayOrder = yukonJdbcTemplate.queryForInt(displaySql);	
		
		//remove any existing assignment
		unassignSubstation(substationId);
		
		SqlStatementBuilder assignSql = new SqlStatementBuilder();
		
		SqlParameterSink params = assignSql.insertInto("CCSubAreaAssignment");
		params.addValue("AreaID", areaId);
		params.addValue("SubstationBusID", substationId);
		params.addValue("DisplayOrder", ++displayOrder);
		
		int rowsAffected = yukonJdbcTemplate.update(assignSql);
		
		boolean result = (rowsAffected == 1);
		
		if (result) {
		    YukonPao substation = paoDao.getYukonPao(substationId);
		    YukonPao area = paoDao.getYukonPao(areaId);
		    dbChangeManager.processPaoDbChange(substation, DbChangeType.UPDATE);
		    dbChangeManager.processPaoDbChange(area, DbChangeType.UPDATE);
		}
		
		return result;
	}

	@Override
	public boolean unassignSubstation(int substationId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		
		sql.append("DELETE FROM CCSubAreaAssignment");
		sql.append("WHERE SubstationBusID").eq(substationId);
		
		int rowsAffected = yukonJdbcTemplate.update(sql);
		
		boolean result = (rowsAffected == 1);
		
		return result;
	}
    
    @Override
    public List<Integer> getAllUnassignedSubstationIds() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT PAObjectID");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE Type").eq(PaoType.CAP_CONTROL_SUBSTATION);
        sql.append(   "AND PAObjectID NOT IN");
        sql.append(      "(SELECT SubstationBusID");
        sql.append(      " FROM CCSubAreaAssignment)");
        sql.append("ORDER BY PAObjectID");
        
        List<Integer> listmap = yukonJdbcTemplate.query(sql, RowMapper.INTEGER);
        
        return listmap;
    }
    
    @Override
	public List<LiteCapControlObject> getOrphans() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Type").eq(PaoType.CAP_CONTROL_SUBSTATION);
        sql.append("    AND PAObjectID not in (SELECT SubstationBusID FROM CCSubAreaAssignment)");
        sql.append("ORDER BY PAOName");
        
        List<LiteCapControlObject> orphans = yukonJdbcTemplate.query(sql, liteCapControlObjectRowMapper);
        
        return orphans;
	}
    
    @Override
    public List<Integer> getAllSpecialAreaUnassignedSubstationIds (Integer areaId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT PAObjectID");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE Type").eq(PaoType.CAP_CONTROL_SUBSTATION);
        sql.append(   "AND PAObjectID NOT IN");
        sql.append(      "(SELECT SubstationBusID");
        sql.append(      " FROM CCSubSpecialAreaAssignment");
        sql.append(      " WHERE AreaID").eq(areaId);
        sql.append(      ")");
        
        List<Integer> listmap = yukonJdbcTemplate.query(sql, RowMapper.INTEGER);
        
        return listmap;
    }
}
