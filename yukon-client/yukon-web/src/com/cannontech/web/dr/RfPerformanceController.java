package com.cannontech.web.dr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.DeviceMemoryCollectionProducer;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationMessageStatus;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.dr.rfn.model.UnknownDevice;
import com.cannontech.dr.rfn.model.UnknownDevices;
import com.cannontech.dr.rfn.model.UnknownStatus;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.model.LiteLmHardware;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dr.model.RfPerformanceSettings;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.util.WebFileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@CheckRole(YukonRole.DEMAND_RESPONSE)
public class RfPerformanceController {

    private static final String homeKey = "yukon.web.modules.dr.home.rfPerformance.";
    private static final String detailsKey = "yukon.web.modules.dr.rf.details.";
    private static final Logger log = YukonLogManager.getLogger(RfPerformanceController.class);
    
    @Autowired private PaoDao paoDao;
    @Autowired private JobManager jobManager;
    @Autowired private ScheduledRepeatingJobDao jobDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private PerformanceVerificationDao rfPerformanceDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private MemoryCollectionProducer memoryCollectionProducer;
    @Autowired private DeviceMemoryCollectionProducer deviceMemoryCollectionProducer;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private DateFormattingService dateFormattingService;
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
    
    @RequestMapping(value="/rf/details/{type}/{test}/page", method=RequestMethod.GET)
    public String page(ModelMap model, @PathVariable String type, @PathVariable long test, 
            PagingParameters pagingParameters) {

        List<PaoIdentifier> paos = new ArrayList<>();
        PerformanceVerificationMessageStatus status;
        int totalCount = 0;
        if (type.equals(UNREPORTED)) {
            model.addAttribute(UNREPORTED, true);
            UnknownDevices devices = 
                    rfPerformanceDao.getDevicesWithUnknownStatus(test, pagingParameters);
            totalCount = devices.getNumTotalBeforePaging();
            Map<Integer, UnknownDevice> unknowns = new HashMap<>();
            for (UnknownDevice device : devices.getUnknownDevices()) {
                unknowns.put(device.getPaoIdentifier().getPaoId(), device);
                paos.add(device.getPaoIdentifier());
            }
            model.addAttribute("unknowns", unknowns);
            status = PerformanceVerificationMessageStatus.UNKNOWN;
        } else if (type.equalsIgnoreCase("failed")) {
            status = PerformanceVerificationMessageStatus.FAILURE;
            paos = rfPerformanceDao.getDevicesWithStatus(test, status, pagingParameters);
            totalCount = rfPerformanceDao.getNumberOfDevices(test, status);
        } else {
            status = PerformanceVerificationMessageStatus.SUCCESS;
            paos = rfPerformanceDao.getDevicesWithStatus(test, status, pagingParameters);
            totalCount = rfPerformanceDao.getNumberOfDevices(test, status);
        }

        List<LiteLmHardware> hardwares = inventoryDao.getLiteLmHardwareByPaos(paos);
        SearchResults<LiteLmHardware> result = 
                SearchResults.pageBasedForSublist(hardwares, pagingParameters, totalCount);
        
        model.addAttribute("type", type);
        model.addAttribute("result", result);
        model.addAttribute("test", test);
        
        return "dr/rf/table.jsp";
    }
    
    @RequestMapping(value="/rf/details/{type}/{test}", method=RequestMethod.GET)
    public String popup(ModelMap model, HttpServletResponse resp, YukonUserContext userContext, @PathVariable long test, 
            @PathVariable String type, PagingParameters pagingParameters) throws JsonProcessingException {
        
        List<PaoIdentifier> paos = new ArrayList<>();
        PerformanceVerificationMessageStatus status;
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        String eventTime = dateFormattingService.format(rfPerformanceDao.getEventTime(test), DateFormatEnum.DATEHM, userContext);
        model.addAttribute("title", accessor.getMessage(detailsKey + type + ".popup.title", eventTime));
        
        int totalCount;
        if (type.equalsIgnoreCase("failed")) {
            status = PerformanceVerificationMessageStatus.FAILURE;
            paos = rfPerformanceDao.getDevicesWithStatus(test, status, pagingParameters);
            totalCount = rfPerformanceDao.getNumberOfDevices(test, status);
        } else if (type.equalsIgnoreCase("success")) {
            status = PerformanceVerificationMessageStatus.SUCCESS;
            paos = rfPerformanceDao.getDevicesWithStatus(test, status, pagingParameters);
            totalCount = rfPerformanceDao.getNumberOfDevices(test, status);
        } else {
            model.addAttribute(UNREPORTED, true);
            status = PerformanceVerificationMessageStatus.UNKNOWN;
            UnknownDevices devices = 
                    rfPerformanceDao.getDevicesWithUnknownStatus(test, pagingParameters);
            totalCount = devices.getNumTotalBeforePaging();

            model.addAttribute("unknownStats", devices);
            
            Map<Integer, UnknownDevice> unknowns = new HashMap<>();
            for (UnknownDevice device : devices.getUnknownDevices()) {
                unknowns.put(device.getPaoIdentifier().getPaoId(), device);
                paos.add(device.getPaoIdentifier());
            }
            model.addAttribute("unknowns", unknowns);
            
            // build json for pie chart
            List<Map<String, Object>> data = new ArrayList<>(PerformanceVerificationMessageStatus.values().length);
            
            Map<String, Object> stat = Maps.newHashMapWithExpectedSize(3);
            stat.put("label", accessor.getMessage(UnknownStatus.COMMUNICATING));
            stat.put("data", devices.getNumCommunicating());
            stat.put("color", "#009933");
            data.add(stat);
            
            stat = Maps.newHashMapWithExpectedSize(3);
            stat.put("label", accessor.getMessage(UnknownStatus.NOT_COMMUNICATING));
            stat.put("data", devices.getNumNotCommunicating());
            stat.put("color", "#fb8521");
            data.add(stat);
            
            stat = Maps.newHashMapWithExpectedSize(3);
            stat.put("label", accessor.getMessage(UnknownStatus.NEW_INSTALL_NOT_COMMUNICATING));
            stat.put("data", devices.getNumNewInstallNotCommunicating());
            stat.put("color", "#4d90fe");
            data.add(stat);

            resp.addHeader("X-JSON", JsonUtils.toJson(data));
        }
        
        List<LiteLmHardware> hardwares = inventoryDao.getLiteLmHardwareByPaos(paos);
        SearchResults<LiteLmHardware> result = SearchResults.pageBasedForSublist(hardwares, pagingParameters, totalCount);
        
        model.addAttribute("type", type);
        model.addAttribute("result", result);
        model.addAttribute("test", test);
        model.addAttribute("devices", hardwares);
        
        return "dr/rf/popup.jsp";
    }
    
    @RequestMapping("/rf/details/{type}/{test}/download")
    public void download(HttpServletResponse response, YukonUserContext userContext, @PathVariable String type, @PathVariable long test) throws IOException {
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        boolean isUnreported = type.equalsIgnoreCase(UNREPORTED);
        List<PaoIdentifier> paos = new ArrayList<>();
        Map<Integer, UnknownDevice> deviceMap = new HashMap<>();
        
        if (isUnreported) {
            UnknownDevices unknownDevices = rfPerformanceDao.getAllDevicesWithUnknownStatus(test);
            for (UnknownDevice device : unknownDevices.getUnknownDevices()) {
                deviceMap.put(device.getPao().getPaoIdentifier().getPaoId(), device);
                paos.add(device.getPao().getPaoIdentifier());
            }
        } else {
            if (type.equalsIgnoreCase("failed")) {
                paos = rfPerformanceDao.getAllDevicesWithStatus(test, PerformanceVerificationMessageStatus.FAILURE);
            } else {
                paos = rfPerformanceDao.getAllDevicesWithStatus(test, PerformanceVerificationMessageStatus.SUCCESS);
            }
        }
        
        List<LiteLmHardware> hardwares = inventoryDao.getLiteLmHardwareByPaos(paos);
        
        String[] headerRow = new String[isUnreported ? 4 : 3];
        
        headerRow[0] = "SERIAL_NUMBER";
        headerRow[1] = "DEVICE_TYPE";
        headerRow[2] = "ACCOUNT_NUMBER";
        if (isUnreported) {
            headerRow[3] = "UNKNOWN_STATUS";
        }
        
        List<String[]> dataRows = Lists.newArrayList();
        for (LiteLmHardware hardware: hardwares) {
            String[] dataRow = new String[4];
            dataRow[0] = hardware.getSerialNumber();
            dataRow[1] = accessor.getMessage(hardware.getInventoryIdentifier().getHardwareType());
            dataRow[2] = hardware.getAccountNo();
            if (isUnreported) {
                dataRow[3] = accessor.getMessage(deviceMap.get(hardware.getDeviceId()).getUnknownStatus());
            }
            dataRows.add(dataRow);
        }
        
        //write out the file
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "RfBroadcastPerformance_" + type + ".csv");
    }
    
    @RequestMapping("/rf/details/{type}/{test}/inventoryAction")
    public String inventoryAction(ModelMap model, YukonUserContext userContext, @PathVariable String type, @PathVariable long test) {
        
        List<? extends YukonPao> paos;
        
        if (type.equalsIgnoreCase(UNREPORTED)) {
            paos = rfPerformanceDao.getAllDevicesWithUnknownStatus(test).getUnknownDevices();
        } else if (type.equalsIgnoreCase("failed")) {
            paos = rfPerformanceDao.getAllDevicesWithStatus(test, PerformanceVerificationMessageStatus.FAILURE);
        } else {
            paos = rfPerformanceDao.getAllDevicesWithStatus(test, PerformanceVerificationMessageStatus.SUCCESS);
        }

        List<Integer> deviceIds = Lists.transform(paos, PaoUtils.getYukonPaoToPaoIdFunction());
        List<InventoryIdentifier> inventory = inventoryDao.getYukonInventoryForDeviceIds(deviceIds);
        
        String description = resolver.getMessageSourceAccessor(userContext).getMessage(detailsKey + "action." + type + ".description");
        
        InventoryCollection temporaryCollection = memoryCollectionProducer.createCollection(inventory.iterator(), description);
        model.addAttribute("inventoryCollection", temporaryCollection);
        model.addAllAttributes(temporaryCollection.getCollectionParameters());
        
        return "redirect:/stars/operator/inventory/inventoryActions";
    }
    
    @RequestMapping("/rf/details/{type}/{test}/collectionAction")
    public String collectionAction(ModelMap model, @PathVariable String type, @PathVariable long test) {
        
        List<? extends YukonPao> paos;
        
        if (type.equalsIgnoreCase(UNREPORTED)) {
            paos = rfPerformanceDao.getAllDevicesWithUnknownStatus(test).getUnknownDevices();
        } else if (type.equalsIgnoreCase("failed")) {
            paos = rfPerformanceDao.getAllDevicesWithStatus(test, PerformanceVerificationMessageStatus.FAILURE);
        } else {
            paos = rfPerformanceDao.getAllDevicesWithStatus(test, PerformanceVerificationMessageStatus.SUCCESS);
        }
        
        DeviceCollection temporaryCollection = deviceMemoryCollectionProducer.createDeviceCollection(paos);
        model.addAttribute("deviceCollection", temporaryCollection);
        model.addAllAttributes(temporaryCollection.getCollectionParameters());
        
        return "redirect:/bulk/collectionActions";
    }
    
    private ScheduledRepeatingJob getJob(YukonJobDefinition<? extends YukonTask> jobDefinition) {
        List<ScheduledRepeatingJob> activeJobs = jobManager.getNotDeletedRepeatingJobsByDefinition(jobDefinition);
        return Iterables.getOnlyElement(activeJobs);
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, final YukonUserContext userContext) {
        datePropertyEditorFactory.setupInstantPropertyEditor(binder, userContext, BlankMode.CURRENT);
    }
    
}