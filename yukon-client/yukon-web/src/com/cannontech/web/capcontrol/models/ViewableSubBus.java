package com.cannontech.web.capcontrol.models;


import java.util.List;

import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.yukon.cbc.SubBus;

public class ViewableSubBus {

	private SubBus subBus = null;
	private LitePoint varPoint = null;
	private LitePoint voltPoint = null;
	private LitePoint wattPoint = null;
	private int alternateStationId;
	private int alternateAreaId;
	private List<ViewableFeeder> feeders = null;
	private Integer ltcId;
	private String ltcName = null;
	
	public SubBus getSubBus() {
		return this.subBus;
	}
	
	public void setSubBus(SubBus subBus) {
		this.subBus = subBus;
	}

	public LitePoint getVarPoint() {
		return varPoint;
	}

	public void setVarPoint(LitePoint varPoint) {
		this.varPoint = varPoint;
	}

	public LitePoint getVoltPoint() {
		return voltPoint;
	}

	public void setVoltPoint(LitePoint voltPoint) {
		this.voltPoint = voltPoint;
	}

	public LitePoint getWattPoint() {
		return wattPoint;
	}

	public void setWattPoint(LitePoint wattPoint) {
		this.wattPoint = wattPoint;
	}
	
    public List<ViewableFeeder> getFeeders() {
        return feeders;
    }

    public void setFeeders(List<ViewableFeeder> feeders) {
        this.feeders = feeders;
    }

    public int getAlternateStationId() {
        return alternateStationId;
    }

    public void setAlternateStationId(int alternateStationId) {
        this.alternateStationId = alternateStationId;
    }

    public int getAlternateAreaId() {
        return alternateAreaId;
    }

    public void setAlternateAreaId(int alternateAreaId) {
        this.alternateAreaId = alternateAreaId;
    }

    public Integer getLtcId() {
        return ltcId;
    }

    public void setLtcId(int ltcId) {
        this.ltcId = ltcId;
    }

    public String getLtcName() {
        return ltcName;
    }

    public void setLtcName(String ltcName) {
        this.ltcName = ltcName;
    }
    
}