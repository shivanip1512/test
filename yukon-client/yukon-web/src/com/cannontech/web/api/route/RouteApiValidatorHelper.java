package com.cannontech.web.api.route;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.route.service.RouteHelper;
import com.cannontech.web.api.route.service.RouteService;
import com.cannontech.yukon.IDatabaseCache;

public class RouteApiValidatorHelper {
    @Autowired private IDatabaseCache dbCache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private RouteService routeService;
    @Autowired private RouteHelper routeHelper;

    private MessageSourceAccessor accessor;

    private final static String commonkey = "yukon.common.";

    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    /**
     * Validate Route name.
     */
    public void validateRouteName(Errors errors, String routeName, Integer id) {

        String nameI18nText = accessor.getMessage(commonkey + "name");
        YukonApiValidationUtils.checkIsBlank(errors, "deviceName", routeName, nameI18nText, false);

        if (!errors.hasFieldErrors("deviceName")) {
            YukonApiValidationUtils.checkExceedsMaxLength(errors, "deviceName", routeName, 60);
            YukonApiValidationUtils.checkIllegalCharacter(errors, "deviceName", routeName, nameI18nText);
            dbCache.getAllRoutes()
                    .stream()
                    .filter(liteRoute -> liteRoute.getPaoName().equalsIgnoreCase(routeName.trim()))
                    .findAny()
                    .ifPresent(liteYukonPAObject -> {
                        if (id == null || liteYukonPAObject.getRouteID() != id) {
                            errors.rejectValue("deviceName", ApiErrorDetails.ALREADY_EXISTS.getCodeString(), new Object[] { routeName },
                                    "");
                        }
                    });
        }
    }

    public void validateSignalTransmitterId(Errors errors, Integer signalTransmitterId, Integer routeId) {
        boolean transmitterExists = dbCache.getAllDevices().stream()
                .anyMatch(device -> device.getPaoType().isTransmitter() && !device.getPaoType().isRepeater()
                        && device.getLiteID() == signalTransmitterId);
        if (!transmitterExists) {
            errors.rejectValue("signalTransmitterId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                    new Object[] { signalTransmitterId }, "");
        }

        if (routeId != null) {
            String oldTransmitterId = routeService.retrieve(routeId).getSignalTransmitterId().toString();
            if ((routeHelper.getPaoTypeFromCache(oldTransmitterId) != routeHelper
                    .getPaoTypeFromCache(signalTransmitterId.toString()))) {
                errors.rejectValue("signalTransmitterId", ApiErrorDetails.TYPE_MISMATCH.getCodeString(),
                        new Object[] { signalTransmitterId }, "");

            }
        }

    }
}