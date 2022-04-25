package com.cannontech.common.device.terminal.model;

public class PagingTapTerminal extends TerminalBase<com.cannontech.database.data.device.PagingTapTerminal> {
    private String pageNumber;

    public PagingTapTerminal() {
        super();
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public void buildDBPersistent(com.cannontech.database.data.device.PagingTapTerminal iEDBase) {
        super.buildDBPersistent(iEDBase);
        iEDBase.getDeviceTapPagingSettings().setPagerNumber(getPageNumber());
        iEDBase.getDeviceTapPagingSettings().setDeviceID(getId());
    }

    @Override
    public void buildModel(com.cannontech.database.data.device.PagingTapTerminal iEDBase) {
        super.buildModel(iEDBase);
        setPageNumber(iEDBase.getDeviceTapPagingSettings().getPagerNumber());
    }
}