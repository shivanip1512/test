package com.cannontech.web.api.point;

import org.springframework.validation.Errors;

import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.tools.points.model.CalcAnalogBase;
import com.cannontech.web.tools.points.model.CalcAnalogPointModel;
import com.cannontech.web.tools.points.model.CalcUpdateType;

public class CalcAnalogPointApiValidator extends ScalarPointApiValidator<CalcAnalogPointModel> {
    
    public CalcAnalogPointApiValidator() {
        super();
    }

    @Override
    public boolean supports(Class clazz) {
        return CalcAnalogPointModel.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(CalcAnalogPointModel calcAnalogPointModel, Errors errors) {
        super.doValidation(calcAnalogPointModel, errors);
        validateCalcAnalogBase(calcAnalogPointModel.getCalcAnalogBase(), errors);
        validateCalcComponent(calcAnalogPointModel, errors);
        validateCalcBaseline(calcAnalogPointModel, errors);
    }

    /**
     * Validate Calc Analog Base Fields.
     */
        private void validateCalcAnalogBase(CalcAnalogBase calcAnalogBase, Errors errors) {
            if (calcAnalogBase != null) {
                if (calcAnalogBase.getUpdateType() != null) {
                    if (calcAnalogBase.getUpdateType() == CalcUpdateType.ON_TIMER || calcAnalogBase.getUpdateType() == CalcUpdateType.ON_TIMER_AND_CHANGE) {
                        YukonValidationUtils.checkIfFieldRequired("calcAnalogBase.periodicRate", errors, calcAnalogBase.getPeriodicRate(), "calcAnalogBase.periodicRate");
                        if (!errors.hasFieldErrors("calcAnalogBase.periodicRate")) {
                            TimeIntervals periodicRate = TimeIntervals.fromSeconds(calcAnalogBase.getPeriodicRate());
                            if (!TimeIntervals.getUpdateAndScanRate().contains(periodicRate)) {
                                errors.rejectValue("calcAnalogBase.periodicRate", baseKey + ".invalid", new Object[] { calcAnalogBase.getPeriodicRate() }, "");
                            }
                        }
                    }
                }
            }
        }

    /**
    * Validate Calc Components Fields.
    */
    private void validateCalcComponent(CalcAnalogPointModel calAnalogPointModel, Errors errors) {
        CalcPointValidationHelper.ValidateCalcComponent(calAnalogPointModel.getCalcComponents(), calAnalogPointModel.getPointType(), errors);
    }

    /**
     * Validate Baseline field for Function.
     */
    private void validateCalcBaseline(CalcAnalogPointModel calcAnalogPointModel, Errors errors) {
        CalcPointValidationHelper.validateCalcBaseline(calcAnalogPointModel.getCalcComponents(), calcAnalogPointModel.getBaselineId(), errors);
    }
}
