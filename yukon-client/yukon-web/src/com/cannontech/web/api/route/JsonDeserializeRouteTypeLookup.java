package com.cannontech.web.api.route;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.exception.TypeNotSupportedException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.web.api.route.model.RouteBaseModel;
import com.cannontech.web.api.route.service.impl.RouteHelperImpl;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class JsonDeserializeRouteTypeLookup extends StdDeserializer<RouteBaseModel<?>> {
    protected JsonDeserializeRouteTypeLookup() {
        this(null);
    }

    protected JsonDeserializeRouteTypeLookup(Class<?> vc) {
        super(vc);
    }

    private RouteHelperImpl routeHelper = new RouteHelperImpl();

    @Override
    public RouteBaseModel<?> deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException, TypeNotSupportedException {
        TreeNode node = parser.readValueAsTree();
        if (node == null) {
            throw new NotFoundException("request is not found in correct format");
        }

        String signalTransmitterId = null;
        if (node.get("signalTransmitterId") != null) {
            signalTransmitterId = node.get("signalTransmitterId").toString().replace("\"", "");
        }

        if (StringUtils.isBlank(signalTransmitterId)) {
            throw new NotFoundException("signalTransmitterId is not present in request.");
        }

        RouteBaseModel<?> routeBaseModel = (RouteBaseModel<?>) parser.getCodec().treeToValue(node,
                routeHelper.getRouteFromModelFactory(routeHelper.getRouteType(signalTransmitterId)).getClass());
        routeBaseModel.setDeviceType(routeHelper.getPaoTypeFromCache(signalTransmitterId));
        return routeBaseModel;
    }

}