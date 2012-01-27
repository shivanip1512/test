package com.cannontech.web.amr.archivedValuesExporter.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.amr.archivedValuesExporter.ArchivedValuesExporterBackingBean;
import com.cannontech.web.amr.archivedValuesExporter.ArchivedValuesExporterController;

public class ExportReportValidator extends SimpleValidator<ArchivedValuesExporterBackingBean> {
    private static final String endDateRequiredMsgKey = ArchivedValuesExporterController.baseKey +"formatError.endDateRequired";

    public ExportReportValidator() {
        super(ArchivedValuesExporterBackingBean.class);
    }

    @Override
    protected void doValidation(ArchivedValuesExporterBackingBean target, Errors errors) {
        if (target.getEndDate() == null) {
            errors.reject(endDateRequiredMsgKey);
        }
    }
}

