package com.cannontech.stars.dr.optout.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.optout.model.OptOutAction;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.model.OptOutEventState;
import com.cannontech.stars.dr.optout.model.OptOutLog;
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class MockOptOutEventDao implements OptOutEventDao {
    private Multimap<Integer, Integer> loadGroupToInventoryMap = ArrayListMultimap.create();
    private Set<Integer> optedOutInventory = Sets.newHashSet();
    
    public MockOptOutEventDao(Multimap<Integer, Integer> loadGroupToInventoryMap, Set<Integer> optedOutInventory) {
        this.loadGroupToInventoryMap = loadGroupToInventoryMap;
        this.optedOutInventory = optedOutInventory;
    }
    
    @Override
    public Set<Integer> getOptedOutInventoryByLoadGroups(Iterable<Integer> loadGroupIds) {
        Set<Integer> inventoryIds = Sets.newHashSet();
        for(Integer loadGroupId : loadGroupIds) {
            inventoryIds.addAll(loadGroupToInventoryMap.get(loadGroupId));
        }
        return getOptedOutInventory(inventoryIds);
    }

    @Override
    public Set<Integer> getOptedOutInventory(Iterable<Integer> inventoryIds) {
        Set<Integer> inventoryToSearch = Sets.newHashSet(inventoryIds);
        return Sets.intersection(optedOutInventory, inventoryToSearch);
    }
    
    /*
     * Unimplemented methods:
     */
    
    @Override
    public OptOutEvent getOptOutEventById(int eventId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public OptOutLog save(OptOutEvent event, OptOutAction action, LiteYukonUser user) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean isOptedOut(int inventoryId, int customerAccountId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<OptOutEventDto> getOptOutHistoryForAccount(int customerAccountId,
                                                           int... numberOfRecords) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Multimap<OptOutEventDto, OptOutLog> getOptOutEventDetails(Iterable<OptOutEventDto> optOutEvents) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<OverrideHistory> getOptOutHistoryForAccount(int accountId, Date startDate, Date stopDate,
                                                           Iterable<OptOutEventState> eventStates) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<OverrideHistory> getOptOutHistoryForAccount(int accountId,
                                                            ReadableInstant reportStartDate,
                                                            ReadableInstant stopDate,
                                                            LiteYukonGroup residentialGroup) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<OverrideHistory> getOptOutHistoryByLogUserId(int userId,
                                                             ReadableInstant reportStartDate,
                                                             ReadableInstant stopDate,
                                                             LiteYukonGroup residentialGroup) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<OverrideHistory> getOptOutHistoryForInventory(int inventoryId, Date startDate, Date stopDate,
                                                             Iterable<OptOutEventState> eventStates) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<OverrideHistory> getOptOutHistoryForInventory(int inventoryId,
                                                              ReadableInstant reportStartDate,
                                                              ReadableInstant stopDate,
                                                              LiteYukonGroup residentialGroup) {
        throw new MethodNotImplementedException();
    }

    @Override
    public OptOutEvent findLastEvent(int inventoryId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void saveOptOutLog(OptOutLog optOutLog) {
        throw new MethodNotImplementedException();
    }

    @Override
    public OptOutEvent getScheduledOptOutEvent(int inventoryId, int customerAccountId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<OptOutEvent> getAllScheduledOptOutEvents(LiteStarsEnergyCompany energyCompany) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<OptOutEvent> getAllScheduledOptOutEvents(int customerAccountId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<OptOutEventDto> getCurrentOptOuts(int customerAccountId, int inventoryId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<OptOutEventDto> getCurrentOptOuts(int customerAccountId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<OptOutEvent> getAllCurrentOptOuts(LiteStarsEnergyCompany energyCompany) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<OptOutEvent> getAllCurrentOptOutsByProgramId(int programId,
                                                             LiteStarsEnergyCompany energyCompany) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Integer getNumberOfOptOutsUsed(int inventoryId, int customerAccountId,
                                          Instant startDate, Instant endDate) {
        throw new MethodNotImplementedException();
    }

    @Override
    public int getTotalNumberOfActiveOptOuts(YukonEnergyCompany yukonEnergyCompany,
                                             List<Integer> assignedProgramIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public int getTotalNumberOfScheduledOptOuts(YukonEnergyCompany yukonEnergyCompany,
                                                List<Integer> assignedProgramIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void changeCurrentOptOutCountState(LiteStarsEnergyCompany energyCompany,
                                              OptOutCounts counts) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void changeCurrentOptOutCountStateForProgramId(LiteStarsEnergyCompany energyCompany,
                                                          OptOutCounts counts,
                                                          int webpublishingProgramId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<Integer> getOptedOutDeviceIdsForAccount(int accountId, Date startTime, Date stopTime) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<OptOutEvent> getScheduledOptOutsToBeStarted() {
        throw new MethodNotImplementedException();
    }

    @Override
    public OptOutEvent getOverdueScheduledOptOut(Integer inventoryId, int customerAccountId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<Integer> getScheduledOptOutInventory(Program program, Date startDate, Date stopDate) {
        throw new MethodNotImplementedException();
    }
}
