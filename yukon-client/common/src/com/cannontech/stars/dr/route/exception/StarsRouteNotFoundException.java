package com.cannontech.stars.dr.route.exception;

import com.cannontech.stars.util.StarsClientRequestException;;

public class StarsRouteNotFoundException extends StarsClientRequestException {

    public StarsRouteNotFoundException(String routeName, String energyCompanyName) {
        super("Route not found: [" + routeName + "], it is not associated with Energy Company [" + energyCompanyName + "]");
    }
}
