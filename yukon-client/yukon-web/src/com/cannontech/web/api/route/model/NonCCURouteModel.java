package com.cannontech.web.api.route.model;

import com.cannontech.database.data.route.RouteBase;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class NonCCURouteModel extends RouteBaseModel<RouteBase> {

}
