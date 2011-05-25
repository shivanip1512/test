package com.cannontech.capcontrol.model;

import java.util.List;

import com.cannontech.common.util.LazyList;
import com.cannontech.database.data.pao.ZoneType;

public abstract class ZoneDto {
    
    private String name;
    private Integer zoneId;
    private Integer parentZoneId;
    private int substationBusId = -1;
    private double graphStartPosition;
    
    private List<ZoneAssignmentCapBankRow> bankAssignments = LazyList.ofInstance(ZoneAssignmentCapBankRow.class);
    private List<ZoneAssignmentPointRow> pointAssignments = LazyList.ofInstance(ZoneAssignmentPointRow.class);
    
    public ZoneDto() {
        super();
    }
    
    public ZoneDto(Zone zone) {
        this.name = zone.getName();
        this.zoneId = zone.getId();
        this.parentZoneId = zone.getParentId();
        this.substationBusId = zone.getSubstationBusId();
        this.graphStartPosition = zone.getGraphStartPosition();
    }
    
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

    public abstract ZoneType getZoneType();

    public abstract ZoneRegulator getRegulator();

    public abstract void setRegulator(ZoneRegulator regulator);

    public abstract List<ZoneRegulator> getRegulators();

    public abstract void setRegulators(List<ZoneRegulator> regulators);

    public abstract ZoneRegulator getRegulatorA();

    public abstract void setRegulatorA(ZoneRegulator regulatorA);

    public abstract ZoneRegulator getRegulatorB();

    public abstract void setRegulatorB(ZoneRegulator regulatorB);

    public abstract ZoneRegulator getRegulatorC();

    public abstract void setRegulatorC(ZoneRegulator regulatorC);
}
