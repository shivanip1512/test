package com.cannontech.capcontrol.model;

import java.util.List;
import java.util.Map;

import com.cannontech.common.util.LazyList;
import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.enums.Phase;

public abstract class AbstractZone {
    
    private String name;
    private Integer zoneId;
    private Integer parentId;
    private int substationBusId = -1;
    private double graphStartPosition;
    
    private List<ZoneAssignmentCapBankRow> bankAssignments = LazyList.ofInstance(ZoneAssignmentCapBankRow.class);
    private List<ZoneAssignmentPointRow> pointAssignments = LazyList.ofInstance(ZoneAssignmentPointRow.class);
    
    public AbstractZone() {
        super();
    }
    
    public AbstractZone(Zone zone) {
        this.name = zone.getName();
        this.zoneId = zone.getId();
        this.parentId = zone.getParentId();
        this.substationBusId = zone.getSubstationBusId();
        this.graphStartPosition = zone.getGraphStartPosition();
    }
    
    public static AbstractZone create(ZoneType zoneType) {
        AbstractZone zoneDto = null;
        if (zoneType == ZoneType.GANG_OPERATED) {
            zoneDto = new ZoneGang();
        } else if (zoneType == ZoneType.SINGLE_PHASE) {
            zoneDto = new ZoneSinglePhase();
        } else if (zoneType == ZoneType.THREE_PHASE) {
            zoneDto = new ZoneThreePhase();
        } else {
            throw new RuntimeException("ZoneType unknown.");
        }
        return zoneDto;
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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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
    public abstract List<RegulatorToZoneMapping> getRegulatorsList();
    public abstract Map<Phase, RegulatorToZoneMapping> getRegulators();
}
