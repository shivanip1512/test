package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.cbc.dao.SubstationDao;
import com.cannontech.cbc.model.Area;
import com.cannontech.cbc.model.Substation;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;

public class SubstationDaoImpl implements SubstationDao {
    
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    
    private static final ParameterizedRowMapper<Substation> rowMapper;
    private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    
    static {
            insertSql = "INSERT INTO CAPCONTROLSUBSTATION "
				+ "(SubstationID,VoltReductionPointId,maplocationid) VALUES (?,?,?)";
            
            removeSql = "DELETE FROM CAPCONTROLSUBSTATION WHERE SubstationID = ?";
            
            updateSql = "UPDATE CAPCONTROLSUBSTATION " 
            	      + " SET VoltReductionPointId = ?, SET maplocationid =?, WHERE SubstationID = ?";
            
            selectAllSql = "SELECT yp.PAOName,SubstationID,VoltReductionPointId "
				+ "FROM CAPCONTROLSUBSTATION, YukonPAObject yp ";
            
            selectByIdSql = selectAllSql + " WHERE SubstationID = yp.PAOName AND SubstationID = ?";
            
            rowMapper = SubstationDaoImpl.createRowMapper();
            liteCapControlObjectRowMapper = CapbankControllerDaoImpl.createLiteCapControlObjectRowMapper();
        }
    
    private static final ParameterizedRowMapper<Substation> createRowMapper() {
        ParameterizedRowMapper<Substation> rowMapper = new ParameterizedRowMapper<Substation>() {
            public Substation mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Substation station = new Substation();
                station.setId(rs.getInt("SubstationID"));
                station.setVoltReductionPointId(rs.getInt("VoltReductionPointId"));
                station.setMapLocationId(rs.getString("maplocationid"));
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
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
		this.nextValueHelper = nextValueHelper;
	}

	@Override
    public void add (Substation substation) {
		int newPaoId = nextValueHelper.getNextValue("YukonPaObject");
		
		YukonPAObject pao = new YukonPAObject();
		pao.setPaObjectID(newPaoId);
		pao.setCategory(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoClass(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoName(substation.getName());
		pao.setType(CapControlType.SUBSTATION.getDbValue());
		pao.setDescription(substation.getDescription());
		pao.setDisableFlag(substation.getDisabled() ? 'Y' : 'N');
		
		try {
		    Transaction.createTransaction(com.cannontech.database.Transaction.INSERT, pao).execute();
		} catch (TransactionException e ) {
		    throw new DataIntegrityViolationException("Insert of Substation, " + substation.getName() + ", in YukonPAObject table failed.", e);
		}
		
		substation.setId(pao.getPaObjectID());
		simpleJdbcTemplate.update(insertSql, substation.getId(), substation.getVoltReductionPointId(), substation.getMapLocationId());
    }
    
    @Override
    public boolean remove (Substation substation) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql,substation.getId() );
        boolean result = (rowsAffected == 1);
        
        if (result) {
    		YukonPAObject pao = new YukonPAObject();
    		pao.setPaObjectID(substation.getId());
    		
    		try {
    			Transaction.createTransaction(com.cannontech.database.Transaction.DELETE, pao).execute();
    		} catch (TransactionException e) {
    			CTILogger.error("Removal of Substation, " + substation.getName() + ", in YukonPAObject table failed.");
    			return false;
    		}
        }
		
        return result;
    }
    
    @Override
    public boolean update (Substation substation) {
		int rowsAffected = 0;
		
		YukonPAObject pao = new YukonPAObject();
		
		pao.setCategory(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoClass(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoName(substation.getName());
		pao.setType(CapControlType.SUBSTATION.getDbValue());
		pao.setDescription(substation.getDescription());
		pao.setDisableFlag(substation.getDisabled() ? 'Y' : 'N');
		pao.setPaObjectID(substation.getId());
		
		try {
			Transaction.createTransaction(com.cannontech.database.Transaction.UPDATE, pao).execute();
		} catch (TransactionException e) {
			CTILogger.error("Update of Substation, " + substation.getName() + ", in YukonPAObject table failed.");
			return false;
		}

		//Added to YukonPAObject table, now add to CAPCONTROLSUBSTATION table
		rowsAffected = simpleJdbcTemplate.update(updateSql, substation.getVoltReductionPointId(), 
															substation.getMapLocationId(), 
															substation.getId());
		
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
		
		//remove any existing assignment
		unassignSubstation(substationId);
		
		int displayOrder = simpleJdbcTemplate.queryForInt(getDisplayOrderSql, areaId);
		int rowsAffected = simpleJdbcTemplate.update(assignStationSql, areaId,substationId,++displayOrder);
		
		boolean result = (rowsAffected == 1);
		return result;
	}
	
	@Override
	public boolean unassignSubstation(Substation substation) {
		return unassignSubstation(substation.getId());
	}

	@Override
	public boolean unassignSubstation(int substationId) {
		String unassignStationSql = "DELETE FROM CCSUBAREAASSIGNMENT WHERE SubstationBusID = ?";
		
		int rowsAffected = simpleJdbcTemplate.update(unassignStationSql,substationId);
		
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
    
	@SuppressWarnings("unchecked")
    @Override
	public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count) {
	    /* Get the unordered total count */
        int orphanCount = simpleJdbcTemplate.queryForInt("SELECT COUNT(*) FROM CapControlSubstation where SubstationId not in (SELECT SubstationBusID FROM CCSubAreaAssignment)");
        
        /* Get the paged subset of cc objects */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Type").eq(PaoType.CAP_CONTROL_SUBSTATION);
        sql.append("    AND PAObjectID not in (SELECT SubstationBusID FROM CCSubAreaAssignment)");
        sql.append("ORDER BY PAOName");
        
        PagingResultSetExtractor orphanExtractor = new PagingResultSetExtractor(start, count, liteCapControlObjectRowMapper);
        simpleJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), orphanExtractor);
        
        List<LiteCapControlObject> unassignedSubstations = (List<LiteCapControlObject>) orphanExtractor.getResultList();
        
        SearchResult<LiteCapControlObject> searchResult = new SearchResult<LiteCapControlObject>();
        searchResult.setResultList(unassignedSubstations);
        searchResult.setBounds(start, count, orphanCount);
        
        return searchResult;
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
