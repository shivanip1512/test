package com.cannontech.common.constants;

import java.util.ArrayList;
import java.util.List;

public class YukonSelectionList {
    private int listId = 0;
    private String ordering = null;
    private String selectionLabel = null;
    private String whereIsList = null;
    private String listName = null;
    private String userUpdateAvailable = null;
    private Integer energyCompanyId = null;

    private List<YukonListEntry> yukonListEntries = null;

    public static final String TABLE_NAME = "YukonSelectionList";

    public YukonSelectionList() {
        super();
    }

    public static String getTABLE_NAME() {
        return TABLE_NAME;
    }

    public int getListID() {
        return listId;
    }

    public String getListName() {
        return listName;
    }

    public String getOrdering() {
        return ordering;
    }

    public String getSelectionLabel() {
        return selectionLabel;
    }

    public String getUserUpdateAvailable() {
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

    public void setListID(int listId) {
        this.listId = listId;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public void setOrdering(String ordering) {
        this.ordering = ordering;
    }

    public void setSelectionLabel(String selectionLabel) {
        this.selectionLabel = selectionLabel;
    }

    public void setUserUpdateAvailable(String userUpdateAvailable) {
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
