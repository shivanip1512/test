package com.cannontech.web.capcontrol.ivvc.models;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class VfGraph {
    
	private VfGraphSettings settings;
    private List<VfLine> lines;
    private List<Double> seriesValues;
    
    private Ordering<VfPoint> positionOrderer = Ordering.natural().onResultOf(new Function<VfPoint, Double>() {
        @Override
        public Double apply(VfPoint from) {
            return from.getX();
        }
    });
    
    public List<VfLine> getLines() {
        return lines;
    }
    
    public int getLinesSize() {
    	return lines.size();
    }
    
    public void setLines(List<VfLine> lines) {
        this.lines = lines;
        transformToLineGraph();
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

    private void transformToLineGraph() {
        //set seriesValues
        List<Double> unsortedXValues = Lists.newArrayList();
        
        for (VfLine line : this.lines) {
            List<VfPoint> points = line.getPoints();
            for (VfPoint point : points) {
                double xValue = point.getX();
                if (!unsortedXValues.contains(xValue)) {
                    unsortedXValues.add(xValue);
                }
            }
        }
        
        Double min = Collections.min(unsortedXValues);
        List<Double> sortedXValues = Ordering.natural().sortedCopy(unsortedXValues);
        List<Double> normalizedXValues = Lists.newArrayList();
        
        Double valueIterator = Math.floor(min);
        for (Double xValue : sortedXValues) {
            while(valueIterator <= xValue) {
                normalizedXValues.add(valueIterator);
                valueIterator++;
            }
            if (!normalizedXValues.contains(xValue)) {
                normalizedXValues.add(xValue);
            }
        }
        
        this.seriesValues = normalizedXValues;
        
        // re-assign x-values of points to the index of its value in seriesValues
        // (this is just how the amchart line graphs need their data formatted)
        for (VfLine line : this.lines) {
            List<VfPoint> tempBasePoints = getBasePoints();
            for (VfPoint tempBasePoint : tempBasePoints) {
                double baseX = tempBasePoint.getX();
                for (VfPoint point : line.getPoints()) {
                    double pointX = point.getX();
                    if (pointX == baseX) {
                        tempBasePoint.setY(point.getY());
                        tempBasePoint.setDescription(point.getDescription());
                        tempBasePoint.setPhase(point.getPhase());
                        tempBasePoint.setRegulator(point.isRegulator());
                    }
                }
            }
            line.setPoints(tempBasePoints);
        }
        
        // re-order based on new x values
        for (VfLine line : this.lines) {
            List<VfPoint> points = line.getPoints();
            Collections.sort(points, positionOrderer);
        }
        
    }
    
    private List<VfPoint> getBasePoints() {
        List<VfPoint> basePoints = Lists.newArrayListWithCapacity(this.seriesValues.size());
        for (int i = 0; i < seriesValues.size(); i++) {
            Double value = seriesValues.get(i);
            VfPoint point = new VfPoint(value, null, i);
            basePoints.add(point);
        }
        return basePoints;
    }
}
