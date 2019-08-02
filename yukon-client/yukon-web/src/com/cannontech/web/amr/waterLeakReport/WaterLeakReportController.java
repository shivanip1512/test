package com.cannontech.web.amr.waterLeakReport;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.DeviceCollectionService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.scheduledFileExport.ScheduledExportType;
import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.common.scheduledFileExport.WaterLeakExportGenerationParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.StringUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronException;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronTagStyleType;
import com.cannontech.web.amr.util.cronExpressionTag.handler.CustomCronTagStyleHandler;
import com.cannontech.web.amr.waterLeakReport.model.SortBy;
import com.cannontech.web.amr.waterLeakReport.model.WaterLeakReportFilter;
import com.cannontech.web.amr.waterLeakReport.model.WaterMeterLeak;
import com.cannontech.web.amr.waterLeakReport.service.WaterMeterLeakService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.multispeak.MspHandler;
import com.cannontech.web.scheduledFileExport.ScheduledFileExportHelper;
import com.cannontech.web.scheduledFileExport.ScheduledFileExportJobData;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledWaterLeakFileExportTask;
import com.cannontech.web.scheduledFileExport.validator.ScheduledFileExportValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

@CheckRoleProperty(YukonRoleProperty.WATER_LEAK_REPORT)
@Controller
@RequestMapping("/waterLeakReport/*")
public class WaterLeakReportController {
    
    @Autowired private ContactDao contactDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private DeviceGroupCollectionHelper collectionHelper;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private MeterDao meterDao;
    @Autowired private PaoDao paoDao;
    @Autowired private WaterMeterLeakService leaksService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private ScheduledFileExportService scheduledFileExportService;
    @Autowired private JobManager jobManager;
    @Autowired private ScheduledFileExportHelper exportHelper;
    @Autowired private MspHandler mspHandler;
    @Autowired private DeviceCollectionService deviceCollectionService;
    
    private ScheduledFileExportValidator scheduledFileExportValidator;
    private final static String baseKey = "yukon.web.modules.amr.waterLeakReport.report";
    private final static Hours water_node_reporting_interval = Hours.hours(24);
    private Map<SortBy, Comparator<WaterMeterLeak>> sorters;
    
    @PostConstruct
    public void initialize() {
        Builder<SortBy, Comparator<WaterMeterLeak>> builder = ImmutableMap.builder();
        builder.put(SortBy.DEVICE_NAME, getMeterNameComparator());
        builder.put(SortBy.METER_NUMBER, getMeterNumberComparator());
        builder.put(SortBy.PAO_TYPE, getDeviceTypeComparator());
        builder.put(SortBy.LEAK_RATE, getLeakRateComparator());
        builder.put(SortBy.USAGE, getUsageComparator());
        builder.put(SortBy.DATE, getDateComparator());
        sorters = builder.build();
    }

    private Validator filterValidator =
        new SimpleValidator<WaterLeakReportFilter>(WaterLeakReportFilter.class) {
            @Override
            public void doValidation(WaterLeakReportFilter filter, Errors errors) {
                // Dates & Hours
                if (filter.getFromInstant() == null && !errors.hasFieldErrors("fromInstant")) {
                    errors.rejectValue("fromInstant", "yukon.web.error.date.validRequired");
                } else if (filter.getToInstant() == null && !errors.hasFieldErrors("toInstant")) {
                    errors.rejectValue("toInstant", "yukon.web.error.date.validRequired");
                } else if(filter.getFromInstant().isAfterNow()) {
                    // If the from date is in the future
                    errors.rejectValue("fromInstant", "yukon.web.error.date.inThePast");
                } else if (filter.getFromInstant().isAfter(filter.getToInstant())) {
                    errors.rejectValue("fromInstant", "yukon.web.error.date.fromAfterTo");
                } else if(filter.getToInstant().isAfterNow()) {
                    // If the to date is in the future
                    errors.rejectValue("toInstant", "yukon.web.error.date.inThePast");
                } else if (Hours.hoursBetween(filter.getFromInstant(), filter.getToInstant()).getHours() < 24) {
                    // FromDate and ToDate should be atleast 24 hours apart
                    errors.rejectValue("toInstant", "yukon.web.error.date.dateDifference");
                }

                // Threshold
                if (filter.getThreshold() < 0) {
                    errors.rejectValue("threshold", baseKey + ".validation.thresholdNegative");
                }
            }
        };

    @RequestMapping(value="report", method = RequestMethod.GET)
    public String report(@ModelAttribute("filter") WaterLeakReportFilter filter,
                         BindingResult bindingResult, 
                         HttpServletRequest request, 
                         ModelMap model,
                         FlashScope flashScope, 
                         YukonUserContext userContext,
                         Integer jobId,
                         @DefaultItemsPerPage(10) PagingParameters paging,
                         @DefaultSort(dir=Direction.desc, sort="LEAK_RATE") SortingParameters sorting)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        
        if (jobId != null) {
            // Editing existing scheduled export
            model.addAttribute("jobId", jobId);
            ScheduledRepeatingJob job = jobManager.getRepeatingJob(jobId);
            ScheduledWaterLeakFileExportTask task = (ScheduledWaterLeakFileExportTask) jobManager.instantiateTask(job);
            model.addAttribute("task", task);
            
            // Populate report data
            DeviceCollection deviceCollection = deviceCollectionService.loadCollection(task.getDeviceCollectionId());
            filter.setDeviceCollection(deviceCollection);
            filter.setFromInstant(filter.getToInstant().minus(Duration.standardHours(task.getHoursPrevious())));
            filter.setIncludeDisabledPaos(task.isIncludeDisabledPaos());
            filter.setThreshold(task.getThreshold());

        } else {
            setupDeviceCollectionFromRequest(filter, request);
            filterValidator.validate(filter, bindingResult);
            if (bindingResult.hasErrors()) {
                List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
                flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
                return "waterLeakReport/report.jsp";
            }
        }
        
        List<WaterMeterLeak> leaks = leaksService.getLeaks(filter, userContext);
        setupFilterResults(filter, userContext, model, leaks, paging, sorting);
        
        // Add scheduled reports
        List<ScheduledFileExportJobData> jobs
            = scheduledFileExportService.getScheduledFileExportJobData(ScheduledExportType.WATER_LEAK);
        model.addAttribute("jobType", FileExportType.WATER_LEAK);
        model.addAttribute("jobs", jobs);
        
        return "waterLeakReport/report.jsp";
    }
    
    /** Get schedule popup */
    @RequestMapping(value="schedule", method = RequestMethod.GET)
    public String schedule(@ModelAttribute("filter") WaterLeakReportFilter filter, HttpServletRequest request,
            YukonUserContext userContext, ModelMap model, Integer jobId) throws ServletRequestBindingException, DeviceCollectionCreationException {
        CronExpressionTagState cronTagState = new CronExpressionTagState();
        ScheduledFileExportData exportData = new ScheduledFileExportData();
        
        if (jobId != null) {
            // populate schedule from job data
            model.addAttribute("jobId", jobId);
            ScheduledRepeatingJob job = jobManager.getRepeatingJob(jobId);
            ScheduledWaterLeakFileExportTask task = (ScheduledWaterLeakFileExportTask) jobManager.instantiateTask(job);
            
            DeviceCollection deviceCollection = deviceCollectionService.loadCollection(task.getDeviceCollectionId());
            model.addAttribute("deviceCollection", deviceCollection);
            model.addAttribute("includeDisabledPaos", task.isIncludeDisabledPaos());
            model.addAttribute("threshold", task.getThreshold());

            cronTagState = cronExpressionTagService.parse(job.getCronString(), job.getUserContext());
            exportData.setDaysOffset(task.getDaysOffset());
            exportData.setHoursPrevious(task.getHoursPrevious());
            exportData.setThreshold(task.getThreshold());
            exportData.setScheduleName(task.getName());
            exportData.setExportFileName(task.getExportFileName());
            exportData.setAppendDateToFileName(task.isAppendDateToFileName());
            exportData.setTimestampPatternField(task.getTimestampPatternField());
            exportData.setOverrideFileExtension(task.isOverrideFileExtension());
            exportData.setExportFileExtension(task.getExportFileExtension());
            exportData.setIncludeExportCopy(task.isIncludeExportCopy());
            exportData.setExportPath(task.getExportPath());
            if (task.getNotificationEmailAddresses() != null) {
                exportData.setNotificationEmailAddresses(task.getNotificationEmailAddresses());
                exportData.setSendEmail(task.isSendEmail());
            } else {
                exportData.setNotificationEmailAddresses(contactDao.getUserEmail(userContext.getYukonUser()));
            }
        } else {
            setupDeviceCollectionFromRequest(filter, request);
            model.addAttribute("deviceCollection", filter.getDeviceCollection());
            exportData.setThreshold(filter.getThreshold());
            exportData.setDaysOffset(0);
            exportData.setHoursPrevious(25);
            exportData.setNotificationEmailAddresses(contactDao.getUserEmail(userContext.getYukonUser()));
        }
        
        model.addAttribute("cronExpressionTagState", cronTagState);
        model.addAttribute("fileExportData", exportData);
        model.addAttribute("fileExtensionChoices", exportHelper.setupFileExtChoices(exportData));
        model.addAttribute("exportPathChoices", exportHelper.setupExportPathChoices(exportData));
        boolean isSmtpConfigured = org.apache.commons.lang3.StringUtils.isBlank(globalSettingDao.getString(GlobalSettingType.SMTP_HOST));
        model.addAttribute("isSmtpConfigured", isSmtpConfigured);
        
        return "waterLeakReport/schedule.jsp";
    }
    
    /** Save schedule */
    @RequestMapping(value="schedule", method = RequestMethod.POST)
    public String schedule(HttpServletRequest request, HttpServletResponse response,
            ModelMap model,
            YukonUserContext userContext,
            FlashScope flashScope,
            @ModelAttribute("fileExportData") ScheduledFileExportData exportData,
            BindingResult bindingResult, 
            @RequestParam(defaultValue="false") Boolean includeDisabledPaos,
            DeviceCollection collection,
            Integer jobId) throws ServletRequestBindingException, ParseException {
        
        try {
            String scheduleCronString = cronExpressionTagService.build("scheduleCronString", request, userContext);
            exportData.setScheduleCronString(scheduleCronString);
        } catch (CronException cronException) {
            bindingResult.rejectValue("scheduleCronString", "yukon.common.invalidCron");
        }

        scheduledFileExportValidator = new ScheduledFileExportValidator(this.getClass());
        scheduledFileExportValidator.validate(exportData, bindingResult);
        
        if (bindingResult.hasErrors()) {
            //send it back
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            if (jobId != null) model.addAttribute("jobId", jobId);
            model.addAttribute("includeDisabledPaos", includeDisabledPaos);
            model.addAttribute("deviceCollection", collection);
            model.addAttribute("fileExtensionChoices", exportHelper.setupFileExtChoices(exportData));
            model.addAttribute("exportPathChoices", exportHelper.setupExportPathChoices(exportData));
            if (bindingResult.hasFieldErrors("scheduleCronString")) {
                CronExpressionTagState state = new CronExpressionTagState();
                state.setCronTagStyleType(CronTagStyleType.CUSTOM);
                state.setCustomExpression(CustomCronTagStyleHandler.getCustomExpression("scheduleCronString", request));
                model.addAttribute("cronExpressionTagState", state);
                model.addAttribute("invalidCronString", true);
            } else {
                model.addAttribute("cronExpressionTagState",
                    cronExpressionTagService.parse(exportData.getScheduleCronString(), userContext));
            }
            
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            
            return "waterLeakReport/schedule.jsp";
        }
        
        WaterLeakExportGenerationParameters parameters = new WaterLeakExportGenerationParameters(collection,
            exportData.getDaysOffset(), exportData.getHoursPrevious(), exportData.getThreshold(), includeDisabledPaos);
        exportData.setParameters(parameters);
        
        if (jobId == null) {
            scheduledFileExportService.scheduleFileExport(exportData, userContext, request);
            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".scheduleSuccess", exportData.getScheduleName()));
        } else {
            scheduledFileExportService.updateFileExport(exportData, userContext, request, jobId);
            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".updateSuccess", exportData.getScheduleName()));
        }
        
        return null;
    }

    @RequestMapping("/jobs/{jobId}/enable")
    public String enableJob(@PathVariable int jobId, ModelMap model, FlashScope flashScope, YukonUserContext userContext) {
        YukonJob job = jobManager.getJob(jobId);
        ScheduledWaterLeakFileExportTask task = (ScheduledWaterLeakFileExportTask) jobManager.instantiateTask(job);
        String jobName = task.getName();
        jobManager.enableJob(job);
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".enableJobSuccess", jobName));
        return "redirect:/amr/waterLeakReport/report";
    }

    @RequestMapping("/jobs/{jobId}/disable")
    public String disableJob(@PathVariable int jobId, ModelMap model, FlashScope flashScope,
            YukonUserContext userContext) {
        YukonJob job = jobManager.getJob(jobId);
        ScheduledWaterLeakFileExportTask task = (ScheduledWaterLeakFileExportTask) jobManager.instantiateTask(job);
        String jobName = task.getName();
        jobManager.disableJob(job);
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".disableJobSuccess", jobName));
        return "redirect:/amr/waterLeakReport/report";
    }

    @RequestMapping("delete")
    public void delete(ModelMap model, int jobId, YukonUserContext userContext, FlashScope flash,
            HttpServletResponse response) {
        
        YukonJob job = jobManager.getJob(jobId);
        ScheduledWaterLeakFileExportTask task = (ScheduledWaterLeakFileExportTask) jobManager.instantiateTask(job);
        String jobName = task.getName();
        
        jobManager.deleteJob(job);
        
        int deviceCollectionId = task.getDeviceCollectionId();
        deviceCollectionService.deleteCollection(deviceCollectionId);
        
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".deletedSuccess", jobName));
        
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    @RequestMapping(value="intervalData", method = RequestMethod.GET)
    public String intervalData(@ModelAttribute("filter") WaterLeakReportFilter filter,
                               ModelMap model, 
                               YukonUserContext userContext,
                               Integer[] paoIds,
                               @DefaultItemsPerPage(25) PagingParameters paging,
                               @DefaultSort(dir=Direction.desc, sort="DATE") SortingParameters sorting) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        // set device collection BEFORE getting leaks
        setupDeviceCollectionForIntervalData(filter, paoIds, userContext);
        List<WaterMeterLeak> waterLeaks = leaksService.getIntervalLeaks(filter, userContext);
        setupFilterResults(filter, userContext, model, waterLeaks, paging, sorting);
        
        String usageText = accessor.getMessage(SortBy.USAGE);
        SortableColumn usageColumn = SortableColumn.of(sorting.getDirection(), 
                SortBy.valueOf(sorting.getSort()) == SortBy.USAGE, usageText, SortBy.USAGE.name());
        model.addAttribute("usageColumn", usageColumn);
        
        String dateText = accessor.getMessage(SortBy.DATE);
        SortableColumn dateColumn = SortableColumn.of(sorting.getDirection(), 
                SortBy.valueOf(sorting.getSort()) == SortBy.DATE, dateText, SortBy.DATE.name());
        model.addAttribute("dateColumn", dateColumn);
        
        model.addAttribute("paoIds", StringUtils.toStringList((Object[])paoIds));
        
        return "waterLeakReport/intervalData.jsp";
    }

    @RequestMapping(value = "cisDetails", method = RequestMethod.GET)
    public String cisDetails(ModelMap model, YukonUserContext userContext, int paoId) {
        MultiSpeakVersion multiSpeakVersion = mspHandler.getMSPVersion();
        model.addAttribute("multiSpeakVersion", multiSpeakVersion);
        return mspHandler.getCisDetails(model, userContext, paoId);
    }

    @RequestMapping(value="leaks-csv", method = RequestMethod.GET)
    public String leaksCsv(@ModelAttribute("filter") WaterLeakReportFilter filter,
                               ModelMap model, 
                               HttpServletRequest request,
                               HttpServletResponse response,
                               YukonUserContext userContext) throws IOException,
            ServletRequestBindingException, DeviceCollectionCreationException {

        setupDeviceCollectionFromRequest(filter, request);
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);

        // header row
        String[] headerRow = new String[4];
        headerRow[0] = messageSourceAccessor.getMessage(SortBy.DEVICE_NAME);
        headerRow[1] = messageSourceAccessor.getMessage(SortBy.METER_NUMBER);
        headerRow[2] = messageSourceAccessor.getMessage(SortBy.PAO_TYPE);
        headerRow[3] = messageSourceAccessor.getMessage(SortBy.LEAK_RATE);

        // data rows
        List<WaterMeterLeak> waterLeaks = leaksService.getLeaks(filter, userContext);
        Collections.sort(waterLeaks, getMeterNameComparator());

        List<String[]> dataRows = Lists.newArrayList();
        for (WaterMeterLeak waterLeak : waterLeaks) {
            String[] dataRow = new String[4];
            dataRow[0] = waterLeak.getMeter().getName();
            dataRow[1] = waterLeak.getMeter().getMeterNumber();
            dataRow[2] = waterLeak.getMeter().getPaoType().getDbString();
            dataRow[3] = String.valueOf(waterLeak.getLeakRate());
            dataRows.add(dataRow);
        }
        String dateStr = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "WaterLeakReport_" + dateStr + ".csv");

        return "";
    }

    @RequestMapping(value="interval-data-csv", method = RequestMethod.GET)
    public String intervalDataCsv(@ModelAttribute("filter") WaterLeakReportFilter filter,
                                           ModelMap model, 
                                           HttpServletRequest request,
                                           HttpServletResponse response,
                                           YukonUserContext userContext) 
    throws IOException, ServletRequestBindingException, DeviceCollectionCreationException {

        setupDeviceCollectionFromRequest(filter, request);
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);

        // header row
        String[] headerRow = new String[5];
        headerRow[0] = messageSourceAccessor.getMessage(SortBy.DEVICE_NAME);
        headerRow[1] = messageSourceAccessor.getMessage(SortBy.METER_NUMBER);
        headerRow[2] = messageSourceAccessor.getMessage(SortBy.PAO_TYPE);
        headerRow[3] = messageSourceAccessor.getMessage(SortBy.USAGE);
        headerRow[4] = messageSourceAccessor.getMessage(SortBy.DATE);

        // data rows
        List<WaterMeterLeak> waterLeaks = leaksService.getIntervalLeaks(filter, userContext);
        Collections.sort(waterLeaks, getDateComparator());

        List<String[]> dataRows = Lists.newArrayList();
        for (WaterMeterLeak waterLeak : waterLeaks) {
            String[] dataRow = new String[5];
            dataRow[0] = waterLeak.getMeter().getName();
            dataRow[1] = waterLeak.getMeter().getMeterNumber();
            dataRow[2] = waterLeak.getMeter().getPaoType().getDbString();
            dataRow[3] = String.valueOf(waterLeak.getPointValueHolder().getValue());

            String formattedDate = dateFormattingService.format(waterLeak.getPointValueHolder().getPointDataTimeStamp(),
                                                                DateFormatEnum.BOTH, userContext);
            dataRow[4] = formattedDate;
            dataRows.add(dataRow);
        }

        String dateStr = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "WaterLeakReportIntervalData_" + dateStr + ".csv");

        return "";
    }
    
    @RequestMapping(value="leaks", method = RequestMethod.GET)
    public String leaks(@ModelAttribute("filter") WaterLeakReportFilter filter,
            HttpServletRequest request,
            ModelMap model, 
            YukonUserContext userContext,
            @DefaultItemsPerPage(10) PagingParameters paging,
            @DefaultSort(dir=Direction.desc, sort="LEAK_RATE") SortingParameters sorting) throws ServletRequestBindingException, DeviceCollectionCreationException {
        
        DeviceCollection collection = deviceCollectionFactory.createDeviceCollection(request);
        filter.setDeviceCollection(collection);
        
        List<WaterMeterLeak> leaks = leaksService.getLeaks(filter, userContext);
        Comparator<WaterMeterLeak> comparator = sorters.get(SortBy.valueOf(sorting.getSort()));
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(leaks, comparator);
        SearchResults<WaterMeterLeak> filtered = SearchResults.pageBasedForWholeList(paging, leaks);
        model.addAttribute("leaks", filtered);
        
        setupMspVendorModelInfo(userContext, model);
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        addColumns(accessor, sorting.getDirection(), SortBy.valueOf(sorting.getSort()), model);
        
        return "waterLeakReport/leaks.jsp";
    }

    private void setupDeviceCollectionFromRequest(WaterLeakReportFilter filter, HttpServletRequest request)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        
        DeviceCollection collection;
        String type = request.getParameter("collectionType");
        if (type != null) {
            collection = deviceCollectionFactory.createDeviceCollection(request);
        } else {

            String groupName = deviceGroupService.getFullPath(SystemGroupEnum.ALL_RFW_METERS);
            // Setup default device group (this is probably the first time the user is hitting this page)
            DeviceGroup deviceGroup = deviceGroupService.findGroupName(groupName);
            if (deviceGroup == null) {
                // We're probably not going to find many water leaks if we get in here. Oh well!
                deviceGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.DEVICE_TYPES);
            }
            collection = collectionHelper.buildDeviceCollection(deviceGroup);
        }
        filter.setDeviceCollection(collection);
    }

    private void setupDeviceCollectionForIntervalData(WaterLeakReportFilter filter,
                                                      Integer[] paoIds,
                                                      YukonUserContext userContext) {
        
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);
        String message = messageSourceAccessor.getMessage(baseKey + ".intervalData.results.deviceCollectionDescription");

        List<PaoIdentifier> paos = paoDao.getPaoIdentifiersForPaoIds(Lists.newArrayList(paoIds));
        Set<YukonMeter> meters = Sets.newHashSet(meterDao.getMetersForYukonPaos(paos));
        DeviceCollection collection = collectionHelper.createDeviceGroupCollection(meters.iterator(), message);
        filter.setDeviceCollection(collection);
    }

    private void setupFilterResults(WaterLeakReportFilter filter,
                                    YukonUserContext userContext, 
                                    ModelMap model,
                                    List<WaterMeterLeak> leaks,
                                    PagingParameters paging,
                                    SortingParameters sorting) {

        SortBy sort = SortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        
        // sort leaks
        Comparator<WaterMeterLeak> compare = sorters.get(sort);
        if (sorting.getDirection() == Direction.desc) compare = Collections.reverseOrder(compare);
        Collections.sort(leaks, compare);

        Set<YukonMeter> meters = Sets.newHashSet();
        for (WaterMeterLeak leak : leaks) {
            meters.add(leak.getMeter());
        }
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String message = accessor.getMessage(baseKey + ".results.deviceCollectionDescription");
        DeviceCollection collection = collectionHelper.createDeviceGroupCollection(meters.iterator(), message);
        model.addAttribute("collectionFromReportResults", collection);

        SearchResults<WaterMeterLeak> filtered = SearchResults.pageBasedForWholeList(paging, leaks);

        model.addAttribute("leaks", filtered);
        model.addAttribute("filter", filter);
        
        addColumns(accessor, dir, sort, model);
        
        Hours hoursBetweenToInstantAndNow = Hours.hoursBetween(filter.getToInstant(), new Instant());
        if (hoursBetweenToInstantAndNow.isLessThan(water_node_reporting_interval)) {
            model.addAttribute("toInstant_now_breach", hoursBetweenToInstantAndNow.getHours());
        }
        Hours hoursBetweenFromAndToInstant = Hours.hoursBetween(filter.getFromInstant(), filter.getToInstant());
        if (hoursBetweenFromAndToInstant.isLessThan(water_node_reporting_interval)) {
            model.addAttribute("reporting_interval", water_node_reporting_interval.getHours());
            model.addAttribute("from_toInstant_breach", hoursBetweenFromAndToInstant.getHours());
        }

        if (filter.getDeviceCollection() != null) {
            model.addAllAttributes(filter.getDeviceCollection().getCollectionParameters());
        }
        setupMspVendorModelInfo(userContext, model);
    }
    
    private void addColumns(MessageSourceAccessor accessor, Direction dir, SortBy sort, ModelMap model) {
        
        String nameText = accessor.getMessage(SortBy.DEVICE_NAME);
        SortableColumn nameColumn = SortableColumn.of(dir, sort == SortBy.DEVICE_NAME, nameText, SortBy.DEVICE_NAME.name());
        model.addAttribute("nameColumn", nameColumn);
        
        String numberText = accessor.getMessage(SortBy.METER_NUMBER);
        SortableColumn numberColumn = SortableColumn.of(dir, sort == SortBy.METER_NUMBER, numberText, SortBy.METER_NUMBER.name());
        model.addAttribute("numberColumn", numberColumn);
        
        String typeText = accessor.getMessage(SortBy.PAO_TYPE);
        SortableColumn typeColumn = SortableColumn.of(dir, sort == SortBy.PAO_TYPE, typeText, SortBy.PAO_TYPE.name());
        model.addAttribute("typeColumn", typeColumn);
        
        String rateText = accessor.getMessage(SortBy.LEAK_RATE);
        SortableColumn rateColumn = SortableColumn.of(dir, sort == SortBy.LEAK_RATE, rateText, SortBy.LEAK_RATE.name());
        model.addAttribute("rateColumn", rateColumn);
    }

    private void setupMspVendorModelInfo(YukonUserContext userContext, ModelMap model) {
        int vendorId = globalSettingDao.getInteger(GlobalSettingType.MSP_PRIMARY_CB_VENDORID);
        boolean hasVendorId = vendorId <= 0 ? false : true;
        model.addAttribute("hasVendorId", hasVendorId);
    }
    
    private static Comparator<WaterMeterLeak> getMeterNameComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<WaterMeterLeak> nameOrdering = normalStringComparer
            .onResultOf(new Function<WaterMeterLeak, String>() {
                @Override
                public String apply(WaterMeterLeak from) {
                    return from.getMeter().getName();
                }
            });
        return nameOrdering;
    }

    private static Comparator<WaterMeterLeak> getMeterNumberComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<WaterMeterLeak> numberOrdering = normalStringComparer
            .onResultOf(new Function<WaterMeterLeak, String>() {
                @Override
                public String apply(WaterMeterLeak from) {
                    return from.getMeter().getMeterNumber();
                }
            });
        return numberOrdering;
    }

    private static Comparator<WaterMeterLeak> getDeviceTypeComparator() {
        Ordering<String> normalStringComparer = Ordering.natural().nullsLast();
        Ordering<WaterMeterLeak> typeOrdering = normalStringComparer
            .onResultOf(new Function<WaterMeterLeak, String>() {
                @Override
                public String apply(WaterMeterLeak from) {
                    return from.getMeter().getPaoType().getDbString();
                }
            });
        return typeOrdering;
    }

    private static Comparator<WaterMeterLeak> getLeakRateComparator() {
        Ordering<Double> normalComparer = Ordering.natural().nullsLast();
        Ordering<WaterMeterLeak> rateOrdering = normalComparer
            .onResultOf(new Function<WaterMeterLeak, Double>() {
                @Override
                public Double apply(WaterMeterLeak from) {
                    return from.getLeakRate();
                }
            });
        Ordering<WaterMeterLeak> result = rateOrdering.compound(getMeterNameComparator());
        return result;
    }

    private static Comparator<WaterMeterLeak> getUsageComparator() {
        Ordering<Double> normalComparer = Ordering.natural().nullsLast();
        Ordering<WaterMeterLeak> usageOrdering = normalComparer
            .onResultOf(new Function<WaterMeterLeak, Double>() {
                @Override
                public Double apply(WaterMeterLeak from) {
                    return from.getPointValueHolder().getValue();
                }
            });
        Ordering<WaterMeterLeak> result = usageOrdering.compound(getMeterNameComparator());
        return result;
    }

    private static Comparator<WaterMeterLeak> getDateComparator() {
        Ordering<Date> normalComparer = Ordering.natural().nullsLast();
        Ordering<WaterMeterLeak> dateOrdering = normalComparer
            .onResultOf(new Function<WaterMeterLeak, Date>() {
                @Override
                public Date apply(WaterMeterLeak from) {
                    return from.getPointValueHolder().getPointDataTimeStamp();
                }
            });
        Ordering<WaterMeterLeak> result = dateOrdering.compound(getMeterNameComparator());
        return result;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, final YukonUserContext userContext) {
        datePropertyEditorFactory.setupInstantPropertyEditor(binder, userContext, BlankMode.CURRENT);
    }

}