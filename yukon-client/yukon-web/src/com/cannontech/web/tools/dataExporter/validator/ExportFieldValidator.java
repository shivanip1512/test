package com.cannontech.web.tools.dataExporter.validator;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.springframework.validation.Errors;

import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.tools.dataExporter.DataExporterFormatController;

public class ExportFieldValidator extends SimpleValidator<ExportField> {

    private static final String invalidPatternMsgKey = DataExporterFormatController.BASE_KEY + "formatError.invalidPattern";
    
    public ExportFieldValidator() {
        super(ExportField.class);
    }

    @Override
    protected void doValidation(ExportField field, Errors errors) {
        
        if (field.getField().getType() == FieldType.PLAIN_TEXT ) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "plainText", field.getPattern(), 50);
        } else if (field.getMaxLength() == null || field.getMaxLength() < 0) {
            errors.rejectValue("exportField.maxLength", DataExporterFormatController.BASE_KEY + "formatError.lessThanZero.fieldSize");
        }

        YukonValidationUtils.checkExceedsMaxLength(errors, "exportField.padChar", field.getPadChar(), 1);
        YukonValidationUtils.checkExceedsMaxLength(errors, "exportField.missingAttributeValue", field.getMissingAttributeValue(), 20);
        
        if (field.isTimestamp()) {
            if (!field.getPattern().isEmpty()) {
                YukonValidationUtils.checkExceedsMaxLength(errors, "timestampPattern", field.getPattern(), 50);
                YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "timestampPattern", invalidPatternMsgKey);
                try {
                    new SimpleDateFormat(field.getPattern());
                } catch (Exception e) {
                    errors.rejectValue("timestampPattern", invalidPatternMsgKey);
                }
            } else {
                field.setPattern("MM/dd/yyyy hh:mm:ss zZ");
            }
        } else if (field.isValue()) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "readingPattern", field.getPattern(), 50);
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "readingPattern", invalidPatternMsgKey);
            try {
                new DecimalFormat(field.getPattern());
            } catch (Exception e) {
                errors.rejectValue("readingPattern", invalidPatternMsgKey);
            }
        }
    }
    
}