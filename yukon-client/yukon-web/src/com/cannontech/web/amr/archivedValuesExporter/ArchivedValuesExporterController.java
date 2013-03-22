package com.cannontech.web.amr.archivedValuesExporter;

import java.beans.PropertyEditor;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;
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
import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.amr.archivedValueExporter.model.DataSelection;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.model.MissingAttribute;
import com.cannontech.amr.archivedValueExporter.model.PadSide;
import com.cannontech.amr.archivedValueExporter.model.YukonRoundingMode;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRangeType;
import com.cannontech.amr.archivedValueExporter.model.dataRange.LocalDateRange;
import com.cannontech.amr.archivedValueExporter.service.ExportReportGeneratorService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.scheduledFileExport.ArchivedDataExportFileGenerationParameters;
import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.archivedValuesExporter.model.ArchivedValuesExporter;
import com.cannontech.web.amr.archivedValuesExporter.model.ArchivedValuesExporterBackingBean;
import com.cannontech.web.amr.archivedValuesExporter.validator.DataRangeValidator;
import com.cannontech.web.amr.archivedValuesExporter.validator.ExportAttributeValidator;
import com.cannontech.web.amr.archivedValuesExporter.validator.ExportFieldValidator;
import com.cannontech.web.amr.archivedValuesExporter.validator.ExportFormatValidator;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.scheduledFileExport.ScheduledFileExportJobData;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledArchivedDataFileExportTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledFileExportTask;
import com.cannontech.web.scheduledFileExport.validator.ScheduledFileExportValidator;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/archivedValuesExporter/*")
@CheckRoleProperty(YukonRoleProperty.ARCHIVED_DATA_EXPORT)
public class ArchivedValuesExporterController {

    public static String baseKey = "yukon.web.modules.amr.archivedValueExporter.";

    private static DataRangeType[] FIXED_RUN_DATA_RANGE_TYPES = {DataRangeType.END_DATE};
    private static DataRangeType[] FIXED_SCHEDULE_DATA_RANGE_TYPES = {DataRangeType.END_DATE};
    private static DataRangeType[] DYNAMIC_RUN_DATA_RANGE_TYPES = {DataRangeType.DATE_RANGE, DataRangeType.DAYS_PREVIOUS};
    private static DataRangeType[] DYNAMIC_SCHEDULE_DATA_RANGE_TYPES = {DataRangeType.DAYS_PREVIOUS, DataRangeType.SINCE_LAST_CHANGE_ID};
    
    @Autowired private DataRangeValidator dataRangeValidator;
    @Autowired private ArchiveValuesExportFormatDao archiveValuesExportFormatDao;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private ExportAttributeValidator exportAttributeValidator;
    @Autowired private ExportFieldValidator exportFieldValidator;
    @Autowired private ExportFormatValidator exportFormatValidator;
    @Autowired private ExportReportGeneratorService exportReportGeneratorService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private MeterDao meterDao;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private AttributeService attributeService;
    @Autowired private ScheduledFileExportService scheduledFileExportService;
    @Autowired private JobManager jobManager;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private ScheduledFileExportValidator scheduledFileExportValidator;
    
    @RequestMapping
    public String view(ModelMap model, HttpServletRequest request, YukonUserContext userContext, 
                       @ModelAttribute ArchivedValuesExporter archivedValuesExporter) throws ServletRequestBindingException, DeviceCollectionCreationException {
        
        List<ExportFormat> allFormats = archiveValuesExportFormatDao.getAllFormats();
        ExportFormat format = getExportFormat(archivedValuesExporter.getFormatId(), allFormats);
        List<String> generatePreview = exportReportGeneratorService.generatePreview(format, userContext);

        archivedValuesExporter.setFormatId(format.getFormatId());
        archivedValuesExporter.setArchivedValuesExportFormatType(format.getFormatType());
        model.addAttribute("archivedValuesExporter", archivedValuesExporter);

        Map<AttributeGroup, List<BuiltInAttribute>> groupedAttributes = 
                objectFormattingService.sortDisplayableValues(BuiltInAttribute.getAllGroupedAttributes(), userContext);
        model.addAttribute("groupedAttributes", groupedAttributes);
        
        model.addAttribute("allFormats", allFormats);
        model.addAttribute("fixedAttribute", ArchivedValuesExportFormatType.FIXED_ATTRIBUTE);
        model.addAttribute("dynamicAttribute", ArchivedValuesExportFormatType.DYNAMIC_ATTRIBUTE);
        model.addAttribute("preview", generatePreview);
        
        model.addAttribute("dataRangeTypes", createJSONArray(DataRangeType.values()));
        model.addAttribute("fixedRunDataRangeTypes", createJSONArray(FIXED_RUN_DATA_RANGE_TYPES));
        model.addAttribute("fixedScheduleDataRangeTypes", createJSONArray(FIXED_SCHEDULE_DATA_RANGE_TYPES));
        model.addAttribute("dynamicRunDataRangeTypes", createJSONArray(DYNAMIC_RUN_DATA_RANGE_TYPES ));
        model.addAttribute("dynamicScheduleDataRangeTypes", createJSONArray(DYNAMIC_SCHEDULE_DATA_RANGE_TYPES));

        if (StringUtils.isNotBlank(request.getParameter("collectionType"))) {
            DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
            model.addAllAttributes(deviceCollection.getCollectionParameters());
            model.addAttribute("deviceCollection", deviceCollection);
            archivedValuesExporter.setDeviceCollection(deviceCollection);
        }
        
        //Jobs List Prep
        List<ScheduledRepeatingJob> adeExportJobs = scheduledFileExportService.getArchivedDataExportJobs();
        List<ScheduledFileExportJobData> jobDataObjects = Lists.newArrayListWithCapacity(adeExportJobs.size());
        
        int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", 25);
        int page = ServletRequestUtils.getIntParameter(request, "page", 1);
        int startIndex = (page-1) * itemsPerPage;
        
        for(ScheduledRepeatingJob job : adeExportJobs) {
        	jobDataObjects.add(scheduledFileExportService.getBillingJobData(job));
        }
        Collections.sort(jobDataObjects);
        int endIndex = startIndex + itemsPerPage > adeExportJobs.size() ? adeExportJobs.size() : startIndex + itemsPerPage;
        jobDataObjects = jobDataObjects.subList(startIndex, endIndex);
        
        SearchResult<ScheduledFileExportJobData> filterResult = new SearchResult<ScheduledFileExportJobData>();
        filterResult.setBounds(startIndex, itemsPerPage, adeExportJobs.size());
        filterResult.setResultList(jobDataObjects);
        model.addAttribute("filterResult", filterResult);
        
        return "archivedValuesExporter/archiveDataExporterHome.jsp";
    }
    
    @RequestMapping
    public String deleteJob(ModelMap model, int jobId, FlashScope flashScope) {
    	YukonJob job = jobManager.getJob(jobId);
    	ScheduledFileExportTask task = (ScheduledFileExportTask) jobManager.instantiateTask(job);
		String jobName = task.getName();
		jobManager.deleteJob(job);
		
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.amr.archivedValueExporter.deletedJobSuccess", jobName));
		return "redirect:view";
    }
    
    @RequestMapping
    public String selectDevices(ModelMap model, HttpServletRequest request) {
        return "archivedValuesExporter/selectDevices.jsp";
    }

    @RequestMapping
    public String selected(ModelMap model, HttpServletRequest request, YukonUserContext userContext,
                           @ModelAttribute ArchivedValuesExporter archivedValuesExporter)
    throws DeviceCollectionCreationException, ServletException {
        return view(model, request, userContext, archivedValuesExporter);
    }

    @RequestMapping
    public String create(ModelMap model, YukonUserContext userContext, ArchivedValuesExportFormatType formatType) {
        ArchivedValuesExporterBackingBean backingBean = new ArchivedValuesExporterBackingBean();
        backingBean.getFormat().setFormatType(formatType);
        List<String> generatePreview = exportReportGeneratorService.generatePreview(backingBean.getFormat(), userContext);

        model.addAttribute("backingBean", backingBean);
        model.addAttribute("preview", generatePreview);
        model.addAttribute("mode", PageEditMode.CREATE);
        setupModel(model, userContext, backingBean.getFormat());
        return "archivedValuesExporter/exporter.jsp";
    }

    @RequestMapping
    public String copy(ModelMap model, HttpServletRequest request, YukonUserContext userContext, int selectedFormatId) {
        
        ExportFormat format = archiveValuesExportFormatDao.getByFormatId(selectedFormatId);
        format.setFormatId(0);
        format.setFormatName("");

        ArchivedValuesExporterBackingBean backingBean = new ArchivedValuesExporterBackingBean();
        backingBean.setFormat(format);

        List<String> generatePreview = exportReportGeneratorService.generatePreview(backingBean.getFormat(), userContext);

        model.addAttribute("backingBean", backingBean);
        model.addAttribute("preview", generatePreview);
        model.addAttribute("mode", PageEditMode.CREATE);
        setupModel(model, userContext, backingBean.getFormat());
        return "archivedValuesExporter/exporter.jsp";
    }

    @RequestMapping
    public String edit(ModelMap model, HttpServletRequest request, YukonUserContext userContext, int selectedFormatId) {

        ExportFormat format = archiveValuesExportFormatDao.getByFormatId(selectedFormatId);
        ArchivedValuesExporterBackingBean backingBean = new ArchivedValuesExporterBackingBean();
        backingBean.setFormat(format);

        List<String> generatePreview = exportReportGeneratorService.generatePreview(backingBean.getFormat(), userContext);

        model.addAttribute("backingBean", backingBean);
        model.addAttribute("preview", generatePreview);
        model.addAttribute("mode", PageEditMode.EDIT);
        setupModel(model, userContext, backingBean.getFormat());
        return "archivedValuesExporter/exporter.jsp";
    }

    @RequestMapping
    public String removeAttribute(ModelMap model, HttpServletRequest request, YukonUserContext userContext,
                                  @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean) {

        backingBean.removeSelectedAttribute();
        model.addAttribute("mode", PageEditMode.EDIT);
        setupModel(model, userContext, backingBean.getFormat());
        return "archivedValuesExporter/exporter.jsp";
    }

    @RequestMapping
    public String addAttribute(ModelMap model, YukonUserContext userContext,
                               @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean, BindingResult bindingResult) {

        exportAttributeValidator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("showAttributePopup", true);
        } else {
            backingBean.addSelectedAttribute();
        }
        
        model.addAttribute("mode", backingBean.getFormat().getFormatId() == 0 ? PageEditMode.CREATE : PageEditMode.EDIT);
        setupModel(model, userContext, backingBean.getFormat());
        return "archivedValuesExporter/exporter.jsp";
    }

    @RequestMapping
    public String ajaxEditAttribute(ModelMap model, YukonUserContext userContext,
                                    @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean) {

        if (backingBean.getRowIndex() == -1) {
            backingBean.setExportAttribute(new ExportAttribute());
            backingBean.setPageNameKey("addAttribute");
        } else {
            backingBean.setExportAttribute(backingBean.getSelectedAttribute());
            backingBean.setPageNameKey("editAttribute");
        }
        
        setupModel(model, userContext, backingBean.getFormat());
        return "archivedValuesExporter/editAttribute.jsp";
    }

    @RequestMapping
    public String addField(ModelMap model, YukonUserContext userContext,
                           @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean, BindingResult bindingResult) {

        backingBean.resetExportFieldValues();
        exportFieldValidator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("showFieldPopup", true);
        } else {
            backingBean.addSelectedField();
        }

        model.addAttribute("mode", backingBean.getFormat().getFormatId() == 0 ? PageEditMode.CREATE : PageEditMode.EDIT);
        setupModel(model, userContext, backingBean.getFormat());
        return "archivedValuesExporter/exporter.jsp";
    }

    @RequestMapping
    public String removeField(ModelMap model, HttpServletRequest request, YukonUserContext userContext, 
                              @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean) {

        backingBean.removeSelectedField();
        model.addAttribute("mode", PageEditMode.EDIT);
        setupModel(model, userContext, backingBean.getFormat());
        return "archivedValuesExporter/exporter.jsp";
    }
    
    @RequestMapping
    public String ajaxEditField(ModelMap model, YukonUserContext userContext,
                                @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean) {

        if (backingBean.getRowIndex() == -1) {
            RoundingMode roundingMode = globalSettingDao.getEnum(GlobalSettingType.DEFAULT_ROUNDING_MODE, RoundingMode.class);
            backingBean.resetField(YukonRoundingMode.valueOf(roundingMode.name()));
            backingBean.setPageNameKey("addField");
        } else {
            backingBean.setExportField(backingBean.getSelectedField());
            backingBean.setSelectedFieldId(backingBean.findSelectedFieldId());
            backingBean.copyPattern(backingBean.getExportField());
            backingBean.setPageNameKey("editField");
        }

        setupModel(model, userContext, backingBean.getFormat());
        return "archivedValuesExporter/editField.jsp";
    }

    @RequestMapping
    public String moveFieldUp(ModelMap model, HttpServletRequest request, YukonUserContext userContext, 
                              @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean) {

        backingBean.moveFieldUp();
        model.addAttribute("mode", PageEditMode.EDIT);
        setupModel(model, userContext, backingBean.getFormat());
        return "archivedValuesExporter/exporter.jsp";
    }

    @RequestMapping
    public String moveFieldDown(ModelMap model, HttpServletRequest request, YukonUserContext userContext,
                                @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean) {

        backingBean.moveFieldDown();
        model.addAttribute("mode", PageEditMode.EDIT);
        setupModel(model, userContext, backingBean.getFormat());
        return "archivedValuesExporter/exporter.jsp";
    }

    @RequestMapping
    public String saveFormat(FlashScope flashScope, ModelMap model, HttpServletRequest request, YukonUserContext userContext,
                             @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean, BindingResult bindingResult) {

        exportFormatValidator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setError(messages);
            
            model.addAttribute("mode", backingBean.getFormat().getFormatId() == 0 ? PageEditMode.CREATE : PageEditMode.EDIT);
            setupModel(model, userContext, backingBean.getFormat());
            return "archivedValuesExporter/exporter.jsp";
        }
        
        if (backingBean.getFormat().getFormatId() == 0) {
            archiveValuesExportFormatDao.create(backingBean.getFormat());
            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey+"createdFormat", backingBean.getFormat().getFormatName()));
        } else {
            archiveValuesExportFormatDao.update(backingBean.getFormat());
            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey+"updatedFormat", backingBean.getFormat().getFormatName()));
        }

        ArchivedValuesExporter archivedValuesExporter = new ArchivedValuesExporter();
        archivedValuesExporter.setFormatId(backingBean.getFormat().getFormatId());
        return "redirect:view";
    }

    @RequestMapping
    public String deleteFormat(ModelMap model, HttpServletRequest request, YukonUserContext userContext, FlashScope flashScope,
                               @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean) {

        int formatId = backingBean.getFormat().getFormatId();
    	archiveValuesExportFormatDao.delete(backingBean.getFormat().getFormatId());
        //delete any jobs using this format
        scheduledFileExportService.deleteAdeJobsByFormatId(formatId);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey+"deletedFormat", backingBean.getFormat().getFormatName()));
        return "redirect:view";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver = new YukonMessageCodeResolver(baseKey);
            binder.setMessageCodesResolver(msgCodesResolver);
        }

        binder.registerCustomEditor(Attribute.class, new EnumPropertyEditor<>(BuiltInAttribute.class));
        binder.registerCustomEditor(AttributeField.class, new EnumPropertyEditor<>(AttributeField.class));
        binder.registerCustomEditor(FieldType.class, new EnumPropertyEditor<>(FieldType.class));
        binder.registerCustomEditor(MissingAttribute.class, new EnumPropertyEditor<>(MissingAttribute.class));
        binder.registerCustomEditor(PadSide.class, new EnumPropertyEditor<>(PadSide.class));
        binder.registerCustomEditor(YukonRoundingMode.class, new EnumPropertyEditor<>(YukonRoundingMode.class));
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

    @RequestMapping
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
        List<Meter> meters = meterDao.getMetersForYukonPaos(deviceList);
        ExportFormat format = archiveValuesExportFormatDao.getByFormatId(archivedValuesExporter.getFormatId());
        List<String> report = exportReportGeneratorService.generateReport(meters, format, dataRange, userContext, archivedValuesExporter.getAttributesArray());
        String fileName = getFileName(Instant.now().toDate(), format.getFormatName());
        setupResponse(response, fileName);
        createReportFile(response, report);
        return  "";
    }
    
    @RequestMapping
    public String scheduleReport(ModelMap model, FlashScope flashScope, HttpServletRequest request, Integer jobId,
    		@ModelAttribute ArchivedValuesExporter archivedValuesExporter, BindingResult bindingResult) 
    		throws ServletRequestBindingException {
    	
    	DeviceCollection deviceCollection;
    	ExportFormat format;
    	Set<Attribute> attributes = null;
    	DataRange dataRange;
    	ScheduledFileExportData exportData;
    	CronExpressionTagState cronTagState;
    	
    	if(jobId != null) {
    		//edit existing schedule
    		model.addAttribute("jobId", jobId);
    		ScheduledRepeatingJob job = jobManager.getRepeatingJob(jobId);
    		ScheduledArchivedDataFileExportTask task = (ScheduledArchivedDataFileExportTask) jobManager.instantiateTask(job);
    		
    		deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(task.getDeviceGroup());
    		format = archiveValuesExportFormatDao.getByFormatId(task.getFormatId());
    		attributes = task.getAttributes();
    		dataRange = task.getDataRange();
    		cronTagState = cronExpressionTagService.parse(job.getCronString(), job.getUserContext());
    		exportData = new ScheduledFileExportData();
    		exportData.setScheduleName(task.getName());
    		exportData.setExportFileName(task.getExportFileName());
    		exportData.setExportPath(task.getExportPath());
    		exportData.setAppendDateToFileName(task.isAppendDateToFileName());
    		exportData.setNotificationEmailAddresses(task.getNotificationEmailAddresses());
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
    	}
    	
    	model.addAttribute("exportFormat", format);
        model.addAttribute("attributes", attributes);
        model.addAttribute("dataRange", dataRange);
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("exportData", exportData);
        model.addAttribute("cronExpressionTagState", cronTagState);
        
        return "archivedValuesExporter/schedule.jsp";
    }
    
    @RequestMapping
    public String doSchedule(ModelMap model, @ModelAttribute("exportData") ScheduledFileExportData exportData, BindingResult bindingResult, HttpServletRequest request,
    		int formatId, String[] attributes, Integer jobId, YukonUserContext userContext, FlashScope flashScope) 
    		throws ServletRequestBindingException, IllegalArgumentException, ParseException {
    	
    	//Build parameters
    	DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
    	DataRange dataRange = getDataRangeFromRequest(request);
    	
    	Set<Attribute> attributeSet = Sets.newHashSet();
    	if(attributes != null) {
    		for(String attribute : attributes) {
    			attributeSet.add(attributeService.resolveAttributeName(attribute));
    		}
    	}
    	ArchivedDataExportFileGenerationParameters parameters = new ArchivedDataExportFileGenerationParameters(deviceCollection, formatId, attributeSet, dataRange);
    	
    	String scheduleCronString = cronExpressionTagService.build("scheduleCronString", request, userContext);
    	exportData.setScheduleCronString(scheduleCronString);
    	exportData.setParameters(parameters);
    	
    	scheduledFileExportValidator.validate(exportData, bindingResult);
		if(bindingResult.hasErrors()) {
			List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setError(messages);
            model.addAttribute("exportFormat", archiveValuesExportFormatDao.getByFormatId(formatId));
	        model.addAttribute("attributes", attributes);
	        model.addAttribute("dataRange", dataRange);
	        model.addAttribute("deviceCollection", deviceCollection);
	        model.addAttribute("cronExpressionTagState", cronExpressionTagService.parse(scheduleCronString, userContext));
            model.addAttribute("jobId", jobId);
            return "archivedValuesExporter/schedule.jsp";
		}
    	
    	if(jobId == null) {
    		scheduledFileExportService.scheduleFileExport(exportData, userContext);
    		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.amr.archivedValueExporterScheduleSetup.scheduleSuccess", exportData.getScheduleName()));
    	} else {
    		scheduledFileExportService.updateFileExport(exportData, userContext, jobId);
    		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.amr.archivedValueExporterScheduleSetup.updateSuccess", exportData.getScheduleName()));
    	}
    	
    	return "redirect:view";
    }
    
    private DataRange getDataRangeFromRequest(HttpServletRequest request) throws ServletRequestBindingException {
    	DataRange dataRange = new DataRange();
    	
    	String dataRangeTypeString = ServletRequestUtils.getStringParameter(request, "dataRange.dataRangeType");
    	dataRange.setDataRangeType(DataRangeType.valueOf(dataRangeTypeString));
    	
    	if(dataRange.getDataRangeType() == DataRangeType.DATE_RANGE) {
    		LocalDateRange localDateRange = new LocalDateRange();
    		LocalDate startDate = LocalDate.parse(ServletRequestUtils.getStringParameter(request, "dataRange.dateRange.startDate"));
    		LocalDate endDate = LocalDate.parse(ServletRequestUtils.getStringParameter(request, "dataRange.dateRange.endDate"));
    		localDateRange.setStartDate(startDate);
    		localDateRange.setEndDate(endDate);
    		dataRange.setLocalDateRange(localDateRange);
    	} else if(dataRange.getDataRangeType() == DataRangeType.DAYS_PREVIOUS) {
    		int daysPrevious = ServletRequestUtils.getIntParameter(request, "dataRange.daysPrevious");
    		dataRange.setDaysPrevious(daysPrevious);
    	} else if(dataRange.getDataRangeType() == DataRangeType.END_DATE) {
    		//don't worry about this, task will set it when run
    	} else if(dataRange.getDataRangeType() == DataRangeType.SINCE_LAST_CHANGE_ID) {
    		//don't worry about this, task will set it when run
    	}
    	
    	return dataRange;
    }
    
    private <E> JSONArray createJSONArray(E[] objectArray) {
        JSONArray jsonArray = new JSONArray();
        jsonArray .addAll(Arrays.asList(objectArray));
        return jsonArray;
    }
    
    private String getFileName(Date endDate, String formatName) {
        SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("MMddyyyy");
        String fileNameDateFormatString = fileNameDateFormat.format(endDate);
        String fileName = ServletUtil.makeWindowsSafeFileName(formatName + fileNameDateFormatString) + ".csv";
        return fileName;
    }

    private void setupResponse(HttpServletResponse response, String fileName) {
        response.setDateHeader("Expires", 0); // prevents caching at the proxy server
        response.setContentType("text/x-comma-separated-values");
        response.addHeader("Content-Disposition", "attachment;filename=\"" + fileName.toString() + "\"");
    }

    private void createReportFile(HttpServletResponse response, List<String> report)
    throws IOException {

        OutputStream outputStream = response.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        for (String row : report) {
            writer.write(row);
            writer.newLine();
        }
        writer.close();
    }

    private ExportFormat getExportFormat(int selectedFormatId, List<ExportFormat> allFormats) {
        if (selectedFormatId != 0) {
            return archiveValuesExportFormatDao.getByFormatId(selectedFormatId);
        } else {
            return getFirstFormat(allFormats);
        }
    }

    private ExportFormat getFirstFormat(List<ExportFormat> formats) {
        ExportFormat format = null;
        if (!formats.isEmpty()) {
            int formatId = formats.get(0).getFormatId();
            format = archiveValuesExportFormatDao.getByFormatId(formatId);
        } else {
            format = new ExportFormat();
        }
        return format;
    }
    
    /**
     * This method sets up the model for all the base information for the create and edit methods 
     * @param model
     */
    private void setupModel(ModelMap model, YukonUserContext userContext, ExportFormat format) {
        Map<AttributeGroup, List<BuiltInAttribute>> groupedAttributes = 
                objectFormattingService.sortDisplayableValues(BuiltInAttribute.getAllGroupedAttributes(), userContext);
        model.addAttribute("groupedAttributes", groupedAttributes);

        List<String> generatePreview = exportReportGeneratorService.generatePreview(format, userContext);
        model.addAttribute("preview", generatePreview);
        model.addAttribute("showAttributeSection", format.getFormatType() == ArchivedValuesExportFormatType.FIXED_ATTRIBUTE);

        model.addAttribute("attributeFields", AttributeField.values());
        model.addAttribute("attributes", BuiltInAttribute.values());
        model.addAttribute("dataSelection", DataSelection.values());
        model.addAttribute("missingAttribute", MissingAttribute.values());
        model.addAttribute("padSide", PadSide.values());
        model.addAttribute("roundingModes", YukonRoundingMode.values());
    }
}