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
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.validator.SimpleValidator;
import com.google.common.collect.ImmutableList;

public class ExportFormatTemplateValidator extends SimpleValidator<ExportFormat> {

    public ExportFormatTemplateValidator() {
        super(ExportFormat.class);
    }

    private static final Logger log = YukonLogManager.getLogger(ExportFormatTemplateValidator.class);
    private static final String invalidKey = "yukon.web.modules.tools.bulk.archivedValueExporter.parseTemplate.invalid";
    private static final String requiredKey = "yukon.web.modules.tools.bulk.archivedValueExporter.parseTemplate.required";
    private static final String notApplicableKey = "yukon.web.modules.tools.bulk.archivedValueExporter.parseTemplate.notApplicable";
    private static final String fieldId = "fieldId";
    private static final String field = "field";
    private static final String formatId = "formatId";
    private static final String attributeField = "attributeField";
    private static final String maxLength = "maxLength";
    private static final String padChar = "padChar";
    private static final String padSide = "padSide";
    private static final String roundingMode = "roundingMode";
    private static final String missingAttribute = "missingAttribute";
    private static final String missingAttributeValue = "missingAttributeValue";
    private static final String pattern = "pattern";
    private static final String attribute = "attribute";
    private static final String fieldAttribute = "field.attribute";

    private List<Field> fields = new ArrayList<Field>();
    private List<String> ignoredFields = new ArrayList<String>();
    private List<String> defaultedFieldNames = new ArrayList<String>();

    @Autowired private ExportAttributeValidator exportAttributeValidator;
    @Autowired private ExportFieldValidator exportFieldValidator;

    @PostConstruct
    public void init() {
        fields = FieldUtils.getAllFieldsList(ExportField.class);
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        builder.add(fieldId);
        builder.add(formatId);
        ignoredFields = builder.build();
        builder = ImmutableList.builder();
        builder.add(padSide);
        builder.add(maxLength);
        defaultedFieldNames = builder.build();
    }

    /**
     * Validate data export templates with help of existing validators and then validate the dependent fields. For detail
     * information about template fields, follow the Confluence page :
     * https://confluence-prod.tcc.etn.com/display/EEST/Data+Export+Format+-+Template
     */
    @Override
    protected void doValidation(ExportFormat exportFormat, Errors errors) {
        // Validate the ExportFormat fields except duplicate formatName. Duplicate format name should not be validated when user
        // create a data export from a template.
        ExportFormatValidatorHelper.validateExportFormatFields(exportFormat, errors);

        // Validate the ExportAttribute fields if present using ExportAttributeValidator class
        for (int i = 0; i < exportFormat.getAttributes().size(); i++) {
            errors.pushNestedPath("attributes[" + i + "]");
            exportAttributeValidator.doValidation(exportFormat.getAttributes().get(i), errors);
            errors.popNestedPath();
        }

        // Validate the ExportFields using ExportFieldValidator class
        boolean isDynamicAttribute = exportFormat.getFormatType() == ArchivedValuesExportFormatType.DYNAMIC_ATTRIBUTE;
        for (int i = 0; i < exportFormat.getFields().size(); i++) {
            errors.pushNestedPath("fields[" + i + "]");
            ExportField exportField = exportFormat.getFields().get(i);

            // 1'st validate whether the fields provided in YAML are applicable for Export Format Type.
            if (isDynamicAttribute && !FieldType.DYNAMIC_ATTRIBUTE_FIELD_TYPES.contains(exportField.getField().getType())) {
                errors.rejectValue("field.type", invalidKey, new Object[] { "dynamic attribute" }, "");
            } else if (!isDynamicAttribute && !FieldType.FIXED_ATTRIBUTE_FIELD_TYPES.contains(exportField.getField().getType())) {
                errors.rejectValue("field.type", invalidKey, new Object[] { "fixed attribute" }, "");
            }
            if (errors.hasFieldErrors("field.type")) {
                errors.popNestedPath();
                continue;
            }

            // Validate fields using ExportFieldValidator
            exportFieldValidator.doValidation(exportField, errors);

            // If field type is ATTRIBUTE, Validate the data wit respect to Attribute Setup section. The selected attribute can
            // only be used in field setup page. i.e if BLINK_COUNT is selected, BLINK_COUNT can be used in field setup.
            if (exportField.getField().getType() == FieldType.ATTRIBUTE) {
                validateAttributeFields(exportField, exportFormat.getAttributes(), errors);
                if (errors.hasFieldErrors(fieldAttribute) || errors.hasFieldErrors(attributeField)) {
                    errors.popNestedPath();
                    continue;
                }
            }

            // Validate the dependent fields.
            validateDependentFields(exportField, errors);

            errors.popNestedPath();
        }
    }

    /**
     * validate provided attribute fields for the Attribute type.
     */
    private void validateAttributeFields(ExportField exportField, List<ExportAttribute> attributes, Errors errors) {
        if (exportField.getField().getAttribute() == null) {
            errors.rejectValue(fieldAttribute, requiredKey, new Object[] { attribute, FieldType.ATTRIBUTE }, "");
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
                errors.rejectValue(fieldAttribute, invalidKey, new Object[] { attribute }, "");
            }
            // For Attribute Type, exportField.getAttributeField() should not be null.
            if (exportField.getAttributeField() == null) {
                errors.rejectValue(attributeField, requiredKey, new Object[] { attributeField, FieldType.ATTRIBUTE }, "");
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
            checkIfFieldApplicable(exportField, errors, field, maxLength, padSide, padChar, missingAttribute,
                    missingAttributeValue);
            break;
        case ATTRIBUTE:
            if (exportField.getAttributeField() == AttributeField.POINT_STATE
                    || exportField.getAttributeField() == AttributeField.UNIT_OF_MEASURE
                    || exportField.getAttributeField() == AttributeField.QUALITY) {
                checkIfFieldApplicable(exportField, errors, field, maxLength, padSide, padChar, missingAttribute,
                        missingAttributeValue, attributeField);
            } else if (exportField.getAttributeField() == AttributeField.TIMESTAMP) {
                checkIfFieldApplicable(exportField, errors, field, maxLength, padSide, padChar, missingAttribute,
                        missingAttributeValue, attributeField, pattern);
            } else if (exportField.getAttributeField() == AttributeField.VALUE) {
                checkIfFieldApplicable(exportField, errors, field, maxLength, padSide, padChar, missingAttribute,
                        missingAttributeValue, roundingMode, attributeField, pattern);
            }
            break;
        case ATTRIBUTE_NAME:
            checkIfFieldApplicable(exportField, errors, field, maxLength, padSide, padChar, missingAttribute,
                    missingAttributeValue, pattern);
            break;
        case DEVICE_TYPE:
            checkIfFieldApplicable(exportField, errors, field, maxLength, padSide, padChar);
            break;
        case PLAIN_TEXT:
            checkIfFieldApplicable(exportField, errors, field);
            break;
        case POINT_TIMESTAMP:
            checkIfFieldApplicable(exportField, errors, field, maxLength, padSide, padChar, missingAttribute,
                    missingAttributeValue, pattern);
            break;
        case POINT_VALUE:
            checkIfFieldApplicable(exportField, errors, field, maxLength, padSide, padChar, missingAttribute,
                    missingAttributeValue, roundingMode, pattern);
            break;
        case RUNTIME:
            checkIfFieldApplicable(exportField, errors, field, maxLength, padSide, padChar, pattern);
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
                    if ((applicableFieldList.contains(fieldName) && type.equals(FieldType.POINT_VALUE))
                            || (type.equals(FieldType.ATTRIBUTE) && exportField.getAttributeField() == AttributeField.VALUE)) {
                        if (exportField.isCustomPattern()
                                && StringUtils.isEmpty(exportField.getPattern())) {
                            if (!errors.hasFieldErrors(pattern)) {
                                errors.rejectValue(pattern, requiredKey, new Object[] { pattern, type }, "");
                            }
                        }
                        continue;
                    }

                    if (applicableFieldList.contains(fieldName) && fieldName.equals(padChar)) {
                        // If padSide is not NONE, padChar is required else it is not applicable.
                        if (exportField.getPadSide() != PadSide.NONE) {
                            if (exportField.getPadChar() == null && !errors.hasFieldErrors(padChar)) {
                                errors.rejectValue(padChar, requiredKey, new Object[] { padChar, type }, "");
                            }
                        } else if (exportField.getPadChar() != null) {
                            errors.rejectValue(padChar, notApplicableKey, new Object[] { padChar, type }, "");
                        }
                        continue;
                    }
                    if (applicableFieldList.contains(fieldName) && fieldName.equals(missingAttributeValue)) {
                        // If missingAttribute is FIXED_VALUE, missingAttributeValue is required else it is not applicable.
                        if (exportField.getMissingAttribute() == MissingAttribute.FIXED_VALUE) {
                            if (exportField.getMissingAttributeValue() == null && !errors.hasFieldErrors(missingAttributeValue)) {
                                errors.rejectValue(missingAttributeValue, requiredKey,
                                        new Object[] { missingAttributeValue, type }, "");
                            }
                        } else if (exportField.getMissingAttributeValue() != null) {
                            errors.rejectValue(missingAttributeValue, notApplicableKey,
                                    new Object[] { missingAttributeValue, type }, "");
                        }
                        continue;
                    }
                    if (applicableFieldList.contains(fieldName)) {
                        if ((fieldValue == null || fieldValue.toString().isBlank()) && !errors.hasFieldErrors(pattern)) {
                            errors.rejectValue(fieldName, requiredKey, new Object[] { fieldName, type }, "");
                        }
                    } else {
                        if (fieldValue != null && !fieldValue.toString().isBlank() && isNotDefaultValue(fieldValue, fieldName)
                                && isNotAutoPopulatedField(fieldName, exportField)) {
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
     * As of now pattern field is empty for PLAIN_TEXT(i.e if kept empty).
     * This method to check whether
     * pattern is auto populated or not.
     */
    private boolean isNotAutoPopulatedField(String fieldName, ExportField exportField) {
        if (fieldName.equals(pattern)) {
            return !(exportField.getField().getType() == FieldType.PLAIN_TEXT);
        }
        return true;
    }

    /**
     * Method to check the default value. This method check the field value and returns true if the true if the value is not default.
     * Used when field is not applicable for a type but provided with default value in yaml / auto populated by the parser.
     */
    private boolean isNotDefaultValue(Object fieldValue, String fieldName) {
        if (defaultedFieldNames.contains(fieldName)) {
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