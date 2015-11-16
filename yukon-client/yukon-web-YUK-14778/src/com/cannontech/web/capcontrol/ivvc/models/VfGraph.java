package com.cannontech.web.capcontrol.ivvc.models;

import java.util.List;

public class VfGraph {
    
	private VfGraphSettings settings;
    private List<VfLine> lines;
    private List<Double> seriesValues;
    
    public List<VfLine> getLines() {
        return lines;
    }
    
    public int getLinesSize() {
    	return lines.size();
    }
    
    public void setLines(List<VfLine> lines) {
        this.lines = lines;
    }

	public VfGraphSettings getSettings() {
		return settings;
	}

	public void setSettings(VfGraphSettings settings) {
		this.settings = settings;
	}

    public List<Double> getSeriesValues() {
        return seriesValues;
    }

    public void setSeriesValues(List<Double> seriesValues) {
        this.seriesValues = seriesValues;
    }
}
