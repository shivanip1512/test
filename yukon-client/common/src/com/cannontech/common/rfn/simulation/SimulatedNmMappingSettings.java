package com.cannontech.common.rfn.simulation;

import java.io.Serializable;

import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResultType;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponseType;
import com.cannontech.common.rfn.message.network.NeighborData;
import com.cannontech.common.rfn.message.network.ParentData;
import com.cannontech.common.rfn.message.network.RfnNeighborDataReplyType;
import com.cannontech.common.rfn.message.network.RfnParentReplyType;
import com.cannontech.common.rfn.message.network.RfnPrimaryRouteDataReplyType;
import com.cannontech.common.rfn.message.network.RouteData;

public class SimulatedNmMappingSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private NeighborData neighborData;
    private ParentData parentData;
    private RouteData routeData;
    private RfnParentReplyType parentReplyType;
    private RfnNeighborDataReplyType neighborReplyType;
    private RfnPrimaryRouteDataReplyType routeReplyType;
    private RfnMetadataMultiResponseType metadataResponseType;
    private RfnMetadataMultiQueryResultType metadataQueryResponseType;
    private String metadataResponseString;
    private Integer emptyNullPercent;
    private Integer minHop;
    private Integer maxHop;
    private Integer nodesOneHop;
    
    public NeighborData getNeighborData() {
        return neighborData;
    }
    public void setNeighborData(NeighborData neighborData) {
        this.neighborData = neighborData;
    }
    public ParentData getParentData() {
        return parentData;
    }
    public void setParentData(ParentData parentData) {
        this.parentData = parentData;
    }
    public RouteData getRouteData() {
        return routeData;
    }
    public void setRouteData(RouteData routeData) {
        this.routeData = routeData;
    }
    public RfnParentReplyType getParentReplyType() {
        return parentReplyType;
    }
    public void setParentReplyType(RfnParentReplyType parentReplyType) {
        this.parentReplyType = parentReplyType;
    }
    public RfnNeighborDataReplyType getNeighborReplyType() {
        return neighborReplyType;
    }
    public void setNeighborReplyType(RfnNeighborDataReplyType neighborReplyType) {
        this.neighborReplyType = neighborReplyType;
    }
    public RfnPrimaryRouteDataReplyType getRouteReplyType() {
        return routeReplyType;
    }
    public void setRouteReplyType(RfnPrimaryRouteDataReplyType routeReplyType) {
        this.routeReplyType = routeReplyType;
    }
    public RfnMetadataMultiResponseType getMetadataResponseType() {
        return metadataResponseType;
    }
    public void setMetadataResponseType(RfnMetadataMultiResponseType metadataResponseType) {
        this.metadataResponseType = metadataResponseType;
    }
    public RfnMetadataMultiQueryResultType getMetadataQueryResponseType() {
        return metadataQueryResponseType;
    }
    public void setMetadataQueryResponseType(RfnMetadataMultiQueryResultType metadataQueryResponseType) {
        this.metadataQueryResponseType = metadataQueryResponseType;
    }
    public String getMetadataResponseString() {
        return metadataResponseString;
    }
    public void setMetadataResponseString(String metadataResponseString) {
        this.metadataResponseString = metadataResponseString;
    }
    public Integer getEmptyNullPercent() {
        return emptyNullPercent;
    }
    public void setEmptyNullPercent(Integer emptyNullPercent) {
        this.emptyNullPercent = emptyNullPercent;
    }
    public Integer getMinHop() {
        return minHop;
    }
    public void setMinHop(Integer minHop) {
        this.minHop = minHop;
    }
    public Integer getMaxHop() {
        return maxHop;
    }
    public void setMaxHop(Integer maxHop) {
        this.maxHop = maxHop;
    }
    public Integer getNodesOneHop() {
        return nodesOneHop;
    }
    public void setNodesOneHop(Integer nodesOneHop) {
        this.nodesOneHop = nodesOneHop;
    }
}
