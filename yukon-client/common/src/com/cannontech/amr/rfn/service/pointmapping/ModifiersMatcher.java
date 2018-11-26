package com.cannontech.amr.rfn.service.pointmapping;

import com.cannontech.common.util.MatchStyle;
import com.google.common.collect.ImmutableSet;

public class ModifiersMatcher {
    private MatchStyle style;
    private ImmutableSet<String> modifiers;

    public ModifiersMatcher(MatchStyle style, Iterable<String> modifiers) {
        this.style = style;
        this.modifiers = ImmutableSet.copyOf(modifiers);
    }

    public MatchStyle getStyle() {
        return style;
    }

    public ImmutableSet<String> getModifiers() {
        return modifiers;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s(%s)", style, modifiers);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((modifiers == null) ? 0 : modifiers.hashCode());
        result = prime * result + ((style == null) ? 0 : style.hashCode());
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
        ModifiersMatcher other = (ModifiersMatcher) obj;
        if (modifiers == null) {
            if (other.modifiers != null)
                return false;
        } else if (!modifiers.equals(other.modifiers))
            return false;
        if (style != other.style)
            return false;
        return true;
    }
}