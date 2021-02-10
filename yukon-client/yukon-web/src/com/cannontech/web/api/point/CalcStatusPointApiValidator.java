package com.cannontech.web.api.point;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.tools.points.model.CalcStatusPointModel;
import com.cannontech.web.tools.points.model.CalcUpdateType;
import com.cannontech.web.tools.points.model.CalculationBase;

public class CalcStatusPointApiValidator extends StatusPointApiValidator<CalcStatusPointModel> {

    public CalcStatusPointApiValidator() {
        super();
    }

    @Override
    public boolean supports(Class clazz) {
        return CalcStatusPointModel.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(CalcStatusPointModel calcStatusPointModel, Errors errors) {
        super.doValidation(calcStatusPointModel, errors);
        validateCalcBase(calcStatusPointModel.getCalculationBase(), errors);
        validateCalcComponent(calcStatusPointModel, errors);
        validateCalcBaseline(calcStatusPointModel, errors);
    }

    /**
     * Validate Calc Base Fields.
     */
    private void validateCalcBase(CalculationBase calculationBase, Errors errors) {
        if (calculationBase != null) {
            if (calculationBase.getUpdateType() != null) {

                if (calculationBase.getUpdateType() == CalcUpdateType.CONSTANT) {
                    errors.rejectValue("calculationBase.updateType", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { ArrayUtils.removeElement(CalcUpdateType.values(), CalcUpdateType.CONSTANT) }, "");
                }

                if (calculationBase.getUpdateType() == CalcUpdateType.ON_TIMER || calculationBase.getUpdateType() == CalcUpdateType.ON_TIMER_AND_CHANGE) {
                    YukonValidationUtils.checkIfFieldRequired("calculationBase.periodicRate", errors, calculationBase.getPeriodicRate(), "calculationBase.periodicRate");
                    if (!errors.hasFieldErrors("calculationBase.periodicRate")) {
                        TimeIntervals periodicRate = TimeIntervals.fromSeconds(calculationBase.getPeriodicRate());
                        if (!TimeIntervals.getUpdateAndScanRate().contains(periodicRate)) {
                            errors.rejectValue("calculationBase.periodicRate", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                    new Object[] { StringUtils.join(TimeIntervals.getUpdateAndScanRate(), ", ") }, "");
                        }
                    }
                }
            }
        }
    }

    /**
     * Validate Calc Components Fields.
     */
    private void validateCalcComponent(CalcStatusPointModel calcStatusPointModel, Errors errors) {
        CalcPointValidationHelper.ValidateCalcComponent(calcStatusPointModel.getCalcComponents(), errors);
    }


    /**
     * Validate Calc Basline Field.
     */
    private void validateCalcBaseline(CalcStatusPointModel calcStatusPointModel, Errors errors) {
        CalcPointValidationHelper.validateCalcBaseline(calcStatusPointModel.getCalcComponents(), calcStatusPointModel.getBaselineId(), errors);
    }
}
