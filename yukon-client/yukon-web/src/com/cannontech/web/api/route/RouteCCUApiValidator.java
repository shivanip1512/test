package com.cannontech.web.api.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.web.api.route.model.CCURouteModel;
import com.cannontech.web.api.route.model.RepeaterRouteModel;
import com.cannontech.yukon.IDatabaseCache;

public class RouteCCUApiValidator<T extends CCURouteModel<?>> extends RouteApiValidator<T> {
    public RouteCCUApiValidator() {
        super();
    }

    @Override
    protected void doValidation(T route, Errors errors) {
        super.doValidation(route, errors);
        int repeaterArrSize = route.getRepeaters().size();
        int count = 0;
        if (route.getRepeaters().size() > 0 && route.getRepeaters().size() <= 7) {
            for (RepeaterRouteModel repeaterRouteModel : route.getRepeaters()) {
                validateRepeater(repeaterRouteModel, errors, repeaterArrSize, count);
            }

            if (route.getCarrierRoute().getBusNumber() != null) {
                validateBusNumber(route.getCarrierRoute().getBusNumber(), errors);
            }

            if (route.getCarrierRoute().getCcuFixBits() != null) {
                validateCcuFixBits(route.getCarrierRoute().getCcuFixBits(), errors);
            }

            if (route.getCarrierRoute().getCcuVariableBits() != null) {
                validateCcuVariableBits(route.getCarrierRoute().getCcuVariableBits(), errors);
            }
        }

    }

    @Autowired private IDatabaseCache dbCache;

    private void validateRepeater(RepeaterRouteModel repeaterRouteModel, Errors errors, Integer repeaterArrSize, Integer count) {
        int repeaterId = repeaterRouteModel.getRepeaterId();
        int variableBits = repeaterRouteModel.getVariableBits();

        /**
         * Checking repaterId belongs to device Repeater or not
         * if repeater array size is equal to last repeater then variable bit is 7 or else ranges between 0 to 6.
         * If Repeater_850 exists in the input then it should be last repeater.
         */

        if (dbCache.getAllPaosMap().get(repeaterId) != null) {
            if (dbCache.getAllPaosMap().get(repeaterId).getPaoType().isRepeater()) {
                if (repeaterArrSize != count) {
                    if (dbCache.getAllPaosMap().get(repeaterId).getPaoType() != PaoType.REPEATER_850) {
                        if (variableBits > 6) {
                            YukonApiValidationUtils.checkRange(errors, "repeaters[" + 0 + "].variableBits", variableBits, 0, 6, false);
                        }
                    } else {
                        errors.rejectValue("repeaters[" + 0 + "].repeaterId", ApiErrorDetails.TYPE_MISMATCH.getCodeString(),
                                new Object[] { repeaterId }, "");
                    }
                } else {
                    if (dbCache.getAllPaosMap().get(repeaterId).getPaoType() == PaoType.REPEATER_850) {

                    } else {
                        if (variableBits != 7) {
                            YukonApiValidationUtils.checkRange(errors, "repeaters[" + 0 + "].variableBits", variableBits, 7, 7, false);
                        }
                    }
                }
            } else {
                errors.rejectValue("repeaters[" + 0 + "].repeaterId", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { repeaterId }, "");
            }

        } else {
            errors.rejectValue("repeaters[" + 0 + "].repeaterId", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { repeaterId }, "");
        }

    }

    private void validateCcuVariableBits(Integer ccuVariableBits, Errors errors) {
        YukonApiValidationUtils.checkRange(errors, "carrierRoute.ccuVariableBits", ccuVariableBits, 0, 6, false);
    }

    private void validateCcuFixBits(Integer ccuFixBits, Errors errors) {
        YukonApiValidationUtils.checkRange(errors, "carrierRoute.ccuFixBits", ccuFixBits, 0, 31, false);

    }

    private void validateBusNumber(Integer busNumber, Errors errors) {
        YukonApiValidationUtils.checkRange(errors, "carrierRoute.busNumber", busNumber, 1, 8, false);
    }
}
