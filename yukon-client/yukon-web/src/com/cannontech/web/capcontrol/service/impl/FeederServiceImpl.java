package com.cannontech.web.capcontrol.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.capcontrol.dao.CapbankDao;
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
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.capcontrol.models.CapBankAssignment;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.service.FeederService;
import com.cannontech.web.capcontrol.util.service.CapControlWebUtilsService;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class FeederServiceImpl implements FeederService {

    @Autowired private CapControlCache ccCache;
    @Autowired private CapControlWebUtilsService ccWebUtilsService;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private CapbankDao capBankDao;
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
            ccCache.getFeeder(feederId);
            List<CapBankDevice> capBanks = ccCache.getCapBanksByFeeder(feederId);
            viewableCapBanks = ccWebUtilsService.createViewableCapBank(capBanks);

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

    /**
     * @throws NotFoundException if the given id is not a feeder
     */
    private void assertFeederExists(int id) throws NotFoundException {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        if (pao == null || pao.getPaoType() != PaoType.CAP_CONTROL_FEEDER) {
            throw new NotFoundException("No feeder with id " + id + " found.");
        }
    }

}
