package com.cannontech.web.capcontrol.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.model.CompleteCapControlSubstation;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.capcontrol.CapControlSubstation;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.capcontrol.CCSubstationSubBusList;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.capcontrol.models.Assignment;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.models.ViewableFeeder;
import com.cannontech.web.capcontrol.models.ViewableSubBus;
import com.cannontech.web.capcontrol.service.BusService;
import com.cannontech.web.capcontrol.service.FeederService;
import com.cannontech.web.capcontrol.service.SubstationService;
import com.cannontech.web.capcontrol.util.service.CapControlWebUtilsService;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class SubstationServiceImpl implements SubstationService {

    @Autowired private IDatabaseCache dbCache;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private SubstationBusDao busDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private CapControlCache ccCache;
    @Autowired private CapControlWebUtilsService ccWebUtilsService;
    @Autowired private FeederService feederService;
    @Autowired private BusService busService;
    @Autowired private PaoPersistenceService paoPersistenceService;

    @Override
    public CapControlSubstation get(int id) {

        assertSubstationExists(id);

        CapControlSubstation sub = new CapControlSubstation();
        sub.setCapControlPAOID(id);
        sub = (CapControlSubstation) dbPersistentDao.retrieveDBPersistent(sub);
        return sub;
    }

    @Override
    public int save(CapControlSubstation sub) {
        if (sub.getId() == null) {
            create(sub);
        } else {
            assertSubstationExists(sub.getId());
            dbPersistentDao.performDBChange(sub,  TransactionType.UPDATE);
        }

        return sub.getId();
    }
    
    private int create(CapControlSubstation substation) {
        CompleteCapControlSubstation completeSubstation = new CompleteCapControlSubstation();
        completeSubstation.setPaoName(substation.getName());
        completeSubstation.setDisabled(substation.isDisabled());
        completeSubstation.setMapLocationId(substation.getCapControlSubstation().getMapLocationID());
        completeSubstation.setDescription(substation.getGeoAreaName());
        completeSubstation.setVoltReductionPointId(substation.getCapControlSubstation().getVoltReductionPointId());
        paoPersistenceService.createPaoWithDefaultPoints(completeSubstation, substation.getPaoType());
        substation.setId(completeSubstation.getPaObjectId());
        return substation.getId();
    }

    @Override
    public void delete(int substationId) {
        CapControlSubstation substation = get(substationId);
        dbPersistentDao.performDBChange(substation, TransactionType.DELETE);
    }
    
    @Override
    public List<ViewableSubBus> getBusesForSubstation(int substationId) {

        List<ViewableSubBus> viewableBuses = new ArrayList<ViewableSubBus>();
        try {
            SubStation cachedSub = ccCache.getSubstation(substationId);
            List<SubBus> capBuses = ccCache.getSubBusesBySubStation(cachedSub);
            viewableBuses = ccWebUtilsService.createViewableSubBus(capBuses);

        } catch (NotFoundException e) {
             //substation is an orphan, we must manually retrieve its buses) 
            CapControlSubstation sub = get(substationId);
            List<CCSubstationSubBusList> busList = sub.getChildList();

            viewableBuses = busList.stream()
                .map(new Function<CCSubstationSubBusList, ViewableSubBus>(){

                    @Override
                    public ViewableSubBus apply(CCSubstationSubBusList capBusItem) {

                        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(capBusItem.getSubstationBusID());

                        ViewableSubBus capBus = new ViewableSubBus();
                        capBus.setCcId(capBusItem.getSubstationBusID());
                        capBus.setCcName(pao.getPaoName());

                        return capBus;
                    }
                })
            .collect(Collectors.toList());
        }

        return viewableBuses;
    }
    

    @Override
    public List<ViewableFeeder> getFeedersForSubBuses(List<ViewableSubBus> subBuses) {
        List<ViewableFeeder> viewableFeeders = new ArrayList<ViewableFeeder>();

        for (ViewableSubBus bus : subBuses) {
            viewableFeeders.addAll(busService.getFeedersForBus(bus.getCcId()));
        }
        
        return viewableFeeders;
    }
    
    @Override
    public List<ViewableCapBank> getCapBanksForFeeders(List<ViewableFeeder> feeders) {
        List<ViewableCapBank> viewableCapBanks = new ArrayList<ViewableCapBank>();

        for (ViewableFeeder feeder : feeders) {
            viewableCapBanks.addAll(feederService.getCapBanksForFeeder(feeder.getCcId()));
        }
        
        return viewableCapBanks;
    }
    
    @Override
    public List<Assignment> getAssignedBusesFor(int substationId) {
        List<Assignment> assigned = new ArrayList<Assignment>();
        CapControlSubstation sub = get(substationId);
        List<CCSubstationSubBusList> busList = sub.getChildList();
        assigned = busList.stream()
                .map(new Function<CCSubstationSubBusList, Assignment>(){

                    @Override
                    public Assignment apply(CCSubstationSubBusList bus) {

                        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(bus.getSubstationBusID());
                        return Assignment.of(bus.getSubstationBusID(), pao.getPaoName());
                    }
                })
            .collect(Collectors.toList());

        return assigned;
    }

    @Override
    public List<Assignment> getUnassignedBuses() {

        List<LiteYukonPAObject> unassignedBuses = busDao.getUnassignedBuses();

        List<Assignment> unassigned = unassignedBuses.stream()
            .map(new Function<LiteYukonPAObject, Assignment>(){

                @Override
                public Assignment apply(LiteYukonPAObject bus) {

                    return Assignment.of(bus.getPaoIdentifier().getPaoId(), bus.getPaoName());
                }})
            .collect(Collectors.toList());
        return unassigned;
    }

    /**
     * @throws NotFoundException if the given id is not a substation
     */
    private void assertSubstationExists(int id) throws NotFoundException {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        if (pao == null || pao.getPaoType() != PaoType.CAP_CONTROL_SUBSTATION) {
            throw new NotFoundException("No substation with id " + id + " found.");
        }
    }
    
    @Override
    public void assignBus(int substationId, int busId) {
        busDao.assignBus(substationId, busId);
        dbChangeManager.processPaoDbChange(PaoIdentifier.of(substationId, PaoType.CAP_CONTROL_SUBSTATION), DbChangeType.UPDATE);
    }

    @Override
    public void assignBuses(int substationId, Iterable<Integer> busIds) {
        busDao.assignBuses(substationId, busIds);
        dbChangeManager.processPaoDbChange(PaoIdentifier.of(substationId, PaoType.CAP_CONTROL_SUBSTATION), DbChangeType.UPDATE);
    }

}
