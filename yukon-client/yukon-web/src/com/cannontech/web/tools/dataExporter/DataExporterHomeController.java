package com.cannontech.web.tools.dataExporter;

import java.beans.PropertyEditor;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFormatDao;
import com.cannontech.amr.archivedValueExporter.model.ArchivedValuesExportFormatType;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.Preview;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRangeType;
import com.cannontech.amr.archivedValueExporter.service.ExportReportGeneratorService;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.service.DeviceCollectionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.scheduledFileExport.ScheduledExportType;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.scheduledFileExport.ScheduledFileExportJobData;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledArchivedDataFileExportTask;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.tools.dataExporter.model.ArchivedValuesExporter;
import com.cannontech.web.tools.dataExporter.validator.DataRangeValidator;
import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
@CheckRoleProperty(YukonRoleProperty.ARCHIVED_DATA_EXPORT)
public class DataExporterHomeController {
    
    @Autowired private ArchiveValuesExportFormatDao archiveValuesExportFormatDao;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private DataRangeValidator dataRangeValidator;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private DeviceCollectionService deviceCollectionService;
    @Autowired private ExportReportGeneratorService exportReportGeneratorService;
    @Autowired private JobManager jobManager;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private ScheduledFileExportService scheduledFileExportService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    public static String baseKey = "yukon.web.modules.tools.bulk.archivedValueExporter.";
    
    private static DataRangeType[] FIXED_RUN_DATA_RANGE_TYPES = {DataRangeType.END_DATE};
    private static DataRangeType[] FIXED_SCHEDULE_DATA_RANGE_TYPES = {DataRangeType.END_DATE};
    private static DataRangeType[] DYNAMIC_RUN_DATA_RANGE_TYPES = {DataRangeType.DATE_RANGE, DataRangeType.DAYS_PREVIOUS};
    private static DataRangeType[] DYNAMIC_SCHEDULE_DATA_RANGE_TYPES = {DataRangeType.DAYS_PREVIOUS, DataRangeType.SINCE_LAST_CHANGE_ID};
    
    @RequestMapping("/data-exporter/view")
    public String view(ModelMap model, 
            HttpServletRequest request, 
            YukonUserContext userContext, 
            @ModelAttribute ArchivedValuesExporter archivedValuesExporter) 
    throws ServletRequestBindingException, DeviceCollectionCreationException, JsonProcessingException {
        
        List<ExportFormat> allFormats = archiveValuesExportFormatDao.getAllFormats();
        ExportFormat format = getExportFormat(archivedValuesExporter.getFormatId(), allFormats);
        Preview preview = exportReportGeneratorService.generatePreview(format, userContext);

        archivedValuesExporter.setFormatId(format.getFormatId());
        archivedValuesExporter.setArchivedValuesExportFormatType(format.getFormatType());
        model.addAttribute("archivedValuesExporter", archivedValuesExporter);

        Map<AttributeGroup, List<BuiltInAttribute>> groupedAttributes = 
                objectFormattingService.sortDisplayableValues(BuiltInAttribute.getAllGroupedAttributes(), userContext);
        model.addAttribute("groupedAttributes", groupedAttributes);
        
        model.addAttribute("allFormats", allFormats);
        model.addAttribute("fixedAttribute", ArchivedValuesExportFormatType.FIXED_ATTRIBUTE);
        model.addAttribute("dynamicAttribute", ArchivedValuesExportFormatType.DYNAMIC_ATTRIBUTE);
        model.addAttribute("preview", preview);
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        /* json configuration object to pass to the js module */
        Map<String, Object> config = new HashMap<>();
        config.put("dataRangeTypes", DataRangeType.values());
        config.put("fixedRunDataRangeTypes", FIXED_RUN_DATA_RANGE_TYPES);
        config.put("fixedScheduleDataRangeTypes", FIXED_SCHEDULE_DATA_RANGE_TYPES);
        config.put("dynamicRunDataRangeTypes", DYNAMIC_RUN_DATA_RANGE_TYPES);
        config.put("dynamicScheduleDataRangeTypes", DYNAMIC_SCHEDULE_DATA_RANGE_TYPES);
        Map<String, String> text = new HashMap<>();
        text.put("ok", accessor.getMessage("yukon.common.okButton"));
        text.put("cancel", accessor.getMessage("yukon.common.cancel"));
        text.put("create", accessor.getMessage("yukon.web.components.button.create.label"));
        text.put("run", accessor.getMessage("yukon.web.modules.tools.bulk.archivedValueExporter.run"));
        text.put("schedule", accessor.getMessage("yukon.web.modules.tools.bulk.archivedValueExporter.schedule"));
        config.put("text", text);
        model.put("jsConfig", config);

        if (StringUtils.isNotBlank(request.getParameter("collectionType"))) {
            DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
            model.addAllAttributes(deviceCollection.getCollectionParameters());
            model.addAttribute("deviceCollection", deviceCollection);
            archivedValuesExporter.setDeviceCollection(deviceCollection);
        }

        scheduledJobsTable(model, new PagingParameters(10, 1));

        return "data-exporter/home.jsp";
    }
    
    @RequestMapping("/data-exporter/scheduledJobsTable")
    public String scheduledJobsTable(ModelMap model, PagingParameters paging) {
        SearchResults<ScheduledFileExportJobData> reportsResult
            = scheduledFileExportService.getScheduledFileExportJobData(ScheduledExportType.ARCHIVED_DATA_EXPORT,
                                                                       paging);

        model.addAttribute("jobType", FileExportType.ARCHIVED_DATA_EXPORT);
        model.addAttribute("scheduledJobsSearchResult", reportsResult);
        return "data-exporter/scheduledJobsTable.jsp";
    }
    
    @RequestMapping("/data-exporter/deleteJob")
    public String deleteJob(int jobId, FlashScope flashScope) {
        YukonJob job = jobManager.getJob(jobId);
        ScheduledArchivedDataFileExportTask task = (ScheduledArchivedDataFileExportTask) jobManager.instantiateTask(job);
        String jobName = task.getName();
        jobManager.deleteJob(job);
        int deviceCollectionId = task.getDeviceCollectionId();
        deviceCollectionService.deleteCollection(deviceCollectionId);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.tools.bulk.archivedValueExporter.deletedJobSuccess", jobName));
        return "redirect:view";
    }

    @RequestMapping("/data-exporter/selectDevices")
    public String selectDevices(ModelMap model, @ModelAttribute ArchivedValuesExporter archivedValuesExporter) {
        model.addAttribute("archivedValuesExporter", archivedValuesExporter);
        return "data-exporter/selectDevices.jsp";
    }
    
    @RequestMapping("/data-exporter/selected")
    public String selected(ModelMap model, HttpServletRequest request, YukonUserContext userContext,
                           @ModelAttribute ArchivedValuesExporter archivedValuesExporter)
                           throws DeviceCollectionCreationException, ServletException, JsonProcessingException {
        return view(model, request, userContext, archivedValuesExporter);
    }
    
    @RequestMapping("/data-exporter/generateReport")
    public String generateReport(FlashScope flashScope, ModelMap model, HttpServletResponse response, HttpServletRequest request,
                                 @ModelAttribute ArchivedValuesExporter archivedValuesExporter, BindingResult bindingResult,
                                 YukonUserContext userContext)
    throws IOException, ServletRequestBindingException, DeviceCollectionCreationException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        archivedValuesExporter.setDeviceCollection(deviceCollection); // TODO It would be awesome if we could just bind this, but we don't have a way just yet.

        bindingResult.pushNestedPath("runDataRange");
        dataRangeValidator.validate(archivedValuesExporter.getRunDataRange(), bindingResult);
        bindingResult.popNestedPath();
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setError(messages);
            
            return view(model, request, userContext, archivedValuesExporter);
        }

        List<SimpleDevice> deviceList = archivedValuesExporter.getDeviceCollection().getDeviceList();
        DataRange dataRange = archivedValuesExporter.getRunDataRange();
        ExportFormat format = archiveValuesExportFormatDao.getByFormatId(archivedValuesExporter.getFormatId());
        List<String> report = exportReportGeneratorService.generateReport(deviceList, format, dataRange, userContext,
            archivedValuesExporter.getAttributesArray());
        
        SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("MMddyyyy");
        String fileNameDateFormatString = fileNameDateFormat.format(Instant.now().toDate());
        String fileName = ServletUtil.makeWindowsSafeFileName(format.getFormatName() + fileNameDateFormatString) + ".csv";
        
        response.setDateHeader("Expires", 0); // prevents caching at the proxy server
        response.setContentType("text/x-comma-separated-values");
        response.addHeader("Content-Disposition", "attachment;filename=\"" + fileName.toString() + "\"");
        
        OutputStream outputStream = response.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        for (String row : report) {
            writer.write(row);
            writer.newLine();
        }
        writer.close();
        
        return  "";
    }
    
    private ExportFormat getExportFormat(int selectedFormatId, List<ExportFormat> allFormats) {
        if (selectedFormatId != 0) {
            return archiveValuesExportFormatDao.getByFormatId(selectedFormatId);
        } else {
            if (!allFormats.isEmpty()) {
                int formatId = allFormats.get(0).getFormatId();
                return archiveValuesExportFormatDao.getByFormatId(formatId);
            } else {
                return new ExportFormat();
            }
        }
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver = new YukonMessageCodeResolver(baseKey);
            binder.setMessageCodesResolver(msgCodesResolver);
        }

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