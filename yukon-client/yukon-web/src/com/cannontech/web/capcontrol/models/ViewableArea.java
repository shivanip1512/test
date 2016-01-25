package com.cannontech.web.capcontrol.models;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.message.capcontrol.streamable.StreamableCapObject;

public class ViewableArea {

    private int ccId;
    private String ccName;
    private int stationCount;
    private List<ViewableSubBus> subBusList = new ArrayList<ViewableSubBus>();
    private List<ViewableFeeder> feederList = new ArrayList<ViewableFeeder>();
    private List<ViewableCapBank> capBankList = new ArrayList<ViewableCapBank>();
    

    public void setAreaInfo(StreamableCapObject area) {
        ccId = area.getCcId();
        ccName = area.getCcName();
    }

    public final int getCcId() {
        return ccId;
    }

    public final String getCcName() {
        return ccName;
    }

    public int getStationCount() {
        return stationCount;
    }

    public void setStationCount(int stationCount) {
        this.stationCount = stationCount;
    }

    public List<ViewableSubBus> getSubBusList() {
        return subBusList;
    }

    public void setSubBusList(List<ViewableSubBus> subBusList) {
        this.subBusList = subBusList;
    }

    public List<ViewableFeeder> getFeederList() {
        return feederList;
    }

    public void setFeederList(List<ViewableFeeder> feederList) {
        this.feederList = feederList;
    }

    public List<ViewableCapBank> getCapBankList() {
        return capBankList;
    }

    public void setCapBankList(List<ViewableCapBank> capBankList) {
        this.capBankList = capBankList;
    }
}