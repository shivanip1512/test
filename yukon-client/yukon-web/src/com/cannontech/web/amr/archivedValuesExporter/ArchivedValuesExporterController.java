package com.cannontech.web.amr.archivedValuesExporter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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
import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.amr.archivedValueExporter.model.DataSelection;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.model.MissingAttribute;
import com.cannontech.amr.archivedValueExporter.model.PadSide;
import com.cannontech.amr.archivedValueExporter.model.YukonRoundingMode;
import com.cannontech.amr.archivedValueExporter.service.ExportReportGeneratorService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.archivedValuesExporter.model.ArchivedValuesExporter;
import com.cannontech.web.amr.archivedValuesExporter.model.ArchivedValuesExporterBackingBean;
import com.cannontech.web.amr.archivedValuesExporter.validator.ArchiveValuesExporterValidator;
import com.cannontech.web.amr.archivedValuesExporter.validator.ExportAttributeValidator;
import com.cannontech.web.amr.archivedValuesExporter.validator.ExportFieldValidator;
import com.cannontech.web.amr.archivedValuesExporter.validator.ExportFormatValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/archivedValuesExporter/*")
@CheckRoleProperty(YukonRoleProperty.ARCHIVED_DATA_EXPORT)
public class ArchivedValuesExporterController {

    public static String baseKey = "yukon.web.modules.amr.archivedValueExporter.";

    @Autowired private ArchiveValuesExporterValidator archiveValuesExporterValidator;
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
    
    @RequestMapping
    public String view(ModelMap model, YukonUserContext userContext, @ModelAttribute ArchivedValuesExporter archivedValuesExporter) {

        if (archivedValuesExporter.getEndDate() == null) {
            archivedValuesExporter.setEndDate(new Date());
        }
        
        List<ExportFormat> allFormats = archiveValuesExportFormatDao.getAllFormats();
        ExportFormat format = getExportFormat(archivedValuesExporter.getFormatId(), allFormats);
        List<String> generatePreview = exportReportGeneratorService.generatePreview(format, userContext);

        model.addAttribute("allFormats", allFormats);
        model.addAttribute("format", format);
        model.addAttribute("preview", generatePreview);
        return "archivedValuesExporter/archiveDataExporterHome.jsp";
    }

    @RequestMapping
    public String selectDevices() {
        return "archivedValuesExporter/selectDevices.jsp";
    }

    @RequestMapping
    public String selected(ModelMap model, HttpServletRequest request, YukonUserContext userContext,
                           @ModelAttribute ArchivedValuesExporter archivedValuesExporter)
    throws DeviceCollectionCreationException, ServletException {

        if (StringUtils.isNotBlank(request.getParameter("collectionType"))) {
            DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
            model.addAllAttributes(deviceCollection.getCollectionParameters());
            model.addAttribute("deviceCollection", deviceCollection);
            archivedValuesExporter.setDeviceCollection(deviceCollection);
        }

        return view(model, userContext, archivedValuesExporter);
    }

    @RequestMapping
    public String create(ModelMap model, YukonUserContext userContext) {
        ArchivedValuesExporterBackingBean backingBean = new ArchivedValuesExporterBackingBean();
        List<String> generatePreview = exportReportGeneratorService.generatePreview(backingBean.getFormat(), userContext);

        model.addAttribute("backingBean", backingBean);
        model.addAttribute("preview", generatePreview);
        model.addAttribute("mode", PageEditMode.CREATE);
        setupModel(model, userContext);
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
        setupModel(model, userContext);
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
        setupModel(model, userContext);
        return "archivedValuesExporter/exporter.jsp";
    }

    @RequestMapping
    public String removeAttribute(ModelMap model, HttpServletRequest request, YukonUserContext userContext,
                                  @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean) {

        backingBean.removeSelectedAttribute();
        model.addAttribute("mode", PageEditMode.EDIT);
        setupModel(model, userContext);
        return "archivedValuesExporter/exporter.jsp";
    }

    @RequestMapping
    public String addAttribute(ModelMap model, YukonUserContext userContext,
                               @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean, BindingResult bindingResult) {

        PageEditMode mode = PageEditMode.EDIT;
        exportAttributeValidator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("showAttributePopup", true);
        } else {
            backingBean.addSelectedAttribute();
        }
        if(backingBean.getFormat().getFormatId() == 0){
            mode = PageEditMode.CREATE;
        }
        
        model.addAttribute("mode", mode);
        setupModel(model, userContext);
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
        
        setupModel(model, userContext);
        return "archivedValuesExporter/editAttribute.jsp";
    }

    @RequestMapping
    public String addField(ModelMap model, YukonUserContext userContext,
                           @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean, BindingResult bindingResult) {

        PageEditMode mode = PageEditMode.EDIT;
        backingBean.resetExportFieldValues();
        exportFieldValidator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("showFieldPopup", true);
        } else {
            backingBean.addSelectedField();
        }
        if(backingBean.getFormat().getFormatId() == 0){
            mode = PageEditMode.CREATE;
        }

        model.addAttribute("mode", mode);
        setupModel(model, userContext);
        return "archivedValuesExporter/exporter.jsp";
    }

    @RequestMapping
    public String removeField(ModelMap model, HttpServletRequest request, YukonUserContext userContext, 
                              @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean) {

        backingBean.removeSelectedField();
        model.addAttribute("mode", PageEditMode.EDIT);
        setupModel(model, userContext);
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

        setupModel(model, userContext);
        return "archivedValuesExporter/editField.jsp";
    }

    @RequestMapping
    public String moveFieldUp(ModelMap model, HttpServletRequest request, YukonUserContext userContext, 
                              @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean) {

        backingBean.moveFieldUp(true);
        model.addAttribute("mode", PageEditMode.EDIT);
        setupModel(model, userContext);
        return "archivedValuesExporter/exporter.jsp";
    }

    @RequestMapping
    public String moveFieldDown(ModelMap model, HttpServletRequest request, YukonUserContext userContext,
                                @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean) {

        backingBean.moveFieldUp(false);
        model.addAttribute("mode", PageEditMode.EDIT);
        setupModel(model, userContext);
        return "archivedValuesExporter/exporter.jsp";
    }

    @RequestMapping
    public String saveFormat(FlashScope flashScope, ModelMap model, HttpServletRequest request, YukonUserContext userContext,
                             @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean, BindingResult bindingResult) {

        exportFormatValidator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setError(messages);
            
            if(backingBean.getFormat().getFormatId() == 0){
                model.addAttribute("mode",PageEditMode.CREATE);
            }else{
                model.addAttribute("mode", PageEditMode.EDIT);
            }

            setupModel(model, userContext);
            return "archivedValuesExporter/exporter.jsp";
        }
        
        if (backingBean.getFormat().getFormatId() == 0) {
            archiveValuesExportFormatDao.create(backingBean.getFormat());
        } else {
            archiveValuesExportFormatDao.update(backingBean.getFormat());
        }

//        TODO We should have a confirm message here.  This will be uncommented when I work on the Dynamic Attribute stuff.        
//        flashScope.setConfirm(message);
        ArchivedValuesExporter archivedValuesExporter = new ArchivedValuesExporter();
        archivedValuesExporter.setFormatId(backingBean.getFormat().getFormatId());
        archivedValuesExporter.setEndDate(new Date());
        return "redirect:view";
    }

    @RequestMapping
    public String deleteFormat(ModelMap model, HttpServletRequest request, YukonUserContext userContext, FlashScope flashScope,
                               @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean) {
//      TODO We should have a confirm message here.  This will be uncommented when I work on the Dynamic Attribute stuff.        
//      flashScope.setConfirm(message);

        archiveValuesExportFormatDao.delete(backingBean.getFormat().getFormatId());
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

        
        binder.registerCustomEditor(Date.class, "endDate", datePropertyEditorFactory.getPropertyEditor(DateFormatEnum.DATE, userContext));
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

    @RequestMapping
    public String generateReport(FlashScope flashScope, ModelMap model, HttpServletResponse response, HttpServletRequest request,
                                 @ModelAttribute ArchivedValuesExporter archivedValuesExporter, BindingResult bindingResult,
                                 YukonUserContext userContext)
    throws IOException, ServletRequestBindingException, DeviceCollectionCreationException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        archivedValuesExporter.setDeviceCollection(deviceCollection); // TODO It would be awesome if we could just bind this, but we don't have a way just yet.

        archiveValuesExporterValidator.validate(archivedValuesExporter, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setError(messages);
            
            return "redirect:view";
        }
            
        List<SimpleDevice> deviceList = archivedValuesExporter.getDeviceCollection().getDeviceList();
        List<Meter> meters = meterDao.getMetersForYukonPaos(deviceList);
        ExportFormat format = archiveValuesExportFormatDao.getByFormatId(archivedValuesExporter.getFormatId());
        List<String> report = exportReportGeneratorService.generateReport(meters, format, archivedValuesExporter.getEndDate(), userContext);
        String fileName = getFileName(archivedValuesExporter.getEndDate(), format.getFormatName());
        setupResponse(response, fileName);
        createReportFile(response, report);
        return  "";
    }

    private String getFileName(Date endDate, String formatName) {
        SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("MMddyyyy");
        String fileNameDateFormatString = fileNameDateFormat.format(endDate);
        String fileName = ServletUtil.makeWindowsSafeFileName(formatName + fileNameDateFormatString) + ".txt";
        return fileName;
    }

    private void setupResponse(HttpServletResponse response, String fileName) {
        response.setDateHeader("Expires", 0); // prevents caching at the proxy server
        response.setContentType("text/x-comma-separated-values");
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName.toString());
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
    
    /**
     * This method sets up the model for all the base information for the create and edit methods 
     * @param model
     */
    private void setupModel(ModelMap model, YukonUserContext userContext) {
        Map<AttributeGroup, List<BuiltInAttribute>> groupedAttributes = 
                objectFormattingService.sortDisplayableValues(BuiltInAttribute.getAllGroupedAttributes(), userContext);
        model.addAttribute("groupedAttributes", groupedAttributes);

        model.addAttribute("attributeFields", AttributeField.values());
        model.addAttribute("attributes", BuiltInAttribute.values());
        model.addAttribute("dataSelection", DataSelection.values());
        model.addAttribute("missingAttribute", MissingAttribute.values());
        model.addAttribute("padSide", PadSide.values());
        model.addAttribute("roundingModes", YukonRoundingMode.values());
    }
}