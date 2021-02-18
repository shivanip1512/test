package com.cannontech.web.api.aggregateIntervalDataReport;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.Duration;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.tools.reports.service.AggregateIntervalReportService.AggregateIntervalReportFilter;
import com.cannontech.web.tools.reports.service.AggregateIntervalReportService.MissingIntervalData;

public class AggregateIntervalDataReportValidator extends SimpleValidator<AggregateIntervalReportFilter> {

    public AggregateIntervalDataReportValidator() {
        super(AggregateIntervalReportFilter.class);
    }

    @Override
    protected void doValidation(AggregateIntervalReportFilter filter, Errors errors) {
        YukonValidationUtils.checkIfFieldRequired("attribute", errors, filter.getAttribute(), "Attribute");
        YukonValidationUtils.checkIfFieldRequired("startDate", errors, filter.getStartDate(), "Start Date");
        YukonValidationUtils.checkIfFieldRequired("endDate", errors, filter.getEndDate(), "End Date");
        YukonValidationUtils.checkIfFieldRequired("interval", errors, filter.getInterval(), "Interval");
        YukonValidationUtils.checkIfFieldRequired("missingIntervalData", errors, filter.getMissingIntervalData(),"Missing Interval Data");
        YukonValidationUtils.checkIfFieldRequired("operation", errors, filter.getOperation(), "Operation");
        if (CollectionUtils.isEmpty(filter.getDevices())) {
            YukonValidationUtils.checkIsBlank(errors, "deviceGroup", filter.getDeviceGroup(), "Device Group", false);
        }
        if (filter.getMissingIntervalData() == MissingIntervalData.FIXED_VALUE) {
            YukonValidationUtils.checkIsBlank(errors, "missingIntervalDataValue", filter.getMissingIntervalDataValue(), "Missing Interval Data Value", false);
        }
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            YukonValidationUtils.checkIfEndDateGreaterThenStartDate("startDate", filter.getStartDate(), filter.getEndDate(), false, errors);
            //Check if greater than a year apart
            Duration dur = new Duration(filter.getStartDate(), filter.getEndDate());
            if (dur.getStandardDays() > 365) {
                errors.rejectValue("startDate", "yukon.web.error.date.rangeGreaterThanOneYear", new Object[] { filter.getStartDate(), filter.getEndDate() }, "");
            }
        }
    }

}
