package com.cannontech.common.device.terminal.model;

public class PagingTapTerminal extends TerminalBase<com.cannontech.database.data.device.PagingTapTerminal> {
    private String pagerNumber;

    public PagingTapTerminal() {
        super();
    }

    public String getPagerNumber() {
        return pagerNumber;
    }

    public void setPagerNumber(String pagerNumber) {
        this.pagerNumber = pagerNumber;
    }

    @Override
    public void buildDBPersistent(com.cannontech.database.data.device.PagingTapTerminal iEDBase) {
        super.buildDBPersistent(iEDBase);
        iEDBase.getDeviceTapPagingSettings().setPagerNumber(getPagerNumber());
        iEDBase.getDeviceTapPagingSettings().setDeviceID(getId());
    }

    @Override
    public void buildModel(com.cannontech.database.data.device.PagingTapTerminal iEDBase) {
        super.buildModel(iEDBase);
        setPagerNumber(iEDBase.getDeviceTapPagingSettings().getPagerNumber());
    }
}