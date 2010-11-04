package com.cannontech.capcontrol.model;

public class Zone {

    private Integer id = null;//Can be null
    private String name;
    private int regulatorId;
    private int substationBusId;
    private Integer parentId;//Can be null
    private double graphStartPosition;
    
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

    public int getRegulatorId() {
        return regulatorId;
    }

    public void setRegulatorId(int regulatorId) {
        this.regulatorId = regulatorId;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(graphStartPosition);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result + regulatorId;
		result = prime * result + substationBusId;
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
		if (regulatorId != other.regulatorId)
			return false;
		if (substationBusId != other.substationBusId)
			return false;
		return true;
	}
}
