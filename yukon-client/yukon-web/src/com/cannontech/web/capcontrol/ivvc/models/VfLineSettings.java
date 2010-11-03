package com.cannontech.web.capcontrol.ivvc.models;

public class VfLineSettings {
    
    private String color;
    private int fillAlpha = 0;
    private String bullet = null;
    private boolean minMax = true;
    private boolean showBullet = true;
    private boolean showBalloon = true;
    private boolean visibleInLegend = false;
    
    public VfLineSettings(String color, int fillAlpha, String bullet, boolean minMax, boolean showBullet, boolean showBalloon, boolean visibleInLegend) {
        this.color = color;
        this.fillAlpha = fillAlpha;
        this.bullet = bullet;
        this.minMax = minMax;
        this.showBullet = showBullet;
        this.showBalloon = showBalloon;
        this.visibleInLegend = visibleInLegend;
    }
    
    public String getColor() {
        return color;
    }
    
    /**
     * Hex value for the color of the line and fillAlpha
     * @param color
     */
    public void setColor(String color) {
        this.color = color;
    }

    public int getFillAlpha() {
        return fillAlpha;
    }

    /**
     * Fill Alpha is the intensity of the color used to fill.
     * 0 - 100. 0 is no fill, 100 is solid.
     * 
     * Uses the color set in this object.
     * @param fillAlpha
     */
    public void setFillAlpha(int fillAlpha) {
        this.fillAlpha = fillAlpha;
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

	public String getBullet() {
		return bullet;
	}

	public void setBullet(String bullet) {
		this.bullet = bullet;
	}
}
