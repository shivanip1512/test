package com.cannontech.cc.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.Validate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.cc.dao.EconomicEventDao;
import com.cannontech.cc.dao.EconomicEventNotifDao;
import com.cannontech.cc.dao.EconomicEventParticipantDao;
import com.cannontech.cc.dao.EconomicEventParticipantSelectionDao;
import com.cannontech.cc.dao.EconomicEventParticipantSelectionWindowDao;
import com.cannontech.cc.dao.EconomicEventPricingDao;
import com.cannontech.cc.dao.EconomicEventPricingWindowDao;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerPointData;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventNotif;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.EconomicEventParticipantSelection;
import com.cannontech.cc.model.EconomicEventParticipantSelectionWindow;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.cc.model.EconomicEventPricingWindow;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameterKey;
import com.cannontech.cc.model.EconomicEventParticipantSelection.SelectionState;
import com.cannontech.cc.service.builder.EconomicBuilder;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.cc.service.builder.VerifiedPlainCustomer;
import com.cannontech.cc.service.enums.EconomicEventState;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.cc.service.exception.EventModificationException;
import com.cannontech.common.exception.PointException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.enums.EconomicEventAction;
import com.cannontech.enums.NotificationState;
import com.cannontech.yukon.INotifConnection;

public abstract class BaseEconomicStrategy extends StrategyBase {
    private INotifConnection notificationProxy;
    private EconomicEventDao economicEventDao;
    private EconomicEventNotifDao economicEventNotifDao;
    private EconomicEventParticipantDao economicEventParticipantDao;
    private EconomicEventPricingDao economicEventPricingDao;
    private EconomicEventPricingWindowDao economicEventPricingWindowDao;
    private EconomicEventParticipantSelectionDao eventParticipantSelectionDao;
    private EconomicEventParticipantSelectionWindowDao eventParticipantSelectionWindowDao;
    private TransactionTemplate transactionTemplate;
    private SimplePointAccessDao pointAccess;
    private EconomicService economicService;
    
    @Override
    public String getMethodKey() {
        return "economic";
    }

    public EconomicBuilder createBuilder(Program program) {
        EconomicBuilder builder = new EconomicBuilder();
        builder.setProgram(program);
        
        TimeZone tz = getProgramService().getTimeZone(program);
        builder.setTimeZone(tz);
        
        EconomicEvent event = new EconomicEvent();
        event.setProgram(program);
        event.setState(EconomicEventState.INITIAL);
        builder.setEvent(event);
        EconomicEventPricing revision = new EconomicEventPricing();
        revision.setRevision(1); // the first pricing rev is 1
        event.addRevision(revision);
        builder.setEventRevision(revision);
        
        Date now = new Date();
        Calendar calendar = Calendar.getInstance(tz);
        calendar.setTime(now);
        int startTimeOffsetMinutes = getDefaultStartTimeOffsetMinutes(program);
        calendar.add(Calendar.MINUTE, startTimeOffsetMinutes);
        if (startTimeOffsetMinutes < 60) {
            TimeUtil.roundDateUp(calendar, 5);
        } else {
            TimeUtil.roundDateUp(calendar, 60);
        }
        builder.getEvent().setStartTime(calendar.getTime());
        
        calendar.add(Calendar.MINUTE, 
                     -getDefaultNotifTimeBacksetMinutes(program));
        builder.getEvent().setNotificationTime(calendar.getTime());
        
        builder.getEvent().setWindowLengthMinutes(getWindowLengthMinutes());
        builder.setNumberOfWindows(getDefaultDurationMinutes(program) / getWindowLengthMinutes());
        
        return builder;
    }

    public EconomicBuilder createExtensionBuilder(EconomicEvent previous) {
        EconomicBuilder builder = new EconomicBuilder();
        Program program = previous.getProgram();
        builder.setProgram(program);
        
        TimeZone tz = getProgramService().getTimeZone(program);
        builder.setTimeZone(tz);
        
        EconomicEvent event = new EconomicEvent();
        event.setInitialEvent(previous);
        event.setProgram(program);
        event.setState(EconomicEventState.INITIAL);
        builder.setEvent(event);
        EconomicEventPricing revision = new EconomicEventPricing();
        revision.setRevision(1); // the first pricing rev is 1
        event.addRevision(revision);
        builder.setEventRevision(revision);
        
        Calendar calendar = Calendar.getInstance(tz);
        builder.getEvent().setStartTime(previous.getStopTime());
        
        calendar.setTime(builder.getEvent().getStartTime());
        calendar.add(Calendar.MINUTE, 
                     -getDefaultNotifTimeBacksetMinutes(program));
        builder.getEvent().setNotificationTime(calendar.getTime());
        
        builder.getEvent().setWindowLengthMinutes(getWindowLengthMinutes());
        builder.setNumberOfWindows(getDefaultDurationMinutes(program) / getWindowLengthMinutes() / 2);
        
        return builder;
    }

    protected int getDefaultNotifTimeBacksetMinutes(Program program) {
        return getParameterValueInt(program, ProgramParameterKey.DEFAULT_NOTIFICATION_OFFSET_MINUTES);
    }
    
    protected int getDefaultStartTimeOffsetMinutes(Program program) {
        return getParameterValueInt(program, ProgramParameterKey.DEFAULT_EVENT_OFFSET_MINUTES);
    }
    
    protected int getDefaultDurationMinutes(Program program) {
        return getParameterValueInt(program, ProgramParameterKey.DEFAULT_EVENT_DURATION_MINUTES);
    }
    
    protected BigDecimal getDefaultEnergyPrice(Program program) {
        float value = getParameterValueFloat(program, ProgramParameterKey.DEFAULT_ENERGY_PRICE);
        // specify rounding to five decimal places
        return new BigDecimal(value, new MathContext(5));
    }
    
    private int getStopNotifOffsetMinutes() {
        return 5;
    }
    
    public int getWindowLengthMinutes() {
        return 60;
    }
    
    public abstract BigDecimal getCustomerElectionPrice(EconomicEventParticipant customer) throws PointException;

    public abstract BigDecimal getCustomerElectionBuyThrough(EconomicEventParticipant customer) throws PointException;
    
    /**
     * This probably isn't the best place for this method. Maybe move to the CustomerPointTypeHelper
     * @param customer
     * @param type
     * @return
     * @throws PointException
     */
    protected BigDecimal getPointValue(EconomicEventParticipant customer, CICustomerPointType type) throws PointException {
        CICustomerPointData data = customer.getCustomer().getPointData().get(type);
        Validate.notNull(data, "Customer " + customer.getCustomer() + " does not have a point for " + type);
        LitePoint litePoint = DaoFactory.getPointDao().getLitePoint(data.getPointId());
        double pointValue = pointAccess.getPointValue(litePoint);
        // It is now somewhat important to round our double. I'm guessing that
        // ten digits of precision is a good number. This will ensure that
        // ==, <=, and >= operations on "normal" numbers will work as expected.
        return new BigDecimal(pointValue, new MathContext(10));
    }
    
    public Date getStopNotificationTime(EconomicEvent event) {
        Date stopTime = event.getStopTime();
        Date stopNotifDate = TimeUtil.addMinutes(stopTime, getStopNotifOffsetMinutes());
        return stopNotifDate;
    }
    
    public void verifyTimes(EconomicBuilder builder) throws EventCreationException {
        if (builder.getEvent().getStartTime().before(builder.getEvent().getNotificationTime())) {
            // start time is equal to or less than notification time
            throw new EventCreationException("Start time must be after notification time.");
        }
        Date now  = new Date();
        if (builder.getEvent().getNotificationTime().before(now)) {
            throw new EventCreationException("Notification time must not be in the past.");
        }
    }
    
    public void setupPriceList(EconomicBuilder builder) {
        int windows = builder.getNumberOfWindows();
        List<EconomicEventPricingWindow> windowList = new ArrayList<EconomicEventPricingWindow>(windows);
        
        BigDecimal bigDecimal = getDefaultEnergyPrice(builder.getProgram());
        for (int i = 0; i < windows; i++) {
            EconomicEventPricingWindow window = new EconomicEventPricingWindow();
            window.setPricingRevision(builder.getEventRevision());
            window.setEnergyPrice(bigDecimal);
            window.setOffset(i);
            windowList.add(window);
        }
        builder.setPrices(windowList);
    }
    
    public void setupExtensionCustomers(EconomicBuilder builder) {
        EconomicEvent event = builder.getEvent();
        if (!event.isEventExtension()) {
            throw new IllegalArgumentException("Event must be an extension.");
        }
        // plan: copy customers from previous event, remove if they've exceeded their limit
        List<EconomicEventParticipant> initialParticipants = 
            economicEventParticipantDao.getForEvent(event.getInitialEvent());
        for (EconomicEventParticipant originalParticipant : initialParticipants) {
            CICustomerStub customer = originalParticipant.getCustomer();
            if (canCustomerParticipateInExtension(builder, customer)) {
                EconomicEventParticipant participant = new EconomicEventParticipant();
                participant.setCustomer(customer);
                participant.setEvent(event);
                participant.setNotifAttribs(originalParticipant.getNotifAttribs());
                
                builder.getParticipantList().add(participant);
            }
        }
        
    }
    
    public void setupCustomers(EconomicBuilder builder) {
        EconomicEvent event = builder.getEvent();
        builder.getParticipantList().clear();
        List<GroupCustomerNotif> customerList = builder.getCustomerList();
        for (GroupCustomerNotif notif : customerList) {
            EconomicEventParticipant participant = new EconomicEventParticipant();
            participant.setCustomer(notif.getCustomer());
            participant.setEvent(event);
            participant.setNotifAttribs(notif.getAttribs());

            builder.getParticipantList().add(participant);
        }        
    }

    public void verifyPrices(EconomicBuilder builder) throws EventCreationException {
        // for the time being, there are no restrictions on what can be entered
    }

    public EconomicEvent createEvent(final EconomicBuilder builder) throws EventCreationException {
        EconomicEvent event;
        
        event = (EconomicEvent) transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                EconomicEvent event;
                // verify times
                verifyTimes(builder);
                
                // verify prices
                verifyPrices(builder);
                
                if (!builder.getEvent().isEventExtension()) {
                    // verify customers for non-extension events
                    verifyCustomers(builder);
                }
                event = createDatabaseObjects(builder);
                return event;
            }
        });
        
        
        if (event.isEventExtension()) {
            getNotificationProxy().sendEconomicNotification(event.getId(), 1, EconomicEventAction.EXTENDING);
        } else {
            getNotificationProxy().sendEconomicNotification(event.getId(), 1, EconomicEventAction.STARTING);
        }
        
        return event;
    }
    
    protected void verifyCustomers(EconomicBuilder builder) throws EventCreationException {
        List<EconomicEventParticipant> participantList = builder.getParticipantList();
        for (EconomicEventParticipant participant : participantList) {
            VerifiedCustomer vCustoemr = new VerifiedPlainCustomer(participant.getCustomer());
            verifyCustomer(builder, vCustoemr);
            if (!vCustoemr.isIncludable()) {
                throw new EventCreationException("Customer " + participant.getCustomer() +
                                                 " can no longer be included: " + vCustoemr.getReasonForExclusion());
            }
        }
    }
    
    protected EconomicEvent createDatabaseObjects(EconomicBuilder builder) throws EventCreationException {
        // create curtail event
        EconomicEvent event = builder.getEvent();
        
        try {
            EconomicEventPricing revision = builder.getEventRevision();
            Date now = new Date(); // now
            revision.setCreationTime(now);
            //event.addRevision(revision);
            //getEconomicEventPricingDao().save(revision);

            for (EconomicEventPricingWindow window : builder.getPrices()) {
                revision.addWindow(window);
                //getEconomicEventPricingWindowDao().save(window);
            }
            getEconomicEventDao().save(event);

            List<EconomicEventParticipant> participantList = builder.getParticipantList();
            for (EconomicEventParticipant participant : participantList) {

                // create "default" selections for first revision
                EconomicEventParticipantSelection initialSelection = new EconomicEventParticipantSelection();
                initialSelection.setPricingRevision(revision);
                initialSelection.setSubmitTime(now);
                initialSelection.setState(SelectionState.DEFAULT);
                initialSelection.setConnectionAudit("default entries");
                participant.addSelection(initialSelection);

                Collections.sort(builder.getPrices());
                BigDecimal lastHourBuy = null;
                for (EconomicEventPricingWindow window : builder.getPrices()) {
                    BigDecimal energyPrice = window.getEnergyPrice();
                    BigDecimal buyThroughPrice = getCustomerElectionPrice(participant);
                    BigDecimal energyToBuy = null;
                    if (lastHourBuy != null && isCurtailPrice(lastHourBuy)) {
                        // you curtailed last hour, you shall curtail this hour
                        energyToBuy = BigDecimal.ZERO;
                    } else if (energyPrice.compareTo(buyThroughPrice) > 0) {
                        // curtail
                        energyToBuy = BigDecimal.ZERO;
                    } else {
                        // buy through
                        energyToBuy = getCustomerElectionBuyThrough(participant);
                    }
                    EconomicEventParticipantSelectionWindow selectionWindow = new EconomicEventParticipantSelectionWindow();
                    selectionWindow.setEnergyToBuy(energyToBuy);
                    selectionWindow.setWindow(window);
                    initialSelection.addWindow(selectionWindow);

                    lastHourBuy = energyToBuy;
                }
                getEconomicEventParticipantDao().save(participant);
            }
        } catch (PointException e) {
            throw new EventCreationException(e.getMessage(), e);
        }

        return event;
    }
    
    public Boolean canEventBeCancelled(EconomicEvent event, LiteYukonUser user) {
        if (event.getState() != EconomicEventState.INITIAL) {
            return false;
        }
        final int UNSTOPPABLE_WINDOW_MINUTES = 2;
        Date now = new Date();
        Date paddedNotif = TimeUtil.addMinutes(event.getNotificationTime(), 0);
        Date paddedStart = TimeUtil.addMinutes(event.getStartTime(), -UNSTOPPABLE_WINDOW_MINUTES);
        return now.before(paddedStart) && now.after(paddedNotif);
    }
    
    public Boolean canEventBeDeleted(EconomicEvent event, LiteYukonUser user) {
        final int UNSTOPPABLE_WINDOW_MINUTES = 2;
        Date now = new Date();
        Date paddedNotif = TimeUtil.addMinutes(event.getNotificationTime(), -UNSTOPPABLE_WINDOW_MINUTES);
        return now.before(paddedNotif);
    }
    
    public Boolean canEventBeSuppressed(EconomicEvent event, LiteYukonUser user) {
        if (event.getState() != EconomicEventState.INITIAL) {
            return false;
        }
        final int UNSTOPPABLE_WINDOW_MINUTES = 2;
        Date now = new Date();
        Date start = TimeUtil.addMinutes(event.getStartTime(), 0);
        Date paddedStop = TimeUtil.addMinutes(event.getStopTime(), -UNSTOPPABLE_WINDOW_MINUTES);
        return now.after(start) && now.before(paddedStop);
    }
    
    public Boolean canEventBeExtended(EconomicEvent event, LiteYukonUser user) {
        if (event.getState() != EconomicEventState.INITIAL) {
            return false;
        }
        final int UNSTOPPABLE_WINDOW_MINUTES = 2;
        Date now = new Date();
        Date paddedStop = TimeUtil.addMinutes(event.getStopTime(), -UNSTOPPABLE_WINDOW_MINUTES);
        
        if (!now.before(paddedStop)) {
            // too late, event is already over
            return false;
        }
        EconomicEvent childEvent = economicEventDao.getChildEvent(event);
        if (childEvent != null) {
            // already been extended 
            return false;
        }
        return true;
    }

    public Boolean canEventBeRevised(EconomicEvent event, LiteYukonUser user) {
        if (event.getState() != EconomicEventState.INITIAL) {
            return false;
        }
        final int PADDING_MINUTES = 2;
        
        // get latest revision
        EconomicEventPricing latestRevision = event.getLatestRevision();
        
        // get its first affected offset
        int affectedWindowOffset = latestRevision.getFirstAffectedWindowOffset();
        
        // our next revision will have to start with the next window
        int firstRevisableOffset = affectedWindowOffset + 1;
        
        if (firstRevisableOffset >= event.getInitialWindows()) {
            // the last window has already been revised
            return false;
        }
        
        // make sure there is still time left (check last possible window)
        int lastWindowOffset = event.getInitialWindows() - 1;
        Date lastWindowStart = latestRevision.getWindows().get(lastWindowOffset).getStartTime();
        
        Date earliestStartTime = TimeUtil.addMinutes(lastWindowStart, -getMinimumRevisionNoticeMinutes());
        
        Date now = new Date();
        Date paddedNow = TimeUtil.addMinutes(now, PADDING_MINUTES);
        
        if (paddedNow.after(earliestStartTime)) {
            return false;
        }
        
        // check that event notice has been sent 
        if (now.before(event.getNotificationTime())) {
            return false;
        }
        
        return true;
    }

    public EconomicEventPricing createEventRevision(EconomicEvent event, LiteYukonUser yukonUser) 
            throws EventModificationException {
        if (!canEventBeRevised(event, yukonUser)) {
            throw new EventModificationException("Event can no longer be revised.");
        }
        
        EconomicEventPricing latestRevision = event.getLatestRevision();
        EconomicEventPricing newestRevision = new EconomicEventPricing();
        newestRevision.setEvent(event);
        newestRevision.setRevision(latestRevision.getRevision().intValue() + 1);
        
        Date now = new Date();
        
        int firstOffset = latestRevision.getFirstAffectedWindowOffset() + 1;
        int numberOfWindows = latestRevision.getEvent().getInitialWindows();
        for (; firstOffset < numberOfWindows; firstOffset++) {
            EconomicEventPricingWindow window = latestRevision.getWindows().get(firstOffset);
            Date paddedStartTime = 
                TimeUtil.addMinutes(window.getStartTime(), -getMinimumRevisionNoticeMinutes());
            boolean windowIsRevisable = now.before(paddedStartTime);
            if (windowIsRevisable) {
                EconomicEventPricingWindow newWindow = new EconomicEventPricingWindow();
                newWindow.setEnergyPrice(window.getEnergyPrice());
                newWindow.setOffset(window.getOffset());
                newWindow.setPricingRevision(newestRevision);
                newestRevision.addWindow(newWindow);
            }
        }
        if (newestRevision.getWindows().values().isEmpty()) {
            throw new IllegalStateException("Created pricing revision with no windows.");
        }
        return newestRevision;
    }
    
    @Transactional
    public void saveRevision(EconomicEventPricing nextRevision) {
        nextRevision.setCreationTime(new Date());
        EconomicEvent event = nextRevision.getEvent();
        event.addRevision(nextRevision);
        economicEventDao.save(event);
        fillInDefaultPricesForRevision(nextRevision);
        notificationProxy.sendEconomicNotification(event.getId(), nextRevision.getRevision(), EconomicEventAction.REVISING);
        
    }

    private void fillInDefaultPricesForRevision(EconomicEventPricing nextRevision) {
        int previousRevNumber = nextRevision.getRevision() - 1;
        EconomicEventPricing previousRevision = nextRevision.getEvent().getRevisions().get(previousRevNumber);
        List<EconomicEventParticipant> participants = 
            economicEventParticipantDao.getForEvent(nextRevision.getEvent());
        for (EconomicEventParticipant participant : participants) {
            EconomicEventParticipantSelection nextSelection = 
                new EconomicEventParticipantSelection();
            nextSelection.setConnectionAudit("default");
            nextSelection.setState(SelectionState.DEFAULT);
            nextSelection.setParticipant(participant);
            nextSelection.setPricingRevision(nextRevision);
            nextSelection.setSubmitTime(nextRevision.getCreationTime());
            
            ArrayList<EconomicEventPricingWindow> windows = 
                new ArrayList<EconomicEventPricingWindow>(nextRevision.getWindows().values());
            Collections.sort(windows);
            for (EconomicEventPricingWindow window : windows) {
                Integer offset = window.getOffset();
                EconomicEventPricingWindow previousRevWindow = 
                    previousRevision.getWindows().get(offset);
                
                EconomicEventParticipantSelection previousSelection = 
                    participant.getSelection(previousRevision);
                EconomicEventParticipantSelectionWindow previousSelectionWindow = 
                    previousSelection.getSelectionWindow(previousRevWindow);
                
                EconomicEventParticipantSelectionWindow nextSelectionWindow = 
                    new EconomicEventParticipantSelectionWindow();
                nextSelectionWindow.setSelection(nextSelection);
                nextSelectionWindow.setWindow(window);
                
                nextSelectionWindow.setEnergyToBuy(previousSelectionWindow.getEnergyToBuy());
                nextSelection.addWindow(nextSelectionWindow);
            }
            participant.addSelection(nextSelection);
            economicEventParticipantDao.save(participant);
        }
        
    }

    public void saveParticipantSelection(EconomicEventParticipantSelection selection, LiteYukonUser user) 
    throws EventModificationException {
        // check time
        Date now = new Date();
        if (!canUserSubmitSelectionForRevision(selection, user, now)) {
            throw new EventModificationException("Cannot save selections at this time.");
        }
        // verify prices
        checkPropossedSelections(selection, user, now);
        
        selection.setState(SelectionState.MANUAL);
        selection.setSubmitTime(now);
        
        // this will cascade down
        economicEventParticipantDao.save(selection.getParticipant());
    }

    protected abstract void checkPropossedSelections(EconomicEventParticipantSelection selection,
                                                     LiteYukonUser user, Date time) 
        throws EventModificationException;

    protected int getMinimumRevisionNoticeMinutes() {
        return 60;
    }
    
    protected int getMinimumAdvanceSelectionEntry() {
        return 30;
    }

    @Transactional
    public void deleteEvent(EconomicEvent event, LiteYukonUser user) throws EventModificationException {
        if (!canEventBeDeleted(event, user)) {
            throw new EventModificationException("This action is not allowed.");
        }
        boolean success = getNotificationProxy().attemptDeleteEconomic(event.getId(), true);
        if (!success) {
            throw new EventModificationException("Notifications were not successfully prevented. Check notif status for each customer.");
        }
        economicEventParticipantDao.deleteForEvent(event);
        economicEventDao.delete(event);
    }
    
    @Transactional
    public void cancelEvent(EconomicEvent event, LiteYukonUser user) {
        event.setState(EconomicEventState.CANCELLED);
        economicEventDao.save(event);
        notificationProxy.sendEconomicNotification(event.getId(), event.getLatestRevision().getRevision(), EconomicEventAction.CANCELING);
    }
    
    @Transactional
    public void suppressEvent(EconomicEvent event, LiteYukonUser user) {
        event.setState(EconomicEventState.SUPPRESSED);
        economicEventDao.save(event);
        notificationProxy.attemptDeleteEconomic(event.getId(), false);
        notificationProxy.sendEconomicNotification(event.getId(), event.getLatestRevision().getRevision(), EconomicEventAction.CANCELING);
    }
    
    public boolean canUserSubmitSelectionForRevision(EconomicEventParticipantSelection selection, LiteYukonUser user, Date time) {
        // revision must be latest
        EconomicEventPricing pricingRevision = selection.getPricingRevision();
        if (!pricingRevision.getEvent().getLatestRevision().equals(pricingRevision)) {
            return false;
        }
        
        if (!isRevisionEditable(selection.getPricingRevision(), selection.getParticipant())) {
            return false;
        }
        // first window of revision must not have started
        Date now = new Date();
        Date revisionStartTime = pricingRevision.getFirstAffectedWindow().getStartTime();
        Date latestEntryTime = TimeUtil.addMinutes(revisionStartTime, -getMinimumAdvanceSelectionEntry());
        if (now.after(latestEntryTime)) {
            return false;
        }
        return true;
    }

    public boolean canPricingSelectionBeEdited(EconomicEventParticipantSelectionWindow selectionWindow, 
                                               LiteYukonUser user, Date time) {
        if (!canUserSubmitSelectionForRevision(selectionWindow.getSelection(), user, time)) {
            return false;
        }
        
        // window number must be >= to the revision number
        Integer revisionNumber = selectionWindow.getSelection().getPricingRevision().getRevision();
        Integer currentOffset = selectionWindow.getWindow().getOffset();
        if (currentOffset + 1 < revisionNumber) {
            return false;
        }
        // check that customer didn't curtail durring the last revision
        return isSelectionWindowEditable(selectionWindow);
    }
    
    public List<EconomicEventParticipant> getRevisionParticipantForNotif(EconomicEventPricing eventRevision) {
        List<EconomicEventParticipant> participants = 
            economicEventParticipantDao.getForEvent(eventRevision.getEvent());
        List<EconomicEventParticipant> result = new ArrayList<EconomicEventParticipant>(participants.size());
        for (EconomicEventParticipant participant : participants) {
            // if participant can edit any of the windows, they should get a notif
            if (isRevisionEditable(eventRevision, participant)) {
                result.add(participant);
            }
            
        }
        return participants;
    }
    
    private boolean isRevisionEditable(EconomicEventPricing eventRevision, EconomicEventParticipant participant) {
        if (eventRevision.getRevision() == 1) {
            return true;
        }
        int offset = eventRevision.getFirstAffectedWindowOffset();
        int numOfWindows = eventRevision.getNumberOfWindows();
        
        for (;offset < numOfWindows; offset++) {
            EconomicEventPricingWindow pricingWindow = eventRevision.getWindows().get(offset);
            EconomicEventParticipantSelection selection = participant.getSelection(eventRevision);
            EconomicEventParticipantSelectionWindow selectionWindow = selection.getSelectionWindow(pricingWindow);
            if (isSelectionWindowEditable(selectionWindow)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isSelectionWindowEditable(EconomicEventParticipantSelectionWindow selectionWindow) {
        EconomicEventParticipantSelection selection = selectionWindow.getSelection();
        EconomicEventPricing pricingRevision = selection.getPricingRevision();
        int revision = pricingRevision.getRevision();
        if (revision == 1) {
            return true;
        }
        EconomicEventPricing previousRevision = pricingRevision.getPrevious();
        EconomicEventPricingWindow previousWindow = 
            previousRevision.getWindows().get(selectionWindow.getWindow().getOffset());
        BigDecimal previousPrice = previousWindow.getEnergyPrice();
        BigDecimal currentPrice = selectionWindow.getWindow().getEnergyPrice();
        boolean priceUp = currentPrice.compareTo(previousPrice) > 0;
        EconomicEventParticipantSelection previousSelection = selection.getParticipant().getSelection(previousRevision);
        EconomicEventParticipantSelectionWindow previousSelectionWindow = 
            previousSelection.getSelectionWindow(previousWindow);
        BigDecimal previousEnergyBought = previousSelectionWindow.getEnergyToBuy();
        return priceUp && !isCurtailPrice(previousEnergyBought);
    }
    
    protected boolean isCurtailPrice(BigDecimal buyThrough) {
        // see note about compareTo vs. equals for BigDecimals
        return buyThrough.compareTo(BigDecimal.ZERO) == 0;
    }
    
    public float getNotificationSuccessRate(EconomicEventParticipant participant) {
        int success = 0;
        int total = 0;
        List<EconomicEventNotif> notifs = economicEventNotifDao.getForParticipant(participant);
        for (EconomicEventNotif notif : notifs) {
            total++;
            if (notif.getState().equals(NotificationState.SUCCEEDED)) {
                success++;
            }
        }
        if (total == 0) {
            return 0;
        }
        return (float)success/(float)total;
    }

    @Override
    public List<? extends BaseEvent> getEventsForProgram(Program program) {
        return economicEventDao.getAllForProgram(program);
    }

    public INotifConnection getNotificationProxy() {
        return notificationProxy;
    }

    public void setNotificationProxy(INotifConnection notificationProxy) {
        this.notificationProxy = notificationProxy;
    }

    public EconomicEventDao getEconomicEventDao() {
        return economicEventDao;
    }

    public void setEconomicEventDao(EconomicEventDao economicEventDao) {
        this.economicEventDao = economicEventDao;
    }

    public EconomicEventNotifDao getEconomicEventNotifDao() {
        return economicEventNotifDao;
    }

    public void setEconomicEventNotifDao(EconomicEventNotifDao economicEventNotifDao) {
        this.economicEventNotifDao = economicEventNotifDao;
    }
    
    public EconomicEventParticipantDao getEconomicEventParticipantDao() {
        return economicEventParticipantDao;
    }

    public void setEconomicEventParticipantDao(EconomicEventParticipantDao economicEventParticipantDao) {
        this.economicEventParticipantDao = economicEventParticipantDao;
    }

    public EconomicEventPricingDao getEconomicEventPricingDao() {
        return economicEventPricingDao;
    }

    public void setEconomicEventPricingDao(EconomicEventPricingDao economicEventPricingDao) {
        this.economicEventPricingDao = economicEventPricingDao;
    }

    public EconomicEventPricingWindowDao getEconomicEventPricingWindowDao() {
        return economicEventPricingWindowDao;
    }

    public void setEconomicEventPricingWindowDao(EconomicEventPricingWindowDao economicEventPricingWindowDao) {
        this.economicEventPricingWindowDao = economicEventPricingWindowDao;
    }

    public EconomicEventParticipantSelectionDao getEventParticipantSelectionDao() {
        return eventParticipantSelectionDao;
    }

    public void setEventParticipantSelectionDao(EconomicEventParticipantSelectionDao eventParticipantSelectionDao) {
        this.eventParticipantSelectionDao = eventParticipantSelectionDao;
    }

    public EconomicEventParticipantSelectionWindowDao getEventParticipantSelectionWindowDao() {
        return eventParticipantSelectionWindowDao;
    }

    public void setEventParticipantSelectionWindowDao(
                                                      EconomicEventParticipantSelectionWindowDao eventParticipantSelectionWindowDao) {
        this.eventParticipantSelectionWindowDao = eventParticipantSelectionWindowDao;
    }

    public void setPointAccess(SimplePointAccessDao pointAccess) {
        this.pointAccess = pointAccess;
    }

    public EconomicService getEconomicService() {
        return economicService;
    }

    public void setEconomicService(EconomicService economicService) {
        this.economicService = economicService;
    }

    protected abstract boolean canCustomerParticipateInExtension(EconomicBuilder builder, CICustomerStub customer);

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }


}
