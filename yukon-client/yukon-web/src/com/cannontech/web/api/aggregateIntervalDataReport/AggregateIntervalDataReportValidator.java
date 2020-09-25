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
    protected void doValidation(AggregateIntervalReportFilter aggregateIntervalReportFilter, Errors errors) {
        if (CollectionUtils.isEmpty(aggregateIntervalReportFilter.getDevices())) {
            YukonValidationUtils.checkIsBlank(errors, "deviceGroup", aggregateIntervalReportFilter.getDeviceGroup(), false);
        }
        if (aggregateIntervalReportFilter.getMissingIntervalData() == MissingIntervalData.FIXED_VALUE) {
            YukonValidationUtils.checkIsBlank(errors, "missingIntervalDataValue",
                    aggregateIntervalReportFilter.getMissingIntervalDataValue(), false);
        }
    }

}
