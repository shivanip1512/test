package com.cannontech.web.amr.archivedValuesExporter.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.amr.archivedValuesExporter.ArchivedValuesExporterController;
import com.cannontech.web.amr.archivedValuesExporter.model.ArchivedValuesExporter;

public class ArchiveValuesExporterValidator extends SimpleValidator<ArchivedValuesExporter> {

    public ArchiveValuesExporterValidator() {
        super(ArchivedValuesExporter.class);
    }

    @Override
    protected void doValidation(ArchivedValuesExporter target, Errors errors) {
        if (target.getEndDate() == null) {
            errors.reject(ArchivedValuesExporterController.baseKey+"formatError.endDateRequired");
        }
    }
}