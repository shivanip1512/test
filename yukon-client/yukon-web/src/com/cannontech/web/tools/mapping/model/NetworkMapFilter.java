package com.cannontech.web.tools.mapping.model;

import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.rfn.message.network.NeighborData;
import com.google.common.collect.Lists;

public class NetworkMapFilter {

    private List<Integer> selectedGatewayIds;
    private ColorCodeBy colorCodeBy;
    private List<LinkStrength> linkStrength;

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

    public List<LinkStrength> getLinkStrength() {
        return linkStrength;
    }

    public void setLinkStrength(List<LinkStrength> linkStrength) {
        this.linkStrength = linkStrength;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
    
    public enum ColorCodeBy implements DisplayableEnum {
        GATEWAY,
        LINK_STRENGTH,;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.operator.comprehensiveMap.colorCodeBy." + this;
        }
    }
    
    public enum Color {
        GREEN("#009933"),
        BLUE("#4d90fe"),
        ORANGE("#ec971f"),
        GREY("#888"),
        YELLOW("#f0cb2f");
        
        private Color(String hexColor) {
            this.hexColor = hexColor;
        }

        private final String hexColor;
        
        public String getHexColor() {
            return hexColor;
        }
    }
    
    public enum LinkStrength {
        GOOD(Color.GREEN, Lists.newArrayList(1f,2f)),
        OK(Color.BLUE, Lists.newArrayList(3f)),
        WEAK(Color.ORANGE, Lists.newArrayList(4f,5f)),
        UNVERIFIED(Color.GREY, Lists.newArrayList());
        
        private static int UNVERIFIED_LIMIT = 50;
        
        private Color color;
        private List<Float> linkCost;
        LinkStrength(Color color, List<Float> linkCost) {
            this.color = color;
            this.linkCost = linkCost;
        }
        public Color getColor() {
            return color;
        }
        public static LinkStrength getLinkStrength(NeighborData neighborData) {
            if(neighborData.getNumSamples() < UNVERIFIED_LIMIT) {
                return LinkStrength.UNVERIFIED;
            }
            return Lists.newArrayList(LinkStrength.values())
                    .stream().filter(ls -> ls.linkCost.contains(neighborData.getNeighborLinkCost()))
                    .findFirst()
                    .orElseThrow(() -> new UnsupportedOperationException("Undefined link cost " + neighborData.getNeighborLinkCost()));
        }
    }
    
    public static class Legend {
        private String hexColor;
        private String text;
        
        public Legend(String hexColor, String text) {
            this.hexColor = hexColor;
            this.text = text;
        }
        public String getHexColor() {
            return hexColor;
        }
        public String getText() {
            return StringEscapeUtils.escapeHtml4(text);
        }
        
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(text);
            builder.append(":");
            builder.append(hexColor);
            return builder.toString();
        }
    }
}
