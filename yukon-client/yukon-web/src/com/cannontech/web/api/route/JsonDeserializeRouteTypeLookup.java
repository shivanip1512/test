package com.cannontech.web.api.route;

import java.io.IOException;

import com.cannontech.common.exception.TypeNotSupportedException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.api.route.model.RouteBaseModel;
import com.cannontech.web.api.route.model.RouteModelFactory;
import com.cannontech.yukon.IDatabaseCache;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class JsonDeserializeRouteTypeLookup extends StdDeserializer<RouteBaseModel> {

    protected JsonDeserializeRouteTypeLookup() {
        this(null);
    }

    protected JsonDeserializeRouteTypeLookup(Class<?> vc) {
        super(vc);
    }

    private IDatabaseCache serverDatabaseCache = YukonSpringHook.getBean(IDatabaseCache.class);

    @Override
    public RouteBaseModel deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException, TypeNotSupportedException {
        TreeNode node = parser.readValueAsTree();
        if (node == null) {
            throw new NotFoundException("request is not found in correct format");
        }

        String signalTransmitterId = node.get("signalTransmitterId").toString();

        String idStr = ServletUtils.getPathVariable("id");
        PaoType paoType;
        TreeNode typeTreeNode = node.get("type");
        Integer id = null;
        if (idStr == null) {
            // Create Case
            paoType = getPaoTypeFromCache(signalTransmitterId);

        } else {
            // Update case
            // if pointType field is present in request, Validate pointType.
            if (typeTreeNode != null) {
                getPaoTypeFromJson(typeTreeNode);
            }
            id = Integer.valueOf(idStr);
            paoType = getPaoTypeFromCache(signalTransmitterId);
        }
        RouteBaseModel routeBaseModel = (RouteBaseModel) parser.getCodec().treeToValue(node,
                getRouteFromModelFactory(getPaoTypeFromCache(signalTransmitterId), id).getClass());

        return routeBaseModel;
    }

    /**
     * Retrieves RouteType from type field provided in JSON.
     * 
     * @throws TypeNotSupportedExcpetion when invalid RouteType is provided in JSON,
     *                                   this exception is handled by ApiExceptionHandler which will convert it into a global
     *                                   error.
     */
    private PaoType getPaoTypeFromJson(TreeNode type) throws TypeNotSupportedException {
        String paoTypeString = null;
        if (type != null) {
            try {
                paoTypeString = type.toString().replace("\"", "");
                return PaoType.valueOf(paoTypeString);
            } catch (IllegalArgumentException e) {
                // throw exception for invalid paoType
                throw new TypeNotSupportedException(paoTypeString + " type is not valid.");
            }
        } else {
            throw new NotFoundException("type is not found in the request.");
        }

    }

    /**
     * Retrieves PaoType from Cache based on id provided.
     */
    private PaoType getPaoTypeFromCache(String id) {
        PaoType paoType;
        try {
            paoType = serverDatabaseCache.getAllPaosMap().get(Integer.valueOf(id)).getPaoType();
        } catch (NumberFormatException e) {
            throw new NotFoundException(id + " not a Signal transmitter id.");
        } catch(NullPointerException e) {
            throw new NotFoundException("Signal transmitter id " + id + " not found.");
        }
        
        PaoType routePaoType = null;
        if (paoType != null) {
            if (paoType.isCcu() || paoType.isRepeater()) {
                routePaoType = PaoType.ROUTE_CCU;
            } else if (paoType.isTcu()) {
                routePaoType = PaoType.ROUTE_TCU;
            } else if (paoType.isLcu()) {
                routePaoType = PaoType.ROUTE_LCU;
            } else if (paoType == PaoType.TAPTERMINAL) {
                routePaoType = PaoType.ROUTE_TAP_PAGING;
            } else if (paoType == PaoType.WCTP_TERMINAL) {
                routePaoType = PaoType.ROUTE_WCTP_TERMINAL;
            } else if (paoType == PaoType.SNPP_TERMINAL) {
                routePaoType = PaoType.ROUTE_SNPP_TERMINAL;
            } else if (paoType == PaoType.TNPP_TERMINAL) {
                routePaoType = PaoType.ROUTE_TNPP_TERMINAL;
            } else if (paoType == PaoType.RTC) {
                routePaoType = PaoType.ROUTE_RTC;
            } else if (paoType == PaoType.SERIES_5_LMI) {
                routePaoType = PaoType.ROUTE_SERIES_5_LMI;
            } else if (paoType == PaoType.RDS_TERMINAL) {
                routePaoType = PaoType.ROUTE_RDS_TERMINAL;
            } else {
                throw new NotFoundException("Route Type " + paoType + " not found.");
            }

        }
        return routePaoType;
    }

    /**
     * Retrieves RouteBaseModel from model factory.
     */
    private RouteBaseModel getRouteFromModelFactory(PaoType paoType, Integer id) {
        RouteBaseModel routeBaseModel = RouteModelFactory.getModel(paoType);
        if (routeBaseModel != null) {
            routeBaseModel.setId(id);
            return routeBaseModel;
        } else {
            // throw exception for not supported pointType
            throw new TypeNotSupportedException(paoType + " type is not supported.");
        }
    }

}