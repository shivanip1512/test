package com.cannontech.web.tools.dataExporter.validator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.amr.archivedValueExporter.model.ArchivedValuesExportFormatType;
import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.model.MissingAttribute;
import com.cannontech.amr.archivedValueExporter.model.PadSide;
import com.cannontech.amr.archivedValueExporter.model.ReadingPattern;
import com.cannontech.amr.archivedValueExporter.model.TimestampPattern;
import com.cannontech.clientutils.YukonLogManager;
import com.google.common.collect.ImmutableList;

public class ExportFormatTemplateValidator extends ExportFormatValidator {

    private final Logger log = YukonLogManager.getLogger(ExportFormatTemplateValidator.class);
    private final String invalidKey = "yukon.web.modules.tools.bulk.archivedValueExporter.parseTemplate.invalid";
    private final String requiredKey = "yukon.web.modules.tools.bulk.archivedValueExporter.parseTemplate.required";
    private final String notApplicableKey = "yukon.web.modules.tools.bulk.archivedValueExporter.parseTemplate.notApplicable";
    List<Field> fields = new ArrayList<Field>();
    private List<String> ignoredFields = new ArrayList<String>();
    private List<String> defaultedFieldNames = new ArrayList<String>();

    @Autowired ExportAttributeValidator exportAttributeValidator;
    @Autowired ExportFieldValidator exportFieldValidator;

    @PostConstruct
    public void init() {
        fields = FieldUtils.getAllFieldsList(ExportField.class);
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        builder.add("fieldId");
        builder.add("formatId");
        ignoredFields = builder.build();
        builder = ImmutableList.builder();
        builder.add("readingPattern");
        builder.add("timestampPattern");
        builder.add("padSide");
        builder.add("maxLength");
        defaultedFieldNames = builder.build();
    }

    @Override
    protected void doValidation(ExportFormat exportFormat, Errors errors) {
        super.doValidation(exportFormat, errors);
        boolean isDynamicAttribute = exportFormat.getFormatType() == ArchivedValuesExportFormatType.DYNAMIC_ATTRIBUTE;
        for (int i = 0; i < exportFormat.getAttributes().size(); i++) {
            errors.pushNestedPath("attributes[" + i + "]");
            exportAttributeValidator.doValidation(exportFormat.getAttributes().get(i), errors);
            errors.popNestedPath();
        }
        for (int i = 0; i < exportFormat.getFields().size(); i++) {
            errors.pushNestedPath("fields[" + i + "]");
            ExportField exportField = exportFormat.getFields().get(i);
            if (isDynamicAttribute && !FieldType.DYNAMIC_ATTRIBUTE_FIELD_TYPES.contains(exportField.getField().getType())) {
                errors.rejectValue("field.type", invalidKey, new Object[] { "dynamic attribute" }, "");
            } else if (!isDynamicAttribute && !FieldType.FIXED_ATTRIBUTE_FIELD_TYPES.contains(exportField.getField().getType())) {
                errors.rejectValue("field.type", invalidKey, new Object[] { "fixed attribute" }, "");
            }
            if (errors.hasFieldErrors("field.type")) {
                errors.popNestedPath();
                continue;
            }
            exportFieldValidator.doValidation(exportField, errors);
            if (exportField.getField().getType() == FieldType.ATTRIBUTE) {
                validateAttributeFields(exportField, exportFormat.getAttributes(), errors);
            }
            validateDependentFields(exportField, errors);
            errors.popNestedPath();
        }
    }

    /**
     * validate provided attribute fields for the Attribute type.
     */
    private void validateAttributeFields(ExportField exportField, List<ExportAttribute> attributes, Errors errors) {
        if (exportField.getField().getAttribute() == null) {
            errors.rejectValue("field.attribute", requiredKey, new Object[] { "attribute", FieldType.ATTRIBUTE }, "");
        } else {
            ExportAttribute exportAttribute = exportField.getField().getAttribute();
            boolean isValidField = false;
            for (ExportAttribute attribute : attributes) {
                if (attribute.equals(exportAttribute)) {
                    isValidField = true;
                    break;
                }
            }
            if (!isValidField) {
                errors.rejectValue("field.attribute", invalidKey, new Object[] { "attribute" }, "");
            }

        }
    }

    /**
     * validate dependent fields based on field type.
     */
    private void validateDependentFields(ExportField exportField, Errors errors) {
        switch (exportField.getField().getType()) {
        case ADDRESS:
        case DEVICE_NAME:
        case LATITUDE:
        case LONGITUDE:
        case METER_NUMBER:
        case ROUTE:
        case POINT_NAME:
        case POINT_STATE:
        case UNIT_OF_MEASURE:
        case POINT_QUALITY:
            checkIfFieldApplicable(exportField, errors, "field", "maxLength", "padSide", "padChar", "missingAttribute");
            break;
        case ATTRIBUTE:
            if (exportField.getAttributeField() == AttributeField.POINT_STATE
                    || exportField.getAttributeField() == AttributeField.UNIT_OF_MEASURE
                    || exportField.getAttributeField() == AttributeField.QUALITY) {
                checkIfFieldApplicable(exportField, errors, "field", "maxLength", "padSide", "padChar", "missingAttribute",
                        "attributeField");
            } else if (exportField.getAttributeField() == AttributeField.TIMESTAMP) {
                checkIfFieldApplicable(exportField, errors, "field", "maxLength", "padSide", "padChar", "missingAttribute",
                        "timestampPattern", "attributeField", "pattern");
            } else if (exportField.getAttributeField() == AttributeField.VALUE) {
                checkIfFieldApplicable(exportField, errors, "field", "maxLength", "padSide", "padChar", "missingAttribute",
                        "readingPattern", "roundingMode", "attributeField", "pattern");
            }
            break;
        case ATTRIBUTE_NAME:
            checkIfFieldApplicable(exportField, errors, "field", "maxLength", "padSide", "padChar", "missingAttribute",
                    "fieldValue");
            break;
        case DEVICE_TYPE:
            checkIfFieldApplicable(exportField, errors, "field", "maxLength", "padSide", "padChar");
            break;
        case PLAIN_TEXT:
            checkIfFieldApplicable(exportField, errors, "field", "pattern");
            break;
        case POINT_TIMESTAMP:
            checkIfFieldApplicable(exportField, errors, "field", "maxLength", "padSide", "padChar", "missingAttribute",
                    "timestampPattern", "pattern");
            break;
        case POINT_VALUE:
            checkIfFieldApplicable(exportField, errors, "field", "maxLength", "padSide", "padChar", "missingAttribute",
                    "readingPattern", "roundingMode", "pattern");
            break;
        case RUNTIME:
            checkIfFieldApplicable(exportField, errors, "field", "maxLength", "padSide", "padChar", "timestampPattern",
                    "pattern");
            break;
        }

    }

    /**
     * Check the fields applicable for field type. If filed is applicable and not provided in yaml file reject it and if field is
     * not applicable for the type and its provided in yaml, reject it.
     */
    private void checkIfFieldApplicable(ExportField exportField, Errors errors, String... applicableFields) {
        List<String> applicableFieldList = Arrays.asList(applicableFields);
        FieldType type = exportField.getField().getType();
        for (Field field : fields) {
            if (!ignoredFields.contains(field.getName())) {
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(exportField);
                    String fieldName = field.getName();
                    if (fieldName.equals("readingPattern")) {
                        if (exportField.getReadingPattern() == ReadingPattern.CUSTOM
                                && StringUtils.isEmpty(exportField.getPattern())) {
                            if (!errors.hasFieldErrors("pattern")) {
                                errors.rejectValue("pattern", requiredKey, new Object[] { "pattern" }, "");
                            }
                        }
                        continue;
                    }
                    if (fieldName.equals("padSide") || fieldName.equals("padChar")) {
                        if (exportField.getPadSide() != PadSide.NONE && exportField.getPadChar() == null) {
                            if (!errors.hasFieldErrors("padChar")) {
                                errors.rejectValue("padChar", requiredKey, new Object[] { "padChar" }, "");
                            }
                        }
                        continue;
                    }
                    if (fieldName.equals("missingAttribute") || fieldName.equals("missingAttributeValue")) {
                        if (exportField.getMissingAttribute() == MissingAttribute.FIXED_VALUE
                                && exportField.getMissingAttributeValue() == null) {
                            if (!errors.hasFieldErrors("missingAttributeValue")) {
                                errors.rejectValue("missingAttributeValue", requiredKey, new Object[] { "missingAttributeValue" },
                                        "");
                            }
                        }
                        continue;
                    }
                    if (applicableFieldList.contains(fieldName)) {
                        if (fieldValue == null || fieldValue.toString().isBlank()) {
                            errors.rejectValue(fieldName, requiredKey, new Object[] { fieldName, type }, "");
                        }
                    } else {
                        if (fieldValue != null && !fieldValue.toString().isBlank() && isNotDefaultValue(fieldValue, fieldName)) {
                            errors.rejectValue(fieldName, notApplicableKey, new Object[] { fieldName, type }, "");
                        }
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    log.error("Error occurred whihle validating fields.", e);
                }
            }
        }
    }

    /**
     * Method to check the default value. Few fields like ReadingPattern, TimestampPattern are initialized in pojo with default
     * values.This method check the field value and returns true if the true if the value is not default.
     * Used when field is not applicable for a type but provided with default value in yaml / auto populated by the parser.
     */
    private boolean isNotDefaultValue(Object fieldValue, String fieldName) {
        if (defaultedFieldNames.contains(fieldName)) {
            if (fieldValue instanceof ReadingPattern && (ReadingPattern) fieldValue == ReadingPattern.FIVE_ZERO) {
                return false;
            }
            if (fieldValue instanceof TimestampPattern && (TimestampPattern) fieldValue == TimestampPattern.MONTH_DAY_YEAR) {
                return false;
            }
            if (fieldValue instanceof PadSide && (PadSide) fieldValue == PadSide.NONE) {
                return false;
            }
            if (fieldValue instanceof Integer && Integer.valueOf(fieldValue.toString()) == 0) {
                return false;
            }
        }
        return true;
    }
}