package com.cannontech.web.amr.archivedValuesExporter.validator;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.springframework.validation.Errors;

import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.amr.archivedValuesExporter.ArchivedValuesExporterController;
import com.cannontech.web.amr.archivedValuesExporter.model.ArchivedValuesExporterBackingBean;

public class ExportFieldValidator extends SimpleValidator<ArchivedValuesExporterBackingBean> {

    private static final String invalidPatternMsgKey = ArchivedValuesExporterController.baseKey+"formatError.invalidPattern";
    
    public ExportFieldValidator() {
        super(ArchivedValuesExporterBackingBean.class);
    }

    @Override
    protected void doValidation(ArchivedValuesExporterBackingBean target, Errors errors) {
        if (target.getExportField().getFieldType() == FieldType.PLAIN_TEXT ) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "plainText", target.getPlainText(), 50);

        } else if (target.getExportField().getMaxLength() == null || target.getExportField().getMaxLength() < 0) {
            errors.rejectValue("exportField.maxLength", ArchivedValuesExporterController.baseKey+"formatError.lessThanZero.fieldSize");
        }

        YukonValidationUtils.checkExceedsMaxLength(errors, "exportField.padChar", target.getExportField().getPadChar(), 1);
        YukonValidationUtils.checkExceedsMaxLength(errors, "exportField.missingAttributeValue", target.getExportField().getMissingAttributeValue(), 20);

        if (target.getExportField().getFieldType() == FieldType.ATTRIBUTE) {
            if (target.getExportField().getAttributeField() == AttributeField.TIMESTAMP) {
                YukonValidationUtils.checkExceedsMaxLength(errors, "timestampPattern", target.getTimestampPattern(), 50);
                YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "timestampPattern", invalidPatternMsgKey);
                try {
                    new SimpleDateFormat(target.getTimestampPattern());
                } catch (Exception e) {
                    errors.rejectValue("timestampPattern", invalidPatternMsgKey);
                }
            } else if (target.getExportField().getAttributeField() == AttributeField.VALUE) {
                YukonValidationUtils.checkExceedsMaxLength(errors, "valuePattern", target.getValuePattern(), 50);
                YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "valuePattern", invalidPatternMsgKey);
                try {
                    new DecimalFormat(target.getValuePattern());
                } catch (Exception e) {
                    errors.rejectValue("valuePattern", invalidPatternMsgKey);
                }
            }
        }
    }
}
