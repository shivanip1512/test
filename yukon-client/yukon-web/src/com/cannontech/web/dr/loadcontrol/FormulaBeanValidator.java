package com.cannontech.web.dr.loadcontrol;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.FormulaInput.InputType;

public class FormulaBeanValidator extends SimpleValidator<FormulaBean> {

    private String baseKey;
    public FormulaBeanValidator(String baseKey) {
        super(FormulaBean.class);
        this.baseKey = baseKey + "error.";
    }

    @Override
    protected void doValidation(FormulaBean bean, Errors errors) {
        // Probably prudent to check for NaN, infinity etc
        if(StringUtils.isEmpty(bean.getName())) {
            errors.rejectValue("name", "yukon.web.error.isBlank", null,"");
        }
        if(bean.getFunctionIntercept() == null) {
            errors.rejectValue("functionIntercept", "yukon.web.error.isBlank", null,"");
        }

        if (bean.getCalculationType() == Formula.CalculationType.FUNCTION) {
            
            if (Double.isNaN(bean.getFunctionIntercept()) || Double.isInfinite(bean.getFunctionIntercept())) {
                errors.rejectValue("functionIntercept", baseKey + "notValidNumber", null,"");
            }
            List<FunctionBean> functions = bean.getFunctions();
            for (int i=0; i<functions.size();i++) {
                FunctionBean function = functions.get(i);
                if (function.getInputType() == InputType.POINT) {
                    if (function.getInputPointId() == null) {
                        errors.rejectValue("functions["+i+"].inputType", baseKey + "invalidPoint", null,"");
                    }
                }
                if(StringUtils.isEmpty(function.getName())) {
                    errors.rejectValue("functions["+ i +"].name", "yukon.web.error.isBlank", null,"");
                } else {
                    YukonValidationUtils.checkExceedsMaxLength(errors, "functions["+ i +"].name", function.getName(), 32);
                }
                if (Double.isNaN(function.getInputMax()) || Double.isInfinite(function.getInputMax())) {
                    errors.rejectValue("functions["+i+"].inputMax", baseKey + "notValidNumber", null,"");
                }
                if (Double.isNaN(function.getInputMin()) || Double.isInfinite(function.getInputMin())) {
                    errors.rejectValue("functions["+i+"].inputMin", baseKey + "notValidNumber", null,"");
                }
                if (Double.isNaN(function.getQuadratic()) || Double.isInfinite(function.getQuadratic())) {
                    errors.rejectValue("functions["+i+"].quadratic", baseKey + "notValidNumber", null,"");
                }
                if (Double.isNaN(function.getLinear()) || Double.isInfinite(function.getLinear())) {
                    errors.rejectValue("functions["+i+"].linear", baseKey + "notValidNumber", null,"");
                }
                if (function.getInputMin() >= function.getInputMax()) {
                    errors.rejectValue("functions["+i+"].inputMax", baseKey + "inputMaxUnderMin", null, "");
                    errors.rejectValue("functions["+i+"].inputMin", baseKey + "inputMinOverMax", null, "");
                }
            }
        } else {
            List<LookupTableBean> tables = bean.getTables();
            for (int i=0; i<tables.size();i++) {
                LookupTableBean table = tables.get(i);
                List<TableEntryBean> entries = table.getEntries();
                List<TimeTableEntryBean> timeEntries = table.getTimeEntries();
                if(StringUtils.isEmpty(table.getName())) {
                    errors.rejectValue("tables["+i+"].name", "yukon.web.error.isBlank", null,"");
                } else {
                    YukonValidationUtils.checkExceedsMaxLength(errors, "functions["+ i +"].name", table.getName(), 32);
                }
                if (table.getInputType() == InputType.TIME) {
                    if(!table.getTimeEntries().isEmpty()) {
                        LocalTime tableMax = Collections.max(table.getTimeEntries()).getKey();
                        if (table.getTimeInputMax() == null) {
                            errors.rejectValue("tables["+i+"].timeInputMax", "yukon.web.error.isBlank", null,"");
                        } else if (tableMax.isAfter(table.getTimeInputMax())) {
                            errors.rejectValue("tables["+i+"].timeInputMax", baseKey + "timeMaxTooSoon", null,"");
                        }

                        // look for duplicate entries
                        Set<LocalTime> keySet = new HashSet<>();
                        LocalTime key;
                        for (int entryIndex=0; entryIndex < timeEntries.size();entryIndex++) {
                            key = timeEntries.get(entryIndex).getKey();
                            if (key == null) {
                                errors.rejectValue("tables["+i+"].timeEntries["+entryIndex+"].key", "yukon.web.error.isBlank", null,"");
                            } else if (keySet.contains(key)) {
                                errors.rejectValue("tables["+i+"].timeEntries["+entryIndex+"].key", baseKey + "duplicateKey", null,"");
                            }
                            keySet.add(key);
                        }
                    } else {
                        errors.rejectValue("tables["+i+"]", baseKey + "noEntries", null, "");
                    }
                } else {
                    if (table.getInputType() == InputType.POINT) {
                        if (table.getInputPointId() == null) {
                            errors.rejectValue("tables["+i+"].inputType", baseKey + "invalidPoint", null,"");
                        }
                    }
                    if(!entries.isEmpty()) {
                        Double tableMax = Collections.max(entries).getKey();

                        if (tableMax > table.getInputMax()) {
                            errors.rejectValue("tables["+i+"].inputMax", baseKey + "inputMaxTooLow", null,"");
                        }

                        // look for duplicate entries
                        Set<Double> keySet = new HashSet<>();
                        Double key;
                        for (int entryIndex=0; entryIndex < entries.size();entryIndex++) {
                            key = entries.get(entryIndex).getKey();
                            if (keySet.contains(key)) {
                                errors.rejectValue("tables["+i+"].entries["+entryIndex+"].key", baseKey + "duplicateKey", null,"");
                            }
                            keySet.add(key);
                            if (Double.isNaN(key) || Double.isInfinite(key)) {
                                errors.rejectValue("tables["+i+"].entries["+entryIndex+"].key", baseKey + "notValidNumber", null,"");
                            }
                        }
                    } else {
                        errors.rejectValue("tables["+i+"]", baseKey + "noEntries", null, "");
                    }
                }
            }
        }
    }
}
