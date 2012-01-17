package com.cannontech.web.amr.archivedValuesExporter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFormatDao;
import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.amr.archivedValueExporter.model.DataSelection;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.model.MissingAttribute;
import com.cannontech.amr.archivedValueExporter.model.PadSide;
import com.cannontech.amr.archivedValueExporter.service.ExportReportGeneratorService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.archivedValuesExporter.validator.ExportAttributeValidator;
import com.cannontech.web.amr.archivedValuesExporter.validator.ExportFieldValidator;
import com.cannontech.web.amr.archivedValuesExporter.validator.ExportFormatValidator;
import com.cannontech.web.amr.archivedValuesExporter.validator.ExportReportValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/archivedValuesExporter/*")
@CheckRoleProperty(YukonRoleProperty.ARCHIVED_DATA_EXPORTER)
public class ArchivedValuesExporterController {

    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private ExportReportValidator exportReportValidator;
    @Autowired private ExportFormatValidator exportFormatValidator;
    @Autowired private ExportAttributeValidator exportAttributeValidator;
    @Autowired private ExportFieldValidator exportFieldValidator;
    @Autowired private ArchiveValuesExportFormatDao archiveValuesExportFormatDao;
    @Autowired private MeterDao meterDao;
    @Autowired private ExportReportGeneratorService exportReportGeneratorService;
    private Meter previewMeter;

    public ArchivedValuesExporterController() {
        previewMeter = new Meter();
        previewMeter.setMeterNumber("12345470");
        previewMeter.setName("MCT-410CL");
        previewMeter.setAddress("60220872");
        previewMeter.setRoute("CCU 38 Prairie");
    }

    @ModelAttribute("attributes")
    public BuiltInAttribute[] getAttributes() {
        return BuiltInAttribute.values();
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
    public List<String> getRoundingModes() {
        return getRoundModes();
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
        backingBean.setPreview(exportReportGeneratorService.generateReport(Collections
            .singletonList(previewMeter), backingBean.getFormat(), null, userContext));
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
        return getView(model, request, PageEditMode.EDIT, backingBean, userContext);
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
        ExportFormatHelper.defaultFormat(backingBean);
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

        exportAttributeValidator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            backingBean.setPopupToOpen("addAttributePopup");
            
        } else {
            backingBean.addSelectedAttribute();
        }
        return getView(model, request, PageEditMode.EDIT, backingBean, userContext);
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
        backingBean.resetExportFieldValues();
        exportFieldValidator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            backingBean.setPopupToOpen("addFieldPopup");            
        } else {
            backingBean.addSelectedField();
        }
        return getView(model, request, PageEditMode.EDIT, backingBean, userContext);
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
            ExportFormatHelper.defaultField(backingBean);
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
            mode = PageEditMode.EDIT;
        } else {
            ExportFormat format = null;
            if (backingBean.getFormat().getFormatId() == 0) {
                format = archiveValuesExportFormatDao.create(backingBean.getFormat());
            } else {
                format = archiveValuesExportFormatDao.update(backingBean.getFormat());
            }
            backingBean.setSelectedFormatId(format.getFormatId());
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

        binder.registerCustomEditor(Attribute.class, attributeEditor);
        binder.registerCustomEditor(FieldType.class, fieldTypeEditor);
        binder.registerCustomEditor(AttributeField.class, attributeFieldEditor);
        binder.registerCustomEditor(MissingAttribute.class, missingAttributeFieldEditor);
        binder.registerCustomEditor(PadSide.class, padSideFieldEditor);
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
            if (backingBean.getEndDate() == null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                backingBean.setEndDate(sdf.format(new Date()));
            }
        }
        backingBean.setPreview(exportReportGeneratorService.generateReport(Collections
            .singletonList(previewMeter), backingBean.getFormat(), null, userContext));
        return "archivedValuesExporter/exporter.jsp";
    }

    private static List<String> getRoundModes() {
        List<String> roundingModeStrs = new ArrayList<String>();
        Set<RoundingMode> roundingModeExcludeList = Collections.singleton(RoundingMode.UNNECESSARY);
        RoundingMode[] roundingModes = RoundingMode.values();
        for (RoundingMode roundingMode : roundingModes) {
            if (!roundingModeExcludeList.contains(roundingMode)) {
                roundingModeStrs.add(roundingMode.toString());
            }
        }
        return roundingModeStrs;
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
                                                            parseEndDate(backingBean.getEndDate()),
                                                            userContext);
            String fileName = getFileName(backingBean.getEndDate(), format.getFormatName());
            setupResponse(response, fileName);
            createReportFile(response, report);
            return  "";
        }
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
        flashScope.setError(messages);
        ExportFormat format = archiveValuesExportFormatDao.getByFormatId(backingBean.getSelectedFormatId());
        backingBean.setFormat(format);
        return getView(model, request, PageEditMode.VIEW, backingBean, userContext);
    }

    private Date parseEndDate(String endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date date = sdf.parse(endDate);
        return date;
    }

    private String getFileName(String endDate, String formatName) throws ParseException {
        Date date = parseEndDate(endDate);
        SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("MMddyyyy");
        String fileNameDateFormatString = fileNameDateFormat.format(date);
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