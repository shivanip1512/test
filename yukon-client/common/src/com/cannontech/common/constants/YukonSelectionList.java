package com.cannontech.common.constants;

import java.util.ArrayList;
import java.util.List;

public class YukonSelectionList {
    private int listId = 0;
    private YukonSelectionListOrder ordering = null;
    private String selectionLabel = null;
    private String whereIsList = null;
    private YukonSelectionListEnum type = null;
    private boolean userUpdateAvailable = false;
    private Integer energyCompanyId = null;

    private List<YukonListEntry> yukonListEntries = null;

    public static final String TABLE_NAME = "YukonSelectionList";

    public YukonSelectionList() {
        super();
    }

    public static String getTABLE_NAME() {
        return TABLE_NAME;
    }

    public int getListId() {
        return listId;
    }

    public YukonSelectionListEnum getType() {
        return type;
    }

    public String getListName() {
        return type.getListName();
    }

    public YukonSelectionListOrder getOrdering() {
        return ordering;
    }

    public String getSelectionLabel() {
        return selectionLabel;
    }

    public boolean isUserUpdateAvailable() {
        return userUpdateAvailable;
    }

    public String getWhereIsList() {
        return whereIsList;
    }

    public Integer getEnergyCompanyId() {
        return energyCompanyId;
    }

    public void setEnergyCompanyId(Integer energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public void setType(YukonSelectionListEnum type) {
        this.type = type;
    }

    public void setOrdering(YukonSelectionListOrder ordering) {
        this.ordering = ordering;
    }

    public void setSelectionLabel(String selectionLabel) {
        this.selectionLabel = selectionLabel;
    }

    public void setUserUpdateAvailable(boolean userUpdateAvailable) {
        this.userUpdateAvailable = userUpdateAvailable;
    }

    public void setWhereIsList(String whereIsList) {
        this.whereIsList = whereIsList;
    }

    public List<YukonListEntry> getYukonListEntries() {
        if (yukonListEntries == null)
            yukonListEntries = new ArrayList<YukonListEntry>();
        return yukonListEntries;
    }

    public void setYukonListEntries(List<YukonListEntry> yukonListEntries) {
        this.yukonListEntries = yukonListEntries;
    }
}
