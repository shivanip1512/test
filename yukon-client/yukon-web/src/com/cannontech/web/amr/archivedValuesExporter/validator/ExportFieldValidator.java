package com.cannontech.web.amr.archivedValuesExporter.validator;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;



import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.amr.archivedValuesExporter.ArchivedValuesExporterBackingBean;


public class ExportFieldValidator extends SimpleValidator<ArchivedValuesExporterBackingBean>{
    private static final String textInputRequired = "yukon.web.modules.amr.archivedValueExporter.formatError.textInputRequired";
    private static final String invalidPattern = "yukon.web.modules.amr.archivedValueExporter.formatError.invalidPattern";
    public ExportFieldValidator() {
        super(ArchivedValuesExporterBackingBean.class);
    }

    @Override
    protected void doValidation(ArchivedValuesExporterBackingBean target, Errors errors) {
        ArchivedValuesExporterBackingBean archivedValuesExporterBackingBean =
            (ArchivedValuesExporterBackingBean) target;
        archivedValuesExporterBackingBean.addFieldTypeToExportField();
        if (archivedValuesExporterBackingBean.getExportField().getFieldType().equals(FieldType.PLAIN_TEXT)) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors,
                                                           "exportField.pattern",
                                                           textInputRequired);
        }
        if (StringUtils.isBlank(target.getExportField().getMissingAttributeValue())) {
            target.getExportField().setMissingAttributeValue("Leave Blank");
        }
        if (archivedValuesExporterBackingBean.getExportField().getFieldType()
            .equals(FieldType.ATTRIBUTE)) {
            if (archivedValuesExporterBackingBean.getExportField().getAttributeField()
                .equals(AttributeField.TIMESTAMP)) {
                if (StringUtils.isBlank(target.getExportField().getPattern())) {
                    target.getExportField().setPattern("dd/MM/yyyy");
                } else {
                    try {
                        new SimpleDateFormat(target.getExportField().getPattern());
                    } catch (IllegalArgumentException e) {
                        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors,
                                                                       "exportField.pattern",
                                                                       invalidPattern);
                    }
                }
            } else if (archivedValuesExporterBackingBean.getExportField().getAttributeField()
                .equals(AttributeField.TIMESTAMP)) {
                try {
                    new DecimalFormat(target.getExportField().getPattern());
                } catch (IllegalArgumentException e) {
                    YukonValidationUtils.rejectIfEmptyOrWhitespace(errors,
                                                                   "exportField.pattern",
                                                                   invalidPattern);
                }
            }
        }
    }
}

