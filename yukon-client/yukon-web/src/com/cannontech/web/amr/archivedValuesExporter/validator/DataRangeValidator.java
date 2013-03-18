package com.cannontech.web.amr.archivedValuesExporter.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRangeType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.amr.archivedValuesExporter.ArchivedValuesExporterController;

public class DataRangeValidator extends SimpleValidator<DataRange> {

    public DataRangeValidator() {
        super(DataRange.class);
    }

    @Override
    protected void doValidation(DataRange target, Errors errors) {
        // Checking End Date
        if (target.getDataRangeType() == DataRangeType.END_DATE) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "endDate", ArchivedValuesExporterController.baseKey+"formatError.endDateRequired");
        }
        
        // Checking Local Date Range
        if (target.getDataRangeType() == DataRangeType.DATE_RANGE) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "localDateRange.startDate", ArchivedValuesExporterController.baseKey+"formatError.startDateRequired");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "localDateRange.endDate", ArchivedValuesExporterController.baseKey+"formatError.endDateRequired");
        }
    }
}