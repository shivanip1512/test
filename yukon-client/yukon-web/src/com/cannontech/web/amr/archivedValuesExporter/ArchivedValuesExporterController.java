package com.cannontech.web.amr.archivedValuesExporter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.archivedValuesExporter.validator.ExportAttributeValidator;
import com.cannontech.web.amr.archivedValuesExporter.validator.ExportFieldValidator;
import com.cannontech.web.amr.archivedValuesExporter.validator.ExportFormatValidator;
import com.cannontech.web.amr.archivedValuesExporter.validator.ExportReportValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/archivedValuesExporter/*")
@CheckRoleProperty(YukonRoleProperty.ARCHIVED_DATA_EXPORT)
public class ArchivedValuesExporterController {

    public static String baseKey = "yukon.web.modules.amr.archivedValueExporter.";
    
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private ExportReportValidator exportReportValidator;
    @Autowired private ExportFormatValidator exportFormatValidator;
    @Autowired private ExportAttributeValidator exportAttributeValidator;
    @Autowired private ExportFieldValidator exportFieldValidator;
    @Autowired private ArchiveValuesExportFormatDao archiveValuesExportFormatDao;
    @Autowired private ExportReportGeneratorService exportReportGeneratorService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private MeterDao meterDao;
    @Autowired private ObjectFormattingService objectFormattingService;
    
    @ModelAttribute("attributes")
    public BuiltInAttribute[] getAttributes() {
        return BuiltInAttribute.values();
    }

    @ModelAttribute("groupedAttributes")
    public Map<AttributeGroup, List<BuiltInAttribute>> getGroupedAttributes(YukonUserContext userContext) {
        return objectFormattingService.sortDisplayableValues(
                BuiltInAttribute.getAllGroupedAttributes(), userContext);
    }

    @ModelAttribute("dataSelection")
    public DataSelection[] getDataSelection() {
        return DataSelection.values();
    }

    @ModelAttribute("attributeFields")
    public AttributeField[] getAttributeField() {
        return AttributeField.values();
    }

    @ModelAttribute("padSide")
    public PadSide[] getPadSide() {
        return PadSide.values();
    }

    @ModelAttribute("missingAttribute")
    public MissingAttribute[] getMissingAttribute() {
        return MissingAttribute.values();
    }

    @ModelAttribute("roundingModes")
    public YukonRoundingMode[] getRoundingModes() {
        return YukonRoundingMode.values();
    }

    @RequestMapping
    public String view(ModelMap model,
                       HttpServletRequest request,
                       @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                       YukonUserContext userContext)
            throws ServletException {
        String view = getView(model, request, PageEditMode.VIEW, backingBean, userContext);
        ExportFormat format = null;
        if (backingBean.getSelectedFormatId() != 0) {
            format = archiveValuesExportFormatDao.getByFormatId(backingBean.getSelectedFormatId());
        } else {
            format = getFirstFormat(backingBean.getAllFormats());
        }
        backingBean.setFormat(format);
        backingBean.setPreview(exportReportGeneratorService.generatePreview(backingBean.getFormat(),userContext));
        model.addAttribute("editMode", PageEditMode.EDIT);
        return view;
    }

    @RequestMapping
    public String cancel(ModelMap model,
                         HttpServletRequest request,
                         @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                         YukonUserContext userContext)
            throws DeviceCollectionCreationException, ServletException {
        backingBean.setSelectedFormatId(backingBean.getFormat().getFormatId());
        return view(model, request, backingBean, userContext);
    }
    
    @RequestMapping
    public String copy(ModelMap model,
                       HttpServletRequest request,
                       @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                       YukonUserContext userContext)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        ExportFormat format =
            archiveValuesExportFormatDao.getByFormatId(backingBean.getSelectedFormatId());
        format.setFormatId(0);
        format.setFormatName("");
        backingBean.setFormat(format);
        return getView(model, request, PageEditMode.CREATE, backingBean, userContext);
    }

    @RequestMapping
    public String edit(ModelMap model,
                       HttpServletRequest request,
                       @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                       YukonUserContext userContext)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        ExportFormat format =
            archiveValuesExportFormatDao.getByFormatId(backingBean.getSelectedFormatId());
        backingBean.setFormat(format);
        return getView(model, request, PageEditMode.EDIT, backingBean, userContext);
    }

    @RequestMapping
    public String create(ModelMap model,
                         HttpServletRequest request,
                         @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                         YukonUserContext userContext)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        backingBean.resetFormat();
        return getView(model, request, PageEditMode.CREATE, backingBean, userContext);
    }

    @RequestMapping
    public String selectDevices(ModelMap model, HttpServletRequest request) {
        return "archivedValuesExporter/selectDevices.jsp";
    }

    @RequestMapping
    public String selected(ModelMap model,
                           HttpServletRequest request,
                           @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                           YukonUserContext userContext)
            throws DeviceCollectionCreationException, ServletException {
        return view(model, request, backingBean, userContext);
    }

    @RequestMapping
    public String removeAttribute(ModelMap model,
                                  HttpServletRequest request,
                                  @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                                  YukonUserContext userContext)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        backingBean.removeSelectedAttribute();
        return getView(model, request, PageEditMode.EDIT, backingBean, userContext);
    }

    @RequestMapping
    public String addAttribute(ModelMap model,
                               @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                               BindingResult bindingResult, HttpServletRequest request,
                               YukonUserContext userContext)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        PageEditMode mode = PageEditMode.EDIT;
        exportAttributeValidator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            backingBean.setPopupToOpen("addAttributePopup");
            
        } else {
            backingBean.addSelectedAttribute();
        }
        if(backingBean.getFormat().getFormatId() == 0){
            mode = PageEditMode.CREATE;
        }
        return getView(model, request, mode, backingBean, userContext);
    }

    @RequestMapping
    public String ajaxEditAttribute(ModelMap model,
                                    @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean) {
        if (backingBean.getRowIndex() == -1) {
            backingBean.setExportAttribute(new ExportAttribute());
            backingBean.setPageNameKey("addAttribute");
        } else {
            backingBean.setExportAttribute(backingBean.getSelectedAttribute());
            backingBean.setPageNameKey("editAttribute");
        }
        model.addAttribute("backingBean", backingBean);
        return "archivedValuesExporter/editAttribute.jsp";
    }

    @RequestMapping
    public String addField(ModelMap model,
                           @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                           BindingResult bindingResult, HttpServletRequest request,
                           YukonUserContext userContext)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        PageEditMode mode = PageEditMode.EDIT;
        backingBean.resetExportFieldValues();
        exportFieldValidator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            backingBean.setPopupToOpen("addFieldPopup");            
        } else {
            backingBean.addSelectedField();
        }
        if(backingBean.getFormat().getFormatId() == 0){
            mode = PageEditMode.CREATE;
        }
        return getView(model, request, mode, backingBean, userContext);
    }

    @RequestMapping
    public String removeField(ModelMap model,
                              @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                              HttpServletRequest request,
                              YukonUserContext userContext)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        backingBean.removeSelectedField();
        return getView(model, request, PageEditMode.EDIT, backingBean, userContext);
    }
    
    @RequestMapping
    public String ajaxEditField(ModelMap model,
                                @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean) {
        if (backingBean.getRowIndex() == -1) {
            RoundingMode roundingMode =
                rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.DEFAULT_ROUNDING_MODE,
                                                     RoundingMode.class,
                                                     null);
            backingBean.resetField(YukonRoundingMode.valueOf(roundingMode.name()));
            backingBean.setPageNameKey("addField");
        } else {
            backingBean.setExportField(backingBean.getSelectedField());
            backingBean.setSelectedFieldId(backingBean.findSelectedFieldId());
            backingBean.copyPattern(backingBean.getExportField());
            backingBean.setPageNameKey("editField");
        }
        model.addAttribute("backingBean", backingBean);
        return "archivedValuesExporter/editField.jsp";
    }

    
    @RequestMapping
    public String moveFieldUp(ModelMap model,
                              @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                              HttpServletRequest request,
                              YukonUserContext userContext)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        backingBean.moveFieldUp(true);
        return getView(model, request, PageEditMode.EDIT, backingBean, userContext);
    }

    @RequestMapping
    public String moveFieldDown(ModelMap model,
                                @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                                HttpServletRequest request,
                                YukonUserContext userContext)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        backingBean.moveFieldUp(false);
        return getView(model, request, PageEditMode.EDIT, backingBean, userContext);
    }

    @RequestMapping
    public String saveFormat(FlashScope flashScope,
                             ModelMap model,
                             HttpServletRequest request,
                             @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                             BindingResult bindingResult, YukonUserContext userContext)
            throws ServletRequestBindingException,
            DeviceCollectionCreationException {
        PageEditMode mode = PageEditMode.VIEW;
        exportFormatValidator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setError(messages);
            if(backingBean.getFormat().getFormatId() == 0){
                mode = PageEditMode.CREATE;
            }else{
                mode = PageEditMode.EDIT;
            }
        } else {
            ExportFormat format = null;
            if (backingBean.getFormat().getFormatId() == 0) {
                format = archiveValuesExportFormatDao.create(backingBean.getFormat());
            } else {
                format = archiveValuesExportFormatDao.update(backingBean.getFormat());
            }
            backingBean.setSelectedFormatId(format.getFormatId());
            model.addAttribute("editMode", PageEditMode.EDIT);
        }
        return getView(model, request, mode, backingBean, userContext);
    }

    @RequestMapping
    public String deleteFormat(ModelMap model,
                               HttpServletRequest request,
                               @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                               YukonUserContext userContext)
            throws ServletException {
        archiveValuesExportFormatDao.delete(backingBean.getFormat().getFormatId());
        backingBean.setSelectedFieldId(0);
        return view(model, request, backingBean, userContext);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver =
                new YukonMessageCodeResolver(baseKey);
            binder.setMessageCodesResolver(msgCodesResolver);
        }

        EnumPropertyEditor<BuiltInAttribute> attributeEditor =
            new EnumPropertyEditor<BuiltInAttribute>(BuiltInAttribute.class);
        EnumPropertyEditor<FieldType> fieldTypeEditor =
            new EnumPropertyEditor<FieldType>(FieldType.class);
        EnumPropertyEditor<AttributeField> attributeFieldEditor =
            new EnumPropertyEditor<AttributeField>(AttributeField.class);
        EnumPropertyEditor<PadSide> padSideFieldEditor =
            new EnumPropertyEditor<PadSide>(PadSide.class);
        EnumPropertyEditor<MissingAttribute> missingAttributeFieldEditor =
            new EnumPropertyEditor<MissingAttribute>(MissingAttribute.class);
        EnumPropertyEditor<YukonRoundingMode> roundingModeEditor =
                new EnumPropertyEditor<YukonRoundingMode>(YukonRoundingMode.class);
     
        binder.registerCustomEditor(Attribute.class, attributeEditor);
        binder.registerCustomEditor(FieldType.class, fieldTypeEditor);
        binder.registerCustomEditor(AttributeField.class, attributeFieldEditor);
        binder.registerCustomEditor(MissingAttribute.class, missingAttributeFieldEditor);
        binder.registerCustomEditor(PadSide.class, padSideFieldEditor);
        binder.registerCustomEditor(YukonRoundingMode.class, roundingModeEditor);
        binder.registerCustomEditor(Date.class, "endDate", datePropertyEditorFactory.getPropertyEditor(DateFormatEnum.DATE, userContext));
    }

    private void addDeviceCollectionAttribute(ModelMap model, HttpServletRequest request,
                                              ArchivedValuesExporterBackingBean backingBean)
            throws ServletRequestBindingException, DeviceCollectionCreationException {

        if (StringUtils.isNotBlank(request.getParameter("collectionType"))) {
            DeviceCollection deviceCollection =
                this.deviceCollectionFactory.createDeviceCollection(request);
            model.addAllAttributes(deviceCollection.getCollectionParameters());
            model.addAttribute("deviceCollection", deviceCollection);
            backingBean.setDeviceCollection(deviceCollection);
        }

    }

    private String getView(ModelMap model, HttpServletRequest request, PageEditMode mode,
                           ArchivedValuesExporterBackingBean backingBean,
                           YukonUserContext userContext)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        model.addAttribute("mode", mode);
        model.addAttribute("backingBean", backingBean);
        addDeviceCollectionAttribute(model, request, backingBean);
        if (mode == PageEditMode.VIEW) {
            backingBean.setAllFormats(archiveValuesExportFormatDao.getAllFormats());
        }
        backingBean.setTimezone(userContext.getJodaTimeZone());
        backingBean.setPreview(exportReportGeneratorService.generatePreview(backingBean.getFormat(),userContext));
        return "archivedValuesExporter/exporter.jsp";
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
    public String generateReport(FlashScope flashScope,
                                 ModelMap model,
                                 HttpServletResponse response,
                                 HttpServletRequest request,
                                 @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                                 BindingResult bindingResult,
                                 YukonUserContext userContext)
            throws ParseException, IOException, ServletRequestBindingException,
            DeviceCollectionCreationException {
        exportReportValidator.validate(backingBean, bindingResult);
        if (!bindingResult.hasErrors()) {
            DeviceCollection deviceCollection =
                deviceCollectionFactory.createDeviceCollection(request);
            List<SimpleDevice> deviceList = deviceCollection.getDeviceList();
            List<Meter> meters = meterDao.getMetersForYukonPaos(deviceList);
            ExportFormat format =
                archiveValuesExportFormatDao.getByFormatId(backingBean.getSelectedFormatId());
            List<String> report =
                exportReportGeneratorService.generateReport(meters,
                                                            format,
                                                            backingBean.getEndDate(),
                                                            userContext);
            String fileName = getFileName(backingBean.getEndDate(), format.getFormatName());
            setupResponse(response, fileName);
            createReportFile(response, report);
            return  "";
        }
        String view = getView(model, request, PageEditMode.VIEW, backingBean, userContext);
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
        flashScope.setError(messages);
        ExportFormat format = archiveValuesExportFormatDao.getByFormatId(backingBean.getSelectedFormatId());
        backingBean.setFormat(format);
        model.addAttribute("editMode", PageEditMode.EDIT);
        backingBean.setPreview(exportReportGeneratorService.generatePreview(backingBean.getFormat(),userContext));
        return view;
    }

    private String getFileName(Date endDate, String formatName) throws ParseException {
        SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("MMddyyyy");
        String fileNameDateFormatString = fileNameDateFormat.format(endDate);
        String fileName =
            ServletUtil.makeWindowsSafeFileName(formatName + fileNameDateFormatString) + ".txt";
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
}