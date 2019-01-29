package com.cannontech.web.amr.meterEventsReport;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.meter.service.impl.MeterEventLookupService;
import com.cannontech.amr.meter.service.impl.MeterEventStatusTypeGroupings;
import com.cannontech.amr.paoPointValue.model.MeterPointValue;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.DeviceCollectionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.scheduledFileExport.MeterEventsExportGenerationParameters;
import com.cannontech.common.scheduledFileExport.ScheduledExportType;
import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.core.service.PaoPointValueService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.db.point.stategroup.EventStatus;
import com.cannontech.database.db.point.stategroup.OutageStatus;
import com.cannontech.database.db.point.stategroup.TrueFalse;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.meterEventsReport.model.MeterEventsFilter;
import com.cannontech.web.amr.meterEventsReport.model.ScheduledFileExport;
import com.cannontech.web.amr.util.cronExpressionTag.CronException;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronTagStyleType;
import com.cannontech.web.amr.util.cronExpressionTag.handler.CustomCronTagStyleHandler;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.scheduledFileExport.ScheduledFileExportHelper;
import com.cannontech.web.scheduledFileExport.ScheduledFileExportJobData;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledMeterEventsFileExportTask;
import com.cannontech.web.scheduledFileExport.validator.ScheduledFileExportValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/meterEventsReport/*")
@CheckRoleProperty(YukonRoleProperty.METER_EVENTS)
public class MeterEventsReportController {
    
    @Autowired private ContactDao contactDao;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private DeviceCollectionService collectionService;
    @Autowired private DeviceGroupCollectionHelper collectionHelper;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private JobManager jobManager;
    @Autowired private MeterEventLookupService meterEventLookupService;
    @Autowired private ObjectFormattingService objectFormatingService;
    @Autowired private PaoPointValueService paoPointValueService;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private ScheduledFileExportService exportService;
    @Autowired private ScheduledFileExportHelper exportHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    
    private ScheduledFileExportValidator exportValidator = 
            new ScheduledFileExportValidator(MeterEventsReportController.class);
    
    private final static Set<Integer> NON_ABNORMAL_VALUE = Sets.filter(
        Sets.newHashSet(OutageStatus.GOOD.getRawState(), EventStatus.CLEARED.getRawState(),
            TrueFalse.FALSE.getRawState()), new Predicate<Integer>() {
            @Override
            public boolean apply(Integer rawValue) {
                if (rawValue == 0) {
                    return true;
                }
                return false;
            }
        });
    
    private final String baseKey = "yukon.web.modules.amr.meterEventsReport";

    static enum SortBy { NAME, METER_NUMBER, TYPE, DATE, EVENT, VALUE; }

    /**After clicking on a schedule from Meter Events Report page **/
    @GetMapping(value="home", params="jobId")
    public String homeWithJob(ModelMap model, Integer jobId, YukonUserContext userContext) {
        
        setupExistingJobHomeModelMap(model, jobId, userContext);
        scheduledJobsTable(model);
        
        return "meterEventsReport/home.jsp";
    }
    
    /**After selecting Show All in Meter Events widget from Meter Details page **/
    @GetMapping(value="home", params="collectionType")
    public String homeWithDeviceCollection(ModelMap model, DeviceCollection collection, YukonUserContext userContext) {
        
        setupNewHomeModelMap(model, collection, userContext);
        scheduledJobsTable(model);
        
        return "meterEventsReport/home.jsp";
    }

    /**After selecting devices from Meter Events Report Device Selection page (By Device, Group, Address Range, File Upload)**/
    @PostMapping("home")
    public String postWithDeviceCollection(ModelMap model, YukonUserContext userContext,
                                           HttpServletRequest request, FlashScope flashScope) {
        if (StringUtils.isNotBlank(request.getParameter("collectionType"))) {
            try {
                DeviceCollection collection = deviceCollectionFactory.createDeviceCollection(request);
                setupNewHomeModelMap(model, collection, userContext);
                scheduledJobsTable(model);
            } catch (Exception e) {
                flashScope.setError(new YukonMessageSourceResolvable(e.getMessage()));
                return "meterEventsReport/selectDevices.jsp";
            }
        }
        return "meterEventsReport/home.jsp";
    }

    /**When clicking on Meter Events Report from AMI Actions (no devices or schedules selected) **/
    @GetMapping("home")
    public String homeJustSchedules(ModelMap model) {
        scheduledJobsTable(model);
        return "meterEventsReport/home.jsp";
    }
    
    private void setupExistingJobHomeModelMap(ModelMap model, Integer jobId, YukonUserContext userContext) {
        
        Instant toInstant = new Instant();
        Instant fromInstant = toInstant.minus(Duration.standardDays(7));

        ScheduledRepeatingJob job = jobManager.getRepeatingJob(jobId);
        ScheduledMeterEventsFileExportTask task = (ScheduledMeterEventsFileExportTask) jobManager.instantiateTask(job);

        ScheduledFileExportData exportData = task.getPartialData();
        exportData.setDaysPrevious(task.getDaysPrevious());
        exportData.setScheduleCronString(job.getCronString());
        if (task.getNotificationEmailAddresses() != null) {
            exportData.setNotificationEmailAddresses(task.getNotificationEmailAddresses());
            exportData.setSendEmail(task.isSendEmail());
        } else {
            exportData.setNotificationEmailAddresses(contactDao.getUserEmail(userContext.getYukonUser()));
        }
        CronExpressionTagState cronExpressionTagState = 
                cronExpressionTagService.parse(job.getCronString(), job.getUserContext());
        //set backing bean parameters
        DeviceCollection collection = collectionService.loadCollection(task.getDeviceCollectionId());

        Set<Attribute> availableEventAttributes = 
                meterEventLookupService.getAvailableEventAttributes(collection.getDeviceList());

        Map<Attribute, Boolean> meterEventTypesMap
            = Maps.newHashMapWithExpectedSize(availableEventAttributes.size());
        for (Attribute attr : availableEventAttributes) {
            meterEventTypesMap.put(attr, false);
        }
        for(Attribute attribute : task.getAttributes()) {
            BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
            if(meterEventTypesMap.containsKey(builtInAttribute)) {
                meterEventTypesMap.put(builtInAttribute, true);
            }
        }

        MeterEventsFilter meterEventsFilter = 
                new MeterEventsFilter(fromInstant, toInstant, availableEventAttributes, false, false, false);
        meterEventsTable(model, meterEventsFilter, PagingParameters.of(10, 1), 
                SortingParameters.of("DATE", Direction.desc), collection, userContext);

        model.addAttribute("numSelectedEventTypes", availableEventAttributes.size());

        model.addAttribute("exportData", exportData);
        model.addAttribute("jsonModel", getJsonModel(userContext, meterEventTypesMap, exportData, false));
        model.addAttribute("cronExpressionTagState", cronExpressionTagState);
        model.addAttribute("deviceCollection", collection);
        model.addAttribute("fileExtensionChoices", exportHelper.setupFileExtChoices(exportData));
        model.addAttribute("exportPathChoices", exportHelper.setupExportPathChoices(exportData));
        model.addAttribute("fromInstant", fromInstant);
        model.addAttribute("toInstant", toInstant);
        model.addAttribute("jobId", jobId);
        boolean isSmtpConfigured = StringUtils.isBlank(globalSettingDao.getString(GlobalSettingType.SMTP_HOST));
        model.addAttribute("isSmtpConfigured", isSmtpConfigured);
       
    }
    
    private void setupNewHomeModelMap(ModelMap model, DeviceCollection collection, YukonUserContext userContext) {
        
        LocalDate now = new LocalDate(userContext.getJodaTimeZone());
        Instant toInstantMidnight = now.plusDays(1).toDateTimeAtStartOfDay().toInstant().minus(1);
        Instant fromInstantStartOfDay = now.toDateTimeAtStartOfDay().toInstant().minus(Duration.standardDays(7));

        Set<Attribute> availableEventAttributes = 
                meterEventLookupService.getAvailableEventAttributes(collection.getDeviceList());

        Map<Attribute, Boolean> meterEventTypesMap
            = Maps.newHashMapWithExpectedSize(availableEventAttributes.size());
        for (Attribute attr : availableEventAttributes) {
            meterEventTypesMap.put(attr, true);
        }
        
        MeterEventsFilter meterEventsFilter = new MeterEventsFilter(fromInstantStartOfDay, toInstantMidnight, 
                availableEventAttributes, false, false, false);
        meterEventsTable(model, meterEventsFilter, PagingParameters.of(10, 1), 
                SortingParameters.of("DATE", Direction.desc), collection, userContext);

        model.addAttribute("numSelectedEventTypes", availableEventAttributes.size());

        ScheduledFileExportData exportData = new ScheduledFileExportData();
        model.addAttribute("exportData", exportData);
        model.addAttribute("jsonModel", getJsonModel(userContext, meterEventTypesMap, exportData, true));
        model.addAttribute("cronExpressionTagState", new CronExpressionTagState());
        model.addAttribute("deviceCollection", collection);
        model.addAttribute("fileExtensionChoices", exportHelper.setupFileExtChoices(exportData));
        model.addAttribute("exportPathChoices", exportHelper.setupExportPathChoices(exportData));
        model.addAttribute("fromInstant", fromInstantStartOfDay);
        model.addAttribute("toInstant", toInstantMidnight);
    }

    private Map<String, Object> getJsonModel(YukonUserContext userContext, Map<Attribute, Boolean> meterEventTypesMap,
        ScheduledFileExportData exportData, boolean isNewSchedule) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = Maps.newHashMapWithExpectedSize(18);
        
        json.put("meterEventTypesMap", meterEventTypesMap);
        json.put("generalEvents", getAttributesData(MeterEventStatusTypeGroupings.getGeneral(), userContext));
        json.put("hardwareEvents", getAttributesData(MeterEventStatusTypeGroupings.getHardware(), userContext));
        json.put("tamperEvents", getAttributesData(MeterEventStatusTypeGroupings.getTamper(), userContext));
        json.put("outageEvents", getAttributesData(MeterEventStatusTypeGroupings.getOutage(), userContext));
        json.put("meteringEvents", getAttributesData(MeterEventStatusTypeGroupings.getMetering(), userContext));
        
        json.put("allTitle", accessor.getMessage(baseKey + ".report.filter.tree.all"));
        json.put("generalTitle", accessor.getMessage(baseKey + ".report.filter.tree.general"));
        json.put("hardwareTitle", accessor.getMessage(baseKey + ".report.filter.tree.hardware"));
        json.put("tamperTitle", accessor.getMessage(baseKey + ".report.filter.tree.tamper"));
        json.put("outageTitle", accessor.getMessage(baseKey + ".report.filter.tree.outage"));
        json.put("meteringTitle", accessor.getMessage(baseKey + ".report.filter.tree.metering"));
        json.put("schedulePopupTitle", 
                accessor.getMessage(baseKey + ".report.schedulePopup.title", exportData.getScheduleName()));
        json.put("newSchedulePopupTitle", accessor.getMessage(baseKey + ".report.schedulePopup.title", ""));
        json.put("openScheduleDialog", !isNewSchedule);
        
        return json;
    }

    @RequestMapping("meterEventsTable")
    public String meterEventsTable(ModelMap model, 
            MeterEventsFilter meterEventsFilter,
            @DefaultItemsPerPage(10) PagingParameters paging,
            @DefaultSort(sort="DATE", dir=Direction.desc) SortingParameters sorting,
            DeviceCollection collection,
            YukonUserContext userContext) {
        
        SortBy sort = SortBy.valueOf(sorting.getSort());
        
        List<MeterPointValue> events;
        if (!CollectionUtils.isEmpty(meterEventsFilter.getAttributes())) {
            events = paoPointValueService.getMeterPointValues(
                Sets.newHashSet(collection.getDeviceList()), meterEventsFilter.getAttributes(),
                Range.inclusive(meterEventsFilter.getFromInstant(), meterEventsFilter.getToInstant()),
                meterEventsFilter.isOnlyLatestEvent() ? 1 : null,  meterEventsFilter.isIncludeDisabledPaos(),
                meterEventsFilter.isOnlyAbnormalEvents() ? NON_ABNORMAL_VALUE : null, userContext);
        } else {
            events = Collections.emptyList();
        }

        if (sorting.getDirection() == Direction.desc) {
            Collections.sort(events, Collections.reverseOrder(getSorter(sort, userContext)));
        } else {
            Collections.sort(events, getSorter(sort, userContext));
        }

        DeviceCollection collectionFromReportResults = getCollectionFromReport(events, userContext);
        SearchResults<MeterPointValue> meterEvents = SearchResults.pageBasedForWholeList(paging, events);

        model.addAttribute("deviceCollection", collection);
        model.addAttribute("collectionFromReportResults", collectionFromReportResults);
        model.addAttribute("meterEvents", meterEvents);
        model.addAttribute("meterEventsFilter", meterEventsFilter);

        Map<String, Object> meterEventsTableModelData = Maps.newHashMapWithExpectedSize(2);
        meterEventsTableModelData.put("sort", sort);
        meterEventsTableModelData.put("descending", sorting.getDirection() == Direction.desc);
        model.addAttribute("meterEventsTableModelData", meterEventsTableModelData);
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        // Add sortable table headers
        String nameText = accessor.getMessage(baseKey + ".report.tableHeader.deviceName.linkText");
        SortableColumn nameColumn = SortableColumn.of(sorting.getDirection(), sort == SortBy.NAME, nameText, SortBy.NAME.name());
        model.addAttribute("nameColumn", nameColumn);
        
        String numberText = accessor.getMessage(baseKey + ".report.tableHeader.meterNumber.linkText");
        SortableColumn numberColumn = SortableColumn.of(sorting.getDirection(), sort == SortBy.METER_NUMBER, numberText, SortBy.METER_NUMBER.name());
        model.addAttribute("numberColumn", numberColumn);
        
        String typeText = accessor.getMessage(baseKey + ".report.tableHeader.deviceType.linkText");
        SortableColumn typeColumn = SortableColumn.of(sorting.getDirection(), sort == SortBy.TYPE, typeText, SortBy.TYPE.name());
        model.addAttribute("typeColumn", typeColumn);
        
        String dateText = accessor.getMessage(baseKey + ".report.tableHeader.date.linkText");
        SortableColumn dateColumn = SortableColumn.of(sorting.getDirection(), sort == SortBy.DATE, dateText, SortBy.DATE.name());
        model.addAttribute("dateColumn", dateColumn);
        
        String eventText = accessor.getMessage(baseKey + ".report.tableHeader.event.linkText");
        SortableColumn eventColumn = SortableColumn.of(sorting.getDirection(), sort == SortBy.EVENT, eventText, SortBy.EVENT.name());
        model.addAttribute("eventColumn", eventColumn);
        
        String valueText = accessor.getMessage(baseKey + ".report.tableHeader.value.linkText");
        SortableColumn valueColumn = SortableColumn.of(sorting.getDirection(), sort == SortBy.VALUE, valueText, SortBy.VALUE.name());
        model.addAttribute("valueColumn", valueColumn);
        
        return "meterEventsReport/meterEventsTable.jsp";
    }

    @RequestMapping("scheduledJobsTable")
    public String scheduledJobsTable(ModelMap model) {
        
        List<ScheduledFileExportJobData> jobs
            = exportService.getScheduledFileExportJobData(ScheduledExportType.METER_EVENT);

        model.addAttribute("jobType", FileExportType.METER_EVENTS);
        model.addAttribute("jobs", jobs);
        
        return "meterEventsReport/scheduledJobsTable.jsp";
    }

    @RequestMapping("/jobs/{jobId}/enable")
    public String enableJob(@PathVariable int jobId, ModelMap model, FlashScope flashScope, YukonUserContext userContext) {
        YukonJob job = jobManager.getJob(jobId);
        ScheduledMeterEventsFileExportTask task = (ScheduledMeterEventsFileExportTask) jobManager.instantiateTask(job);
        String jobName = task.getName();
        jobManager.enableJob(job);
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".report.enableJobSuccess", jobName));
        return "redirect:/amr/meterEventsReport/home";
    }

    @RequestMapping("/jobs/{jobId}/disable")
    public String disableJob(@PathVariable int jobId, ModelMap model, FlashScope flashScope,
            YukonUserContext userContext) {
        YukonJob job = jobManager.getJob(jobId);
        ScheduledMeterEventsFileExportTask task = (ScheduledMeterEventsFileExportTask) jobManager.instantiateTask(job);
        String jobName = task.getName();
        jobManager.disableJob(job);
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".report.disableJobSuccess", jobName));
        return "redirect:/amr/meterEventsReport/home";
    }

    @RequestMapping("scheduledMeterEventsDialog")
    public String scheduledMeterEventsDialog(YukonUserContext userContext, ModelMap model, DeviceCollection collection) {
        ScheduledFileExportData exportData = new ScheduledFileExportData();
        exportData.setNotificationEmailAddresses(contactDao.getUserEmail(userContext.getYukonUser()));
        model.addAttribute("exportData", exportData);
        model.addAttribute("cronExpressionTagState", new CronExpressionTagState());
        model.addAttribute("deviceCollection", collection);
        model.addAttribute("fileExtensionChoices", exportHelper.setupFileExtChoices(exportData));
        model.addAttribute("exportPathChoices", exportHelper.setupExportPathChoices(exportData));
        boolean isSmtpConfigured = StringUtils.isBlank(globalSettingDao.getString(GlobalSettingType.SMTP_HOST));
        model.addAttribute("isSmtpConfigured", isSmtpConfigured);
        return "meterEventsReport/scheduledMeterEventsDialog.jsp";
    }

    @RequestMapping("saveScheduledMeterEventJob")
    public String saveScheduledMeterEventJob(ModelMap model, 
            MeterEventsFilter meterEventsFilter,
            @ModelAttribute("exportData") ScheduledFileExportData exportData, 
            BindingResult result,
            FlashScope flashScope, 
            DeviceCollection collection, 
            YukonUserContext userContext, 
            Integer jobId,
            HttpServletRequest request) 
                    throws ServletRequestBindingException, IllegalArgumentException, ParseException {
        
        try {
            String scheduleCronString = cronExpressionTagService.build("scheduleCronString", request, userContext);
            exportData.setScheduleCronString(scheduleCronString);
        } catch (CronException cronException) {
            result.rejectValue("scheduleCronString", "yukon.common.invalidCron");
        }
        if (CollectionUtils.isEmpty(meterEventsFilter.getAttributes())) {
             // The UI attempts to make this never happen but just in case it does, lets reject this request
            result.reject(baseKey + ".noEventTypes");
        }
        exportData.setScheduleName(StringUtils.trim(exportData.getScheduleName()));
        exportValidator.validate(exportData, result);
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("exportData", exportData);
            model.addAttribute("scheduledFileExport", new ScheduledFileExport());
            model.addAttribute("deviceCollection", collection);
            model.addAttribute("fileExtensionChoices", exportHelper.setupFileExtChoices(exportData));
            model.addAttribute("exportPathChoices", exportHelper.setupExportPathChoices(exportData));
            model.addAttribute("jobId", jobId);
            model.addAttribute("scheduleModelData", Collections.singletonMap("success", false));

            if (result.hasFieldErrors("scheduleCronString")) {
                CronExpressionTagState state = new CronExpressionTagState();
                state.setCronTagStyleType(CronTagStyleType.CUSTOM);
                state.setCustomExpression(CustomCronTagStyleHandler.getCustomExpression("scheduleCronString", request));
                model.addAttribute("cronExpressionTagState", state);
                model.addAttribute("invalidCronString", true);
            } else {
                model.addAttribute("cronExpressionTagState",
                    cronExpressionTagService.parse(exportData.getScheduleCronString(), userContext));
            }
            return "meterEventsReport/scheduledMeterEventsDialog.jsp";
        }

        MeterEventsExportGenerationParameters parameters = 
            new MeterEventsExportGenerationParameters(exportData.getDaysPrevious(),
                meterEventsFilter.isOnlyLatestEvent(), meterEventsFilter.isOnlyAbnormalEvents(),
                meterEventsFilter.isIncludeDisabledPaos(), collection, meterEventsFilter.getAttributes());
        exportData.setParameters(parameters);
        
        if (jobId == null) {
            exportService.scheduleFileExport(exportData, userContext, request);
            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".jobs.scheduleSuccess",
                                                                   exportData.getScheduleName()));
        } else {
            exportService.updateFileExport(exportData, userContext, request, jobId);
            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".jobs.updateSuccess",
                                                                   exportData.getScheduleName()));
        }

        model.addAttribute("scheduleModelData", Collections.singletonMap("success", true));

        return "meterEventsReport/scheduledMeterEventsDialog.jsp";
    }

    @RequestMapping("selectDevices")
    public String selectDevices() {
        return "meterEventsReport/selectDevices.jsp";
    }

    
    @RequestMapping("delete")
    public @ResponseBody Map<String, Object> delete(int jobId, YukonUserContext userContext) {
        
        YukonJob job = jobManager.getJob(jobId);
        ScheduledMeterEventsFileExportTask task = (ScheduledMeterEventsFileExportTask) jobManager.instantiateTask(job);
        String jobName = task.getName();
        jobManager.deleteJob(job);
        
        int deviceCollectionId = task.getDeviceCollectionId();
        collectionService.deleteCollection(deviceCollectionId);
        
        String key = baseKey + ".jobs.deletedSuccess";
        YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable(key, jobName);
        String message = objectFormatingService.formatObjectAsString(resolvable, userContext);
        
        Map<String, Object> json = Maps.newHashMapWithExpectedSize(2);
        json.put("success", true);
        json.put("successMsg", message);
        
        return json;
    }

    @RequestMapping("csv")
    public void csv(HttpServletResponse response, 
            MeterEventsFilter meterEventsFilter,
            DeviceCollection collection,
            @DefaultSort(sort="DATE", dir=Direction.desc) SortingParameters sorting,
            YukonUserContext userContext) throws IOException, DeviceCollectionCreationException {

        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);
        
        SortBy sort = SortBy.DATE;
        boolean desc = false;
        if (sorting != null) {
            sort = SortBy.valueOf(sorting.getSort());
            desc = sorting.getDirection() == Direction.desc;
        }

        String[] headerRow = new String[5];
        headerRow[0] = messageSourceAccessor.getMessage(baseKey + ".report.tableHeader.deviceName.linkText");
        headerRow[1] = messageSourceAccessor.getMessage(baseKey + ".report.tableHeader.meterNumber.linkText");
        headerRow[2] = messageSourceAccessor.getMessage(baseKey + ".report.tableHeader.date.linkText");
        headerRow[3] = messageSourceAccessor.getMessage(baseKey + ".report.tableHeader.event.linkText");
        headerRow[4] = messageSourceAccessor.getMessage(baseKey + ".report.tableHeader.value.linkText");

        List<MeterPointValue> events = 
                paoPointValueService.getMeterPointValues(
                    Sets.newHashSet(collection.getDeviceList()), meterEventsFilter.getAttributes(),
                    Range.inclusive(meterEventsFilter.getFromInstant(), meterEventsFilter.getToInstant()),
                    meterEventsFilter.isOnlyLatestEvent() ? 1 : null,  meterEventsFilter.isIncludeDisabledPaos(),
                    meterEventsFilter.isOnlyAbnormalEvents() ? NON_ABNORMAL_VALUE : null, userContext);

            if (desc) {
                Collections.sort(events, Collections.reverseOrder(getSorter(sort, userContext)));
            } else {
                Collections.sort(events, getSorter(sort, userContext));
            }

        //data rows
        List<String[]> dataRows = Lists.newArrayList();

        for (MeterPointValue event : events) {
            String[] dataRow = new String[5];
            dataRow[0] = event.getDeviceName();
            dataRow[1] = event.getMeterNumber();

            DateTime timeStamp = new DateTime(event.getPointValueHolder().getPointDataTimeStamp(), userContext.getJodaTimeZone());
            String dateTimeString = timeStamp.toString(DateTimeFormat.mediumDateTime());
            dataRow[2] = dateTimeString;
            dataRow[3] = StringEscapeUtils.escapeCsv(event.getPointName());
            dataRow[4] = StringEscapeUtils.escapeCsv(pointFormattingService.getValueString(event.getPointValueHolder(), Format.VALUE, userContext));
            dataRows.add(dataRow);
        }
        
        String dateStr = dateFormattingService.format(new LocalDate(userContext.getJodaTimeZone()), DateFormatEnum.DATE, userContext);
        
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "MeterEvents_" + dateStr + ".csv");
    }
    
    private DeviceCollection getCollectionFromReport(List<MeterPointValue> events, YukonUserContext userContext) {
        
        Set<SimpleDevice> meters = Sets.newHashSet();
        for (MeterPointValue reportEvent : events) {
            meters.add(deviceDao.getYukonDevice(reportEvent.getPaoIdentifier().getPaoId()));
        }

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String message = accessor.getMessage(baseKey + ".report.results.deviceCollectionDescription");

        DeviceCollection collection = collectionHelper.createDeviceGroupCollection(meters.iterator(), message);
        
        return collection;
    }

    private Map<String, Object> getAttributesData(Set<BuiltInAttribute> originalSet, YukonUserContext userContext) {
        
        Map<String, BuiltInAttribute> attributeMap = Maps.newHashMapWithExpectedSize(originalSet.size());
        List<String> attributes = new ArrayList<>(originalSet.size());
        for (BuiltInAttribute attr: originalSet) {
            String formatedAttr = objectFormatingService.formatObjectAsString(attr.getMessage(), userContext);
            attributeMap.put(formatedAttr, attr);
            attributes.add(formatedAttr);
        }
        Collections.sort(attributes);

        Map<String, Object> json = Maps.newHashMapWithExpectedSize(2);
        json.put("attributes", attributes);
        json.put("attributeMap", attributeMap);
        
        return json;
    }
    
    private Comparator<MeterPointValue> getSorter(SortBy SortBy, YukonUserContext userContext) {
        switch (SortBy) {
            case DATE: 
                return MeterPointValue.getDateMeterNameComparator();
            case EVENT: 
                return MeterPointValue.getPointNameMeterNameComparator();
            case METER_NUMBER: 
                return MeterPointValue.getMeterNumberComparator();
            case TYPE: 
                return MeterPointValue.getDeviceTypeComparator();
            case VALUE: 
                return MeterPointValue.getFormattedValueComparator(pointFormattingService, userContext);
            default:
            case NAME: 
                return MeterPointValue.getMeterNameComparator();
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, final YukonUserContext userContext) {
        
        datePropertyEditorFactory.setupInstantPropertyEditor(binder, userContext, BlankMode.CURRENT);

        PropertyEditor dayStartDateEditor = datePropertyEditorFactory.getInstantPropertyEditor(DateFormatEnum.DATEHM,
                userContext, BlankMode.CURRENT, DateOnlyMode.START_OF_DAY);
        PropertyEditor dayEndDateEditor = datePropertyEditorFactory.getInstantPropertyEditor(DateFormatEnum.DATEHM, 
                userContext, BlankMode.CURRENT, DateOnlyMode.END_OF_DAY);

        binder.registerCustomEditor(Instant.class, "fromInstant", dayStartDateEditor);
        binder.registerCustomEditor(Instant.class, "toInstant", dayEndDateEditor);
    }

}