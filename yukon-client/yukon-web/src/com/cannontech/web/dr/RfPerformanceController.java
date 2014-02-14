package com.cannontech.web.dr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
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
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.util.JsonUtils;
import com.cannontech.web.util.WebFileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEMAND_RESPONSE)
public class RfPerformanceController {

    private static final String baseKey = "yukon.web.modules.dr.home.rfPerformance.";
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
    
    @Autowired @Qualifier("rfnPerformanceVerification")
        private YukonJobDefinition<RfnPerformanceVerificationTask> rfnVerificationJobDef;
    @Autowired @Qualifier("rfnPerformanceVerificationEmail")
        private YukonJobDefinition<RfnPerformanceVerificationEmailTask> rfnEmailJobDef;
    
    /**
     * Update settings for rf performance test command and email generation
     */
    @RequestMapping(value="/rf/performance", method=RequestMethod.POST)
    public String saveSettings(@ModelAttribute("settings") RfPerformanceSettings settings, YukonUserContext userContext, FlashScope flash) {
        
        LocalTime commandTime = LocalTime.MIDNIGHT.plusMinutes(settings.getTime());
        LocalTime emailTime = LocalTime.MIDNIGHT.plusMinutes(settings.getEmailTime());
        
        StringBuilder commandCron = new StringBuilder("0 ");
        commandCron.append(commandTime.getMinuteOfHour() + " ");
        commandCron.append(commandTime.getHourOfDay() + " * * ?");
        
        StringBuilder emailCron = new StringBuilder("0 ");
        emailCron.append(emailTime.getMinuteOfHour() + " ");
        emailCron.append(emailTime.getHourOfDay() + " * * ?");
        
        ScheduledRepeatingJob commandJob = getJob(rfnVerificationJobDef);
        commandJob.setSystemUser(true);  // important!
        
        try {
            if (!commandCron.toString().equals(commandJob.getCronString())) {
                jobManager.replaceScheduledJob(commandJob.getId(), 
                                               rfnVerificationJobDef, 
                                               commandJob.getJobDefinition().createBean(), 
                                               commandCron.toString(), 
                                               null); // system user!
            }
            
            ScheduledRepeatingJob emailJob = getJob(rfnEmailJobDef);
            emailJob.getJobProperties().put("notificationGroups", StringUtils.join(settings.getNotifGroupIds(), ","));
//            emailJob.getJobProperties().put("additionalEmails", "");
            
            if (settings.isEmail()) {
                // Email setting is enabled. We might need to update the cron string.
            if (!emailCron.toString().equals(emailJob.getCronString())) {
                jobManager.replaceScheduledJob(emailJob.getId(), 
                        rfnEmailJobDef, 
                        emailJob.getJobDefinition().createBean(), 
                        emailCron.toString(), 
                            null,
                            emailJob.getJobProperties()); 
                
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
            
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "configure.success"));
        } catch (Exception e) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "configure.failed"));
            log.error("Failed to update RF Broadcast Performance Email Job", e);
        }
        
        return "redirect:/dr/home";
    }
    
    @RequestMapping(value="/rf/details/day", method=RequestMethod.GET)
    public String detailsDay(ModelMap model) {
        
        Instant to = new Instant();
        model.addAttribute("to", new Instant());
        Instant from = to.minus(Duration.standardDays(1));
        model.addAttribute("from", from);
        
        detailsModel(model, from, to);
        
        return "dr/rf/details.jsp";
    }
    
    @RequestMapping(value="/rf/details/week", method=RequestMethod.GET)
    public String detailsWeek(ModelMap model) {
        
        Instant to = new Instant();
        model.addAttribute("to", new Instant());
        Instant from = to.minus(Duration.standardDays(7));
        model.addAttribute("from", from);
        
        detailsModel(model, from, to);
        
        return "dr/rf/details.jsp";
    }
    
    @RequestMapping(value="/rf/details/month", method=RequestMethod.GET)
    public String detailsMonth(ModelMap model) {
        
        Instant to = new Instant();
        model.addAttribute("to", new Instant());
        Instant from = to.minus(Duration.standardDays(30));
        model.addAttribute("from", from);
        
        detailsModel(model, from, to);
        
        return "dr/rf/details.jsp";
    }
    
    @RequestMapping(value="/rf/details", method=RequestMethod.GET)
    public String details(ModelMap model, @RequestParam(required=false) Instant from, @RequestParam(required=false) Instant to) {
        
        if (from == null) from =  new Instant().minus(Duration.standardDays(7));
        if (to == null) to =  new Instant();
        Instant toFullDay = to.plus(Duration.standardDays(1)).toDateTime().toDateMidnight().toInstant();
        
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        
        detailsModel(model, from, toFullDay);
        
        return "dr/rf/details.jsp";
    }
    
    private void detailsModel(ModelMap model, Instant from, Instant to) {
        List<PerformanceVerificationEventMessageStats> tests = rfPerformanceDao.getReports(Range.inclusiveExclusive(from, to));
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
    }
    
    @RequestMapping(value="/rf/details/unknown/{test}/page", method=RequestMethod.GET)
    public String unknownPage(ModelMap model,
            YukonUserContext userContext,
            @PathVariable long test, 
            @RequestParam(defaultValue="10") Integer itemsPerPage, 
            @RequestParam(defaultValue="1") Integer page) {
        
        UnknownDevices unknownDevices = rfPerformanceDao.getDevicesWithUnknownStatus(test);
        SearchResults<UnknownDevice> result = SearchResults.pageBasedForWholeList(page, itemsPerPage, unknownDevices.getUnknownDevices());
        
        model.addAttribute("result", result);
        model.addAttribute("test", test);
        
        return "dr/rf/page.jsp";
    }
    
    @RequestMapping(value="/rf/details/unknown/{test}", method=RequestMethod.GET)
    public String unknown(ModelMap model, HttpServletResponse resp,
            YukonUserContext userContext,
            @PathVariable long test, 
            @RequestParam(defaultValue="10") Integer itemsPerPage, 
            @RequestParam(defaultValue="1") Integer page) throws JsonProcessingException {
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        
        UnknownDevices unknownDevices = rfPerformanceDao.getDevicesWithUnknownStatus(test);
        SearchResults<UnknownDevice> result = 
                SearchResults.pageBasedForWholeList(page, itemsPerPage, unknownDevices.getUnknownDevices());
        
        model.addAttribute("result", result);
        
        List<Map<String, Object>> data = new ArrayList<>();
        
        Map<String, Object> stat = Maps.newHashMapWithExpectedSize(3);
        stat.put("label", accessor.getMessage(UnknownStatus.ACTIVE));
        stat.put("data", unknownDevices.getNumActive());
        stat.put("color", "#009933");
        data.add(stat);
        
        stat = Maps.newHashMapWithExpectedSize(3);
        stat.put("label", accessor.getMessage(UnknownStatus.INACTIVE));
        stat.put("data", unknownDevices.getNumInactive());
        stat.put("color", "#888888");
        data.add(stat);
        
        stat = Maps.newHashMapWithExpectedSize(3);
        stat.put("label", accessor.getMessage(UnknownStatus.UNAVAILABLE));
        stat.put("data", unknownDevices.getNumUnavailable());
        stat.put("color", "#fb8521");
        data.add(stat);
        
        stat = Maps.newHashMapWithExpectedSize(3);
        stat.put("label", accessor.getMessage(UnknownStatus.UNREPORTED_NEW));
        stat.put("data", unknownDevices.getNumUnreportedNew());
        stat.put("color", "#4d90fe");
        data.add(stat);
        
        stat = Maps.newHashMapWithExpectedSize(3);
        stat.put("label", accessor.getMessage(UnknownStatus.UNREPORTED_OLD));
        stat.put("data", unknownDevices.getNumUnreportedOld());
        stat.put("color", "#D14836");
        data.add(stat);
        
        resp.addHeader("X-JSON", JsonUtils.toJson(data));
        model.addAttribute("test", test);
        model.addAttribute("unknownDevices", unknownDevices);

        return "dr/rf/unknown.jsp";
    }
    
    @RequestMapping("/rf/details/unknown/{test}/download")
    public void download(HttpServletResponse response, YukonUserContext userContext, @PathVariable long test) throws IOException {
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        
        UnknownDevices unknownDevices = rfPerformanceDao.getDevicesWithUnknownStatus(test);
        
        Map<Integer, UnknownDevice> deviceMap = new HashMap<>(unknownDevices.getUnknownDevices().size());
        List<PaoIdentifier> paos = new ArrayList<>(unknownDevices.getUnknownDevices().size());
        for (UnknownDevice device : unknownDevices.getUnknownDevices()) {
            deviceMap.put(device.getPao().getPaoIdentifier().getPaoId(), device);
            paos.add(device.getPao().getPaoIdentifier());
        }
        
        List<LiteLmHardware> hardwares = inventoryDao.getLiteLmHardwareByPaos(paos);
        
        String[] headerRow = new String[4];
        
        headerRow[0] = "SERIAL_NUMBER";
        headerRow[1] = "DEVICE_TYPE";
        headerRow[2] = "ACCOUNT_NUMBER";
        headerRow[3] = "UNKNOWN_STATUS";
        
        List<String[]> dataRows = Lists.newArrayList();
        for(LiteLmHardware hardware: hardwares) {
            String[] dataRow = new String[4];
            dataRow[0] = hardware.getSerialNumber();
            dataRow[1] = accessor.getMessage(hardware.getInventoryIdentifier().getHardwareType());
            dataRow[2] = hardware.getAccountNo();
            dataRow[3] = deviceMap.get(hardware.getDeviceId()).getUnknownStatus().name();
            dataRows.add(dataRow);
        }
        
        //write out the file
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "RfBroadcastPerformance_Unknown.csv");
    }
    
    @RequestMapping("/rf/details/unknown/{test}/inventoryAction")
    public String inventoryAction(ModelMap model, YukonUserContext userContext, @PathVariable long test) {
        
        UnknownDevices unknownDevices = rfPerformanceDao.getDevicesWithUnknownStatus(test);
        List<Integer> deviceIds = Lists.transform(unknownDevices.getUnknownDevices(), new Function<UnknownDevice, Integer>() {
            @Override
            public Integer apply(UnknownDevice input) {
                return input.getPao().getPaoIdentifier().getPaoId();
            }
            
        });
        List<InventoryIdentifier> inventory = inventoryDao.getYukonInventoryForDeviceIds(deviceIds);
        
        String description = resolver.getMessageSourceAccessor(userContext).getMessage("yukon.web.modules.dr.rf.details.actionUnknown.description");
        InventoryCollection temporaryCollection = memoryCollectionProducer.createCollection(inventory.iterator(), description);
        model.addAttribute("inventoryCollection", temporaryCollection);
        model.addAllAttributes(temporaryCollection.getCollectionParameters());
        return "redirect:/stars/operator/inventory/inventoryActions";
    }
    
    @RequestMapping("/rf/details/unknown/{test}/collectionAction")
    public String collectionAction(ModelMap model, YukonUserContext userContext, @PathVariable long test) {
        
        UnknownDevices unknownDevices = rfPerformanceDao.getDevicesWithUnknownStatus(test);
        List<YukonPao> paos = Lists.transform(unknownDevices.getUnknownDevices(), new Function<UnknownDevice, YukonPao>() {
            @Override
            public YukonPao apply(UnknownDevice input) {
                return input.getPao();
            }
            
        });
        
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