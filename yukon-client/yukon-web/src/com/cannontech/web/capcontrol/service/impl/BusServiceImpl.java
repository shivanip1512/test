package com.cannontech.web.capcontrol.service.impl;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.model.CompleteCapControlSubstationBus;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.schedule.dao.PaoScheduleDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.pao.PAOScheduleAssign;
import com.cannontech.database.db.pao.PaoScheduleAssignment;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.capcontrol.models.Assignment;
import com.cannontech.web.capcontrol.models.ViewableFeeder;
import com.cannontech.web.capcontrol.service.BusService;
import com.cannontech.web.capcontrol.util.service.CapControlWebUtilsService;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class BusServiceImpl implements BusService {

    @Autowired private CapControlCache ccCache;
    @Autowired private CapControlWebUtilsService ccWebUtilsService;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private FeederDao feederDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private PaoScheduleDao paoScheduleDao;
    @Autowired private SubstationBusDao busDao;
    @Autowired private DBPersistentDao dbPersistentDao;

    @Override
    public CapControlSubBus get(int id) {

        assertBusExists(id);

        CompleteCapControlSubstationBus completeBus = new CompleteCapControlSubstationBus();
        completeBus.setPaoIdentifier(PaoIdentifier.of(id, PaoType.CAP_CONTROL_SUBBUS));
        completeBus = paoPersistenceService.retreivePao(completeBus, CompleteCapControlSubstationBus.class);

        CapControlSubBus bus = CapControlSubBus.of(completeBus);

        List<PaoScheduleAssignment> schedules = paoScheduleDao.getByPaoId(id);

        List<PAOScheduleAssign> assigns = schedules.stream()
           .map(new Function<PaoScheduleAssignment, PAOScheduleAssign>(){

                @Override
                public PAOScheduleAssign apply(PaoScheduleAssignment assignment) {
                    return PAOScheduleAssign.of(assignment);
                }
            })
           .collect(Collectors.toList());

        /*
         * TODO JAVA 8
         *
         * .map(assign -> PAOScheduleAssign.of(assign))
         */

        bus.setSchedules(assigns);
        return bus;
    }

    @Override
    public int save(CapControlSubBus bus) {

        CompleteCapControlSubstationBus completeBus = bus.asCompletePao();

        if (bus.getId() == null) {
            paoPersistenceService.createPaoWithDefaultPoints(completeBus, bus.getPaoType());
        } else {
            assertBusExists(bus.getId());
            paoPersistenceService.updatePao(completeBus);
        }

        return completeBus.getPaObjectId();
    }
    
    @Override
    public void saveSchedules(CapControlSubBus bus) {

        List<PaoScheduleAssignment> assignments = bus.getSchedules().stream()
            .filter(new Predicate<PAOScheduleAssign>() {
                @Override
                public boolean test(PAOScheduleAssign assign) {
                    return !StringUtils.isEmpty(assign.getCommand()) && assign.getScheduleID() != -1;
                }
            })
            .map(new Function<PAOScheduleAssign, PaoScheduleAssignment>() {

                @Override
                public PaoScheduleAssignment apply(PAOScheduleAssign assign) {
                    assign.setPaoID(bus.getId());
                    PaoScheduleAssignment result = assign.asPaoScheduleAssignment();
                    return result;
            }})
            .collect(Collectors.toList());

        paoScheduleDao.deleteAssignmentsForPao(bus.getId());
        paoScheduleDao.assignCommand(assignments);

    }

    @Override
    public void delete(int busId) {
        CapControlSubBus bus = get(busId);
        dbPersistentDao.performDBChange(bus, TransactionType.DELETE);
    }
    
    @Override
    public List<ViewableFeeder> getFeedersForBus(int busId) {

        List<ViewableFeeder> viewableFeeders;
        try {
            ccCache.getSubBus(busId);
            List<Feeder> feeders = ccCache.getFeedersBySubBus(busId);
            viewableFeeders = ccWebUtilsService.createViewableFeeder(feeders);

        } catch (NotFoundException e) {
            /* Bus is an orphan, we must manually retrieve its feeders) */
            List<Integer> feederIds = busDao.getFeederIds(busId);

            viewableFeeders = feederIds.stream()
                .map(new Function<Integer, ViewableFeeder>(){

                    @Override
                    public ViewableFeeder apply(Integer feederId) {

                        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(feederId);

                        ViewableFeeder feeder = new ViewableFeeder();
                        feeder.setCcId(feederId);
                        feeder.setCcName(pao.getPaoName());
                        feeder.setParentId(busId);


                        return feeder;
                    }
                })
            .collect(Collectors.toList());
        }

        return viewableFeeders;
    }
    
    @Override
    public List<Assignment> getAssignedFeedersFor(int busId) {
        List<Integer> feederIds = busDao.getFeederIds(busId);

        List<Assignment> assigned = feederIds.stream()
            .map(new Function<Integer, Assignment>(){

                @Override
                public Assignment apply(Integer id) {
                    LiteYukonPAObject feeder = dbCache.getAllPaosMap().get(id);

                    return Assignment.of(id, feeder.getPaoName());
                }})
            .collect(Collectors.toList());
        return assigned;
    }

    @Override
    public List<Assignment> getUnassignedFeeders() {

        List<LiteYukonPAObject> unassignedFeeders = feederDao.getUnassignedFeeders();

        List<Assignment> unassigned = unassignedFeeders.stream()
            .map(new Function<LiteYukonPAObject, Assignment>(){

                @Override
                public Assignment apply(LiteYukonPAObject feeder) {

                    return Assignment.of(feeder.getPaoIdentifier().getPaoId(), feeder.getPaoName());
                }})
            .collect(Collectors.toList());
        return unassigned;
    }

    @Override
    public void assignFeeders(int busId, Iterable<Integer> feederIds) {
        busDao.assignFeeders(busId, feederIds);
        dbChangeManager.processPaoDbChange(
            PaoIdentifier.of(busId, PaoType.CAP_CONTROL_SUBBUS), DbChangeType.UPDATE);
    }

    /**
     * @throws NotFoundException if the given id is not a bus
     */
    private void assertBusExists(int id) throws NotFoundException {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        if (pao == null || pao.getPaoType() != PaoType.CAP_CONTROL_SUBBUS) {
            throw new NotFoundException("No bus with id " + id + " found.");
        }
    }
}
