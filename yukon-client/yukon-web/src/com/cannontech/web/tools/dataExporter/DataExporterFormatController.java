package com.cannontech.web.tools.dataExporter;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.math.RoundingMode;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.TimeZoneFormat;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.tools.dataExporter.model.ArchivedValuesExporter;
import com.cannontech.web.tools.dataExporter.validator.ExportAttributeValidator;
import com.cannontech.web.tools.dataExporter.validator.ExportFieldValidator;
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
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private ScheduledFileExportService scheduledFileExportService;
    @Autowired private ToolsEventLogService toolsEventLogService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @RequestMapping(value = "/data-exporter/format/{id}", method = RequestMethod.GET)
    public String view(ModelMap model, YukonUserContext userContext, @PathVariable int id) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        
        ExportFormat format = archiveValuesExportFormatDao.getByFormatId(id);
        
        model.addAttribute("format", format);
        model.addAttribute("formatName", format.getFormatName());
        
        setupModel(model, userContext, format);
        
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
    
    @RequestMapping(value = "/data-exporter/format/create", method = RequestMethod.GET)
    public String create(ModelMap model, YukonUserContext userContext, ArchivedValuesExportFormatType formatType) {
        
        model.addAttribute("mode", PageEditMode.CREATE);
        
        ExportFormat format = new ExportFormat();
        format.setFormatType(formatType);
        model.addAttribute("format", format);
        
        setupModel(model, userContext, format);
        
        return "data-exporter/format/format.jsp";
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
        
        Map<AttributeGroup, List<BuiltInAttribute>> groupedAttributes = 
                objectFormattingService.sortDisplayableValues(BuiltInAttribute.getAllGroupedAttributes(), userContext);
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
            
            Map<AttributeGroup, List<BuiltInAttribute>> groupedAttributes = 
                    objectFormattingService.sortDisplayableValues(BuiltInAttribute.getAllGroupedAttributes(), userContext);
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
            RoundingMode roundingMode = globalSettingDao.getEnum(GlobalSettingType.DEFAULT_ROUNDING_MODE, YukonRoundingMode.class).getRoundingMode();
            exportField.setRoundingMode(YukonRoundingMode.valueOf(roundingMode.name()));
            model.addAttribute("exportField", exportField);
        } else {
            // edit field popup
            ExportField exportField = JsonUtils.fromJson(exportFieldJson, ExportField.class);
            model.addAttribute("exportField", exportField);
        }
        
        model.addAttribute("fields", getFields(formatType, attributeList.getAttributes()));
        model.addAttribute("padSides", PadSide.values());
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
            model.addAttribute("padSides", PadSide.values());
            model.addAttribute("attributeFields", AttributeField.values());
            model.addAttribute("missingAttributes", MissingAttribute.values());
            model.addAttribute("roundingModes", YukonRoundingMode.values());
            model.addAttribute("readingPatterns", ReadingPattern.values());
            model.addAttribute("timestampPatterns", TimestampPattern.values());
            
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            FieldError patternError = result.getFieldError("timestampPattern");
            if (patternError != null) {
                model.addAttribute("timestampPatternError", accessor.getMessage(patternError));
            }
            patternError = result.getFieldError("readingPattern");
            if (patternError != null) {
                model.addAttribute("readingPatternError", accessor.getMessage(patternError));
            }
            
            return "data-exporter/format/field.jsp";
        }
        
        // success
        FieldType type = exportField.getField().getType();
        boolean isPlainText = type == FieldType.PLAIN_TEXT;
        boolean isAttribute = exportField.getField().getAttribute() != null;
        boolean isTimestamp = exportField.isTimestamp();;
        boolean isValue = exportField.isValue();
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        model.clear();
        
        Map<String, Object> json = new HashMap<>();
        json.put("exportField", exportField);
        
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
        
        text.put("roundingMode", isAttribute && isValue ? accessor.getMessage(exportField.getRoundingMode()) : "");
        
        if (isPlainText || isTimestamp || isValue) {
            String pattern = exportField.getPattern();
            if (isValue) {
                if (!exportField.getReadingPattern().isCustom()) {
                    pattern = accessor.getMessage(exportField.getReadingPattern());
                } else { // custom format, default if empty
                    if (pattern.isEmpty()) {
                        pattern = "#####.00";
                    }
                }
            } else if (isTimestamp) {
                if (!exportField.getTimestampPattern().isCustom()) {
                    pattern = accessor.getMessage(exportField.getTimestampPattern());
                } else { // custom format, default if empty
                    if (pattern.isEmpty()) {
                        pattern = "MM/dd/yyyy hh:mm:ss zZ";
                    }
                }
            } 
            text.put("pattern", pattern);
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

        binder.registerCustomEditor(Attribute.class, new EnumPropertyEditor<>(BuiltInAttribute.class));
        binder.registerCustomEditor(AttributeField.class, new EnumPropertyEditor<>(AttributeField.class));
        binder.registerCustomEditor(FieldType.class, new EnumPropertyEditor<>(FieldType.class));
        binder.registerCustomEditor(MissingAttribute.class, new EnumPropertyEditor<>(MissingAttribute.class));
        binder.registerCustomEditor(PadSide.class, new EnumPropertyEditor<>(PadSide.class));
        binder.registerCustomEditor(YukonRoundingMode.class, new EnumPropertyEditor<>(YukonRoundingMode.class));
        
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

}