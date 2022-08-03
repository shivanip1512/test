package com.cannontech.web.tools.dataExporter;

import java.beans.PropertyEditorSupport;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFormatDao;
import com.cannontech.amr.archivedValueExporter.model.ArchivedValuesExportFormatType;
import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.amr.archivedValueExporter.model.AttributeList;
import com.cannontech.amr.archivedValueExporter.model.DataExportDelimiter;
import com.cannontech.amr.archivedValueExporter.model.DataSelection;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.Field;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.model.FieldValue;
import com.cannontech.amr.archivedValueExporter.model.MissingAttribute;
import com.cannontech.amr.archivedValueExporter.model.PadSide;
import com.cannontech.amr.archivedValueExporter.model.Preview;
import com.cannontech.amr.archivedValueExporter.model.ReadingPattern;
import com.cannontech.amr.archivedValueExporter.model.TimestampPattern;
import com.cannontech.amr.archivedValueExporter.model.YukonRoundingMode;
import com.cannontech.amr.archivedValueExporter.service.ExportReportGeneratorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.ToolsEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.TimeZoneFormat;
import com.cannontech.common.util.YamlParserUtils;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.WebMessageSourceResolvable;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeListType;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.input.type.AttributeType;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.tools.dataExporter.model.ArchivedValuesExporter;
import com.cannontech.web.tools.dataExporter.validator.ExportAttributeValidator;
import com.cannontech.web.tools.dataExporter.validator.ExportFieldValidator;
import com.cannontech.web.tools.dataExporter.validator.ExportFormatTemplateValidator;
import com.cannontech.web.tools.dataExporter.validator.ExportFormatValidator;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty(YukonRoleProperty.ARCHIVED_DATA_EXPORT)
public class DataExporterFormatController {
    
    public final static String BASE_KEY = "yukon.web.modules.tools.bulk.archivedValueExporter.";
    private final static Logger log = YukonLogManager.getLogger(DataExporterFormatController.class);

    @Autowired private ArchiveValuesExportFormatDao archiveValuesExportFormatDao;
    @Autowired private ExportAttributeValidator exportAttributeValidator;
    @Autowired private ExportFieldValidator exportFieldValidator;
    @Autowired private ExportFormatValidator exportFormatValidator;
    @Autowired private ExportReportGeneratorService exportReportGeneratorService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ScheduledFileExportService scheduledFileExportService;
    @Autowired private ToolsEventLogService toolsEventLogService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private AttributeService attributeService;
    @Autowired private AttributeType attributeTypeEditor;
    @Autowired private ExportFormatTemplateValidator exportFormatTemplateValidator;

    @GetMapping(value = "/data-exporter/format/{id}")
    public String view(ModelMap model, YukonUserContext userContext, @PathVariable int id) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        
        ExportFormat format = archiveValuesExportFormatDao.getByFormatId(id);
        
        model.addAttribute("format", format);
        model.addAttribute("formatName", format.getFormatName());
        
        setupModel(model, userContext, format);
        
        return "data-exporter/format/format.jsp";
    }
    
    @GetMapping(value = "/data-exporter/format/renderTemplatePreview/{fileName}")
    public String renderTemplatePreview(ModelMap model, YukonUserContext userContext, FlashScope flashScope,
            @PathVariable String fileName, @ModelAttribute ExportFormat exportFormat, BindingResult result) {
        model.addAttribute("mode", PageEditMode.VIEW);
        try {
            exportFormat = parseAndValidateTemplate(fileName, flashScope, result, userContext);
        } catch (IOException e) {
            log.error("Error occurred while parsing the template file", e);
            flashScope.setError(new YukonMessageSourceResolvable(BASE_KEY + "parseTemplate.error"));
            exportFormat = setExportFormatForErrorScenario();
        }
        Preview preview = exportReportGeneratorService.generatePreview(exportFormat, userContext);
        model.addAttribute("preview", preview);
        model.addAttribute("showAttributeSection",
                exportFormat.getFormatType() == ArchivedValuesExportFormatType.FIXED_ATTRIBUTE);
        model.addAttribute("format", exportFormat);
        return "data-exporter/format/format.jsp";
    }

    @RequestMapping(value = "/data-exporter/format/{id}/copy", method = RequestMethod.GET)
    public String copy(ModelMap model, YukonUserContext userContext, @PathVariable int id) {

        String name = "";
        ExportFormat format = archiveValuesExportFormatDao.getByFormatId(id);
        name = format.getFormatName();
        format.setFormatId(0);
        format.setFormatName("");

        model.addAttribute("format", format);
        model.addAttribute("mode", PageEditMode.CREATE);
        
        setupModel(model, userContext, format);
        toolsEventLogService.dataExportFormatCopyAttempted(userContext.getYukonUser(), name);
        
        return "data-exporter/format/format.jsp";
    }

    @GetMapping(value = "/data-exporter/format/create")
    public String create(ModelMap model, YukonUserContext userContext, FlashScope flashScope,
            @RequestParam(required = false, name = "formatType", defaultValue = "FIXED_ATTRIBUTE") ArchivedValuesExportFormatType formatType,
            @RequestParam("useTemplate") boolean useTemplate, @RequestParam("fileName") String fileName,
            @ModelAttribute ExportFormat exportFormat, BindingResult result) {

        model.addAttribute("mode", PageEditMode.CREATE);
        if (useTemplate) {
            try {
                exportFormat = parseAndValidateTemplate(fileName, flashScope, result, userContext);
            } catch (Exception e) {
                log.error("Error occurred while parsing the template file", e);
                flashScope.setError(new YukonMessageSourceResolvable(BASE_KEY + "parseTemplate.error"));
                exportFormat = setExportFormatForErrorScenario();
                model.addAttribute("showAttributeSection",
                        exportFormat.getFormatType() == ArchivedValuesExportFormatType.FIXED_ATTRIBUTE);
            }
        } else {
            exportFormat = new ExportFormat();
            exportFormat.setFormatType(formatType);
        }
        model.addAttribute("format", exportFormat);
        setupModel(model, userContext, exportFormat);
        return "data-exporter/format/format.jsp";
    }

    private ExportFormat setExportFormatForErrorScenario() {
        ExportFormat format = new ExportFormat();
        format.setFormatType(ArchivedValuesExportFormatType.FIXED_ATTRIBUTE);
        format.setDelimiter(null);
        format.setDateTimeZoneFormat(null);
        return format;
    }

    @RequestMapping(value = "/data-exporter/format", method = RequestMethod.POST)
    public String save(FlashScope flashScope, 
            ModelMap model, 
            YukonUserContext userContext,
            @ModelAttribute("format") ExportFormat format, 
            BindingResult result) {

        exportFormatValidator.validate(format, result);
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flashScope.setError(messages);
            
            model.addAttribute("mode", format.getFormatId() == 0 ? PageEditMode.CREATE : PageEditMode.EDIT);
            setupModel(model, userContext, format);
            
            return "data-exporter/format/format.jsp";
        }
        
        if (format.getFormatId() == 0) {
            archiveValuesExportFormatDao.create(format);
            toolsEventLogService.dataExportFormatCreated(userContext.getYukonUser(), format.getFormatName());
            flashScope.setConfirm(new YukonMessageSourceResolvable(BASE_KEY + "createdFormat", format.getFormatName()));
        } else {
            archiveValuesExportFormatDao.update(format);
            toolsEventLogService.dataExportFormatUpdated(userContext.getYukonUser(), format.getFormatName());
            flashScope.setConfirm(new YukonMessageSourceResolvable(BASE_KEY + "updatedFormat", format.getFormatName()));
        }

        ArchivedValuesExporter archivedValuesExporter = new ArchivedValuesExporter();
        archivedValuesExporter.setFormatId(format.getFormatId());
        
        return "redirect:view";
    }
    
    @RequestMapping(value = "/data-exporter/format/{id}", method = RequestMethod.DELETE)
    public String delete(FlashScope flashScope, @PathVariable int id, YukonUserContext userContext) {
        
        String name = archiveValuesExportFormatDao.getName(id);
        archiveValuesExportFormatDao.delete(id);
        //delete any jobs using this format
        scheduledFileExportService.deleteAdeJobsByFormatId(id);
        toolsEventLogService.dataExportFormatDeleted(userContext.getYukonUser(), name);
        flashScope.setConfirm(new YukonMessageSourceResolvable(BASE_KEY + "deletedFormat", name));
        
        return "redirect:/tools/data-exporter/view";
    }
    
    @RequestMapping(value = "/data-exporter/format/attribute", method = RequestMethod.GET)
    public String attributePopup(ModelMap model, YukonUserContext userContext) {
        
        model.addAttribute("attribute", new ExportAttribute());
        
        Map<AttributeGroup, List<Attribute>> groupedAttributes = attributeService.getAllGroupedAttributes(userContext);
        model.addAttribute("groupedAttributes", groupedAttributes);
        model.addAttribute("dataSelection", DataSelection.values());
        
        return "data-exporter/format/attribute.jsp";
    }
    
    @RequestMapping(value = "/data-exporter/format/attribute", method = RequestMethod.POST)
    public String attribute(ModelMap model, 
            YukonUserContext userContext,
            HttpServletResponse resp,
            @ModelAttribute("attribute") ExportAttribute attribute,
            BindingResult result) throws JsonGenerationException, JsonMappingException, IOException {
        
        exportAttributeValidator.validate(attribute, result);
        
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            
            Map<AttributeGroup, List<Attribute>> groupedAttributes = attributeService.getAllGroupedAttributes(userContext);
            model.addAttribute("groupedAttributes", groupedAttributes);
            model.addAttribute("dataSelection", DataSelection.values());
            
            return "data-exporter/format/attribute.jsp";
        }
        
        // success
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        model.clear();
        
        Map<String, Object> json = new HashMap<>();
        json.put("attribute", attribute);

        Map<String, Object> text = new HashMap<>();
        text.put("attribute", accessor.getMessage(attribute.getAttribute().getMessage()));
        text.put("dataSelection", accessor.getMessage(attribute.getDataSelection()));
        text.put("daysPrevious", attribute.getDaysPrevious());
        
        json.put("text", text);
        
        resp.setContentType("application/json");
        JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
        
        return null;
    }
    
    @RequestMapping(value = "/data-exporter/format/field", method = RequestMethod.GET)
    public String fieldPopup(ModelMap model, 
            YukonUserContext userContext, 
            @ModelAttribute AttributeList attributeList, 
            ArchivedValuesExportFormatType formatType,
            String exportFieldJson) throws IOException {

        if (StringUtils.isEmpty(exportFieldJson)) {
            // add field popup
            ExportField exportField = new ExportField();
            RoundingMode roundingMode = globalSettingDao
                .getEnum(GlobalSettingType.DEFAULT_ROUNDING_MODE, YukonRoundingMode.class)
                .getRoundingMode();
            exportField.setRoundingMode(YukonRoundingMode.valueOf(roundingMode.name()));
            model.addAttribute("exportField", exportField);
        } else {
            // edit field popup
            ExportField exportField = JsonUtils.fromJson(exportFieldJson, ExportField.class);
            model.addAttribute("exportField", exportField);
            model.addAttribute("customSelected", exportField.isCustomPattern());
        }
        
        model.addAttribute("fields", getFields(formatType, attributeList.getAttributes()));
        model.addAttribute("padSides", PadSide.values());
        model.addAttribute("fieldValues", FieldValue.values());
        model.addAttribute("attributeFields", AttributeField.values());
        model.addAttribute("missingAttributes", MissingAttribute.values());
        model.addAttribute("roundingModes", YukonRoundingMode.values());
        model.addAttribute("readingPatterns", ReadingPattern.values());
        model.addAttribute("timestampPatterns", TimestampPattern.values());
        
        return "data-exporter/format/field.jsp";
    }
    
    @RequestMapping(value = "/data-exporter/format/field", method = RequestMethod.POST)
    public String field(ModelMap model, 
            YukonUserContext userContext,
            HttpServletResponse resp,
            ArchivedValuesExportFormatType formatType,
            @ModelAttribute AttributeList attributeList,
            @ModelAttribute("exportField") ExportField exportField,
            BindingResult result) throws JsonGenerationException, JsonMappingException, IOException {
        
        exportFieldValidator.validate(exportField, result);
        
        boolean failed = result.hasErrors();
        
        if (failed) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            
            model.addAttribute("fields", getFields(formatType, attributeList.getAttributes()));
            model.addAttribute("fieldValues", FieldValue.values());
            model.addAttribute("padSides", PadSide.values());
            model.addAttribute("attributeFields", AttributeField.values());
            model.addAttribute("missingAttributes", MissingAttribute.values());
            model.addAttribute("roundingModes", YukonRoundingMode.values());
            model.addAttribute("readingPatterns", ReadingPattern.values());
            model.addAttribute("timestampPatterns", TimestampPattern.values());
            
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            FieldError patternError = result.getFieldError("pattern");
            if (patternError != null && exportField.isTimestamp()) {
                model.addAttribute("timestampPatternError", accessor.getMessage(patternError));
            }
            if (patternError != null && exportField.isValue()) {
                model.addAttribute("readingPatternError", accessor.getMessage(patternError));
            }
            
            return "data-exporter/format/field.jsp";
        }
        
        // success
        FieldType type = exportField.getField().getType();
        boolean isPlainText = type == FieldType.PLAIN_TEXT;
        boolean isAttribute = exportField.getField().getAttribute() != null;
        boolean isTimestamp = exportField.isTimestamp();
        boolean isValue = exportField.isValue();

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        model.clear();

        Map<String, Object> json = new HashMap<>();
        Map<String, String> text = new HashMap<>();
        text.put("exportField", accessor.getMessage(exportField.getMessage()));
        text.put("attributeField", isAttribute ? accessor.getMessage(exportField.getAttributeField()) : "");
        text.put("dataSelection", isAttribute ? accessor.getMessage(exportField.getField().getAttribute().getDataSelection()) : "");
        text.put("daysPrevious", isAttribute ? exportField.getField().getAttribute().getDaysPrevious().toString() : "");
        
        if (isPlainText) {
            text.put("missingAttribute", "");
        } else {
            String missingText = accessor.getMessage(exportField.getMissingAttribute());
            if (exportField.getMissingAttribute() == MissingAttribute.FIXED_VALUE) {
                missingText += " " + exportField.getMissingAttributeValue();
            }
            text.put("missingAttribute", missingText);
        }
        
        text.put("roundingMode", isValue ? accessor.getMessage(exportField.getRoundingMode()) : "");
        
        if (isPlainText || isTimestamp || isValue) {
            text.put("pattern", exportField.getPattern());
        } else if (exportField.getField().isAttributeName()) {
            text.put("pattern", exportField.getPattern().toString());
        } else {
            text.put("pattern", "");
        }
        
        text.put("maxLength", exportField.getMaxLength() == 0 ? accessor.getMessage(BASE_KEY + "noMax") : exportField.getMaxLength().toString());
        if (!isPlainText) {
            String padding = accessor.getMessage(exportField.getPadSide());
            if (exportField.getPadSide() != PadSide.NONE) {
                padding += " " + exportField.getPadChar();
            }
            text.put("padding", padding);
        } else {
            text.put("padding", "");
        }
        json.put("exportField", exportField);
        json.put("text", text);

        resp.setContentType("application/json");
        JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
        
        return null;
    }
    
    @RequestMapping(value = "/data-exporter/format/preview", method = RequestMethod.POST)
    public @ResponseBody Preview preview(YukonUserContext userContext, @ModelAttribute("format") ExportFormat format) {
        
        Preview preview = exportReportGeneratorService.generatePreview(format, userContext);
        
        return preview;
    }
    
    public List<Field> getFields(ArchivedValuesExportFormatType formatType, List<ExportAttribute> attributes) {
        
        List<Field> fieldSelect = Lists.newArrayList();
        
        List<FieldType> fieldTypes = null;
        if (formatType == ArchivedValuesExportFormatType.FIXED_ATTRIBUTE) {
            fieldTypes = Lists.newArrayList(FieldType.FIXED_ATTRIBUTE_FIELD_TYPES);
        } else if (formatType == ArchivedValuesExportFormatType.DYNAMIC_ATTRIBUTE) {
            fieldTypes = Lists.newArrayList(FieldType.DYNAMIC_ATTRIBUTE_FIELD_TYPES);
        }
        
        for (FieldType type : fieldTypes) {
            if (type != FieldType.ATTRIBUTE) {
                fieldSelect.add(new Field(type, null));
            }
        }
        
        if (!attributes.isEmpty()) {
            for (ExportAttribute attribute : attributes) {
                Field field = new Field(FieldType.ATTRIBUTE, attribute);
                fieldSelect.add(field);
            }
        }
        
        return fieldSelect;
    }
    
    /**
     * This method sets up the model for all the base information for the create and edit methods.
     * @return Returns the javascript configuration map, incase more needs to be added to it.
     */
    private Map<String, Object> setupModel(ModelMap model, YukonUserContext userContext, ExportFormat format) {
        
        model.addAttribute("showAttributeSection", format.getFormatType() == ArchivedValuesExportFormatType.FIXED_ATTRIBUTE);
        model.addAttribute("delimiters", DataExportDelimiter.values());
        model.addAttribute("attributes", BuiltInAttribute.values());
        model.addAttribute("dateTimeZoneFormats", TimeZoneFormat.values());
        
        Preview preview = exportReportGeneratorService.generatePreview(format, userContext);
        model.addAttribute("preview", preview);
        
        /* json configuration object to pass to the js module */
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> config = new HashMap<>();
        Map<String, String> text = new HashMap<>();
        text.put("editAttribute", accessor.getMessage("yukon.web.modules.tools.bulk.archivedValueExporter.editAttribute.title"));
        text.put("addAttribute", accessor.getMessage("yukon.web.modules.tools.bulk.archivedValueExporter.addAttribute.title"));
        text.put("editField", accessor.getMessage("yukon.web.modules.tools.bulk.archivedValueExporter.editField.title"));
        text.put("addField", accessor.getMessage("yukon.web.modules.tools.bulk.archivedValueExporter.addField.title"));
        config.put("text", text);
        model.put("jsConfig", config);
        
        return config;
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        
        if (binder.getTarget() != null) {
            binder.setMessageCodesResolver(new YukonMessageCodeResolver(BASE_KEY));
        }

        binder.registerCustomEditor(AttributeField.class, new EnumPropertyEditor<>(AttributeField.class));
        binder.registerCustomEditor(FieldType.class, new EnumPropertyEditor<>(FieldType.class));
        binder.registerCustomEditor(MissingAttribute.class, new EnumPropertyEditor<>(MissingAttribute.class));
        binder.registerCustomEditor(PadSide.class, new EnumPropertyEditor<>(PadSide.class));
        binder.registerCustomEditor(YukonRoundingMode.class, new EnumPropertyEditor<>(YukonRoundingMode.class));
        binder.registerCustomEditor(Attribute.class, attributeTypeEditor.getPropertyEditor());

        binder.registerCustomEditor(Field.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String fieldString) throws IllegalArgumentException {
                if (fieldString == null || fieldString.length() < 1) {
                    setValue(null);
                    return;
                }
                Field field;
                try {
                    field = JsonUtils.fromJson(fieldString, Field.class);
                    setValue(field);
                } catch (IOException e) {
                    log.error("Unable to convert JSON to Field", e);
                    setValue(null);
                }
            }
            @Override
            public String getAsText() {
                Field field = (Field) getValue();
                if (field == null) {
                    return null;
                }
                try {
                    return JsonUtils.toJson(field);
                } catch (JsonProcessingException e) {
                    log.error("Unable to convert Field to JSON", e);
                    return "";
                }
            }
        });
        binder.registerCustomEditor(ExportField.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String exportFieldString) throws IllegalArgumentException {
                if (exportFieldString == null || exportFieldString.length() < 1) {
                    setValue(null);
                    return;
                }
                ExportField exportField;
                try {
                    exportField = JsonUtils.fromJson(exportFieldString, ExportField.class);
                    setValue(exportField);
                } catch (IOException e) {
                    log.error("Unable to convert JSON to ExportField", e);
                    setValue(null);
                }
            }
            @Override
            public String getAsText() {
                ExportField exportField = (ExportField) getValue();
                if (exportField == null) {
                    return null;
                }
                try {
                    return JsonUtils.toJson(exportField);
                } catch (JsonProcessingException e) {
                    log.error("Unable to convert ExportField to JSON", e);
                    return "";
                }
            }
        });
        binder.registerCustomEditor(ExportAttribute.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String exportAttributeString) throws IllegalArgumentException {
                if (exportAttributeString == null || exportAttributeString.length() < 1) {
                    setValue(null);
                    return;
                }
                ExportAttribute exportAttribute;
                try {
                    exportAttribute = JsonUtils.fromJson(exportAttributeString, ExportAttribute.class);
                    setValue(exportAttribute);
                } catch (IOException e) {
                    log.error("Unable to convert JSON to ExportAttribute", e);
                    setValue(null);
                }
            }
            @Override
            public String getAsText() {
                ExportAttribute exportAttribute = (ExportAttribute) getValue();
                if (exportAttribute == null) {
                    return null;
                }
                try {
                    return JsonUtils.toJson(exportAttribute);
                } catch (JsonProcessingException e) {
                    log.error("Unable to convert ExportAttribute to JSON", e);
                    return "";
                }
            }
        });
    }

    /**
     * Returns ExportFormat, after parsing and then validating fields for the specified template file name.
     */
    private ExportFormat parseAndValidateTemplate(String fileName, FlashScope flashScope, BindingResult result,
            YukonUserContext userContext) throws FileNotFoundException, IOException {
        String sep = System.getProperty("file.separator");
        FileInputStream inputStream = new FileInputStream(
                StringUtils.joinWith(sep, CtiUtilities.getDataExportTemplatesDirPath(), fileName));
        ExportFormat exportFormat = YamlParserUtils.parseToObject(inputStream, ExportFormat.class, fileName);
        exportFormatTemplateValidator.validate(exportFormat, result);

        if (result.hasErrors()) {
            List<MessageSourceResolvable> validationErrors = retreiveValidationErrors(result, userContext);
            flashScope.setError(validationErrors, FlashScopeListType.NONE);
            exportFormat = setExportFormatForErrorScenario();
        }
        return exportFormat;
    }

    /**
     * Return validation errors and global errors for UI.
     */
    private List<MessageSourceResolvable> retreiveValidationErrors(BindingResult result, YukonUserContext userContext) {
        List<MessageSourceResolvable> validationErrors = new ArrayList<MessageSourceResolvable>();
        validationErrors.add(new WebMessageSourceResolvable(BASE_KEY + "parseTemplate.validationFailed"));
        if (result.hasFieldErrors()) {
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError error : errors) {
                validationErrors.add(new WebMessageSourceResolvable(error.getCode(), error.getArguments()));
                validationErrors.add(new WebMessageSourceResolvable(BASE_KEY + "parseTemplate.lineBreaker"));
            }
        }
        if (result.hasGlobalErrors()) {
            List<ObjectError> errors = result.getGlobalErrors();
            for (ObjectError error : errors) {
                validationErrors.add(new WebMessageSourceResolvable(error.getCode(), error.getArguments()));
                validationErrors.add(new WebMessageSourceResolvable(BASE_KEY + "parseTemplate.lineBreaker"));
            }
        }
        return validationErrors;
    }
}