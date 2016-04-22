package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.model.FeederPhaseData;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.PointIdContainer;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.impl.LitePaoRowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;

public class FeederDaoImpl implements FeederDao {
    
    @Autowired private PaoDao paoDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public List<LiteYukonPAObject> getUnassignedFeeders() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT PAO.PAObjectID, PAO.Category, PAO.PAOName,");
        sql.append("    PAO.Type, PAO.PAOClass, PAO.Description, PAO.DisableFlag,");
        sql.append("    D.PORTID, DCS.ADDRESS, DR.routeid"); 
        sql.append("FROM YukonPaObject PAO");
        sql.append("    LEFT OUTER JOIN DeviceDirectCommSettings D ON PAO.PAObjectID = D.DeviceID");
        sql.append("    LEFT OUTER JOIN DeviceCarrierSettings DCS ON PAO.PAObjectID = DCS.DeviceID"); 
        sql.append("    LEFT OUTER JOIN DeviceRoutes DR ON PAO.PAObjectID = DR.DeviceID");
        sql.append("    JOIN CapControlFeeder CCF ON PAO.PAObjectID = CCF.FeederID");
        sql.append("WHERE PAO.PAObjectID NOT IN");
        sql.append(   "(SELECT FeederId");
        sql.append(   " FROM CCFeederSubAssignment)");
        
        List<LiteYukonPAObject> feeders = yukonJdbcTemplate.query(sql, new LitePaoRowMapper());
        
        return feeders;
    }

    @Override
    public FeederPhaseData getFeederPhaseData(int feederId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CurrentVarLoadPointId, PhaseB, PhaseC, UsePhaseData");
        sql.append("FROM CapControlFeeder");
        sql.append("WHERE FeederId").eq(feederId);
        
        RowMapper<FeederPhaseData> mapper = 
                new RowMapper<FeederPhaseData>() {
            
            @Override
            public FeederPhaseData mapRow(ResultSet rs, int rowNum) throws SQLException {
                int currentVarLoadPointId = rs.getInt("CurrentVarLoadPointId");
                int phaseB = rs.getInt("PhaseB");
                int phaseC = rs.getInt("PhaseC");
                boolean usePhaseData = ("Y").equals(rs.getString("UsePhaseData"));
                
                return new FeederPhaseData(currentVarLoadPointId, phaseB, phaseC, usePhaseData);
            }
        };
        
        return yukonJdbcTemplate.queryForObject(sql, mapper);
    }
    
    @Override
    public List<LiteCapControlObject> getOrphans() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Type").eq(PaoType.CAP_CONTROL_FEEDER);
        sql.append("    AND PAObjectID not in (SELECT FeederId FROM CCFeederSubAssignment)");
        sql.append("ORDER BY PAOName");
        
        List<LiteCapControlObject> orphans = yukonJdbcTemplate.query(sql, CapbankControllerDao.LITE_ORPHAN_MAPPER);
        
        return orphans;
    }
    
    /**
     * This method returns the SubBus ID that owns the given feeder ID.
     */
    @Override
    public int getParentSubBusID( int feederID ) throws EmptyResultDataAccessException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT SubstationBusID");
        sql.append("FROM CCFeederSubAssignment");
        sql.append("WHERE FeederId").eq(feederID);

        return yukonJdbcTemplate.queryForInt(sql);
    }
   
    @Override
    public boolean assignFeeder(YukonPao feeder, String subBusName) {
        YukonPao substationBus = paoDao.findYukonPao(subBusName, PaoType.CAP_CONTROL_SUBBUS);
        return (substationBus == null) ? false : assignFeeder(substationBus, feeder);
    };

    @Override
    public boolean assignFeeder(YukonPao substationBus, YukonPao feeder) {
        int substationBusId = substationBus.getPaoIdentifier().getPaoId();
        SqlStatementBuilder displaySql = new SqlStatementBuilder();

        displaySql.append("SELECT MAX(DisplayOrder)");
        displaySql.append("FROM CCFeederSubAssignment");
        displaySql.append("WHERE SubStationBusID").eq(substationBusId);

        int displayOrder = yukonJdbcTemplate.queryForInt(displaySql);

        // remove any existing assignment
        unassignFeeder(feeder);

        SqlStatementBuilder assignSql = new SqlStatementBuilder();
        SqlParameterSink params = assignSql.insertInto("CCFeederSubAssignment");

        params.addValue("SubStationBusID", substationBusId);
        params.addValue("FeederID", feeder.getPaoIdentifier().getPaoId());
        params.addValue("DisplayOrder", ++displayOrder);

        int rowsAffected = yukonJdbcTemplate.update(assignSql);

        boolean result = (rowsAffected == 1);

        if (result) {
            dbChangeManager.processPaoDbChange(feeder, DbChangeType.UPDATE);
            dbChangeManager.processPaoDbChange(substationBus, DbChangeType.UPDATE);
        }

        return result;
    }

    @Override
    public boolean unassignFeeder(YukonPao feeder) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("DELETE FROM CCFeederSubAssignment");
        sql.append("WHERE FeederID").eq(feeder.getPaoIdentifier().getPaoId());

        int rowsAffected = yukonJdbcTemplate.update(sql);

        boolean result = (rowsAffected == 1);
        return result;
    }

    @Override
    public PointIdContainer getFeederPointIds(int feederId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
                
        sql.append("SELECT CurrentVarLoadPointId, CurrentWattLoadPointId, CurrentVoltLoadPointId, UsePhaseData, PhaseB, PhaseC");
        sql.append("FROM CapControlFeeder");
        sql.append("WHERE FeederId").eq(feederId);
            
        PointIdContainer points = yukonJdbcTemplate.queryForObject(sql, pointIdContainerMapper);
            
        return points;
    }
}