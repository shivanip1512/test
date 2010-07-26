package com.cannontech.web.stars.dr.operator.validator;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.validation.Errors;

import com.cannontech.common.events.model.DateFilterValue;
import com.cannontech.common.events.model.EventLogFilter;
import com.cannontech.common.events.model.FilterValue;
import com.cannontech.common.events.model.NumberFilterValue;
import com.cannontech.common.events.model.StringFilterValue;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.common.events.model.EventLogTypeBackingBean;

public class EventLogTypeValidator extends SimpleValidator<EventLogTypeBackingBean> {

    public EventLogTypeValidator() {
        super(EventLogTypeBackingBean.class);
    }

    @Override
    public void doValidation(EventLogTypeBackingBean target, Errors errors) {
        EventLogTypeBackingBean eventLogTypeBackingBean = (EventLogTypeBackingBean)target;

        // Validate filter value
        List<EventLogFilter> eventLogFilters = eventLogTypeBackingBean.getEventLogFilters();
        for (int i = 0; i < eventLogFilters.size(); i++) {
            EventLogFilter eventLogFilter = eventLogFilters.get(i);
        
            FilterValue filterValue = eventLogFilter.getFilterValue();
            errors.pushNestedPath("eventLogFilters["+i+"].filterValueFactoryBean");
            validateFilterValue(filterValue, errors);
            errors.popNestedPath();
        }
        
        // Validate start and stop dates
        LocalDate startDate = eventLogTypeBackingBean.getStartDate();
        LocalDate stopDate = eventLogTypeBackingBean.getStopDate();
        if (startDate != null && stopDate != null && startDate.isAfter(stopDate)) {
            YukonValidationUtils.rejectValues(errors,
                                              "yukon.web.modules.support.startDateMustBeBeforeStopDate",
                                              "startDate",
                                              "stopDate");
        }
        
    }

    private void validateFilterValue(FilterValue filterValue, Errors errors) {
        if (filterValue instanceof StringFilterValue) {
            StringFilterValue stringFilterValue = (StringFilterValue) filterValue;

            YukonValidationUtils.checkExceedsMaxLength(errors, "stringFilterValue", 
                                                       stringFilterValue.getFilterValue(), 2000);

        } else if (filterValue instanceof NumberFilterValue) {
            NumberFilterValue numberFilterValue = (NumberFilterValue) filterValue;
            String doubleNumberStr = String.valueOf(numberFilterValue.getDoubleFilterValue()); 
                        
            YukonValidationUtils.checkExceedsMaxLength(errors, "doublefilterValue", 
                                                       doubleNumberStr, 38);

        } else if (filterValue instanceof DateFilterValue) {
            DateFilterValue dateFilterValue = (DateFilterValue) filterValue;
            
            DateTime startDate = dateFilterValue.getStartDate();
            DateTime stopDate = dateFilterValue.getStopDate();
            if (startDate != null && stopDate != null && startDate.isAfter(stopDate)) {
                YukonValidationUtils.rejectValues(errors,
                                                  "yukon.web.modules.support.startDateMustBeBeforeStopDate",
                                                  "dateStartDate",
                                                  "dateStopDate");
            }

        }
        
    }
    
}
