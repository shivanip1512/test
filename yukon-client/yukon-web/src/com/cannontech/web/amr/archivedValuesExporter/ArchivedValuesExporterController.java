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
@CheckRoleProperty(YukonRoleProperty.DYNAMIC_BILLING_FILE_SETUP)
public class ArchivedValuesExporterController {

    @Autowired
    private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired
    private ExportReportValidator exportReportValidator;
    @Autowired
    private ExportFormatValidator exportFormatValidator;
    @Autowired
    private ExportAttributeValidator exportAttributeValidator;
    @Autowired
    private ExportFieldValidator exportFieldValidator;
    @Autowired
    private ArchiveValuesExportFormatDao archiveValuesExportFormatDao;
    @Autowired
    private MeterDao meterDao;

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

    @ModelAttribute("roundingModes")
    public List<String> getRoundingModes() {
        return getRoundModes();
    }

    @RequestMapping
    public String view(ModelMap model,
                       HttpServletRequest request,
                       @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean)
            throws ServletException {
        String view = getView(model, request, PageEditMode.VIEW, backingBean);
        ExportFormat format = null;
        if (backingBean.getSelectedFormatId() != 0) {
            format = archiveValuesExportFormatDao.getByFormatId(backingBean.getSelectedFormatId());
        } else {
            format = getFirstFormat(backingBean.getAllFormats());
        }
        backingBean.setFormat(format);
        return view;
    }

    @RequestMapping(params = "cancel")
    public String cancel(ModelMap model,
                         HttpServletRequest request,
                         @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        return getView(model, request, PageEditMode.VIEW, backingBean);
    }

    @RequestMapping(params = "copy")
    public String copy(ModelMap model, HttpServletRequest request,
                       @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        ExportFormat format =
            archiveValuesExportFormatDao.getByFormatId(backingBean.getSelectedFormatId());
        format.setFormatId(0);
        format.setFormatName("");
        backingBean.setFormat(format);
        return getView(model, request, PageEditMode.EDIT, backingBean);
    }

    @RequestMapping(params = "edit")
    public String edit(ModelMap model, HttpServletRequest request,
                       @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        ExportFormat format =
            archiveValuesExportFormatDao.getByFormatId(backingBean.getSelectedFormatId());
        backingBean.setFormat(format);
        return getView(model, request, PageEditMode.EDIT, backingBean);
    }

    @RequestMapping(params = "create")
    public String create(ModelMap model,
                         HttpServletRequest request,
                         @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        ExportFormatHelper.preloadFormat(backingBean);
        return getView(model, request, PageEditMode.CREATE, backingBean);
    }

    @RequestMapping(params = "selectDevices")
    public String selectDevices(ModelMap model, HttpServletRequest request) {
        return "archivedValuesExporter/selectDevices.jsp";
    }

    @RequestMapping
    public String selected(ModelMap model,
                           HttpServletRequest request,
                           @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean)
            throws DeviceCollectionCreationException, ServletException {
        return view(model, request, backingBean);
    }

    @RequestMapping(params = "editAttribute")
    public String editAttribute(ModelMap model,
                                HttpServletRequest request,
                                @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        backingBean.setExportAttribute(backingBean.getSelectedAttribute());
        backingBean.setPopupToOpen("addAttributePopup");
        return getView(model, request, PageEditMode.EDIT, backingBean);
    }

    @RequestMapping(params = "removeAttribute")
    public String removeAttribute(ModelMap model,
                                  HttpServletRequest request,
                                  @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        backingBean.removeSelectedAttribute();
        return getView(model, request, PageEditMode.EDIT, backingBean);
    }

    @RequestMapping(params = "addAttribute")
    public String addAttribute(ModelMap model,
                               @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                               BindingResult bindingResult, HttpServletRequest request)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        exportAttributeValidator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            backingBean.setPopupToOpen("addAttributePopup");
        } else {
            backingBean.addSelectedAttribute();
        }
        return getView(model, request, PageEditMode.EDIT, backingBean);
    }

    @RequestMapping(params = "addNewAttribute")
    public String addNewAttribute(ModelMap model,
                                  @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                                  BindingResult bindingResult, HttpServletRequest request)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        backingBean.setExportAttribute(new ExportAttribute());
        backingBean.setRowIndex(-1);
        backingBean.setPopupToOpen("addAttributePopup");
        return getView(model, request, PageEditMode.EDIT, backingBean);
    }

    @RequestMapping(params = "addField")
    public String addField(ModelMap model,
                           @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                           BindingResult bindingResult, HttpServletRequest request)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        backingBean.prefillExportField();
        exportFieldValidator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            backingBean.setPopupToOpen("addFieldPopup");
        } else {
            backingBean.addSelectedField();
        }
        return getView(model, request, PageEditMode.EDIT, backingBean);
    }

    @RequestMapping(params = "removeField")
    public String removeField(ModelMap model,
                              @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                              BindingResult bindingResult, HttpServletRequest request)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        backingBean.removeSelectedField();
        return getView(model, request, PageEditMode.EDIT, backingBean);
    }

    @RequestMapping(params = "editField")
    public String editField(ModelMap model,
                            HttpServletRequest request,
                            @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        backingBean.setExportField(backingBean.getSelectedField());
        backingBean.setSelectedFieldId(backingBean.getExportField());
        backingBean.setPopupToOpen("addFieldPopup");
        return getView(model, request, PageEditMode.EDIT, backingBean);
    }

    @RequestMapping(params = "addNewField")
    public String addNewField(ModelMap model,
                              HttpServletRequest request,
                              @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        ExportFormatHelper.preloadField(backingBean);
        backingBean.setPopupToOpen("addFieldPopup");
        return getView(model, request, PageEditMode.EDIT, backingBean);
    }

    @RequestMapping(params = "moveFieldUp")
    public String moveFieldUp(ModelMap model,
                              @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                              BindingResult bindingResult, HttpServletRequest request)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        backingBean.moveFieldUp(true);
        return getView(model, request, PageEditMode.EDIT, backingBean);
    }

    @RequestMapping(params = "moveFieldDown")
    public String moveFieldDown(ModelMap model,
                                @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                                BindingResult bindingResult, HttpServletRequest request)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        backingBean.moveFieldUp(false);
        return getView(model, request, PageEditMode.EDIT, backingBean);
    }

    @RequestMapping(params = "saveFormat")
    public String saveFormat(FlashScope flashScope,
                             ModelMap model,
                             HttpServletRequest request,
                             @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                             BindingResult bindingResult) throws ServletRequestBindingException,
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
        return getView(model, request, mode, backingBean);
    }

    @RequestMapping(params = "deleteFormat")
    public String deleteFormat(ModelMap model,
                               HttpServletRequest request,
                               @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean)
            throws ServletException {
        archiveValuesExportFormatDao.delete(backingBean.getFormat().getFormatId());
        backingBean.setSelectedFieldId(0);
        return view(model, request, backingBean);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        EnumPropertyEditor<BuiltInAttribute> attributeEditor =
            new EnumPropertyEditor<BuiltInAttribute>(BuiltInAttribute.class);
        EnumPropertyEditor<FieldType> fieldTypeEditor =
            new EnumPropertyEditor<FieldType>(FieldType.class);
        EnumPropertyEditor<AttributeField> attributeFieldEditor =
            new EnumPropertyEditor<AttributeField>(AttributeField.class);

        binder.registerCustomEditor(Attribute.class, attributeEditor);
        binder.registerCustomEditor(FieldType.class, fieldTypeEditor);
        binder.registerCustomEditor(AttributeField.class, attributeFieldEditor);
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
                           ArchivedValuesExporterBackingBean backingBean)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        model.addAttribute("mode", mode);
        model.addAttribute("backingBean", backingBean);
        addDeviceCollectionAttribute(model, request, backingBean);
        if (mode.equals(PageEditMode.VIEW)) {
            backingBean.setAllFormats(archiveValuesExportFormatDao.getAllFormats());
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            backingBean.setEndDate(sdf.format(new Date()));
        }
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

    // I am still working on the report generation
    @RequestMapping(params = "generateReport")
    public String generateReport(ModelMap model,
                                 HttpServletResponse response,
                                 HttpServletRequest request,
                                 @ModelAttribute("backingBean") ArchivedValuesExporterBackingBean backingBean,
                                 BindingResult bindingResult)
            throws ParseException, IOException, ServletRequestBindingException,
            DeviceCollectionCreationException {
        String view = getView(model, request, PageEditMode.VIEW, backingBean);
        exportReportValidator.validate(backingBean, bindingResult);
        if (!bindingResult.hasErrors()) {
            DeviceCollection deviceCollection =
                deviceCollectionFactory.createDeviceCollection(request);
            List<SimpleDevice> deviceList = deviceCollection.getDeviceList();
            List<Meter> meters = meterDao.getMetersForYukonPaos(deviceList);
            ExportFormat format =
                archiveValuesExportFormatDao.getByFormatId(backingBean.getSelectedFormatId());
            ExportReportGenarator reportGenarator = new ExportReportGenarator(meters, format);
            List<String> report = reportGenarator.getReport();
            String fileName = getFileName(backingBean.getEndDate(), format.getFormatName());
            setupResponse(response, fileName);
            createReportFile(response, report);
            view = "";
        }
        return view;
    }

    private String getFileName(String endDate, String formatName) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date date = sdf.parse(endDate);
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