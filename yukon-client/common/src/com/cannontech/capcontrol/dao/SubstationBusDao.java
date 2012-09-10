package com.cannontech.capcontrol.dao;

import java.sql.SQLException;
import java.util.List;

import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.PointIdContainer;
import com.cannontech.capcontrol.model.PointPaoIdentifier;
import com.cannontech.capcontrol.model.Substation;
import com.cannontech.capcontrol.model.SubstationBus;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public interface SubstationBusDao {
    
    public final YukonRowMapper<PointPaoIdentifier> pointPaoRowMapper  = new YukonRowMapper<PointPaoIdentifier>(){

        @Override
        public PointPaoIdentifier mapRow(YukonResultSet rs) throws SQLException {
            PointPaoIdentifier bankPoint= new PointPaoIdentifier();

            int paoId = rs.getInt("PaObjectId");
            String typeStr = rs.getString("Type");
            PaoType paoType = PaoType.getForDbString(typeStr);
            bankPoint.setPaoIdentifier(new PaoIdentifier(paoId, paoType));
            
            String paoName = rs.getString("PaoName");
            bankPoint.setPaoName(paoName);
            
            int pointId = rs.getInt("PointId");
            bankPoint.setPointId(pointId);
            
            return bankPoint;
        }
    };
    
    /**
     * This method returns a {@link SubstationBus} specified by an Id.
     * @param id the paoId of the {@link SubstationBus}
     * @return {@link SubstationBus} specified.
     */
    public SubstationBus findSubBusById(int id);
    
    /**
     * This method returns a list of integers representing the {@link SubstationBus} objects
     * which don't have assignments.
     */
    public List<Integer> getAllUnassignedBuses();
    
    public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count);
    
    /**
     * This method assigns a {@link SubstationBus} to a {@link Substation} and performs all
     * necessary db change messaging.
     * @param subBusId the PaoId of the {@link SubstationBus} being assigned.
     * @param substationName the name of the {@link Substation} being assigned to.
     * @return true if the assignment occurred and only updated one row in the db, false otherwise.
     */
    public boolean assignSubstationBus(int subBusId, String substationName);
    
    /**
     * This method assigns a {@link SubstationBus} to a {@link Substation} and performs all
     * necessary db change messaging.
     * @param substationBusId the PaoId of the {@link SubstationBus} being assigned.
     * @param substationId the PaoId of the {@link Substation} being assigned to.
     * @return true if the assignment occurred and only updated one row in the db, false otherwise.
     */
    public boolean assignSubstationBus(int substationId, int substationBusId);
    
    /**
     * This method removes all assignments for a given {@link SubstationBus}.
     * @param substationBusId the PaoId of the {@link SubstationBus}.
     * @return true if the unassignment occurred and only updated one row in the db, false otherwise.
     */
    public boolean unassignSubstationBus(int substationBusId);
    

    public List<PointPaoIdentifier> getBankStatusPointPaoIdsBySubbusId(int substationBusId);
    
    public List<Integer> getBankStatusPointIdsBySubbusId(int substationBusId);
    
    /**
     * Returns a container with the PointId's selected by the pickers for the substation bus with the given Pao Id.
     * @param substationBusId
     * @return
     */
    public PointIdContainer getSubstationBusPointIds(int substationBusId);
}
