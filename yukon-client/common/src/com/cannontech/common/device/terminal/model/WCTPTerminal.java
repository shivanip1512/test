package com.cannontech.common.device.terminal.model;

import org.apache.commons.lang3.StringUtils;

public class WCTPTerminal extends TerminalBase<com.cannontech.database.data.device.WCTPTerminal> {
    private String pagerNumber;
    private String sender;
    private String securityCode;

    public WCTPTerminal() {
        super();
    }

    public String getPagerNumber() {
        return pagerNumber;
    }

    public void setPagerNumber(String pagerNumber) {
        this.pagerNumber = pagerNumber;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    @Override
    public void buildDBPersistent(com.cannontech.database.data.device.WCTPTerminal iEDBase) {
        super.buildDBPersistent(iEDBase);
        iEDBase.getDeviceTapPagingSettings().setPagerNumber(getPagerNumber());
        iEDBase.getDeviceTapPagingSettings().setSender(getSender());
        iEDBase.getDeviceTapPagingSettings().setSecurityCode(getSecurityCode());
        if (StringUtils.isNotBlank(getPassword())) {
            iEDBase.getDeviceTapPagingSettings().setPOSTPath(getPassword());
        } else {
            iEDBase.getDeviceTapPagingSettings().setPOSTPath("/wctp");
        }

    }

    @Override
    public void buildModel(com.cannontech.database.data.device.WCTPTerminal iEDBase) {
        super.buildModel(iEDBase);
        setPagerNumber(iEDBase.getDeviceTapPagingSettings().getPagerNumber());
        setSecurityCode(iEDBase.getDeviceTapPagingSettings().getSecurityCode());
        setSender(iEDBase.getDeviceTapPagingSettings().getSender());
        setPassword(iEDBase.getDeviceTapPagingSettings().getPOSTPath());
    }

}
