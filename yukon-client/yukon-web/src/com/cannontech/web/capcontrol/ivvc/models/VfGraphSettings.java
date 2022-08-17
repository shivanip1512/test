package com.cannontech.web.capcontrol.ivvc.models;

import java.util.Map;

import com.cannontech.common.model.Phase;


public class VfGraphSettings {

	private double yLowerBound; // strategyLow
	private double yUpperBound; // strategyHigh
	private double yMin; // aren't set in the constructor
	private double yMax; // aren't set in the constructor
	private String yAxisLabel;
	private String xAxisLabel;
	private String graphTitle;
	private String graphWidgetName;
    private Map<Phase, String> phaseStringMap;
    private Map<Phase, String> phaseZoneLineColorMap;
    private String zoneLineColorNoPhase;
    private String zonePointColorIgnoredPoints;
    private boolean showZoneTransitionTextBusGraph;
    private boolean showZoneTransitionTextZoneGraph;
    private String zoneTransitionDataLabel;
    
    private String balloonDistanceText;
    

    public VfGraphSettings(double yLowerBound, double yUpperBound,
                           String yAxisLabel, String xAxisLabel,
                           String graphTitle, String graphWidgetName,
                           Map<Phase, String> phaseStringMap,
                           Map<Phase, String> phaseZoneLineColorMap, String zoneLineColorNoPhase,
                           boolean showZoneTransitionTextBusGraph,
                           boolean showZoneTransitionTextZoneGraph, String zoneTransitionDataLabel,
                           String balloonDistanceText, String zonePointColorIgnoredPoints) {
        this.yLowerBound = yLowerBound;
        this.yUpperBound = yUpperBound;
        this.yAxisLabel = yAxisLabel;
        this.xAxisLabel = xAxisLabel;
        this.graphTitle = graphTitle;
        this.graphWidgetName = graphWidgetName;
        this.phaseStringMap = phaseStringMap;
        this.phaseZoneLineColorMap = phaseZoneLineColorMap;
        this.zoneLineColorNoPhase = zoneLineColorNoPhase;
        this.showZoneTransitionTextBusGraph = showZoneTransitionTextBusGraph;
        this.showZoneTransitionTextZoneGraph = showZoneTransitionTextZoneGraph;
        this.zoneTransitionDataLabel = zoneTransitionDataLabel;
        this.balloonDistanceText = balloonDistanceText;
        this.setZonePointColorIgnoredPoints(zonePointColorIgnoredPoints);
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

    public double getyMin() {
        return yMin;
    }

    public void setyMin(double yMin) {
        this.yMin = yMin;
    }

    public double getyMax() {
        return yMax;
    }

    public void setyMax(double yMax) {
        this.yMax = yMax;
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

    public String getZonePointColorIgnoredPoints() {
        return zonePointColorIgnoredPoints;
    }

    public void setZonePointColorIgnoredPoints(String zonePointColorIgnoredPoints) {
        this.zonePointColorIgnoredPoints = zonePointColorIgnoredPoints;
    }
}
