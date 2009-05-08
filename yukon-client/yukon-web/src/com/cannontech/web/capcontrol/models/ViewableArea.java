package com.cannontech.web.capcontrol.models;

import java.util.List;

import com.cannontech.yukon.cbc.StreamableCapObject;

public class ViewableArea {

	private StreamableCapObject area = null;
	private List<ViewableSubStation> subStations = null;	
	
	public ViewableArea() {
		
	}

	public StreamableCapObject getArea() {
		return area;
	}

	public void setArea(StreamableCapObject area) {
		this.area = area;
	}

	public List<ViewableSubStation> getSubStations() {
		return subStations;
	}

	public void setSubStations(List<ViewableSubStation> subStations) {
		this.subStations = subStations;
	}
}
