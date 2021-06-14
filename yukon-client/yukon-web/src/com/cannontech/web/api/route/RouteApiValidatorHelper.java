package com.cannontech.web.api.route;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

public class RouteApiValidatorHelper {
    @Autowired private IDatabaseCache dbCache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

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
        YukonApiValidationUtils.checkIsBlank(errors, "name", routeName, nameI18nText, false);

        if (!errors.hasFieldErrors("name")) {
            YukonApiValidationUtils.checkExceedsMaxLength(errors, "name", routeName, 60);
            YukonApiValidationUtils.checkIllegalCharacter(errors, "name", routeName, nameI18nText);
            dbCache.getAllRoutes()
                    .stream()
                    .filter(liteRoute -> liteRoute.getPaoName().equalsIgnoreCase(routeName.trim()))
                    .findAny()
                    .ifPresent(liteYukonPAObject -> {
                        if (id == null || liteYukonPAObject.getRouteID() != id) {
                            errors.rejectValue("name", ApiErrorDetails.ALREADY_EXISTS.getCodeString(), new Object[] { routeName },
                                    "");
                        }
                    });
        }
    }

    public void validateSignalTransmitterId(Errors errors, Integer signalTransmitterId) {
        boolean allTransmiterList = dbCache.getAllDevices().stream()
                .anyMatch(device -> device.getPaoType().isTransmitter() && !device.getPaoType().isRepeater()
                        && device.getLiteID() == signalTransmitterId);
        if (allTransmiterList) {
            errors.rejectValue("signalTransmitterId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                    new Object[] { signalTransmitterId }, "");
        }
    }

}
