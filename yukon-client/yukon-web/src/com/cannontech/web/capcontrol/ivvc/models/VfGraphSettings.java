package com.cannontech.web.capcontrol.ivvc.models;


public class VfGraphSettings {

	private double yLowerBound;
	private double yUpperBound;
	private String yAxisLabel;
	private String xAxisLabel;
	private String graphTitle;
	private String graphWidgetName;

    private String phaseAString;
    private String phaseBString;
    private String phaseCString;

    private String zoneLineColorPhaseA;
    private String zoneLineColorPhaseB;
    private String zoneLineColorPhaseC;
    private String zoneLineColorNoPhase;
     
    private String phaseABulletType;
    private String phaseBBulletType;
    private String phaseCBulletType;
    
    private boolean showZoneTransitionText;
    private String zoneTransitionDataLabel;
    
    private String balloonDistanceText;
    
	public VfGraphSettings(double yLowerBound, double yUpperBound, String yAxisLabel, String xAxisLabel,
                           String graphTitle, String graphWidgetName, String phaseAString, String phaseBString, String phaseCString,
                           String zoneLineColorPhaseA, String zoneLineColorPhaseB, String zoneLineColorPhaseC,
                           String zoneLineColorNoPhase, String phaseABulletType, String phaseBBulletType,
                           String phaseCBulletType, boolean showZoneTransitionText, String zoneTransitionDataLabel,
                           String balloonDistanceText) {
        this.yLowerBound = yLowerBound;
        this.yUpperBound = yUpperBound;
        this.yAxisLabel = yAxisLabel;
        this.xAxisLabel = xAxisLabel;
        this.graphTitle = graphTitle;
        this.graphWidgetName = graphWidgetName;
        this.phaseAString = phaseAString;
        this.phaseBString = phaseBString;
        this.phaseCString = phaseCString;
        this.zoneLineColorPhaseA = zoneLineColorPhaseA;
        this.zoneLineColorPhaseB = zoneLineColorPhaseB;
        this.zoneLineColorPhaseC = zoneLineColorPhaseC;
        this.zoneLineColorNoPhase = zoneLineColorNoPhase;
        this.phaseABulletType = phaseABulletType;
        this.phaseBBulletType = phaseBBulletType;
        this.phaseCBulletType = phaseCBulletType;
        this.showZoneTransitionText = showZoneTransitionText;
        this.zoneTransitionDataLabel = zoneTransitionDataLabel;
        this.balloonDistanceText = balloonDistanceText;
    }

    public double getYLowerBound() {
		return yLowerBound;
	}

	public void setYLowerBound(double yLowerBound) {
		this.yLowerBound = yLowerBound;
	}

	public double getYUpperBound() {
		return yUpperBound;
	}

	public void setYUpperBound(double yUpperBound) {
		this.yUpperBound = yUpperBound;
	}

	public String getYAxisLabel() {
		return yAxisLabel;
	}

	public void setYAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}

	public String getXAxisLabel() {
		return xAxisLabel;
	}

	public void setXAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}

	public String getGraphTitle() {
		return graphTitle;
	}

	public String getGraphWidgetName() {
        return graphWidgetName;
    }

    public void setGraphWidgetName(String graphWidgetName) {
        this.graphWidgetName = graphWidgetName;
    }

    public void setGraphTitle(String graphTitle) {
		this.graphTitle = graphTitle;
	}

    public String getPhaseAString() {
        return phaseAString;
    }

    public void setPhaseAString(String phaseAString) {
        this.phaseAString = phaseAString;
    }

    public String getPhaseBString() {
        return phaseBString;
    }

    public void setPhaseBString(String phaseBString) {
        this.phaseBString = phaseBString;
    }

    public String getPhaseCString() {
        return phaseCString;
    }

    public void setPhaseCString(String phaseCString) {
        this.phaseCString = phaseCString;
    }

    public String getZoneLineColorPhaseA() {
        return zoneLineColorPhaseA;
    }

    public void setZoneLineColorPhaseA(String zoneLineColorPhaseA) {
        this.zoneLineColorPhaseA = zoneLineColorPhaseA;
    }

    public String getZoneLineColorPhaseB() {
        return zoneLineColorPhaseB;
    }

    public void setZoneLineColorPhaseB(String zoneLineColorPhaseB) {
        this.zoneLineColorPhaseB = zoneLineColorPhaseB;
    }

    public String getZoneLineColorPhaseC() {
        return zoneLineColorPhaseC;
    }

    public void setZoneLineColorPhaseC(String zoneLineColorPhaseC) {
        this.zoneLineColorPhaseC = zoneLineColorPhaseC;
    }

    public String getZoneLineColorNoPhase() {
        return zoneLineColorNoPhase;
    }

    public void setZoneLineColorNoPhase(String zoneLineColorNoPhase) {
        this.zoneLineColorNoPhase = zoneLineColorNoPhase;
    }

    public String getPhaseABulletType() {
        return phaseABulletType;
    }

    public void setPhaseABulletType(String phaseABulletType) {
        this.phaseABulletType = phaseABulletType;
    }

    public String getPhaseBBulletType() {
        return phaseBBulletType;
    }

    public void setPhaseBBulletType(String phaseBBulletType) {
        this.phaseBBulletType = phaseBBulletType;
    }

    public String getPhaseCBulletType() {
        return phaseCBulletType;
    }

    public void setPhaseCBulletType(String phaseCBulletType) {
        this.phaseCBulletType = phaseCBulletType;
    }

    public boolean isShowZoneTransitionText() {
        return showZoneTransitionText;
    }

    public void setShowZoneTransitionText(boolean showZoneTransitionText) {
        this.showZoneTransitionText = showZoneTransitionText;
    }

    public String getZoneTransitionDataLabel() {
        return zoneTransitionDataLabel;
    }

    public void setZoneTransitionDataLabel(String zoneTransitionDataLabel) {
        this.zoneTransitionDataLabel = zoneTransitionDataLabel;
    }

    public String getBalloonDistanceText() {
        return balloonDistanceText;
    }

    public void setBalloonDistanceText(String balloonDistanceText) {
        this.balloonDistanceText = balloonDistanceText;
    }
}
