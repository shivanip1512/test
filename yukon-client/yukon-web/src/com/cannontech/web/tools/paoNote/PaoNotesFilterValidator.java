package com.cannontech.web.tools.paoNote;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.notes.filter.model.PaoNotesFilter;
import com.cannontech.common.pao.notes.filter.model.PaoSelectionMethod;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class PaoNotesFilterValidator extends SimpleValidator<PaoNotesFilter> {

    private static final String baseKey = "yukon.web.common.paoNotesSearch.";

    public PaoNotesFilterValidator() {
        super(PaoNotesFilter.class);
    }

    @Override
    protected void doValidation(PaoNotesFilter filter, Errors errors) {
        if (!filter.getDateRange().isValid() || filter.getDateRange().isEmpty()) {
            YukonValidationUtils.rejectValues(errors, baseKey + "invalidDateRange", "dateRange.min", "dateRange.max");
        }
        if (filter.getPaoSelectionMethod() == PaoSelectionMethod.selectIndividually
            && CollectionUtils.isEmpty(filter.getPaoIds())) {
            errors.rejectValue("paoIds", baseKey + "noDeviceSelected");
        } else if (filter.getPaoSelectionMethod() == PaoSelectionMethod.byDeviceGroups
            && CollectionUtils.isEmpty(filter.getDeviceGroups())) {
            errors.rejectValue("deviceGroups", baseKey + "noDeviceGroupSelected");
        }
    }

}
