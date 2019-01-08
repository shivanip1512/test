package com.cannontech.web.dr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

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
import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationMessageStatus;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.dr.rfn.model.BroadcastEventDeviceDetails;
import com.cannontech.dr.rfn.model.DeviceStatus;
import com.cannontech.dr.rfn.service.RfnPerformanceVerificationService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.dr.model.RfPerformanceSettings;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.DEMAND_RESPONSE)
public class RfPerformanceController {

    private static final String homeKey = "yukon.web.modules.dr.home.rfPerformance.";
    private static final String detailsKey = "yukon.web.modules.dr.rf.broadcast.eventDetail.";
    private static final Logger log = YukonLogManager.getLogger(RfPerformanceController.class);

    @Autowired private JobManager jobManager;
    @Autowired private PerformanceVerificationDao rfPerformanceDao;
    @Autowired private MemoryCollectionProducer memoryCollectionProducer;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoNotesService paoNotesService;
    @Autowired private RfnPerformanceVerificationService verificationService;
    
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

        DateTime now = DateTime.now();
        if (from == null) {
            from =  now.withTimeAtStartOfDay().minusDays(5).toInstant();
        }
        if (to == null) {
            to =  now.toInstant();
        }
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        detailsModel(model, from, to);

        return "dr/rf/details.jsp";
    }

    @GetMapping("/rf/broadcast/eventDetail/{eventId}")
    public String broadcastEventDetail(ModelMap model, @PathVariable long eventId, YukonUserContext userContext) {
        PerformanceVerificationEventMessageStats event = verificationService.getReportForEvent(eventId); 
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
        unReportedDeviceText(model, eventId, accessor);
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
        List<BroadcastEventDeviceDetails> broadcastEventDetails =
            verificationService.getDevicesWithStatus(eventId, statuses, paging, subGroups);
        sortBroadcastEventDetails(broadcastEventDetails, dir, sortBy, userContext);
        int totalEventCount = verificationService.getTotalCount(eventId, statuses, subGroups);
        SearchResults<BroadcastEventDeviceDetails> searchResults =
            SearchResults.pageBasedForSublist(broadcastEventDetails, paging, totalEventCount);
        model.addAttribute("searchResults", searchResults);

        /* PAO Notes */
        List<Integer> notesList = paoNotesService.getPaoIdsWithNotes(searchResults.getResultList().stream().map(
            rfBroadcastEventDetail -> rfBroadcastEventDetail.getHardware().getDeviceId()).collect(Collectors.toList()));
        model.addAttribute("notesList", notesList);

    }

    @GetMapping(value = "/rf/broadcast/eventDetail/filteredResultInventoryAction")
    public String filteredResultInventoryAction(ModelMap model, long eventId, String[] deviceSubGroups,
            PerformanceVerificationMessageStatus[] statuses, YukonUserContext userContext) {
        List<DeviceGroup> subGroups = retrieveSubGroups(deviceSubGroups);
        List<BroadcastEventDeviceDetails> broadcastEventDetails =
            verificationService.getDevicesWithStatus(eventId, statuses, PagingParameters.EVERYTHING, subGroups);

        String description =
            messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(detailsKey + "description");
        List<InventoryIdentifier> inventoryIdentifiers = new ArrayList<>();
        broadcastEventDetails.stream().forEach(broadcastEventDetail -> inventoryIdentifiers.add(
            broadcastEventDetail.getHardware().getInventoryIdentifier()));

        InventoryCollection temporaryCollection =
            memoryCollectionProducer.createCollection(inventoryIdentifiers.iterator(), description);
        model.addAttribute("inventoryCollection", temporaryCollection);
        model.addAllAttributes(temporaryCollection.getCollectionParameters());

        return "redirect:/stars/operator/inventory/inventoryActions";

    }

    @GetMapping(value = "/rf/broadcast/eventDetail/collectionAction")
    public String collectionAction(String actionUrl, ModelMap model, long eventId, String[] deviceSubGroups,
            PerformanceVerificationMessageStatus[] statuses, YukonUserContext userContext) {
        List<DeviceGroup> subGroups = retrieveSubGroups(deviceSubGroups);
        List<BroadcastEventDeviceDetails> broadcastEventDetails =
            verificationService.getDevicesWithStatus(eventId, statuses, PagingParameters.EVERYTHING, subGroups);
        List<Integer> deviceIds = broadcastEventDetails.stream().map(rfBroadcastEventDetail -> rfBroadcastEventDetail.getHardware().getDeviceId())
                                                                .collect(Collectors.toList());
        List<SimpleDevice> devices = new ArrayList<>();
        devices.addAll(deviceDao.getYukonDeviceObjectByIds(deviceIds));
        StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(tempGroup, devices);

        return "redirect:" + actionUrl + "?collectionType=group&group.name=" + tempGroup.getFullName();
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
                         (t1, t2) -> t2.getTimeMessageSent().compareTo(t1.getTimeMessageSent()));
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

    private ScheduledRepeatingJob getJob(YukonJobDefinition<? extends YukonTask> jobDefinition) {
        List<ScheduledRepeatingJob> activeJobs = jobManager.getNotDeletedRepeatingJobsByDefinition(jobDefinition);
        return Iterables.getOnlyElement(activeJobs);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, final YukonUserContext userContext) {
        datePropertyEditorFactory.setupInstantPropertyEditor(binder, userContext, BlankMode.CURRENT);
    }

    @GetMapping("/rf/broadcast/eventDetail/{eventId}/downloadAll")
    public void downloadAll(HttpServletResponse response, YukonUserContext userContext, @PathVariable long eventId)
            throws IOException {
        // get the header row
        String[] headerRow = getDownloadHeaderRow(userContext);
        // get the data rows
        List<String[]> dataRows = getDownloadDataRows(userContext, eventId, null,
            getStatusTypes().toArray(new PerformanceVerificationMessageStatus[getStatusTypes().size()]));
        // write out the file
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "RfBroadcastPerformance_" + eventId + ".csv");
    }

    @GetMapping("/rf/broadcast/downloadFilteredResults")
    public void downloadFilteredRF(HttpServletResponse response, YukonUserContext userContext, long eventId,
            String[] deviceSubGroups, PerformanceVerificationMessageStatus[] statuses) throws IOException {
        // get the header row
        String[] headerRow = getDownloadHeaderRow(userContext);
        // get the data rows
        List<String[]> dataRows = getDownloadDataRows(userContext, eventId, deviceSubGroups, statuses);
        // write out the file
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "RfBroadcastPerformance_" + eventId + ".csv");
    }

    private String[] getDownloadHeaderRow(YukonUserContext userContext) {
        MessageSourceAccessor Accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        // header row
        String[] headerRow = new String[5];
        headerRow[0] = Accessor.getMessage(EventDetailSortBy.DEVICE_NAME);
        headerRow[1] = Accessor.getMessage(EventDetailSortBy.DEVICE_TYPE);
        headerRow[2] = Accessor.getMessage(EventDetailSortBy.ACCOUNT_NUMBER);
        headerRow[3] = Accessor.getMessage(EventDetailSortBy.CURRENT_STATUS);
        headerRow[4] = Accessor.getMessage(detailsKey + "LAST_COMMUNICATION");

        return headerRow;
    }

    private List<String[]> getDownloadDataRows(YukonUserContext userContext, long eventId, String[] deviceSubGroups,
            PerformanceVerificationMessageStatus[] statuses) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        List<BroadcastEventDeviceDetails> broadcastEventDetails =
            verificationService.getDevicesWithStatus(eventId, statuses, PagingParameters.EVERYTHING, retrieveSubGroups(deviceSubGroups));
        sortBroadcastEventDetails(broadcastEventDetails, null, EventDetailSortBy.DEVICE_NAME, userContext);
        int totalEventCount = verificationService.getTotalCount(eventId, statuses, retrieveSubGroups(deviceSubGroups));
        SearchResults<BroadcastEventDeviceDetails> searchResults =
            SearchResults.pageBasedForSublist(broadcastEventDetails, PagingParameters.EVERYTHING, totalEventCount);

        List<String[]> dataRows = Lists.newArrayList();
        searchResults.getResultList().forEach(details -> {
            String[] dataRow = new String[5];

            dataRow[0] = deviceDao.getFormattedName(details.getHardware().getDeviceId());
            dataRow[1] = accessor.getMessage(details.getHardware().getInventoryIdentifier().getHardwareType());
            dataRow[2] = details.getHardware().getAccountNo();
            dataRow[3] = accessor.getMessage(details.getDeviceStatus().getFormatKey());
            dataRow[4] = details.getLastComm() != null ? details.getLastComm().toString()
                : accessor.getMessage(detailsKey + "lastCommNotAvailable");
            dataRows.add(dataRow);
        });

        return dataRows;
    }

    /**
     * This method populate the device count and status for Unreported devices to be displayed on event detail
     * page on Results Unreported grey box tool tip.
     */
    private void unReportedDeviceText(ModelMap model, long eventId, MessageSourceAccessor accessor) {
        Map<DeviceStatus, Integer> unReportedCountWithStatus = verificationService.getUnknownCounts(eventId);
        List<String> args = new ArrayList<>();
        Stream.of(DeviceStatus.values()).forEach(deviceStatus -> {
            Integer count =
                    unReportedCountWithStatus.get(deviceStatus) != null ? unReportedCountWithStatus.get(deviceStatus) : 0;
            String status = accessor.getMessage(deviceStatus.getFormatKey());
            args.add(status + " : " + count.toString());
        });
        String unReportedDeviceText = accessor.getMessage(detailsKey + "unReportedDeviceCountText", args.toArray());
        model.put("unReportedDeviceText", unReportedDeviceText);
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