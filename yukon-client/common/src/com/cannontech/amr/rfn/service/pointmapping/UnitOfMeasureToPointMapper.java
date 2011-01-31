package com.cannontech.amr.rfn.service.pointmapping;

import java.util.Set;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.MatchStyle;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public interface UnitOfMeasureToPointMapper {
    
    public static class ModifiersMatcher {
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
    }

    public static class PointMapper {
        private String name;
        private String uom;
        private double multiplier = 1.0d;
        private boolean siPrefixParsing = false;
        private ImmutableList<ModifiersMatcher> modifiersMatchers;
        
        public PointMapper(String name, String uom, double multiplier, boolean siPrefixParsing,
                Iterable<ModifiersMatcher> modifiersMatchers) {
            this.name = name;
            this.uom = uom;
            this.multiplier = multiplier;
            this.siPrefixParsing = siPrefixParsing;
            this.modifiersMatchers = ImmutableList.copyOf(modifiersMatchers);
        }
        public String getName() {
            return name;
        }
        public String getUom() {
            return uom;
        }
        public double getMultiplier() {
            return multiplier;
        }
        public boolean isSiPrefixParsing() {
            return siPrefixParsing;
        }
        public ImmutableList<ModifiersMatcher> getModifiersMatchers() {
            return modifiersMatchers;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return String.format("PointMapper [uom=%s, name=%s, multiplier=%s, siPrefixParsing=%s, modifiersMatchers=%s]",
                                 uom,
                                 name,
                                 multiplier,
                                 siPrefixParsing,
                                 modifiersMatchers);
        }
        
    }
    
    public PointValueHandler findMatch(YukonPao pao, String unitOfMeasure,
            Set<String> unitOfMeasureModifiers);

}