package com.cannontech.web.api.macroRoute;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.macroRoute.model.MacroRouteList;
import com.cannontech.yukon.IDatabaseCache;

public class MacroRouteApiValidatorHelper {
    @Autowired private IDatabaseCache serverDatabaseCache;
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
    public void validateMacroRouteName(Errors errors, String routeName, Integer id) {

        String nameI18nText = accessor.getMessage(commonkey + "name");
        YukonApiValidationUtils.checkIsBlank(errors, "deviceName", routeName, nameI18nText, false);

        if (!errors.hasFieldErrors("deviceName")) {
            YukonApiValidationUtils.checkExceedsMaxLength(errors, "deviceName", routeName, 60);
            YukonApiValidationUtils.checkIllegalCharacter(errors, "deviceName", routeName, nameI18nText);
            serverDatabaseCache.getAllRoutes()
                    .stream()
                    .filter(liteRoute -> liteRoute.getPaoName().equalsIgnoreCase(routeName.trim()))
                    .findAny()
                    .ifPresent(liteYukonPAObject -> {
                        if (id == null || liteYukonPAObject.getLiteID() != id) {
                            errors.rejectValue("deviceName", ApiErrorDetails.ALREADY_EXISTS.getCodeString(), new Object[] { routeName },
                                    "");
                        }
                    });
        }
    }

    public void validateRouteIds(Errors errors, MacroRouteList macroRouteList, int routeListIndex) {

        boolean routeIdExists = serverDatabaseCache.getAllRoutes()
                .stream()
                .anyMatch(route -> route.getLiteID() == macroRouteList.getRouteId() && (!route.getPaoType().equals(PaoType.ROUTE_MACRO)));
        if (!routeIdExists) {
            errors.rejectValue("routeList["+routeListIndex+"].routeId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { macroRouteList.getRouteId() }, "");
        }
    }

}
