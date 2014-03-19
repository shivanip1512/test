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

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.meter.service.impl.MeterEventLookupService;
import com.cannontech.amr.meter.service.impl.MeterEventStatusTypeGroupings;
import com.cannontech.amr.paoPointValue.model.MeterPointValue;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.service.DeviceCollectionService;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.scheduledFileExport.MeterEventsExportGenerationParameters;
import com.cannontech.common.scheduledFileExport.ScheduledExportType;
import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.core.service.PaoPointValueService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.db.point.stategroup.EventStatus;
import com.cannontech.database.db.point.stategroup.OutageStatus;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.meterEventsReport.model.MeterEventsFilter;
import com.cannontech.web.amr.meterEventsReport.model.ScheduledFileExport;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.scheduledFileExport.ScheduledFileExportHelper;
import com.cannontech.web.scheduledFileExport.ScheduledFileExportJobData;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledMeterEventsFileExportTask;
import com.cannontech.web.scheduledFileExport.validator.ScheduledFileExportValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@CheckRoleProperty(YukonRoleProperty.METER_EVENTS)
@Controller
@RequestMapping("/meterEventsReport/*")
public class MeterEventsReportController {
    
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private PaoPointValueService paoPointValueService;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private MeterEventLookupService meterEventLookupService;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private AttributeService attributeService;
    @Autowired private ObjectFormattingService objectFormatingService;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private ScheduledFileExportService scheduledFileExportService;
    @Autowired private JobManager jobManager;
    @Autowired private ScheduledFileExportHelper exportHelper;
    @Autowired private DeviceCollectionService deviceCollectionService;

    private ScheduledFileExportValidator scheduledFileExportValidator = 
            new ScheduledFileExportValidator(this.getClass());
    private final static Set<String> NON_ABNORMAL_VALUES = Sets.newHashSet(OutageStatus.GOOD.name().toLowerCase(),
                                                                           EventStatus.CLEARED.name().toLowerCase());
    private final String baseKey = "yukon.web.modules.amr.meterEventsReport.report";

    static enum SortBy {NAME, METER_NUMBER, TYPE, DATE, EVENT, VALUE;}

    private Comparator<MeterPointValue> getSorter(SortBy SortBy, YukonUserContext context) {
        switch(SortBy) {
            case DATE: 
                return MeterPointValue.getDateMeterNameComparator();
            case EVENT: 
                return MeterPointValue.getPointNameMeterNameComparator();
            case METER_NUMBER: 
                return MeterPointValue.getMeterNumberComparator();
            case TYPE: 
                return MeterPointValue.getDeviceTypeComparator();
            case VALUE: 
                return MeterPointValue.getFormattedValueComparator(pointFormattingService, context);
            default:
            case NAME: 
                return MeterPointValue.getMeterNameComparator();
        }
    }

    @RequestMapping(value="home", params="jobId")
    public String homeWithJob(ModelMap model, Integer jobId, YukonUserContext userContext) {
        setupExistingJobHomeModelMap(model, jobId, userContext);
        scheduledJobsTable(model, new PagingParameters(10, 1));
        return "meterEventsReport/home.jsp";
    }

    @RequestMapping(value="home", params="collectionType")
    public String homeWithDeviceCollection(ModelMap model, DeviceCollection deviceCollection,
                       YukonUserContext userContext) {
        setupNewHomeModelMap(model, deviceCollection, userContext);
        scheduledJobsTable(model, new PagingParameters(10, 1));
        return "meterEventsReport/home.jsp";
    }

    @RequestMapping(value="home")
    public String homeJustSchedules(ModelMap model) {
        scheduledJobsTable(model, new PagingParameters(10, 1));
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
        CronExpressionTagState cronExpressionTagState = 
                cronExpressionTagService.parse(job.getCronString(), job.getUserContext());
        //set backing bean parameters
        DeviceCollection deviceCollection = deviceCollectionService.loadCollection(task.getDeviceCollectionId());

        Set<Attribute> availableEventAttributes = 
                meterEventLookupService.getAvailableEventAttributes(deviceCollection.getDeviceList());

        Map<Attribute, Boolean> meterEventTypesMap
            = Maps.newHashMapWithExpectedSize(availableEventAttributes.size());
        for (Attribute attr : availableEventAttributes) {
            meterEventTypesMap.put(attr, true);
        }
        for(Attribute attribute : task.getAttributes()) {
            BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
            if(meterEventTypesMap.containsKey(builtInAttribute)) {
                meterEventTypesMap.put(builtInAttribute, true);
            }
        }

        MeterEventsFilter meterEventsFilter = 
                new MeterEventsFilter(fromInstant, toInstant, availableEventAttributes, false, false, false);
        meterEventsTable(model, meterEventsFilter, new PagingParameters(10, 1), deviceCollection,
                         SortBy.NAME, false, userContext);

        model.addAttribute("numSelectedEventTypes", availableEventAttributes.size());

        model.addAttribute("exportData", exportData);
        model.addAttribute("jsonModel", getJsonModel(userContext, meterEventTypesMap, exportData, false));
        model.addAttribute("cronExpressionTagState", cronExpressionTagState);
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("fileExtensionChoices", exportHelper.setupFileExtChoices(exportData));
        model.addAttribute("exportPathChoices", exportHelper.setupExportPathChoices(exportData));
        
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("fromInstant", fromInstant);
        model.addAttribute("toInstant", toInstant);
        model.addAttribute("jobId", jobId);
    }
    
    private void setupNewHomeModelMap(ModelMap model, DeviceCollection deviceCollection, YukonUserContext userContext) {
        LocalDate now = new LocalDate(userContext.getJodaTimeZone());
        Instant toInstantMidnight = now.plusDays(1).toDateTimeAtStartOfDay().toInstant().minus(1);
        Instant fromInstantStartOfDay = now.toDateTimeAtStartOfDay().toInstant().minus(Duration.standardDays(7));

        Set<Attribute> availableEventAttributes = 
                meterEventLookupService.getAvailableEventAttributes(deviceCollection.getDeviceList());

        Map<Attribute, Boolean> meterEventTypesMap
            = Maps.newHashMapWithExpectedSize(availableEventAttributes.size());
        for (Attribute attr : availableEventAttributes) {
            meterEventTypesMap.put(attr, true);
        }
        
        MeterEventsFilter meterEventsFilter = 
                new MeterEventsFilter(fromInstantStartOfDay, toInstantMidnight, availableEventAttributes, false, false, false);
        meterEventsTable(model, meterEventsFilter, new PagingParameters(10, 1), deviceCollection,
                         SortBy.DATE, false, userContext);

        model.addAttribute("numSelectedEventTypes", availableEventAttributes.size());

        ScheduledFileExportData exportData = new ScheduledFileExportData();
        model.addAttribute("exportData", exportData);
        model.addAttribute("jsonModel", getJsonModel(userContext, meterEventTypesMap, exportData, true));
        model.addAttribute("cronExpressionTagState", new CronExpressionTagState());
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("fileExtensionChoices", exportHelper.setupFileExtChoices(exportData));
        model.addAttribute("exportPathChoices", exportHelper.setupExportPathChoices(exportData));
        
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("fromInstant", fromInstantStartOfDay);
        model.addAttribute("toInstant", toInstantMidnight);
    }

    private Map<String, Object> getJsonModel(YukonUserContext userContext, Map<Attribute, Boolean> meterEventTypesMap,
        ScheduledFileExportData exportData, boolean isNewSchedule) {
        MessageSourceAccessor messageAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> jsonModel = Maps.newHashMapWithExpectedSize(18);
        jsonModel.put("meterEventTypesMap", meterEventTypesMap);
        jsonModel.put("generalEvents", getAttributesData(MeterEventStatusTypeGroupings.getGeneral(), userContext));
        jsonModel.put("hardwareEvents", getAttributesData(MeterEventStatusTypeGroupings.getHardware(), userContext));
        jsonModel.put("tamperEvents", getAttributesData(MeterEventStatusTypeGroupings.getTamper(), userContext));
        jsonModel.put("outageEvents", getAttributesData(MeterEventStatusTypeGroupings.getOutage(), userContext));
        jsonModel.put("meteringEvents", getAttributesData(MeterEventStatusTypeGroupings.getMetering(), userContext));
        
        jsonModel.put("allTitle", messageAccessor.getMessage(baseKey + ".filter.tree.all"));
        jsonModel.put("generalTitle", messageAccessor.getMessage(baseKey + ".filter.tree.general"));
        jsonModel.put("hardwareTitle", messageAccessor.getMessage(baseKey + ".filter.tree.hardware"));
        jsonModel.put("tamperTitle", messageAccessor.getMessage(baseKey + ".filter.tree.tamper"));
        jsonModel.put("outageTitle", messageAccessor.getMessage(baseKey + ".filter.tree.outage"));
        jsonModel.put("meteringTitle", messageAccessor.getMessage(baseKey + ".filter.tree.metering"));
        jsonModel.put("schedulePopupTitle", 
                messageAccessor.getMessage(baseKey + ".schedulePopup.title", exportData.getScheduleName()));
        jsonModel.put("newSchedulePopupTitle", messageAccessor.getMessage(baseKey + ".schedulePopup.title", ""));
        jsonModel.put("confirmScheduleDeletion", 
                messageAccessor.getMessage("yukon.web.modules.tools.scheduledFileExport.jobs.deleteSchedule.title"));
        jsonModel.put("okBtnLbl", messageAccessor.getMessage("yukon.web.components.button.ok.label"));
        jsonModel.put("cancelBtnLbl", messageAccessor.getMessage("yukon.web.components.button.cancel.label"));
        jsonModel.put("openScheduleDialog", !isNewSchedule);
        return jsonModel;
    }

    @RequestMapping("meterEventsTable")
    public String meterEventsTable(ModelMap model, MeterEventsFilter meterEventsFilter,
            @DefaultItemsPerPage(10) PagingParameters paging, DeviceCollection deviceCollection,
            @RequestParam(defaultValue="DATE") SortBy sort, boolean descending, YukonUserContext userContext) {
        List<MeterPointValue> events = 
            paoPointValueService.getMeterPointValues(
                Sets.newHashSet(deviceCollection.getDeviceList()), meterEventsFilter.getAttributes(),
                Range.inclusive(meterEventsFilter.getFromInstant(), meterEventsFilter.getToInstant()),
                meterEventsFilter.isOnlyLatestEvent() ? 1 : null,  meterEventsFilter.isIncludeDisabledPaos(),
                meterEventsFilter.isOnlyAbnormalEvents() ? NON_ABNORMAL_VALUES : null, userContext);

        if (descending) {
            Collections.sort(events, Collections.reverseOrder(getSorter(sort, userContext)));
        } else {
            Collections.sort(events, getSorter(sort, userContext));
        }

        DeviceCollection collectionFromReportResults = getDeviceCollectionFromReportResults(events, userContext);
        SearchResults<MeterPointValue> meterEvents = SearchResults.pageBasedForWholeList(paging, events);

        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("collectionFromReportResults", collectionFromReportResults);
        model.addAttribute("meterEvents", meterEvents);
        model.addAttribute("meterEventsFilter", meterEventsFilter);

        model.addAttribute("sort", sort);
        model.addAttribute("descending", descending);

        Map<String, Object> meterEventsTableModelData = Maps.newHashMapWithExpectedSize(2);
        meterEventsTableModelData.put("sort", sort);
        meterEventsTableModelData.put("descending", descending);
        model.addAttribute("meterEventsTableModelData", meterEventsTableModelData);
        
        return "meterEventsReport/meterEventsTable.jsp";
    }

    @RequestMapping("scheduledJobsTable")
    public String scheduledJobsTable(ModelMap model, @DefaultItemsPerPage(10) PagingParameters paging) {
        SearchResults<ScheduledFileExportJobData> reportsResult
            = scheduledFileExportService.getScheduledFileExportJobData(ScheduledExportType.METER_EVENT, paging);

        model.addAttribute("jobType", FileExportType.METER_EVENTS);
        model.addAttribute("scheduledJobsSearchResult", reportsResult);
        return "meterEventsReport/scheduledJobsTable.jsp";
    }

    @RequestMapping("scheduledMeterEventsDialog")
    public String scheduledMeterEventsDialog(ModelMap model, DeviceCollection deviceCollection) {
        ScheduledFileExportData exportData = new ScheduledFileExportData();
        model.addAttribute("exportData", exportData);
        model.addAttribute("cronExpressionTagState", new CronExpressionTagState());
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("fileExtensionChoices", exportHelper.setupFileExtChoices(exportData));
        model.addAttribute("exportPathChoices", exportHelper.setupExportPathChoices(exportData));
        return "meterEventsReport/scheduledMeterEventsDialog.jsp";
    }

    @RequestMapping("saveScheduledMeterEventJob")
    public String saveScheduledMeterEventJob(ModelMap model, MeterEventsFilter meterEventsFilter,
           @ModelAttribute("exportData") ScheduledFileExportData exportData, BindingResult bindingResult,
           FlashScope flashScope, DeviceCollection deviceCollection, YukonUserContext userContext, Integer jobId,
           HttpServletRequest request) throws ServletRequestBindingException, IllegalArgumentException, ParseException {
        scheduledFileExportValidator.validate(exportData, bindingResult);
        if(bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages =
                    YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            CronExpressionTagState cronExpressionTagState = new CronExpressionTagState();
            model.addAttribute("exportData", exportData);
            model.addAttribute("cronExpressionTagState", cronExpressionTagState);
            model.addAttribute("scheduledFileExport", new ScheduledFileExport());
            model.addAttribute("deviceCollection", deviceCollection);
            model.addAttribute("fileExtensionChoices", exportHelper.setupFileExtChoices(exportData));
            model.addAttribute("exportPathChoices", exportHelper.setupExportPathChoices(exportData));
            model.addAttribute("jobId", jobId);
            model.addAttribute("scheduleModelData", Collections.singletonMap("success", false));
            return "meterEventsReport/scheduledMeterEventsDialog.jsp";
        }

        MeterEventsExportGenerationParameters parameters = 
            new MeterEventsExportGenerationParameters(exportData.getDaysPrevious(),
                meterEventsFilter.isOnlyLatestEvent(), meterEventsFilter.isOnlyAbnormalEvents(),
                meterEventsFilter.isIncludeDisabledPaos(), deviceCollection, meterEventsFilter.getAttributes());
        exportData.setParameters(parameters);

        String scheduleCronString = cronExpressionTagService.build("scheduleCronString", request, userContext);
        exportData.setScheduleCronString(scheduleCronString);

        if(jobId == null) {
            scheduledFileExportService.scheduleFileExport(exportData, userContext, request);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.amr.meterEventsReport.jobs.scheduleSuccess",
                                                                   exportData.getScheduleName()));
        } else {
            scheduledFileExportService.updateFileExport(exportData, userContext, request, jobId);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.amr.meterEventsReport.jobs.updateSuccess",
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
    @ResponseBody
    public Map<String, Object> delete(int jobId, YukonUserContext userContext) {
        YukonJob job = jobManager.getJob(jobId);
        ScheduledMeterEventsFileExportTask task = (ScheduledMeterEventsFileExportTask) jobManager.instantiateTask(job);
        String jobName = task.getName();
        jobManager.deleteJob(job);
        int deviceCollectionId = task.getDeviceCollectionId();
        deviceCollectionService.deleteCollection(deviceCollectionId);
        String key = "yukon.web.modules.amr.meterEventsReport.jobs.deletedSuccess";
        String successMessage = 
                objectFormatingService.formatObjectAsString(new YukonMessageSourceResolvable(key, jobName), userContext);
        Map<String, Object> returnJson = Maps.newHashMapWithExpectedSize(2);
        returnJson.put("success", true);
        returnJson.put("successMsg", successMessage);
        return returnJson;
    }

    @RequestMapping("csv")
    public void csv(HttpServletResponse response, MeterEventsFilter meterEventsFilter,
                    DeviceCollection deviceCollection, @RequestParam(defaultValue="DATE") SortBy sort,
                    boolean descending, YukonUserContext userContext) 
                            throws IOException, DeviceCollectionCreationException {

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        String[] headerRow = new String[5];
        headerRow[0] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.deviceName.linkText");
        headerRow[1] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.meterNumber.linkText");
        headerRow[2] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.date.linkText");
        headerRow[3] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.event.linkText");
        headerRow[4] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.value.linkText");

        List<MeterPointValue> events = 
                paoPointValueService.getMeterPointValues(
                    Sets.newHashSet(deviceCollection.getDeviceList()), meterEventsFilter.getAttributes(),
                    Range.inclusive(meterEventsFilter.getFromInstant(), meterEventsFilter.getToInstant()),
                    meterEventsFilter.isOnlyLatestEvent() ? 1 : null,  meterEventsFilter.isIncludeDisabledPaos(),
                    meterEventsFilter.isOnlyAbnormalEvents() ? NON_ABNORMAL_VALUES : null, userContext);

            if (descending) {
                Collections.sort(events, Collections.reverseOrder(getSorter(sort, userContext)));
            } else {
                Collections.sort(events, getSorter(sort, userContext));
            }

        //data rows
        List<String[]> dataRows = Lists.newArrayList();

        for(MeterPointValue event : events) {
            String[] dataRow = new String[5];
            dataRow[0] = event.getMeter().getName();
            dataRow[1] = event.getMeter().getMeterNumber();

            DateTime timeStamp = new DateTime(event.getPointValueHolder().getPointDataTimeStamp(), userContext.getJodaTimeZone());
            String dateTimeString = timeStamp.toString(DateTimeFormat.mediumDateTime());
            dataRow[2] = dateTimeString;
            dataRow[3] = event.getPointName();
            dataRow[4] = pointFormattingService.getValueString(event.getPointValueHolder(), Format.VALUE, userContext);
            dataRows.add(dataRow);
        }
        
        String dateStr = dateFormattingService.format(new LocalDate(userContext.getJodaTimeZone()), DateFormatEnum.DATE, userContext);
        
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "MeterEvents_" + dateStr + ".csv");
    }
    
    private DeviceCollection getDeviceCollectionFromReportResults(List<MeterPointValue> events, YukonUserContext userContext) {
        Set<YukonMeter> meters = Sets.newHashSet();
        for (MeterPointValue reportEvent : events) {
            meters.add(reportEvent.getMeter());
        }

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String message = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.results.deviceCollectionDescription");

        DeviceCollection resultsDeviceCollection = deviceGroupCollectionHelper.createDeviceGroupCollection(meters.iterator(), message);
        return resultsDeviceCollection;
    }

    private Map<String, Object> getAttributesData(Set<BuiltInAttribute> originalSet, YukonUserContext context) {
        Map<String, BuiltInAttribute> attributeMap = Maps.newHashMapWithExpectedSize(originalSet.size());
        List<String> strList = new ArrayList<>(originalSet.size());
        for (BuiltInAttribute attr: originalSet) {
            String formatedAttr = objectFormatingService.formatObjectAsString(attr.getMessage(), context);
            attributeMap.put(formatedAttr, attr);
            strList.add(formatedAttr);
        }
        Collections.sort(strList);

        Map<String, Object> jsonObj = Maps.newHashMapWithExpectedSize(2);
        jsonObj.put("attributes", strList);
        jsonObj.put("attributeMap", attributeMap);
        return jsonObj;
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