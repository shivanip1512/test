package com.cannontech.web.api.aggregateIntervalDataReport;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.Errors;


import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.tools.reports.service.AggregateIntervalReportService.AggregateIntervalReportFilter;
import com.cannontech.web.tools.reports.service.AggregateIntervalReportService.MissingIntervalData;

public class AggregateIntervalDataReportValidator extends SimpleValidator<AggregateIntervalReportFilter> {

    public AggregateIntervalDataReportValidator() {
        super(AggregateIntervalReportFilter.class);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void doValidation(AggregateIntervalReportFilter filter, Errors errors) {
        if (CollectionUtils.isEmpty(filter.getDevices())) {
            YukonValidationUtils.checkIsBlank(errors, "deviceGroup", filter.getDeviceGroup(), false);
        }
        if (filter.getMissingIntervalData() == MissingIntervalData.FIXED_VALUE) {
            YukonValidationUtils.checkIsBlank(errors, "missingIntervalDataValue",
                    filter.getMissingIntervalDataValue(), false);
        }
        YukonValidationUtils.checkIfEndDateGreaterThenStartDate("startDate", filter.getStartDate(), filter.getEndDate(), false, errors);
        
    }

}
