package com.cannontech.web.tools.mapping.model;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;

public class NetworkMapFilter {
    
    public enum ColorCodeBy {
        GATEWAY,
        LINK_STENGTH,
    }
    
    public enum Color {
        GREEN,
        BLUE,
        RED,
        GREY
    }
    
    public enum LinkStrength {
        GOOD(Color.GREEN),
        OK(Color.BLUE),
        WEAK(Color.RED),
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
        private Color color;
        private String text;
        
        public Legend(Color color, String text) {
            this.color = color;
            this.text = text;
        }
        public Color getColor() {
            return color;
        }
        public String getText() {
            return text;
        }
    }
    
    private List<PaoIdentifier> selectedGatewayIds;
    private ColorCodeBy colorCodeBy;
    private List<LinkStrength> linkStrength;

    public List<PaoIdentifier> getSelectedGatewayIds() {
        return selectedGatewayIds;
    }

    public void setSelectedGatewayIds(List<PaoIdentifier> selectedGatewayIds) {
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
}
