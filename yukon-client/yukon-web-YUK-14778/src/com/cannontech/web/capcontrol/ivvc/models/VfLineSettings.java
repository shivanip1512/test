package com.cannontech.web.capcontrol.ivvc.models;

public class VfLineSettings {
    
    private String color;
    private String lineAlpha = null;
    private boolean minMax = true;
    private boolean showBullet = true;
    private boolean showBalloon = true;
    private boolean visibleInLegend = false;
    private boolean selected = true;
    private boolean verticalLines = false;
    private String dataLabel = null;
    
    public VfLineSettings(String color, boolean minMax, 
                          boolean showBullet, boolean showBalloon, boolean visibleInLegend, 
                          boolean selected) {
        this(color, null, minMax, showBullet, showBalloon, visibleInLegend, selected, false, null);
    }
    
    public VfLineSettings(String color, String lineAlpha, boolean minMax, 
                          boolean showBullet, boolean showBalloon, boolean visibleInLegend, 
                          boolean selected) {
        this(color, lineAlpha, minMax, showBullet, showBalloon, visibleInLegend, selected, false, null);
    }
    
    public VfLineSettings(String color, String lineAlpha, boolean minMax, 
                          boolean showBullet, boolean showBalloon, boolean visibleInLegend, 
                          boolean selected, boolean verticalLines, String dataLabel) {
        this.color = color;
        this.lineAlpha = lineAlpha;
        this.minMax = minMax;
        this.showBullet = showBullet;
        this.showBalloon = showBalloon;
        this.visibleInLegend = visibleInLegend;
        this.selected = selected;
        this.verticalLines = verticalLines;
        this.dataLabel = dataLabel;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }

	public String getLineAlpha() {
        return lineAlpha;
    }

    public void setLineAlpha(String lineAlpha) {
        this.lineAlpha = lineAlpha;
    }

    public boolean isMinMax() {
		return minMax;
	}

	public void setMinMax(boolean minMax) {
		this.minMax = minMax;
	}

	public boolean isShowBullet() {
		return showBullet;
	}

	public void setShowBullet(boolean showBullet) {
		this.showBullet = showBullet;
	}

	public boolean isShowBalloon() {
		return showBalloon;
	}

	public void setShowBalloon(boolean showBalloon) {
		this.showBalloon = showBalloon;
	}

	public boolean isVisibleInLegend() {
		return visibleInLegend;
	}

	public void setVisibleInLegend(boolean visibleInLegend) {
		this.visibleInLegend = visibleInLegend;
	}

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isVerticalLines() {
        return verticalLines;
    }

    public void setVerticalLines(boolean verticalLines) {
        this.verticalLines = verticalLines;
    }

    public String getDataLabel() {
        return dataLabel;
    }

    public void setDataLabel(String dataLabel) {
        this.dataLabel = dataLabel;
    }
}
