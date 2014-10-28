package com.cannontech.web.tools.dataExporter;

import java.beans.PropertyEditor;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFormatDao;
import com.cannontech.amr.archivedValueExporter.model.ArchivedValuesExportFormatType;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRangeType;
import com.cannontech.amr.archivedValueExporter.model.dataRange.LocalDateRange;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.service.DeviceCollectionService;
import com.cannontech.common.events.loggers.ToolsEventLogService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.scheduledFileExport.ArchivedDataExportFileGenerationParameters;
import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronTagStyleType;
import com.cannontech.web.amr.util.cronExpressionTag.handler.CustomCronTagStyleHandler;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.scheduledFileExport.ScheduledFileExportHelper;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledArchivedDataFileExportTask;
import com.cannontech.web.scheduledFileExport.validator.ScheduledFileExportValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.tools.dataExporter.model.ArchivedValuesExporter;
import com.cannontech.web.tools.dataExporter.validator.DataRangeValidator;
import com.google.common.collect.Sets;

@Controller
@CheckRoleProperty(YukonRoleProperty.ARCHIVED_DATA_EXPORT)
public class DataExporterScheduleController {
    
    @Autowired private ArchiveValuesExportFormatDao archiveValuesExportFormatDao;
    @Autowired private AttributeService attributeService;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private DataRangeValidator dataRangeValidator;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private DeviceCollectionService deviceCollectionService;
    @Autowired private EmailService emailService;
    @Autowired private JobManager jobManager;
    @Autowired private ScheduledFileExportHelper exportHelper;
    @Autowired private ScheduledFileExportService scheduledFileExportService;
    @Autowired private ToolsEventLogService toolsEventLogService;
    
    public static String baseKey = "yukon.web.modules.tools.bulk.archivedValueExporter.";
    private ScheduledFileExportValidator scheduledFileExportValidator = new ScheduledFileExportValidator(this.getClass());
    
    @RequestMapping("/data-exporter/scheduleReport")
    public String scheduleReport(ModelMap model, FlashScope flashScope, HttpServletRequest request, Integer jobId,
            @ModelAttribute ArchivedValuesExporter archivedValuesExporter, BindingResult bindingResult) 
            throws ServletRequestBindingException {
        
        DeviceCollection deviceCollection;
        ExportFormat format;
        Set<Attribute> attributes = null;
        DataRange dataRange;
        ScheduledFileExportData exportData;
        CronExpressionTagState cronTagState;
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        if(jobId != null) {
            //edit existing schedule
            model.addAttribute("jobId", jobId);
            ScheduledRepeatingJob job = jobManager.getRepeatingJob(jobId);
            ScheduledArchivedDataFileExportTask task = (ScheduledArchivedDataFileExportTask) jobManager.instantiateTask(job);
            
            deviceCollection = deviceCollectionService.loadCollection(task.getDeviceCollectionId());
            format = archiveValuesExportFormatDao.getByFormatId(task.getFormatId());
            attributes = task.getAttributes();
            dataRange = task.getDataRange();
            cronTagState = cronExpressionTagService.parse(job.getCronString(), job.getUserContext());
            exportData = new ScheduledFileExportData();
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
                exportData.setNotificationEmailAddresses(emailService.getUserEmail(userContext));
            }
        } else {
            //create new schedule
            deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
            archivedValuesExporter.setDeviceCollection(deviceCollection);
            
            bindingResult.pushNestedPath("scheduleDataRange");
            dataRangeValidator.validate(archivedValuesExporter.getScheduleDataRange(), bindingResult);
            bindingResult.popNestedPath();
            if (bindingResult.hasErrors()) {
                List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
                flashScope.setError(messages);
                
                return "redirect:view";
            }
            format = archiveValuesExportFormatDao.getByFormatId(archivedValuesExporter.getFormatId());
            if(archivedValuesExporter.getArchivedValuesExportFormatType() == ArchivedValuesExportFormatType.DYNAMIC_ATTRIBUTE) {
                attributes = archivedValuesExporter.getAttributes();
            }
            dataRange = archivedValuesExporter.getScheduleDataRange();
            exportData = new ScheduledFileExportData();
            cronTagState = new CronExpressionTagState();
            exportData.setNotificationEmailAddresses(emailService.getUserEmail(userContext));
        }

        model.addAttribute("exportFormat", format);
        model.addAttribute("attributes", attributes);
        model.addAttribute("dataRange", dataRange);
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("exportData", exportData);
        model.addAttribute("cronExpressionTagState", cronTagState);
        model.addAttribute("fileExtensionChoices", exportHelper.setupFileExtChoices(exportData));
        model.addAttribute("exportPathChoices", exportHelper.setupExportPathChoices(exportData));
        model.addAttribute("isSMTPConfigured",emailService.isSmtpConfigured());
       
        return "data-exporter/schedule.jsp";
    }
    

    @RequestMapping("/data-exporter/doSchedule")
    public String doSchedule(ModelMap model, @ModelAttribute("exportData") ScheduledFileExportData exportData, BindingResult bindingResult, HttpServletRequest request,
     int formatId, String[] attributes, Integer jobId, YukonUserContext userContext, FlashScope flashScope) 
            throws ServletRequestBindingException, IllegalArgumentException, ParseException {
        
        // Build parameters
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        DataRange dataRange = getDataRangeFromRequest(request);

        Set<Attribute> attributeSet = Sets.newHashSet();
        if (attributes != null) {
            for (String attribute : attributes) {
                attributeSet.add(attributeService.resolveAttributeName(attribute));
            }
        }
        ArchivedDataExportFileGenerationParameters parameters = new ArchivedDataExportFileGenerationParameters(deviceCollection, formatId, attributeSet, dataRange);
        exportData.setParameters(parameters);

        try {
            String scheduleCronString = cronExpressionTagService.build("scheduleCronString", request, userContext);
            exportData.setScheduleCronString(scheduleCronString);
        } catch (ServletRequestBindingException | IllegalArgumentException | ParseException e) {
            bindingResult.rejectValue("scheduleCronString", "yukon.common.invalidCron");
        }

        scheduledFileExportValidator.validate(exportData, bindingResult);

        if (bindingResult.hasErrors()) {

            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setError(messages);
            model.addAttribute("exportFormat", archiveValuesExportFormatDao.getByFormatId(formatId));
            model.addAttribute("attributes", attributeSet);
            model.addAttribute("dataRange", dataRange);
            model.addAttribute("deviceCollection", deviceCollection);

            if (bindingResult.hasFieldErrors("scheduleCronString")) {
                CronExpressionTagState state = new CronExpressionTagState();
                state.setCronTagStyleType(CronTagStyleType.CUSTOM);
                state.setCustomExpression(CustomCronTagStyleHandler.getCustomExpression("scheduleCronString", request));
                model.addAttribute("cronExpressionTagState", state);
                model.addAttribute("invalidCronString", true);
            } else {
                model.addAttribute("cronExpressionTagState", cronExpressionTagService.parse(exportData.getScheduleCronString(), userContext));
            }
            
            model.addAttribute("jobId", jobId);
            model.addAttribute("fileExtensionChoices", exportHelper.setupFileExtChoices(exportData));
            model.addAttribute("exportPathChoices", exportHelper.setupExportPathChoices(exportData));
            
            return "data-exporter/schedule.jsp";
        }

        String scheduledCronDescription = cronExpressionTagService.getDescription(exportData.getScheduleCronString(), userContext);

        if (jobId == null) {
            scheduledFileExportService.scheduleFileExport(exportData, userContext, request);
            toolsEventLogService.dataExportScheduleCreated(userContext.getYukonUser(), exportData.getScheduleName(),
                exportData.getExportFileName(), scheduledCronDescription);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.tools.bulk.archivedValueExporterScheduleSetup.scheduleSuccess",exportData.getScheduleName()));
        } else {
            scheduledFileExportService.updateFileExport(exportData, userContext, request, jobId);
            toolsEventLogService.dataExportScheduleUpdated(userContext.getYukonUser(), exportData.getScheduleName(),
                exportData.getExportFileName(), scheduledCronDescription);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.tools.bulk.archivedValueExporterScheduleSetup.updateSuccess", exportData.getScheduleName()));
        }
                    
        return "redirect:view";
    }

    private DataRange getDataRangeFromRequest(HttpServletRequest request) throws ServletRequestBindingException {
        DataRange dataRange = new DataRange();
        
        String dataRangeTypeString = ServletRequestUtils.getStringParameter(request, "dataRange.dataRangeType");
        dataRange.setDataRangeType(DataRangeType.valueOf(dataRangeTypeString));

        if (dataRange.getDataRangeType() == DataRangeType.DATE_RANGE) {
            LocalDateRange localDateRange = new LocalDateRange();
            LocalDate startDate = LocalDate.parse(ServletRequestUtils.getStringParameter(request, "dataRange.dateRange.startDate"));
            LocalDate endDate = LocalDate.parse(ServletRequestUtils.getStringParameter(request, "dataRange.dateRange.endDate"));
            localDateRange.setStartDate(startDate);
            localDateRange.setEndDate(endDate);
            dataRange.setLocalDateRange(localDateRange);
        } else if (dataRange.getDataRangeType() == DataRangeType.DAYS_PREVIOUS) {
            int daysPrevious = ServletRequestUtils.getIntParameter(request, "dataRange.daysPrevious");
            dataRange.setDaysPrevious(daysPrevious);
        } else if (dataRange.getDataRangeType() == DataRangeType.END_DATE) {
            // don't worry about this, task will set it when run
        } else if (dataRange.getDataRangeType() == DataRangeType.SINCE_LAST_CHANGE_ID) {
            // don't worry about this, task will set it when run
        }
        
        return dataRange;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {

        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver = new YukonMessageCodeResolver(baseKey);
            binder.setMessageCodesResolver(msgCodesResolver);
        }

        binder.registerCustomEditor(Attribute.class, new EnumPropertyEditor<>(BuiltInAttribute.class));
        binder.registerCustomEditor(DataRangeType.class, new EnumPropertyEditor<>(DataRangeType.class));

        PropertyEditor localDatePropertyEditor = datePropertyEditorFactory.getLocalDatePropertyEditor(DateFormatEnum.DATE, userContext);
        binder.registerCustomEditor(LocalDate.class, "runDataRange.endDate", localDatePropertyEditor);
        binder.registerCustomEditor(LocalDate.class, "scheduleDataRange.endDate", localDatePropertyEditor);

        PropertyEditor dayStartDateEditor = datePropertyEditorFactory.getLocalDatePropertyEditor(DateFormatEnum.DATE, userContext);
        PropertyEditor dayEndDateEditor = datePropertyEditorFactory.getLocalDatePropertyEditor(DateFormatEnum.DATE, userContext);

        binder.registerCustomEditor(LocalDate.class, "runDataRange.localDateRange.startDate", dayStartDateEditor);
        binder.registerCustomEditor(LocalDate.class, "runDataRange.localDateRange.endDate", dayEndDateEditor);

        binder.registerCustomEditor(LocalDate.class, "scheduleDataRange.localDateRange.startDate", dayStartDateEditor);
        binder.registerCustomEditor(LocalDate.class, "scheduleDataRange.localDateRange.endDate", dayEndDateEditor);
    }

}