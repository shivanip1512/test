package com.cannontech.web.amr.waterLeakReport;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.waterMeterLeak.model.WaterMeterLeak;
import com.cannontech.amr.waterMeterLeak.service.WaterMeterLeakService;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.scheduledFileExport.ScheduledExportType;
import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.common.scheduledFileExport.WaterLeakExportGenerationParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.service.MultispeakCustomerInfoService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.waterLeakReport.model.WaterLeakReportFilterBackingBean;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.scheduledFileExport.ScheduledFileExportHelper;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportJobsTagService;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledFileExportTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledWaterLeakFileExportTask;
import com.cannontech.web.scheduledFileExport.validator.ScheduledFileExportValidator;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/waterLeakReport/*")
public class WaterLeakReportController {
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private MeterDao meterDao;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MultispeakDao multispeakDao;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private PaoDao paoDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private WaterMeterLeakService waterMeterLeakService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private MultispeakCustomerInfoService multispeakCustomerInfoService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private ScheduledFileExportService scheduledFileExportService;
    @Autowired private ScheduledFileExportJobsTagService scheduledFileExportJobsTagService;
    @Autowired private JobManager jobManager;
    @Autowired private ScheduledFileExportHelper exportHelper;
    
    private ScheduledFileExportValidator scheduledFileExportValidator;
    private final static String baseKey = "yukon.web.modules.amr.waterLeakReport.report";
    private final static Hours water_node_reporting_interval = Hours.hours(24);
    private Map<String, Comparator<WaterMeterLeak>> sorters;
    private Cache<Integer, MspMeterAccountInfo> mspMeterAccountInfoMap = CacheBuilder.newBuilder()
        .concurrencyLevel(1).expireAfterWrite(1, TimeUnit.HOURS).build();

    private class MspMeterAccountInfo {
        com.cannontech.multispeak.deploy.service.Customer mspCustomer;
        com.cannontech.multispeak.deploy.service.ServiceLocation mspServLoc;
        com.cannontech.multispeak.deploy.service.Meter mspMeter;
        List<String> phoneNumbers;
        List<String> emailAddresses;
    }

    @PostConstruct
    public void initialize() {
        Builder<String, Comparator<WaterMeterLeak>> builder = ImmutableMap.builder();
        builder.put("DEVICE_NAME", getMeterNameComparator());
        builder.put("METER_NUMBER", getMeterNumberComparator());
        builder.put("PAO_TYPE", getDeviceTypeComparator());
        builder.put("LEAK_RATE", getLeakRateComparator());
        builder.put("USAGE", getUsageComparator());
        builder.put("DATE", getDateComparator());
        sorters = builder.build();
    }

    private Validator filterValidator =
        new SimpleValidator<WaterLeakReportFilterBackingBean>(WaterLeakReportFilterBackingBean.class) {
            @Override
            public void doValidation(WaterLeakReportFilterBackingBean backingBean, Errors errors) {
                /* Dates & Hours */
                if (backingBean.getFromInstant() == null && !errors.hasFieldErrors("fromInstant")) {
                    errors.rejectValue("fromInstant", "yukon.web.error.required");
                } else if (backingBean.getToInstant() == null && !errors.hasFieldErrors("toInstant")) {
                    errors.rejectValue("toInstant", "yukon.web.error.required");
                } else if(backingBean.getFromInstant().isAfterNow()) {
                    // If the from date is in the future
                    errors.rejectValue("fromInstant", baseKey + ".validation.fromDateInFuture");
                } else if (backingBean.getFromInstant().isAfter(backingBean.getToInstant())) {
                    errors.rejectValue("fromInstant", baseKey + ".validation.fromDateAfterToDate");
                } else if(backingBean.getToInstant().isAfterNow()) {
                    // If the to date is in the future
                    errors.rejectValue("toInstant", baseKey + ".validation.toDateInFuture");
                }

                /* Threshold */
                if (backingBean.getThreshold() < 0) {
                    errors.rejectValue("threshold", baseKey + ".validation.thresholdNegative");
                }
            }
        };

    @RequestMapping(method = RequestMethod.GET)
    public String report(@ModelAttribute("backingBean") WaterLeakReportFilterBackingBean backingBean,
                         BindingResult bindingResult, HttpServletRequest request, ModelMap model,
                         FlashScope flashScope, YukonUserContext userContext, boolean initReport,
                         boolean resetReport, Integer jobId,
                         @RequestParam(defaultValue="false") Boolean hasScheduleError)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
    	
    	model.addAttribute("hasScheduleError", hasScheduleError);
    	CronExpressionTagState cronTagState = new CronExpressionTagState();
    	ScheduledFileExportData exportData = new ScheduledFileExportData();
    	
    	if (initReport) model.addAttribute("first_visit", true);
        if (initReport || resetReport) {
            backingBean = new WaterLeakReportFilterBackingBean(backingBean);
        }
        
        //editing existing scheduled export
        if(jobId != null) {
        	model.addAttribute("jobId", jobId);
        	ScheduledRepeatingJob job = jobManager.getRepeatingJob(jobId);
    		ScheduledWaterLeakFileExportTask task = (ScheduledWaterLeakFileExportTask) jobManager.instantiateTask(job);
    		model.addAttribute("task", task);
    		
    		//populate report data
    		backingBean.setDeviceCollection(deviceGroupCollectionHelper.buildDeviceCollection(task.getDeviceGroup()));
    		backingBean.setFromInstant(backingBean.getToInstant().minus(Duration.standardHours(task.getHoursPrevious())));
    		backingBean.setIncludeDisabledPaos(task.isIncludeDisabledPaos());
    		backingBean.setThreshold(task.getThreshold());

    		//populate scheduled export data
    		cronTagState = cronExpressionTagService.parse(job.getCronString(), job.getUserContext());
    		exportData = new ScheduledFileExportData();
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
    		exportData.setNotificationEmailAddresses(task.getNotificationEmailAddresses());
        } else {
        	setupDeviceCollectionFromRequest(backingBean, request);
            filterValidator.validate(backingBean, bindingResult);
            if (bindingResult.hasErrors()) {
                List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
                flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
                model.addAttribute("hasFilterError", true);
                return "waterLeakReport/report.jsp";
            }
        }
        
        model.addAttribute("cronExpressionTagState", cronTagState);
    	model.addAttribute("fileExportData", exportData);
        model.addAttribute("fileExtensionChoices", exportHelper.setupFileExtChoices(exportData));
        model.addAttribute("exportPathChoices", exportHelper.setupExportPathChoices(exportData));
        
        setupWaterLeakReportFromFilter(request, backingBean, userContext, model);
        return "waterLeakReport/report.jsp";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String schedule(@ModelAttribute("fileExportData") ScheduledFileExportData scheduledFileExportData,
    		BindingResult bindingResult, 
    		@RequestParam(defaultValue="false") Boolean includeDisabledPaos,
    		String collectionType, Integer jobId, YukonUserContext userContext, HttpServletRequest request, 
    		ModelMap model, FlashScope flashScope) throws ServletRequestBindingException, ParseException {
    	
    	DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        
        String scheduleCronString = cronExpressionTagService.build("scheduleCronString", request, userContext);
    	scheduledFileExportData.setScheduleCronString(scheduleCronString);

    	scheduledFileExportValidator = new ScheduledFileExportValidator(this.getClass());
    	scheduledFileExportValidator.validate(scheduledFileExportData, bindingResult);
    	
        if(bindingResult.hasErrors()) {
			//send it back
        	List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("hasScheduleError", true);
            if(jobId != null) model.addAttribute("jobId", jobId);
            model.addAttribute("includeDisabledPaos", includeDisabledPaos);
            model.addAttribute("fileExportData", scheduledFileExportData);
            model.addAttribute("cronExpressionTagState", cronExpressionTagService.parse(scheduleCronString, userContext));
            model.addAttribute("fileExtensionChoices", exportHelper.setupFileExtChoices(scheduledFileExportData));
            model.addAttribute("exportPathChoices", exportHelper.setupExportPathChoices(scheduledFileExportData));
            WaterLeakReportFilterBackingBean backingBean = new WaterLeakReportFilterBackingBean();
            backingBean.setIncludeDisabledPaos(includeDisabledPaos);
            backingBean.setDeviceCollection(deviceCollection);
            model.addAttribute("backingBean", backingBean);
            setupWaterLeakReportFromFilter(null, backingBean, userContext, model);
            return "waterLeakReport/report.jsp";
		}
    	
        WaterLeakExportGenerationParameters parameters = 
                new WaterLeakExportGenerationParameters(deviceCollection, scheduledFileExportData.getHoursPrevious(), 
                    scheduledFileExportData.getThreshold(), includeDisabledPaos);
        scheduledFileExportData.setParameters(parameters);
        
		if(jobId == null) {
    		scheduledFileExportService.scheduleFileExport(scheduledFileExportData, userContext, request);
    		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.amr.waterLeakReport.jobs.scheduleSuccess", scheduledFileExportData.getScheduleName()));
    	} else {
    		scheduledFileExportService.updateFileExport(scheduledFileExportData, userContext, request, jobId);
    		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.amr.waterLeakReport.jobs.updateSuccess", scheduledFileExportData.getScheduleName()));
    	}
		
    	return "redirect:jobs";
    }
    
    @RequestMapping
    public String jobs(ModelMap model,Integer itemsPerPage, @RequestParam(defaultValue="1") int page) {

        itemsPerPage = CtiUtilities.itemsPerPage(itemsPerPage);
		scheduledFileExportJobsTagService.populateModel(model, FileExportType.WATER_LEAK, ScheduledExportType.WATER_LEAK, page, itemsPerPage);
		return "waterLeakReport/jobs.jsp";
	}
    
    @RequestMapping
	public String delete(ModelMap model, int jobId, FlashScope flashScope) {
		YukonJob job = jobManager.getJob(jobId);
		ScheduledFileExportTask task = (ScheduledFileExportTask) jobManager.instantiateTask(job);
		String jobName = task.getName();
		jobManager.deleteJob(job);
		
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.amr.waterLeakReport.jobs.deletedSuccess", jobName));
		return "redirect:jobs";
	}
    
    @RequestMapping(method = RequestMethod.GET)
    public String intervalData(@ModelAttribute("backingBean") WaterLeakReportFilterBackingBean backingBean,
                               ModelMap model, YukonUserContext userContext,
                               Integer[] selectedPaoIds)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        setupDeviceCollectionForIntervalData(backingBean, selectedPaoIds, userContext);
        setupWaterLeakIntervalFromFilter(backingBean, userContext, model);
        return "waterLeakReport/intervalData.jsp";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String cisDetails(ModelMap model, YukonUserContext userContext, int paoId) {
        MspMeterAccountInfo mspMeterAccountInfo = mspMeterAccountInfoMap.getIfPresent(paoId);
        if (mspMeterAccountInfo == null) {
            YukonMeter meter = meterDao.getForId(paoId);
            MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());

            mspMeterAccountInfo = new MspMeterAccountInfo();
            mspMeterAccountInfo.mspCustomer = mspObjectDao.getMspCustomer(meter, mspVendor);
            mspMeterAccountInfo.mspServLoc = mspObjectDao.getMspServiceLocation(meter, mspVendor);
            mspMeterAccountInfo.mspMeter = mspObjectDao.getMspMeter(meter, mspVendor);
            mspMeterAccountInfo.phoneNumbers =
                multispeakCustomerInfoService.getPhoneNumbers(mspMeterAccountInfo.mspCustomer,
                                                              userContext);
            mspMeterAccountInfo.emailAddresses =
                multispeakCustomerInfoService.getEmailAddresses(mspMeterAccountInfo.mspCustomer,
                                                                userContext);
            mspMeterAccountInfoMap.put(paoId, mspMeterAccountInfo);
        }

        model.addAttribute("mspPhoneNumbers", mspMeterAccountInfo.phoneNumbers);
        model.addAttribute("mspEmailAddresses", mspMeterAccountInfo.emailAddresses);
        model.addAttribute("mspCustomer", mspMeterAccountInfo.mspCustomer);
        model.addAttribute("mspServLoc", mspMeterAccountInfo.mspServLoc);
        model.addAttribute("mspMeter", mspMeterAccountInfo.mspMeter);

        setupMspVendorModelInfo(userContext, model);

        return "waterLeakReport/accountInfoAjax.jsp";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String csvWaterLeak(@ModelAttribute WaterLeakReportFilterBackingBean backingBean,
                               ModelMap model, HttpServletRequest request,
                               HttpServletResponse response,
                               YukonUserContext userContext) throws IOException,
            ServletRequestBindingException, DeviceCollectionCreationException {

        setupDeviceCollectionFromRequest(backingBean, request);
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        // header row
        String[] headerRow = new String[4];
        headerRow[0] = messageSourceAccessor.getMessage(baseKey + ".tableHeader.deviceName.linkText");
        headerRow[1] = messageSourceAccessor.getMessage(baseKey + ".tableHeader.meterNumber.linkText");
        headerRow[2] = messageSourceAccessor.getMessage(baseKey + ".tableHeader.deviceType.linkText");
        headerRow[3] = messageSourceAccessor.getMessage(baseKey + ".tableHeader.leakRate.linkText");

        // data rows
        List<WaterMeterLeak> waterLeaks = waterMeterLeakService.getWaterMeterLeaks(Sets.newHashSet(backingBean.getDeviceCollection().getDeviceList()),
                                                                                   backingBean.getRange(),
                                                                                   backingBean.isIncludeDisabledPaos(),
                                                                                   backingBean.getThreshold(),
                                                                                   userContext);
        if (backingBean.getSort() != null) {
            if (backingBean.getDescending()) {
                Collections.sort(waterLeaks, Collections.reverseOrder(sorters.get(backingBean.getSort())));
            } else {
                Collections.sort(waterLeaks, sorters.get(backingBean.getSort()));
            }
        } else {
            Collections.sort(waterLeaks, getMeterNameComparator());
        }

        List<String[]> dataRows = Lists.newArrayList();
        for (WaterMeterLeak waterLeak : waterLeaks) {
            String[] dataRow = new String[4];
            dataRow[0] = waterLeak.getMeter().getName();
            dataRow[1] = waterLeak.getMeter().getMeterNumber();
            dataRow[2] = waterLeak.getMeter().getPaoType().getDbString();
            dataRow[3] = String.valueOf(waterLeak.getLeakRate());
            dataRows.add(dataRow);
        }
        String dateStr = dateFormattingService.format(new LocalDate(userContext.getJodaTimeZone()), DateFormatEnum.DATE, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "WaterLeakReport_" + dateStr + ".csv");

        return "";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String csvWaterLeakIntervalData(@ModelAttribute WaterLeakReportFilterBackingBean backingBean,
                                           ModelMap model, HttpServletRequest request,
                                           HttpServletResponse response,
                                           YukonUserContext userContext) throws IOException,
            ServletRequestBindingException, DeviceCollectionCreationException {

        setupDeviceCollectionFromRequest(backingBean, request);
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        // header row
        String[] headerRow = new String[5];
        headerRow[0] = messageSourceAccessor.getMessage(baseKey + ".intervalData.tableHeader.deviceName.linkText");
        headerRow[1] = messageSourceAccessor.getMessage(baseKey + ".intervalData.tableHeader.meterNumber.linkText");
        headerRow[2] = messageSourceAccessor.getMessage(baseKey + ".intervalData.tableHeader.deviceType.linkText");
        headerRow[3] = messageSourceAccessor.getMessage(baseKey + ".intervalData.tableHeader.usage.linkText");
        headerRow[4] = messageSourceAccessor.getMessage(baseKey + ".intervalData.tableHeader.date.linkText");

        // data rows
        List<WaterMeterLeak> waterLeaks = waterMeterLeakService
                .getWaterMeterLeakIntervalData(Sets.newHashSet(backingBean.getDeviceCollection().getDeviceList()),
                                               backingBean.getRange(),
                                               backingBean.isIncludeDisabledPaos(),
                                               backingBean.getThreshold(),
                                               userContext);
        if (backingBean.getSort() != null) {
            if (backingBean.getDescending()) {
                Collections.sort(waterLeaks, Collections.reverseOrder(sorters.get(backingBean.getSort())));
            } else {
                Collections.sort(waterLeaks, sorters.get(backingBean.getSort()));
            }
        } else {
            Collections.sort(waterLeaks, getDateComparator());
        }

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

        String dateStr = dateFormattingService.format(new LocalDate(userContext.getJodaTimeZone()), DateFormatEnum.DATE, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "WaterLeakReportIntervalData_" + dateStr + ".csv");

        return "";
    }

    private void setupDeviceCollectionFromRequest(WaterLeakReportFilterBackingBean backingBean,
                                                  HttpServletRequest request)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        DeviceCollection deviceCollection;
        String type = request.getParameter("collectionType");
        if (type != null) {
            deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        } else {

            String groupName = deviceGroupService.getFullPath(SystemGroupEnum.DEVICE_TYPES) + PaoType.RFWMETER.getPaoTypeName();
            // Setup default device group (this is probably the first time the user is hitting this page)
            DeviceGroup deviceGroup = deviceGroupService.findGroupName(groupName);
            if (deviceGroup == null) {
                // We're probably not going to find many water leaks if we get in here. Oh well!
                deviceGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.DEVICE_TYPES);
            }
            deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(deviceGroup);
        }
        backingBean.setDeviceCollection(deviceCollection);
    }

    private void setupDeviceCollectionForIntervalData(WaterLeakReportFilterBackingBean backingBean,
                                                      Integer[] selectedPaoIds,
                                                      YukonUserContext userContext)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String message = messageSourceAccessor
                .getMessage(baseKey + ".intervalData.results.deviceCollectionDescription");

        HashSet<PaoIdentifier> paoIdentifiers = Sets.newHashSet(paoDao.getPaoIdentifiersForPaoIds(Lists.newArrayList(selectedPaoIds)));
        Set<YukonMeter> meters = Sets.newHashSet(meterDao.getMetersForYukonPaos(paoIdentifiers));
        DeviceCollection deviceCollection = getDeviceCollectionFromYukonDevices(meters, message);
        backingBean.setDeviceCollection(deviceCollection);
    }

    private void setupWaterLeakReportFromFilter(HttpServletRequest request,
                                                WaterLeakReportFilterBackingBean backingBean,
                                                YukonUserContext userContext, ModelMap model)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        List<WaterMeterLeak> waterLeaks =
            waterMeterLeakService.getWaterMeterLeaks(Sets.newHashSet(backingBean.getDeviceCollection().getDeviceList()),
                                                     backingBean.getRange(),
                                                     backingBean.isIncludeDisabledPaos(),
                                                     backingBean.getThreshold(),
                                                     userContext);
        setupFilterResults(backingBean, userContext, model, waterLeaks);
    }

    private void setupWaterLeakIntervalFromFilter(WaterLeakReportFilterBackingBean backingBean,
                                                  YukonUserContext userContext, ModelMap model) {
        List<WaterMeterLeak> waterLeaks = 
                waterMeterLeakService.getWaterMeterLeakIntervalData(Sets.newHashSet(backingBean.getDeviceCollection().getDeviceList()),
                                                                    backingBean.getRange(),
                                                                    backingBean.isIncludeDisabledPaos(),
                                                                    backingBean.getThreshold(),
                                                                    userContext);
        setupFilterResults(backingBean, userContext, model, waterLeaks);
    }

    private void setupFilterResults(WaterLeakReportFilterBackingBean backingBean,
                                    YukonUserContext userContext, ModelMap model,
                                    List<WaterMeterLeak> waterLeaks) {
        if (backingBean.getSort() != null) {
            if (backingBean.getDescending()) {
                Collections.sort(waterLeaks, Collections.reverseOrder(sorters.get(backingBean.getSort())));
            } else {
                Collections.sort(waterLeaks, sorters.get(backingBean.getSort()));
            }
        } else {
            Collections.sort(waterLeaks, Collections.reverseOrder(getLeakRateComparator()));
        }

        DeviceCollection collectionFromReportResults = getDeviceCollectionFromReportResults(waterLeaks, userContext);
        model.addAttribute("collectionFromReportResults", collectionFromReportResults);

        SearchResults<WaterMeterLeak> filterResult = new SearchResults<WaterMeterLeak>();
        filterResult.setBounds(backingBean.getStartIndex(),
                               backingBean.getItemsPerPage(),
                               waterLeaks.size());

        waterLeaks = waterLeaks.subList(backingBean.getStartIndex(),
                                        backingBean.getStartIndex() +
                                                backingBean.getItemsPerPage() > waterLeaks.size() ?
                                                waterLeaks.size() : backingBean.getStartIndex() +
                                                                    backingBean.getItemsPerPage());

        filterResult.setResultList(waterLeaks);
        model.addAttribute("filterResult", filterResult);
        model.addAttribute("backingBean", backingBean);
        
        WaterLeakReportFilterBackingBean defaultsTest = new WaterLeakReportFilterBackingBean();
        String groupName = backingBean.getDeviceCollection().getCollectionParameters().get("group.name");
        String defaultGroupName = deviceGroupService.getFullPath(SystemGroupEnum.DEVICE_TYPES) + PaoType.RFWMETER.getPaoTypeName();
        boolean defaultFilterValues = (groupName != null && groupName.equals(defaultGroupName))
                && backingBean.getFromInstant().isEqual(defaultsTest.getFromInstant().getMillis())
                && backingBean.getToInstant().isEqual(defaultsTest.getToInstant().getMillis())
                && backingBean.getThreshold() == defaultsTest.getThreshold()
                && backingBean.isIncludeDisabledPaos() == defaultsTest.isIncludeDisabledPaos();
        model.addAttribute("usingDefaultFilter", defaultFilterValues);

        Hours hoursBetweenToInstantAndNow = Hours.hoursBetween(backingBean.getToInstant(), new Instant());
        if (hoursBetweenToInstantAndNow.isLessThan(water_node_reporting_interval)) {
            model.addAttribute("toInstant_now_breach", hoursBetweenToInstantAndNow.getHours());
        }
        Hours hoursBetweenFromAndToInstant = Hours.hoursBetween(backingBean.getFromInstant(), backingBean.getToInstant());
        if (hoursBetweenFromAndToInstant.isLessThan(water_node_reporting_interval)) {
            model.addAttribute("reporting_interval", water_node_reporting_interval.getHours());
            model.addAttribute("from_toInstant_breach", hoursBetweenFromAndToInstant.getHours());
        }

        if (backingBean.getDeviceCollection() != null) {
            model.addAllAttributes(backingBean.getDeviceCollection().getCollectionParameters());
        }
        setupMspVendorModelInfo(userContext, model);
    }

    private void setupMspVendorModelInfo(YukonUserContext userContext, ModelMap model) {
        int vendorId = globalSettingDao.getInteger(GlobalSettingType.MSP_PRIMARY_CB_VENDORID);
        boolean hasVendorId = vendorId <= 0 ? false : true;
        model.addAttribute("hasVendorId", hasVendorId);
    }
    
    private DeviceCollection getDeviceCollectionFromReportResults(List<WaterMeterLeak> reportRows,
                                                                  YukonUserContext userContext) {
        Set<YukonMeter> meters = Sets.newHashSet();
        for (WaterMeterLeak row : reportRows) {
            meters.add(row.getMeter());
        }

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String message = messageSourceAccessor
                .getMessage(baseKey + ".results.deviceCollectionDescription");
        return getDeviceCollectionFromYukonDevices(meters, message);
    }

    private DeviceCollection getDeviceCollectionFromYukonDevices(Set<YukonMeter> meters, String message) {
        DeviceCollection resultsDeviceCollection =
            deviceGroupCollectionHelper.createDeviceGroupCollection(meters.iterator(), message);
        return resultsDeviceCollection;
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