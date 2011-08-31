package com.cannontech.capcontrol.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.CtiUtilities;

public class Area implements YukonPao {

	private String name;
	private String description = CtiUtilities.STRING_NONE;
	
	private PaoIdentifier paoIdentifier;
	private int voltReductionPointId = 0;
    private boolean disabled = false;
	
	public Area(PaoIdentifier paoIdentifier) {
		this.paoIdentifier = paoIdentifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean getDisabled() {
        return disabled;
    }
    
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

	public int getId() {
		return paoIdentifier.getPaoId();
	}

	public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
		this.paoIdentifier = paoIdentifier;
	}

	public int getVoltReductionPointId() {
		return voltReductionPointId;
	}

	public void setVoltReductionPointId(int voltReductionPointId) {
		this.voltReductionPointId = voltReductionPointId;
	}
	
	@Override
	public PaoIdentifier getPaoIdentifier() {
		return paoIdentifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (disabled ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
		result = prime * result + voltReductionPointId;
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
		Area other = (Area) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (disabled != other.disabled)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (paoIdentifier == null) {
			if (other.paoIdentifier != null)
				return false;
		} else if (!paoIdentifier.equals(other.paoIdentifier))
			return false;
		if (voltReductionPointId != other.voltReductionPointId)
			return false;
		return true;
	}
}
