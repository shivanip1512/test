package com.cannontech.web.capcontrol.ivvc.models;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.util.LazyList;
import com.cannontech.common.util.SimpleSupplier;

public class ZoneDto {
    
    private String name;
    private Integer zoneId = null;
    private Integer parentZoneId = null;
    private int substationBusId = -1;
    private int regulatorId = -1;
    private double graphStartPosition;
    
    private List<ZoneAssignmentCapBankRow> bankAssignments = new LazyList<ZoneAssignmentCapBankRow>(new ArrayList<ZoneAssignmentCapBankRow>(), 
			new SimpleSupplier<ZoneAssignmentCapBankRow>(ZoneAssignmentCapBankRow.class));
    
    private List<ZoneAssignmentPointRow> pointAssignments = new LazyList<ZoneAssignmentPointRow>(new ArrayList<ZoneAssignmentPointRow>(), 
            										new SimpleSupplier<ZoneAssignmentPointRow>(ZoneAssignmentPointRow.class));

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentZoneId() {
        return parentZoneId;
    }

    public void setParentZoneId(Integer parentZoneId) {
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

	public double getGraphStartPosition() {
		return graphStartPosition;
	}

	public void setGraphStartPosition(double graphStartPosition) {
		this.graphStartPosition = graphStartPosition;
	}

	public List<ZoneAssignmentCapBankRow> getBankAssignments() {
		return bankAssignments;
	}

	public void setBankAssignments(List<ZoneAssignmentCapBankRow> bankAssignments) {
		this.bankAssignments = bankAssignments;
	}

	public List<ZoneAssignmentPointRow> getPointAssignments() {
		return pointAssignments;
	}

	public void setPointAssignments(List<ZoneAssignmentPointRow> pointAssignments) {
		this.pointAssignments = pointAssignments;
	}
}
