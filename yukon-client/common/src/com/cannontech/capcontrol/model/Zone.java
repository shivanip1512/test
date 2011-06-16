package com.cannontech.capcontrol.model;

import java.util.List;

import com.cannontech.database.data.pao.ZoneType;

public class Zone {

    private Integer id;
    private String name;
    private List<RegulatorToZoneMapping> regulators;
    private int substationBusId;
    private Integer parentId;
    private double graphStartPosition;
    private ZoneType zoneType = ZoneType.GANG_OPERATED;
    
    public Zone () {
        
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RegulatorToZoneMapping> getRegulators() {
        return regulators;
    }

    public void setRegulators(List<RegulatorToZoneMapping> regulators) {
        this.regulators = regulators;
    }

    public int getSubstationBusId() {
        return substationBusId;
    }

    public void setSubstationBusId(int substationBusId) {
        this.substationBusId = substationBusId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

	public double getGraphStartPosition() {
		return graphStartPosition;
	}

	public void setGraphStartPosition(double graphStartPosition) {
		this.graphStartPosition = graphStartPosition;
	}

    public ZoneType getZoneType() {
        return zoneType;
    }

    public void setZoneType(ZoneType zoneType) {
        this.zoneType = zoneType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(graphStartPosition);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
        result = prime * result + ((regulators == null) ? 0 : regulators.hashCode());
        result = prime * result + substationBusId;
        result = prime * result + ((zoneType == null) ? 0 : zoneType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Zone other = (Zone) obj;
        if (Double.doubleToLongBits(graphStartPosition) != Double
            .doubleToLongBits(other.graphStartPosition))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (parentId == null) {
            if (other.parentId != null)
                return false;
        } else if (!parentId.equals(other.parentId))
            return false;
        if (regulators == null) {
            if (other.regulators != null)
                return false;
        } else if (!regulators.equals(other.regulators))
            return false;
        if (substationBusId != other.substationBusId)
            return false;
        if (zoneType != other.zoneType)
            return false;
        return true;
    }

}
