package com.cannontech.development.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.rfn.message.node.NodeConnectionState;
import com.cannontech.common.rfn.model.RfnManufacturerModel;

public class CellularTestCommArchive {

    private String serialNumber;
    private RfnManufacturerModel manufacturerModel;
    private NodeConnectionState nodeConnectionState;
    private int rssi;
    private int rsrp;
    private int rsrq;
    private int sinr;

    public RfnManufacturerModel getManufacturerModel() {
        return manufacturerModel;
    }

    public void setManufacturerModel(RfnManufacturerModel manufacturerModel) {
        this.manufacturerModel = manufacturerModel;
    }
    
    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getRsrp() {
        return rsrp;
    }

    public void setRsrp(int rsrp) {
        this.rsrp = rsrp;
    }

    public int getRsrq() {
        return rsrq;
    }

    public void setRsrq(int rsrq) {
        this.rsrq = rsrq;
    }

    public int getSinr() {
        return sinr;
    }

    public void setSinr(int sinr) {
        this.sinr = sinr;
    }

    public NodeConnectionState getNodeConnectionState() {
        return nodeConnectionState;
    }

    public void setNodeConnectionState(NodeConnectionState nodeConnectionState) {
        this.nodeConnectionState = nodeConnectionState;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
