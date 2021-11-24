package com.cannontech.web.api.route;


import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.route.model.CCURouteModel;
import com.cannontech.web.api.route.model.RepeaterRouteModel;
import com.cannontech.yukon.IDatabaseCache;

public class RouteCCUApiValidator<T extends CCURouteModel<?>> extends RouteApiValidator<T> {
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    private MessageSourceAccessor accessor;
    private final static String basekey = "yukon.web.error.repeater850Message";

    public RouteCCUApiValidator() {
        super();
    }

    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }
    
    @Override
    protected void doValidation(T route, Errors errors) {
        super.doValidation(route, errors);
        int repeaterArrSize = route.getRepeaters().size();
        int count = 0;
        if (route.getRepeaters().size() > 0 && route.getRepeaters().size() <= 7) {
            for (RepeaterRouteModel repeaterRouteModel : route.getRepeaters()) {
                validateRepeater(repeaterRouteModel, errors, repeaterArrSize, count, count == repeaterArrSize - 1);
                count++;
            }
        }
        if (route.getCarrierRoute() != null) {
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

    private void validateRepeater(RepeaterRouteModel repeaterRouteModel, Errors errors, Integer repeaterArrSize, Integer count,
            boolean lastRepeater) {
        if (repeaterRouteModel.getRepeaterId() == null) {
            errors.rejectValue("repeaters[" + count + "].repeaterId", ApiErrorDetails.FIELD_REQUIRED.getCodeString(),
                    new Object[] { "repeaterId" }, "");
        } else {
            int repeaterId = repeaterRouteModel.getRepeaterId();
            /**
             * Checking repaterId belongs to device Repeater or not
             * if repeater array size is equal to last repeater then variable bit is 7 or else ranges between 0 to 6.
             * If Repeater_850 exists in the input then it should be last repeater.
             */
            LiteYukonPAObject liteYukonPAObject = dbCache.getAllPaosMap().get(repeaterId);
            if (liteYukonPAObject == null || !liteYukonPAObject.getPaoType().isRepeater()) {
                errors.rejectValue("repeaters[" + count + "].repeaterId", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { repeaterId }, "");
            } else {
                YukonApiValidationUtils.checkIfFieldRequired("repeaters[" + count + "].variableBits", errors,
                        repeaterRouteModel.getVariableBits(), "VariableBits");
                if (!errors.hasFieldErrors("variableBits")) {
                    int variableBits = repeaterRouteModel.getVariableBits();
                    if (!lastRepeater) {
                        // non last Repeater can have variable bits from 1 to 6
                        YukonApiValidationUtils.checkRange(errors, "repeaters[" + count + "].variableBits", variableBits, 1, 6,
                                false);
                        // 1st 6 cant be of type REPEATER_850
                        if (liteYukonPAObject.getPaoType() == PaoType.REPEATER_850) {
                            String repeaterI18nText = accessor.getMessage(basekey, String.valueOf(repeaterId));
                            errors.rejectValue("repeaters[" + count + "].repeaterId",
                                    ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                    new Object[] { repeaterI18nText }, "");
                        }
                    } else if (variableBits != 7) {
                        errors.rejectValue("repeaters[" + count + "].variableBits", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                new Object[] { 7 }, "");
                    }
                }
            }
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