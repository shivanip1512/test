package com.cannontech.web.dr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationMessageStatus;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.dr.rfn.model.BroadcastEventDeviceDetails;
import com.cannontech.dr.rfn.model.UnknownStatus;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.model.LiteLmHardware;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.dr.model.RfPerformanceSettings;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.DEMAND_RESPONSE)
public class RfPerformanceController {

    private static final String homeKey = "yukon.web.modules.dr.home.rfPerformance.";
    private static final String detailsKey = "yukon.web.modules.dr.rf.broadcast.eventDetail.";
    private static final Logger log = YukonLogManager.getLogger(RfPerformanceController.class);

    @Autowired private JobManager jobManager;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private PerformanceVerificationDao rfPerformanceDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private MemoryCollectionProducer memoryCollectionProducer;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private PaoNotesService paoNotesService;
    public static final String UNREPORTED = "unreported";
    @Autowired @Qualifier("rfnPerformanceVerification")
        private YukonJobDefinition<RfnPerformanceVerificationTask> rfnVerificationJobDef;
    @Autowired @Qualifier("rfnPerformanceVerificationEmail")
        private YukonJobDefinition<RfnPerformanceVerificationEmailTask> rfnEmailJobDef;
    
    /**
     * Update settings for rf performance test command and email generation
     */
    @RequestMapping(value="/rf/performance", method=RequestMethod.POST)
    public String saveSettings(@ModelAttribute("settings") RfPerformanceSettings settings, FlashScope flash) {
        
        LocalTime commandTime = LocalTime.MIDNIGHT.plusMinutes(settings.getTime());
        LocalTime emailTime = LocalTime.MIDNIGHT.plusMinutes(settings.getEmailTime());
        
        StringBuilder commandCron = new StringBuilder("0 ");
        commandCron.append(commandTime.getMinuteOfHour() + " ");
        commandCron.append(commandTime.getHourOfDay() + " * * ?");
        
        StringBuilder emailCron = new StringBuilder("0 ");
        emailCron.append(emailTime.getMinuteOfHour() + " ");
        emailCron.append(emailTime.getHourOfDay() + " * * ?");
        
        ScheduledRepeatingJob commandJob = getJob(rfnVerificationJobDef);
        
        try {
            if (!commandCron.toString().equals(commandJob.getCronString())) {
                jobManager.replaceScheduledJob(commandJob.getId(), 
                                               rfnVerificationJobDef, 
                                               commandJob.getJobDefinition().createBean(), 
                                               commandCron.toString());
            }
            
            ScheduledRepeatingJob emailJob = getJob(rfnEmailJobDef);
            
            if (settings.isEmail()) {
                // Email setting is enabled. We might need to update the cron string or notification groups.
                String notifGroups = StringUtils.join(settings.getNotifGroupIds(), ",");
                boolean notifGroupsChanged = !notifGroups.equals(emailJob.getJobProperties().get("notificationGroups"));
                emailJob.getJobProperties().put("notificationGroups", notifGroups);
                
                if (!emailCron.toString().equals(emailJob.getCronString()) || notifGroupsChanged) {
                    jobManager.replaceScheduledJob(emailJob.getId(), 
                            rfnEmailJobDef, 
                            emailJob.getJobDefinition().createBean(), 
                            emailCron.toString(), 
                            null,
                            emailJob.getJobProperties()); 

                    // Grab the newly re-scheduled version of the email job for the disabled check.
                    emailJob = getJob(rfnEmailJobDef);
                }

                if (emailJob.isDisabled()) {
                    jobManager.enableJob(emailJob);
                }
            } else if (!emailJob.isDisabled()) {
                jobManager.disableJob(emailJob);
            }
            
            // Always call one of these since we may need to update
            // job properties and these two methods end up saving 
            // everything about the job (hacky)
            
            flash.setConfirm(new YukonMessageSourceResolvable(homeKey + "configure.success"));
        } catch (Exception e) {
            flash.setError(new YukonMessageSourceResolvable(homeKey + "configure.failed"));
            log.error("Failed to update RF Broadcast Performance Email Job", e);
        }
        
        return "redirect:/dr/home";
    }
    
    @RequestMapping(value="/rf/details/day", method=RequestMethod.GET)
    public String detailsDay(ModelMap model) {
        
        Instant to = new Instant();
        model.addAttribute("to", to);
        Instant from = to.minus(Duration.standardDays(1));
        model.addAttribute("from", from);
        detailsModel(model, from, to);
        
        return "dr/rf/details.jsp";
    }
    
    @RequestMapping(value="/rf/details/week", method=RequestMethod.GET)
    public String detailsWeek(ModelMap model) {
        
        Instant to = new Instant();
        model.addAttribute("to", to);
        Instant from = to.minus(Duration.standardDays(7));
        model.addAttribute("from", from);
        detailsModel(model, from, to);
        
        return "dr/rf/details.jsp";
    }
    
    @RequestMapping(value="/rf/details/month", method=RequestMethod.GET)
    public String detailsMonth(ModelMap model) {
        
        Instant to = new Instant();
        model.addAttribute("to",to);
        Instant from = to.minus(Duration.standardDays(30));
        model.addAttribute("from", from);
        detailsModel(model, from, to);
        
        return "dr/rf/details.jsp";
    }
    
    @RequestMapping(value="/rf/details", method=RequestMethod.GET)
    public String details(ModelMap model, @RequestParam(required=false) Instant from, @RequestParam(required=false) Instant to) {
        
        if (from == null) {
            from =  new Instant().minus(Duration.standardDays(7));
        }
        if (to == null) {
            to =  new Instant();
        }
        Instant toFullDay = to.plus(Duration.standardDays(1)).toDateTime().withTimeAtStartOfDay().toInstant();
        
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        detailsModel(model, from, toFullDay);
        
        return "dr/rf/details.jsp";
    }

    @GetMapping("/rf/broadcast/eventDetail/{eventId}")
    public String broadcastEventDetail(ModelMap model, @PathVariable long eventId, YukonUserContext userContext) {
        PerformanceVerificationEventMessageStats event = mockStats(eventId); // TODO : Need to be replaced with service layer call
        model.addAttribute("event", event);
        model.addAttribute("eventId", eventId);
        List<PerformanceVerificationMessageStatus> statusTypes = getStatusTypes();
        List<PerformanceVerificationMessageStatus> statuses = new ArrayList<>();
        model.addAttribute("statusTypes", statusTypes);
        statuses.addAll(statusTypes);
        model.addAttribute("statuses", statuses);
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String helpText = accessor.getMessage(detailsKey + "helpText");
        model.addAttribute("helpText", helpText);
        return "dr/rf/broadcast/eventDetail.jsp";
    }

    @GetMapping(value = "/rf/broadcast/eventDetail/filterResults")
    public String filterResults(ModelMap model, long eventId, String[] deviceSubGroups,
            PerformanceVerificationMessageStatus[] statuses, YukonUserContext userContext,
            @DefaultSort(dir = Direction.asc, sort = "DEVICE_NAME") SortingParameters sorting,
            @DefaultItemsPerPage(value = 250) PagingParameters paging) {
        getFilteredResults(model, sorting, paging, userContext, eventId, deviceSubGroups,
            statuses);
        return "dr/rf/broadcast/filteredResults.jsp";
    }
    
    @GetMapping(value = "rf/broadcast/eventDetail/filterResultsTable")
    public String filterResultsTable(ModelMap model, long eventId, String[] deviceSubGroups,
            PerformanceVerificationMessageStatus[] statuses, YukonUserContext userContext,
            @DefaultSort(dir = Direction.asc, sort = "DEVICE_NAME") SortingParameters sorting,
            @DefaultItemsPerPage(value = 250) PagingParameters paging) {
        getFilteredResults(model, sorting, paging, userContext, eventId, deviceSubGroups, statuses);
        return "dr/rf/broadcast/filteredResultsTable.jsp";
    }

    private void getFilteredResults(ModelMap model, SortingParameters sorting, PagingParameters paging,
            YukonUserContext userContext, long eventId, String[] deviceSubGroups,
            PerformanceVerificationMessageStatus[] statuses) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        if (statuses == null) {
            model.addAttribute("statuses", getStatusTypes());
        } else {
            model.addAttribute("statuses", statuses);
        }
        model.addAttribute("deviceSubGroups", deviceSubGroups);
        model.addAttribute("eventId", eventId);
        EventDetailSortBy sortBy = EventDetailSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        for (EventDetailSortBy column : EventDetailSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
        List<DeviceGroup> subGroups = retrieveSubGroups(deviceSubGroups);
        List<BroadcastEventDeviceDetails> broadcastEventDetails = mockBroadCastEventDetails();// TODO : Will be replaced with Service Layer call
        sortBroadcastEventDetails(broadcastEventDetails, dir, sortBy, userContext);
        int totalEventCount = 4;//TODO : Will be replaced with Service call to get the total count;
        SearchResults<BroadcastEventDeviceDetails> searchResults =
            SearchResults.pageBasedForSublist(broadcastEventDetails, paging, totalEventCount);
        model.addAttribute("searchResults", searchResults);
        /* Build temporary collection for Collection Actions */
        List<SimpleDevice> devices = new ArrayList<>();
        searchResults.getResultList().stream().filter(
            rfBroadcastEventDetail -> rfBroadcastEventDetail.getHardware().getDeviceId() != 0).forEach(
                rfBroadcastEventDetail -> devices.add(
                    deviceDao.getYukonDevice(rfBroadcastEventDetail.getHardware().getDeviceId())));
        StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(tempGroup, devices);
        DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(tempGroup);
        model.addAttribute("deviceCollection", deviceCollection);

        /* PAO Notes */
        List<Integer> notesList = paoNotesService.getPaoIdsWithNotes(
            devices.stream().map(device -> device.getPaoIdentifier().getPaoId()).collect(Collectors.toList()));
        model.addAttribute("notesList", notesList);

    }

    private void sortBroadcastEventDetails(List<BroadcastEventDeviceDetails> broadcastEventDetails, Direction dir,
            EventDetailSortBy sortBy, YukonUserContext userContext) {
        Comparator<BroadcastEventDeviceDetails> comparator = null;
        if (sortBy == EventDetailSortBy.DEVICE_NAME) {
            comparator = (o1, o2) -> {
                return deviceDao.getFormattedName(o1.getHardware().getDeviceId()).compareTo(
                    deviceDao.getFormattedName(o2.getHardware().getDeviceId()));
            };
        }

        if (sortBy == EventDetailSortBy.DEVICE_TYPE) {
            comparator = (o1, o2) -> o1.getHardware().getIdentifier().getHardwareType().compareTo(
                o2.getHardware().getIdentifier().getHardwareType());
        }
        if (sortBy == EventDetailSortBy.ACCOUNT_NUMBER) {
            comparator = (o1, o2) -> o1.getHardware().getAccountNo().compareTo(o2.getHardware().getAccountNo());
        }
        if (sortBy == EventDetailSortBy.CURRENT_STATUS) {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            comparator = (o1, o2) -> accessor.getMessage(o1.getDeviceStatus().getFormatKey()).compareTo(
                accessor.getMessage(o2.getDeviceStatus().getFormatKey()));
        }
        if (dir == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(broadcastEventDetails, comparator);
    }

    private List<DeviceGroup> retrieveSubGroups(String[] deviceSubGroups) {
        List<DeviceGroup> subGroups = new ArrayList<>();
        if (deviceSubGroups != null) {
            for (String subGroup : deviceSubGroups) {
                subGroups.add(deviceGroupService.resolveGroupName(subGroup));
            }
        }
        return subGroups;
    }

    private List<PerformanceVerificationMessageStatus> getStatusTypes() {
        List<PerformanceVerificationMessageStatus> statusTypes = new ArrayList<>();
        statusTypes.add(PerformanceVerificationMessageStatus.SUCCESS);
        statusTypes.add(PerformanceVerificationMessageStatus.FAILURE);
        statusTypes.add(PerformanceVerificationMessageStatus.UNKNOWN);
        return statusTypes;
    }

    private void detailsModel(ModelMap model, Instant from, Instant to) {
        List<PerformanceVerificationEventMessageStats> tests = rfPerformanceDao.getReports(Range.inclusiveExclusive(from, to));
        
        Instant afterDate = new DateTime().plusDays(-180).withTimeAtStartOfDay().toInstant();
        if (from.isBefore(afterDate)) {
            List<PerformanceVerificationEventMessageStats> report 
                    = rfPerformanceDao.getArchiveReports(Range.inclusiveExclusive(from,to));
            tests.addAll(report);
        }
        Collections.sort(tests,
                         (t1, t2) -> t1.getTimeMessageSent().compareTo(t2.getTimeMessageSent()));
        model.addAttribute("tests", tests);
        
        int totalSuccess = 0;
        int totalFailed = 0;
        int totalUnknown = 0;
        for (PerformanceVerificationEventMessageStats stats : tests) {
            totalSuccess += stats.getNumSuccesses();
            totalFailed += stats.getNumFailures();
            totalUnknown += stats.getNumUnknowns();
        }
        
        model.addAttribute("totalSuccess", totalSuccess);
        model.addAttribute("totalFailed", totalFailed);
        model.addAttribute("totalUnknown", totalUnknown);
        boolean hasStats = true;
        if (totalSuccess == 0 && totalFailed == 0 && totalUnknown == 0) {
            hasStats = false;
        }
        model.addAttribute("hasStats", hasStats);
    }

    @GetMapping(value ="rf/eventDetail/{deviceId}/inventoryAction")
    public String inventoryAction(ModelMap model, YukonUserContext userContext, @PathVariable int deviceId) {
        InventoryIdentifier inventory = inventoryDao.getYukonInventoryForDeviceId(deviceId);

        String description = resolver.getMessageSourceAccessor(userContext).getMessage(detailsKey + "description");
        List<InventoryIdentifier> inventoryList = new ArrayList<>();
        inventoryList.add(inventory);
        InventoryCollection temporaryCollection =
            memoryCollectionProducer.createCollection(inventoryList.iterator(), description);
        model.addAttribute("inventoryCollection", temporaryCollection);
        model.addAllAttributes(temporaryCollection.getCollectionParameters());

        return "redirect:/stars/operator/inventory/inventoryActions";
    }

    @GetMapping(value = "/rf/broadcast/eventDetail/filteredResultInventoryAction")
    public String filteredResultInventoryAction(ModelMap model, Integer eventId, String[] deviceSubGroups,
            PerformanceVerificationMessageStatus[] statuses, YukonUserContext userContext) {
        List<PaoIdentifier> paos = new ArrayList<>(); //TODO : Will be replaced by Service layer call to get the paos
        List<Integer> deviceIds = Lists.transform(paos, PaoUtils.getYukonPaoToPaoIdFunction());
        List<InventoryIdentifier> inventory = inventoryDao.getYukonInventoryForDeviceIds(deviceIds);

        String description = resolver.getMessageSourceAccessor(userContext).getMessage(detailsKey + "description");

        InventoryCollection temporaryCollection =
            memoryCollectionProducer.createCollection(inventory.iterator(), description);
        model.addAttribute("inventoryCollection", temporaryCollection);
        model.addAllAttributes(temporaryCollection.getCollectionParameters());

        return "redirect:/stars/operator/inventory/inventoryActions";

    }

    // TODO : Need to be removed after Service layer implementation
    private PerformanceVerificationEventMessageStats mockStats(long eventId) {
        PerformanceVerificationEventMessageStats stats = new PerformanceVerificationEventMessageStats(eventId, Instant.now(), false, 1, 1, 1);
        return stats;
    }

    // TODO : Need to be removed after Service layer implementation
    private List<BroadcastEventDeviceDetails> mockBroadCastEventDetails() {
        List<BroadcastEventDeviceDetails> broadcastEventDetails = new ArrayList<>();
        LiteLmHardware lm1 = new LiteLmHardware();
        lm1.setAccountId(242);
        lm1.setAccountNo("1010105");
        lm1.setDeviceId(12725);
        lm1.setEnergyCompanyId(93);
        lm1.setLabel("9876");
        lm1.setSerialNumber("9876");
        InventoryIdentifier id1 = new InventoryIdentifier(602, HardwareType.LCR_6200_RFN);
        lm1.setIdentifier(id1);
        BroadcastEventDeviceDetails event1 = new BroadcastEventDeviceDetails(PerformanceVerificationMessageStatus.SUCCESS, lm1, new Instant().minus(Duration.standardDays(3)), UnknownStatus.COMMUNICATING);
        LiteLmHardware lm2 = new LiteLmHardware();
        lm2.setAccountId(242);
        lm2.setAccountNo("1010105");
        lm2.setDeviceId(16626);
        lm2.setEnergyCompanyId(32);
        lm2.setLabel("4567");
        lm2.setSerialNumber("4567");
        InventoryIdentifier id2 = new InventoryIdentifier(992, HardwareType.LCR_6200_RFN);
        lm2.setIdentifier(id2);
        BroadcastEventDeviceDetails event2 = new BroadcastEventDeviceDetails(PerformanceVerificationMessageStatus.FAILURE, lm2, new Instant().minus(Duration.standardDays(2)), UnknownStatus.COMMUNICATING);
        LiteLmHardware lm3 = new LiteLmHardware();
        lm3.setAccountId(242);
        lm3.setAccountNo("1010105");
        lm3.setDeviceId(16627);
        lm3.setEnergyCompanyId(32);
        lm3.setLabel("1596");
        lm3.setSerialNumber("1596");
        InventoryIdentifier id3 = new InventoryIdentifier(993, HardwareType.LCR_6200_RFN);
        lm3.setIdentifier(id3);
        BroadcastEventDeviceDetails event3 = new BroadcastEventDeviceDetails(PerformanceVerificationMessageStatus.UNKNOWN, lm3, new Instant().minus(Duration.standardDays(6)), UnknownStatus.NOT_COMMUNICATING);
        LiteLmHardware lm4 = new LiteLmHardware();
        lm4.setAccountId(242);
        lm4.setAccountNo("1010105");
        lm4.setDeviceId(16627);
        lm4.setEnergyCompanyId(32);
        lm4.setLabel("1596");
        lm4.setSerialNumber("1596");
        InventoryIdentifier id4 = new InventoryIdentifier(993, HardwareType.LCR_6200_RFN);
        lm4.setIdentifier(id4);
        BroadcastEventDeviceDetails event4 = new BroadcastEventDeviceDetails(PerformanceVerificationMessageStatus.UNKNOWN, lm4, new Instant().minus(Duration.standardDays(6)), UnknownStatus.NEW_INSTALL_NOT_COMMUNICATING);
        broadcastEventDetails.add(event1);
        broadcastEventDetails.add(event2);
        broadcastEventDetails.add(event3);
        broadcastEventDetails.add(event4);
        return broadcastEventDetails;
    }

    private ScheduledRepeatingJob getJob(YukonJobDefinition<? extends YukonTask> jobDefinition) {
        List<ScheduledRepeatingJob> activeJobs = jobManager.getNotDeletedRepeatingJobsByDefinition(jobDefinition);
        return Iterables.getOnlyElement(activeJobs);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, final YukonUserContext userContext) {
        datePropertyEditorFactory.setupInstantPropertyEditor(binder, userContext, BlankMode.CURRENT);
    }

    public enum EventDetailSortBy implements DisplayableEnum {

        DEVICE_NAME, 
        DEVICE_TYPE, 
        ACCOUNT_NUMBER, 
        CURRENT_STATUS;

        @Override
        public String getFormatKey() {
            return detailsKey + name();
        }
    }
}