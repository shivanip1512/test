package com.cannontech.web.capcontrol.ivvc.models;

import java.util.Map;

import com.cannontech.enums.Phase;


public class VfGraphSettings {

	private double yLowerBound;
	private double yUpperBound;
	private String yAxisLabel;
	private String xAxisLabel;
	private String graphTitle;
	private String graphWidgetName;
    private Map<Phase, String> phaseStringMap;
    private Map<Phase, String> phaseZoneLineColorMap;
    private String zoneLineColorNoPhase;
    private Map<Phase, String> phaseBulletTypeMap;
    private boolean showZoneTransitionTextBusGraph;
    private boolean showZoneTransitionTextZoneGraph;
    private String zoneTransitionDataLabel;
    
    private String balloonDistanceText;
    

    public VfGraphSettings(double yLowerBound, double yUpperBound, String yAxisLabel, String xAxisLabel,
                           String graphTitle, String graphWidgetName, Map<Phase, String> phaseStringMap,
                           Map<Phase, String> phaseZoneLineColorMap, String zoneLineColorNoPhase,
                           Map<Phase, String> phaseBulletTypeMap, boolean showZoneTransitionTextBusGraph,
                           boolean showZoneTransitionTextZoneGraph, String zoneTransitionDataLabel, 
                           String balloonDistanceText) {
        this.yLowerBound = yLowerBound;
        this.yUpperBound = yUpperBound;
        this.yAxisLabel = yAxisLabel;
        this.xAxisLabel = xAxisLabel;
        this.graphTitle = graphTitle;
        this.graphWidgetName = graphWidgetName;
        this.phaseStringMap = phaseStringMap;
        this.phaseZoneLineColorMap = phaseZoneLineColorMap;
        this.zoneLineColorNoPhase = zoneLineColorNoPhase;
        this.phaseBulletTypeMap = phaseBulletTypeMap;
        this.showZoneTransitionTextBusGraph = showZoneTransitionTextBusGraph;
        this.showZoneTransitionTextZoneGraph = showZoneTransitionTextZoneGraph;
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

    public Map<Phase, String> getPhaseStringMap() {
        return phaseStringMap;
    }
    
    public String getPhaseString(Phase phase) {
        return phaseStringMap.get(phase);
    }

    public void setPhaseStringMap(Map<Phase, String> phaseStringMap) {
        this.phaseStringMap = phaseStringMap;
    }

    public Map<Phase, String> getPhaseZoneLineColorMap() {
        return phaseZoneLineColorMap;
    }
    
    public String getPhaseZoneLineColor(Phase phase) {
        return phaseZoneLineColorMap.get(phase);
    }

    public void setPhaseZoneLineColorMap(Map<Phase, String> phaseZoneLineColorMap) {
        this.phaseZoneLineColorMap = phaseZoneLineColorMap;
    }

    public String getZoneLineColorNoPhase() {
        return zoneLineColorNoPhase;
    }

    public void setZoneLineColorNoPhase(String zoneLineColorNoPhase) {
        this.zoneLineColorNoPhase = zoneLineColorNoPhase;
    }

    public Map<Phase, String> getPhaseBulletTypeMap() {
        return phaseBulletTypeMap;
    }
    
    public String getPhaseBulletType(Phase phase) {
        return phaseBulletTypeMap.get(phase);
    }

    public void setPhaseBulletTypeMap(Map<Phase, String> phaseBulletTypeMap) {
        this.phaseBulletTypeMap = phaseBulletTypeMap;
    }

    public boolean isShowZoneTransitionTextBusGraph() {
        return showZoneTransitionTextBusGraph;
    }

    public void setShowZoneTransitionTextBusGraph(boolean showZoneTransitionTextBusGraph) {
        this.showZoneTransitionTextBusGraph = showZoneTransitionTextBusGraph;
    }

    public boolean isShowZoneTransitionTextZoneGraph() {
        return showZoneTransitionTextZoneGraph;
    }

    public void setShowZoneTransitionTextZoneGraph(boolean showZoneTransitionTextZoneGraph) {
        this.showZoneTransitionTextZoneGraph = showZoneTransitionTextZoneGraph;
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
