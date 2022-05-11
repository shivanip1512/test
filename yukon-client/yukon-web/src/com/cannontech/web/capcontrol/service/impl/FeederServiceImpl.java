package com.cannontech.web.capcontrol.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.model.CompleteCapControlFeeder;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.capcontrol.CCFeederBankList;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.capcontrol.models.CapBankAssignment;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.service.FeederService;
import com.cannontech.web.capcontrol.util.service.CapControlWebUtilsService;
import com.cannontech.yukon.IDatabaseCache;

public class FeederServiceImpl implements FeederService {

    @Autowired private CapControlCache ccCache;
    @Autowired private CapControlWebUtilsService ccWebUtilsService;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private CapbankDao capBankDao;
    @Autowired private FeederDao feederDao;
    @Autowired private ZoneDao zoneDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private DBPersistentDao dbPersistentDao;

    @Override
    public CapControlFeeder get(int id) {

        assertFeederExists(id);
        
        CapControlFeeder feeder = new CapControlFeeder();
        feeder.setCapControlPAOID(id);
        feeder = (CapControlFeeder) dbPersistentDao.retrieveDBPersistent(feeder);
        return feeder;
    }

    @Override
    public int save(CapControlFeeder feeder) {
        if (feeder.getId() == null) {
            create(feeder);
        } else {
            assertFeederExists(feeder.getId());
            dbPersistentDao.performDBChange(feeder,  TransactionType.UPDATE);
        }

        return feeder.getId();
    }
    
    private int create(CapControlFeeder feeder) {
        CompleteCapControlFeeder completeFeeder = new CompleteCapControlFeeder();
        completeFeeder.setPaoName(feeder.getName());
        completeFeeder.setDisabled(feeder.isDisabled());
        completeFeeder.setMapLocationId(feeder.getCapControlFeeder().getMapLocationID());
        paoPersistenceService.createPaoWithDefaultPoints(completeFeeder, feeder.getPaoType());
        feeder.setId(completeFeeder.getPaObjectId());
        return feeder.getId();
    }

    @Override
    public boolean delete(int feederId) {
        CompleteCapControlFeeder completeFeeder = new CompleteCapControlFeeder();
        completeFeeder.setPaoIdentifier(PaoIdentifier.of(feederId, PaoType.CAP_CONTROL_FEEDER));
        completeFeeder = paoPersistenceService.retreivePao(completeFeeder, CompleteCapControlFeeder.class);
        paoPersistenceService.deletePao(completeFeeder);
        return true;
    }
    
    @Override
    public List<ViewableCapBank> getCapBanksForFeeder(int feederId) {

        List<ViewableCapBank> viewableCapBanks = new ArrayList<ViewableCapBank>();
        try {
            Feeder feeder = ccCache.getFeeder(feederId);
            SubBus subBus = ccCache.getParentSubBus(feederId);
            List<CapBankDevice> capBanks = ccCache.getCapBanksByFeeder(feederId);
            Collections.sort(capBanks, CapControlUtils.BANK_CONTROL_ORDER_COMPARATOR);
            viewableCapBanks = ccWebUtilsService.createViewableCapBank(capBanks);
            viewableCapBanks.forEach(capBank -> capBank.setUserPerPhaseData(feeder.getUsePhaseData() || subBus.getUsePhaseData()));
        } catch (NotFoundException e) {
             //Feeder is an orphan, we must manually retrieve its cap banks) 
            CapControlFeeder feeder = get(feederId);
            List<CCFeederBankList> bankList = feeder.getChildList();

            viewableCapBanks = bankList.stream()
                .map(new Function<CCFeederBankList, ViewableCapBank>(){

                    @Override
                    public ViewableCapBank apply(CCFeederBankList capBankItem) {

                        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(capBankItem.getDeviceID());

                        ViewableCapBank capBank = new ViewableCapBank();
                        capBank.setCcId(capBankItem.getDeviceID());
                        capBank.setCcName(pao.getPaoName());
                        capBank.setParentId(feederId);


                        return capBank;
                    }
                })
            .collect(Collectors.toList());
            viewableCapBanks.forEach(capBank -> capBank.setUserPerPhaseData(feeder.getCapControlFeeder().getUsePhaseDataBoolean()));
        }
        


        return viewableCapBanks;
    }
    
    @Override
    public List<CapBankAssignment> getAssignedCapBanksForFeeder(int feederId) {
        List<CapBankAssignment> assigned = new ArrayList<CapBankAssignment>();
        CapControlFeeder feeder = get(feederId);
        List<CCFeederBankList> bankList = feeder.getChildList();
        Collections.sort(bankList, CapControlUtils.BANK_DISPLAY_ORDER_COMPARATOR);
        assigned = bankList.stream()
                .map(new Function<CCFeederBankList, CapBankAssignment>(){

                    @Override
                    public CapBankAssignment apply(CCFeederBankList capBankItem) {

                        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(capBankItem.getDeviceID());
                        return CapBankAssignment.of(capBankItem.getDeviceID(), pao.getPaoName(), capBankItem.getControlOrder(), capBankItem.getTripOrder(), capBankItem.getCloseOrder());
                    }
                })
            .collect(Collectors.toList());

        return assigned;
    }

    @Override
    public List<CapBankAssignment> getUnassignedCapBanks() {

        List<LiteYukonPAObject> unassignedCapBanks = capBankDao.getUnassignedCapBanks();

        List<CapBankAssignment> unassigned = unassignedCapBanks.stream()
            .map(new Function<LiteYukonPAObject, CapBankAssignment>(){

                @Override
                public CapBankAssignment apply(LiteYukonPAObject feeder) {

                    return CapBankAssignment.of(feeder.getPaoIdentifier().getPaoId(), feeder.getPaoName());
                }})
            .collect(Collectors.toList());
        return unassigned;
    }

    @Override
    public void assignCapBanks(int feederId, List<Integer> capBankIds, List<Integer> closeOrders, List<Integer> tripOrders) {
        
        //first delete all assigned capbanks for the feeder
        capBankDao.unassignCapbanksForFeeder(feederId);
        LiteYukonPAObject feeder = dbCache.getAllPaosMap().get(feederId);
        for(Integer capBankId : capBankIds){
            LiteYukonPAObject capBank = dbCache.getAllPaosMap().get(capBankId);
            //find orders
            int controlOrder = capBankIds.indexOf(capBankId) + 1;
            int closeOrder = closeOrders.indexOf(capBankId) + 1;
            int tripOrder = tripOrders.indexOf(capBankId) + 1;
            capBankDao.assignAndOrderCapbank(feeder, capBank, controlOrder, closeOrder, tripOrder);
        }

        dbChangeManager.processPaoDbChange(
            PaoIdentifier.of(feederId, PaoType.CAP_CONTROL_FEEDER), DbChangeType.UPDATE);
    }

    @Override
    public boolean isCapBanksAssignedToZone(int feederId) throws EmptyResultDataAccessException, NotFoundException {

        Integer substationBusId = feederDao.getParentSubBusID(feederId);
        SubBus bus = ccCache.getSubBus(substationBusId);
        if (bus.getAlgorithm() == ControlAlgorithm.INTEGRATED_VOLT_VAR) {
            List<CapBankAssignment> capBankAssignments = getAssignedCapBanksForFeeder(feederId);
            List<Integer> feederCapBankIds = capBankAssignments.stream()
                                                               .map(e -> e.getId())
                                                               .collect(Collectors.toList());

            List<Integer> subBusCapbankIds = zoneDao.getCapBankIdsBySubBusId(substationBusId);

            if (!Collections.disjoint(subBusCapbankIds, feederCapBankIds)) {
                return true;
            }
        }

        return false;
    }
    
    @Override
    public boolean isFeederAssignedToVoltagePointForZone(int feederId) throws EmptyResultDataAccessException, NotFoundException {

        Integer substationBusId = feederDao.getParentSubBusID(feederId);
        SubBus bus = ccCache.getSubBus(substationBusId);
        if (bus.getAlgorithm() == ControlAlgorithm.INTEGRATED_VOLT_VAR) {
            List<Zone> zones = zoneDao.getZonesBySubBusId(substationBusId);
            for (Zone zone: zones) {
                List<PointToZoneMapping> pointMappings = zoneDao.getPointToZoneMappingByZoneId(zone.getId());
                Optional<PointToZoneMapping> feederPoint = pointMappings.stream()
                        .filter(point -> point.getFeederId() != null && point.getFeederId().equals(feederId))
                        .findFirst();
                if (feederPoint.isPresent()) {
                    return true;
                }
            }
        }

        return false;
    }
    
    @Override
    public boolean isFeederAssignedToRegulatorPointForZone(int feederId) throws EmptyResultDataAccessException, NotFoundException {

        Integer substationBusId = feederDao.getParentSubBusID(feederId);
        SubBus bus = ccCache.getSubBus(substationBusId);
        if (bus.getAlgorithm() == ControlAlgorithm.INTEGRATED_VOLT_VAR) {
            List<Zone> zones = zoneDao.getZonesBySubBusId(substationBusId);
            for (Zone zone: zones) {
                List<RegulatorToZoneMapping> regMappings = zoneDao.getRegulatorToZoneMappingsByZoneId(zone.getId());
                Optional<RegulatorToZoneMapping> feederPoint = regMappings.stream()
                        .filter(point -> point.getFeederId() != null && point.getFeederId().equals(feederId))
                        .findFirst();
                if (feederPoint.isPresent()) {
                    return true;
                }
            }
        }

        return false;
    }
    
    /**
     * @throws NotFoundException if the given id is not a feeder
     */
    private void assertFeederExists(int id) throws NotFoundException {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        if (pao == null || pao.getPaoType() != PaoType.CAP_CONTROL_FEEDER) {
            throw new NotFoundException("No feeder with id " + id + " found.");
        }
    }

    @Override
    public boolean isCapBanksAssignedToZone(List<Integer> availableCapBanksIds) {
        boolean isCapbankAssigned = false;
        List<CapBankAssignment> unassignedCapBanks = getUnassignedCapBanks();
        List<Integer> unassignedCapBanksId = unassignedCapBanks.stream().map(CapBankAssignment::getId)
                                                                        .collect(Collectors.toList());
        // Match the available capbank id with unassigned capbank ids to get the list of ids . Using 
        // these ids we will check if the capbank is assigned to some zone.
        List<Integer> filteredCapBankIds = availableCapBanksIds.stream().filter(id -> !unassignedCapBanksId.contains(id))
                                                                        .collect(Collectors.toList());
        if (!filteredCapBankIds.isEmpty()) {
            isCapbankAssigned = capBankDao.isCapBanksAssignedToZone(filteredCapBankIds);
        }
        return isCapbankAssigned;
    }

}
