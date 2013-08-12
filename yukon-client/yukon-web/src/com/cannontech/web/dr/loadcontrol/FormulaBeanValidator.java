package com.cannontech.web.dr.loadcontrol;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.FormulaInput.InputType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.flashScope.FlashScope;

public class FormulaBeanValidator extends SimpleValidator<FormulaBean> {

    private String baseKey;
    public FormulaBeanValidator(String baseKey) {
        super(FormulaBean.class);
        this.baseKey = baseKey + "error.";
    }

    public boolean doBeanValidation(FormulaBean bean, BindingResult errors, FlashScope flashScope) {
        boolean hasErrors = false;
        List<LookupTableBean> tables = bean.getTables();

        for (int i=0; i<tables.size();i++) {
            LookupTableBean table = tables.get(i);

            if (table.getInputType() == InputType.TIME) {
                List<TimeTableEntryBean> timeEntries = table.getTimeEntries();
                if (timeEntries.isEmpty()) {
                    flashScope.setError(YukonMessageSourceResolvable.createDefaultWithoutCode("Table found without entries. All tables must have one or more entries."));
                    hasErrors = true;
                }
            } else {
                List<TableEntryBean> entries = table.getEntries();
                if (entries.isEmpty()) {
                    flashScope.setError(YukonMessageSourceResolvable.createDefaultWithoutCode("Table found without entries. All tables must have one or more entries."));
                    hasErrors = true;
                }
            }
        }
        doValidation(bean, errors);
        return hasErrors || errors.hasErrors();
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
            List<FunctionBean> functions = bean.getFunctions();
            for (int i=0; i<functions.size();i++) {
                FunctionBean function = functions.get(i);
                if(StringUtils.isEmpty(function.getName())) {
                    errors.rejectValue("functions["+ i +"].name", "yukon.web.error.isBlank", null,"");
                }
                // User specified a max which is lower than the min ... tisk tisk
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
                }
                if (table.getInputType() == InputType.TIME) {
                    if(!table.getTimeEntries().isEmpty()) {
//                        LocalTime tableMax = Collections.max(TimeTableEntryBean.toLookupTableMap(table.getTimeEntries()).keySet());
//                        if (tableMax.isAfter(table.getTimeInputMax())) {
//                            errors.rejectValue("tables["+i+"].timeInputMax", baseKey + "timeMaxTooSoon", null,"");
//                        }

                        // LOOP OVER LOOKING FOR DUPLICATES
                        Set<LocalTime> keySet = new HashSet<>();
                        LocalTime key;
                        for (int entryIndex=0; entryIndex < timeEntries.size();entryIndex++) {
                            key = timeEntries.get(entryIndex).getKey();
                            if (keySet.contains(key)) {
                                errors.rejectValue("tables["+i+"].timeEntries["+entryIndex+"].key", baseKey + "duplicateKey", null,"");
                            }
                            keySet.add(key);
                        }
                    }
                } else {
                    if(!entries.isEmpty()) {
//                        Double tableMax = Collections.max(TableEntryBean.toLookupTableMap(entries).keySet());
                        // User specified a max which is lower than a value in the table... tisk tisk
//                        if (tableMax > table.getInputMax()) {
//                            errors.rejectValue("tables["+i+"].inputMax", baseKey + "inputMaxTooLow", null,"");
//                        }

                        // LOOP OVER LOOKING FOR DUPLICATES
                        Set<Double> keySet = new HashSet<>();
                        Double key;
                        for (int entryIndex=0; entryIndex < entries.size();entryIndex++) {
                            key = entries.get(entryIndex).getKey();
                            if (keySet.contains(key)) {
                                errors.rejectValue("tables["+i+"].entries["+entryIndex+"].key", baseKey + "duplicateKey", null,"");
                            }
                            keySet.add(key);
                        }
                    }
                }

            }
        }
    }

}
