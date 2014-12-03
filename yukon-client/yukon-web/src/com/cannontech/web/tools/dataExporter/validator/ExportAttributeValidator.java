package com.cannontech.web.tools.dataExporter.validator;

import org.springframework.validation.Errors;

import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.tools.dataExporter.DataExporterFormatController;

public class ExportAttributeValidator extends SimpleValidator<ExportAttribute> {
    
    private static final String invalidDaysPreviousMsgKey = DataExporterFormatController.BASE_KEY + "formatError.lessThanZero.daysPrevious";

    public ExportAttributeValidator() {
        super(ExportAttribute.class);
    }

    @Override
    protected void doValidation(ExportAttribute target, Errors errors) {
        
        if (target.getDaysPrevious() == null || target.getDaysPrevious() < 1) {
            errors.rejectValue("daysPrevious", invalidDaysPreviousMsgKey);
        }
    }
}