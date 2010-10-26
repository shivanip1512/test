package com.cannontech.web.capcontrol.ivvc.models;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.util.LazyList;
import com.cannontech.common.util.SimpleSupplier;

public class ZoneDto {
    
    private String name;
    private int zoneId = -1;
    private int parentZoneId = -1;
    private int substationBusId = -1;
    private int regulatorId = -1;
    
    private List<ZoneAssignmentRow> bankAssignments = new LazyList<ZoneAssignmentRow>(new ArrayList<ZoneAssignmentRow>(), 
			new SimpleSupplier<ZoneAssignmentRow>(ZoneAssignmentRow.class));
    
    private List<ZoneAssignmentRow> pointAssignments = new LazyList<ZoneAssignmentRow>(new ArrayList<ZoneAssignmentRow>(), 
            										new SimpleSupplier<ZoneAssignmentRow>(ZoneAssignmentRow.class));

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentZoneId() {
        return parentZoneId;
    }

    public void setParentZoneId(int parentZoneId) {
        this.parentZoneId = parentZoneId;
    }

    public int getSubstationBusId() {
        return substationBusId;
    }

    public void setSubstationBusId(int substationBusId) {
        this.substationBusId = substationBusId;
    }

    public int getRegulatorId() {
        return regulatorId;
    }

    public void setRegulatorId(int regulatorId) {
        this.regulatorId = regulatorId;
    }

	public List<ZoneAssignmentRow> getBankAssignments() {
		return bankAssignments;
	}

	public void setBankAssignments(List<ZoneAssignmentRow> bankAssignments) {
		this.bankAssignments = bankAssignments;
	}

	public List<ZoneAssignmentRow> getPointAssignments() {
		return pointAssignments;
	}

	public void setPointAssignments(List<ZoneAssignmentRow> pointAssignments) {
		this.pointAssignments = pointAssignments;
	}
}
