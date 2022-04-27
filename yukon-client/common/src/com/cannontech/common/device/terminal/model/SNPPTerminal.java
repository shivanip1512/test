package com.cannontech.common.device.terminal.model;

import org.apache.commons.lang3.StringUtils;

public class SNPPTerminal extends TerminalBase<com.cannontech.database.data.device.SNPPTerminal> {

    private String pagerNumber;
    private String login = StringUtils.EMPTY;

    public SNPPTerminal() {
        super();
    }

    public String getPagerNumber() {
        return pagerNumber;
    }

    public void setPagerNumber(String pagerNumber) {
        this.pagerNumber = pagerNumber;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public void buildDBPersistent(com.cannontech.database.data.device.SNPPTerminal iEDBase) {
        super.buildDBPersistent(iEDBase);
        iEDBase.getDeviceTapPagingSettings().setPagerNumber(getPagerNumber());
        iEDBase.getDeviceTapPagingSettings().setDeviceID(getId());
        iEDBase.getDeviceTapPagingSettings().setSender(getLogin());
        iEDBase.getDeviceTapPagingSettings().setSecurityCode(getPassword());
    }

    @Override
    public void buildModel(com.cannontech.database.data.device.SNPPTerminal iEDBase) {
        super.buildModel(iEDBase);
        setPagerNumber(iEDBase.getDeviceTapPagingSettings().getPagerNumber());
        setLogin(iEDBase.getDeviceTapPagingSettings().getSender());
        setPassword(iEDBase.getDeviceTapPagingSettings().getSecurityCode());
    }
}