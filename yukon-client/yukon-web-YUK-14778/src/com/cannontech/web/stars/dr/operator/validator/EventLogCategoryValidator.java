package com.cannontech.web.stars.dr.operator.validator;

import org.joda.time.LocalDate;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.common.events.model.EventLogCategoryFilter;

public class EventLogCategoryValidator extends SimpleValidator<EventLogCategoryFilter> {

    public EventLogCategoryValidator() {
        super(EventLogCategoryFilter.class);
    }

    @Override
    public void doValidation(EventLogCategoryFilter target, Errors errors) {
        EventLogCategoryFilter eventLogCategoryBackingBean = (EventLogCategoryFilter)target;

        // Validate filter value
        YukonValidationUtils.checkExceedsMaxLength(errors, "filterValue", 
                                                   eventLogCategoryBackingBean.getFilterValue(), 2000);
        
        // Validate start and stop dates
        LocalDate startDate = eventLogCategoryBackingBean.getStartDate();
        LocalDate stopDate = eventLogCategoryBackingBean.getStopDate();
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", "yukon.web.modules.support.eventViewer.dateMustNotBeBlank");
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "stopDate", "yukon.web.modules.support.eventViewer.dateMustNotBeBlank");
        if (startDate != null && stopDate != null && startDate.isAfter(stopDate)) {
            YukonValidationUtils.rejectValues(errors,
                                              "yukon.web.modules.support.eventViewer.startDateMustBeBeforeStopDate",
                                              "startDate",
                                              "stopDate");
        }
        
    }

}
