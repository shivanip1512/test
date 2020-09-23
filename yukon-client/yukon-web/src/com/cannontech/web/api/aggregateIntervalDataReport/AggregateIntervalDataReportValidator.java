package com.cannontech.web.api.aggregateIntervalDataReport;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.tools.reports.service.AggregateIntervalReportService.AggregateIntervalReportFilter;
import com.cannontech.web.tools.reports.service.AggregateIntervalReportService.MissingIntervalData;

public class AggregateIntervalDataReportValidator extends SimpleValidator<AggregateIntervalReportFilter>{
    
    public AggregateIntervalDataReportValidator() {
        super(AggregateIntervalReportFilter.class);
    }

    @Override
    protected void doValidation(AggregateIntervalReportFilter aggregateIntervalReportFilter, Errors errors) {
        if(aggregateIntervalReportFilter.getDevices().isEmpty()) {
            YukonValidationUtils.checkIsBlank(errors, "deviceGroup", aggregateIntervalReportFilter.getDeviceGroup(), false);
        }
        
        
        if(aggregateIntervalReportFilter.getMissingIntervalData() == MissingIntervalData.FIXED_VALUE) {
            YukonValidationUtils.checkIsBlank(errors, "missingIntervalDataValue", aggregateIntervalReportFilter.getMissingIntervalDataValue(), false);
        }
//        Either Devices list or DeviceGroup are required. - done
        
//        Attribute can be a BuiltInAttribute string value, or a custom attribute ID value. - What does this mean?
        
//        Intervals can be 5, 15, 30, 60, and daily (midnight). 
//            These will correspond to TimeInterval.java enum values (MINUTES_5, MINUTES_15, MINUTES_30, HOURS_1, DAYS_1). 
//                Consider adding a special case for daily midnight intervals? -- added to TimeIntervals
        
//        IncompleteIntervalDataStrategy options: "SKIP", "BLANK", "PARTIAL", "FIXED_VALUE" -enum in Interface?
        
//        IncompleteIntervalDataValue: Only required when IncompleteIntervalDataStrategy is FIXED_VALUE. - done
        
//        Aggregation: "ADD", "MAX". -enum in Interface?
        
    }

}
