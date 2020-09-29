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
        YukonValidationUtils.checkIfFieldRequired("attribute", errors, filter.getAttribute(), "Attribute");
        YukonValidationUtils.checkIfFieldRequired("startDate", errors, filter.getStartDate(), "Start Date");
        YukonValidationUtils.checkIfFieldRequired("endDate", errors, filter.getEndDate(), "End Date");
        YukonValidationUtils.checkIfFieldRequired("interval", errors, filter.getInterval(), "Interval");
        YukonValidationUtils.checkIfFieldRequired("missingIntervalData", errors, filter.getMissingIntervalData(),"Missing Interval Data");
        YukonValidationUtils.checkIfFieldRequired("missingIntervalDataValue", errors, filter.getMissingIntervalDataValue(),"Missing Interval Data Value");
        YukonValidationUtils.checkIfFieldRequired("operation", errors, filter.getOperation(), "Operation");
        if (CollectionUtils.isEmpty(filter.getDevices())) {
            YukonValidationUtils.checkIsBlank(errors, "deviceGroup", filter.getDeviceGroup(), false);
        }
        if (filter.getMissingIntervalData() == MissingIntervalData.FIXED_VALUE) {
            YukonValidationUtils.checkIsBlank(errors, "missingIntervalDataValue", filter.getMissingIntervalDataValue(), false);
        }
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            YukonValidationUtils.checkIfEndDateGreaterThenStartDate("startDate", filter.getStartDate(), filter.getEndDate(), false, errors);
        }
    }

}
