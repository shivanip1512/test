package com.cannontech.web.stars.dr.operator.validator;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.validation.Errors;

import com.cannontech.common.events.model.DateFilterValue;
import com.cannontech.common.events.model.EventLogFilter;
import com.cannontech.common.events.model.FilterValue;
import com.cannontech.common.events.model.NumberFilterValue;
import com.cannontech.common.events.model.StringFilterValue;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.common.events.model.EventLogTypeFilter;

public class EventLogTypeValidator extends SimpleValidator<EventLogTypeFilter> {

    public EventLogTypeValidator() {
        super(EventLogTypeFilter.class);
    }

    @Override
    public void doValidation(EventLogTypeFilter target, Errors errors) {
        EventLogTypeFilter eventLogTypeBackingBean = (EventLogTypeFilter)target;

        // Validate filter value
        List<EventLogFilter> eventLogFilters = eventLogTypeBackingBean.getEventLogFilters();
        for (int i = 0; i < eventLogFilters.size(); i++) {
            EventLogFilter eventLogFilter = eventLogFilters.get(i);
        
            FilterValue filterValue = eventLogFilter.getFilterValue();
            errors.pushNestedPath("eventLogFilters["+i+"].filterValue");
            validateFilterValue(filterValue, errors);
            errors.popNestedPath();
        }
        
        // Validate start and stop dates
        LocalDate startDate = eventLogTypeBackingBean.getStartDate();
        LocalDate stopDate = eventLogTypeBackingBean.getStopDate();
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", "yukon.web.modules.support.eventViewer.dateMustNotBeBlank");
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "stopDate", "yukon.web.modules.support.eventViewer.dateMustNotBeBlank");
        if (startDate != null && stopDate != null && startDate.isAfter(stopDate)) {
            YukonValidationUtils.rejectValues(errors,
                                              "yukon.web.modules.support.eventViewer.startDateMustBeBeforeStopDate",
                                              "startDate",
                                              "stopDate");
        }
        
    }

    private void validateFilterValue(FilterValue filterValue, Errors errors) {
        if (filterValue instanceof StringFilterValue) {
            StringFilterValue stringFilterValue = (StringFilterValue) filterValue;

            YukonValidationUtils.checkExceedsMaxLength(errors, "filterValue", 
                                                       stringFilterValue.getFilterValue(), 2000);

        } else if (filterValue instanceof NumberFilterValue) {
            // consider anything to be valid
        } else if (filterValue instanceof DateFilterValue) {
            DateFilterValue dateFilterValue = (DateFilterValue) filterValue;
            
            LocalDate startDate = dateFilterValue.getStartDate();
            LocalDate stopDate = dateFilterValue.getStopDate();
            if (startDate != null && stopDate != null && startDate.isAfter(stopDate)) {
                YukonValidationUtils.rejectValues(errors,
                                                  "yukon.web.modules.support.eventViewer.startDateMustBeBeforeStopDate",
                                                  "startDate",
                                                  "stopDate");
            }


        }
        
    }
    
}
