package com.cannontech.capcontrol.dao;

import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.capcontrol.model.FeederPhaseData;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.PointIdContainer;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public interface FeederDao {
    
    public static YukonRowMapper<PointIdContainer> pointIdContainerMapper = new YukonRowMapper<PointIdContainer>() {

        @Override
        public PointIdContainer mapRow(YukonResultSet rs) throws SQLException {
            PointIdContainer pc = new PointIdContainer();
            
            pc.setTotalizekVar(!"Y".equalsIgnoreCase(rs.getString("UsePhaseData")));
            pc.setVarTotalId(rs.getInt("CurrentVarLoadPointId"));
            pc.setVarAId(rs.getInt("CurrentVarLoadPointId"));
            pc.setVarBId(rs.getInt("PhaseB"));
            pc.setVarCId(rs.getInt("PhaseC"));
            pc.setVoltId(rs.getInt("CurrentVoltLoadPointId"));
            pc.setWattId(rs.getInt("CurrentWattLoadPointId"));
            
            return pc;
        }
    };
    
    /**
     * This method returns all the Feeder IDs that are not assigned
     *  to a SubBus.
     */
    public List<Integer> getUnassignedFeederIds();
    
    /**
     * This method returns all the Feeder IDs that are assigned
     *  to the SubBus passed in.
     */
    public List<Integer> getFeederIdBySubstationBus(YukonPao subbus);
    
    /**
     * Returns the phase data pointIds for the feeder id provided.
     * @param feederId the paoId of the feeder
     * @return a {@link FeederPhaseData} object populated with the Feeder's data 
     *      from the database.
     */
    public FeederPhaseData getFeederPhaseData(int feederId);
    
    public SearchResult<LiteCapControlObject> getOrphans(int start, int count);
    
    /**
     * This method returns the SubBus ID that owns the given feeder ID.
     * If no parent is found, CtiUtilities.NONE_ZERO_ID is returned.
     */
    public int getParentSubBusID( int feederID ) throws EmptyResultDataAccessException;
    
    /**
     * This method assigns a feeder to a subbus and does all necessary db change messaging.
     * @param feederId the paoId of the feeder being assigned
     * @param subBusName the name of the subbus being assigned to.
     * @return true if the assignment occurred and only updated one row in the db, false otherwise.
     */
    public boolean assignFeeder(int feederId, String subBusName);
    
    /**
     * This method assigns a feeder to a subbus and does all necessary db change messaging.
     * @param feederId the paoId of the feeder being assigned
     * @param substationBusId the paoId of the subbus being assigned to.
     * @return true if the assignment occurred and only updated one row in the db, false otherwise.
     */
    public boolean assignFeeder(int substationBusId, int feederId);

    /**
     * This method removes all assignments for a given feeder.
     * @param feederId the paoId of the feeder.
     * @return true if the unassignment occurred and only updated one row in the db, false otherwise.
     */
    public boolean unassignFeeder(int feederId);
    
    /**
     * Returns a container with the PointId's selected by the pickers for the feeder with the given Pao Id.
     * @param feederId
     * @return
     */
    public PointIdContainer getFeederPointIds(int feederId);
}
