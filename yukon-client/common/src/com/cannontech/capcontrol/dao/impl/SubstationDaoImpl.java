package com.cannontech.capcontrol.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.Substation;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

public class SubstationDaoImpl implements SubstationDao {	
    private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;
    
    private DBPersistentDao dbPersistentDao;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private PaoDao paoDao;
    
    static {
        liteCapControlObjectRowMapper = CapbankControllerDaoImpl.createLiteCapControlObjectRowMapper();
    }
    
    @Autowired
    public void setYukonJdbcTemplate(final YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
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
		    sendDbChange(substationId, CapControlType.SUBSTATION, DbChangeType.UPDATE);
		    sendDbChange(areaId, CapControlType.AREA, DbChangeType.UPDATE);
		}
		
		return result;
	}
	
	private void sendDbChange(int id, CapControlType ccType, DbChangeType type) {
	    DBChangeMsg msg = new DBChangeMsg(id, DBChangeMsg.CHANGE_PAO_DB, 
	                                      PaoCategory.CAPCONTROL.getDbString(),
	                                      ccType.getDbValue(), type);
	    dbPersistentDao.processDBChange(msg);
	}
	
	@Override
	public boolean unassignSubstation(Substation substation) {
		return unassignSubstation(substation.getId());
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
    
    public List<Integer> getAllUnassignedSubstationIds() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT PAObjectID");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE Type").eq(PaoType.CAP_CONTROL_SUBSTATION);
        sql.append(   "AND PAObjectID NOT IN");
        sql.append(      "(SELECT SubstationBusID");
        sql.append(      " FROM CCSubAreaAssignment)");
        sql.append("ORDER BY PAObjectID");
        
        List<Integer> listmap = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        
        return listmap;
    }
    
    @Override
	public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count) {
	    /* Get the unordered total count */
    	SqlStatementBuilder orphanSql = new SqlStatementBuilder();
    	
    	orphanSql.append("SELECT COUNT(*)");
    	orphanSql.append("FROM CapControlSubstation");
    	orphanSql.append("WHERE SubstationID NOT IN");
    	orphanSql.append(   "(SELECT SubstationBusID");
    	orphanSql.append(   " FROM CCSubAreaAssignment)");
    	
        int orphanCount = yukonJdbcTemplate.queryForInt(orphanSql);
        
        /* Get the paged subset of cc objects */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Type").eq(PaoType.CAP_CONTROL_SUBSTATION);
        sql.append("    AND PAObjectID not in (SELECT SubstationBusID FROM CCSubAreaAssignment)");
        sql.append("ORDER BY PAOName");
        
        PagingResultSetExtractor<LiteCapControlObject> orphanExtractor = new PagingResultSetExtractor<LiteCapControlObject>(start, count, liteCapControlObjectRowMapper);
        yukonJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), orphanExtractor);
        
        List<LiteCapControlObject> unassignedSubstations = (List<LiteCapControlObject>) orphanExtractor.getResultList();
        
        SearchResult<LiteCapControlObject> searchResult = new SearchResult<LiteCapControlObject>();
        searchResult.setResultList(unassignedSubstations);
        searchResult.setBounds(start, count, orphanCount);
        
        return searchResult;
	}
    
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
        
        List<Integer> listmap = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        
        return listmap;
    }

    @Override
    public List<Integer> getAllSubstationIds() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT SubstationID");
        sql.append("FROM CapControlSubstation");
        
        List<Integer> listmap = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        
        return listmap;
    }
    
    @Override
    public int getSubstationIdByName(String name) throws EmptyResultDataAccessException{
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("SELECT SubstationID");
    	sql.append("FROM CapControlSubstation, YukonPAObject");
    	sql.append("WHERE SubstationID = PAObjectID AND PAOName LIKE '" + name + "'");

        int i = yukonJdbcTemplate.queryForInt(sql);
        
        return i;
    }
	
    @Override
	public int getParentId(Substation station) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	sql.append("SELECT AreaID");
    	sql.append("FROM CCSubAreaAssignment");
    	sql.append("WHERE SubstationBusID").eq(station.getId());
		
		int id = yukonJdbcTemplate.queryForInt(sql);
		
		return id;
	}
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
}
