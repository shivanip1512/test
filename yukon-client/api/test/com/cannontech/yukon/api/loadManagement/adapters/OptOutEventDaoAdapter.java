package com.cannontech.yukon.api.loadManagement.adapters;

import java.util.Date;
import java.util.List;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutAction;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.model.OptOutLog;
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.google.common.collect.Multimap;

public class OptOutEventDaoAdapter implements OptOutEventDao {

    @Override
    public OptOutEvent getOptOutEventById(int eventId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public OptOutLog save(OptOutEvent event, OptOutAction action, LiteYukonUser user) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isOptedOut(int inventoryId, int customerAccountId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<OptOutEventDto> getOptOutHistoryForAccount(int customerAccountId, int... numberOfRecords) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Multimap<OptOutEventDto, OptOutLog> getOptOutEventDetails(Iterable<OptOutEventDto> optOutEvents) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<OverrideHistory> getOptOutHistoryForAccount(int accountId, Date startDate, Date stopDate) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<OverrideHistory> getOptOutHistoryForAccount(int accountId, ReadableInstant reportStartDate, ReadableInstant stopDate, LiteYukonGroup residentialGroup) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<OverrideHistory> getOptOutHistoryByLogUserId(int userId, ReadableInstant reportStartDate, ReadableInstant stopDate, LiteYukonGroup residentialGroup) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<OverrideHistory> getOptOutHistoryForInventory(int inventoryId, Date startDate, Date stopDate) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<OverrideHistory> getOptOutHistoryForInventory(int inventoryId, ReadableInstant reportStartDate, ReadableInstant stopDate, LiteYukonGroup residentialGroup) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public OptOutEvent findLastEvent(int inventoryId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void saveOptOutLog(OptOutLog optOutLog) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public OptOutEvent getScheduledOptOutEvent(int inventoryId, int customerAccountId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<OptOutEvent> getAllScheduledOptOutEvents(LiteStarsEnergyCompany energyCompany) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<OptOutEvent> getAllScheduledOptOutEvents(int customerAccountId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<OptOutEventDto> getCurrentOptOuts(int customerAccountId, int inventoryId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<OptOutEventDto> getCurrentOptOuts(int customerAccountId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<OptOutEvent> getAllCurrentOptOuts(LiteStarsEnergyCompany energyCompany) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<OptOutEvent> getAllCurrentOptOutsByProgramId(int programId,
                                                             LiteStarsEnergyCompany energyCompany) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Integer getNumberOfOptOutsUsed(int inventoryId, int customerAccountId,
                                          Instant startDate, Instant endDate) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int getTotalNumberOfActiveOptOuts(LiteStarsEnergyCompany energyCompany) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int getTotalNumberOfScheduledOptOuts(LiteStarsEnergyCompany energyCompany) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void changeCurrentOptOutCountState(LiteStarsEnergyCompany energyCompany,
                                              OptOutCounts counts) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void changeCurrentOptOutCountStateForProgramId(LiteStarsEnergyCompany energyCompany,
                                                          OptOutCounts counts,
                                                          int webpublishingProgramId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<Integer> getOptedOutDeviceIdsForAccount(int accountId, Date startTime, Date stopTime) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<OptOutEvent> getScheduledOptOutsToBeStarted() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public OptOutEvent getOverdueScheduledOptOut(Integer inventoryId, int customerAccountId) {
        throw new UnsupportedOperationException("not implemented");
    }

}
