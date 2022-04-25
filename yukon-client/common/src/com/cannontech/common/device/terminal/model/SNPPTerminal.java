package com.cannontech.common.device.terminal.model;

import org.apache.commons.lang3.StringUtils;

public class SNPPTerminal extends TerminalBase<com.cannontech.database.data.device.SNPPTerminal> {

    private String pageNumber;
    private String login = StringUtils.EMPTY;

    public SNPPTerminal() {
        super();
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
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
        iEDBase.getDeviceTapPagingSettings().setPagerNumber(getPageNumber());
        iEDBase.getDeviceTapPagingSettings().setDeviceID(getId());
        iEDBase.getDeviceTapPagingSettings().setSender(getLogin());
        iEDBase.getDeviceTapPagingSettings().setSecurityCode(getPassword());
    }

    @Override
    public void buildModel(com.cannontech.database.data.device.SNPPTerminal iEDBase) {
        super.buildModel(iEDBase);
        setPageNumber(iEDBase.getDeviceTapPagingSettings().getPagerNumber());
        setLogin(iEDBase.getDeviceTapPagingSettings().getSender());
        setPassword(iEDBase.getDeviceTapPagingSettings().getSecurityCode());
    }
}