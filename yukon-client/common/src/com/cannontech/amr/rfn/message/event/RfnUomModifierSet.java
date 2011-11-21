package com.cannontech.amr.rfn.message.event;

import java.io.Serializable;
import java.util.Set;

public class RfnUomModifierSet implements Serializable {
    private static final long serialVersionUID = 1L;

    private Set<String> uomModifiers;

    public Set<String> getUomModifiers() {
        return uomModifiers;
    }

    public void setUomModifiers(Set<String> uomModifiers) {
        this.uomModifiers = uomModifiers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uomModifiers == null) ? 0 : uomModifiers.hashCode());
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
        RfnUomModifierSet other = (RfnUomModifierSet) obj;
        if (uomModifiers == null) {
            if (other.uomModifiers != null)
                return false;
        } else if (!uomModifiers.equals(other.uomModifiers))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("RfnUOMModifierSet [uomModifiers=%s]", uomModifiers);
    }

}
