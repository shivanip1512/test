package com.cannontech.common.rfn.simulation;

import java.io.Serializable;

import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResultType;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponseType;
import com.cannontech.common.rfn.message.neighbor.NeighborData;
import com.cannontech.common.rfn.message.route.RouteData;

public class SimulatedNmMappingSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private NeighborData neighborData;
    private RouteData routeData;
    private RfnMetadataMultiResponseType metadataResponseType;
    private RfnMetadataMultiQueryResultType metadataQueryResponseType;
    private String metadataResponseString;
    private Integer emptyNullPercent;
    private Integer maxHop;
    private Integer nodesOneHop;
    private Integer numberOfDevicesPerGateway;
    private Boolean createGateways;
    
    public NeighborData getNeighborData() {
        return neighborData;
    }
    public void setNeighborData(NeighborData neighborData) {
        this.neighborData = neighborData;
    }
    public RouteData getRouteData() {
        return routeData;
    }
    public void setRouteData(RouteData routeData) {
        this.routeData = routeData;
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
    public Integer getNumberOfDevicesPerGateway() {
        return numberOfDevicesPerGateway;
    }
    public void setNumberOfDevicesPerGateway(Integer numberOfDevicesPerGateway) {
        this.numberOfDevicesPerGateway = numberOfDevicesPerGateway;
    }
    public Boolean getCreateGateways() {
        return createGateways;
    }
    public void setCreateGateways(Boolean createGateways) {
        this.createGateways = createGateways;
    }
}
