package com.cannontech.web.tools.mapping.model;

import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.i18n.DisplayableEnum;

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
        GOOD(Color.GREEN),
        OK(Color.BLUE),
        WEAK(Color.ORANGE),
        UNVERIFIED(Color.GREY);
        
        private Color color;
        LinkStrength(Color color) {
            this.color = color;
        }
        public Color getColor() {
            return color;
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
