package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.cbc.dao.FeederDao;
import com.cannontech.cbc.model.Feeder;
import com.cannontech.cbc.model.SubstationBus;
import com.cannontech.cbc.point.CBCPointFactory;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.util.Validator;

public class FeederDaoImpl implements FeederDao {
	
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    
    private static final ParameterizedRowMapper<Feeder> rowMapper;
    private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    
    static {
        insertSql = "INSERT INTO CapControlFeeder (FeederID," + 
        "CurrentVarLoadPointID,CurrentWattLoadPointID,MapLocationID,CurrentVoltLoadPointID," + 
        "MultiMonitorControl,usephasedata,phaseb,phasec, ControlFlag) VALUES (?,?,?,?,?,?,?,?,?,?)";
        
        removeSql = "DELETE FROM CapControlFeeder WHERE FeederID = ?";
        
        updateSql = "UPDATE CapControlFeeder SET CurrentVarLoadPointID = ?," + 
        "CurrentWattLoadPointID = ?,MapLocationID = ?,CurrentVoltLoadPointID = ?, " + 
        "MultiMonitorControl = ?, usephasedata = ?,phaseb = ?, phasec = ?, ControlFlag = ? WHERE FeederID = ?";
        
        selectAllSql = "SELECT yp.PAOName,FeederID,CurrentVarLoadPointID,CurrentWattLoadPointID," + 
        "MapLocationID,CurrentVoltLoadPointID,MultiMonitorControl,usephasedata," + 
        "phaseb,phasec,ControlFlag FROM CapControlFeeder, YukonPAObject yp ";
        
        selectByIdSql = selectAllSql + " WHERE FeederID = yp.PAObjectID AND FeederID = ?";
        
        rowMapper = FeederDaoImpl.createRowMapper();
        liteCapControlObjectRowMapper = CapbankControllerDaoImpl.createLiteCapControlObjectRowMapper();
    }

    private static final ParameterizedRowMapper<Feeder> createRowMapper() {
        ParameterizedRowMapper<Feeder> rowMapper = new ParameterizedRowMapper<Feeder>() {
            public Feeder mapRow(ResultSet rs, int rowNum) throws SQLException {
                Feeder feeder = new Feeder();
                feeder.setId(rs.getInt("FeederID"));
                feeder.setCurrentVarLoadPointId(rs.getInt("CurrentVarLoadPointID"));
                feeder.setCurrentWattLoadPointId(rs.getInt("CurrentWattLoadPointID"));
                feeder.setMapLocationId(rs.getString("MapLocationID"));
                feeder.setCurrentVoltLoadPointId(rs.getInt("CurrentVoltLoadPointID"));
                String data = rs.getString("MultiMonitorControl");
                Validator.isNotNull(data);
                feeder.setMultiMonitorControl(data);
                data = rs.getString("usephasedata");
                Validator.isNotNull(data);
                feeder.setUsePhaseData(data);
                feeder.setPhaseb(rs.getInt("phaseb"));
                feeder.setPhasec(rs.getInt("phasec"));
                data = rs.getString("ControlFlag");
                Validator.isNotNull(data);
                feeder.setControlFlag(data);
                return feeder;
            }
        };
        return rowMapper;
    }

    @Override
    public void add(Feeder feeder) {
    	int newPaoId = nextValueHelper.getNextValue("YukonPaObject");
    	
		YukonPAObject pao = new YukonPAObject();
		pao.setPaObjectID(newPaoId);
		pao.setCategory(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoClass(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoName(feeder.getName());
		pao.setType(CapControlType.FEEDER.getDbValue());
		pao.setDescription(feeder.getDescription());
		pao.setDisableFlag(feeder.getDisabled()?'Y':'N');
		try {
		    Transaction.createTransaction(com.cannontech.database.Transaction.INSERT, pao).execute();
		} catch (TransactionException e ) { 
		    throw new DataIntegrityViolationException("Insert of Feeder, " + feeder.getName() + ", in YukonPAObject table failed.", e);
		}

		//Added to YukonPAObject table, now add to CapControlFeeder
		feeder.setId(pao.getPaObjectID());
		int rowsAffected = simpleJdbcTemplate.update(insertSql, feeder.getId(),
                                                         feeder.getCurrentVarLoadPointId(),
                                                         feeder.getCurrentWattLoadPointId(),
                                                         feeder.getMapLocationId(),
                                                         feeder.getCurrentVoltLoadPointId(),
                                                         feeder.getMultiMonitorControl(),
                                                         feeder.getUsePhaseData(),
                                                         feeder.getPhaseb(),
                                                         feeder.getPhasec(),
                                                         feeder.getControlFlag());
        boolean result = (rowsAffected == 1);
        
		if (result == false) {
			CTILogger.debug("Insert of Feeder, " + feeder.getName() + ", in CapControlFeeder table failed.");
		}
		
		SmartMultiDBPersistent smartMulti = CBCPointFactory.createPointsForPAO(pao.getPaObjectID(),pao.getDbConnection());
		
		try {
			Transaction.createTransaction(com.cannontech.database.Transaction.INSERT, smartMulti).execute();
		} catch (TransactionException e) {
			CTILogger.error("Inserting Points for Feeder, " + feeder.getName() + " failed.");
		}
		
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Feeder getById(int id) {
        Feeder f = simpleJdbcTemplate.queryForObject(selectByIdSql, rowMapper, id);
        return f;
    }
    
    @Override
    public boolean remove(Feeder feeder) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql,feeder.getId());
        boolean result = (rowsAffected == 1);
        
        //TODO Delete Points on Feeder
        
        if (result) {
    		YukonPAObject pao = new YukonPAObject();
    		pao.setPaObjectID(feeder.getId());
    		try {
    			Transaction.createTransaction(com.cannontech.database.Transaction.DELETE, pao).execute();
    		} catch (TransactionException e) {
    			CTILogger.error("Removal of Feeder, " + feeder.getName() + ", in YukonPAObject table failed.");
    			return false;
    		} 	
        }
		
        return result;
    }
    
    @Override
    public boolean update(Feeder feeder) {
		int rowsAffected = 0;
		YukonPAObject pao = new YukonPAObject();
		
		pao.setCategory(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoClass(PAOGroups.STRING_CAT_CAPCONTROL);
		pao.setPaoName(feeder.getName());
		pao.setType(CapControlType.FEEDER.getDbValue());
		pao.setDescription(feeder.getDescription());
		pao.setPaObjectID(feeder.getId());
		pao.setDisableFlag(feeder.getDisabled()?'Y':'N');
		
		try {
			Transaction.createTransaction(com.cannontech.database.Transaction.UPDATE, pao).execute();
		} catch (TransactionException e) {
			CTILogger.error("Update of Feeder, " + feeder.getName() + ", in YukonPAObject table failed.");
			return false;
		}

        rowsAffected = simpleJdbcTemplate.update(updateSql, feeder.getCurrentVarLoadPointId(),
                                                     feeder.getCurrentWattLoadPointId(),
                                                     feeder.getMapLocationId(),
                                                     feeder.getCurrentVoltLoadPointId(),
                                                     feeder.getMultiMonitorControl(),
                                                     feeder.getUsePhaseData(),
                                                     feeder.getPhaseb(),
                                                     feeder.getPhasec(),
                                                     feeder.getControlFlag(),
                                                     feeder.getId());
        boolean result = (rowsAffected == 1);
        
		if (result == false) {
			CTILogger.debug("Insert of Feeder, " + feeder.getName() + ", in CapControlFeeder table failed.");
		}
		
        return result;
    }

    /**
     * This method returns all the Feeder IDs that are not assigned
     *  to a SubBus.
     */
    public List<Integer> getUnassignedFeederIds() {
        String sql = "SELECT FeederID FROM  CapControlFeeder where " +
        " FeederID not in (select FeederID from CCFeederSubAssignment) ORDER BY FeederID";

        ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
                Integer i = new Integer ( rs.getInt("FeederID") );
                return i;
            }
        };
        
        List<Integer> listmap = simpleJdbcTemplate.query(sql, mapper);
        return listmap;
    }

	@SuppressWarnings("unchecked")
    @Override
	public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count) {
	    /* Get the unordered total count */
        int orphanCount = simpleJdbcTemplate.queryForInt("SELECT COUNT(*) FROM CapControlFeeder where FeederId not in (SELECT FeederId FROM CCFeederSubAssignment)");
        
        /* Get the paged subset of cc objects */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Type").eq(PaoType.CAP_CONTROL_FEEDER);
        sql.append("    AND PAObjectID not in (SELECT FeederId FROM CCFeederSubAssignment)");
        sql.append("ORDER BY PAOName");
        
        PagingResultSetExtractor orphanExtractor = new PagingResultSetExtractor(start, count, liteCapControlObjectRowMapper);
        simpleJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), orphanExtractor);
        
        List<LiteCapControlObject> unassignedFeeders = (List<LiteCapControlObject>) orphanExtractor.getResultList();
        
        SearchResult<LiteCapControlObject> searchResult = new SearchResult<LiteCapControlObject>();
        searchResult.setResultList(unassignedFeeders);
        searchResult.setBounds(start, count, orphanCount);
        
        return searchResult;
	}
    
    /**
     * This method returns the SubBus ID that owns the given feeder ID.
     */
    public int getParentSubBusID( int feederID ) throws EmptyResultDataAccessException {
    
        String sql = "SELECT SubStationBusID FROM CCFeederSubAssignment where FeederID = ?"; 

        return simpleJdbcTemplate.queryForInt(sql,feederID);
    }
    
	@Override
	public boolean assignFeeder(SubstationBus substationBus,Feeder feeder) {
		return assignFeeder(substationBus.getId(),feeder.getId());
	}

	@Override
	public boolean assignFeeder(int substationBusId, int feederId) {
    	String getDisplayOrderSql = "SELECT max(DisplayOrder) FROM CCFeederSubAssignment WHERE SubStationBusID = ?";
    	String insertAssignmentSql = "INSERT INTO CCFeederSubAssignment (SubStationBusID,FeederID,DisplayOrder) VALUES (?,?,?)";
    	
		//remove any existing assignment
    	unassignFeeder(feederId);
    	
		int displayOrder = simpleJdbcTemplate.queryForInt(getDisplayOrderSql, substationBusId);
		int rowsAffected = simpleJdbcTemplate.update(insertAssignmentSql, substationBusId,feederId,++displayOrder);
		
		boolean result = (rowsAffected == 1);
		return result;
	}

	@Override
	public boolean unassignFeeder(Feeder feeder) {
		return unassignFeeder(feeder.getId());
	}

	@Override
	public boolean unassignFeeder(int feederId) {
    	String deleteAssignmentSql = "DELETE FROM CCFeederSubAssignment WHERE FeederID = ?";   	
		int rowsAffected = simpleJdbcTemplate.update(deleteAssignmentSql,feederId);
		
		boolean result = (rowsAffected == 1);
		return result;
	}

	@Override
	public int getParentId(Feeder feeder) {
		String getParentSql = "SELECT SubStationBusID FROM CCFeederSubAssignment WHERE FeederID= ?";
		
		int id = simpleJdbcTemplate.queryForInt(getParentSql, feeder.getId());
		return id;
	}
    
    @Autowired
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
		this.nextValueHelper = nextValueHelper;
	}

}