package com.cannontech.web.tools.dataExporter.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRangeType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.tools.dataExporter.DataExporterFormatController;

public class DataRangeValidator extends SimpleValidator<DataRange> {

    public DataRangeValidator() {
        super(DataRange.class);
    }

    @Override
    protected void doValidation(DataRange target, Errors errors) {
        // Checking End Date
        if (target.getDataRangeType() == DataRangeType.END_DATE) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "endDate", DataExporterFormatController.BASE_KEY + "formatError.endDateRequired");
        }
        
        // Checking Local Date Range
        if (target.getDataRangeType() == DataRangeType.DATE_RANGE) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "localDateRange.startDate", DataExporterFormatController.BASE_KEY + "formatError.startDateRequired");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "localDateRange.endDate", DataExporterFormatController.BASE_KEY + "formatError.endDateRequired");
        }
        
        if (target.getDataRangeType() == DataRangeType.DAYS_PREVIOUS) {
            if (target.getDaysPrevious() < 1) {
                errors.rejectValue("daysPrevious", DataExporterFormatController.BASE_KEY + "formatError.lessThanZero.daysPrevious");
            }
        }
        
        if (target.getDataRangeType() == DataRangeType.DAYS_OFFSET) {
            YukonValidationUtils.checkIsPositiveInt(errors, "daysOffset", target.getDaysOffset());
        }
    }
}