package com.cannontech.common.device.terminal.model;

import com.cannontech.database.db.device.DeviceTNPPSettings;

public class TNPPTerminal extends TerminalBase<com.cannontech.database.data.device.TNPPTerminal> {
    private Integer originAddress;
    private Integer destinationAddress;
    private Integer inertia;
    private Protocol protocol;
    private DataFormat dataFormat;
    private IdentifierFormat identifierFormat;
    private String pagerId;
    private Integer channel;
    private Integer functionCode;
    private Integer zone;

    public TNPPTerminal() {
        super();
    }

    public Integer getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(Integer originAddress) {
        this.originAddress = originAddress;
    }

    public Integer getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(Integer destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public Integer getInertia() {
        return inertia;
    }

    public void setInertia(Integer inertia) {
        this.inertia = inertia;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public DataFormat getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(DataFormat dataFormat) {
        this.dataFormat = dataFormat;
    }

    public IdentifierFormat getIdentifierFormat() {
        return identifierFormat;
    }

    public void setIdentifierFormat(IdentifierFormat identifierFormat) {
        this.identifierFormat = identifierFormat;
    }

    public String getPagerId() {
        return pagerId;
    }

    public void setPagerId(String pagerId) {
        this.pagerId = pagerId;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Integer getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(Integer functionCode) {
        this.functionCode = functionCode;
    }

    public Integer getZone() {
        return zone;
    }

    public void setZone(Integer zone) {
        this.zone = zone;
    }

    @Override
    public void buildDBPersistent(com.cannontech.database.data.device.TNPPTerminal iEDBase) {
        super.buildDBPersistent(iEDBase);
        iEDBase.getDeviceTNPPSettings().setInertia(getInertia());
        iEDBase.getDeviceTNPPSettings().setDestinationAddress(getDestinationAddress());
        iEDBase.getDeviceTNPPSettings().setOriginAddress(getOriginAddress());
        iEDBase.getDeviceTNPPSettings().setIdentifierFormat(getIdentifierFormat().getDbChar());
        iEDBase.getDeviceTNPPSettings().setProtocol(getProtocol().getDbChar());
        iEDBase.getDeviceTNPPSettings().setDataFormat(getDataFormat().getDbChar());
        iEDBase.getDeviceTNPPSettings().setChannel(DeviceTNPPSettings.addMaskAndConvertToChar(getChannel()));
        iEDBase.getDeviceTNPPSettings().setZone(DeviceTNPPSettings.addMaskAndConvertToChar(getZone()));
        iEDBase.getDeviceTNPPSettings().setFunctionCode(DeviceTNPPSettings.addMaskAndConvertToChar(getFunctionCode()));
        iEDBase.getDeviceTNPPSettings().setPagerId(getPagerId());
    }

    @Override
    public void buildModel(com.cannontech.database.data.device.TNPPTerminal iEDBase) {
        super.buildModel(iEDBase);
        setInertia(iEDBase.getDeviceTNPPSettings().getInertia());
        setDestinationAddress(iEDBase.getDeviceTNPPSettings().getDestinationAddress());
        setOriginAddress(iEDBase.getDeviceTNPPSettings().getOriginAddress());
        setIdentifierFormat(IdentifierFormat.getIdentifierFormatForDbChar(iEDBase.getDeviceTNPPSettings().getIdentifierFormat()));
        setProtocol(Protocol.getProtocolMapForDbChar(iEDBase.getDeviceTNPPSettings().getProtocol()));
        setDataFormat(DataFormat.getDataFormatForDbChar(iEDBase.getDeviceTNPPSettings().getDataFormat()));
        setChannel(DeviceTNPPSettings.convertToIntAndRemoveMask(iEDBase.getDeviceTNPPSettings().getChannel()));
        setZone(DeviceTNPPSettings.convertToIntAndRemoveMask(iEDBase.getDeviceTNPPSettings().getZone()));
        setFunctionCode(DeviceTNPPSettings.convertToIntAndRemoveMask(iEDBase.getDeviceTNPPSettings().getFunctionCode()));
        setPagerId(iEDBase.getDeviceTNPPSettings().getPagerId());
    }

}
