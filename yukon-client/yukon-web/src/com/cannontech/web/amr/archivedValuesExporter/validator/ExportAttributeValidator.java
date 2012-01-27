package com.cannontech.web.amr.archivedValuesExporter.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.amr.archivedValuesExporter.ArchivedValuesExporterBackingBean;
import com.cannontech.web.amr.archivedValuesExporter.ArchivedValuesExporterController;

public class ExportAttributeValidator extends SimpleValidator<ArchivedValuesExporterBackingBean> {
    private static final String invalidDaysPreviousMsgKey = ArchivedValuesExporterController.baseKey +"formatError.lessThanZero.daysPrevious";

    public ExportAttributeValidator() {
        super(ArchivedValuesExporterBackingBean.class);
    }

    @Override
    protected void doValidation(ArchivedValuesExporterBackingBean target, Errors errors) {
        if (target.getExportAttribute().getDaysPrevious() == null
                || target.getExportAttribute().getDaysPrevious() < 1) {
            errors.rejectValue("exportAttribute.daysPrevious", invalidDaysPreviousMsgKey);
        }
    }
}
