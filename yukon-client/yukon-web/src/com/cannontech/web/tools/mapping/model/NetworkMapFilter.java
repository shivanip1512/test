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
    
    public enum ColorCodeBy implements DisplayableEnum {
        GATEWAY,
        LINK_QUALITY,;

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
        YELLOW("#f0cb2f", 5);
        
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
