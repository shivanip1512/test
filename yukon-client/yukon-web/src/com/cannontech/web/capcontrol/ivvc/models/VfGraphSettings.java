package com.cannontech.web.capcontrol.ivvc.models;


public class VfGraphSettings {

	private double yLowerBound;
	private double yUpperBound;
	private String yAxisLabel;
	private String xAxisLabel;
	private String graphTitle;
    
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

	public void setGraphTitle(String graphTitle) {
		this.graphTitle = graphTitle;
	}
}
