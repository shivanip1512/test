package com.cannontech.web.amr.archivedValuesExporter.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.amr.archivedValuesExporter.ArchivedValuesExporterBackingBean;

public class ExportAttributeValidator extends SimpleValidator<ArchivedValuesExporterBackingBean> {
    private static final String daysPrevious =
        "yukon.web.modules.amr.archivedValueExporter.formatError.lessThanZero.daysPrevious";

    public ExportAttributeValidator() {
        super(ArchivedValuesExporterBackingBean.class);
    }

    @Override
    protected void doValidation(ArchivedValuesExporterBackingBean target, Errors errors) {
        if (target.getExportAttribute().getDaysPrevious() == null
                || target.getExportAttribute().getDaysPrevious() < 1) {
            errors.rejectValue("exportAttribute.daysPrevious", daysPrevious);
        }
    }
}
