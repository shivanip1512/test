package com.cannontech.common.constants;

public class DisplayableSelectionList {
    private YukonSelectionList proxy;
    private boolean isInherited;

    public DisplayableSelectionList(YukonSelectionList proxy, boolean isInherited) {
        this.proxy = proxy;
        this.isInherited = isInherited;
    }

    public int getListId() {
        return proxy.getListId();
    }

    public YukonSelectionListEnum getType() {
        return proxy.getType();
    }

    public String getListName() {
        return proxy.getListName();
    }

    public Integer getEnergyCompanyId() {
        return proxy.getEnergyCompanyId();
    }

    public YukonSelectionList getProxy() {
        return proxy;
    }

    public void setProxy(YukonSelectionList proxy) {
        this.proxy = proxy;
    }

    public boolean isInherited() {
        return isInherited;
    }

    public void setInherited(boolean isInherited) {
        this.isInherited = isInherited;
    }
}
