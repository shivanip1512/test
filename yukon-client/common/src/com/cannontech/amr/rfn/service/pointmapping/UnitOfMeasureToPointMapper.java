package com.cannontech.amr.rfn.service.pointmapping;

import com.cannontech.amr.rfn.message.read.ChannelData;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.MatchStyle;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

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
        
        /*
         * Both baseOum and baseModifiersMatchers will be null if this point mapper is 
         * NOT for mapping to a point of coinicidental measurements.
         * Example: the var measurement at a peak demand event, the baseOum and baseModifiersMatchers
         * will be those of the peak demand channel data. 
         */
        private String baseUom;
        private ImmutableList<ModifiersMatcher> baseModifiersMatchers;
        
        public PointMapper(String name, 
                           String uom, 
                           double multiplier, 
                           boolean siPrefixParsing,
                           Iterable<ModifiersMatcher> modifiersMatchers, 
                           String baseUom, 
                           Iterable<ModifiersMatcher> baseModifiersMatchers) {
            
            this.name = name;
            this.uom = uom;
            this.multiplier = multiplier;
            this.siPrefixParsing = siPrefixParsing;
            this.modifiersMatchers = ImmutableList.copyOf(modifiersMatchers);
            this.baseUom = baseUom;
            if (baseModifiersMatchers == null) {
                this.baseModifiersMatchers = ImmutableList.of();
            } else {
                this.baseModifiersMatchers = ImmutableList.copyOf(baseModifiersMatchers);
            }
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
        public String getBaseUom() {
            return baseUom;
        }
        
        /*
         * Will never be null. If there are no base modifier matchers this will be an empty ImmutableList
         */
        public ImmutableList<ModifiersMatcher> getBaseModifiersMatchers() {
            return baseModifiersMatchers;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return String.format("PointMapper [uom=%s, name=%s, multiplier=%s, siPrefixParsing=%s, modifiersMatchers=%s, baseUom=%s, baseModifiersMatchers=%s]",
                                 uom,
                                 name,
                                 multiplier,
                                 siPrefixParsing,
                                 modifiersMatchers,
                                 baseUom,
                                 baseModifiersMatchers);
        }
        
    }
    
    public <T extends ChannelData> PointValueHandler findMatch(YukonPao pao, T channelData);

    Multimap<PaoType, PointMapper> getPointMapper();
}