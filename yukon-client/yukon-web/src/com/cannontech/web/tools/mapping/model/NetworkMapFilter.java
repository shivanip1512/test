package com.cannontech.web.tools.mapping.model;

import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.rfn.message.neighbor.NeighborData;
import com.google.common.collect.Lists;

public class NetworkMapFilter {

    private List<Integer> selectedGatewayIds;
    private ColorCodeBy colorCodeBy;
    private List<LinkQuality> linkQuality;
    private List<DescendantCount> descendantCount;
    private List<HopCount> hopCount;

    public List<Integer> getSelectedGatewayIds() {
        return selectedGatewayIds;
    }

    public void setSelectedGatewayIds(List<Integer> selectedGatewayIds) {
        this.selectedGatewayIds = selectedGatewayIds;
    }

    public ColorCodeBy getColorCodeBy() {
        return colorCodeBy;
    }

    public void setColorCodeBy(ColorCodeBy colorCodeBy) {
        this.colorCodeBy = colorCodeBy;
    }

    public List<LinkQuality> getLinkQuality() {
        return linkQuality;
    }

    public void setLinkQuality(List<LinkQuality> linkQuality) {
        this.linkQuality = linkQuality;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
    
    public List<DescendantCount> getDescendantCount() {
        return descendantCount;
    }

    public void setDescendantCount(List<DescendantCount> descendantCount) {
        this.descendantCount = descendantCount;
    }

    public List<HopCount> getHopCount() {
        return hopCount;
    }

    public void setHopCount(List<HopCount> hopCount) {
        this.hopCount = hopCount;
    }

    public enum ColorCodeBy implements DisplayableEnum {
        GATEWAY,
        LINK_QUALITY,
        DESCENDANT_COUNT,
        HOP_COUNT;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.operator.comprehensiveMap.colorCodeBy." + this;
        }
    }
    
    public enum Color {
        GREEN("#009933", 1),
        BLUE("#4d90fe", 2),
        ORANGE("#ec971f", 3),
        GREY("#888", 4),
        YELLOW("#f0cb2f", 5),
        TEAL("#00b2a9", 6),
        WINE("#ba5670", 7),
        PURPLE("#b779f4", 8),
        LIGHT_GREEN("#b2c98d", 9),
        LIGHT_ORANGE("#f1b16b", 10);
        
        private Color(String hexColor, int order) {
            this.hexColor = hexColor;
            this.order = order;
        }

        private final String hexColor;
        private final int order;
        
        public String getHexColor() {
            return hexColor;
        }
        
        public int getOrder() {
            return order;
        }
    }
    
    public enum LinkQuality implements DisplayableEnum {
        EXCELLENT(Color.GREEN, Lists.newArrayList((short)1,(short)2)),
        AVERAGE(Color.BLUE, Lists.newArrayList((short)3)),
        BELOW_AVERAGE(Color.ORANGE, Lists.newArrayList((short)4, (short)5, (short)6)),
        EVALUATING(Color.GREY, Lists.newArrayList());
        
        private static int EVALUATING_LIMIT = 50;
        
        private Color color;
        private List<Short> linkCost;
        LinkQuality(Color color, List<Short> linkCost) {
            this.color = color;
            this.linkCost = linkCost;
        }
        public Color getColor() {
            return color;
        }
        public static LinkQuality getLinkQuality(NeighborData neighborData) {
            if(neighborData.getNumSamples() < EVALUATING_LIMIT) {
                return LinkQuality.EVALUATING;
            }
            return Lists.newArrayList(LinkQuality.values())
                    .stream().filter(ls -> ls.linkCost.contains((short)neighborData.getEtxBand()))
                    .findFirst()
                    .orElseThrow(() -> new UnsupportedOperationException("Undefined link cost " + neighborData.getEtxBand()));
        }
        
        @Override
        public String getFormatKey() {
            return "yukon.web.modules.operator.comprehensiveMap.linkQuality." + this;
        }
    }
    
    public enum DescendantCount implements DisplayableEnum {
        EXCELLENT(Color.GREEN),     // <= 40
        GOOD(Color.BLUE),           // 41 - 80
        AVERAGE(Color.ORANGE),      // 81 - 120
        BELOW_AVERAGE(Color.GREY);  // > 120
        
        private Color color;
        
        DescendantCount(Color color) {
            this.color = color;
        }
        public Color getColor() {
            return color;
        }
        
        @Override
        public String getFormatKey() {
            return "yukon.web.modules.operator.comprehensiveMap.descendantCount." + this;
        }
    }
    
    public enum HopCount implements DisplayableEnum {
        REALLY_LOW(),   // 1 - 8
        LOW(),          // 9 - 12
        AVERAGE(),      // 13 - 16
        HIGH(),         // 17 - 20
        REALLY_HIGH();  // > 20
                
        HopCount() {
        }
        
        @Override
        public String getFormatKey() {
            return "yukon.web.modules.operator.comprehensiveMap.hopCount." + this;
        }
    }
    
    public enum HopCountColors {
        ONE(Color.GREEN, HopCount.REALLY_LOW, 1),
        TWO(Color.BLUE, HopCount.REALLY_LOW, 2),         
        THREE(Color.ORANGE, HopCount.REALLY_LOW, 3),      
        FOUR(Color.GREY, HopCount.REALLY_LOW, 4),        
        FIVE(Color.YELLOW, HopCount.REALLY_LOW, 5),
        SIX(Color.TEAL, HopCount.REALLY_LOW, 6),
        SEVEN(Color.WINE, HopCount.REALLY_LOW, 7),
        EIGHT(Color.PURPLE, HopCount.REALLY_LOW, 8),
        NINE(Color.LIGHT_GREEN, HopCount.LOW, 9),
        TEN(Color.LIGHT_ORANGE, HopCount.LOW, 10),
        ELEVEN(Color.GREEN, HopCount.LOW, 11),
        TWELVE(Color.BLUE, HopCount.LOW, 12),
        THIRTEEN(Color.ORANGE, HopCount.AVERAGE, 13),
        FOURTEEN(Color.GREY, HopCount.AVERAGE, 14),
        FIFTEEN(Color.YELLOW, HopCount.AVERAGE, 15),
        SIXTEEN(Color.TEAL, HopCount.AVERAGE, 16),
        SEVENTEEN(Color.WINE, HopCount.HIGH, 17),
        EIGHTEEN(Color.PURPLE, HopCount.HIGH, 18),
        NINETEEN(Color.LIGHT_GREEN, HopCount.HIGH, 19),
        TWENTY(Color.LIGHT_ORANGE, HopCount.HIGH, 20),
        OVER_TWENTY(Color.GREY, HopCount.REALLY_HIGH, 21);
        
        private Color color;
        private HopCount hopCountRange;
        private int number;
                
        HopCountColors(Color color, HopCount hopCountRange, int number) {
            this.color = color;
            this.hopCountRange = hopCountRange;
            this.number = number;
        }
        
        public Color getColor() {
            return color;
        }
        
        public HopCount getHopCountRange() {
            return hopCountRange;
        }
        
        public int getNumber() {
            return number;
        }
    }
    
    public static class Legend {
        private Color color;
        private String text;
        
        public Legend(Color color, String text) {
            this.color = color;
            this.text = text;
        }
        public String getHexColor() {
            return color.getHexColor();
        }
        public String getText() {
            return StringEscapeUtils.escapeHtml4(text);
        }
        public int getOrder() {
            return color.getOrder();
        }
        
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(text);
            builder.append(":");
            builder.append(getHexColor());
            return builder.toString();
        }
    }
}
