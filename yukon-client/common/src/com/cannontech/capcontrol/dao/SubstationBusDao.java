package com.cannontech.capcontrol.dao;

import java.sql.SQLException;
import java.util.List;

import com.cannontech.capcontrol.model.CymePaoPoint;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.PointIdContainer;
import com.cannontech.capcontrol.model.Substation;
import com.cannontech.capcontrol.model.SubstationBus;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface SubstationBusDao {
        
    public final YukonRowMapper<CymePaoPoint> cymePaoPointRowMapper  = new YukonRowMapper<CymePaoPoint>(){

        @Override
        public CymePaoPoint mapRow(YukonResultSet rs) throws SQLException {
            int paoId = rs.getInt("PaObjectId");
            int pointId = rs.getInt("PointId");
            String typeStr = rs.getString("Type");
            PaoType paoType = PaoType.getForDbString(typeStr);
            
            PaoIdentifier paoPointId = new PaoIdentifier(paoId,paoType);
            
            String name = rs.getString("PaoName");
            
            return new CymePaoPoint(paoPointId,name,pointId);
        }
    };
    
    /**
     * This method returns a {@link SubstationBus} specified by an Id.
     * @param id the paoId of the {@link SubstationBus}
     * @return {@link SubstationBus} specified.
     */
    public SubstationBus findSubBusById(int id);
    
    /**
     * Get the PaoIdentifiers of all the sub buses in the system.
     */
    public List<PaoIdentifier> getAllSubstationBuses();
    
    /**
     * This method returns a list of LiteYukonPAObjects representing the {@link SubstationBus} objects
     * which don't have assignments.
     */
    public List<LiteYukonPAObject> getUnassignedBuses();
    
    public List<LiteCapControlObject> getOrphans();
    
    /**
     * This method assigns a {@link SubstationBus} to a {@link Substation} and
     * performs all necessary db change messaging.
     * @param subBus the Pao of the {@link SubstationBus} being assigned.
     * @param substationName the name of the {@link Substation} being assigned
     *            to.
     * @return true if the assignment occurred and only updated one row in the
     *         db, false otherwise.
     */
    public boolean assignSubstationBus(YukonPao subBus, String substationName);
    
    /**
     * This method assigns a {@link SubstationBus} to a {@link Substation} and
     * performs all necessary db change messaging.
     * @param substationBus the Pao of the {@link SubstationBus} being assigned.
     * @param substation the Pao of the {@link Substation} being assigned to.
     * @return true if the assignment occurred and only updated one row in the
     *         db, false otherwise.
     */
    public boolean assignSubstationBus(YukonPao substation, YukonPao substationBus);
    
    /**
     * This method removes all assignments for a given {@link SubstationBus}.
     * @param substationBus the Pao of the {@link SubstationBus}.
     * @return true if the unassignment occurred and only updated one row in the
     *         db, false otherwise.
     */
    public boolean unassignSubstationBus(YukonPao substationBus);
    

    public List<CymePaoPoint> getBankStatusPointPaoIdsBySubbusId(int substationBusId);
    
    /**
     * Returns a container with the PointId's selected by the pickers for the substation bus with the given Pao Id.
     * @param substationBusId
     * @return
     */
    public PointIdContainer getSubstationBusPointIds(int substationBusId);
    
    /**
     * Returns a list of feeder names from the bus
     * 
     * @param subbusId
     * @return
     */
    public List<String> getAssignedFeederPaoNames(int subbusId);
    
    /**
     * Returns a list of Regulator ids that are assigned to the bus of the given id.
     */
    List<Integer> getRegulatorsForBus(int id);

}