package com.cannontech.common.pao.definition.model;

import com.cannontech.common.pao.PaoIdentifier;

public class PaoPointTemplate {

	public PaoPointTemplate(PaoIdentifier paoIdentifier,
            PointTemplate pointTemplate) {
        super();
		this.paoIdentifier = paoIdentifier;
        this.pointTemplate = pointTemplate;
    }
    private PaoIdentifier paoIdentifier;
    private PointTemplate pointTemplate;
    
    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
		this.paoIdentifier = paoIdentifier;
	}
    public PaoIdentifier getPaoIdentifier() {
		return paoIdentifier;
	}
    public PointTemplate getPointTemplate() {
        return pointTemplate;
    }
    public void setPointTemplate(PointTemplate pointTemplate) {
        this.pointTemplate = pointTemplate;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pointTemplate == null) ? 0
                : pointTemplate.hashCode());
        result = prime * result + ((paoIdentifier == null) ? 0
                : paoIdentifier.hashCode());
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
        PaoPointTemplate other = (PaoPointTemplate) obj;
        if (pointTemplate == null) {
            if (other.pointTemplate != null)
                return false;
        } else if (!pointTemplate.equals(other.pointTemplate))
            return false;
        if (paoIdentifier == null) {
            if (other.paoIdentifier != null)
                return false;
        } else if (!paoIdentifier.equals(other.paoIdentifier))
            return false;
        return true;
    }
    
    
    
}
