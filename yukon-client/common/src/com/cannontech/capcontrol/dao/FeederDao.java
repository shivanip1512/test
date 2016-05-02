package com.cannontech.capcontrol.dao;

import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.capcontrol.model.FeederPhaseData;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.PointIdContainer;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonPAObject;

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
     * This method returns all the Feeders that are not assigned to a SubBus.
     */
    public List<LiteYukonPAObject> getUnassignedFeeders();
    
    /**
     * Returns the phase data pointIds for the feeder id provided.
     * @param feederId the paoId of the feeder
     * @return a {@link FeederPhaseData} object populated with the Feeder's data 
     *      from the database.
     */
    public FeederPhaseData getFeederPhaseData(int feederId);
    
    public List<LiteCapControlObject> getOrphans();
    
    /**
     * This method returns the SubBus ID that owns the given feeder ID. If no
     * parent is found, CtiUtilities.NONE_ZERO_ID is returned.
     */
    public int getParentSubBusID( int feederID ) throws EmptyResultDataAccessException;
    
    /**
     * This method assigns a feeder to a subbus and does all necessary db change
     * messaging.
     * @param feeder the YukonPao of the feeder being assigned
     * @param subBusName the name of the subbus being assigned to.
     * @return true if the assignment occurred and only updated one row in the
     *         db, false otherwise.
     */
    public boolean assignFeeder(YukonPao feeder, String subBusName);

    /**
     * This method assigns a feeder to a subbus and does all necessary db change
     * messaging.
     * @param feeder the YukonPao of the feeder being assigned
     * @param substationBus the YukonPao of the subbus being assigned to.
     * @return true if the assignment occurred and only updated one row in the
     *         db, false otherwise.
     */
    public boolean assignFeeder(YukonPao substationBus, YukonPao feeder);

    /**
     * This method removes all assignments for a given feeder.
     * @param feeder the YukonPao of the feeder.
     * @return true if the unassignment occurred and only updated one row in the
     *         db, false otherwise.
     */
    public boolean unassignFeeder(YukonPao feeder);
    
    /**
     * Returns a container with the PointId's selected by the pickers for the feeder with the given Pao Id.
     * @param feederId
     * @return
     */
    public PointIdContainer getFeederPointIds(int feederId);
    /**
     * This method assigns a feeder to a subbus
     * @param substationBusId the id of the bus being assigned to
     * @param feederId the id of the feeder to assign
     * @return true if the assignment occurred and only updated one row in the
     *         db, false otherwise.
     */
    boolean assignFeeder(int substationBusId, int feederId);
}
